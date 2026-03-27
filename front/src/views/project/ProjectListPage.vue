<script setup>
import { useProjectStore } from '@/stores/project';
import { storeToRefs } from 'pinia';
import { inject, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

// --- 1. 스토어 연결 (진짜 데이터 소스) ---
const projectStore = useProjectStore();
const { projects, loading } = storeToRefs(projectStore);

const setProject = inject('selectedProject');
const router = useRouter();
const menu = ref();
const selectedRow = ref(null);

// --- 2. 필터 상태 ---
const filters = reactive({
  title: null,
  pm: null,
  startDate: null,
  endDate: null,
  showArchived: false
});

// Select 옵션 (이건 보통 공통 코드 API로 받아오지만, 일단 화면용으로 유지)
const projectOptions = ref(['모바일 앱 리뉴얼', 'API 마이그레이션', '대시보드 설계']);
const pmOptions = ref(['곽현우', '노정화', '조영진', '한유민']);

// --- 3. 진짜 조회 로직 ---
onMounted(() => {
  fetchProjects(); // 페이지 진입 시 최초 조회
});

const fetchProjects = () => {
  // 스토어의 액션을 호출하여 API 통신 실행
  // 필터값을 인자로 넘겨 서버측 필터링이 가능하도록 구성
  projectStore.fetchProjects(filters);
};

const resetFilters = () => {
  Object.assign(filters, {
    title: null,
    pm: null,
    startDate: null,
    endDate: null,
    showArchived: false
  });
  fetchProjects(); // 초기화 후 재조회
};

// --- 나머지 유틸리티 ---
const toggleMenu = (event, data) => {
  selectedRow.value = data;
  menu.value.toggle(event);
};

const goToDetail = (project) => {
  if (setProject) setProject.value = { title: project.title };
  // router.push(`/project/detail/${project.id}`);
};

const formatDate = (v) => {
  if (!v) return '-';
  const d = new Date(v);
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`;
};

const actionItems = ref([
  { label: '프로젝트 잠금보관', icon: 'pi pi-lock' },
  { label: '프로젝트 복사', icon: 'pi pi-copy' },
  { label: '프로젝트 삭제', icon: 'pi pi-trash', class: 'text-red-500' }
]);
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen">
    <!-- 타이틀 + 등록 버튼 -->
    <div class="flex justify-between items-end mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 목록</h1>
      <Button label="프로젝트 등록" icon="pi pi-plus" outlined class="btn-register-outline" />
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
        <Button label="조회" icon="pi pi-search" class="btn-amber" @click="fetchProjects" />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="projects" :loading="loading" dataKey="id" :rowHover="true">
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
</style>
