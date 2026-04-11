<!-- GroupManagePage.vue 상세 조회 및 수정 -->
<script setup>
import GroupInfoPanel from '@/components/group/GroupInfoPanel.vue';
import MemberPanel from '@/components/group/MemberPanel.vue';
import ProjectPanel from '@/components/group/ProjectPanel.vue';
import { useGroupStore } from '@/stores/group';
import { useToast } from 'primevue';
import { onMounted, onUnmounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const toast = useToast();
const groupStore = useGroupStore();

const infoPanelRef = ref(null);
const groupId = route.params.id;

onMounted(async () => {
  if (groupId) {
    try {
      await groupStore.findGroupDetail(groupId);
    } catch (err) {
      toast.add({ severity: 'error', summary: '조회 실패', detail: '데이터를 불러오지 못했습니다.' });
      router.push('/group/list');
    }
  }
});

const onUpdate = async () => {
  const isInfoValid = infoPanelRef.value.validate();
  if (!isInfoValid) return;

  try {
    await groupStore.updateGroup(groupId);

    toast.add({
      severity: 'success',
      summary: '수정 완료',
      detail: '그룹 정보가 수정되었습니다.',
      life: 3000,
      closable: false
    });

    router.push('/group/list');
  } catch (err) {
    const errorMsg = err.response?.data?.message || '수정 중 오류가 발생했습니다.';
    toast.add({ severity: 'error', summary: '수정 실패', detail: errorMsg, life: 3000 });
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
    <h1 class="text-2xl font-bold text-[#1A1816] mb-[50px]">그룹 상세 조회/수정</h1>

    <div class="grid grid-cols-3 gap-4 mb-6">
      <GroupInfoPanel ref="infoPanelRef" />
      <MemberPanel />
      <ProjectPanel />
    </div>

    <div class="flex justify-end gap-3 mt-6">
      <Button label="취소" severity="secondary" raised @click="onCancel" />
      <Button label="수정" raised @click="onUpdate" />
    </div>
  </div>
</template>
