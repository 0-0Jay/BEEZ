<script setup>
import { computed, ref } from 'vue';

// ── 예시 데이터 ────────────────────────────────────────────────
const tasks = ref([
  {
    id: 'TASK-001',
    projectId: 'PRJ-001',
    versionId: 'VER-1.2',
    title: '로그인 페이지 보안 취약점 수정',
    description: 'SQL Injection 방어 로직 추가 및 입력값 검증 강화',
    userId: '김민준',
    type: '결함',
    category: '보안',
    workflow: '진행',
    priority: '높음',
    plannedStart: '2025-03-01',
    plannedEnd: '2025-03-15',
    actualStart: '2025-03-02',
    actualEnd: null,
    estimatedTime: 16,
    progress: 65,
    status: '1',
    parentId: null,
    isPublic: '1',
    creator: '이서연',
    fileId: 'FILE-012'
  },
  {
    id: 'TASK-002',
    projectId: 'PRJ-001',
    versionId: 'VER-1.3',
    title: '대시보드 차트 컴포넌트 신규 개발',
    description: '실시간 데이터 시각화를 위한 Chart.js 기반 컴포넌트 구현',
    userId: '박지수',
    type: '새기능',
    category: '프론트',
    workflow: '해결',
    priority: '보통',
    plannedStart: '2025-03-10',
    plannedEnd: '2025-03-28',
    actualStart: '2025-03-11',
    actualEnd: null,
    estimatedTime: 24,
    progress: 90,
    status: '1',
    parentId: null,
    isPublic: '1',
    creator: '최동현',
    fileId: null
  },
  {
    id: 'TASK-003',
    projectId: 'PRJ-002',
    versionId: 'VER-2.0',
    title: 'REST API 응답 성능 최적화',
    description: '주요 엔드포인트 쿼리 튜닝 및 캐싱 레이어 도입',
    userId: '이서연',
    type: '지원',
    category: '백',
    workflow: '완료',
    priority: '긴급',
    plannedStart: '2025-02-20',
    plannedEnd: '2025-03-05',
    actualStart: '2025-02-21',
    actualEnd: '2025-03-04',
    estimatedTime: 32,
    progress: 100,
    status: '1',
    parentId: null,
    isPublic: '0',
    creator: '김민준',
    fileId: 'FILE-009'
  }
]);

// ── 필터 상태 ────────────────────────────────────────────────
const filters = ref({
  workflow: null,
  userId: null,
  plannedStart: null,
  plannedEnd: null,
  type: null,
  category: null,
  priority: null,
  title: '',
  includeSubProject: false
});

// ── 드롭다운 옵션 ─────────────────────────────────────────────
const workflowOptions = ['신규', '진행', '해결', '반려', '완료'];
const userOptions = ['김민준', '박지수', '이서연', '최동현', '홍길동'];
const typeOptions = ['결함', '새기능', '지원', '테스트', '기타'];
const categoryOptions = ['보안', '백', '프론트', '디자인', '인프라', '기타'];
const priorityOptions = ['긴급', '높음', '보통', '낮음'];

// ── 담당자 검색 드롭다운 ──────────────────────────────────────
const userSearch = ref('');
const userDropdownOpen = ref(false);
const filteredUsers = computed(() => userOptions.filter((u) => u.includes(userSearch.value)));

function selectUser(u) {
  filters.value.userId = u;
  userSearch.value = u;
  userDropdownOpen.value = false;
}
function clearUser() {
  filters.value.userId = null;
  userSearch.value = '';
}

// ── 필터 초기화 ───────────────────────────────────────────────
function resetFilters() {
  filters.value = {
    workflow: null,
    userId: null,
    plannedStart: null,
    plannedEnd: null,
    type: null,
    category: null,
    priority: null,
    title: '',
    includeSubProject: false
  };
  userSearch.value = '';
}

// ── 페이징 ───────────────────────────────────────────────────
const currentPage = ref(1);
const pageSize = 10;
const pageBlockSize = 10;

const totalPages = computed(() => Math.max(1, Math.ceil(tasks.value.length / pageSize)));
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
const pagedTasks = computed(() => tasks.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize));

function goPage(p) {
  currentPage.value = p;
}
function prevBlock() {
  if (hasPrevBlock.value) currentPage.value = blockStart.value - 1;
}
function nextBlock() {
  if (hasNextBlock.value) currentPage.value = blockEnd.value + 1;
}

// ── 유틸 ────────────────────────────────────────────────────

function progressBarColor(p) {
  if (p >= 100) return '#C97700';
  if (p >= 60) return '#F5A623';
  if (p >= 30) return '#FFDA7A';
  return '#C8C4B8';
}

const workflowClass = {
  신규: 'status-new',
  진행: 'status-in-progress',
  해결: 'status-resolved',
  반려: 'status-rejected',
  완료: 'status-done'
};

const priorityClass = {
  긴급: 'priority-urgent',
  높음: 'priority-high',
  보통: 'priority-mid',
  낮음: 'priority-low'
};
</script>

