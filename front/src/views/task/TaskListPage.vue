<script setup>
import { useTaskStore } from '@/stores/task';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const taskStore = useTaskStore();
const router = useRouter();
const projectId = 'PROJ2511001';
const userId = '20261111';
const tasks = computed(() => taskStore.taskList);

// 드롭다운 옵션
const workflowOptions = computed(() => taskStore.workflowList);
const userOptions = computed(() => taskStore.memberList);
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const priorityOptions = computed(() => taskStore.priorityList);

// value → name 맵핑
const workflowMap = computed(() => Object.fromEntries(workflowOptions.value.map((w) => [w.id, w.name])));
const priorityMap = computed(() => Object.fromEntries(priorityOptions.value.map((p) => [p.id, p.name])));
const taskTypeMap = computed(() => Object.fromEntries(typeOptions.value.map((t) => [t.id, t.name])));
const taskCateMap = computed(() => Object.fromEntries(categoryOptions.value.map((c) => [c.id, c.name])));

// 필터 초기값
const defaultFilters = () => ({
  workflow: null,
  userId: null,
  plannedStart: null,
  plannedEnd: null,
  type: null,
  category: null,
  priority: null,
  title: '',
  showMyTasks: false,
  showWatching: false
});

const filters = ref(defaultFilters());
const appliedFilters = ref(defaultFilters());

// 조회 버튼 클릭
function applyFilters() {
  appliedFilters.value = { ...filters.value };
  currentPage.value = 1;
}

// 필터 초기화
function resetFilters() {
  filters.value = defaultFilters();
  userSearch.value = '';
  appliedFilters.value = defaultFilters();
  currentPage.value = 1;
}

// 담당자 검색 드롭다운
const userSearch = ref('');
const userDropdownOpen = ref(false);
const filteredUsers = computed(() => userOptions.value.filter((u) => u.name?.includes(userSearch.value)));

function selectUser(u) {
  filters.value.userId = u.name;
  userSearch.value = u.name;
  userDropdownOpen.value = false;
}
function clearUser() {
  filters.value.userId = null;
  userSearch.value = '';
}

// 정렬
const sortKey = ref(null); // 'id' | 'priority' | 'plannedEnd' | 'progress'
const sortDir = ref('asc'); // 'asc' | 'desc'

const priorityWeight = { S3: 4, S2: 3, S1: 2, S0: 1 };

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

// 필터 결과
const processedTasks = computed(() => {
  let list = [...tasks.value];

  const { showMyTasks, showWatching } = appliedFilters.value;
  if (showMyTasks || showWatching) {
    list = list.filter((t) => {
      const isMyTask = showMyTasks && t.userId === userId;
      const isWatching = showWatching && t.isWatch === 1;
      return isMyTask || isWatching;
    });
  }

  if (appliedFilters.value.workflow) list = list.filter((t) => t.workflow === appliedFilters.value.workflow);
  if (appliedFilters.value.userId) list = list.filter((t) => t.userName === appliedFilters.value.userId);
  if (appliedFilters.value.type) list = list.filter((t) => t.type === appliedFilters.value.type);
  if (appliedFilters.value.category) list = list.filter((t) => t.category === appliedFilters.value.category);
  if (appliedFilters.value.priority) list = list.filter((t) => t.priority === appliedFilters.value.priority);
  if (appliedFilters.value.title) list = list.filter((t) => t.title.includes(appliedFilters.value.title));
  if (appliedFilters.value.plannedStart) list = list.filter((t) => t.plannedEnd && t.plannedEnd >= appliedFilters.value.plannedStart);
  if (appliedFilters.value.plannedEnd) list = list.filter((t) => t.plannedEnd && t.plannedEnd <= appliedFilters.value.plannedEnd);

  // 정렬
  if (sortKey.value) {
    const dir = sortDir.value === 'asc' ? 1 : -1;
    list.sort((a, b) => {
      if (sortKey.value === 'priority') {
        return ((priorityWeight[a.priority] ?? 0) - (priorityWeight[b.priority] ?? 0)) * dir;
      }
      if (sortKey.value === 'progress') {
        return (a.progress - b.progress) * dir;
      }
      const va = a[sortKey.value] ?? '';
      const vb = b[sortKey.value] ?? '';
      return va < vb ? -dir : va > vb ? dir : 0;
    });
  }

  return list;
});

// 페이징
const currentPage = ref(1);
const pageSize = 10;
const pageBlockSize = 10;

