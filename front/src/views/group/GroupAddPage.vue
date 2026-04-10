<!-- GroupAddPage.vue -->
<script setup>
import GroupInfoPanel from '@/components/group/GroupInfoPanel.vue';
import MemberPanel from '@/components/group/MemberPanel.vue';
import ProjectPanel from '@/components/group/ProjectPanel.vue';
import { useGroupStore } from '@/stores/group';
import { useRouter } from 'vue-router';

const router = useRouter();
const groupStore = useGroupStore();

const onSave = async () => {
  await groupStore.createGroup();
  router.push('/groups');
};

const onCancel = () => {
  // groupStore.resetForm();
  router.push('/group/list');
};
</script>

<template>
  <div class="p-8 bg-white min-h-full">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-[50px]">그룹 추가</h1>

    <div class="grid grid-cols-3 gap-4 mb-6">
      <GroupInfoPanel />
      <MemberPanel />
      <ProjectPanel />
    </div>

    <div class="flex justify-end gap-3 mt-6">
      <Button label="취소" severity="secondary" raised @click="onCancel" />
      <Button label="등록" raised @click="onSave" />
    </div>
  </div>
</template>