<template>
  <div class="min-h-screen bg-stone-50 px-10 py-8 text-stone-700">
    <!-- ── 페이지 헤더 ──────────────────────────────────────── -->
    <div class="flex items-end justify-between mb-7">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-stone-900">일감 목록</h1>
      </div>
      <button
        class="inline-flex items-center gap-1.5 bg-amber-600 hover:bg-amber-700 text-amber-50 font-semibold text-sm px-5 py-2.5 rounded-lg shadow-md shadow-amber-200 transition-all duration-150 hover:-translate-y-0.5 cursor-pointer border-none"
      >
        <span class="text-base leading-none font-bold">＋</span>
        일감 추가
      </button>
    </div>

    <!-- ── 필터 카드 ────────────────────────────────────────── -->
    <div class="bg-white border border-stone-200 rounded-xl shadow-sm px-7 pt-5 pb-5 mb-5">
      <p class="text-xl font-bold tracking-wider uppercase text-amber-700 mb-4"><i class="pi pi-search" style="font-size: 1rem"></i> 검색 필터</p>

      <div class="grid grid-cols-4 gap-x-5 gap-y-4">
        <!-- 진행 상태 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">진행 상태</label>
          <select v-model="filters.workflow" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="w in workflowOptions" :key="w" :value="w">{{ w }}</option>
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
              <div v-for="u in filteredUsers" :key="u" class="px-3 py-2 text-sm text-stone-700 hover:bg-amber-100 hover:text-amber-700 cursor-pointer transition-colors" @mousedown.prevent="selectUser(u)">{{ u }}</div>
              <div v-if="filteredUsers.length === 0" class="px-3 py-2.5 text-sm text-stone-400">결과 없음</div>
            </div>
          </div>
        </div>

        <!-- 마감일 범위 (2칸) -->
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
            <option v-for="t in typeOptions" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>

        <!-- 일감 범주 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">일감 범주</label>
          <select v-model="filters.category" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>

        <!-- 우선순위 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">우선순위</label>
          <select v-model="filters.priority" class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors cursor-pointer">
            <option :value="null">전체</option>
            <option v-for="p in priorityOptions" :key="p" :value="p">{{ p }}</option>
          </select>
        </div>

        <!-- 제목 (2칸) -->
        <div class="flex flex-col gap-1.5 col-span-2">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-400">제목</label>
          <input v-model="filters.title" class="h-9 w-full px-3 bg-stone-50 border border-stone-200 rounded-md text-sm text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors" placeholder="일감 제목 검색..." />
        </div>
      </div>

      <!-- 하단 옵션 행 -->
      <div class="flex items-center justify-between mt-5 pt-4 border-t border-stone-100">
        <!-- 하위 프로젝트 체크박스 -->
        <label class="flex items-center gap-2 text-sm text-stone-500 cursor-pointer select-none">
          <input v-model="filters.includeSubProject" type="checkbox" class="hidden peer" />
          <span class="w-4 h-4 rounded border border-stone-300 bg-stone-50 flex items-center justify-center transition-colors shrink-0" :class="filters.includeSubProject ? 'bg-amber-600 border-amber-600' : ''">
            <svg v-if="filters.includeSubProject" class="w-2.5 h-2.5 text-amber-50" viewBox="0 0 10 10" fill="none">
              <polyline points="1.5,5 4,7.5 8.5,2.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </span>
          하위 프로젝트 일감 보기
        </label>

        <div class="flex gap-2.5">
          <button class="px-4 py-2 text-sm font-semibold text-stone-500 bg-stone-100 border border-stone-200 rounded-lg hover:bg-stone-200 transition-colors cursor-pointer" @click="resetFilters">초기화</button>
          <button class="px-5 py-2 text-sm font-bold text-amber-50 bg-amber-400 hover:bg-amber-600 rounded-lg shadow shadow-amber-200 transition-colors cursor-pointer border-none">조회</button>
        </div>
      </div>
    </div>

    <!-- ── 테이블 카드 ──────────────────────────────────────── -->
    <div class="bg-white border border-stone-200 rounded-xl shadow-sm overflow-hidden">
      <!-- 메타 -->
      <div class="px-6 py-3.5 border-b border-stone-100">
        <span class="text-sm text-stone-400">
          총 <strong class="text-stone-700 font-bold">{{ tasks.length }}</strong
          >개
        </span>
      </div>

      <!-- 표 -->
      <div class="overflow-x-auto">
        <table class="w-full border-collapse text-sm">
          <thead>
            <tr class="bg-stone-100 border-b border-stone-200">
              <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-50">번호</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">유형</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-28">상태</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">우선순위</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-24">범주</th>
              <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-stone-400 min-w-52">제목</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-50">진척도</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-28">담당자</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-stone-400 whitespace-nowrap w-30">마감일</th>
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
                {{ task.type }}
              </td>

              <!-- 상태 -->
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded-full text-sm font-semibold whitespace-nowrap" :class="workflowClass[task.workflow] ?? 'bg-stone-100 text-stone-500'">{{ task.workflow }}</span>
              </td>

              <!-- 우선순위 -->
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded-full text-sm font-semibold" :class="priorityClass[task.priority] ?? 'bg-stone-100 text-stone-400'">{{ task.priority }}</span>
              </td>

              <!-- 범주 -->
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block mt-1 text-sm text-stone-400 bg-stone-100 px-2 py-px rounded">
                  {{ task.category }}
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
                  <span class="text-base font-semibold text-stone-500 w-9 text-right shrink-0"> {{ task.progress }}% </span>
                </div>
              </td>

              <!-- 담당자 -->
              <td class="px-4 py-3.5 text-base text-stone-600 text-center">
                {{ task.userId }}
              </td>

              <!-- 마감일 -->
              <td class="px-4 py-3.5 text-base text-stone-500 tabular-nums text-center">
                {{ task.plannedEnd }}
              </td>
            </tr>

            <!-- 빈 상태 -->
            <tr v-if="pagedTasks.length === 0">
              <td colspan="8" class="text-center py-16 text-sm text-stone-400">일감이 없습니다.</td>
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
/* Tailwind로 처리하기 어려운 최소한의 보조 스타일만 */
select {
  appearance: auto;
}
input[type='date']::-webkit-calendar-picker-indicator {
  opacity: 0.5;
  cursor: pointer;
}

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

/* 상태 */
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
