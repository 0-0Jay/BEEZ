<script setup>
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import DatePicker from 'primevue/datepicker';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import { computed, onMounted, ref } from 'vue';

const taskStore = useTaskStore();
const projectStore = useProjectStore();

const project = computed(() => projectStore.selectedProject);
const spent = computed(() => taskStore.spent);

// 필터 상태 (draft → applied)
const defaultFilters = () => ({
  projectId: null,
  type: null,
  category: null,
  assignee: null,
  activityType: null,
  dateFrom: null,
  dateTo: null,
  titleSearch: ''
});

const filterDraft = ref(defaultFilters());
const appliedFilter = ref(defaultFilters());

function applyFilter() {
  appliedFilter.value = { ...filterDraft.value };
  currentPage.value = 1;
}

function resetFilter() {
  filterDraft.value = defaultFilters();
  appliedFilter.value = defaultFilters();
  currentPage.value = 1;
}

// 드롭다운 옵션
const projectOptions = computed(() => {
  const map = new Map();
  (spent.value || []).forEach((s) => {
    if (s.projectId && !map.has(s.projectId)) {
      map.set(s.projectId, { label: s.projectTitle, value: s.projectId });
    }
  });
  return Array.from(map.values());
});

const commonCodes = computed(() => taskStore.commonCodeList);
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const activityOptions = computed(() => commonCodes.value.filter((c) => c.cgroup === '0U'));

const typeMap = computed(() => Object.fromEntries(typeOptions.value.map((t) => [t.id, t.name])));
const categoryMap = computed(() => Object.fromEntries(categoryOptions.value.map((c) => [c.id, c.name])));
const activityMap = computed(() => Object.fromEntries(activityOptions.value.map((a) => [a.id, a.name])));
const assigneeMap = computed(() => Object.fromEntries((spent.value || []).map((s) => [s.userId, s.name])));
const projectMap = computed(() => Object.fromEntries((spent.value || []).map((s) => [s.projectId, s.projectTitle])));

const assigneeOptions = computed(() => {
  const map = new Map();

  (spent.value || []).forEach((s) => {
    if (s.userId && !map.has(s.userId)) {
      map.set(s.userId, {
        id: s.userId,
        name: s.name
      });
    }
  });

  return Array.from(map.values());
});

// 필터링된 데이터
const filteredSpent = computed(() => {
  const f = appliedFilter.value;
  return (spent.value || []).filter((s) => {
    if (f.projectId && s.projectId !== f.projectId) return false;
    if (f.type && s.type !== f.type) return false;
    if (f.category && s.category !== f.category) return false;
    if (f.assignee && s.userId !== f.assignee?.value) return false;
    if (f.activityType && s.activityType !== f.activityType) return false;
    if (f.titleSearch && !s.taskTitle?.toLowerCase().includes(f.titleSearch.toLowerCase())) return false;
    if (f.dateFrom || f.dateTo) {
      const d = new Date(s.taskStart);
      if (f.dateFrom) {
        const from = new Date(f.dateFrom);
        from.setHours(0, 0, 0, 0);
        if (d < from) return false;
      }
      if (f.dateTo) {
        const to = new Date(f.dateTo);
        to.setHours(23, 59, 59, 999);
        if (d > to) return false;
      }
    }
    return true;
  });
});

const totalFilteredMinutes = computed(() => filteredSpent.value.reduce((sum, s) => sum + (s.spent || 0), 0));

// 정렬
const sortKey = ref('taskStart');
const sortDir = ref('desc');

function toggleSort(key) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortKey.value = key;
    sortDir.value = 'asc';
  }
}

function sortIcon(key) {
  if (sortKey.value !== key) return '↕';
  return sortDir.value === 'asc' ? '↑' : '↓';
}

const sortedFilteredSpent = computed(() => {
  const list = [...filteredSpent.value];
  if (!sortKey.value) return list;
  const dir = sortDir.value === 'asc' ? 1 : -1;
  list.sort((a, b) => {
    const va = a[sortKey.value] ?? '';
    const vb = b[sortKey.value] ?? '';
    return va < vb ? -dir : va > vb ? dir : 0;
  });
  return list;
});

// 페이징
const currentPage = ref(1);
const pageSize = 10;
const pageBlockSize = 10;

