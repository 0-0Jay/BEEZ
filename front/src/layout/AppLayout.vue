<template>
  <div class="flex h-screen">
    <!-- 사이드바 -->
    <AppSidebar />

    <!-- 메인 영역 -->
    <div class="flex-1 flex flex-col">
      <!-- 헤더 -->
      <AppHeader />

      <!-- 페이지 -->
      <div class="p-4 bg-gray-100 flex-1 overflow-auto">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { provide, ref } from 'vue';
import AppHeader from './AppHeader.vue';
import AppSidebar from './AppSidebar.vue';

// 사이드바 상태
const isOpen = ref(true);
const toggleMenu = () => {
  isOpen.value = !isOpen.value;
};

// provide로 전역 공유
provide('menuState', isOpen);
provide('toggleMenu', toggleMenu);
</script>
