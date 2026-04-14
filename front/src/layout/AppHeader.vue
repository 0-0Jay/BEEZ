<script setup>
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import Notification from '@/components/notification/NotificationComponent.vue';
import UserDetailModal from '@/components/users/UserDetailModal.vue';
import { useAuthStore } from '@/stores/auth';
import { useProjectStore } from '@/stores/project';
import { useToast } from 'primevue';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

const projectStore = useProjectStore();
const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();
const userId = computed(() => authStore.user?.id);

const selectedProject = computed(() => projectStore.selectedProject);

// 로그아웃
const visible = ref(false);
const confirmMsg = ref('');
const openLogoutConfirm = () => {
  confirmMsg.value = '정말 로그아웃 하시겠습니까?';
  visible.value = true;
};

const handleLogoutConfirm = () => {
  authStore.logout();
  projectStore.selectedProject = null;
  visible.value = false;
  router.push('/');
};

const isModalVisible = ref(false);

const openMyProfile = () => {
  if (authStore.user) {
    isModalVisible.value = true;
  }
};

const handleUserSaved = () => {
  isModalVisible.value = false;

  // 로그아웃
  authStore.logout();
  projectStore.selectedProject = null;

  router.push('/');

  toast.add({
    severity: 'info',
    summary: '재로그인 필요',
    detail: '보안을 위해 다시 로그인해 주세요.',
    life: 3000,
    closable: false
  });
};
</script>

<template>
  <div class="h-20 bg-header border-b shadow flex items-center justify-between px-4">
    <!-- 좌측 -->
    <div class="flex items-center gap-3">
      <!-- 프로젝트명 -->
      <span v-if="selectedProject" class="ml-4 text-gray-600"> {{ selectedProject.title }} ({{ selectedProject.startDate }} - {{ selectedProject.endDate }}) </span>
      <span v-else class="ml-4 text-gray-600">프로젝트를 선택해주세요</span>
    </div>

    <!-- 우측 -->
    <div class="flex items-center gap-4 pr-10">
      <Button icon="pi pi-user" :label="authStore.user?.name || '사용자명'" plain text size="large" @click="openMyProfile" />
      <div class="relative inline-flex">
        <Notification :user-id="userId" />
      </div>
      <Button icon="pi pi-sign-out" label="로그아웃" plain text size="large" @click="openLogoutConfirm" />
    </div>
  </div>

  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="handleLogoutConfirm">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>

  <UserDetailModal v-model:visible="isModalVisible" :user-data="authStore.user" @saved="handleUserSaved"></UserDetailModal>
</template>

<style scope>
.bg-header {
  background-color: #f7f8fc;
}

.title-color {
  color: #f5a623;
}
</style>
