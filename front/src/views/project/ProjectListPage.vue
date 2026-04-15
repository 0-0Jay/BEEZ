<script setup>
import ProjectDeleteModal from '@/components/project/ProjectDeleteModal.vue';
import { useProjectStore } from '@/stores/project';
import { storeToRefs } from 'pinia';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const toast = useToast();
const deleteModalVisible = ref(false);
const projectToDelete = ref({ id: '', identifier: '' });

const openDeleteModal = (data) => {
  // 1. 현재 화면에 있는 목록 중 나를 부모로 가진 프로젝트가 있는지 확인
  const hasChild = projectStore.projects.some((p) => p.parentId && String(p.parentId).trim() === String(data.id).trim());

  if (hasChild) {
    // 하위 항목이 있으면 토스트 띄우고 중단
    toast.add({
      severity: 'warn',
      summary: '삭제 불가',
      detail: '하위 프로젝트가 존재하는 프로젝트는 삭제할 수 없습니다.',
      life: 3000
    });
    return;
  }

  // 2. 하위 항목이 없으면 삭제 확인 모달 오픈
  projectToDelete.value = { id: data.id, identifier: data.identifier };
  deleteModalVisible.value = true;
};

const handleActualDelete = async (id) => {
  deleteModalVisible.value = false;

  try {
    await projectStore.deleteProject(id);
    fetchProjects(); // 목록 새로고침
    toast.add({
      severity: 'success',
      summary: '삭제 완료',
      detail: '프로젝트가 영구적으로 삭제되었습니다.',
      life: 2000
    });
  } catch (error) {
    toast.add({ severity: 'error', summary: '삭제 실패', detail: '오류가 발생했습니다.' });
  }
};

// --- 1. 스토어 연결 ---
const projectStore = useProjectStore();
const { projects, loading } = storeToRefs(projectStore);

const router = useRouter();
const menu = ref();
const selectedRow = ref(null);

// --- 2. 필터 상태 ---
const filters = reactive({
  title: null,
  pmId: null,
  startDate: null,
  endDate: null,
  isLock: false
});

// 목록 조회 후 Select 옵션 구성
const projectOptions = ref([]);
const pmOptions = ref([]);

// --- 3. 조회 로직 ---

// 1. 옵션 구성 로직을 별도 함수로 분리
const updateSelectOptions = () => {
  // 프로젝트명 옵션
  projectOptions.value = projectStore.projects.map((p) => ({
    label: p.title,
    value: p.id
  }));

  // PM/PL 옵션 (중복 제거)
  pmOptions.value = [...new Map(projectStore.projects.map((p) => [p.pm, { label: p.pm, value: p.pmId }])).values()];
};

// 2. 조회 함수
const fetchProjects = async () => {
  await projectStore.fetchProjects({
    ...filters,
    startDate: formatDate(filters.startDate),
    endDate: formatDate(filters.endDate)
  });

  // 조회가 끝난 후 최신 데이터로 옵션 갱신
  updateSelectOptions();
};

// 체크박스 전용 감시자
watch(
  () => filters.isLock,
  () => {
    filters.title = null;
    filters.pmId = null;
    filters.startDate = null;
    filters.endDate = null;
    fetchProjects(); // 즉시 재조회
  }
);

// 3. 초기 로딩 수정
onMounted(() => {
  fetchProjects(); // 이미 정의된 조회 로직 호출
});

const resetFilters = () => {
  Object.assign(filters, {
    title: null,
    pmId: null,
    startDate: null,
    endDate: null,
    isLock: false
  });
  fetchProjects(); // 초기화 후 재조회 (여기서도 옵션이 갱신됨)
};

// --- 나머지 유틸리티 ---
const toggleMenu = (event, data) => {
  selectedRow.value = data;
  menu.value.toggle(event);
};

const goToDetail = (project) => {
  console.log(project);
  if (project.isLock === 'L1') return;
  projectStore.selectedProject = { title: project.title, id: project.id, startDate: project.startDate, endDate: project.endDate };
  console.log(projectStore.selectedProject);
  router.push(`/project`);
};

