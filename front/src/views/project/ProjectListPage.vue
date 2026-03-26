<script setup>
import { useProjectStore } from '@/stores/project';
import { storeToRefs } from 'pinia';
import { inject, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const projectStore = useProjectStore();
const { projects, loading } = storeToRefs(projectStore);

const setProject = inject('selectedProject');
const router = useRouter();
const menu = ref();
const selectedRow = ref(null);

/* [필터 로직 주석 처리] 
const filters = ref({
  global: { value: null, matchMode: 'contains' },
  title: { value: null, matchMode: 'contains' },
  pm: { value: null, matchMode: 'equals' }
});
*/

// 페이지 로드 시 자동으로 조회 실행
onMounted(() => {
  fetchProjects();
});

// 우클릭/메뉴 아이템
const actionItems = ref([
  { label: '프로젝트 잠금보관', icon: 'pi pi-lock', command: () => console.log('잠금:', selectedRow.value) },
  { label: '프로젝트 복사', icon: 'pi pi-copy' },
  { label: '프로젝트 삭제', icon: 'pi pi-trash', class: 'text-red-500' },
  { label: '프로젝트 보고서', icon: 'pi pi-file-pdf' }
]);

const toggleMenu = (event, data) => {
  selectedRow.value = data;
  menu.value.toggle(event);
};

const goToDetail = (project) => {
  if (setProject) setProject.value = { title: project.title };
  // router.push(`/project/detail/${project.id}`); // 상세 페이지 이동 시 주석 해제
};

// 조회 버튼 클릭 시 스토어의 액션 호출
const fetchProjects = () => {
  projectStore.fetchProjects(); // 인자 없이 전체 목록만 요청
};

const openAddProject = () => {
  console.log('프로젝트 등록 모달 열기');
};

// 날짜 포맷
const formatDate = (v) => {
  if (!v) return '-';
  const d = new Date(v);
  if (Number.isNaN(d.getTime())) return v;
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}.${m}.${day}`;
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] text-[#1A1816]">
    <div class="flex justify-between items-end mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" outlined class="btn-register-outline" @click="openAddProject" />
    </div>

    <div class="flex-1 overflow-auto bg-white rounded-xl shadow-sm border border-[#C7C7C2]">
      <DataTable :value="projects" :loading="loading" dataKey="id" :rowHover="true" tableLayout="fixed">
        <template #empty>
          <div class="text-center py-6 text-gray-400">데이터 없음</div>
        </template>

        <Column header="No." headerClass="table-header" bodyClass="table-body" style="width: 80px">
          <template #body="slotProps">
            {{ slotProps.index + 1 }}
          </template>
        </Column>

        <Column field="title" header="프로젝트명" headerClass="table-header" class="text-center" style="width: 150px">
          <template #body="{ data }">
            <span class="font-semibold text-[#C97700] cursor-pointer hover:underline" @click="goToDetail(data)">
              {{ data.title }}
            </span>
          </template>
        </Column>

        <Column header="일감 수" headerClass="table-header" bodyClass="table-body" style="width: 100px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ data.issueCount }}</span>
          </template>
        </Column>

        <Column header="진행률" headerClass="table-header" bodyClass="table-body" style="width: 200px">
          <template #body="{ data }">
            <div class="flex items-center gap-2">
              <ProgressBar :value="data.progress" :showValue="false" class="progress-bar-custom flex-1" style="height: 8px" />
              <span class="text-xs text-[#9A9B90] whitespace-nowrap">{{ data.progress }}%</span>
            </div>
          </template>
        </Column>

        <Column field="pm" header="PM/PL" headerClass="table-header" bodyClass="table-body" style="width: 1-0px" />

        <Column header="마감일" headerClass="table-header" bodyClass="table-body" style="width: 120px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ formatDate(data.endDate) }}</span>
          </template>
        </Column>

        <Column headerClass="table-header" style="width: 50px">
          <template #body="{ data }">
            <Button icon="pi pi-ellipsis-h" class="p-button-text p-button-rounded text-[#6B6B63] hover:bg-[#F2F0EB]" @click="toggleMenu($event, data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Menu ref="menu" :model="actionItems" :popup="true" />
  </div>
</template>

<style scoped>
/* ===== 필터 바 전체 높이 ===== */
.filter-bar {
  height: 72px;
}

/* ===== 라벨 ===== */
.filter-label {
  font-size: 1rem;
  font-weight: 600;
  color: #3a3b35;
  white-space: nowrap;
  flex-shrink: 0;
}

/* ===== 입력칸 높이 통일 (38px) ===== */
:deep(.filter-input.p-select),
:deep(.filter-input.p-datepicker) {
  height: 38px !important;
  min-height: 38px !important;
  display: flex !important;
  align-items: center !important;
}
:deep(.filter-input .p-select-label),
:deep(.filter-input .p-datepicker-input),
:deep(.filter-input input.p-inputtext) {
  height: 38px !important;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  line-height: 38px !important;
  box-sizing: border-box !important;
}

/* ===== 버튼 높이 통일 ===== */
.filter-btn {
  height: 36px !important;
  min-height: 36px !important;
  box-sizing: border-box !important;
  padding: 18px;
}

/* ===== 조회 버튼 ===== */
:deep(.btn-amber) {
  background-color: #e8920e !important;
  border-color: #e8920e !important;
  color: #ffffff !important;
}
:deep(.btn-amber:hover) {
  background-color: #c97700 !important;
  border-color: #c97700 !important;
}

/* ===== 등록 버튼 ===== */
:deep(.btn-register-outline) {
  border-color: #e8920e !important;
  color: #e8920e !important;
  background-color: transparent !important;
}
:deep(.btn-register-outline:hover) {
  background-color: #fff8e8 !important;
  border-color: #c97700 !important;
  color: #c97700 !important;
}

/* ===== 초기화 버튼 ===== */
:deep(.btn-reset) {
  color: #6b6b63 !important;
  background-color: transparent !important;
  border: 1px solid #c7c7c2 !important;
}
:deep(.btn-reset:hover) {
  background-color: #e5e2d9 !important;
  color: #1a1816 !important;
}

/* ===== 테이블 헤더 공통 스타일 (위치 정렬 포함) ===== */
:deep(.table-header) {
  background-color: #f2f0eb !important;
  color: #1a1816 !important;
  border-bottom: 2px solid #c7c7c2 !important;
  padding: 1.5rem 0 !important; /* 위아래 패딩만 */
  font-weight: 700 !important;
  font-size: 1rem !important;
}

:deep(.table-header .p-datatable-column-header-content) {
  justify-content: center;
}

/* ===== 테이블 바디 공통 스타일 ===== */
:deep(.p-datatable-tbody > tr > td) {
  padding: 1rem 0 !important;
  text-align: center !important; /* 바디 글자 가운데로 */
  border-bottom: 1px solid #f2f0eb !important;
  vertical-align: middle;
}

/* ===== 진행률 바 ===== */
:deep(.progress-bar-custom) {
  border-radius: 10px;
  background-color: #e5e2d9;
  border: none;
}
:deep(.progress-bar-custom .p-progressbar-value) {
  background-color: #e8920e;
  border-radius: 10px;
}
</style>
