<script setup>
import { useProjectStore } from '@/stores/project';
import { storeToRefs } from 'pinia';
import { computed, inject, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

// 삭제할 id를 임시 저장
const pendingDeleteId = ref(null);
const visible = ref(false);
const confirmMsg = ref('');

const openDeleteConfirm = (id) => {
  pendingDeleteId.value = id;
  confirmMsg.value = '정말 삭제하시겠습니까?';
  visible.value = true;
};

const handleDelConfirm = async () => {
  visible.value = false;
  if (pendingDeleteId.value !== null) {
    await projectStore.deleteProject(pendingDeleteId.value);
    pendingDeleteId.value = null;
    fetchProjects();
  }
};

defineEmits(['selectProject']);

// --- 1. 스토어 연결 ---
const projectStore = useProjectStore();
const { projects, loading } = storeToRefs(projectStore);

const setProject = inject('selectedProject');
const router = useRouter();
const menu = ref();
const selectedRow = ref(null);

// --- 2. 필터 상태 ---
const filters = reactive({
  id: null,
  pmId: null,
  startDate: null,
  endDate: null,
  isLock: false
});

// 목록 조회 후 Select 옵션 구성
const projectOptions = ref([]);
const pmOptions = ref([]);

// --- 3. 조회 로직 ---
onMounted(async () => {
  await projectStore.fetchProjects(filters);

  // select 옵션 구성
  projectOptions.value = projectStore.projects.map((p) => ({
    label: p.title,
    value: p.id
  }));
  pmOptions.value = [...new Map(projectStore.projects.map((p) => [p.pm, { label: p.pm, value: p.pmId }])).values()]; // 중복 제거
});

const fetchProjects = () => {
  projectStore.fetchProjects({
    ...filters,
    startDate: formatDate(filters.startDate),
    endDate: formatDate(filters.endDate)
  });
};

const resetFilters = () => {
  Object.assign(filters, {
    id: null,
    pmId: null,
    startDate: null,
    endDate: null,
    isLock: false
  });
  fetchProjects(); // 초기화 후 재조회
};

// --- 나머지 유틸리티 ---
const toggleMenu = (event, data) => {
  selectedRow.value = data;
  menu.value.toggle(event);
};

const goToDetail = (project) => {
  if (project.isLock === 'L1') return;
  if (setProject) setProject.value = { title: project.title, id: project.id };
  router.push(`/project/setting/${project.id}`);
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
  { label: '프로젝트 복사', icon: 'pi pi-copy' },
  { label: '프로젝트 삭제', icon: 'pi pi-trash', command: () => openDeleteConfirm(selectedRow.value.id) }
]);

const rowClass = (data) => {
  return data.isLock === 'L1' ? 'locked-row' : '';
};

const lockProject = async (id) => {
  await projectStore.lockProject(id);
  fetchProjects();
};

const unlockProject = async (id) => {
  await projectStore.unlockProject(id);
  fetchProjects();
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen">
    <!-- 타이틀 + 등록 버튼 -->
    <div class="flex justify-between items-end mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" outlined class="btn-register-outline" @click="router.push('/project/create')" />
    </div>

    <div class="bg-[#F2F0EB] px-10 py-8 rounded-lg mb-8 shadow-sm border border-[#C7C7C2] flex">
      <!-- 입력칸 + 체크박스 묶음 -->
      <div class="flex items-center">
        <label class="filter-label mr-5">프로젝트명</label>
        <Select v-model="filters.id" :options="projectOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-80 mr-10" />

        <label class="filter-label mr-5">PM/PL</label>
        <Select v-model="filters.pmId" :options="pmOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-42 mr-10" />
        <label class="filter-label mr-5">마감일</label>
        <DatePicker v-model="filters.startDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-15" />
        <span class="text-sm text-[#6B6B63] px-4">~</span>
        <DatePicker v-model="filters.endDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-25" />
        <Checkbox v-model="filters.isLock" :binary="true" inputId="archived" class="mr-3" />
        <label for="archived" class="text-sm font-medium text-[#1A1816] whitespace-nowrap cursor-pointer">잠금 보관 프로젝트 보기</label>
      </div>

      <!-- 버튼 묶음 — ml-auto로 오른쪽, items-end로 아래 -->
      <div class="flex gap-2 ml-auto">
        <Button label="초기화" text class="btn-reset filter-btn mr-1" @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" class="btn-amber" @click="fetchProjects" />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="projects" :loading="loading" dataKey="id" :rowHover="true" :rowClass="rowClass">
        <template #empty>
          <div class="text-center py-10 text-gray-400">조회된 데이터가 없습니다.</div>
        </template>

        <Column header="No." headerClass="table-header" style="width: 80px">
          <template #body="slotProps">
            {{ slotProps.index + 1 }}
          </template>
        </Column>

        <Column field="title" header="프로젝트명" headerClass="table-header" style="width: 250px">
          <template #body="{ data }">
            <span class="font-semibold text-[#C97700] cursor-pointer hover:underline" @click="goToDetail(data)">
              {{ data.title }}
            </span>
          </template>
        </Column>

        <Column header="일감 수" headerClass="table-header" style="width: 120px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ data.issueCount }}</span>
          </template>
        </Column>

        <Column header="진행률" headerClass="table-header" style="width: 250px">
          <template #body="{ data }">
            <div class="flex items-center gap-2">
              <ProgressBar :value="data.progress" :showValue="false" class="progress-bar-custom flex-1" style="height: 8px" />
              <span class="text-xs text-[#9A9B90] whitespace-nowrap">{{ data.progress }}%</span>
            </div>
          </template>
        </Column>

        <Column field="pm" header="PM/PL" headerClass="table-header" style="width: 120px" />

        <Column header="마감일" headerClass="table-header" style="width: 150px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ formatDate(data.endDate) }}</span>
          </template>
        </Column>

        <Column headerClass="table-header" style="width: 60px">
          <template #body="{ data }">
            <Button icon="pi pi-ellipsis-h" class="p-button-text p-button-rounded text-[#6B6B63] hover:bg-[#F2F0EB]" @click="toggleMenu($event, data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Menu ref="menu" :model="actionItems" :popup="true" />
  </div>

  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="handleDelConfirm">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>
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
:deep(.btn-amber) {
  background-color: #e8920e !important;
  color: #ffffff !important;
  border: 1px solid transparent !important;
  box-shadow: none !important;
  height: 36px !important;
  min-height: 36px !important;
  box-sizing: border-box !important;
  padding: 18px;
}
:deep(.btn-amber:hover) {
  background-color: #c97700 !important;
  border-color: #c97700 !important;
}
:deep(.btn-register-outline) {
  border-color: #e8920e !important;
  color: #e8920e !important;
}
:deep(.btn-register-outline:hover) {
  background-color: #fff8e8 !important;
  border-color: #c97700 !important;
  color: #c97700 !important;
}
:deep(.btn-reset) {
  color: #6b6b63 !important;
  border: 1px solid #c7c7c2 !important;
  height: 36px !important;
  min-height: 36px !important;
  box-sizing: border-box !important;
  padding: 18px;
}
:deep(.btn-reset:hover) {
  background-color: #e5e2d9 !important;
  color: #1a1816 !important;
}
:deep(.table-header) {
  background-color: #f2f0eb !important;
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
