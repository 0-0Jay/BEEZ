<script setup>
import UserFormModal from '@/components/users/UserFormModal.vue';
import { useUsersStore } from '@/stores/users';
import { computed, defineEmits, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const usersStore = useUsersStore();
const router = useRouter();

const loading = ref(false);
const visible = ref(false);
const emit = defineEmits(['selectProject']); // 자꾸 콘솔에 경고가 생겨서 넣음, 실제로는 사용 안함

const search = reactive({
  name: '',
  status: '',
  startDate: null,
  endDate: null
});

const statusOptions = [
  { label: '활성', value: 'H1' },
  { label: '비활성', value: 'H2' }
];

const currentPage = ref(1);
const pageSize = 10;

// 조회 / 초기화
const fetchUsers = async () => {
  loading.value = true;

  try {
    const searchParams = {
      name: search.name,
      status: search.status,
      startDate: formatDate(search.startDate),
      endDate: formatDate(search.endDate)
    };

    await usersStore.fetchUsers(searchParams);
    currentPage.value = 1; // 검색 시 1페이지로 이동
  } finally {
    loading.value = false;
  }
};

// 페이지네이션
const totalCount = computed(() => usersStore.userList.length);
const totalPages = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)));

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return usersStore.userList.slice(start, start + pageSize);
});

// 초기 로드
onMounted(() => {
  fetchUsers();
});

const resetFilters = () => {
  Object.assign(search, { name: '', status: '', startDate: null, endDate: null });
  currentPage.value = 1;
  fetchUsers();
};

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const openUserFormModal = () => {
  visible.value = true;
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
          <InputText v-model="search.name" placeholder="이름을 입력해 주세요." class="filter-input w-52" @keyup.enter="fetchUsers" />
        </div>

        <label class="filter-label mr-3">상태</label>
        <Select v-model="search.status" :options="statusOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-36 mr-8" />

        <label class="filter-label mr-5">등록일</label>
        <DatePicker v-model="search.startDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-15" />
        <span class="text-sm text-[#6B6B63] px-4">~</span>
        <DatePicker v-model="search.endDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-25" />
      </div>

      <div class="flex gap-2 ml-auto">
        <Button label="초기화" text class="btn-reset filter-btn mr-1" @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" class="btn-amber" @click="fetchUsers" />
      </div>
    </div>

    <!-- 전체 건수 + 사용자 추가 -->
    <div class="flex justify-between items-center mb-3">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ totalCount }}명</span>
      <Button label="사용자 추가" icon="pi pi-plus" class="btn-amber" @click="openUserFormModal" />
      <!-- @click="router.push('/user/create')" -->
    </div>

    <!-- 테이블 -->
    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden mb-6">
      <DataTable :value="pagedUsers" :loading="loading" dataKey="id" :rowHover="true">
        <template #empty>
          <div class="flex flex-col items-center justify-center py-10">
            <i class="pi pi-search text-4xl text-[#C7C7C2] mb-3"></i>
            <p class="text-[#6B6B63]">조회된 사용자 데이터가 없습니다.</p>
          </div>
        </template>

        <Column field="id" header="사원번호" headerClass="table-header" style="width: 160px" />
        <Column field="name" header="이름" headerClass="table-header" style="width: 120px" />
        <Column field="email" header="이메일" headerClass="table-header" style="width: 220px" />

        <Column header="관리자 여부" headerClass="table-header" style="width: 120px">
          <template #body="{ data }">
            {{ data.role && data.role.includes('ADMIN') ? '예' : '아니오' }}
          </template>
        </Column>

        <Column header="등록일" headerClass="table-header" style="width: 150px">
          <template #body="{ data }">{{ data.createdOn }}</template>
        </Column>

        <Column header="상태" headerClass="table-header" style="width: 100px">
          <template #body="{ data }">
            <span :class="data.status === 'H1' ? 'text-[#E8920E] font-semibold' : 'text-[#9A9B90]'">
              {{ data.status === 'H1' ? '활성' : '비활성' }}
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

  <UserFormModal v-model:visible="visible"></UserFormModal>
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
