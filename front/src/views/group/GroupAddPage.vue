<!-- GroupAddPage.vue -->
<script setup>
import GroupInfoPanel from '@/components/group/GroupInfoPanel.vue';
import MemberPanel from '@/components/group/MemberPanel.vue';
import ProjectPanel from '@/components/group/ProjectPanel.vue';
import { useGroupStore } from '@/stores/group';
import { useToast } from 'primevue';
import { onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const toast = useToast();
const groupStore = useGroupStore();

const infoPanelRef = ref(null);

const onSave = async () => {
  const isInfoValid = infoPanelRef.value.validate();

  if (!isInfoValid) {
    return;
  }

  try {
    await groupStore.insertGroups();

    toast.add({
      severity: 'success',
      summary: '등록 완료',
      detail: '새 그룹이 등록되었습니다.',
      life: 3000,
      closable: false
    });

    router.push('/group/list');
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || '등록 중 오류가 발생했습니다.';
    if (errorMsg.includes('이름') || errorMsg.includes('중복')) {
      infoPanelRef.value.setExternalError(errorMsg);
    } else {
      toast.add({
        severity: 'error',
        summary: '등록 실패',
        detail: errorMsg,
        life: 3000
      });
    }
  }
};

const onCancel = () => {
  router.push('/group/list');
};

onUnmounted(() => {
  groupStore.resetSelectedData();
});
</script>

<template>
  <div class="p-8 bg-white min-h-full">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-[50px]">그룹 추가</h1>

    <div class="grid grid-cols-3 gap-4 mb-6">
      <GroupInfoPanel ref="infoPanelRef" />
      <MemberPanel />
      <ProjectPanel />
    </div>

    <div class="flex justify-center gap-3 mt-6">
      <Button label="취소" severity="secondary" raised @click="onCancel" />
      <Button label="등록" raised @click="onSave" />
    </div>
  </div>
</template>
