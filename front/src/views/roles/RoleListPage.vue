<script setup>
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import { useRolesStore } from '@/stores/roles';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const rolesStore = useRolesStore();
const router = useRouter();
const toast = useToast();

const loading = ref(false);
const visible = ref(false);
const confirmMsg = ref('');
const selectedRoleId = ref(null); // 삭제할 role id

const totalCount = computed(() => rolesStore.roleList.length);

onMounted(async () => {
  loading.value = true;
  try {
    await rolesStore.findRoles();
  } finally {
    loading.value = false;
  }
});

const formatPermissions = (perNames) => {
  if (!perNames) return '-';

  const names = perNames.split(',').map((name) => name.trim());

  if (names.length <= 3) {
    return names.join(', ');
  }

  const displayNames = names.slice(0, 2).join(', ');
  const extraCount = names.length - 2;

  return `${displayNames} 외 ${extraCount}건`;
};

// 삭제 컨펌
const openDeleteConfirm = (data) => {
  selectedRoleId.value = data.id;
  confirmMsg.value = `'${data.name}' 역할을 삭제하시겠습니까?`;
  visible.value = true;
};

// 역할 삭제
const onDelete = async () => {
  if (!selectedRoleId.value) return;

  try {
    await rolesStore.deleteRoles(selectedRoleId.value);
    toast.add({
      severity: 'success',
      summary: '삭제 완료',
      detail: '역할 삭제가 완료되었습니다.',
      life: 3000,
      closable: false
    });

    await rolesStore.findRoles();
    visible.value = false;
  } catch (err) {
    const isConflict = err.response?.status === 409;

    toast.add({
      severity: isConflict ? 'warn' : 'error',
      summary: isConflict ? '삭제 불가' : '오류 발생',
      detail: isConflict ? '현재 사용 중인 역할은 삭제할 수 없습니다.' : '서버 통신 중 오류가 발생했습니다.',
      life: 5000
    });
  } finally {
    visible.value = false;
    selectedRoleId.value = null;
  }
};

// 역할 복사 페이지 이동
const onCopy = (data) => {
  router.push({
    path: '/roles/add',
    query: { copyId: data.id }
  });
};

// 역할 수정 페이지 이동
const onEdit = (data) => {
  router.push({
    path: '/roles/edit',
    query: { editId: data.id }
  });
};
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816]">역할 목록</h1>

    <div class="flex justify-between items-center mb-3" style="margin-top: 50px">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ totalCount }}개</span>
      <Button label="새 역할 추가" icon="pi pi-plus" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" @click="$router.push('/roles/add')" />
    </div>

    <!-- 테이블 -->
    <div class="bg-white rounded-xl shadow-sm border border-[#5B6E96] overflow-hidden mb-6">
      <DataTable :value="rolesStore.roleList" :loading="loading" dataKey="id" :rowHover="true" paginator :rows="10" paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown">
        <template #empty>
          <div class="flex flex-col items-center justify-center py-10">
            <i class="pi pi-search text-4xl text-[#C7C7C2] mb-3"></i>
            <p class="text-[#6B6B63]">조회된 역할 데이터가 없습니다.</p>
          </div>
        </template>

        <Column field="name" header="이름" headerClass="table-header" style="width: 120px">
          <template #body="{ data }">
            <span @click="onEdit(data)" class="cursor-pointer hover:underline">
              {{ data.name }}
            </span>
          </template>
        </Column>
        <Column header="권한" headerClass="table-header" style="width: 250px">
          <template #body="{ data }">
            <span v-tooltip.top="data.perNames" class="cursor-help">
              {{ formatPermissions(data.perNames) ? formatPermissions(data.perNames) : '-' }}
            </span>
          </template>
        </Column>

        <Column header="복사" headerClass="table-header" style="width: 100px">
          <template #body="{ data }">
            <Button icon="pi pi-copy" class="p-button-rounded p-button-text p-button-secondary" @click="onCopy(data)" v-tooltip="'역할 복사'" />
          </template>
        </Column>

        <Column header="삭제" headerClass="table-header" style="width: 100px">
          <template #body="{ data }">
            <Button icon="pi pi-trash" :disabled="data.useCount > 0" class="p-button-rounded p-button-text p-button-danger" @click="openDeleteConfirm(data)" v-tooltip="data.useCount > 0 ? '사용 중인 역할은 삭제할 수 없습니다' : '역할 삭제'" />
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="onDelete()">
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

.btn-add {
  --p-button-background: #2d8fad;
  --p-button-border-color: #2d8fad;
  --p-button-hover-background: #256e87;
  --p-button-hover-border-color: #256e87;
  --p-button-active-background: #1e5a6f;
  --p-button-color: #ffffff;
}
</style>
