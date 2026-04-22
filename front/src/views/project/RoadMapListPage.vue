<script setup>
import { useProjectStore } from '@/stores/project';
import { useVersionStore } from '@/stores/version';
import { storeToRefs } from 'pinia';
import { computed, onMounted, reactive, watch } from 'vue';
import { useRouter } from 'vue-router';

const projectStore = useProjectStore();
const versionStore = useVersionStore();
const { loading, roadmapList, taskTypes } = storeToRefs(projectStore);
const { commonCodeList } = storeToRefs(versionStore);
const router = useRouter();

// ─── 초기 데이터 로드 ───────────────────────────────────────
onMounted(async () => {
  await versionStore.findCommonCodeList();
  await projectStore.fetchTaskTypes();
  await projectStore.fetchRoadmaps(filters);
});

const fetchRoadmaps = async () => {
  await projectStore.fetchRoadmaps(filters);
};

// ─── 필터 ───────────────────────────────────────────────────
const filters = reactive({
  versionId: '',
  versionStatus: '',
  includeSubProject: true
});

watch(
  () => filters.includeSubProject,
  () => {
    fetchRoadmaps();
  }
);

const versionStatusOptions = computed(() => commonCodeList.value.filter((c) => c.cgroup === '0N'));

const resetFilters = () => {
  Object.assign(filters, { versionId: '', versionStatus: '', includeSubProject: true });
  fetchRoadmaps();
};

// ─── 그룹핑 (버전별) ─────────────────────────────────────────
const groupedRoadmap = computed(() => {
  const grouped = {};
  roadmapList.value.forEach((row) => {
    if (!grouped[row.versionId]) {
      grouped[row.versionId] = {
        versionId: row.versionId,
        versionName: row.versionName,
        versionStatus: row.versionStatus,
        endDate: row.endDate,
        description: row.description,
        projectId: row.projectId,
        projectName: row.projectName,
        tasks: []
      };
    }
    if (row.taskId) {
      grouped[row.versionId].tasks.push({
        taskId: row.taskId,
        taskTitle: row.taskTitle,
        taskType: row.taskType,
        taskTypeName: row.taskTypeName,
        taskWorkflow: row.taskWorkflow,
        taskPriority: row.taskPriority,
        taskCreator: row.taskCreator,
        taskUserId: row.taskUserId,
        taskUserName: row.taskUserName,
        taskCategory: row.taskCategory,
        taskCategoryName: row.taskCategoryName,
        progress: row.progress,
        estimatedTime: row.estimatedTime,
        spent: row.totalSpent,
        projectName: row.projectName
      });
    }
  });
  return Object.values(grouped);
});

// 버전 드롭다운 옵션
const versionOptions = computed(() => groupedRoadmap.value.map((v) => ({ label: v.versionName, value: v.versionId })));

// ─── 버전별 선택 그룹 키 ─────────────────────────────────────
// { [versionId]: { groupBy: 'type'|'workflow'|'priority'|'creator'|'assignee'|'category', selectedKey: string|null } }
const versionGroupState = reactive({});

const getGroupState = (versionId) => {
  if (!versionGroupState[versionId]) {
    versionGroupState[versionId] = { groupBy: 'type', selectedKey: null };
  }
  return versionGroupState[versionId];
};

const GROUP_OPTIONS = [
  { value: 'type', label: '일감 유형' },
  { value: 'workflow', label: '상태' },
  { value: 'priority', label: '우선순위' },
  { value: 'creator', label: '작성자' },
  { value: 'assignee', label: '담당자' },
  { value: 'category', label: '일감 범주' }
];

const getTaskKey = (task, groupBy) => {
  switch (groupBy) {
    case 'type':
      return task.taskTypeName || task.taskType || '미분류';
    case 'workflow':
      return workflowLabel(task.taskWorkflow);
    case 'priority':
      return task.taskPriority || '미설정';
    case 'creator':
      return task.taskCreator || '미설정';
    case 'assignee':
      return task.taskUserName || '미배정';
    case 'category':
      return task.taskCategoryName || task.taskCategory || '미분류';
    default:
      return '기타';
  }
};

