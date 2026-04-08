<script setup>
import UserFormModal from '@/components/users/UserFormModal.vue';
import { useUsersStore } from '@/stores/users';
import { computed, onMounted, reactive, ref } from 'vue';

const usersStore = useUsersStore();

const loading = ref(false);
const visible = ref(false);
const emit = defineEmits(['selectProject']); // 콘솔창에 경고 떠서 넣음

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

const totalCount = computed(() => usersStore.userList.length);

const findUsers = async () => {
  loading.value = true;
  try {
    await usersStore.findUsers({
      name: search.name,
      status: search.status,
      startDate: formatDate(search.startDate),
      endDate: formatDate(search.endDate)
    });
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  findUsers();
});

const resetFilters = () => {
  Object.assign(search, { name: '', status: '', startDate: null, endDate: null });
  findUsers();
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const openUserFormModal = () => {
  visible.value = true;
};
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816]">사용자 목록</h1>

    <div class="flex justify-between items-center mb-3">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ totalCount }}명</span>
      <Button label="사용자 추가" icon="pi pi-plus" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" @click="openUserFormModal" />
    </div>

    <!-- 검색 필터 -->
    <div class="bg-[#F2F3F8] px-10 py-8 rounded-lg mb-4 shadow-sm border border-[#ECEEF4] flex items-center">
      <div class="flex items-center flex-wrap gap-y-3">
        <div class="flex items-center mr-8">
          <label class="filter-label mr-3">사원 이름</label>
          <InputText v-model="search.name" placeholder="이름을 입력해 주세요." class="filter-input w-52" @keyup.enter="findUsers" />
        </div>

        <label class="filter-label mr-3">상태</label>
        <Select v-model="search.status" :options="statusOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-36 mr-8" />

        <label class="filter-label mr-5">등록일</label>
        <DatePicker v-model="search.startDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-15" />
        <span class="text-sm text-[#6B6B63] px-4">~</span>
        <DatePicker v-model="search.endDate" dateFormat="yy-mm-dd" placeholder="YYYY-MM-DD" class="filter-input w-36 mr-25" />
      </div>

      <div class="flex gap-2 ml-auto">
        <Button label="초기화" severity="secondary" raised @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" raised @click="findUsers" />
      </div>
    </div>

    <!-- 테이블 -->
    <div class="bg-white rounded-xl shadow-sm border border-[#5B6E96] overflow-hidden mb-6">
      <DataTable :value="usersStore.userList" :loading="loading" dataKey="id" :rowHover="true" paginator :rows="10" paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown">
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
            {{ data.role && data.role.includes('ROLE0001') ? '예' : '아니오' }}
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
  </div>

  <UserFormModal v-model:visible="visible" />
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

:deep(.p-paginator-page-selected:focus),
:deep(.p-paginator-page-selected:focus-visible) {
  box-shadow: none !important;
  outline: none !important;
  border-color: #fd9e0f !important;
}

.btn-add {
  --p-button-background: #2d8fad;
  --p-button-border-color: #2d8fad;
  --p-button-hover-background: #256e87;
  --p-button-hover-border-color: #256e87;
  --p-button-active-background: #1e5a6f;
  --p-button-color: #ffffff;
}
</style>
