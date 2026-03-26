<script setup>
import Notification from '@/components/notification/Notification.vue';
import { inject } from 'vue';

const selectedProject = inject('selectedProject');

const userId = '20261111';
</script>

<template>
  <div class="h-20 bg-header border-b shadow flex items-center justify-between px-4">
    <!-- 좌측 -->
    <div class="flex items-center gap-3">
      <!-- 메뉴 버튼 -->
      <!-- <Button icon="pi pi-bars" size="large" @click="toggleMenu" plain text /> -->

      <!-- 로고 -->
      <!-- <img src="/demo/images/logo.png" alt="logo" class="w-10 h-10 object-contain" /> -->
      <!-- <span class="font-bold text-4xl title-color">BEEZ</span> -->

      <!-- 프로젝트명 -->
      <span v-if="selectedProject" class="ml-4 text-gray-600">
        {{ selectedProject.name }}
      </span>
      <span v-else class="ml-4 text-gray-600">선택한 프로젝트 이름 나올 자리</span>
    </div>

    <!-- 우측 -->
    <div class="flex items-center gap-4 pr-10">
      <Button icon="pi pi-user" label="사용자명" plain text size="large" />
      <div class="relative inline-flex">
        <Notification :user-id="userId" />
      </div>
      <Button icon="pi pi-sign-out" label="로그아웃" plain text size="large" />
    </div>
  </div>
</template>

<style scope>
.bg-header {
  background-color: #f7f8fc;
}

.title-color {
  color: #f5a623;
}
</style>
