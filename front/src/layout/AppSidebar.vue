<template>
  <div :class="['bg-gray-900 text-white h-full transition-all duration-300 overflow-hidden flex flex-col', isOpen ? 'w-64' : 'w-20']">
    <!-- 펼쳐진 상태 헤더 -->
    <div v-if="isOpen" class="flex items-center justify-between h-20 px-5 shrink-0">
      <div v-show="showContent" class="flex items-center gap-2">
        <img src="/demo/images/logo.png" alt="logo" class="w-10 h-10 object-contain shrink-0" />
        <span class="font-bold text-4xl title-color whitespace-nowrap">BEEZ</span>
      </div>
      <div v-show="showContent" class="btn-color shrink-0">
        <Button @click="toggleMenu" icon="pi pi-arrow-left" size="large" plain text />
      </div>
    </div>

    <!-- 접힌 상태 헤더 -->
    <div v-else class="flex flex-col items-center px-5 py-5 pb-2 shrink-0 gap-2">
      <template v-if="showContent">
        <img src="/demo/images/logo.png" alt="logo" class="w-10 h-10 object-contain" />
        <div class="btn-color">
          <Button @click="toggleMenu" icon="pi pi-arrow-right" size="large" plain text />
        </div>
      </template>
    </div>

    <!-- 메뉴 -->
    <div v-show="showContent && isOpen" class="px-2 py-2 space-y-1">
      <div class="menu-item whitespace-nowrap cursor-pointer px-3 py-2.5 rounded-lg hover:bg-gray-700 transition-colors duration-150">대시보드</div>
      <div class="menu-item whitespace-nowrap cursor-pointer px-3 py-2.5 rounded-lg hover:bg-gray-700 transition-colors duration-150 flex items-center justify-between">
        <span>프로젝트 목록</span>
      </div>
      <div v-if="selectedProject" class="mt-1">
        <div class="px-3 py-1.5 text-xs font-semibold text-gray-400 uppercase tracking-wider whitespace-nowrap">
          {{ selectedProject.name }}
        </div>
        <div class="mt-1 space-y-0.5">
          <div class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150">개요</div>
          <div class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150">일감목록</div>
          <div class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150">게시판</div>
          <!-- 여기에 기능 메뉴 추가 -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { inject, ref, watch } from 'vue';

const isOpen = inject('menuState');
const toggleMenu = inject('toggleMenu');
const selectedProject = inject('selectedProject');

const showContent = ref(isOpen.value);

watch(isOpen, (val) => {
  if (val) {
    // 펼칠 때: 너비 확장(300ms) 후 콘텐츠 등장
    setTimeout(() => {
      showContent.value = true;
    }, 300);
  } else {
    // 접을 때: 콘텐츠 즉시 제거 → 너비 애니메이션 진행
    showContent.value = false;
    // 접힌 후(300ms) 접힌 상태 콘텐츠(로고+화살표) 등장
    setTimeout(() => {
      showContent.value = true;
    }, 300);
  }
});
</script>

<style scoped>
.btn-color :deep(.p-button) {
  color: #f5a623 !important;
}
.btn-color :deep(.p-button:hover) {
  color: #f5a623cc !important;
  background: rgba(245, 166, 35, 0.1) !important;
}

.menu-item {
  font-size: 1.25rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: #e2e8f0;
}
.menu-item:hover {
  color: #ffffff;
}

.sub-menu-item {
  font-size: 0.925rem;
  font-weight: 500;
  border-left: 2px solid transparent;
}
.sub-menu-item:hover {
  border-left-color: #f5a623;
}
</style>
