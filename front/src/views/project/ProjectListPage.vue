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

// --- 목업 데이터 (백엔드 연동 전까지 사용) ---
const projects = ref([
  { id: 1, title: '모바일 앱 v3.0 리뉴얼', issueCount: '10/30', progress: 33, pm: '곽현우', endDate: '2026-12-30' },
  { id: 2, title: '백엔드 API 마이그레이션', issueCount: '1/10', progress: 10, pm: '노정화', endDate: '2027-01-24' },
  { id: 3, title: '관리자 대시보드 설계', issueCount: '13/25', progress: 52, pm: '조영진', endDate: '2026-08-11' },
  { id: 4, title: 'QA 자동화 테스트 구축', issueCount: '10/30', progress: 33, pm: '한유민', endDate: '2026-07-15' }
]);

const projectOptions = ref(['모바일 앱 리뉴얼', 'API 마이그레이션', '대시보드 설계']);
const pmOptions = ref(['곽현우', '노정화', '조영진', '한유민']);

// --- 더보기 메뉴 항목 (설계도 2번) ---
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
  setProject.value = { name: project.title }; // 헤더 프로젝트명 변경
  // router.push(`/project/detail/${project.id}`); // 나중에 상세페이지 주소로 변경
};

const formatDate = (date) => {
  if (!date) return '-';
  return date; // 현재는 목업 데이터가 문자열이라 그대로 반환
};

const resetFilters = () => {
  filters.value = { title: null, pm: null, startDate: null, endDate: null, showArchived: false };
};

const fetchProjects = () => {
  console.log('필터 조건으로 백엔드 호출:', filters.value);
  // axios.get(...) 코드가 들어갈 자리
};
</script>

<template>
  <div class="p-8 bg-gray-50 min-h-screen">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" severity="secondary" outlined @click="openAddProject" />
    </div>

    <div class="bg-gray-200 p-6 rounded-lg mb-8 flex flex-wrap gap-4 items-end shadow-sm">
      <div class="flex flex-col gap-2">
        <label class="text-sm font-bold text-gray-600">프로젝트명</label>
        <Select v-model="filters.title" :options="projectOptions" placeholder="선택" class="w-52" />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-sm font-bold text-gray-600">PM</label>
        <Select v-model="filters.pm" :options="pmOptions" placeholder="선택" class="w-40" />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-sm font-bold text-gray-600">마감일</label>
        <div class="flex items-center gap-2">
          <DatePicker v-model="filters.startDate" placeholder="연도-월-일" class="w-40" />
          <span class="text-gray-500">~</span>
          <DatePicker v-model="filters.endDate" placeholder="연도-월-일" class="w-40" />
        </div>
      </div>
      <div class="flex items-center gap-2 ml-auto">
        <Checkbox v-model="filters.showArchived" :binary="true" inputId="archived" />
        <label for="archived" class="text-sm font-medium text-gray-700">잠금 보관 프로젝트 보기</label>
        <Button label="초기화" severity="secondary" text class="ml-4" @click="resetFilters" />
        <Button label="조회" severity="contrast" class="px-6" @click="fetchProjects" />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <DataTable :value="projects" :paginator="true" :rows="10" dataKey="id" responsiveLayout="scroll" stripedRows class="p-datatable-sm">
        <Column field="id" header="No." headerClass="table-header" style="width: 70px" />

        <Column field="title" header="프로젝트명" headerClass="table-header" style="width: 250px">
          <template #body="{ data }">
            <span class="font-semibold text-blue-600 cursor-pointer hover:underline" @click="goToDetail(data)">
              {{ data.title }}
            </span>
          </template>
        </Column>

        <Column header="일감 수" headerClass="table-header" style="width: 120px">
          <template #body="{ data }">
            <span class="text-gray-600">{{ data.issueCount }}</span>
          </template>
        </Column>

        <Column header="진행률" headerClass="table-header" style="width: 200px">
          <template #body="{ data }">
            <div class="flex flex-col gap-1">
              <ProgressBar :value="data.progress" :showValue="false" style="height: 8px" />
              <span class="text-xs text-right text-gray-400">{{ data.progress }}%</span>
            </div>
          </template>
        </Column>

        <Column field="pm" header="PM" headerClass="table-header" style="width: 120px" />

        <Column header="마감일" headerClass="table-header" style="width: 150px">
          <template #body="{ data }">
            {{ formatDate(data.endDate) }}
          </template>
        </Column>

        <Column headerClass="table-header" style="width: 60px">
          <template #body="{ data }">
            <Button icon="pi pi-ellipsis-h" class="p-button-text p-button-secondary p-button-rounded" @click="toggleMenu($event, data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Menu ref="menu" :model="actionItems" :popup="true" />
  </div>
</template>

<style scoped>
/* 기존 Center 관리 스타일 계승 및 보완 */
:deep(.table-header) {
  background-color: #f8f9fa !important;
  text-align: center !important;
  font-weight: 700 !important;
}

:deep(.p-datatable-tbody > tr > td) {
  padding: 1rem;
  text-align: center;
}

:deep(.p-progressbar) {
  border-radius: 10px;
  background-color: #e5e7eb;
}

:deep(.p-progressbar-value) {
  background-color: #3b82f6; /* 진행률 바 색상 */
}
</style>
