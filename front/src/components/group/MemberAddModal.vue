<script setup>
import { useGroupStore } from '@/stores/group';
import { useUsersStore } from '@/stores/users';
import { useToast } from 'primevue';
import { computed, onUnmounted, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible']);
const groupStore = useGroupStore();
const userStore = useUsersStore();
const toast = useToast();

const searchKeyword = ref('');
const selectedUsers = ref([]);
let timer = null;

watch(
  () => props.visible,
  async (val) => {
    if (val) {
      await userStore.findUsers({ name: '', excludeRoleId: 'ROLE0001' });
    }
  }
);

// 검색어 디바운스 처리
watch(searchKeyword, (keyword) => {
  clearTimeout(timer);
  timer = setTimeout(async () => {
    await userStore.findUsers({ name: keyword || '' });
  }, 300);
});

const displayUserList = computed(() => {
  return userStore.userList.filter((user) => !groupStore.selectedMembers.some((m) => m.id === user.id));
});

const close = () => {
  searchKeyword.value = '';
  selectedUsers.value = [];
  emit('update:visible', false);
};

const handleAdd = () => {
  if (selectedUsers.value.length === 0) {
    toast.add({ severity: 'warn', summary: '미선택', detail: '추가할 사용자를 선택해주세요.', life: 2000 });
    return;
  }

  groupStore.addMembers(selectedUsers.value);
  close();
};

onUnmounted(() => {
  if (timer) clearTimeout(timer);
});
</script>

<template>
  <Dialog :visible="visible" header="구성원 추가" modal :style="{ width: '550px' }" @update:visible="close">
    <div class="flex flex-col gap-4 pt-1">
      <div class="relative">
        <InputText v-model="searchKeyword" placeholder="이름을 입력해주세요." class="w-full" />
        <i class="pi pi-search absolute right-3 top-1/2 -translate-y-1/2 text-stone-400" />
      </div>

      <DataTable
        :value="displayUserList"
        v-model:selection="selectedUsers"
        dataKey="id"
        :paginator="true"
        :rows="5"
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink"
        responsiveLayout="scroll"
        class="p-datatable-sm custom-table"
      >
        <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
        <Column field="name" header="이름" sortable style="width: 30%"></Column>
        <Column field="email" header="이메일" sortable style="width: 60%"></Column>

        <template #empty>
          <div class="py-4 text-center text-stone-500 text-sm">조회된 사용자가 없습니다.</div>
        </template>
      </DataTable>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="추가" raised @click="handleAdd" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
:deep(.custom-table .p-datatable-thead > tr > th) {
  background-color: #f8f8f7;
  font-size: 0.8rem;
  color: #57534e;
}

:deep(.custom-table .p-datatable-tbody > tr > td) {
  font-size: 0.85rem;
  padding: 0.75rem;
}

:deep(.p-paginator) {
  padding: 0.5rem;
  justify-content: center;
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
</style>
