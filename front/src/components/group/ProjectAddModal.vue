<script setup>
import { useGroupStore } from '@/stores/group';
import { useProjectStore } from '@/stores/project';
import { useRolesStore } from '@/stores/roles';
import { useToast } from 'primevue';
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';

const props = defineProps({ visible: { type: Boolean, required: true } });
const emit = defineEmits(['update:visible']);

const groupStore = useGroupStore();
const projectStore = useProjectStore();
const roleStore = useRolesStore();
const toast = useToast();

const searchKeyword = ref('');
const selectedItems = ref([]);
const roleOptions = ref([]);
const isRoleLoading = ref(false);
let timer = null;

watch(
  () => props.visible,
  async (val) => {
    if (val) await projectStore.fetchProjects({ title: '' });
  }
);

watch(searchKeyword, (keyword) => {
  clearTimeout(timer);
  timer = setTimeout(async () => {
    await projectStore.fetchProjects({ title: keyword || '' });
  }, 300);
});

onMounted(async () => {
  try {
    await roleStore.findRoles();
    if (roleStore.roleList) {
      roleOptions.value = roleStore.roleList.map((role) => ({
        name: role.name,
        id: role.id
      }));
      isRoleLoading.value = true;
    }
  } catch (e) {
    console.error(e);
  }
});

const displayList = computed(() => {
  if (!projectStore.projects) return [];
  return projectStore.projects.filter((proj) => !groupStore.selectedProjects.some((p) => p.id === proj.id));
});

const close = () => {
  searchKeyword.value = '';
  selectedItems.value = [];
  emit('update:visible', false);
};

const handleAdd = () => {
  if (selectedItems.value.length === 0) {
    toast.add({ severity: 'warn', summary: '미선택', detail: '프로젝트를 선택해주세요.', life: 2000 });
    return;
  }

  // 배열의 길이를 체크해서 역할 미선택 확인
  const hasEmptyRole = selectedItems.value.some((p) => !p.role || p.role.length === 0);
  if (hasEmptyRole) {
    toast.add({ severity: 'warn', summary: '역할 미선택', detail: '선택한 프로젝트의 역할을 지정해주세요.', life: 2000 });
    return;
  }

  groupStore.addProjects(selectedItems.value);
  close();
};

onUnmounted(() => {
  if (timer) clearTimeout(timer);
});
</script>

<template>
  <Dialog :visible="visible" header="프로젝트 추가" modal :style="{ width: '750px' }" @update:visible="close">
    <div class="flex flex-col gap-4 pt-1">
      <div class="relative">
        <InputText v-model="searchKeyword" placeholder="프로젝트명을 입력해주세요." class="w-full" />
        <i class="pi pi-search absolute right-3 top-1/2 -translate-y-1/2 text-stone-400" />
      </div>

      <DataTable :value="displayList" v-model:selection="selectedItems" dataKey="id" :paginator="true" :rows="5" class="p-datatable-sm custom-table">
        <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
        <Column field="title" header="프로젝트명" style="width: 35%"></Column>

        <Column header="역할" style="width: 45%">
          <template #body="slotProps">
            <MultiSelect v-if="isRoleLoading" display="chip" v-model="slotProps.data.role" :options="roleOptions" optionLabel="name" optionValue="id" placeholder="역할 선택" :maxSelectedLabels="999" class="w-full text-xs" @click.stop />
          </template>
        </Column>

        <template #empty>
          <div class="py-4 text-center text-stone-500 text-sm">조회된 프로젝트가 없습니다.</div>
        </template>
      </DataTable>
    </div>
    <template #footer>
      <div class="flex justify-end gap-2">
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
}
:deep(.custom-table .p-datatable-tbody > tr > td) {
  font-size: 0.85rem;
}
:deep(.p-paginator) {
  padding: 1rem 0 !important;
  justify-content: center;
  background: transparent !important;
  border: none !important;
}
:deep(.p-paginator-page-selected) {
  background-color: #fd9e0f !important;
  color: #ffffff !important;
}

:deep(.p-multiselect) {
  border-radius: 8px;
}

:deep(.p-multiselect:hover) {
  border-color: #c8c4b8;
}

:deep(.p-multiselect-chip) {
  background-color: #fef3c7;
  color: #92400e;
  border-radius: 6px;
  font-size: 12px;
}

:deep(.p-multiselect.p-invalid) {
  border-color: #e8920e;
}
</style>
