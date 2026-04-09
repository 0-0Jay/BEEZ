<template>
  <div class="min-h-screen bg-white px-6 py-6 font-sans">
    <!-- 페이지 제목 -->
    <h1 class="text-2xl font-bold text-gray-800 mb-6 tracking-tight">간트 차트</h1>

    <!-- 검색/필터 카드 -->
    <div class="rounded-xl p-5 mb-4 shadow-sm" style="background-color: #f2f3f8">
      <div class="flex flex-wrap items-end gap-4">
        <!-- 담당자 -->
        <div class="flex flex-col gap-1 min-w-[180px]">
          <label class="text-xs font-semibold text-gray-500">담당자</label>
          <Select v-model="filters.userId" :options="userOptions" option-label="name" option-value="id" placeholder="이름 검색" filter filter-placeholder="이름 검색" show-clear class="w-full">
            <template #option="data">{{ data.option.name }} ({{ data.option.id }})</template>
          </Select>
        </div>
        <!-- 프로젝트 -->
        <div class="flex flex-col gap-1 min-w-[160px]">
          <label class="text-xs font-semibold text-gray-500">프로젝트</label>
          <InputText v-model="filters.projectTitle" placeholder="프로젝트 검색" class="w-full" />
        </div>
        <!-- 버전 -->
        <div class="flex flex-col gap-1 min-w-[140px]">
          <label class="text-xs font-semibold text-gray-500">버전</label>
          <InputText v-model="filters.versionName" placeholder="버전 검색" class="w-full" />
        </div>
        <!-- 일감 제목 -->
        <div class="flex flex-col gap-1 min-w-[160px]">
          <label class="text-xs font-semibold text-gray-500">일감 제목</label>
          <InputText v-model="filters.taskTitle" placeholder="일감 제목 검색" class="w-full" />
        </div>
        <!-- 버튼 -->
        <div class="flex gap-2 items-end pb-0.5">
          <Button label="적용" raised @click="applyFilters" />
          <Button label="초기화" raised severity="secondary" @click="resetFilters" />
        </div>
      </div>
    </div>

    <!-- 이동 카드 -->
    <div class="rounded-xl p-4 mb-5 shadow-sm" style="background-color: #f2f3f8">
      <div class="flex flex-wrap items-end gap-4">
        <div class="flex flex-col gap-1 min-w-[220px]">
          <label class="text-xs font-semibold text-gray-500">일감으로 이동</label>
          <Select v-model="navigation.taskId" :options="taskNavigationOptions" option-label="label" option-value="id" placeholder="일감 선택" filter filter-placeholder="일감 검색" show-clear class="w-full" @change="onNavigationTaskChange">
            <template #option="data">{{ data.option.label }}</template>
          </Select>
        </div>
        <div class="flex flex-col gap-1 min-w-[180px]">
          <label class="text-xs font-semibold text-gray-500">날짜로 이동</label>
          <DatePicker v-model="navigation.date" placeholder="날짜 선택" date-format="yy-mm-dd" show-button-bar class="w-full" @date-select="onNavigationDateChange" />
        </div>
      </div>
    </div>

    <!-- 범례 -->
    <div class="flex flex-wrap items-center gap-6 mb-3 px-1">
      <!-- 우선순위 범례 -->
      <div class="flex items-center gap-2 flex-wrap">
        <span class="text-xs text-gray-500 font-semibold mr-1">우선순위:</span>
        <span v-for="p in priorityLegend" :key="p.label" :style="{ backgroundColor: p.bg, color: p.color, borderColor: p.color + '44' }" class="text-xs px-2.5 py-0.5 rounded-full font-medium border">{{ p.label }}</span>
      </div>
      <!-- 연결선 범례 -->
      <div class="flex items-center gap-3 flex-wrap">
        <span class="text-xs text-gray-500 font-semibold mr-1">관계:</span>
        <span class="flex items-center gap-1 text-xs text-gray-600">
          <svg width="28" height="10">
            <line x1="0" y1="5" x2="20" y2="5" stroke="#6366f1" stroke-width="2" stroke-dasharray="4,2" />
            <polygon points="20,2 28,5 20,8" fill="#6366f1" />
          </svg>
          부모-자식
        </span>
        <span class="flex items-center gap-1 text-xs text-gray-600">
          <svg width="28" height="10">
            <line x1="0" y1="5" x2="20" y2="5" stroke="#f59e42" stroke-width="2" />
            <polygon points="20,2 28,5 20,8" fill="#f59e42" />
          </svg>
          막고있음/막혀있음
        </span>
        <span class="flex items-center gap-1 text-xs text-gray-600">
          <svg width="28" height="10">
            <line x1="0" y1="5" x2="20" y2="5" stroke="#10b981" stroke-width="2" />
            <polygon points="20,2 28,5 20,8" fill="#10b981" />
          </svg>
          선행/후행
        </span>
        <span class="flex items-center gap-1 text-xs text-gray-600">
          <svg width="28" height="10">
            <line x1="0" y1="5" x2="20" y2="5" stroke="#94a3b8" stroke-width="1.5" stroke-dasharray="2,2" />
            <polygon points="20,2 28,5 20,8" fill="#94a3b8" />
          </svg>
          관련됨
        </span>
      </div>
    </div>

    <!-- 간트차트 컨테이너 -->
    <div class="rounded-xl overflow-hidden shadow border border-gray-200" ref="ganttWrapperRef">
      <!-- 헤더 (고정) -->
      <div class="flex" style="background-color: #5b6e96">
        <!-- 좌측 테이블 헤더 -->
        <div class="flex-shrink-0 flex border-r border-blue-900/30" :style="{ width: leftPanelWidth + 'px' }">
          <div v-for="col in tableColumns" :key="col.key" class="text-xs font-semibold py-2.5 px-2 border-r border-blue-900/30 last:border-r-0 flex items-center justify-center" :style="{ width: col.width + 'px', color: '#DDE3F0' }">
            {{ col.label }}
          </div>
        </div>
        <!-- 날짜 헤더 스크롤 영역 -->
        <div class="flex-1 overflow-hidden" ref="dateHeaderScrollRef">
          <div :style="{ width: totalChartWidth + 'px' }">
            <!-- 월 행 -->
            <div class="flex">
              <div v-for="month in monthHeaders" :key="month.key" class="text-xs font-bold py-1.5 text-center border-r border-blue-900/30 last:border-r-0" :style="{ width: month.days * DAY_WIDTH + 'px', color: '#DDE3F0' }">{{ month.label }}</div>
            </div>
            <!-- 일 행 -->
            <div class="flex">
              <div
                v-for="day in allDays"
                :key="day.dateStr"
                class="text-xs py-1 text-center border-r border-blue-900/30 last:border-r-0 relative"
                :style="{ width: DAY_WIDTH + 'px', color: day.isWeekend ? '#f87171' : '#DDE3F0', fontWeight: day.isToday ? '700' : '400' }"
              >
                {{ day.d }}
                <div v-if="day.isToday" class="absolute bottom-0 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full bg-red-400"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 바디 -->
      <div class="flex" ref="ganttBodyRef" :style="{ maxHeight: '520px' }">
        <!-- 좌측 테이블 바디 (세로 스크롤) -->
        <div class="flex-shrink-0 overflow-y-auto overflow-x-hidden border-r border-gray-200" :style="{ width: leftPanelWidth + 'px' }" ref="leftPanelRef" @wheel.prevent="onLeftPanelWheel">
          <div
            v-for="(task, idx) in filteredTasks"
            :key="task.taskId"
            class="flex border-b border-gray-100 hover:bg-blue-50/40 transition-colors"
            :class="{ 'bg-indigo-50/30': highlightedTaskId === task.taskId }"
            :style="{ height: ROW_HEIGHT + 'px' }"
          >
            <!-- 일감명 -->
            <div class="border-r border-gray-200 flex items-center px-2 overflow-hidden" :style="{ width: tableColumns[0].width + 'px' }">
              <span class="text-xs text-indigo-700 hover:text-indigo-500 cursor-pointer truncate font-medium hover:underline" :title="task.taskTitle" @click="goToTask(task.taskId)">{{ task.taskTitle }}</span>
            </div>
            <!-- 담당자 -->
            <div class="border-r border-gray-200 flex items-center px-2 overflow-hidden" :style="{ width: tableColumns[1].width + 'px' }">
              <div class="overflow-hidden">
                <div class="text-xs font-medium text-gray-700 truncate">{{ task.name }}</div>
                <div class="text-xs text-gray-400 truncate">{{ task.userId }}</div>
              </div>
            </div>
            <!-- 시작계획일 -->
            <div class="border-r border-gray-200 flex items-center px-2" :style="{ width: tableColumns[2].width + 'px' }">
              <span class="text-xs text-gray-600">{{ task.plannedStart || '-' }}</span>
            </div>
            <!-- 마감계획일 -->
            <div class="flex items-center px-2" :style="{ width: tableColumns[3].width + 'px' }">
              <span class="text-xs text-gray-600">{{ task.plannedEnd || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- 우측 차트 바디 (가로 스크롤) -->
        <div class="flex-1 overflow-x-auto overflow-y-auto" ref="chartScrollRef" @wheel.prevent="onChartWheel" @scroll="syncScroll">
          <div :style="{ width: totalChartWidth + 'px', minHeight: filteredTasks.length * ROW_HEIGHT + 'px', position: 'relative' }">
            <!-- 오늘 세로선 -->
            <div v-if="todayOffset >= 0" class="absolute top-0 bottom-0 z-20 pointer-events-none" :style="{ left: todayOffset + 'px', width: '2px', backgroundColor: '#ef4444', opacity: 0.7 }">
              <div class="absolute -top-1 left-1/2 -translate-x-1/2 w-2 h-2 rounded-full bg-red-500"></div>
            </div>

            <!-- 행들 -->
            <div
              v-for="(task, rowIdx) in filteredTasks"
              :key="task.taskId"
              class="absolute w-full border-b border-gray-100"
              :class="{ 'bg-indigo-50/20': highlightedTaskId === task.taskId }"
              :style="{ top: rowIdx * ROW_HEIGHT + 'px', height: ROW_HEIGHT + 'px' }"
            >
              <!-- 주말 배경 -->
              <div v-for="day in weekendDays" :key="day.dateStr" class="absolute top-0 bottom-0 bg-gray-50/80 pointer-events-none" :style="{ left: day.offset + 'px', width: DAY_WIDTH + 'px' }"></div>

              <!-- 계획 바 -->
              <div
                v-if="task.plannedStart && task.plannedEnd"
                class="absolute rounded-md cursor-pointer flex items-center overflow-hidden transition-transform hover:scale-y-105 hover:shadow-md z-10 group"
                :style="getBarStyle(task)"
                :title="`${task.taskTitle} | ${task.plannedStart} ~ ${task.plannedEnd}`"
                @click="openModal(task)"
              >
                <!-- 진척도 내부 바 -->
                <div class="absolute left-0 top-0 bottom-0 opacity-40 rounded-l-md" :style="{ width: getEffectiveProgress(task) + '%', backgroundColor: '#1e293b' }"></div>
                <span class="relative z-10 text-xs font-semibold px-2 truncate">{{ task.taskTitle }}</span>
                <span class="relative z-10 text-xs ml-auto pr-2 opacity-70">{{ getEffectiveProgress(task) }}%</span>
              </div>
            </div>

            <!-- SVG 연결선 오버레이 -->
            <svg class="absolute top-0 left-0 pointer-events-none z-30" :width="totalChartWidth" :height="filteredTasks.length * ROW_HEIGHT" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <marker id="arrow-parent" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
                  <polygon points="0 0, 6 3, 0 6" fill="#6366f1" />
                </marker>
                <marker id="arrow-block" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
                  <polygon points="0 0, 6 3, 0 6" fill="#f59e42" />
                </marker>
                <marker id="arrow-seq" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
                  <polygon points="0 0, 6 3, 0 6" fill="#10b981" />
                </marker>
                <marker id="arrow-related" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
                  <polygon points="0 0, 6 3, 0 6" fill="#94a3b8" />
                </marker>
              </defs>
              <g v-for="conn in connections" :key="conn.key">
                <path :d="conn.path" :stroke="conn.color" :stroke-width="conn.width" :stroke-dasharray="conn.dash" fill="none" :marker-end="'url(#' + conn.marker + ')'" />
              </g>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 상세정보 모달 -->
    <GanttTaskDetailModal v-model="showModal" :task="selectedTask" :all-tasks="allTasks" />
  </div>
</template>

<script setup>
import GanttTaskDetailModal from '@/components/task/GanttTaskDetailModal.vue';
import Button from 'primevue/button';
import DatePicker from 'primevue/datepicker';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// ─── 상수 ────────────────────────────────────────────────
const DAY_WIDTH = 32;
const ROW_HEIGHT = 40;

const tableColumns = [
  { key: 'taskTitle', label: '일감명', width: 160 },
  { key: 'assignee', label: '담당자', width: 110 },
  { key: 'plannedStart', label: '시작계획일', width: 90 },
  { key: 'plannedEnd', label: '마감계획일', width: 90 }
];
const leftPanelWidth = computed(() => tableColumns.reduce((s, c) => s + c.width, 0));

// ─── 샘플 데이터 ──────────────────────────────────────────
const allTasks = ref([
  {
    taskId: 'T001',
    taskTitle: '요구사항 분석',
    projectId: 'P001',
    projectTitle: '비즈 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'user01',
    name: '김민준',
    plannedStart: '2025-04-01',
    plannedEnd: '2025-04-07',
    actualStart: '2025-04-01',
    actualEnd: '2025-04-06',
    priority: '상',
    workflow: '완료',
    type: '기획',
    cate: '분석',
    progress: 100,
    subProgress: 0,
    parentId: null,
    relation: [{ relatedTaskId: 'T002', relationType: '선행' }]
  },
  {
    taskId: 'T002',
    taskTitle: '시스템 설계',
    projectId: 'P001',
    projectTitle: '비즈 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'user02',
    name: '이서연',
    plannedStart: '2025-04-08',
    plannedEnd: '2025-04-15',
    actualStart: '2025-04-08',
    actualEnd: null,
    priority: '긴급',
    workflow: '실행',
    type: '설계',
    cate: '아키텍처',
    progress: 60,
    subProgress: 0,
    parentId: null,
    relation: [
      { relatedTaskId: 'T001', relationType: '후행' },
      { relatedTaskId: 'T003', relationType: '막고있음' }
    ]
  },
  {
    taskId: 'T003',
    taskTitle: 'DB 스키마 설계',
    projectId: 'P001',
    projectTitle: '비즈 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'user03',
    name: '박지호',
    plannedStart: '2025-04-10',
    plannedEnd: '2025-04-20',
    actualStart: null,
    actualEnd: null,
    priority: '중',
    workflow: '신규',
    type: '설계',
    cate: 'DB',
    progress: 0,
    subProgress: 0,
    parentId: null,
    relation: [{ relatedTaskId: 'T002', relationType: '막혀있음' }]
  },
  {
    taskId: 'T004',
    taskTitle: '로그인 기능 개발',
    projectId: 'P001',
    projectTitle: '비즈 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'user01',
    name: '김민준',
    plannedStart: '2025-04-14',
    plannedEnd: '2025-04-25',
    actualStart: '2025-04-14',
    actualEnd: null,
    priority: '상',
    workflow: '실행',
    type: '개발',
    cate: '인증',
    progress: 40,
    subProgress: 55,
    parentId: null,
    relation: [{ relatedTaskId: 'T005', relationType: '관련됨' }]
  },
  {
    taskId: 'T005',
    taskTitle: '로그인 UI 구현',
    projectId: 'P001',
    projectTitle: '비즈 플랫폼',
    versionId: 'V002',
    versionName: 'v1.1',
    userId: 'user04',
    name: '최예진',
    plannedStart: '2025-04-16',
    plannedEnd: '2025-04-22',
    actualStart: '2025-04-16',
    actualEnd: null,
    priority: '하',
    workflow: '실행',
    type: '개발',
    cate: '프론트',
    progress: 70,
    subProgress: 0,
    parentId: 'T004',
    relation: [{ relatedTaskId: 'T004', relationType: '관련됨' }]
  },
  {
    taskId: 'T006',
    taskTitle: '로그인 API 개발',
    projectId: 'P001',
    projectTitle: '비즈 플랫폼',
    versionId: 'V001',
    versionName: 'v1.0',
    userId: 'user02',
    name: '이서연',
    plannedStart: '2025-04-14',
    plannedEnd: '2025-04-21',
    actualStart: '2025-04-14',
    actualEnd: null,
    priority: '긴급',
    workflow: '실행',
    type: '개발',
    cate: '백엔드',
    progress: 50,
    subProgress: 0,
    parentId: 'T004',
    relation: []
  },
  {
    taskId: 'T007',
    taskTitle: '테스트 계획 수립',
    projectId: 'P002',
    projectTitle: '모바일 앱',
    versionId: 'V003',
    versionName: 'v2.0',
    userId: 'user03',
    name: '박지호',
    plannedStart: '2025-04-20',
    plannedEnd: '2025-04-30',
    actualStart: null,
    actualEnd: null,
    priority: '중',
    workflow: '신규',
    type: 'QA',
    cate: '테스트',
    progress: 0,
    subProgress: 0,
    parentId: null,
    relation: [{ relatedTaskId: 'T008', relationType: '선행' }]
  },
  {
    taskId: 'T008',
    taskTitle: '통합 테스트',
    projectId: 'P002',
    projectTitle: '모바일 앱',
    versionId: 'V003',
    versionName: 'v2.0',
    userId: 'user04',
    name: '최예진',
    plannedStart: '2025-04-25',
    plannedEnd: '2025-05-05',
    actualStart: null,
    actualEnd: null,
    priority: '하',
    workflow: '신규',
    type: 'QA',
    cate: '테스트',
    progress: 0,
    subProgress: 0,
    parentId: null,
    relation: [{ relatedTaskId: 'T007', relationType: '후행' }]
  }
]);

// ─── 날짜 범위 계산 ───────────────────────────────────────
const chartStartDate = computed(() => {
  const dates = allTasks.value.flatMap((t) => [t.plannedStart, t.plannedEnd].filter(Boolean));
  if (!dates.length) return new Date();
  const min = new Date(Math.min(...dates.map((d) => new Date(d))));
  min.setDate(min.getDate() - 7);
  min.setDate(1); // 월 초로
  return min;
});

const chartEndDate = computed(() => {
  const dates = allTasks.value.flatMap((t) => [t.plannedStart, t.plannedEnd].filter(Boolean));
  if (!dates.length) {
    const d = new Date();
    d.setMonth(d.getMonth() + 2);
    return d;
  }
  const max = new Date(Math.max(...dates.map((d) => new Date(d))));
  max.setDate(max.getDate() + 14);
  return max;
});

const allDays = computed(() => {
  const days = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  let cur = new Date(chartStartDate.value);
  let offset = 0;
  while (cur <= chartEndDate.value) {
    const dow = cur.getDay();
    days.push({
      date: new Date(cur),
      dateStr: cur.toISOString().slice(0, 10),
      d: cur.getDate(),
      m: cur.getMonth() + 1,
      y: cur.getFullYear(),
      isWeekend: dow === 0 || dow === 6,
      isToday: cur.getTime() === today.getTime(),
      offset
    });
    offset += DAY_WIDTH;
    cur.setDate(cur.getDate() + 1);
  }
  return days;
});

const totalChartWidth = computed(() => allDays.value.length * DAY_WIDTH);

const monthHeaders = computed(() => {
  const map = new Map();
  allDays.value.forEach((day) => {
    const key = `${day.y}-${day.m}`;
    if (!map.has(key)) map.set(key, { key, label: `${day.y}년 ${day.m}월`, days: 0 });
    map.get(key).days++;
  });
  return [...map.values()];
});

const weekendDays = computed(() => allDays.value.filter((d) => d.isWeekend));

const todayOffset = computed(() => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const day = allDays.value.find((d) => d.date.getTime() === today.getTime());
  return day ? day.offset + DAY_WIDTH / 2 : -1;
});

// ─── 필터 ────────────────────────────────────────────────
const filters = ref({ userId: null, projectTitle: '', versionName: '', taskTitle: '' });
const appliedFilters = ref({ userId: null, projectTitle: '', versionName: '', taskTitle: '' });

const userOptions = computed(() => {
  const map = new Map();
  allTasks.value.forEach((t) => {
    if (!map.has(t.userId)) map.set(t.userId, { id: t.userId, name: t.name });
  });
  return [...map.values()];
});

const filteredTasks = computed(() => {
  const f = appliedFilters.value;
  return allTasks.value.filter((t) => {
    if (f.userId && t.userId !== f.userId) return false;
    if (f.projectTitle && !t.projectTitle.includes(f.projectTitle)) return false;
    if (f.versionName && !t.versionName?.includes(f.versionName)) return false;
    if (f.taskTitle && !t.taskTitle.includes(f.taskTitle)) return false;
    return true;
  });
});

const applyFilters = () => {
  appliedFilters.value = { ...filters.value };
};
const resetFilters = () => {
  filters.value = { userId: null, projectTitle: '', versionName: '', taskTitle: '' };
  appliedFilters.value = { userId: null, projectTitle: '', versionName: '', taskTitle: '' };
};

// ─── 이동 ────────────────────────────────────────────────
const navigation = ref({ taskId: null, date: null });
const highlightedTaskId = ref(null);

const taskNavigationOptions = computed(() => allTasks.value.map((t) => ({ id: t.taskId, label: `${t.taskTitle} (${t.taskId})` })));

const chartScrollRef = ref(null);
const leftPanelRef = ref(null);
const dateHeaderScrollRef = ref(null);

const scrollToDate = (dateStr) => {
  const day = allDays.value.find((d) => d.dateStr === dateStr);
  if (day && chartScrollRef.value) {
    chartScrollRef.value.scrollTo({ left: day.offset - 100, behavior: 'smooth' });
  }
};

const scrollToTask = (taskId) => {
  const idx = filteredTasks.value.findIndex((t) => t.taskId === taskId);
  if (idx < 0) return;
  const task = filteredTasks.value[idx];
  if (task.plannedStart) scrollToDate(task.plannedStart);
  if (leftPanelRef.value) {
    leftPanelRef.value.scrollTo({ top: idx * ROW_HEIGHT - 80, behavior: 'smooth' });
  }
  highlightedTaskId.value = taskId;
  setTimeout(() => {
    highlightedTaskId.value = null;
  }, 2000);
};

const onNavigationTaskChange = () => {
  navigation.value.date = null;
  if (navigation.value.taskId) scrollToTask(navigation.value.taskId);
};

const onNavigationDateChange = () => {
  navigation.value.taskId = null;
  if (navigation.value.date) {
    const d = new Date(navigation.value.date);
    const dateStr = d.toISOString().slice(0, 10);
    scrollToDate(dateStr);
  }
};

// ─── 스크롤 동기화 ────────────────────────────────────────
const isSyncing = ref(false);

const syncScroll = () => {
  if (isSyncing.value) return;
  isSyncing.value = true;
  const scrollLeft = chartScrollRef.value?.scrollLeft || 0;
  const scrollTop = chartScrollRef.value?.scrollTop || 0;
  if (dateHeaderScrollRef.value) dateHeaderScrollRef.value.scrollLeft = scrollLeft;
  if (leftPanelRef.value) leftPanelRef.value.scrollTop = scrollTop;
  isSyncing.value = false;
};

const onChartWheel = (e) => {
  if (!chartScrollRef.value) return;
  if (Math.abs(e.deltaX) > Math.abs(e.deltaY)) {
    chartScrollRef.value.scrollLeft += e.deltaX;
  } else {
    chartScrollRef.value.scrollLeft += e.deltaY;
  }
  syncScroll();
};

const onLeftPanelWheel = (e) => {
  if (!leftPanelRef.value) return;
  leftPanelRef.value.scrollTop += e.deltaY;
  if (chartScrollRef.value) chartScrollRef.value.scrollTop = leftPanelRef.value.scrollTop;
};

// ─── 바 스타일 ────────────────────────────────────────────
const priorityColors = {
  하: { bg: '#eaf3de', text: '#3b6d11', border: '#3b6d11' },
  중: { bg: '#e6f1fb', text: '#0c447c', border: '#0c447c' },
  상: { bg: '#faeeda', text: '#633806', border: '#633806' },
  긴급: { bg: '#fcebeb', text: '#791f1f', border: '#791f1f' }
};

const priorityLegend = [
  { label: '하', bg: '#eaf3de', color: '#3b6d11' },
  { label: '중', bg: '#e6f1fb', color: '#0c447c' },
  { label: '상', bg: '#faeeda', color: '#633806' },
  { label: '긴급', bg: '#fcebeb', color: '#791f1f' }
];

const dateToOffset = (dateStr) => {
  const day = allDays.value.find((d) => d.dateStr === dateStr);
  return day ? day.offset : -1;
};

const getBarStyle = (task) => {
  const startOffset = dateToOffset(task.plannedStart);
  const endOffset = dateToOffset(task.plannedEnd);
  if (startOffset < 0 || endOffset < 0) return { display: 'none' };
  const width = Math.max(endOffset - startOffset + DAY_WIDTH, DAY_WIDTH);
  const c = priorityColors[task.priority] || { bg: '#f1f5f9', text: '#475569', border: '#94a3b8' };
  return {
    left: startOffset + 'px',
    top: '6px',
    height: ROW_HEIGHT - 12 + 'px',
    width: width + 'px',
    backgroundColor: c.bg,
    color: c.text,
    border: `1.5px solid ${c.border}44`,
    borderLeft: `3px solid ${c.border}`
  };
};

const getEffectiveProgress = (task) => {
  const hasChildren = allTasks.value.some((t) => t.parentId === task.taskId);
  return hasChildren ? task.subProgress : task.progress;
};

// ─── 연결선 ───────────────────────────────────────────────
const connections = computed(() => {
  const lines = [];
  const taskIndexMap = new Map(filteredTasks.value.map((t, i) => [t.taskId, i]));

  filteredTasks.value.forEach((task) => {
    const fromIdx = taskIndexMap.get(task.taskId);
    if (fromIdx === undefined) return;

    // 부모-자식 연결
    if (task.parentId) {
      const parentIdx = taskIndexMap.get(task.parentId);
      if (parentIdx !== undefined) {
        const parentTask = filteredTasks.value[parentIdx];
        const fromOffset = dateToOffset(task.plannedStart);
        const toOffset = dateToOffset(parentTask.plannedStart);
        if (fromOffset >= 0 && toOffset >= 0) {
          const x1 = fromOffset + 4;
          const y1 = fromIdx * ROW_HEIGHT + ROW_HEIGHT / 2;
          const x2 = toOffset + DAY_WIDTH - 4;
          const y2 = parentIdx * ROW_HEIGHT + ROW_HEIGHT / 2;
          lines.push({
            key: `parent-${task.taskId}`,
            path: `M ${x1} ${y1} C ${x1 - 20} ${y1}, ${x2 + 20} ${y2}, ${x2} ${y2}`,
            color: '#6366f1',
            width: 1.5,
            dash: '5,3',
            marker: 'arrow-parent'
          });
        }
      }
    }

    // 연관 관계
    task.relation?.forEach((rel) => {
      const toIdx = taskIndexMap.get(rel.relatedTaskId);
      if (toIdx === undefined) return;
      const toTask = filteredTasks.value[toIdx];
      const fromOff = dateToOffset(task.plannedEnd);
      const toOff = dateToOffset(toTask.plannedStart);
      if (fromOff < 0 || toOff < 0) return;
      const x1 = fromOff + DAY_WIDTH;
      const y1 = fromIdx * ROW_HEIGHT + ROW_HEIGHT / 2;
      const x2 = toOff;
      const y2 = toIdx * ROW_HEIGHT + ROW_HEIGHT / 2;

      let color = '#94a3b8',
        dash = '3,3',
        marker = 'arrow-related',
        width = 1.2;
      if (rel.relationType?.includes('막고') || rel.relationType?.includes('막혀')) {
        color = '#f59e42';
        dash = 'none';
        marker = 'arrow-block';
        width = 1.5;
      } else if (rel.relationType?.includes('선행') || rel.relationType?.includes('후행')) {
        color = '#10b981';
        dash = 'none';
        marker = 'arrow-seq';
        width = 1.5;
      }

      lines.push({
        key: `rel-${task.taskId}-${rel.relatedTaskId}`,
        path: `M ${x1} ${y1} C ${x1 + 20} ${y1}, ${x2 - 20} ${y2}, ${x2} ${y2}`,
        color,
        dash,
        marker,
        width
      });
    });
  });

  return lines;
});

// ─── 모달 ────────────────────────────────────────────────
const showModal = ref(false);
const selectedTask = ref(null);

const openModal = (task) => {
  selectedTask.value = task;
  showModal.value = true;
};

// ─── 라우터 ───────────────────────────────────────────────
const goToTask = (taskId) => {
  router.push(`/task/${taskId}`);
};
</script>

<style scoped>
/* 스크롤바 스타일 */
::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}
::-webkit-scrollbar-track {
  background: #f1f5f9;
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 하이라이트 애니메이션 */
@keyframes highlight-pulse {
  0%,
  100% {
    background-color: transparent;
  }
  50% {
    background-color: rgba(99, 102, 241, 0.12);
  }
}
</style>