// 그룹별 현황 계산
const calcGroupStats = (tasks, groupBy) => {
  const map = {};
  tasks.forEach((t) => {
    const key = getTaskKey(t, groupBy);
    if (!map[key]) map[key] = { total: 0, done: 0, tasks: [] };
    map[key].total++;
    if (t.taskWorkflow === 'Q3') map[key].done++;
    map[key].tasks.push(t);
  });
  return map;
};

const selectGroupKey = (versionId, key) => {
  const state = getGroupState(versionId);
  state.selectedKey = state.selectedKey === key ? null : key;
};

const onGroupByChange = (versionId) => {
  getGroupState(versionId).selectedKey = null;
};

// 현재 필터링된 일감 목록
const filteredTasks = (versionId, tasks) => {
  const state = getGroupState(versionId);
  if (!state.selectedKey) return tasks;
  const groups = calcGroupStats(tasks, state.groupBy);
  return groups[state.selectedKey]?.tasks || [];
};

// ─── 작업시간 계산 ───────────────────────────────────────────
const calcTimeStats = (tasks) => {
  const estimated = tasks.reduce((sum, t) => sum + (Number(t.estimatedTime) || 0), 0);
  const spent = tasks.reduce((sum, t) => sum + (Number(t.spent) || 0), 0);
  const pct = estimated > 0 ? Math.round((spent / estimated) * 100) : 0;
  return { estimated, spent, pct };
};

function formatHours(hrs) {
  if (!hrs && hrs !== 0) return '-';
  const d = Math.floor(hrs / 8);
  const h = hrs % 8;
  const parts = [];
  if (d > 0) parts.push(`${d}일`);
  if (h > 0) parts.push(`${h}시간`);
  return parts.length > 0 ? parts.join(' ') : '0시간';
}

// ─── 전체 진척률 ─────────────────────────────────────────────
const calcProgress = (tasks) => {
  if (!tasks || tasks.length === 0) return 0;
  const done = tasks.filter((t) => t.taskWorkflow === 'Q3').length;
  return Math.round((done / tasks.length) * 100);
};

function progressBarColor(p) {
  if (p === 100) return '#22C55E';
  if (p >= 90) return '#86EFAC';
  if (p >= 60) return '#EAB308';
  if (p >= 30) return '#F97316';
  return '#EF4444';
}

