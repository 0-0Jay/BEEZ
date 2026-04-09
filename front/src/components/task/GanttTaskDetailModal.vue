<template>
  <Dialog v-model:visible="visible" modal header="일감 상세 정보" :style="{ width: '680px', maxWidth: '95vw' }" :pt="{ content: { class: 'p-0' } }">
    <div v-if="task" class="max-h-[70vh] overflow-y-auto">
      <!-- 기본 정보 -->
      <section class="px-6 pt-4 pb-3 border-b border-gray-100">
        <p class="text-base font-bold text-[#5B6E96] uppercase tracking-widest mb-3">기본 정보</p>
        <div class="grid grid-cols-2 gap-x-6 gap-y-3">
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">일감 번호</span>
            <span class="text-base text-gray-700">{{ task.taskId }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">일감명</span>
            <span class="text-base font-semibold text-gray-800">{{ task.taskTitle }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">업무 유형</span>
            <span class="text-base text-gray-700">{{ task.type || '-' }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">업무 범주</span>
            <span class="text-base text-gray-700">{{ task.cate || '-' }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">업무흐름 상태</span>
            <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold bg-indigo-50 text-indigo-700 w-fit">
              {{ task.workflow }}
            </span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">우선순위</span>
            <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold w-fit" :class="priorityClass(task.priority)">
              {{ task.priority }}
            </span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">부모 일감 번호</span>
            <span class="text-base text-gray-700">{{ task.parentId || '없음' }}</span>
          </div>
        </div>
      </section>

      <!-- 프로젝트 / 버전 -->
      <section class="px-6 pt-4 pb-3 border-b border-gray-100">
        <p class="text-base font-bold text-[#5B6E96] uppercase tracking-widest mb-3">프로젝트 / 버전</p>
        <div class="grid grid-cols-2 gap-x-6 gap-y-3">
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">프로젝트 번호</span>
            <span class="text-base text-gray-700">{{ task.projectId }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">프로젝트명</span>
            <span class="text-base text-gray-700">{{ task.projectTitle }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">버전 번호</span>
            <span class="text-base text-gray-700">{{ task.versionId }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">버전명</span>
            <span class="text-base text-gray-700">{{ task.versionName }}</span>
          </div>
        </div>
      </section>

      <!-- 담당자 -->
      <section class="px-6 pt-4 pb-3 border-b border-gray-100">
        <p class="text-base font-bold text-[#5B6E96] uppercase tracking-widest mb-3">담당자</p>
        <div class="grid grid-cols-2 gap-x-6 gap-y-3">
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">담당자 번호</span>
            <span class="text-base text-gray-700">{{ task.userId }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">담당자 이름</span>
            <span class="text-base text-gray-700">{{ task.name }}</span>
          </div>
        </div>
      </section>

      <!-- 일정 -->
      <section class="px-6 pt-4 pb-3 border-b border-gray-100">
        <p class="text-base font-bold text-[#5B6E96] uppercase tracking-widest mb-3">일정</p>
        <div class="grid grid-cols-2 gap-x-6 gap-y-3">
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">시작계획일</span>
            <span class="text-base text-gray-700">{{ task.plannedStart || '-' }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">마감계획일</span>
            <span class="text-base text-gray-700">{{ task.plannedEnd || '-' }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">실제시작일</span>
            <span class="text-base text-gray-700">{{ task.actualStart || '-' }}</span>
          </div>
          <div class="flex flex-col gap-0.5">
            <span class="text-base text-gray-400 font-medium">실제종료일</span>
            <span class="text-base text-gray-700">{{ task.actualEnd || '-' }}</span>
          </div>
        </div>
      </section>

      <!-- 진척도 -->
      <section class="px-6 pt-4 pb-3 border-b border-gray-100">
        <p class="text-base font-bold text-[#5B6E96] uppercase tracking-widest mb-3">진척도</p>
        <div class="flex items-center gap-4">
          <span class="text-base text-gray-400 font-medium min-w-[130px]">
            {{ hasChildren ? '하위 진척도 (subProgress)' : '진척도 (progress)' }}
          </span>
          <div class="flex flex-1 items-center gap-3">
            <div class="flex-1">
              <ProgressBar :value="hasChildren ? task.subProgress : task.progress" :pt="{ value: { style: { background: progressBarColor(task.priority) } } }" />
            </div>
            <span class="text-base font-semibold text-[#5B6E96] min-w-[36px] text-right"> {{ hasChildren ? task.subProgress : task.progress }}% </span>
          </div>
        </div>
      </section>

      <!-- 연관 일감 -->
      <section class="px-6 pt-4 pb-5">
        <p class="text-base font-bold text-[#5B6E96] uppercase tracking-widest mb-3">연관 일감</p>
        <div v-if="task.relation && task.relation.length > 0" class="flex flex-col gap-2">
          <div v-for="(rel, idx) in task.relation" :key="idx" class="flex items-center gap-3 px-3 py-2 bg-gray-50 rounded-lg">
            <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold" :class="relationClass(rel.relationType)">
              {{ rel.relationType }}
            </span>
            <span class="text-base text-gray-700">{{ rel.relatedTaskId }}</span>
          </div>
        </div>
        <p v-else class="text-base text-gray-400 py-1">연관된 일감이 없습니다.</p>
      </section>
    </div>

    <template #footer>
      <div class="flex justify-end px-5 py-3 border-t border-gray-100">
        <Button label="닫기" raised severity="secondary" @click="visible = false" />
      </div>
    </template>
  </Dialog>
</template>

<script setup>
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import ProgressBar from 'primevue/progressbar';
import { computed } from 'vue';

const props = defineProps({
  modelValue: Boolean,
  task: Object,
  allTasks: { type: Array, default: () => [] }
});
const emit = defineEmits(['update:modelValue']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

const hasChildren = computed(() => {
  if (!props.task) return false;
  return props.allTasks.some((t) => t.parentId === props.task.taskId);
});

function priorityClass(priority) {
  return { 하: 'priority-low', 중: 'priority-mid', 상: 'priority-high', 긴급: 'priority-urgent' }[priority] || '';
}

function progressBarColor(priority) {
  return { 하: '#6aaa27', 중: '#1a7fd4', 상: '#e07b1a', 긴급: '#c0392b' }[priority] || '#5B6E96';
}

function relationClass(type) {
  if (type === '막고있음' || type === '막혀있음') return 'rel-block';
  if (type === '선행' || type === '후행') return 'rel-seq';
  return 'rel-related';
}
</script>

<style scoped>
.priority-low {
  background-color: #eaf3de;
  color: #3b6d11;
}
.priority-mid {
  background-color: #e6f1fb;
  color: #0c447c;
}
.priority-high {
  background-color: #faeeda;
  color: #633806;
}
.priority-urgent {
  background-color: #fcebeb;
  color: #791f1f;
}

.rel-related {
  background: #e6f1fb;
  color: #0c447c;
}
.rel-block {
  background: #fcebeb;
  color: #791f1f;
}
.rel-seq {
  background: #faeeda;
  color: #633806;
}

:deep(.p-progressbar) {
  height: 14px !important;
  border-radius: 7px !important;
  background: #eef1f8 !important;
}
:deep(.p-progressbar-value) {
  border-radius: 7px !important;
}
:deep(.p-progressbar-label) {
  display: none !important;
}
</style>
