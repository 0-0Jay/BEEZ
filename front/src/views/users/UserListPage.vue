<script setup>
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// --- 하드코딩 데이터 ---
const allUsers = ref([
  { id: 1, employeeId: 'EMP-2026-0001', name: '관리자', email: 'admin@test.com', isAdmin: true, createdAt: '2026-03-19', status: 'ACTIVE' },
  { id: 2, employeeId: 'EMP-2026-0002', name: '곽현우', email: 'khw12@test.com', isAdmin: true, createdAt: '2026-03-19', status: 'ACTIVE' },
  { id: 3, employeeId: 'EMP-2026-0003', name: '노정화', email: 'njh123@test.com', isAdmin: true, createdAt: '2026-03-19', status: 'ACTIVE' },
  { id: 4, employeeId: 'EMP-2026-0004', name: '조영진', email: 'jyj123@test.com', isAdmin: true, createdAt: '2026-03-19', status: 'ACTIVE' },
  { id: 5, employeeId: 'EMP-2026-0005', name: '한유민', email: 'hym12@test.com', isAdmin: true, createdAt: '2026-03-19', status: 'ACTIVE' },
  { id: 6, employeeId: 'EMP-2026-0006', name: '이수진', email: 'lsj@test.com', isAdmin: false, createdAt: '2026-03-20', status: 'INACTIVE' },
  { id: 7, employeeId: 'EMP-2026-0007', name: '박민준', email: 'pmj@test.com', isAdmin: false, createdAt: '2026-03-20', status: 'ACTIVE' },
  { id: 8, employeeId: 'EMP-2026-0008', name: '김태희', email: 'kth@test.com', isAdmin: false, createdAt: '2026-03-21', status: 'ACTIVE' },
  { id: 9, employeeId: 'EMP-2026-0009', name: '정우성', email: 'jws@test.com', isAdmin: false, createdAt: '2026-03-21', status: 'INACTIVE' },
  { id: 10, employeeId: 'EMP-2026-0010', name: '최예린', email: 'cyr@test.com', isAdmin: false, createdAt: '2026-03-22', status: 'ACTIVE' },
  { id: 11, employeeId: 'EMP-2026-0011', name: '윤서준', email: 'ysj@test.com', isAdmin: false, createdAt: '2026-03-22', status: 'ACTIVE' },
  { id: 12, employeeId: 'EMP-2026-0012', name: '강다은', email: 'kde@test.com', isAdmin: false, createdAt: '2026-03-23', status: 'ACTIVE' }
]);

const loading = ref(false);

// --- 필터 상태 ---
const filters = reactive({
  name: '',
  status: null,
  startDate: null,
  endDate: null
});

const statusOptions = [
  { label: '활성', value: 'ACTIVE' },
  { label: '비활성', value: 'INACTIVE' }
];

// --- 페이지네이션 ---
const currentPage = ref(1);
const pageSize = 10;

// --- 필터링된 전체 목록 ---
const filteredUsers = computed(() => {
  return allUsers.value.filter((u) => {
    if (filters.name && !u.name.includes(filters.name)) return false;
    if (filters.status && u.status !== filters.status) return false;
    if (filters.startDate && u.createdAt < formatDate(filters.startDate)) return false;
    if (filters.endDate && u.createdAt > formatDate(filters.endDate)) return false;
    return true;
  });
});

const totalCount = computed(() => filteredUsers.value.length);
const totalPages = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)));

// --- 현재 페이지 데이터 ---
const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredUsers.value.slice(start, start + pageSize);
});

// --- 조회 / 초기화 ---
const fetchUsers = () => {
  currentPage.value = 1;
};

const resetFilters = () => {
  Object.assign(filters, { name: '', status: null, startDate: null, endDate: null });
  currentPage.value = 1;
};

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8]">
    <h1 class="text-2xl font-bold text-[#1A1816]">사용자 목록</h1>
    <!-- 검색 필터 -->
    <div class="bg-[#F2F0EB] px-10 py-8 rounded-lg mb-4 shadow-sm border border-[#C7C7C2] flex items-center">
      <div class="flex items-center flex-wrap gap-y-3">
        <div class="flex items-center mr-8">
          <label class="filter-label mr-3">사원 이름</label>
          <InputText v-model="filters.name" placeholder="이름을 입력해 주세요." class="filter-input w-52" />
        </div>

        <label class="filter-label mr-3">상태</label>
        <Select v-model="filters.status" :options="statusOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-36 mr-8" />

        <label class="filter-label mr-5">등록일</label>
        <DatePicker v-model="filters.startDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-15" />
        <span class="text-sm text-[#6B6B63] px-4">~</span>
        <DatePicker v-model="filters.endDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-25" />
      </div>

      <div class="flex gap-2 ml-auto">
        <Button label="초기화" text class="btn-reset filter-btn mr-1" @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" class="btn-amber" @click="fetchUsers" />
      </div>
    </div>

    <!-- 전체 건수 + 사용자 추가 -->
    <div class="flex justify-between items-center mb-3">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ totalCount }}명</span>
      <Button label="사용자 추가" icon="pi pi-plus" class="btn-amber" @click="router.push('/user/create')" />
    </div>

    <!-- 테이블 -->
    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden mb-6">
      <DataTable :value="pagedUsers" :loading="loading" dataKey="id" :rowHover="true">
        <template #empty>
          <div class="text-center py-10 text-gray-400">조회된 데이터가 없습니다.</div>
        </template>

        <Column header="번호" headerClass="table-header" style="width: 80px">
          <template #body="slotProps">
            {{ (currentPage - 1) * pageSize + slotProps.index + 1 }}
          </template>
        </Column>
        <Column field="employeeId" header="사원번호" headerClass="table-header" style="width: 160px" />
        <Column field="name" header="이름" headerClass="table-header" style="width: 120px" />
        <Column field="email" header="이메일" headerClass="table-header" style="width: 220px" />
        <Column header="관리자 여부" headerClass="table-header" style="width: 120px">
          <template #body="{ data }">{{ data.isAdmin ? '예' : '아니오' }}</template>
        </Column>
        <Column header="등록일" headerClass="table-header" style="width: 150px">
          <template #body="{ data }">{{ data.createdAt }}</template>
        </Column>
        <Column header="상태" headerClass="table-header" style="width: 100px">
          <template #body="{ data }">
            <span :class="data.status === 'ACTIVE' ? 'text-[#E8920E] font-semibold' : 'text-[#9A9B90]'">
              {{ data.status === 'ACTIVE' ? '활성' : '비활성' }}
            </span>
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- 페이지네이션 -->
    <div class="flex justify-center items-center gap-1 mt-4">
      <button class="pagination-btn" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">
        <i class="pi pi-chevron-left text-xs" />
      </button>
      <button v-for="page in totalPages" :key="page" class="pagination-btn" :class="{ 'pagination-btn-active': page === currentPage }" @click="goToPage(page)">
        {{ page }}
      </button>
      <button class="pagination-btn" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
        <i class="pi pi-chevron-right text-xs" />
      </button>
    </div>
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
.pagination-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid #e5e2d9;
  background: white;
  color: #3a3b35;
  font-size: 0.85rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;
}
.pagination-btn:hover:not(:disabled) {
  background-color: #f2f0eb;
}
.pagination-btn:disabled {
  opacity: 0.35;
  cursor: default;
}
.pagination-btn-active {
  background-color: #e8920e !important;
  color: white !important;
  border-color: #e8920e !important;
  font-weight: 700;
}
</style>
