<script setup>
import GanttTaskDetailModal from '@/components/task/GanttTaskDetailModal.vue';
import Button from 'primevue/button';
import DatePicker from 'primevue/datepicker';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import { computed, ref } from 'vue';
// import { GGanttChart } from 'vue-ganttastic';
// import 'vue-ganttastic/dist/style.css';
import { useRouter } from 'vue-router';

const router = useRouter();

// ─── 샘플 데이터 ────────────────────────────────────────────
const tasks = ref([
  {
    taskId: 'T001',
    taskTitle: '요구사항 분석',
    projectId: 'P001',
    projectTitle: '비즈니스 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'U001',
    name: '김민준',
    plannedStart: '2025-04-01',
    plannedEnd: '2025-04-07',
    actualStart: '2025-04-01',
    actualEnd: '',
    priority: '상',
    workflow: '완료',
    type: '분석',
    cate: '기획',
    progress: 100,
    subProgress: 0,
    parentId: '',
    relation: [{ relatedTaskId: 'T002', relationType: '선행' }]
  },
  {
    taskId: 'T002',
    taskTitle: 'UI 설계',
    projectId: 'P001',
    projectTitle: '비즈니스 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'U002',
    name: '이서연',
    plannedStart: '2025-04-05',
    plannedEnd: '2025-04-14',
    actualStart: '2025-04-06',
    actualEnd: '',
    priority: '중',
    workflow: '실행',
    type: '설계',
    cate: 'UI/UX',
    progress: 60,
    subProgress: 0,
    parentId: '',
    relation: [
      { relatedTaskId: 'T001', relationType: '후행' },
      { relatedTaskId: 'T003', relationType: '관련됨' }
    ]
  },
  {
    taskId: 'T003',
    taskTitle: '백엔드 API 개발',
    projectId: 'P001',
    projectTitle: '비즈니스 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'U003',
    name: '박준혁',
    plannedStart: '2025-04-08',
    plannedEnd: '2025-04-22',
    actualStart: '2025-04-08',
    actualEnd: '',
    priority: '긴급',
    workflow: '실행',
    type: '개발',
    cate: '백엔드',
    progress: 40,
    subProgress: 55,
    parentId: '',
    relation: [{ relatedTaskId: 'T004', relationType: '막고있음' }]
  },
  {
    taskId: 'T004',
    taskTitle: 'API 연동 (하위)',
    projectId: 'P001',
    projectTitle: '비즈니스 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'U003',
    name: '박준혁',
    plannedStart: '2025-04-10',
    plannedEnd: '2025-04-18',
    actualStart: '',
    actualEnd: '',
    priority: '긴급',
    workflow: '신규',
    type: '개발',
    cate: '백엔드',
    progress: 0,
    subProgress: 0,
    parentId: 'T003',
    relation: [{ relatedTaskId: 'T003', relationType: '막혀있음' }]
  },
  {
    taskId: 'T005',
    taskTitle: '프론트엔드 개발',
    projectId: 'P001',
    projectTitle: '비즈니스 플랫폼',
    versionId: 'V002',
    versionName: 'v1.1',
    userId: 'U004',
    name: '최유진',
    plannedStart: '2025-04-12',
    plannedEnd: '2025-04-28',
    actualStart: '2025-04-13',
    actualEnd: '',
    priority: '하',
    workflow: '실행',
    type: '개발',
    cate: '프론트엔드',
    progress: 30,
    subProgress: 0,
    parentId: '',
    relation: []
  },
  {
    taskId: 'T006',
    taskTitle: 'QA 테스트',
    projectId: 'P002',
    projectTitle: '모바일 앱',
    versionId: 'V003',
    versionName: 'v2.0',
    userId: 'U001',
    name: '김민준',
    plannedStart: '2025-04-20',
    plannedEnd: '2025-04-30',
    actualStart: '',
    actualEnd: '',
    priority: '중',
    workflow: '신규',
    type: '테스트',
    cate: 'QA',
    progress: 0,
    subProgress: 0,
    parentId: '',
    relation: [{ relatedTaskId: 'T005', relationType: '관련됨' }]
  }
]);

// ─── 공통 ─────────────────────────────────────────────
function parseDate(str) {
  if (!str) return null;
  const [y, m, d] = str.split('-').map(Number);
  return new Date(y, m - 1, d);
}

function progressBarColor(priority) {
  return { 하: '#6aaa27', 중: '#1a7fd4', 상: '#e07b1a', 긴급: '#c0392b' }[priority] || '#5B6E96';
}

