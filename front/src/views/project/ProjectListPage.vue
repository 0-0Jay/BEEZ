<script setup>
import { inject, ref } from 'vue';
import { useRouter } from 'vue-router';

// --- 전역 상태 및 유틸리티 ---
const setProject = inject('selectedProject');
const router = useRouter();
const menu = ref();
const selectedRow = ref(null);

// --- 필터 상태 ---
const filters = ref({
  title: null,
  pm: null,
  startDate: null,
  endDate: null,
  showArchived: false
});

// --- 목업 데이터 (설계도 기준) ---
const projects = ref([
  { id: 1, title: '모바일 앱 v3.0 리뉴얼', issueCount: '10/30', progress: 33, pm: '곽현우', endDate: '2026-12-30' },
  { id: 2, title: '백엔드 API 마이그레이션', issueCount: '1/10', progress: 10, pm: '노정화', endDate: '2027-01-24' },
  { id: 3, title: '관리자 대시보드 설계', issueCount: '13/25', progress: 52, pm: '조영진', endDate: '2026-08-11' },
  { id: 4, title: 'QA 자동화 테스트 구축', issueCount: '10/30', progress: 33, pm: '한유민', endDate: '2026-07-15' }
]);

const projectOptions = ref(['모바일 앱 리뉴얼', 'API 마이그레이션', '대시보드 설계']);
const pmOptions = ref(['곽현우', '노정화', '조영진', '한유민']);

// --- 더보기 메뉴 항목 ---
const actionItems = ref([
  { label: '프로젝트 잠금보관', icon: 'pi pi-lock', command: () => console.log('잠금:', selectedRow.value) },
  { label: '프로젝트 복사', icon: 'pi pi-copy' },
  { label: '프로젝트 삭제', icon: 'pi pi-trash', class: 'text-red-500' },
  { label: '프로젝트 보고서', icon: 'pi pi-file-pdf' }
]);

// --- 함수 ---
const toggleMenu = (event, data) => {
  selectedRow.value = data;
  menu.value.toggle(event);
};

const goToDetail = (project) => {
  setProject.value = { title: project.title };
  // router.push(`/project/detail/${project.id}`);
};

const formatDate = (date) => {
  if (!date) return '-';
  return date;
};

const resetFilters = () => {
  filters.value = { title: null, pm: null, startDate: null, endDate: null, showArchived: false };
};

const fetchProjects = () => {
  console.log('조회 실행:', filters.value);
};

const openAddProject = () => {
  console.log('등록 모달 열기');
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen text-[#1A1816]">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" outlined class="btn-amber-outline" @click="openAddProject" />
    </div>

    <div class="bg-[#F2F0EB] p-6 rounded-lg mb-8 flex flex-wrap gap-4 items-end shadow-sm border border-[#C7C7C2]">
      <div class="flex flex-col gap-2">
        <label class="text-sm font-bold text-[#3A3B35]">프로젝트명</label>
        <Select v-model="filters.title" :options="projectOptions" placeholder="선택" class="w-52" />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-sm font-bold text-[#3A3B35]">PM</label>
        <Select v-model="filters.pm" :options="pmOptions" placeholder="선택" class="w-40" />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-sm font-bold text-[#3A3B35]">마감일</label>
        <div class="flex items-center gap-2">
          <DatePicker v-model="filters.startDate" placeholder="연도-월-일" class="w-40" />
          <span class="text-[#3A3B35]">~</span>
          <DatePicker v-model="filters.endDate" placeholder="연도-월-일" class="w-40" />
        </div>
      </div>
      <div class="flex items-center gap-2 ml-auto">
        <Checkbox v-model="filters.showArchived" :binary="true" inputId="archived" />
        <label for="archived" class="text-sm font-medium text-[#1A1816]">잠금 보관 프로젝트 보기</label>

        <Button label="초기화" text class="btn-amber-outline ml-4" @click="resetFilters" />

        <Button label="조회" icon="pi pi-search" class="btn-amber px-6" @click="fetchProjects" />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="projects" :paginator="true" :rows="10" dataKey="id" responsiveLayout="scroll" stripedRows class="p-datatable-sm">
        <Column field="id" header="No." headerClass="table-header" style="width: 70px" />

        <Column field="title" header="프로젝트명" headerClass="table-header" style="width: 250px">
          <template #body="{ data }">
            <span class="font-semibold text-[#E8920E] cursor-pointer hover:underline" @click="goToDetail(data)">
              {{ data.title }}
            </span>
          </template>
        </Column>

        <Column header="일감 수" headerClass="table-header" style="width: 120px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ data.issueCount }}</span>
          </template>
        </Column>

        <Column header="진행률" headerClass="table-header" style="width: 200px">
          <template #body="{ data }">
            <div class="flex flex-col gap-1">
              <ProgressBar :value="data.progress" :showValue="false" class="progress-bar-custom" style="height: 8px" />
              <span class="text-xs text-right text-[#3A3B35]">{{ data.progress }}%</span>
            </div>
          </template>
        </Column>

        <Column field="pm" header="PM" headerClass="table-header" style="width: 120px" />

        <Column header="마감일" headerClass="table-header" style="width: 150px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ formatDate(data.endDate) }}</span>
          </template>
        </Column>

        <Column headerClass="table-header" style="width: 60px">
          <template #body="{ data }">
            <Button icon="pi pi-ellipsis-h" class="p-button-text text-[#3A3B35] p-button-rounded hover:bg-[#FAFAF8]" @click="toggleMenu($event, data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Menu ref="menu" :model="actionItems" :popup="true" />
  </div>
</template>

<style scoped>
/* --- 버튼 색상 커스텀 (방법 2: :deep 활용) --- */

/* 1. 조회 버튼 (배경색) */
:deep(.btn-amber) {
  background-color: #e8920e !important;
  border-color: #e8920e !important;
  color: #ffffff !important;
}
:deep(.btn-amber:hover) {
  background-color: #f5a623 !important;
  border-color: #f5a623 !important;
}

/* 2. 등록 버튼 (테두리) */
:deep(.btn-amber-outline) {
  border-color: #e8920e !important;
  color: #e8920e !important;
}
:deep(.btn-amber-outline:hover) {
  background-color: #fffbf0 !important;
  border-color: #f5a623 !important;
  color: #f5a623 !important;
}

/* 3. 초기화 버튼 (텍스트) */
:deep(.btn-stone-text) {
  color: #3a3b35 !important;
}
:deep(.btn-stone-text:hover) {
  background-color: #fafaf8 !important;
}

/* --- 테이블 스타일 --- */
:deep(.table-header) {
  background-color: #f2f0eb !important;
  text-align: center !important;
  font-weight: 700 !important;
  color: #1a1816 !important;
  border-bottom: 2px solid #c7c7c2 !important;
}

:deep(.p-datatable-tbody > tr > td) {
  padding: 1rem;
  text-align: center;
  border-bottom: 1px solid #fafaf8 !important;
}

:deep(.p-datatable-striped .p-datatable-tbody > tr:nth-child(even)) {
  background-color: #fafaf8 !important;
}

:deep(.p-datatable-tbody > tr:hover) {
  background-color: #fffbf0 !important;
}

/* --- 진행률 바 커스텀 --- */
:deep(.progress-bar-custom) {
  border-radius: 10px;
  background-color: #fafaf8;
  border: 1px solid #c7c7c2;
}

:deep(.progress-bar-custom .p-progressbar-value) {
  background-color: #e8920e;
}
</style>