// ─── 날짜 유틸 ───────────────────────────────────────────────
const calcDelay = (endDate) => {
  if (!endDate) return null;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const end = new Date(endDate);
  end.setHours(0, 0, 0, 0);
  const diff = Math.floor((today - end) / (1000 * 60 * 60 * 24));
  if (diff > 0) return { type: 'delay', days: diff };
  if (diff < 0) return { type: 'remain', days: Math.abs(diff) };
  return { type: 'today', days: 0 };
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}/${String(d.getDate()).padStart(2, '0')}`;
};

// ─── 워크플로우 ──────────────────────────────────────────────
const workflowLabel = (workflow) => {
  const map = { Q0: '신규', Q1: '실행', Q2: '해결', Q3: '완료', Q4: '반려' };
  return map[workflow] || workflow;
};

const workflowSeverity = (workflow) => {
  const map = { Q0: 'secondary', Q1: 'warn', Q2: 'info', Q3: 'success', Q4: 'danger' };
  return map[workflow] || 'secondary';
};
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-6">로드맵</h1>

    <!-- 필터 -->
    <div class="bg-[#F2F3F8] px-6 py-4 rounded-lg mb-6 shadow-sm border border-[#ECEEF4] flex items-center flex-wrap gap-4">
      <div class="flex items-center gap-3 mr-3">
        <label class="filter-label">버전</label>
        <Select v-model="filters.versionId" :options="versionOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-36" showClear />
      </div>
      <div class="flex items-center gap-3 mr-10">
        <label class="filter-label">버전 상태</label>
        <Select v-model="filters.versionStatus" :options="versionStatusOptions" optionLabel="name" optionValue="id" placeholder="선택" class="filter-input w-36" showClear />
      </div>
      <div class="flex items-center gap-2">
        <Checkbox v-model="filters.includeSubProject" :binary="true" inputId="subProject" />
        <label for="subProject" class="filter-label cursor-pointer">하위 프로젝트 일감 포함</label>
      </div>
      <div class="ml-auto flex gap-3">
        <Button label="초기화" severity="secondary" raised @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" raised @click="fetchRoadmaps" />
      </div>
    </div>

    <!-- 로딩 -->
    <div v-if="loading" class="flex justify-center py-20">
      <i class="pi pi-spin pi-spinner text-3xl text-[#5B6E96]"></i>
    </div>

    <!-- 로드맵 목록 -->
    <div v-else class="flex flex-col gap-4">
      <div v-if="groupedRoadmap.length === 0" class="flex flex-col items-center justify-center py-10">
        <i class="pi pi-search text-4xl text-[#C7C7C2] mb-3"></i>
        <p class="text-[#6B6B63]">조회된 데이터가 없습니다.</p>
      </div>

      <div v-else class="flex flex-col gap-4 mb-10">
        <div v-for="version in groupedRoadmap" :key="version.versionId" class="bg-white rounded-xl border border-[#ECEEF4] shadow-sm overflow-hidden">
          <!-- 버전 헤더 -->
          <div class="bg-[#5B6E96] px-5 py-3 flex items-center gap-3 border-b border-[#4a5d82]">
            <span class="text-lg font-medium text-white"> {{ version.projectName }} {{ version.versionName }}</span>
            <Tag :value="version.versionStatus === 'N1' ? '진행' : '닫힘'" :severity="version.versionStatus === 'N1' ? 'success' : 'secondary'" />
          </div>

          <!-- 버전 바디 -->
          <div class="px-5 py-4">
            <!-- 날짜 / 지연 -->
            <template v-if="calcDelay(version.endDate)">
              <div class="text-lg mb-1">
                <span
                  :class="{
                    'font-bold text-[#D85A30]': calcDelay(version.endDate).type === 'delay',
                    'font-bold text-[#0f6e56]': calcDelay(version.endDate).type === 'remain',
                    'font-bold text-[#185fa5]': calcDelay(version.endDate).type === 'today'
                  }"
                >
                  <template v-if="calcDelay(version.endDate).type === 'delay'">{{ calcDelay(version.endDate).days }}일 지연</template>
                  <template v-else-if="calcDelay(version.endDate).type === 'remain'">{{ calcDelay(version.endDate).days }}일 남음</template>
                  <template v-else>오늘 마감</template>
                </span>
                <span v-if="version.endDate" class="text-[#6B6B63]"> ({{ formatDate(version.endDate) }})</span>
              </div>
            </template>

            <!-- 버전 설명 -->
            <div v-if="version.description" class="text text-[#6B6B63] mb-4 mt-2">{{ version.description }}</div>

            <!-- 전체 진척률 -->
            <div class="flex items-center gap-3 mb-1">
              <div class="flex-1 h-5 bg-[#ECEEF4] rounded-lg overflow-hidden">
                <ProgressBar
                  :style="{
                    width: calcProgress(version.tasks) + '%',
                    background: progressBarColor(calcProgress(version.tasks))
                  }"
                />
              </div>
              <span class="text-sm text-[#6B6B63] min-w-[36px] text-right">{{ calcProgress(version.tasks) }}%</span>
            </div>
            <div class="text-xs text-[#9A9B90] mb-4">{{ version.tasks.length }} 일감 ({{ version.tasks.filter((t) => t.taskWorkflow === 'Q3').length }}건 완료 / {{ version.tasks.filter((t) => t.taskWorkflow !== 'Q3').length }}건 진행중)</div>

            <!-- ── 작업시간 관리 ── -->
            <template v-if="version.tasks.length > 0">
              <div class="mb-1 text font-semibold text-[#1A1816]">작업 시간 관리</div>
              <div class="grid grid-cols-2 gap-3 mb-2">
                <div class="bg-[#F2F3F8] rounded-lg py-3 text-center">
                  <div class="text-m text-[#6B6B63] mb-1">추정시간</div>
                  <div class="text-2xl font-semibold text-[#185fa5]">
                    {{ formatHours(calcTimeStats(version.tasks).estimated) }}
                  </div>
                </div>
                <div class="bg-[#F2F3F8] rounded-lg py-3 text-center">
                  <div class="text-m text-[#6B6B63] mb-1">소요시간</div>
                  <div class="text-2xl font-semibold text-[#185fa5]">
                    {{ formatHours(calcTimeStats(version.tasks).spent) }}
                  </div>
                </div>
              </div>
              <!-- <div class="h-1.5 bg-[#ECEEF4] rounded-full overflow-hidden mb-1">
                <div class="h-full rounded-full bg-[#5B6E96]" :style="{ width: calcTimeStats(version.tasks).pct + '%' }" />
              </div> -->
              <!-- <div class="text-xs text-[#9A9B90] mb-4">추정 대비 {{ calcTimeStats(version.tasks).pct }}% 소요</div> -->
            </template>

            <!-- 일감 없음 -->
            <div v-if="version.tasks.length === 0" class="text-sm text-[#9A9B90] py-2">이 버전에 해당하는 일감 없음</div>

            <template v-else>
              <div class="border-t border-[#ECEEF4] my-3" />

              <!-- ── 일감 현황 (그룹 드롭다운) ── -->
              <div class="bg-[#F2F3F8] rounded-lg p-4 mb-4">
                <div class="flex items-center gap-2 mb-3">
                  <Select v-model="getGroupState(version.versionId).groupBy" :options="GROUP_OPTIONS" optionLabel="label" optionValue="value" class="filter-input w-32" @change="onGroupByChange(version.versionId)" />
                  <span class="text-sm font-medium text-[#1A1816]">별 일감 현황</span>
                </div>

                <!-- 그룹별 현황 바 -->
                <div
                  v-for="(stat, key) in calcGroupStats(version.tasks, getGroupState(version.versionId).groupBy)"
                  :key="key"
                  class="flex items-center gap-2 mb-2 cursor-pointer px-2 py-1 rounded-md transition-all"
                  :class="getGroupState(version.versionId).selectedKey === key ? 'bg-white outline outline-[1.5px] outline-[#5B6E96]' : 'hover:bg-white'"
                  @click="selectGroupKey(version.versionId, key)"
                >
                  <span class="text-m text-[#185fa5] font-medium text-right" style="min-width: 80px">{{ key }}</span>
                  <div class="flex-1 h-4 bg-white rounded-lg overflow-hidden">
                    <div class="h-full rounded-lg bg-[#5B6E96]" :style="{ width: Math.round((stat.done / stat.total) * 100) + '%' }" />
                  </div>
                  <span class="text-xs text-[#9A9B90]" style="min-width: 36px; text-align: right">{{ stat.done }}/{{ stat.total }}</span>
                </div>
              </div>

              <!-- ── 연결된 일감 ── -->
              <div class="flex items-center gap-2 mb-3 mt-3">
                <span class="text font-semibold text-[#1A1816]">연결된 일감</span>
                <span class="text text-[#6B6B63]">
                  <template v-if="getGroupState(version.versionId).selectedKey"> — {{ getGroupState(version.versionId).selectedKey }} ({{ filteredTasks(version.versionId, version.tasks).length }}건) </template>
                  <template v-else> (전체 {{ version.tasks.length }}건) </template>
                </span>
              </div>

              <div class="flex flex-col gap-2">
                <div v-for="task in filteredTasks(version.versionId, version.tasks)" :key="task.taskId" class="flex items-center justify-between px-4 py-2 bg-[#F2F3F8] rounded-lg border border-[#ECEEF4]">
                  <div class="flex items-center gap-3">
                    <Tag :value="task.taskTypeName" severity="info" />
                    <div class="text-sm text-[#1A1816] cursor-pointer hover:underline hover:text-[#185fa5]" @click="router.push(`/task/${task.taskId}`)">{{ task.projectName }} - #{{ task.taskId }}: {{ task.taskTitle }}</div>
                  </div>
                  <div class="flex items-center gap-2">
                    <Tag :value="workflowLabel(task.taskWorkflow)" :severity="workflowSeverity(task.taskWorkflow)" />
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #000000c2;
  white-space: nowrap;
}
:deep(.filter-input) {
  height: 38px !important;
}
:deep(.filter-input .p-select-label) {
  display: flex;
  align-items: center;
}
</style>
