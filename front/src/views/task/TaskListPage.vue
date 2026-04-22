<script setup>
import { useAuthStore } from '@/stores/auth';
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import DatePicker from 'primevue/datepicker';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const taskStore = useTaskStore();
const projectStore = useProjectStore();
const authStore = useAuthStore();
const router = useRouter();
const project = computed(() => projectStore.selectedProject);
const userId = computed(() => authStore.user.id ?? null);
const tasks = computed(() => taskStore.taskList);

// 드롭다운 옵션
const commonCodes = computed(() => taskStore.commonCodeList);
const workflowOptions = computed(() => commonCodes.value.filter((w) => w.cgroup === '0Q'));
const userOptions = computed(() => taskStore.memberList);
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const priorityOptions = computed(() => commonCodes.value.filter((p) => p.cgroup === '0S'));

// value → name 맵핑
const workflowMap = computed(() => Object.fromEntries(workflowOptions.value.map((w) => [w.id, w.name])));
const priorityMap = computed(() => Object.fromEntries(priorityOptions.value.map((p) => [p.id, p.name])));
const taskTypeMap = computed(() => Object.fromEntries(typeOptions.value.map((t) => [t.id, t.name])));
const taskCateMap = computed(() => Object.fromEntries(categoryOptions.value.map((c) => [c.id, c.name])));

const formatListDate = (ts) => {
  if (!ts) return '-';
  return new Date(ts).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

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
  showMyTasks: false
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
  appliedFilters.value = defaultFilters();
  currentPage.value = 1;
}

// 내 일감 보기 필터
watch(
  () => filters.value.showMyTasks,
  (val) => {
    appliedFilters.value.showMyTasks = val;
    currentPage.value = 1;
  }
);

// 정렬
const sortKey = ref('priority');
const sortDir = ref('desc');

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

// 트리 빌드 후 DFS 평탄화 (_depth, _children 포함)
const flattenedTasks = computed(() => {
  const map = {};
  tasks.value.forEach((t) => (map[t.id] = { ...t, _children: [] }));

  const roots = [];
  tasks.value.forEach((t) => {
    if (t.parentId && map[t.parentId]) {
      map[t.parentId]._children.push(map[t.id]);
    } else {
      roots.push(map[t.id]);
    }
  });

  // 형제 노드 정렬 함수
  function sortNodes(nodes) {
    const dir = sortDir.value === 'asc' ? 1 : -1;
    return [...nodes].sort((a, b) => {
      if (sortKey.value === 'priority') return ((priorityWeight[a.priority] ?? 0) - (priorityWeight[b.priority] ?? 0)) * dir;
      if (sortKey.value === 'progress') return (a.progress - b.progress) * dir;
      const va = a[sortKey.value] ?? '';
      const vb = b[sortKey.value] ?? '';
      return va < vb ? -dir : va > vb ? dir : 0;
    });
  }

  const result = [];
  function dfs(node, depth) {
    result.push({ ...node, _depth: depth });
    sortNodes(node._children).forEach((child) => dfs(child, depth + 1));
  }
  sortNodes(roots).forEach((r) => dfs(r, 0));
  return result;
});