const goToSetting = (project) => {
  projectStore.selectedProject = {
    title: project.title,
    id: project.id,
    startDate: project.startDate,
    endDate: project.endDate
  };

  router.push(`/project/setting/${project.id}/info`);
};

// 날짜 포맷 변환 함수
const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const actionItems = computed(() => [
  {
    label: selectedRow.value?.isLock === 'L1' ? '잠금보관 해제' : '프로젝트 잠금보관',
    icon: selectedRow.value?.isLock === 'L1' ? 'pi pi-lock-open' : 'pi pi-lock',
    command: () => (selectedRow.value?.isLock === 'L1' ? unlockProject(selectedRow.value.id) : lockProject(selectedRow.value.id))
  },
  { label: '프로젝트 복사', icon: 'pi pi-copy', command: () => router.push(`/project/copy/${selectedRow.value.id}`) },
  {
    label: '프로젝트 설정',
    icon: 'pi pi-cog',
    command: () => goToSetting(selectedRow.value)
  },
  { label: '프로젝트 삭제', icon: 'pi pi-trash', command: () => openDeleteModal(selectedRow.value) }
]);

const rowClass = (data) => {
  return data.isLock === 'L1' ? 'locked-row' : '';
};

const lockProject = async (id) => {
  try {
    await projectStore.lockProject(id);
    fetchProjects();
    toast.add({ severity: 'success', summary: '성공', detail: '프로젝트가 잠금보관되었습니다.', life: 2000 });
  } catch (error) {
    // 1. 서버에서 보낸 메시지 추출 (구조 확인)
    const errorMsg = error.response?.data?.message || error.message;

    console.log('추출된 메시지:', errorMsg); // "CHILD_PROJECT_NOT_LOCKED"가 찍히는지 확인

    if (errorMsg === 'CHILD_PROJECT_NOT_LOCKED') {
      toast.add({
        severity: 'warn',
        summary: '잠금 불가',
        detail: '잠금되지 않은 하위 프로젝트가 존재합니다.',
        life: 3000
      });
    } else {
      toast.add({
        severity: 'error',
        summary: '오류 발생',
        detail: '잠금 처리 중 오류가 발생했습니다.',
        life: 3000
      });
    }
  }
};

