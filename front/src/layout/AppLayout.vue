<template>
  <div class="flex h-screen">
    <!-- 사이드바 -->
    <AppSidebar />

    <!-- 메인 영역 -->
    <div class="flex-1 flex flex-col">
      <!-- 헤더 -->
      <AppHeader />

      <!-- 페이지 -->
      <div class="p-4 bg-white flex-1 overflow-auto min-w-0">
        <router-view />
      </div>
      <ChatRoom v-if="project != null"></ChatRoom>
    </div>
  </div>
</template>

<script setup>
import ChatRoom from '@/components/chat/ChatComponent.vue';
import { useProjectStore } from '@/stores/project';
import { computed, provide, ref } from 'vue';
import AppHeader from './AppHeader.vue';
import AppSidebar from './AppSidebar.vue';

const projectStore = useProjectStore();
const project = computed(() => projectStore.selectedProject);

// 사이드바 상태
const isOpen = ref(true);
const toggleMenu = () => {
  isOpen.value = !isOpen.value;
};

// provide로 전역 공유
provide('menuState', isOpen);
provide('toggleMenu', toggleMenu);
</script>