// 필터 결과
const processedTasks = computed(() => {
  let list = flattenedTasks.value.filter((t) => t.isPublic === 'J1' || t.creator === userId.value || t.userId === userId.value);

  if (appliedFilters.value.showMyTasks) {
    list = list.filter((t) => t.userId === userId.value);
  }

  if (appliedFilters.value.workflow) list = list.filter((t) => t.workflow === appliedFilters.value.workflow);
  if (appliedFilters.value.userId) list = list.filter((t) => t.userName === appliedFilters.value.userId);
  if (appliedFilters.value.type) list = list.filter((t) => t.type === appliedFilters.value.type);
  if (appliedFilters.value.category) list = list.filter((t) => t.category === appliedFilters.value.category);
  if (appliedFilters.value.priority) list = list.filter((t) => t.priority === appliedFilters.value.priority);
  if (appliedFilters.value.title) list = list.filter((t) => t.title.includes(appliedFilters.value.title));

  // DatePicker가 Date 객체를 반환하므로 문자열로 변환 후 비교
  const startDate = appliedFilters.value.plannedStart ? new Date(appliedFilters.value.plannedStart) : null;
  const endDate = appliedFilters.value.plannedEnd ? new Date(appliedFilters.value.plannedEnd) : null;
  if (startDate) {
    startDate.setHours(0, 0, 0, 0);
  }
  if (endDate) {
    endDate.setHours(23, 59, 59, 999);
  }
  if (startDate) list = list.filter((t) => t.plannedEnd && new Date(t.plannedEnd) >= startDate);
  if (endDate) list = list.filter((t) => t.plannedEnd && new Date(t.plannedEnd) <= endDate);

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

// 일감 상세 이동
function goToTask(taskId) {
  router.push(`/task/${taskId}`);
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
  Q3: 'status-done',
  Q4: 'status-rejected'
};

const priorityClass = {
  S3: 'priority-urgent',
  S2: 'priority-high',
  S1: 'priority-mid',
  S0: 'priority-low'
};

const loading = ref(false);
onMounted(async () => {
  loading.value = true;
  await Promise.all([taskStore.findCateList(), taskStore.findTypeList(), taskStore.findMember(project.value.id), taskStore.findTaskList(project.value.id, userId.value), taskStore.findCommonCodeList()]);
  loading.value = false;
});
</script>

<template>
  <div class="min-h-screen bg-white px-10 py-8 text-stone-700">
    <!-- 페이지 헤더 -->
    <div class="flex items-end justify-between mb-7">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-stone-900">일감 목록</h1>
      </div>
      <Button icon="pi pi-plus" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" label="일감 추가" raised @click="router.push('/task/create')" />
    </div>

    <!-- 필터 카드 -->
    <div class="bg-[#F2F3F8] border border-[#C7C7C2] rounded-xl shadow-sm px-7 pt-5 pb-5 mb-5">
      <p class="text-xl font-bold tracking-wider uppercase text-stone-700 mb-4"><i class="pi pi-search" style="font-size: 1rem"></i> 검색 필터</p>

      <div class="grid grid-cols-4 gap-x-5 gap-y-4">
        <!-- 진행 상태 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">진행 상태</label>
          <Select v-model="filters.workflow" :options="workflowOptions" option-label="name" option-value="id" placeholder="전체" show-clear />
        </div>

        <!-- 담당자: Select + filter 옵션 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">담당자</label>
          <Select v-model="filters.userId" :options="userOptions" option-label="name" option-value="id" placeholder="이름 검색" filter filter-placeholder="이름 검색" show-clear>
            <template #option="data"> {{ data.option.name }} ({{ data.option.id }}) </template>
          </Select>
        </div>

        <!-- 마감일 범위 -->
        <div class="flex flex-col gap-1.5 col-span-2">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">마감일</label>
          <div class="flex items-center gap-2">
            <DatePicker v-model="filters.plannedStart" date-format="yy-mm-dd" :maxDate="filters.plannedEnd" placeholder="시작일" show-button-bar showIcon class="flex-1 min-w-0 w-full" input-class="w-full" />
            <span class="text-stone-500 text-base shrink-0">~</span>
            <DatePicker v-model="filters.plannedEnd" date-format="yy-mm-dd" :minDate="filters.plannedStart" placeholder="종료일" show-button-bar showIcon class="flex-1 min-w-0 w-full" input-class="w-full" />
          </div>
        </div>

        <!-- 일감 유형 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">일감 유형</label>
          <Select v-model="filters.type" :options="typeOptions" option-label="name" option-value="id" placeholder="전체" show-clear />
        </div>

        <!-- 일감 범주 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">일감 범주</label>
          <Select v-model="filters.category" :options="categoryOptions" option-label="name" option-value="id" placeholder="전체" show-clear />
        </div>

        <!-- 우선순위 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">우선순위</label>
          <Select v-model="filters.priority" :options="priorityOptions" option-label="name" option-value="id" placeholder="전체" show-clear />
        </div>

        <!-- 제목 -->
        <div class="flex flex-col gap-1.5 col-span-2">
          <label class="text-base font-semibold uppercase tracking-wider text-stone-500">제목</label>
          <InputText v-model="filters.title" placeholder="일감 제목 검색" class="w-full" />
        </div>
      </div>

      <div class="flex items-center justify-between mt-5 pt-4 border-t border-[#C7C7C2]">
        <div class="flex items-center gap-5">
          <!-- 내 일감 보기 -->
          <div class="flex items-center gap-2">
            <Checkbox v-model="filters.showMyTasks" :binary="true" inputId="showMyTasks" />
            <label for="showMyTasks" class="text-base text-stone-500 cursor-pointer select-none">내 일감 보기</label>
          </div>
        </div>

        <div class="flex gap-2.5">
          <Button class="px-4 py-2" severity="secondary" raised @click="resetFilters">초기화</Button>
          <Button class="px-5 py-2" raised @click="applyFilters">조회</Button>
        </div>
      </div>
    </div>

    <!-- 테이블 카드 -->
    <div class="bg-white border border-[#C7C7C2] rounded-xl shadow-sm overflow-hidden">
      <div class="px-6 py-3.5 border-b border-stone-100">
        <span class="text-lg text-stone-500">
          총 <strong class="text-stone-700 font-bold">{{ loading ? processedTasks.length : 0 }}</strong
          >개
        </span>
      </div>

      <div v-if="loading" class="flex justify-center items-center py-20 text-stone-500">
        <i class="pi pi-spin pi-spinner text-2xl mr-2"></i>
        데이터 불러오는 중...
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full border-collapse text-base">
          <thead>
            <tr class="bg-[#5B6E96] border-b border-stone-200">
              <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-50">
                <button class="sort-btn" :class="{ active: sortKey === 'id' }" @click="toggleSort('id')">
                  번호 <span class="sort-icon">{{ sortIcon('id') }}</span>
                </button>
              </th>
              <th class="px-4 py-3 text-left text-base font-bold uppercase tracking-wider text-[#FFFFFF] min-w-70">제목</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-24">유형</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-28">상태</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-24">
                <button class="sort-btn" :class="{ active: sortKey === 'priority' }" @click="toggleSort('priority')">
                  우선순위 <span class="sort-icon">{{ sortIcon('priority') }}</span>
                </button>
              </th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-24">범주</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-70">
                <button class="sort-btn" :class="{ active: sortKey === 'progress' }" @click="toggleSort('progress')">
                  진척도 <span class="sort-icon">{{ sortIcon('progress') }}</span>
                </button>
              </th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-45">담당자</th>
              <th class="px-4 py-3 text-center text-base font-bold uppercase tracking-wider text-[#FFFFFF] whitespace-nowrap w-36">
                <button class="sort-btn" :class="{ active: sortKey === 'plannedEnd' }" @click="toggleSort('plannedEnd')">
                  마감일 <span class="sort-icon">{{ sortIcon('plannedEnd') }}</span>
                </button>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(task, idx) in pagedTasks"
              :key="task.id"
              class="border-b border-stone-100 last:border-none hover:bg-amber-50 transition-colors duration-100 cursor-pointer"
              :class="idx % 2 !== 0 ? 'bg-stone-50/60' : 'bg-white'"
              @click="goToTask(task.id)"
            >
              <td class="px-4 py-3.5">
                <span class="font-mono text-m font-semibold bg-amber-100 text-amber-700 px-2 py-0.5 rounded">{{ task.id }}</span>
              </td>
              <td class="px-4 py-3.5">
                <span class="block text-base font-medium text-stone-800 hover:text-amber-600 transition-colors leading-snug" :style="{ paddingLeft: task._depth * 20 + 'px' }">
                  <span v-if="task._depth > 0" class="text-stone-500 mr-1">└</span>
                  {{ task.title }}
                </span>
              </td>
              <td class="px-4 py-3.5 text-base text-stone-600 text-center">{{ taskTypeMap[task.type] ?? task.type }}</td>
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold whitespace-nowrap" :class="workflowClass[task.workflow] ?? 'bg-stone-100 text-stone-500'">
                  {{ workflowMap[task.workflow] ?? task.workflow }}
                </span>
              </td>
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold" :class="priorityClass[task.priority] ?? 'bg-stone-100 text-stone-500'">
                  {{ priorityMap[task.priority] ?? task.priority }}
                </span>
              </td>
              <td class="px-4 py-3.5 text-center">
                <span class="inline-block mt-1 text-base text-stone-500 bg-stone-100 px-2 py-px rounded">{{ taskCateMap[task.category] ?? task.category }}</span>
              </td>
              <td class="px-4 py-3.5">
                <ProgressBar
                  :value="task.progress"
                  :pt="{
                    value: {
                      style: {
                        background: progressBarColor(task.progress)
                      }
                    }
                  }"
                ></ProgressBar>
              </td>
              <td class="px-4 py-3.5 text-base text-stone-600 text-center">{{ task.userName }}({{ task.userId }})</td>
              <td class="px-4 py-3.5 text-base text-stone-500 tabular-nums text-center">{{ formatListDate(task.plannedEnd) }}</td>
            </tr>
            <tr v-if="pagedTasks.length === 0">
              <td colspan="9" class="text-center py-16 text-base text-stone-500">일감이 없습니다.</td>
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
  </div>
</template>

<style scoped>
/*  정렬 버튼  */
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

.sort-btn.active {
  color: #ffffff;
}
.sort-icon {
  font-size: 0.75rem;
  opacity: 0.7;
}
.sort-btn.active .sort-icon {
  opacity: 1;
  color: #ffffff;
}

/*  우선순위  */
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

/*  상태  */
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