const totalPages = computed(() => Math.max(1, Math.ceil(sortedFilteredSpent.value.length / pageSize)));
const currentBlock = computed(() => Math.ceil(currentPage.value / pageBlockSize));
const blockStart = computed(() => (currentBlock.value - 1) * pageBlockSize + 1);
const blockEnd = computed(() => Math.min(blockStart.value + pageBlockSize - 1, totalPages.value));
const pageNumbers = computed(() => {
  const a = [];
  for (let i = blockStart.value; i <= blockEnd.value; i++) a.push(i);
  return a;
});
const hasPrevBlock = computed(() => currentBlock.value > 1);
const hasNextBlock = computed(() => blockEnd.value < totalPages.value);
const pagedSpent = computed(() => sortedFilteredSpent.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize));

function goPage(p) {
  currentPage.value = p;
}
function prevBlock() {
  if (hasPrevBlock.value) currentPage.value = blockStart.value - 1;
}
function nextBlock() {
  if (hasNextBlock.value) currentPage.value = blockEnd.value + 1;
}

// 보고서 기간 필터
const reportDateFrom = ref(null);
const reportDateTo = ref(null);

const reportSpent = computed(() => {
  if (!reportDateFrom.value && !reportDateTo.value) return spent.value || [];
  return (spent.value || []).filter((s) => {
    const d = new Date(s.taskStart);
    if (reportDateFrom.value) {
      const from = new Date(reportDateFrom.value);
      from.setHours(0, 0, 0, 0);
      if (d < from) return false;
    }
    if (reportDateTo.value) {
      const to = new Date(reportDateTo.value);
      to.setHours(23, 59, 59, 999);
      if (d > to) return false;
    }
    return true;
  });
});

function resetReportDate() {
  reportDateFrom.value = null;
  reportDateTo.value = null;
}

// 요약 통계
const reportTotalMinutes = computed(() => reportSpent.value.reduce((sum, s) => sum + (s.spent || 0), 0));

const dailyAvgMinutes = computed(() => {
  const days = new Set(reportSpent.value.map((s) => new Date(s.taskStart).toDateString())).size;
  return days > 0 ? Math.round(reportTotalMinutes.value / days) : 0;
});

// 통계 헬퍼
function groupByKey(data, key, labelMap) {
  const map = new Map();

  data.forEach((s) => {
    const k = s[key] || '미분류';
    const label = labelMap?.[k] ?? k;

    if (!map.has(k)) {
      map.set(k, { label, minutes: 0 });
    }

    map.get(k).minutes += s.spent || 0;
  });

  const total = [...map.values()].reduce((sum, v) => sum + v.minutes, 0);

  return [...map.values()]
    .sort((a, b) => b.minutes - a.minutes)
    .map((v) => ({
      ...v,
      pct: total > 0 ? Math.round((v.minutes / total) * 100) : 0
    }));
}

const categoryStats = computed(() => groupByKey(reportSpent.value, 'category', categoryMap.value));
const typeStats = computed(() => groupByKey(reportSpent.value, 'type', typeMap.value));
const activityStats = computed(() => groupByKey(reportSpent.value, 'activityType', activityMap.value));
const assigneeStats = computed(() => groupByKey(reportSpent.value, 'userId', assigneeMap.value));
const projectStats = computed(() => groupByKey(reportSpent.value, 'projectId', projectMap.value));

// 도넛 차트
const donutColors = ['#F59E0B', '#FCD34D', '#D97706', '#92400E', '#78350F', '#FDE68A', '#B45309'];
const chartData = computed(() => ({
  labels: projectStats.value.map((c) => c.label),
  datasets: [
    {
      data: projectStats.value.map((c) => c.minutes),
      backgroundColor: donutColors
    }
  ]
}));

const chartOptions = {
  plugins: {
    legend: {
      position: 'none'
    }
  }
};

// 유틸
function formatMinutes(mins) {
  if (!mins && mins !== 0) return '-';

  const totalMins = mins;
  const d = Math.floor(totalMins / (8 * 60)); // 480분 = 1일
  const h = Math.floor((totalMins % (8 * 60)) / 60);
  const m = totalMins % 60;

  const parts = [];
  if (d > 0) parts.push(`${d}일`);
  if (h > 0) parts.push(`${h}시간`);
  if (m > 0) parts.push(`${m}분`);

  return parts.length > 0 ? parts.join(' ') : '0분';
}

