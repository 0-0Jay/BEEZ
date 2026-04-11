<script setup>
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import { useGroupStore } from '@/stores/group';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const groupStore = useGroupStore();
const router = useRouter();
const toast = useToast();

const loading = ref(false);
const visible = ref(false);
const confirmMsg = ref('');

const selectedGroupId = ref(null);
const searchQuery = ref('');

const totalCount = computed(() => groupStore.groupList.length);

const fetchGroup = async () => {
  loading.value = true;
  try {
    await groupStore.findGroup({ name: searchQuery.value.trim() });
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  await fetchGroup();
});

const onSearch = () => {
  fetchGroup();
};

const onReset = () => {
  searchQuery.value = '';
  fetchGroup();
};

// 삭제 컨펌 오픈
const openDeleteConfirm = (data) => {
  selectedGroupId.value = data.id;
  confirmMsg.value = `'${data.name}' 그룹을 삭제하시겠습니까?`;
  visible.value = true;
};

// 그룹 삭제 실행
const onDelete = async () => {
  if (!selectedGroupId.value) return;

  try {
    await groupStore.deleteGroup(selectedGroupId.value);
    toast.add({
      severity: 'success',
      summary: '삭제 완료',
      detail: '그룹 삭제가 완료되었습니다.',
      life: 3000,
      closable: false
    });
    await fetchGroup();
  } catch (err) {
    const isConflict = err.response?.status === 409;
    toast.add({
      severity: isConflict ? 'warn' : 'error',
      summary: isConflict ? '삭제 불가' : '오류 발생',
      detail: isConflict ? '현재 프로젝트에 할당된 그룹은 삭제할 수 없습니다.' : '서버 통신 중 오류가 발생했습니다.',
      life: 5000
    });
  } finally {
    visible.value = false;
    selectedGroupId.value = null;
  }
};

const onEdit = (data) => {
  router.push(`/group/${data.id}`);
};
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816]">그룹 목록</h1>

    <div class="flex items-center gap-2 mt-[50px] mb-3">
      <div class="relative flex items-center">
        <InputText v-model="searchQuery" placeholder="그룹명을 입력해 주세요." class="pr-10 w-64 border border-[#C7C7C2] rounded-lg text-sm" @keyup.enter="onSearch" />
      </div>
      <Button label="초기화" severity="secondary" raised @click="onReset" />
      <Button label="조회" icon="pi pi-search" raised @click="onSearch" />
    </div>

    <div class="flex justify-between items-center mb-3">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ totalCount }}개</span>
      <Button label="새 그룹 추가" icon="pi pi-plus" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" @click="$router.push('/group/add')" />
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#5B6E96] overflow-hidden mb-6">
      <DataTable :value="groupStore.groupList" :loading="loading" dataKey="id" :rowHover="true" paginator :rows="10" paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown">
        <template #empty>
          <div class="flex flex-col items-center justify-center py-10">
            <i class="pi pi-search text-4xl text-[#C7C7C2] mb-3"></i>
            <p class="text-[#6B6B63]">조회된 그룹 데이터가 없습니다.</p>
          </div>
        </template>

        <Column field="id" header="번호" headerClass="table-header" style="padding-left: 30px; width: 10%">
          <template #body="{ data }">
            <span @click="onEdit(data)" class="cursor-pointer hover:underline">{{ data.id }}</span>
          </template>
        </Column>

        <Column field="name" header="그룹명" headerClass="table-header" style="width: 20%">
          <template #body="{ data }">
            <span @click="onEdit(data)" class="cursor-pointer hover:underline">{{ data.name }}</span>
          </template>
        </Column>

        <Column field="memberCnt" header="인원 수" headerClass="table-header" style="width: 13%">
          <template #body="{ data }">
            <span>{{ data.memberCnt }}명</span>
          </template>
        </Column>

        <Column field="projectCnt" header="참여 중 프로젝트" headerClass="table-header" style="width: 13%">
          <template #body="{ data }">
            <span>{{ data.projectCnt }}개</span>
          </template>
        </Column>

        <Column field="description" header="설명" headerClass="table-header">
          <template #body="{ data }">
            <span>{{ data.description }}</span>
          </template>
        </Column>

        <Column header="삭제" headerClass="table-header" style="width: 13%">
          <template #body="{ data }">
            <Button
              icon="pi pi-trash"
              class="p-button-rounded p-button-text p-button-danger"
              :disabled="data.projectCnt > 0"
              @click="openDeleteConfirm(data)"
              v-tooltip="data.projectCnt > 0 ? '프로젝트에 속한 그룹은 삭제할 수 없습니다' : '그룹 삭제'"
            />
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="onDelete">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>
</template>

<style scoped>
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
.btn-reset {
  --p-button-border-color: #c7c7c2;
  --p-button-color: #3a3b35;
  --p-button-hover-background: #f2f0eb;
}
</style>
