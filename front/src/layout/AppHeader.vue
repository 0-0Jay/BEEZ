<script setup>
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import Notification from '@/components/notification/NotificationComponent.vue';
import { useAuthStore } from '@/stores/auth';
import { inject, ref } from 'vue';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const selectedProject = inject('selectedProject');

const userId = '20261111';

// 로그아웃
const visible = ref(false);
const confirmMsg = ref('');
const openLogoutConfirm = () => {
  confirmMsg.value = '정말 로그아웃 하시겠습니까?';
  visible.value = true;
};

const handleLogoutConfirm = () => {
  authStore.logout();
  visible.value = false;
  router.push('/');
};
</script>

<template>
  <div class="h-20 bg-header border-b shadow flex items-center justify-between px-4">
    <!-- 좌측 -->
    <div class="flex items-center gap-3">
      <!-- 프로젝트명 -->
      <span v-if="selectedProject" class="ml-4 text-gray-600">
        {{ selectedProject.title }}
      </span>
      <span v-else class="ml-4 text-gray-600">선택한 프로젝트 이름 나올 자리</span>
    </div>

    <!-- 우측 -->
    <div class="flex items-center gap-4 pr-10">
      <Button icon="pi pi-user" :label="authStore.user?.name || '사용자명'" plain text size="large" />
      <div class="relative inline-flex">
        <Notification :user-id="userId" />
      </div>
      <Button icon="pi pi-sign-out" label="로그아웃" plain text size="large" @click="openLogoutConfirm" />
    </div>
  </div>

  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="handleLogoutConfirm">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>
</template>

<style scope>
.bg-header {
  background-color: #f7f8fc;
}

.title-color {
  color: #f5a623;
}
</style>
