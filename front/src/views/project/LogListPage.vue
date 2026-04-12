<script setup>
import { useProjectStore } from '@/stores/project';
import { useVersionStore } from '@/stores/version';
import { storeToRefs } from 'pinia';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const projectStore = useProjectStore();
const versionStore = useVersionStore();
const { logs, loading, logTotal } = storeToRefs(projectStore);
const { commonCodeList } = storeToRefs(versionStore);
const route = useRoute();
const router = useRouter();

onMounted(async () => {
  await versionStore.findCommonCodeList();
  await projectStore.fetchLogs({ page: 1, size: rowsPerPage.value });
});

const categoryOptions = computed(() => commonCodeList.value.filter((c) => c.cgroup === '0B'));
const typeOptions = computed(() => commonCodeList.value.filter((c) => c.cgroup === '0A'));
const userOptions = computed(() => {
  if (!logs.value || !Array.isArray(logs.value)) return [];
  return [...new Map(logs.value.map((p) => [p.userId, { label: p.name, value: p.userId }])).values()];
});

const filters = reactive({
  type: null,
  category: null,
  userId: null,
  startDate: null,
  endDate: null,
  content: null
});

// const totalCount = computed(() => logs.value.length);
const currentPage = ref(1);
const rowsPerPage = ref(10);

const handleSearch = async () => {
  currentPage.value = 1;
  await projectStore.fetchLogs({
    ...filters,
    startDate: formatDate(filters.startDate),
    endDate: formatDate(filters.endDate),
    page: 1,
    size: rowsPerPage.value
  });
};

const resetFilters = () => {
  Object.assign(filters, { type: null, category: null, startDate: null, endDate: null, content: null });
  projectStore.fetchLogs();
  currentPage.value = 1;
  projectStore.fetchLogs({ page: 1, size: rowsPerPage.value });
};

const onPageChange = async (e) => {
  currentPage.value = e.page + 1;
  await projectStore.fetchLogs({
    ...filters,
    page: currentPage.value,
    size: rowsPerPage.value
  });
};

const goToLink = (link) => {
  window.location.href = link;
};

const typeStyleMap = {
  A0: { bg: '#F5F5F5', color: '#616161' }, // 기타
  A1: { bg: '#E8F5E9', color: '#2E7D32' }, // 생성
  A2: { bg: '#E3F2FD', color: '#1565C0' }, // 수정
  A3: { bg: '#FFEBEE', color: '#C62828' } // 삭제
};

