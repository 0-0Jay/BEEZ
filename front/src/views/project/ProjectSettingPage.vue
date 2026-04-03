<script setup>
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const projectId = route.params.id;

const tabs = [
  { label: '프로젝트', icon: 'pi pi-folder', name: 'ProjectSettingInfo' },
  { label: '구성원', icon: 'pi pi-users', name: 'ProjectSettingMembers' },
  { label: '버전', icon: 'pi pi-tag', name: 'ProjectSettingVersions' }
];

const goToTab = (tabName) => {
  router.push({ name: tabName, params: { id: projectId } });
};

const isActive = (tabName) => {
  return route.name === tabName;
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 설정</h1>
    </div>

    <!-- 탭 버튼 -->
    <div class="flex gap-1 mb-6 border-b border-[#C7C7C2]">
      <button v-for="tab in tabs" :key="tab.name" class="tab-btn px-6 py-3 text-sm font-medium transition-all" :class="isActive(tab.name) ? 'tab-active' : 'tab-inactive'" @click="goToTab(tab.name)">
        <i :class="tab.icon" class="f mr-2" />
        {{ tab.label }}
      </button>
    </div>

    <!-- 탭 컨텐츠 -->
    <RouterView />
  </div>
</template>

<style scoped>
.tab-btn {
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  border-radius: 0;
  background: none;
  cursor: pointer;
}
.tab-active {
  color: #e8920e;
  border-bottom-color: #e8920e;
  font-weight: 600;
}
.tab-inactive {
  color: #6b6b63;
}
.tab-inactive:hover {
  color: #1a1816;
  background-color: #f2f0eb;
}
</style>
