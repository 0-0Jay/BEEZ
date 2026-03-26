<script setup>
import { inject, ref } from 'vue';
import { useRouter } from 'vue-router';

const setProject = inject('selectedProject');
const router = useRouter();
const menu = ref();
const selectedRow = ref(null);

const filters = ref({
  title: null,
  pm: null,
  startDate: null,
  endDate: null,
  showArchived: false
});

const projects = ref([
  { id: 1, title: '모바일 앱 v3.0 리뉴얼 이거 아닌 것 같아', issueCount: '10/30', progress: 33, pm: '곽현우', endDate: '2026-12-30' },
  { id: 2, title: '백엔드 API 마이그레이션', issueCount: '1/10', progress: 10, pm: '노정화', endDate: '2027-01-24' },
  { id: 3, title: '관리자 대시보드 설계', issueCount: '13/25', progress: 52, pm: '조영진', endDate: '2026-08-11' },
  { id: 4, title: 'QA 자동화 테스트 구축', issueCount: '10/30', progress: 33, pm: '한유민', endDate: '2026-07-15' }
]);

const projectOptions = ref(['모바일 앱 리뉴얼', 'API 마이그레이션', '대시보드 설계']);
const pmOptions = ref(['곽현우', '노정화', '조영진', '한유민']);

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
  setProject.value = { title: project.title };
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
    <!-- 타이틀 + 등록 버튼 -->
    <div class="flex justify-between items-end mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" outlined class="btn-register-outline" @click="openAddProject" />
    </div>

    <div class="bg-[#F2F0EB] px-10 py-8 rounded-lg mb-8 shadow-sm border border-[#C7C7C2] flex">
      <!-- 입력칸 + 체크박스 묶음 -->
      <div class="flex items-center">
        <label class="filter-label mr-5">프로젝트명</label>
        <Select v-model="filters.title" :options="projectOptions" placeholder="선택" class="filter-input w-80 mr-10" />

        <label class="filter-label mr-5">PM/PL</label>
        <Select v-model="filters.pm" :options="pmOptions" placeholder="선택" class="filter-input w-42 mr-10" />

        <label class="filter-label mr-5">마감일</label>
        <DatePicker v-model="filters.startDate" placeholder="연도-월-일" class="filter-input w-36 mr-15" />
        <span class="text-sm text-[#6B6B63] px-4">~</span>
        <DatePicker v-model="filters.endDate" placeholder="연도-월-일" class="filter-input w-36 mr-25" />

        <Checkbox v-model="filters.showArchived" :binary="true" inputId="archived" class="mr-3" />
        <label for="archived" class="text-sm font-medium text-[#1A1816] whitespace-nowrap cursor-pointer">잠금 보관 프로젝트 보기</label>
      </div>

      <!-- 버튼 묶음 — ml-auto로 오른쪽, items-end로 아래 -->
      <div class="flex gap-2 ml-auto">
        <Button label="초기화" text class="btn-reset filter-btn mr-1" @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" class="btn-amber filter-btn" @click="fetchProjects" />
      </div>
    </div>

    <!-- 테이블 -->
    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="projects" :paginator="true" :rows="10" dataKey="id" responsiveLayout="scroll" stripedRows class="p-datatable-sm">
        <Column field="id" header="No." headerClass="table-header" headerStyle="padding-left: 3rem;" style="width: 70px" />
        <Column field="title" header="프로젝트명" headerClass="table-header" headerStyle="padding-left: 10rem;" style="width: 180px">
          <template #body="{ data }">
            <span class="font-semibold text-[#C97700] cursor-pointer hover:underline" @click="goToDetail(data)">
              {{ data.title }}
            </span>
          </template>
        </Column>
        <Column header="일감 수" headerClass="table-header" headerStyle="padding-left: 4.8rem;" style="width: 100px">
          <template #body="{ data }">
            <span class="text-[#3A3B35]">{{ data.issueCount }}</span>
          </template>
        </Column>
        <Column header="진행률" headerClass="table-header" headerStyle="padding-left: 12rem;" style="width: 200px">
          <template #body="{ data }">
            <div class="flex flex-col gap-1">
              <ProgressBar :value="data.progress" :showValue="false" class="progress-bar-custom" style="height: 8px" />
              <span class="text-xs text-right text-[#9A9B90]">{{ data.progress }}%</span>
            </div>
          </template>
        </Column>
        <Column field="pm" header="PM/PL" headerClass="table-header" headerStyle="padding-left: 6rem;" style="width: 100px" />
        <Column header="마감일" headerClass="table-header" headerStyle="padding-left: 8rem;" style="width: 150px">
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

/* ===== 테이블 헤더 ===== */
:deep(.table-header) {
  background-color: #f2f0eb !important;
  color: #1a1816 !important;
  border-bottom: 2px solid #c7c7c2 !important;
  padding-top: 1.5rem !important;
  padding-bottom: 1.5rem !important;
  font-weight: 700 !important;
  font-size: 1rem !important;
}

/* ===== 테이블 바디 ===== */
:deep(.p-datatable-tbody > tr > td) {
  text-align: center;
  border-bottom: 1px solid #f2f0eb !important;
}
:deep(.p-datatable-striped .p-datatable-tbody > tr:nth-child(even)) {
  background-color: #fafaf8 !important;
}
:deep(.p-datatable-tbody > tr:hover) {
  background-color: #fff8e8 !important;
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
