<script setup>
import GanttChartComponent from '@/components/task/GanttChartComponent.vue';
import GanttTaskDetailModal from '@/components/task/GanttTaskDetailModal.vue';
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import { computed, onMounted, ref } from 'vue';

const projectStore = useProjectStore();
const taskStore = useTaskStore();

const project = computed(() => projectStore.selectedProject);
const allTasks = computed(() => taskStore.ganttData);

const userOptions = computed(() => {
  const map = new Map();
  allTasks.value.forEach((t) => {
    if (!map.has(t.userId)) map.set(t.userId, { id: t.userId, name: t.name });
  });
  return [...map.values()];
});

const projectOptions = computed(() => {
  const set = new Set();
  allTasks.value.forEach((t) => {
    if (t.projectTitle) set.add(t.projectTitle);
  });
  return [...set].map((v) => ({ label: v, value: v }));
});

const versionOptions = computed(() => {
  const set = new Set();
  allTasks.value.forEach((t) => {
    if (t.versionName) set.add(t.versionName);
  });
  return [...set].map((v) => ({ label: v, value: v }));
});

const filters = ref({ userId: null, projectTitle: null, versionName: null, taskTitle: null });
const appliedFilters = ref({ userId: null, projectTitle: null, versionName: null, taskTitle: null });

const filteredTasks = computed(() => {
  const f = appliedFilters.value;
  return allTasks.value.filter((t) => {
    if (f.userId && t.userId !== f.userId) return false;
    if (f.projectTitle && t.projectTitle !== f.projectTitle) return false;
    if (f.versionName && t.versionName !== f.versionName) return false;
    if (f.taskTitle && !t.taskTitle?.toLowerCase().includes(f.taskTitle.toLowerCase())) return false;
    return true;
  });
});

const applyFilters = () => {
  appliedFilters.value = { ...filters.value };
};
const resetFilters = () => {
  filters.value = { userId: null, projectTitle: null, versionName: null, taskTitle: null };
  appliedFilters.value = { ...filters.value };
};

const modalVisible = ref(false);
const selectedTaskId = ref(null);

const selectedTask = computed(() => allTasks.value.find((t) => t.taskId === selectedTaskId.value) ?? null);

function onTaskClick(taskId) {
  selectedTaskId.value = taskId;
  modalVisible.value = true;
}

onMounted(async () => {
  await taskStore.findGanttData(project.value?.id);
});
</script>

<template>
  <div class="h-full flex flex-col bg-white px-6 py-6 font-sans overflow-hidden">
    <h1 class="text-2xl font-bold text-gray-800 mb-6 tracking-tight shrink-0">간트 차트</h1>

    <!-- 필터 카드 -->
    <div class="rounded-xl p-5 mb-5 shadow-sm shrink-0" style="background-color: #f2f3f8">
      <div class="flex flex-wrap items-end gap-4">
        <div class="flex flex-col gap-1 min-w-[180px]">
          <label class="text-xs font-semibold text-gray-500">담당자</label>
          <Select v-model="filters.userId" :options="userOptions" option-label="name" option-value="id" placeholder="전체" filter filter-placeholder="이름 검색" show-clear class="w-full">
            <template #option="data">{{ data.option.name }} ({{ data.option.id }})</template>
          </Select>
        </div>

        <div class="flex flex-col gap-1 min-w-[180px]">
          <label class="text-xs font-semibold text-gray-500">프로젝트</label>
          <Select v-model="filters.projectTitle" :options="projectOptions" option-label="label" option-value="value" placeholder="전체" filter filter-placeholder="프로젝트 검색" show-clear class="w-full" />
        </div>

        <div class="flex flex-col gap-1 min-w-[160px]">
          <label class="text-xs font-semibold text-gray-500">버전</label>
          <Select v-model="filters.versionName" :options="versionOptions" option-label="label" option-value="value" placeholder="전체" filter filter-placeholder="버전 검색" show-clear class="w-full" />
        </div>

        <div class="flex flex-col gap-1 min-w-[200px]">
          <label class="text-xs font-semibold text-gray-500">일감 제목</label>
          <InputText v-model="filters.taskTitle" placeholder="제목 검색" class="w-full" />
        </div>

        <div class="flex gap-2 ml-auto items-end pb-0.5">
          <Button label="적용" raised @click="applyFilters" />
          <Button label="초기화" raised severity="secondary" @click="resetFilters" />
        </div>
      </div>
    </div>

    <!-- 간트: 남은 공간 전부 차지 -->
    <GanttChartComponent :tasks="filteredTasks" @task-click="onTaskClick" />

    <GanttTaskDetailModal v-model="modalVisible" :task="selectedTask" :all-tasks="allTasks" />
  </div>
</template>
