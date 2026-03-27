<script setup>
import { computed, ref } from 'vue';
import TaskList from './TaskList.vue';

const activeTab = ref(0);

const tabs = [
  { label: '전체 일감', value: 0 },
  { label: '나의 일감', value: 1 },
  { label: '관람 중인 일감', value: 2 }
];

const indicatorStyle = computed(() => ({
  transform: `translateX(${activeTab.value * 100}%)`,
  width: `${100 / tabs.length}%`
}));
</script>

<template>
  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
    <!-- Tab Header -->
    <div class="relative border-b border-gray-100">
      <!-- Tab Buttons -->
      <div class="flex">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          @click="activeTab = tab.value"
          :class="['relative z-10 flex-1 py-4 text-sm font-medium tracking-wide transition-colors duration-200 focus:outline-none', activeTab === tab.value ? 'text-gray-900' : 'text-gray-400 hover:text-gray-600']"
        >
          {{ tab.label }}

          <!-- Active dot indicator -->
          <span v-if="activeTab === tab.value" class="absolute bottom-0 left-1/2 -translate-x-1/2 translate-y-1/2 w-1.5 h-1.5 rounded-full bg-gray-900" />
        </button>
      </div>

      <!-- Sliding underline -->
      <div class="absolute bottom-0 left-0 h-0.5 bg-gray-900 transition-transform duration-300 ease-out" :style="indicatorStyle" />
    </div>

    <!-- Tab Content -->
    <div class="p-6">
      <!-- 전체 일감 -->
      <Transition name="fade" mode="out-in">
        <div v-if="activeTab === 0" key="0">
          <TaskList />
        </div>

        <!-- 나의 일감 -->
        <div v-else-if="activeTab === 1" key="1">
          <div class="flex items-center gap-2 mb-4">
            <div class="w-6 h-6 rounded-full bg-gray-900 flex items-center justify-center">
              <svg class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 10a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm0 2c-4.42 0-8 1.79-8 4v1h16v-1c0-2.21-3.58-4-8-4z" />
              </svg>
            </div>
            <span class="text-xs font-semibold text-gray-400 uppercase tracking-widest">나에게 배정된 일감</span>
          </div>
          <p class="text-sm text-gray-500 leading-relaxed">나에게 배정된 일감이 없습니다.</p>
        </div>

        <!-- 관람 중인 일감 -->
        <div v-else-if="activeTab === 2" key="2">
          <div class="flex items-center gap-2 mb-4">
            <div class="w-6 h-6 rounded-full bg-gray-900 flex items-center justify-center">
              <svg class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 3C5 3 1.73 7.11 1 10c.73 2.89 4 7 9 7s8.27-4.11 9-7c-.73-2.89-4-7-9-7zm0 12a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z" />
              </svg>
            </div>
            <span class="text-xs font-semibold text-gray-400 uppercase tracking-widest">구독 중인 일감</span>
          </div>
          <p class="text-sm text-gray-500 leading-relaxed">관람 중인 일감이 없습니다.</p>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.15s ease,
    transform 0.15s ease;
}
.fade-enter-from {
  opacity: 0;
  transform: translateY(4px);
}
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