const totalPages = computed(() => Math.max(1, Math.ceil(processedTasks.value.length / pageSize)));
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
const pagedTasks = computed(() => processedTasks.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize));

function goPage(p) {
  currentPage.value = p;
}
function prevBlock() {
  if (hasPrevBlock.value) currentPage.value = blockStart.value - 1;
}
function nextBlock() {
  if (hasNextBlock.value) currentPage.value = blockEnd.value + 1;
}

// 유틸
function progressBarColor(p) {
  if (p == 100) return '#22C55E';
  if (p >= 90) return '#86EFAC';
  if (p >= 60) return '#EAB308';
  if (p >= 30) return '#F97316';
  return '#EF4444';
}

const workflowClass = {
  Q0: 'status-new',
  Q1: 'status-in-progress',
  Q2: 'status-resolved',
  Q3: 'status-rejected',
  Q4: 'status-done'
};

const priorityClass = {
  S3: 'priority-urgent',
  S2: 'priority-high',
  S1: 'priority-mid',
  S0: 'priority-low'
};

onMounted(async () => {
  await taskStore.findCateList();
  await taskStore.findTypeList();
  await taskStore.findMember(projectId);
  await taskStore.findTaskList(projectId, userId);
  await taskStore.findPriorityList();
  await taskStore.findWorkflowList();
});
</script>

