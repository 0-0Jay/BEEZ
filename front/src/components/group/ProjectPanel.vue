<!-- ProjectPanel.vue -->
<script setup>
import { useGroupStore } from '@/stores/group';
import { ref } from 'vue';

const groupStore = useGroupStore();

const roleOptions = ['개발자', '디자이너', 'QA'];
const confirmVisible = ref(false);
const pendingDeleteId = ref(null);

const openDeleteConfirm = (id) => {
  pendingDeleteId.value = id;
  confirmVisible.value = true;
};

const onDeleteConfirm = () => {
  groupStore.removeProject(pendingDeleteId.value);
  pendingDeleteId.value = null;
  confirmVisible.value = false;
};
</script>

<template>
  <div class="border border-[#5B6E96] rounded-xl p-6 h-[600px] overflow-y-auto">
    <div class="flex justify-between items-center border-b border-[#C7C7C2] pb-3 mb-4">
      <h2 class="text-sm font-medium text-[#1A1816]">프로젝트</h2>
      <Button label="프로젝트 추가" icon="pi pi-plus" size="small" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" />
    </div>

    <div class="flex flex-col items-center justify-center h-48">
      <i class="pi pi-folder text-3xl text-[#C7C7C2] mb-2" />
      <p class="text-xs text-[#6B6B63]">추가된 프로젝트가 없습니다.</p>
    </div>

    <div class="grid grid-cols-2 gap-2">
      <div v-for="project in groupStore.selectedProjects" :key="project.id" class="border border-[#E0E0DA] rounded-lg p-3 relative">
        <button class="absolute top-2 right-2 text-[#6B6B63] hover:text-red-500 text-base leading-none" @click="openDeleteConfirm(project.id)">×</button>
        <p class="text-xs font-medium text-[#1A1816] mb-2">{{ project.name }}</p>
        <label class="block text-[10px] text-[#6B6B63] mb-1">역할</label>
        <Select :modelValue="project.role" :options="roleOptions" class="w-full text-xs" @update:modelValue="groupStore.updateProjectRole(project.id, $event)" />
      </div>
    </div>
  </div>

  <ConfirmDialog v-model:visible="confirmVisible" confirmLabel="삭제" @confirm="onDeleteConfirm">
    <span class="text-gray-700 font-medium">해당 프로젝트를 삭제하시겠습니까?</span>
  </ConfirmDialog>
</template>