function formatDateTime(dt) {
  if (!dt) return '-';
  const d = new Date(dt);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
}

const activeTab = ref(0);

onMounted(async () => {
  await taskStore.findCommonCodeList();
  await taskStore.findCateList();
  await taskStore.findTypeList();
  await taskStore.findSpentOverview(project.value?.id);
  console.log(taskStore.spent);
});
</script>

<template>
  <div class="min-h-screen bg-stone-50 px-10 py-8 text-stone-700">
    <!-- 페이지 헤더 -->
    <div class="mb-7">
      <h1 class="text-2xl font-bold tracking-tight text-stone-900">소요시간</h1>
    </div>

    <!-- 탭 네비게이션 -->
    <div class="flex gap-0 mb-5 border-b border-[#C7C7C2]">
      <button class="tab-btn" :class="{ active: activeTab === 0 }" @click="activeTab = 0">소요시간 목록</button>
      <button class="tab-btn" :class="{ active: activeTab === 1 }" @click="activeTab = 1">소요시간 보고서</button>
    </div>

    <!-- ===== 탭1: 소요시간 목록 ===== -->
    <template v-if="activeTab === 0">
      <!-- 필터 카드 -->
      <div class="bg-[#F2F0EB] border border-[#C7C7C2] rounded-xl shadow-sm px-7 pt-5 pb-5 mb-5">
        <p class="text-xl font-bold tracking-wider uppercase text-amber-700 mb-4"><i class="pi pi-search" style="font-size: 1rem"></i> 검색 필터</p>

        <div class="grid grid-cols-4 gap-x-5 gap-y-4">
          <!-- 프로젝트 -->
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">프로젝트</label>
            <Select v-model="filterDraft.projectId" :options="projectOptions" optionLabel="label" optionValue="value" placeholder="전체" showClear />
          </div>

          <!-- 일감 유형 -->
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">일감 유형</label>
            <Select v-model="filterDraft.type" :options="typeOptions" optionLabel="name" optionValue="id" placeholder="전체" showClear />
          </div>

          <!-- 일감 범주 -->
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">일감 범주</label>
            <Select v-model="filterDraft.category" :options="categoryOptions" optionLabel="name" optionValue="id" placeholder="전체" showClear />
          </div>

          <!-- 담당자 -->
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">담당자</label>
            <Select v-model="filterDraft.assignee" :options="assigneeOptions" optionLabel="name" optionValue="id" placeholder="이름 검색" filter filter-placeholder="이름 검색" showClear />
          </div>

          <!-- 작업종류 -->
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">작업종류</label>
            <Select v-model="filterDraft.activityType" :options="activityOptions" optionLabel="name" optionValue="id" placeholder="전체" showClear />
          </div>

          <!-- 조회 기간 -->
          <div class="flex flex-col gap-1.5 col-span-2">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">조회 기간 </label>
            <div class="flex gap-1.5 items-center">
              <DatePicker v-model="filterDraft.dateFrom" :manualInput="false" showButtonBar showIcon placeholder="시작일" dateFormat="yy-mm-dd" class="w-full" inputClass="w-full" />
              <span class="text-stone-400 text-base shrink-0">~</span>
              <DatePicker v-model="filterDraft.dateTo" :manualInput="false" showButtonBar showIcon placeholder="종료일" dateFormat="yy-mm-dd" class="w-full" inputClass="w-full" />
            </div>
          </div>

          <!-- 빈 칸 (4열 맞춤) -->
          <div></div>

          <!-- 제목 검색 (다음 줄, 2칸 차지) -->
          <div class="flex flex-col gap-1.5 col-span-2">
            <label class="text-base font-semibold uppercase tracking-wider text-stone-400">제목 검색</label>
            <InputText v-model="filterDraft.titleSearch" placeholder="일감 제목을 입력하세요" class="w-full" />
          </div>
        </div>

        <div class="flex items-center justify-end mt-5 pt-4 border-t border-[#C7C7C2]">
          <div class="flex gap-2.5">
            <Button class="px-4 py-2" severity="secondary" raised @click="resetFilter">초기화</Button>
            <Button class="px-5 py-2" raised @click="applyFilter">조건 적용</Button>
          </div>
        </div>
      </div>

      <!-- 테이블 카드 -->
      <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm overflow-hidden">
        <div class="flex items-center justify-between px-6 py-3.5 border-b border-stone-100">
          <span class="text-base text-stone-400">
            총 <strong class="text-stone-700 font-bold">{{ sortedFilteredSpent.length }}</strong
            >건
          </span>
          <span class="text-base text-stone-400">
            총 소요시간 <strong class="text-amber-700 font-bold">{{ formatMinutes(totalFilteredMinutes) }}</strong>
          </span>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full border-collapse text-base">
            <thead>
              <tr class="bg-stone-100 border-b border-stone-200">
                <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 min-w-30">프로젝트</th>
                <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 min-w-30">일감</th>
                <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">유형</th>
                <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">범주</th>
                <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-28">담당자</th>
                <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">작업종류</th>
                <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 min-w-60">설명</th>
                <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-36">
                  <button class="sort-btn" :class="{ active: sortKey === 'taskStart' }" @click="toggleSort('taskStart')">
                    작업일시 <span class="sort-icon">{{ sortIcon('taskStart') }}</span>
                  </button>
                </th>
                <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-40">
                  <button class="sort-btn" :class="{ active: sortKey === 'spent' }" @click="toggleSort('spent')">
                    소요시간 <span class="sort-icon">{{ sortIcon('spent') }}</span>
                  </button>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in pagedSpent" :key="`${item.taskId}-${item.taskStart}`" class="border-b border-stone-100 last:border-none hover:bg-amber-50 transition-colors duration-100" :class="idx % 2 !== 0 ? 'bg-stone-50/60' : 'bg-white'">
                <td class="px-4 py-3.5">
                  <span class="block text-base text-stone-700 leading-snug">{{ item.projectTitle }}</span>
                  <span class="text-base font-mono text-stone-400 mt-0.5 inline-block">#{{ item.projectId }}</span>
                </td>
                <td class="px-4 py-3.5">
                  <span class="block text-base font-medium text-stone-800 leading-snug">{{ item.taskTitle }}</span>
                  <span class="font-mono text-base font-semibold bg-amber-100 text-amber-700 px-1.5 py-0.5 rounded mt-0.5 inline-block">{{ item.taskId }}</span>
                </td>
                <td class="px-4 py-3.5 text-center">
                  <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold whitespace-nowrap bg-[#eeedfe] text-[#3c3489]">{{ typeMap[item.type] ?? item.type }}</span>
                </td>
                <td class="px-4 py-3.5 text-center">
                  <span class="inline-block text-base text-stone-400 bg-stone-100 px-2 py-px rounded">{{ categoryMap[item.category] ?? item.category }}</span>
                </td>
                <td class="px-4 py-3.5 text-center">
                  <span class="block text-base text-stone-600">{{ item.name }}</span>
                  <span class="text-base font-mono text-stone-400">{{ item.userId }}</span>
                </td>
                <td class="px-4 py-3.5 text-center">
                  <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold whitespace-nowrap bg-[#faeeda] text-[#633806]">{{ activityMap[item.activityType] ?? item.activityType }}</span>
                </td>
                <td class="px-4 py-3.5">
                  <span class="text-base text-stone-500 line-clamp-2">{{ item.description }}</span>
                </td>
                <td class="px-4 py-3.5 text-base text-stone-500 tabular-nums text-center whitespace-nowrap">{{ formatDateTime(item.taskStart) }}</td>
                <td class="px-4 py-3.5 text-center">
                  <span class="font-mono text-base font-semibold bg-amber-100 text-amber-700 px-2 py-0.5 rounded">{{ formatMinutes(item.spent) }}</span>
                </td>
              </tr>
              <tr v-if="pagedSpent.length === 0">
                <td colspan="9" class="text-center py-16 text-base text-stone-400">조건에 맞는 소요시간 데이터가 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 페이징 -->
        <div class="flex items-center justify-center gap-1 px-6 py-5 border-t border-stone-100">
          <Button icon="pi pi-chevron-left" text rounded :disabled="!hasPrevBlock" @click="prevBlock" />
          <Button v-for="p in pageNumbers" :key="p" :label="String(p)" :severity="p === currentPage ? undefined : 'secondary'" :text="p !== currentPage" rounded @click="goPage(p)" />
          <Button icon="pi pi-chevron-right" text rounded :disabled="!hasNextBlock" @click="nextBlock" />
        </div>
      </div>
    </template>

    <!-- ===== 탭2: 소요시간 보고서 ===== -->
    <template v-if="activeTab === 1">
      <!-- 기간 선택 (시작일 / 종료일 분리) -->
      <div class="bg-[#F2F0EB] border border-[#C7C7C2] rounded-xl shadow-sm px-7 py-4 mb-5 flex items-center gap-4">
        <span class="text-base font-semibold uppercase tracking-wider text-stone-400">보고서 기간</span>
        <DatePicker v-model="reportDateFrom" :manualInput="false" showButtonBar showIcon placeholder="시작일" dateFormat="yy-mm-dd" />
        <span class="text-stone-400">~</span>
        <DatePicker v-model="reportDateTo" :manualInput="false" showButtonBar showIcon placeholder="종료일" dateFormat="yy-mm-dd" />
        <Button label="초기화" severity="secondary" raised @click="resetReportDate" />
        <span class="text-base text-stone-400 ml-2">{{ reportSpent.length }}건의 데이터</span>
      </div>

      <!-- 전체 소요시간 요약 -->
      <div class="bg-[#F2F0EB] border border-[#C7C7C2] rounded-xl shadow-sm px-7 pt-5 pb-5 mb-5">
        <p class="text-2xl font-bold tracking-wider uppercase text-amber-700 mb-4"><i class="pi pi-chart-bar" style="font-size: 1rem"></i> 전체 소요시간</p>
        <div class="grid grid-cols-2 gap-5">
          <div class="summary-stat-card">
            <div class="text-6xl font-bold text-amber-700">{{ formatMinutes(reportTotalMinutes) }}</div>
            <div class="text-base text-stone-400 mt-1">총 {{ reportSpent.length }}개 일감</div>
          </div>
          <div class="summary-stat-card text-right">
            <div class="text-base font-semibold uppercase tracking-wider text-stone-400 mb-2">일 평균</div>
            <div class="text-3xl font-bold text-amber-700">{{ formatMinutes(dailyAvgMinutes) }}<span class="text-base text-stone-400 mt-1"> / 일</span></div>
          </div>
        </div>
      </div>

      <!-- 프로젝트별 (도넛 + 리스트) -->
      <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm px-7 py-5 mb-5">
        <p class="text-2xl font-bold uppercase tracking-wider text-stone-500 mb-5">프로젝트별 소요시간</p>
        <div class="text-base text-stone-400 flex items-center gap-2 mb-4">
          <i class="pi pi-info-circle"></i>
          <span>현재 프로젝트와 직속 하위 프로젝트만 집계됩니다.</span>
        </div>
        <div class="flex gap-12 items-center">
          <!-- 도넛 -->
          <div class="flex-shrink-0 relative" style="width: 200px; height: 200px">
            <Chart class="w-58" type="doughnut" :data="chartData" :options="chartOptions"></Chart>
            <div class="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <span class="text-base text-stone-400">합계</span>
              <span class="text-xl font-bold text-amber-700">{{ formatMinutes(reportTotalMinutes) }}</span>
            </div>
          </div>
          <!-- 범례 + 프로그레스 -->
          <div class="flex-1 space-y-3">
            <div v-for="(item, idx) in projectStats" :key="item.label">
              <div class="flex justify-between items-center mb-1.5">
                <div class="flex items-center gap-2">
                  <span class="inline-block w-3 h-3 rounded-sm flex-shrink-0" :style="`background:${donutColors[idx % donutColors.length]}`"></span>
                  <span class="text-base text-stone-700">{{ item.label }}</span>
                </div>
                <span class="font-mono text-base font-bold text-amber-700">{{ formatMinutes(item.minutes) }}</span>
              </div>
              <ProgressBar
                :value="item.pct"
                :pt="{
                  value: {
                    style: {
                      background: donutColors[idx % donutColors.length]
                    }
                  }
                }"
              />
            </div>
            <div v-if="!projectStats.length" class="text-center py-8 text-base text-stone-400">데이터가 없습니다.</div>
          </div>
        </div>
      </div>

      <!-- 2×2 그리드 -->
      <div class="grid grid-cols-2 gap-5">
        <!-- 일감 범주별 -->
        <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm px-7 py-5">
          <p class="text-2xl font-bold uppercase tracking-wider text-stone-500 mb-5">일감 범주별 소요시간</p>
          <div class="space-y-4">
            <div v-for="item in categoryStats" :key="item.label">
              <div class="flex justify-between items-center mb-1.5">
                <span class="text-base text-stone-700">{{ item.label }}</span>
                <span class="font-mono text-base font-bold text-amber-700">{{ formatMinutes(item.minutes) }}</span>
              </div>
              <ProgressBar
                :value="item.pct"
                :pt="{
                  value: {
                    style: {
                      background: donutColors[1]
                    }
                  }
                }"
              ></ProgressBar>
            </div>
            <div v-if="!categoryStats.length" class="text-center py-8 text-base text-stone-400">데이터가 없습니다.</div>
          </div>
        </div>

        <!-- 일감 유형별 -->
        <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm px-7 py-5">
          <p class="text-2xl font-bold uppercase tracking-wider text-stone-500 mb-5">일감 유형별 소요시간</p>
          <div class="space-y-4">
            <div v-for="item in typeStats" :key="item.label">
              <div class="flex justify-between items-center mb-1.5">
                <span class="text-base text-stone-700">{{ item.label }}</span>
                <span class="font-mono text-base font-bold text-amber-700">{{ formatMinutes(item.minutes) }}</span>
              </div>
              <ProgressBar
                :value="item.pct"
                :pt="{
                  value: {
                    style: {
                      background: donutColors[2]
                    }
                  }
                }"
              ></ProgressBar>
            </div>
            <div v-if="!typeStats.length" class="text-center py-8 text-base text-stone-400">데이터가 없습니다.</div>
          </div>
        </div>

        <!-- 담당자별 -->
        <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm px-7 py-5">
          <p class="text-2xl font-bold uppercase tracking-wider text-stone-500 mb-5">담당자별 소요시간</p>
          <div class="space-y-4">
            <div v-for="item in assigneeStats" :key="item.label">
              <div class="flex justify-between items-center mb-1.5">
                <span class="text-base text-stone-700">{{ item.label }}</span>
                <span class="font-mono text-base font-bold text-amber-700">{{ formatMinutes(item.minutes) }}</span>
              </div>
              <ProgressBar
                :value="item.pct"
                :pt="{
                  value: {
                    style: {
                      background: donutColors[3]
                    }
                  }
                }"
              ></ProgressBar>
            </div>
            <div v-if="!assigneeStats.length" class="text-center py-8 text-base text-stone-400">데이터가 없습니다.</div>
          </div>
        </div>

        <!-- 작업 종류별 -->
        <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm px-7 py-5">
          <p class="text-2xl font-bold uppercase tracking-wider text-stone-500 mb-5">작업 종류별 소요시간</p>
          <div class="space-y-4">
            <div v-for="item in activityStats" :key="item.label">
              <div class="flex justify-between items-center mb-1.5">
                <span class="text-base text-stone-700">{{ item.label }}</span>
                <span class="font-mono text-base font-bold text-amber-700">{{ formatMinutes(item.minutes) }}</span>
              </div>
              <ProgressBar :value="item.pct"></ProgressBar>
            </div>
            <div v-if="!activityStats.length" class="text-center py-8 text-base text-stone-400">데이터가 없습니다.</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
/* ── 탭 네비게이션 ── */
.tab-btn {
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 600;
  color: #78716c;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  cursor: pointer;
  transition:
    color 0.15s,
    border-color 0.15s;
  letter-spacing: 0.01em;
}
.tab-btn:hover {
  color: #44403c;
}
.tab-btn.active {
  color: #b45309;
  border-bottom-color: #f59e0b;
}

/* ── 정렬 버튼 ── */
.sort-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  padding: 2px 4px;
  border-radius: 4px;
  cursor: pointer;
  font-size: inherit;
  font-weight: inherit;
  color: inherit;
  letter-spacing: inherit;
  text-transform: inherit;
  transition:
    background 0.15s,
    color 0.15s;
  white-space: nowrap;
}
.sort-btn:hover {
  background: #fef3c7;
  color: #92400e;
}
.sort-btn.active {
  color: #b45309;
}
.sort-icon {
  font-size: 0.75rem;
  opacity: 0.7;
}
.sort-btn.active .sort-icon {
  opacity: 1;
  color: #d97706;
}

/* ── 요약 카드 ── */
.summary-stat-card {
  border-radius: 10px;
  padding: 10px 10px;
}

/* ── line-clamp ── */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
