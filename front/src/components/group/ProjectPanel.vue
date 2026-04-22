<script setup>
import { useGroupStore } from '@/stores/group';
import { useRolesStore } from '@/stores/roles';
import { onMounted, ref } from 'vue';
import ProjectAddModal from './ProjectAddModal.vue';

const groupStore = useGroupStore();
const roleStore = useRolesStore();
const visible = ref(false);

const roleOptions = ref([]);
const isLoaded = ref(false);

onMounted(async () => {
  try {
    await roleStore.findRoles();
    if (roleStore.roleList) {
      roleOptions.value = roleStore.roleList.map((role) => ({
        name: role.name,
        id: role.id
      }));
      isLoaded.value = true;
    }
  } catch (error) {
    console.error('역할 목록 로드 실패:', error);
  }
});
</script>

<template>
  <div class="border border-[#5B6E96] rounded-xl p-6 h-[620px] overflow-y-auto">
    <div class="flex justify-between items-center border-b border-[#C7C7C2] pb-3 mb-4">
      <h2 class="font-medium text-[#1A1816]">프로젝트</h2>
      <Button label="프로젝트 추가" icon="pi pi-plus" size="medium" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" @click="visible = true" />
    </div>

    <div v-if="groupStore.selectedProjects.length === 0" class="flex flex-col items-center justify-center h-48 text-[#6B6B63]">
      <i class="pi pi-folder text-3xl mb-2" />
      <p>추가된 프로젝트가 없습니다.</p>
    </div>

    <div v-else class="grid grid-cols-2 gap-2">
      <div v-for="project in groupStore.selectedProjects" :key="project.id" class="border border-[#E0E0DA] rounded-lg p-3 relative bg-white">
        <button class="absolute top-2 right-2 text-[#6B6B63] hover:text-red-500" @click="groupStore.removeProject(project.id)">×</button>

        <p class="font-medium text-[#1A1816] mb-2 truncate" :title="project.title">
          {{ project.title }}
        </p>

        <label class="block text-[12px] text-[#6B6B63] mb-1">역할(다중 선택 가능)</label>
        <MultiSelect v-if="isLoaded" v-model="project.role" :options="roleOptions" optionLabel="name" optionValue="id" placeholder="역할 선택" display="chip" :maxSelectedLabels="999" class="w-full custom-multi" />
        <div v-else class="h-8 bg-stone-100 animate-pulse rounded"></div>
      </div>
    </div>
  </div>

  <ProjectAddModal v-model:visible="visible" />
</template>

<style scoped>
:deep(.custom-multi .p-multiselect-label) {
  padding: 0.25rem;
}

:deep(.p-multiselect) {
  border-radius: 8px;
}

:deep(.p-multiselect:hover) {
  border-color: #c8c4b8;
}

:deep(.p-multiselect.p-focus) {
  border-color: #f5a623;
  box-shadow: 0 0 0 3px rgba(245, 166, 35, 0.15);
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