// ─── 필터 ─────────────────────────────────────────────
const filterForm = ref({ userId: null, projectTitle: '', versionName: '', taskTitle: '' });
const appliedFilter = ref({ userId: null, projectTitle: '', versionName: '', taskTitle: '' });

const filteredTasks = computed(() =>
  tasks.value.filter((t) => {
    const f = appliedFilter.value;
    if (f.userId && t.userId !== f.userId) return false;
    if (f.projectTitle && !t.projectTitle.includes(f.projectTitle)) return false;
    if (f.versionName && !t.versionName.includes(f.versionName)) return false;
    if (f.taskTitle && !t.taskTitle.includes(f.taskTitle)) return false;
    return true;
  })
);

const userOptions = computed(() => {
  const map = {};
  tasks.value.forEach((t) => {
    if (!map[t.userId]) map[t.userId] = { id: t.userId, name: t.name };
  });
  return Object.values(map);
});

const taskOptions = computed(() =>
  filteredTasks.value.map((t) => ({
    label: `${t.taskTitle} (${t.taskId})`,
    value: t.taskId
  }))
);

function applyFilter() {
  appliedFilter.value = { ...filterForm.value };
}

function resetFilter() {
  filterForm.value = { userId: null, projectTitle: '', versionName: '', taskTitle: '' };
  appliedFilter.value = { userId: null, projectTitle: '', versionName: '', taskTitle: '' };
}

// ─── 간트 데이터 변환 ─────────────────────────────────
const ganttRows = computed(() =>
  filteredTasks.value.map((task) => ({
    id: task.taskId,
    label: `${task.taskTitle} (${task.name})`,
    start: parseDate(task.plannedStart),
    end: parseDate(task.plannedEnd),
    style: {
      background: progressBarColor(task.priority)
    }
  }))
);

// ─── 이동 (간단화) ────────────────────────────────────
const gotoTaskId = ref(null);
const gotoDate = ref(null);

