<script setup>
import { useAuthStore } from '@/stores/auth';
import { useProjectStore } from '@/stores/project';
import axios from 'axios';
import { computed, inject, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goWiki = async (projectId) => {
  try {
    const res = await axios.get(`/api/wiki/latest/${projectId}`);

    if (res.data) {
      const wikiId = res.data.wikiId;
      router.push(`/wiki/detail/${projectId}/${wikiId}`);
    } else {
      router.push(`/wiki/write/${projectId}`);
    }
  } catch (e) {
    console.error(e);
  }
};
const isOpen = inject('menuState');
const toggleMenu = inject('toggleMenu');

const authStore = useAuthStore();
const projectStore = useProjectStore();

const selectedProject = computed(() => projectStore.selectedProject);
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

const isAdminMenuOpen = ref(false);

const toggleAdminMenu = () => {
  isAdminMenuOpen.value = !isAdminMenuOpen.value;
};
</script>

<template>
  <div :class="['bg-gray-900 text-white h-full transition-all duration-300 overflow-hidden flex flex-col', isOpen ? 'w-64' : 'w-20']">
    <!-- 펼쳐진 상태 사이드바 상단 -->
    <div v-if="isOpen" class="flex items-center justify-between h-20 px-5 shrink-0">
      <div v-show="showContent" class="flex items-center gap-2">
        <img src="/demo/images/logo.png" alt="logo" class="w-10 h-10 object-contain shrink-0" />
        <span class="font-bold text-4xl title-color whitespace-nowrap">BEEZ</span>
      </div>
      <div v-show="showContent" class="btn-color shrink-0">
        <Button @click="toggleMenu" icon="pi pi-arrow-left" size="large" plain text />
      </div>
    </div>

    <!-- 접힌 상태 사이드바 상단 -->
    <div v-else class="flex flex-col items-center px-5 py-5 pb-2 shrink-0 gap-2">
      <template v-if="showContent">
        <img src="/demo/images/logo.png" alt="logo" class="w-10 h-10 object-contain" />
        <div class="btn-color">
          <Button @click="toggleMenu" icon="pi pi-arrow-right" size="large" plain text />
        </div>
      </template>
    </div>

    <!-- 메뉴 -->
    <div v-show="showContent && isOpen">
      <router-link to="/main" class="menu-item whitespace-nowrap cursor-pointer px-3 py-2.5 rounded-lg hover:bg-gray-700 transition-colors duration-150 flex items-center justify-between">
        <span>대시보드</span>
      </router-link>

      <router-link to="/project/list" class="menu-item whitespace-nowrap cursor-pointer px-3 py-2.5 rounded-lg hover:bg-gray-700 transition-colors duration-150 flex items-center justify-between">
        <span>프로젝트 목록</span>
      </router-link>

      <div v-if="selectedProject" class="mt-1">
        <div class="px-3 py-1.5 text-xs font-semibold text-gray-400 uppercase tracking-wider whitespace-nowrap">
          {{ selectedProject.title }}
        </div>
        <div class="mt-1 space-y-0.5">
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>개요</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>로드맵</span>
          </router-link>
          <router-link to="/tasks" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>일감 목록</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>간트차트</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>달력</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>공지사항</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>게시판</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>파일</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>문서</span>
          </router-link>
          <!-- <router-link
            :to="`/wiki/detail/${selectedProject.id}`"
            class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between"
          >
            <span>위키 조회</span>
          </router-link> -->
          <div @click="goWiki(selectedProject.id)" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>위키 조회</span>
          </div>
          <router-link
            :to="`/wiki/write/${selectedProject.id}`"
            class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between"
          >
            <span>위키 등록 - 조회와 합칠예정</span>
          </router-link>
          <router-link to="/spent" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>소요시간</span>
          </router-link>
          <router-link to="" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
            <span>로그</span>
          </router-link>
        </div>
      </div>

      <div v-show="showContent && isOpen">
        <template v-if="authStore.user?.role.includes('ROLE0001')">
          <div @click="toggleAdminMenu" class="menu-item whitespace-nowrap cursor-pointer px-3 py-2.5 rounded-lg hover:bg-gray-700 transition-colors duration-150 flex items-center justify-between">
            <span>관리</span>
          </div>

          <div v-if="isAdminMenuOpen" class="mt-1 space-y-0.5">
            <router-link to="/users/list" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
              <span>사용자 관리</span>
            </router-link>
            <router-link to="/roles/list" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
              <span>역할 관리</span>
            </router-link>
            <router-link to="/workflow/list" class="sub-menu-item whitespace-nowrap cursor-pointer px-4 py-2.5 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors duration-150 flex items-center justify-between">
              <span>업무 흐름</span>
            </router-link>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

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