const unlockProject = async (id) => {
  await projectStore.unlockProject(id);
  fetchProjects();
  toast.add({ severity: 'success', summary: '프로젝트 잠금보관 해제', detail: '프로젝트가 잠금보관 해제되었습니다.', life: 2000 });
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen">
    <!-- 타이틀 + 등록 버튼 -->
    <div class="flex justify-between items-end mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" severity="contrast" outlined @click="router.push('/project/create')" />
    </div>

    <div class="bg-[#E8E5DC] px-10 py-8 rounded-lg mb-8 shadow-sm border border-[#C7C7C2] flex flex-col gap-3">
      <!-- 입력칸 + 체크박스 묶음 -->
      <div class="flex items-center flex-wrap gap-y-3">
        <label class="filter-label mr-3 self-start mt-3">프로젝트명</label>
        <InputText v-model="filters.title" placeholder="검색할 키워드를 입력하세요." class="filter-input w-100 mr-10" @keyup.enter="fetchProjects" />

        <label class="filter-label mr-3 self-start mt-3">PM/PL</label>
        <Select v-model="filters.pmId" :options="pmOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-42 mr-10 self-start" />
        <label class="filter-label mr-3 self-start mt-3">마감일</label>
        <div class="flex flex-col self-start">
          <div class="flex items-center">
            <DatePicker v-model="filters.startDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" :maxDate="filters.endDate" class="filter-input w-50" showIcon inputClass="w-full" />
            <span class="text-sm text-[#6B6B63] px-4">~</span>
            <DatePicker v-model="filters.endDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" :minDate="filters.startDate" class="filter-input w-50 mr-15" showIcon inputClass="w-full" />
          </div>
        </div>
        <Checkbox v-model="filters.isLock" :binary="true" inputId="archived" class="mr-3 self-start mt-3" />
        <label for="archived" class="text-sm font-medium text-[#1A1816] whitespace-nowrap cursor-pointer self-start mt-3">잠금 보관 프로젝트 보기</label>
        <!-- 버튼 묶음 — 항상 오른쪽 끝 -->
        <div class="flex gap-2 ml-auto shrink-0 self-start">
          <Button label="초기화" severity="secondary" raised @click="resetFilters" />
          <Button label="조회" icon="pi pi-search" raised @click="fetchProjects" />
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="projects" :loading="loading" dataKey="id" :rowHover="true" :rowClass="rowClass" tableStyle="width: 100%" scrollable scrollHeight="500px">
        <template #empty>
          <div class="text-center py-10 text-gray-400">조회된 데이터가 없습니다.</div>
        </template>

        <Column header="No." headerClass="table-header" style="width: 5%">
          <template #body="slotProps">
            {{ slotProps.index + 1 }}
          </template>
        </Column>

        <Column field="title" header="프로젝트명" headerClass="table-header" style="width: 25%">
          <template #body="{ data }">
            <div :style="{ paddingLeft: `${data.level * 2}rem` }" class="flex items-center">
              <span v-if="data.level > 0" class="text-stone-400 mr-2 font-mono">└</span>

              <i v-if="data.level === 0" class="mr-2 text-amber-500"></i>
              <i v-else class="mr-2 text-stone-400"></i>

              <span class="font-medium text-stone-800 hover:text-amber-600 transition-colors cursor-pointer" @click="goToDetail(data)">
                {{ data.title }}
              </span>
            </div>
          </template>
        </Column>

        <Column header="일감 수" headerClass="table-header" style="width: 10%">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">
              {{ data.totalTaskCount === 0 ? '0 / 0' : `${data.completedTaskCount} / ${data.totalTaskCount}` }}
            </span>
          </template>
        </Column>

        <Column header="진행률" headerClass="table-header" style="width: 30%">
          <template #body="{ data }">
            <div class="flex items-center gap-5 px-10">
              <ProgressBar :value="data.progressRate ?? 0" :showValue="false" class="progress-bar-custom flex-1" style="height: 8px" />
              <span class="text-xs text-[#9A9B90] whitespace-nowrap"> {{ data.progressRate ?? 0 }}% </span>
            </div>
          </template>
        </Column>

        <Column field="pm" header="PM/PL" headerClass="table-header" style="width: 15%" />

        <Column header="마감일" headerClass="table-header" style="width: 10%">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ formatDate(data.endDate) }}</span>
          </template>
        </Column>

        <Column headerClass="table-header" style="width: 5%">
          <template #body="{ data }">
            <Button icon="pi pi-ellipsis-h" class="p-button-text p-button-rounded text-[#6B6B63] hover:bg-[#F2F0EB]" @click="toggleMenu($event, data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Menu ref="menu" :model="actionItems" :popup="true" />
  </div>
  <ProjectDeleteModal v-model:visible="deleteModalVisible" :projectId="projectToDelete.id" :identifier="projectToDelete.identifier" @confirm="handleActualDelete" />
</template>

<style scoped>
.filter-label {
  font-size: 1rem;
  font-weight: 600;
  color: #3a3b35;
  white-space: nowrap;
}
:deep(.filter-input) {
  height: 38px !important;
}
:deep(.filter-input .p-select-label) {
  display: flex;
  align-items: center;
}
:deep(.table-header) {
  background-color: #e8e5dc !important;
  color: #1a1816 !important;
  font-weight: 700 !important;
  text-align: center !important;
  padding: 1.25rem 0 !important;
}
:deep(.table-header .p-datatable-column-header-content) {
  justify-content: center;
}
:deep(.p-datatable-tbody > tr > td) {
  text-align: center !important;
  padding: 1rem 0 !important;
  border-bottom: 1px solid #f2f0eb !important;
}
:deep(.progress-bar-custom) {
  border-radius: 10px;
  background-color: #e5e2d9;
  border: none;
}
:deep(.progress-bar-custom .p-progressbar-value) {
  background-color: #e8920e;
  border-radius: 10px;
}
:deep(.locked-row) {
  background-color: #f5f5f5 !important;
  color: #b0b0b0 !important;
}
:deep(.locked-row td) {
  color: #b0b0b0 !important;
  background-color: #f5f5f5 !important;
}
:deep(.locked-row td span) {
  color: #b0b0b0 !important;
}
</style>