function onGotoTaskChange(val) {
  if (!val) return;
  const el = document.querySelector(`[data-id="${val}"]`);
  el?.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function onGotoDateChange(val) {
  if (!val) return;
  // vue-ganttastic는 내부 scroll API 없음 → 생략 또는 커스텀 필요
}

// ─── 모달 ────────────────────────────────────────────
const modalVisible = ref(false);
const selectedTask = ref(null);

function openModal(task) {
  selectedTask.value = task;
  modalVisible.value = true;
}
</script>

<template>
  <div class="min-h-screen bg-white px-6 py-5 font-sans">
    <!-- 페이지 제목 -->
    <h1 class="text-2xl font-extrabold text-gray-800 tracking-tight mb-4">간트 차트</h1>

    <!-- 필터 카드 -->
    <div class="rounded-xl p-4 mb-2" style="background: #f2f3f8">
      <p class="text-[11px] font-bold uppercase tracking-widest mb-2" style="color: #5b6e96">필터</p>
      <div class="grid grid-cols-4 gap-3">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-semibold" style="color: #5b6e96">담당자</label>
          <Select v-model="filterForm.userId" :options="userOptions" option-label="name" option-value="id" placeholder="이름 검색" filter filter-placeholder="이름 검색" show-clear class="w-full">
            <template #option="data">{{ data.option.name }} ({{ data.option.id }})</template>
          </Select>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs font-semibold" style="color: #5b6e96">프로젝트</label>
          <InputText v-model="filterForm.projectTitle" placeholder="프로젝트명" class="w-full" />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs font-semibold" style="color: #5b6e96">버전명</label>
          <InputText v-model="filterForm.versionName" placeholder="버전명" class="w-full" />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs font-semibold" style="color: #5b6e96">일감 제목</label>
          <InputText v-model="filterForm.taskTitle" placeholder="일감 제목" class="w-full" />
        </div>
      </div>
      <div class="flex justify-end gap-2 mt-3">
        <Button label="적용" raised @click="applyFilter" />
        <Button label="초기화" raised severity="secondary" @click="resetFilter" />
      </div>
    </div>

    <!-- 이동 카드 -->
    <div class="rounded-xl p-4 mb-3" style="background: #f2f3f8">
      <p class="text-[11px] font-bold uppercase tracking-widest mb-2" style="color: #5b6e96">이동</p>
      <div class="grid grid-cols-2 gap-3">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-semibold" style="color: #5b6e96">일감으로 이동</label>
          <Select v-model="gotoTaskId" :options="taskOptions" option-label="label" option-value="value" placeholder="일감 선택" filter filter-placeholder="일감 검색" show-clear class="w-full" @update:modelValue="onGotoTaskChange" />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs font-semibold" style="color: #5b6e96">날짜로 이동</label>
          <DatePicker v-model="gotoDate" placeholder="날짜 선택" date-format="yy-mm-dd" show-button-bar class="w-full" @update:modelValue="onGotoDateChange" />
        </div>
      </div>
    </div>

    <!-- 범례 -->
    <div class="flex flex-wrap items-center gap-5 bg-gray-50 border border-gray-200 rounded-lg px-4 py-2 mb-3 text-xs">
      <div class="flex items-center gap-1.5">
        <span class="font-bold text-[#5B6E96]">우선순위:</span>
        <span class="px-2.5 py-0.5 rounded-full font-semibold priority-low">하</span>
        <span class="px-2.5 py-0.5 rounded-full font-semibold priority-mid">중</span>
        <span class="px-2.5 py-0.5 rounded-full font-semibold priority-high">상</span>
        <span class="px-2.5 py-0.5 rounded-full font-semibold priority-urgent">긴급</span>
      </div>
      <div class="flex items-center gap-3 flex-wrap">
        <span class="font-bold text-[#5B6E96]">연결선:</span>
        <span class="flex items-center gap-1 text-gray-600">
          <svg width="40" height="14">
            <line x1="2" y1="7" x2="30" y2="7" stroke="#3b82f6" stroke-width="2" stroke-dasharray="5,3" />
            <polygon points="30,3 38,7 30,11" fill="#3b82f6" /></svg
          >관련됨
        </span>
        <span class="flex items-center gap-1 text-gray-600">
          <svg width="40" height="14">
            <line x1="2" y1="7" x2="30" y2="7" stroke="#ef4444" stroke-width="2" />
            <polygon points="30,3 38,7 30,11" fill="#ef4444" /></svg
          >막고있음/막혀있음
        </span>
        <span class="flex items-center gap-1 text-gray-600">
          <svg width="40" height="14">
            <line x1="2" y1="7" x2="30" y2="7" stroke="#f59e0b" stroke-width="2" stroke-dasharray="2,2" />
            <polygon points="30,3 38,7 30,11" fill="#f59e0b" /></svg
          >선행/후행
        </span>
        <span class="flex items-center gap-1 text-gray-600">
          <svg width="40" height="14">
            <line x1="2" y1="7" x2="30" y2="7" stroke="#8b5cf6" stroke-width="2" stroke-dasharray="6,2,1,2" />
            <polygon points="30,3 38,7 30,11" fill="#8b5cf6" /></svg
          >부모/자식
        </span>
      </div>
    </div>

    <!-- 간트 영역 -->
    <div class="border rounded-lg overflow-hidden">
      <!-- <GGanttChart
        :rows="ganttRows"
        :from="chartStartDate"
        :to="chartEndDate"
        :row-height="44"
        :bar-height="28"
        :grid="true"
        :highlight-today="true"
        :labels-width="300"
        class="w-full"
        @bar-click="(bar) => openModal(tasks.find((t) => t.taskId === bar.id))"
      /> -->
    </div>

    <!-- 모달 -->
    <GanttTaskDetailModal v-model="modalVisible" :task="selectedTask" :all-tasks="tasks" />
  </div>
</template>

<style scoped>
/* 우선순위 */
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

/* 테이블 헤더 공통 */
.left-th {
  background: #5b6e96;
  color: #dde3f0;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0 8px;
  height: 52px;
  text-align: left;
  white-space: nowrap;
  border-right: 1px solid rgba(255, 255, 255, 0.15);
}

/* 하이라이트 행 펄스 */
.highlighted-row .bar-click-area {
  outline: 3px solid #f59e0b;
  animation: highlightPulse 2s ease-in-out;
}

@keyframes highlightPulse {
  0%,
  100% {
    box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.5);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(245, 158, 11, 0);
  }
}

/* ProgressBar 내부 스타일 */
:deep(.p-progressbar) {
  background: #e2e8f0 !important;
  height: 100% !important;
  border-radius: 6px !important;
}
:deep(.p-progressbar-value) {
  border-radius: 6px !important;
  transition: width 0.3s ease;
}
:deep(.p-progressbar-label) {
  display: none !important;
}

/* 스크롤바 스타일 */
.gantt-right::-webkit-scrollbar {
  height: 6px;
  width: 5px;
}
.gantt-right::-webkit-scrollbar-thumb {
  background: #c5cfe8;
  border-radius: 3px;
}
.gantt-left::-webkit-scrollbar {
  width: 4px;
}
.gantt-left::-webkit-scrollbar-thumb {
  background: #c5cfe8;
  border-radius: 3px;
}
</style>