const categoryStyleMap = {
  B0: { bg: '#F3E5F5', color: '#6A1B9A' }, // 프로젝트
  B1: { bg: '#EDE7F6', color: '#4527A0' }, // 버전
  B2: { bg: '#E3F2FD', color: '#1565C0' }, // 일감
  B3: { bg: '#FFF3E0', color: '#E65100' }, // 역할/권한
  B4: { bg: '#FCE4EC', color: '#880E4F' }, // 위키
  B5: { bg: '#E0F7FA', color: '#006064' }, // 문서
  B6: { bg: '#F5F5F5', color: '#616161' } // 기타
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const formatDateTime = (datetime) => {
  if (!datetime) return '';
  const d = new Date(datetime);
  const date = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  const time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;
  return `${date} ${time}`;
};
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-4">로그 목록</h1>

    <!-- 검색 필터 -->
    <div class="bg-[#F2F3F8] px-10 py-8 rounded-lg mb-4 shadow-sm border border-[#ECEEF4]">
      <!-- 1줄: 드롭다운 + 날짜 -->
      <div class="flex items-center flex-wrap gap-y-3 mb-8">
        <div class="flex items-center mr-15">
          <label class="filter-label mr-5">구분</label>
          <Select v-model="filters.category" :options="categoryOptions" optionLabel="name" optionValue="id" placeholder="선택" class="filter-input w-45" showClear />
        </div>

        <div class="flex items-center mr-15">
          <label class="filter-label mr-5 ml-20">로그 유형</label>
          <Select v-model="filters.type" :options="typeOptions" optionLabel="name" optionValue="id" placeholder="선택" class="filter-input w-45" showClear />
        </div>

        <div class="flex items-center mr-15">
          <label class="filter-label mr-5 ml-20">담당자</label>
          <Select v-model="filters.userId" :options="userOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-45" showClear />
        </div>
      </div>

      <!-- 2줄: 내용 + 버튼 -->
      <div class="flex items-center">
        <div class="flex items-center mr-8">
          <label class="filter-label mr-5">내용</label>
          <InputText v-model="filters.content" placeholder="검색할 키워드를 입력하세요." class="filter-input w-100" @keyup.enter="handleSearch" />
        </div>

        <div class="flex items-center">
          <label class="filter-label mr-5 ml-30">발생일</label>
          <DatePicker v-model="filters.startDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-20" />
          <span class="text-sm text-[#6B6B63] px-2">~</span>
          <DatePicker v-model="filters.endDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 ml-4" />
        </div>
        <div class="flex gap-2 ml-auto">
          <Button label="초기화" severity="secondary" raised @click="resetFilters" />
          <Button label="조회" icon="pi pi-search" raised @click="handleSearch" />
        </div>
      </div>
    </div>

    <div class="flex justify-between items-center mb-3">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ logTotal }}건</span>
    </div>

    <!-- 테이블 -->
    <div class="bg-white rounded-xl shadow-sm border border-[#5B6E96] overflow-hidden mb-6">
      <DataTable
        :value="logs"
        :loading="loading"
        dataKey="logId"
        :rowHover="true"
        :paginator="true"
        :rows="rowsPerPage"
        :totalRecords="logTotal"
        :lazy="true"
        @page="onPageChange"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink"
        tableStyle="width: 100%"
      >
        <template #empty>
          <div class="flex flex-col items-center justify-center py-10">
            <i class="pi pi-search text-4xl text-[#C7C7C2] mb-3"></i>
            <p class="text-[#6B6B63]">조회된 로그 데이터가 없습니다.</p>
          </div>
        </template>

        <Column field="logId" header="로그ID" headerClass="table-header" style="width: 20%" />

        <Column header="구분" headerClass="table-header" style="width: 10%">
          <template #body="{ data }">
            <span
              class="type-badge"
              :style="{
                background: categoryStyleMap[data.category]?.bg,
                color: categoryStyleMap[data.category]?.color
              }"
            >
              {{ data.categoryName }}
            </span>
          </template>
        </Column>

        <Column header="로그 유형" headerClass="table-header" style="width: 10%">
          <template #body="{ data }">
            <span
              class="type-badge"
              :style="{
                background: typeStyleMap[data.type]?.bg,
                color: typeStyleMap[data.type]?.color
              }"
            >
              {{ data.typeName }}
            </span>
          </template>
        </Column>

        <Column header="내용" headerClass="table-header" style="width: 30%">
          <template #body="{ data }">
            <span class="log-content-link" @click="goToLink(data.link)" :title="data.link" style="display: block; text-align: left; padding-left: 1rem">
              {{ data.content }}
              <i class="pi pi-external-link ml-1 text-xs opacity-50"></i>
            </span>
          </template>
        </Column>

        <Column field="name" header="담당자" headerClass="table-header" style="width: 10%" />

        <Column header="발생일시" headerClass="table-header" style="width: 20%">
          <template #body="{ data }">
            {{ formatDateTime(data.createdOn) }}
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- 페이지네이션 -->
    <!--<div class="flex justify-center mt-4">
      <Paginator :rows="rowsPerPage" :totalRecords="logTotal" :first="(currentPage - 1) * rowsPerPage" @page="onPageChange" template="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink" />
    </div>-->
  </div>
</template>

<style scoped>
.filter-label {
  font-size: 1rem;
  font-weight: 600;
  color: #000000c2;
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
  background-color: #5b6e96 !important;
  color: #dde3f0 !important;
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

:deep(.p-paginator) {
  background: transparent !important;
  border: none !important;
  padding: 1rem 0 !important;
}
:deep(.p-paginator-page-selected) {
  background-color: #fd9e0f !important;
  color: #ffffff !important;
  border-color: #fd9e0f !important;
  font-weight: 700 !important;
}
:deep(.p-paginator-page:not(.p-highlight):hover),
:deep(.p-paginator-first:hover),
:deep(.p-paginator-prev:hover),
:deep(.p-paginator-next:hover),
:deep(.p-paginator-last:hover) {
  background-color: #f2f0eb !important;
  color: #1a1816 !important;
}

.type-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 600;
}

.log-content-link {
  cursor: pointer;
  color: #1a1816;
  transition: color 0.15s;
}
.log-content-link:hover {
  color: #2d8fad;
  text-decoration: underline;
}
:deep(.p-paginator) {
  background: transparent !important;
  border: none !important;
  padding: 1rem 0 !important;
}
:deep(.p-paginator-page-selected) {
  background-color: #fd9e0f !important;
  color: #ffffff !important;
  border-color: #fd9e0f !important;
  font-weight: 700 !important;
}
:deep(.p-paginator-page:not(.p-highlight):hover),
:deep(.p-paginator-first:hover),
:deep(.p-paginator-prev:hover),
:deep(.p-paginator-next:hover),
:deep(.p-paginator-last:hover) {
  background-color: #f2f0eb !important;
  color: #1a1816 !important;
}
</style>