<template>
  <div class="min-h-screen bg-stone-50 px-10 py-8 text-stone-700">
    <!-- 페이지 헤더  -->
    <div class="flex items-end justify-between mb-7">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-stone-900">일감 목록</h1>
      </div>
      <button
        class="inline-flex items-center gap-1.5 bg-amber-600 hover:bg-amber-700 text-amber-50 font-semibold text-sm px-5 py-2.5 rounded-lg shadow-md shadow-amber-200 transition-all duration-150 hover:-translate-y-0.5 cursor-pointer border-none"
        @click="router.push('/task/create')"
      >
        <span class="text-base leading-none font-bold">＋</span>
        일감 추가
      </button>
    </div>

    <!-- 필터 카드 -->
    <div class="bg-white border border-stone-200 rounded-xl shadow-sm px-7 pt-5 pb-5 mb-5">
      <p class="text-xl font-bold tracking-wider uppercase text-amber-700 mb-4"><i class="pi pi-search" style="font-size: 1rem"></i> 검색 필터</p>

      <div class="grid grid-cols-4 gap-x-5 gap-y-4">
        <!-- 진행 상태 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">진행 상태</label>
          <select v-model="filters.workflow" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="w in workflowOptions" :key="w.id" :value="w.id">{{ w.name }}</option>
          </select>
        </div>

        <!-- 담당자 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">담당자</label>
          <div class="relative">
            <input
              v-model="userSearch"
              class="h-9 w-full px-3 pr-8 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors"
              placeholder="이름으로 검색"
              @focus="userDropdownOpen = true"
              @blur="setTimeout(() => (userDropdownOpen = false), 150)"
            />
            <button v-if="filters.userId" class="absolute right-2.5 top-1/2 -translate-y-1/2 text-stone-400 hover:text-stone-600 text-xs cursor-pointer bg-transparent border-none leading-none p-0" @click="clearUser">✕</button>
            <div v-if="userDropdownOpen" class="absolute top-full mt-1 left-0 right-0 bg-white border border-stone-200 rounded-lg shadow-lg z-50 overflow-hidden">
              <div v-for="u in filteredUsers" :key="u.id" class="px-3 py-2 text-sm text-stone-700 hover:bg-amber-100 hover:text-amber-700 cursor-pointer transition-colors" @mousedown.prevent="selectUser(u)">
                {{ u.name }}
              </div>
              <div v-if="filteredUsers.length === 0" class="px-3 py-2.5 text-sm text-stone-400">결과 없음</div>
            </div>
          </div>
        </div>

        <!-- 마감일 범위 -->
        <div class="flex flex-col gap-1.5 col-span-2">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">마감일</label>
          <div class="flex items-center gap-2">
            <input v-model="filters.plannedStart" type="date" class="h-9 flex-1 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors" />
            <span class="text-stone-400 text-sm shrink-0">~</span>
            <input v-model="filters.plannedEnd" type="date" class="h-9 flex-1 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors" />
          </div>
        </div>

        <!-- 일감 유형 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">일감 유형</label>
          <select v-model="filters.type" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="t in typeOptions" :key="t.id" :value="t.id">{{ t.name }}</option>
          </select>
        </div>

        <!-- 일감 범주 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">일감 범주</label>
          <select v-model="filters.category" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="c in categoryOptions" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>

        <!-- 우선순위 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">우선순위</label>
          <select v-model="filters.priority" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="p in priorityOptions" :key="p.id" :value="p.id">{{ p.name }}</option>
          </select>
        </div>

        <!-- 제목 -->
        <div class="flex flex-col gap-1.5 col-span-2">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">제목</label>
          <input v-model="filters.title" class="h-9 w-full px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors" placeholder="일감 제목 검색..." />
        </div>
      </div>

      <div class="flex items-center justify-between mt-5 pt-4 border-t border-stone-100">
        <div class="flex items-center gap-5">
          <!-- 내 일감 보기 -->
          <label class="flex items-center gap-2 text-sm text-stone-500 cursor-pointer select-none" @click="filters.showMyTasks = !filters.showMyTasks">
            <span class="w-4 h-4 rounded border flex items-center justify-center transition-colors shrink-0" :class="filters.showMyTasks ? 'bg-amber-600 border-amber-600' : 'border-stone-300 bg-stone-50'">
              <svg v-if="filters.showMyTasks" class="w-2.5 h-2.5 text-amber-50" viewBox="0 0 10 10" fill="none">
                <polyline points="1.5,5 4,7.5 8.5,2.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            내 일감 보기
          </label>

          <!-- 관람 중인 일감 보기 -->
          <label class="flex items-center gap-2 text-sm text-stone-500 cursor-pointer select-none" @click="filters.showWatching = !filters.showWatching">
            <span class="w-4 h-4 rounded border flex items-center justify-center transition-colors shrink-0" :class="filters.showWatching ? 'bg-amber-600 border-amber-600' : 'border-stone-300 bg-stone-50'">
              <svg v-if="filters.showWatching" class="w-2.5 h-2.5 text-amber-50" viewBox="0 0 10 10" fill="none">
                <polyline points="1.5,5 4,7.5 8.5,2.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </span>
            관람 중인 일감 보기
          </label>
        </div>

        <div class="flex gap-2.5">
          <button class="px-4 py-2 text-sm font-semibold text-stone-500 bg-stone-100 border border-stone-200 rounded-lg hover:bg-stone-200 transition-colors cursor-pointer" @click="resetFilters">초기화</button>
          <button class="px-5 py-2 text-sm font-bold text-amber-50 bg-amber-400 hover:bg-amber-600 rounded-lg shadow shadow-amber-200 transition-colors cursor-pointer border-none" @click="applyFilters">조회</button>
        </div>
      </div>
    </div>

    <!-- 테이블 카드 -->
    <div class="bg-white border border-stone-200 rounded-xl shadow-sm overflow-hidden">
      <!-- 메타 -->
      <div class="px-6 py-3.5 border-b border-stone-100">
        <span class="text-sm text-stone-400">
          총 <strong class="text-stone-700 font-bold">{{ processedTasks.length }}</strong
          >개
        </span>
      </div>

      <!-- 표 -->
      <div class="overflow-x-auto">
        <table class="w-full border-collapse text-sm">
          <thead>
            <tr class="bg-stone-100 border-b border-stone-200">
              <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-50">
                <button class="sort-btn" :class="{ active: sortKey === 'id' }" @click="toggleSort('id')">
                  번호 <span class="sort-icon">{{ sortIcon('id') }}</span>
                </button>
              </th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">유형</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-28">상태</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">
                <button class="sort-btn" :class="{ active: sortKey === 'priority' }" @click="toggleSort('priority')">
                  우선순위 <span class="sort-icon">{{ sortIcon('priority') }}</span>
                </button>
              </th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">범주</th>
              <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 min-w-52">제목</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-50">
                <button class="sort-btn" :class="{ active: sortKey === 'progress' }" @click="toggleSort('progress')">
                  진척도 <span class="sort-icon">{{ sortIcon('progress') }}</span>
                </button>
              </th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-28">담당자</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-30">
                <button class="sort-btn" :class="{ active: sortKey === 'plannedEnd' }" @click="toggleSort('plannedEnd')">
                  마감일 <span class="sort-icon">{{ sortIcon('plannedEnd') }}</span>
                </button>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(task, idx) in pagedTasks" :key="task.id" class="border-b border-stone-100 last:border-none hover:bg-amber-50 transition-colors duration-100" :class="idx % 2 !== 0 ? 'bg-stone-50/60' : 'bg-white'">
              <!-- 번호 -->
              <td class="px-4 py-3.5">
                <span class="font-mono text-m font-semibold bg-amber-100 text-amber-700 px-2 py-0.5 rounded">
                  {{ task.id }}
                </span>
              </td>

              <!-- 유형 -->
              <td class="px-4 py-3.5 text-base text-stone-600 text-center">
                {{ taskTypeMap[task.type] ?? task.type }}
              </td>

              <!-- 상태 -->
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded-full text-sm font-semibold whitespace-nowrap" :class="workflowClass[task.workflow] ?? 'bg-stone-100 text-stone-500'">
                  {{ workflowMap[task.workflow] ?? task.workflow }}
                </span>
              </td>

              <!-- 우선순위 -->
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded-full text-sm font-semibold" :class="priorityClass[task.priority] ?? 'bg-stone-100 text-stone-400'">
                  {{ priorityMap[task.priority] ?? task.priority }}
                </span>
              </td>

              <!-- 범주 -->
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block mt-1 text-sm text-stone-400 bg-stone-100 px-2 py-px rounded">
                  {{ taskCateMap[task.category] ?? task.category }}
                </span>
              </td>

              <!-- 제목 -->
              <td class="px-4 py-3.5">
                <a href="#" class="block text-base font-medium text-stone-800 hover:text-amber-600 transition-colors leading-snug">
                  {{ task.title }}
                </a>
              </td>

              <!-- 진척도 -->
              <td class="px-4 py-3.5">
                <div class="flex items-center gap-2">
                  <div class="flex-1 h-1.5 bg-stone-200 rounded-full overflow-hidden">
                    <div class="h-full rounded-full transition-all duration-500" :style="{ width: task.progress + '%', background: progressBarColor(task.progress) }"></div>
                  </div>
                  <span class="text-base font-semibold text-stone-500 w-9 text-right shrink-0">{{ task.progress }}%</span>
                </div>
              </td>

              <!-- 담당자 -->
              <td class="px-4 py-3.5 text-base text-stone-600 text-center">{{ task.userName }}</td>

              <!-- 마감일 -->
              <td class="px-4 py-3.5 text-base text-stone-500 tabular-nums text-center">{{ task.plannedEnd }}</td>
            </tr>

            <!-- 빈 상태 -->
            <tr v-if="pagedTasks.length === 0">
              <td colspan="9" class="text-center py-16 text-sm text-stone-400">일감이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이징 -->
      <div class="flex items-center justify-center gap-1 px-6 py-5 border-t border-stone-100">
        <button
          class="w-8 h-8 flex items-center justify-center rounded-md border border-stone-200 bg-white text-stone-400 text-base hover:bg-amber-100 hover:border-amber-300 hover:text-amber-700 disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer"
          :disabled="!hasPrevBlock"
          @click="prevBlock"
        >
          ‹
        </button>

        <button
          v-for="p in pageNumbers"
          :key="p"
          class="w-8 h-8 flex items-center justify-center rounded-md border text-sm font-medium transition-colors cursor-pointer"
          :class="p === currentPage ? 'bg-amber-600 border-amber-600 text-amber-50 font-bold shadow-sm shadow-amber-300' : 'border-stone-200 bg-white text-stone-500 hover:bg-amber-100 hover:border-amber-300 hover:text-amber-700'"
          @click="goPage(p)"
        >
          {{ p }}
        </button>

        <button
          class="w-8 h-8 flex items-center justify-center rounded-md border border-stone-200 bg-white text-stone-400 text-base hover:bg-amber-100 hover:border-amber-300 hover:text-amber-700 disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer"
          :disabled="!hasNextBlock"
          @click="nextBlock"
        >
          ›
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
select {
  appearance: auto;
}
input[type='date']::-webkit-calendar-picker-indicator {
  opacity: 0.5;
  cursor: pointer;
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

/* ── 우선순위 ── */
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

/* ── 상태 ── */
.status-new {
  background-color: #f1efe8;
  color: #444441;
}
.status-in-progress {
  background-color: #eeedfe;
  color: #3c3489;
}
.status-resolved {
  background-color: #e1f5ee;
  color: #085041;
}
.status-rejected {
  background-color: #faece7;
  color: #712b13;
}
.status-done {
  background-color: #eaf3de;
  color: #27500a;
}
</style>
