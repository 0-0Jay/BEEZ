<script setup>
import { Gantt, ProjectModel } from '@bryntum/gantt';
import { onBeforeUnmount, onMounted, ref, watch } from 'vue';

const props = defineProps({
  tasks: { type: Array, default: () => [] }
});
const emit = defineEmits(['task-click']);

const ganttContainer = ref(null);
let ganttInstance = null;

const ALLOWED_FROM = new Set(['선행', '막고있음']);

const mapDependencies = (rawTasks) => {
  const deps = [];
  const ids = new Set(rawTasks.map((t) => t.taskId));
  const seen = new Set();
  rawTasks.forEach((t) => {
    (t.relation || []).forEach((rel) => {
      const { relationType, relatedTaskId } = rel;
      if (!ids.has(relatedTaskId)) return;
      if (ALLOWED_FROM.has(relationType)) {
        const key = `${t.taskId}->${relatedTaskId}`;
        if (!seen.has(key)) {
          seen.add(key);
          deps.push({ fromTask: t.taskId, toTask: relatedTaskId, type: 2 });
        }
      } else if (relationType === '관련됨') {
        const [from, to] = t.taskId < relatedTaskId ? [t.taskId, relatedTaskId] : [relatedTaskId, t.taskId];
        const key = `${from}->${to}`;
        if (!seen.has(key)) {
          seen.add(key);
          deps.push({ fromTask: from, toTask: to, type: 2 });
        }
      }
    });
  });
  return deps;
};

const mapTasksToTree = (rawTasks) => {
  const map = new Map();
  const roots = [];
  rawTasks.forEach((t) => {
    // 종료일 23:59:59 보정
    const endDate = t.plannedEnd
      ? (() => {
          const d = new Date(t.plannedEnd);
          d.setHours(23, 59, 59, 0);
          return d;
        })()
      : null;

    map.set(t.taskId, {
      id: t.taskId,
      name: t.taskTitle,
      startDate: t.plannedStart, // 00:00:00 그대로
      endDate, // 23:59:59 보정값
      percentDone: t.progress != null ? Number(t.progress) : 0,
      expanded: true,
      manuallyScheduled: true,
      taskData: t,
      children: []
    });
  });
  rawTasks.forEach((t) => {
    const node = map.get(t.taskId);
    if (t.parentId && map.has(t.parentId)) map.get(t.parentId).children.push(node);
    else roots.push(node);
  });
  const clean = (node) => {
    if (node.children.length === 0) delete node.children;
    else node.children.forEach(clean);
    return node;
  };
  return roots.map(clean);
};

function initGantt(rawTasks) {
  if (ganttInstance) {
    ganttInstance.destroy();
    ganttInstance = null;
  }

  const containerWidth = ganttContainer.value.offsetWidth || 1000;

  const ganttProject = new ProjectModel({
    tasks: mapTasksToTree(rawTasks),
    dependencies: mapDependencies(rawTasks),
    autoLoad: false,
    autoSetConstraints: false,
    skipNonWorkingTimeWhenSchedulingManually: false,
    autoCalculatePercentDoneForParentTasks: false
  });

  ganttInstance = new Gantt({
    appendTo: ganttContainer.value,
    project: ganttProject,
    width: containerWidth,
    height: '100%',

    viewPreset: {
      base: 'weekAndDay',
      headers: [
        { unit: 'month', dateFormat: 'YYYY년 MM월' },
        { unit: 'day', dateFormat: 'D', increment: 1 }
      ],
      tickWidth: 38,
      columnLinesFor: 1
    },

    rowHeight: 36,
    barMargin: 5,
    columnLines: false,

    subGridConfigs: {
      locked: { width: 610 },
      normal: { flex: 1 }
    },

    features: {
      dependencies: { radius: 3, clickWidth: 5, allowCreate: false },
      timeRanges: { showCurrentTimeLine: true },
      percentBar: { allowResize: false },
      cellEdit: false,
      taskEdit: false,
      taskDrag: true,
      taskResize: false,
      dependencyEdit: false,
      taskMenu: false,
      // ① 표/차트 구분선 드래그 비활성화
      regionResize: false,
      filter: true,
      tree: true
    },

    columns: [
      { type: 'name', text: '일감명', width: 220, field: 'name' },
      { type: 'startdate', text: '시작일', width: 110, format: 'YYYY-MM-DD' },
      { type: 'enddate', text: '마감일', width: 110, format: 'YYYY-MM-DD' },
      { type: 'percentdone', text: '진행률', width: 70, mode: 'circle' },
      {
        text: '상세보기',
        width: 90,
        htmlEncode: false,
        sortable: false,
        filterable: false,
        // ② 버튼에 data-task-id 심어서 클릭 판별을 확실하게
        renderer: ({ record }) => `<button class="gantt-detail-btn" data-task-id="${record.id}" style="padding:2px 10px;border-radius:6px;border:1px solid #6366f1;background:#eef2ff;color:#4f46e5;font-size:12px;cursor:pointer;">상세보기</button>`
      }
    ],

    listeners: {
      cellClick({ record, event }) {
        const btn = event.target.closest('.gantt-detail-btn');
        if (btn) {
          // 상세보기 버튼 클릭 → 모달 열기
          emit('task-click', record.id);
        } else {
          // ④ 일반 행 클릭 → 해당 일감 바로 스크롤
          ganttInstance.scrollTaskIntoView(record, { block: 'center', animate: true });
        }
      }
    }
  });

  // ③ 초기화 후 오늘 날짜가 화면 중앙에 오도록 스크롤
  ganttInstance.project.commitAsync().then(() => {
    ganttInstance?.scrollToDate(new Date(), { block: 'center', animate: false });
  });
}

// ResizeObserver
let resizeObserver = null;
function setupResizeObserver() {
  const wrapperEl = ganttContainer.value.parentElement;
  resizeObserver = new ResizeObserver(() => {
    if (!ganttInstance || !ganttContainer.value) return;
    ganttInstance.width = 0;
    requestAnimationFrame(() => {
      if (!ganttInstance || !ganttContainer.value) return;
      const newWidth = ganttContainer.value.parentElement.getBoundingClientRect().width;
      if (newWidth > 0) ganttInstance.width = newWidth;
    });
  });
  resizeObserver.observe(wrapperEl);
}

onMounted(() => {
  setupResizeObserver();
  if (props.tasks.length) initGantt(props.tasks);
});

watch(
  () => props.tasks,
  (newVal) => {
    if (newVal?.length) initGantt(newVal);
  }
);

onBeforeUnmount(() => {
  resizeObserver?.disconnect();
  ganttInstance?.destroy();
  ganttInstance = null;
});
</script>

<template>
  <div class="gantt-wrapper">
    <div ref="ganttContainer" class="gantt-container" />
  </div>
</template>

<style scoped>
.gantt-wrapper {
  height: calc(100vh - 244px);
  min-height: 400px;
  width: 100%;
  overflow: hidden;
}
.gantt-container {
  width: 100%;
  height: 100%;
}
</style>
