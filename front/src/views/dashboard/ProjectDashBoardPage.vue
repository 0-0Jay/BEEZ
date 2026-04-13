<script setup>
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import FullCalendar from '@fullcalendar/vue3';
import { computed, ref } from 'vue';

// ──────────────────────── 유틸 ────────────────────────
function calcDday(dateStr) {
  const target = new Date(dateStr);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.ceil((target - today) / (1000 * 60 * 60 * 24));
}

// ──────────────────────── 현재 프로젝트 ────────────────────────
// TODO: 실제로는 route params 또는 store에서 주입
const currentProject = ref({
  title: '사내 프로젝트 관리 시스템 개편',
  status: '진행중',
  pm: '김철수',
  startDate: '2025.01.06',
  endDate: '2025.07.31',
  progress: 72,
  dday: calcDday('2025-07-31'),
  totalTasks: 18,
  doneTasks: 11,
  inProgressTasks: 5
});

// 프로젝트 상태 배지 색상
function projectStatusCls(status) {
  const map = {
    진행중: 'bg-blue-100 text-blue-700',
    닫힘: 'bg-slate-200 text-slate-600',
    완료: 'bg-emerald-100 text-emerald-700',
    보류: 'bg-yellow-100 text-yellow-700'
  };
  return map[status] ?? 'bg-slate-200 text-slate-600';
}

// 상단 통계 3개
const projectStats = computed(() => [
  { label: '총 일감', value: currentProject.value.totalTasks, icon: 'pi pi-list' },
  { label: '완료된 일감', value: currentProject.value.doneTasks, icon: 'pi pi-check-circle' },
  { label: '진행중 일감', value: currentProject.value.inProgressTasks, icon: 'pi pi-spinner' }
]);

// ──────────────────────── 일감 공통 ────────────────────────
const statusOrder = ['신규', '실행', '해결', '완료', '반려'];
const statusColorMap = {
  신규: { cls: 'bg-slate-200 text-slate-700', severity: 'secondary' },
  실행: { cls: 'bg-blue-100 text-blue-700', severity: 'info' },
  해결: { cls: 'bg-violet-100 text-violet-700', severity: 'help' },
  완료: { cls: 'bg-emerald-100 text-emerald-700', severity: 'success' },
  반려: { cls: 'bg-red-100 text-red-700', severity: 'danger' }
};
function statusSeverity(s) {
  return statusColorMap[s]?.severity ?? 'secondary';
}

// ──────────────────────── 전체 일감 ────────────────────────
const allTasks = ref([
  { id: 1, title: 'Gantt 차트 반응형 구현', assignee: '홍길동', status: '실행', progress: 60, deadline: '2025.04.25' },
  { id: 2, title: '일감 보고서 페이지 개발', assignee: '홍길동', status: '실행', progress: 40, deadline: '2025.05.01' },
  { id: 3, title: 'WebSocket 알림 버그 수정', assignee: '홍길동', status: '해결', progress: 100, deadline: '2025.04.15' },
  { id: 4, title: 'Oracle JOIN 쿼리 최적화', assignee: '홍길동', status: '완료', progress: 100, deadline: '2025.04.10' },
  { id: 5, title: 'API 명세서 작성', assignee: '김영희', status: '신규', progress: 0, deadline: '2025.05.10' },
  { id: 6, title: '로그인 페이지 UI 개선', assignee: '김영희', status: '실행', progress: 75, deadline: '2025.04.28' },
  { id: 7, title: '알림 센터 컴포넌트', assignee: '이민준', status: '신규', progress: 0, deadline: '2025.05.15' },
  { id: 8, title: '권한 관리 기능 리팩토링', assignee: '이민준', status: '반려', progress: 30, deadline: '2025.04.20' }
]);

const allTaskStatusCounts = computed(() =>
  statusOrder.map((s) => ({
    label: s,
    count: allTasks.value.filter((t) => t.status === s).length,
    cls: statusColorMap[s].cls
  }))
);

// ──────────────────────── 나의 일감 ────────────────────────
const myTasks = ref([
  { id: 1, title: 'Gantt 차트 반응형 구현', project: '사내 PM 개편', status: '실행', progress: 60, deadline: '2025.04.25' },
  { id: 2, title: '일감 보고서 페이지 개발', project: '사내 PM 개편', status: '실행', progress: 40, deadline: '2025.05.01' },
  { id: 3, title: 'WebSocket 알림 버그 수정', project: '사내 PM 개편', status: '해결', progress: 100, deadline: '2025.04.15' },
  { id: 4, title: 'Oracle JOIN 쿼리 최적화', project: '데이터 마이그레이션', status: '완료', progress: 100, deadline: '2025.04.10' },
  { id: 5, title: 'API 명세서 작성', project: 'ERP 연동', status: '신규', progress: 0, deadline: '2025.05.10' }
]);

const myTaskStatusCounts = computed(() =>
  statusOrder.map((s) => ({
    label: s,
    count: myTasks.value.filter((t) => t.status === s).length,
    cls: statusColorMap[s].cls
  }))
);

// ──────────────────────── 메모 ────────────────────────
const now = new Date();
let memoIdSeq = ref(3);
const memos = ref([
  { id: 1, title: 'FullCalendar 이벤트 색상 커스텀', content: 'eventColor 또는 backgroundColor/borderColor로 per-event 색상 지정 가능.\ntheme 옵션 확인 필요.', date: '04.10' },
  { id: 2, title: 'PrimeVue DataTable 정렬 버그', content: '다중 컬럼 정렬 시 removableSort 옵션 활성화 필요. sortMode="multiple" 설정 확인.', date: '04.11' },
  { id: 3, title: 'Spring @Transactional 주의사항', content: '동일 클래스 내 self-invocation 은 프록시를 거치지 않아 트랜잭션이 적용되지 않음.', date: '04.12' }
]);

const memoDialogVisible = ref(false);
const memoDialogMode = ref('view');
const activeMemo = ref({ id: null, title: '', content: '', date: '' });

function openMemo(memo) {
  activeMemo.value = { ...memo };
  memoDialogMode.value = 'view';
  memoDialogVisible.value = true;
}
function openNewMemo() {
  activeMemo.value = { id: null, title: '', content: '', date: '' };
  memoDialogMode.value = 'new';
  memoDialogVisible.value = true;
}
function saveMemo() {
  const today = now.toLocaleDateString('ko-KR', { month: '2-digit', day: '2-digit' }).replace('. ', '.').replace('.', '');
  if (activeMemo.value.id) {
    const idx = memos.value.findIndex((m) => m.id === activeMemo.value.id);
    if (idx !== -1) memos.value[idx] = { ...activeMemo.value };
  } else {
    memos.value.push({ ...activeMemo.value, id: ++memoIdSeq.value, date: today });
  }
  memoDialogVisible.value = false;
}
function deleteMemo(id) {
  memos.value = memos.value.filter((m) => m.id !== id);
}

// ──────────────────────── 달력 ────────────────────────
const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locale: 'ko',
  height: 280,
  headerToolbar: { left: 'prev', center: 'title', right: 'next' },
  buttonText: { today: '오늘' },
  events: [
    { title: 'Gantt 개발', start: '2025-04-14', end: '2025-04-18', color: '#6366f1' },
    { title: '일감 보고서', start: '2025-04-16', end: '2025-04-20', color: '#10b981' },
    { title: '스프린트 회의', start: '2025-04-17', allDay: true, color: '#f59e0b' },
    { title: 'API 설계', start: '2025-04-21', end: '2025-04-25', color: '#6366f1' },
    { title: '코드 리뷰', start: '2025-04-24', allDay: true, color: '#ef4444' },
    { title: '릴리즈 준비', start: '2025-04-28', end: '2025-05-02', color: '#8b5cf6' }
  ],
  eventDisplay: 'block',
  dayMaxEvents: 2,
  fixedWeekCount: false
});
</script>

<template>
  <div class="min-h-screen bg-slate-50 p-5 flex flex-col gap-5">
    <!-- ──────────────────────────────────────────────
         상단 프로젝트 정보 카드
    ────────────────────────────────────────────── -->
    <Card class="rounded-2xl border-0 shadow-md overflow-hidden">
      <template #content>
        <div class="bg-gradient-to-r from-indigo-600 via-indigo-500 to-blue-500 px-8 py-6 rounded-xl">
          <!-- 상단: 프로젝트명 + 상태 배지 + PM + 기간 -->
          <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4 mb-5">
            <div class="flex flex-col gap-1.5">
              <div class="flex items-center gap-3 flex-wrap">
                <h1 class="text-white text-2xl font-bold leading-tight">
                  {{ currentProject.title }}
                </h1>
                <span class="text-xs font-semibold px-3 py-1 rounded-full" :class="projectStatusCls(currentProject.status)">
                  {{ currentProject.status }}
                </span>
              </div>
              <div class="flex items-center gap-4 flex-wrap mt-1">
                <span class="text-indigo-200 text-sm"> <i class="pi pi-user mr-1.5" />PM : {{ currentProject.pm }} </span>
                <span class="text-indigo-200 text-sm"> <i class="pi pi-calendar mr-1.5" />{{ currentProject.startDate }} ~ {{ currentProject.endDate }} </span>
              </div>
            </div>

            <!-- D-Day 배지 -->
            <div class="flex flex-col items-center justify-center bg-white/15 backdrop-blur-sm rounded-2xl px-6 py-4 shrink-0 min-w-[96px]">
              <span class="text-indigo-100 text-xs mb-1">마감 D-Day</span>
              <span class="text-2xl font-bold" :class="currentProject.dday <= 0 ? 'text-red-300' : currentProject.dday <= 7 ? 'text-yellow-300' : 'text-white'">
                {{ currentProject.dday === 0 ? 'D-day' : currentProject.dday > 0 ? `D-${currentProject.dday}` : `D+${Math.abs(currentProject.dday)}` }}
              </span>
            </div>
          </div>

          <!-- 진척도 바 -->
          <div class="flex items-center gap-3 mb-5">
            <span class="text-indigo-100 text-xs w-14 shrink-0">진척도</span>
            <div class="flex-1 bg-white/20 rounded-full h-2.5 overflow-hidden">
              <div class="h-full bg-white rounded-full transition-all" :style="{ width: currentProject.progress + '%' }" />
            </div>
            <span class="text-white font-bold text-sm w-10 text-right">{{ currentProject.progress }}%</span>
          </div>

          <!-- 통계 3개 -->
          <div class="grid grid-cols-3 gap-3">
            <div v-for="stat in projectStats" :key="stat.label" class="flex flex-col items-center justify-center bg-white/15 backdrop-blur-sm rounded-xl px-4 py-3">
              <i :class="[stat.icon, 'text-white text-lg mb-1']" />
              <span class="text-white text-xl font-bold">{{ stat.value }}</span>
              <span class="text-indigo-100 text-xs text-center mt-0.5">{{ stat.label }}</span>
            </div>
          </div>
        </div>
      </template>
    </Card>

    <!-- ──────────────────────────────────────────────
         2 × 2 그리드
    ────────────────────────────────────────────── -->
    <div class="grid grid-cols-1 xl:grid-cols-2 gap-5 flex-1">
      <!-- ① 전체 일감 현황 -->
      <Card class="rounded-2xl border-0 shadow-md flex flex-col">
        <template #header>
          <div class="flex items-center gap-2 px-6 pt-5 pb-0">
            <span class="w-1 h-5 bg-indigo-500 rounded-full inline-block" />
            <h2 class="text-slate-700 font-bold text-base">전체 일감 현황</h2>
          </div>
        </template>
        <template #content>
          <!-- 상태별 카운트 뱃지 -->
          <div class="flex flex-wrap gap-2 mb-3 px-2">
            <div v-for="st in allTaskStatusCounts" :key="st.label" class="flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold" :class="st.cls">
              <span>{{ st.label }}</span>
              <span class="bg-white/40 rounded-full px-1.5 py-0.5">{{ st.count }}</span>
            </div>
          </div>

          <!-- 일감 목록 -->
          <div class="overflow-y-auto max-h-52 px-2 flex flex-col gap-2">
            <div v-for="task in allTasks" :key="task.id" class="flex items-center gap-3 bg-slate-50 hover:bg-indigo-50 transition-colors rounded-xl px-4 py-3">
              <div class="flex-1 min-w-0">
                <p class="text-slate-800 text-sm font-medium truncate">
                  {{ task.title }}
                  <span class="text-slate-400 font-normal text-xs">({{ task.assignee }})</span>
                </p>
                <div class="flex items-center gap-2 mt-1">
                  <ProgressBar :value="task.progress" :style="{ height: '5px', width: '80px' }" />
                  <span class="text-slate-400 text-xs">{{ task.progress }}%</span>
                  <span class="text-slate-400 text-xs ml-auto">{{ task.deadline }}</span>
                </div>
              </div>
              <Tag :value="task.status" :severity="statusSeverity(task.status)" class="text-xs shrink-0" />
            </div>
          </div>
        </template>
      </Card>

      <!-- ② 나의 일감 현황 -->
      <Card class="rounded-2xl border-0 shadow-md flex flex-col">
        <template #header>
          <div class="flex items-center gap-2 px-6 pt-5 pb-0">
            <span class="w-1 h-5 bg-emerald-500 rounded-full inline-block" />
            <h2 class="text-slate-700 font-bold text-base">나의 일감 현황</h2>
          </div>
        </template>
        <template #content>
          <div class="flex flex-wrap gap-2 mb-3 px-2">
            <div v-for="st in myTaskStatusCounts" :key="st.label" class="flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold" :class="st.cls">
              <span>{{ st.label }}</span>
              <span class="bg-white/40 rounded-full px-1.5 py-0.5">{{ st.count }}</span>
            </div>
          </div>

          <div class="overflow-y-auto max-h-52 px-2 flex flex-col gap-2">
            <div v-for="task in myTasks" :key="task.id" class="flex items-center gap-3 bg-slate-50 hover:bg-emerald-50 transition-colors rounded-xl px-4 py-3">
              <div class="flex-1 min-w-0">
                <p class="text-slate-800 text-sm font-medium truncate">
                  {{ task.title }}
                  <span class="text-slate-400 font-normal text-xs">({{ task.project }})</span>
                </p>
                <div class="flex items-center gap-2 mt-1">
                  <ProgressBar :value="task.progress" :style="{ height: '5px', width: '80px' }" />
                  <span class="text-slate-400 text-xs">{{ task.progress }}%</span>
                  <span class="text-slate-400 text-xs ml-auto">{{ task.deadline }}</span>
                </div>
              </div>
              <Tag :value="task.status" :severity="statusSeverity(task.status)" class="text-xs shrink-0" />
            </div>
          </div>
        </template>
      </Card>

      <!-- ③ 메모 -->
      <Card class="rounded-2xl border-0 shadow-md flex flex-col">
        <template #header>
          <div class="flex items-center gap-2 px-6 pt-5 pb-0">
            <span class="w-1 h-5 bg-amber-400 rounded-full inline-block" />
            <h2 class="text-slate-700 font-bold text-base">메모</h2>
            <Button icon="pi pi-plus" label="메모 추가" size="small" severity="secondary" class="ml-auto text-xs py-1 px-3" @click="openNewMemo" />
          </div>
        </template>
        <template #content>
          <div class="overflow-y-auto max-h-64 px-2 flex flex-col gap-2">
            <TransitionGroup name="memo-list">
              <div v-for="memo in memos" :key="memo.id" class="flex items-center justify-between gap-2 bg-amber-50 hover:bg-amber-100 transition-colors rounded-xl px-4 py-3 cursor-pointer group" @click="openMemo(memo)">
                <div class="flex items-center gap-2 min-w-0">
                  <i class="pi pi-file text-amber-400 text-sm shrink-0" />
                  <span class="text-slate-700 text-sm font-medium truncate">{{ memo.title }}</span>
                </div>
                <div class="flex items-center gap-2 shrink-0">
                  <span class="text-slate-400 text-xs hidden group-hover:inline">{{ memo.date }}</span>
                  <Button icon="pi pi-times" text rounded severity="danger" size="small" class="w-6 h-6 p-0" @click.stop="deleteMemo(memo.id)" />
                </div>
              </div>
            </TransitionGroup>
            <div v-if="memos.length === 0" class="text-center text-slate-400 text-sm py-8">
              <i class="pi pi-inbox text-3xl block mb-2 opacity-40" />
              메모가 없습니다.
            </div>
          </div>
        </template>
      </Card>

      <!-- ④ 달력 -->
      <Card class="rounded-2xl border-0 shadow-md flex flex-col">
        <template #header>
          <div class="flex items-center gap-2 px-6 pt-5 pb-0">
            <span class="w-1 h-5 bg-rose-400 rounded-full inline-block" />
            <h2 class="text-slate-700 font-bold text-base">내 일정</h2>
          </div>
        </template>
        <template #content>
          <div class="px-1 overflow-hidden dashboard-calendar">
            <FullCalendar :options="calendarOptions" />
          </div>
        </template>
      </Card>
    </div>

    <!-- ──────────────────────────────────────────────
         메모 다이얼로그
    ────────────────────────────────────────────── -->
    <Dialog v-model:visible="memoDialogVisible" :header="memoDialogMode === 'new' ? '새 메모' : '메모 보기'" modal :style="{ width: '480px' }" class="rounded-2xl">
      <div class="flex flex-col gap-4 pt-2">
        <div class="flex flex-col gap-1">
          <label class="text-slate-500 text-xs font-semibold uppercase tracking-wide">제목</label>
          <InputText v-model="activeMemo.title" placeholder="메모 제목" :readonly="memoDialogMode === 'view'" class="w-full" />
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-slate-500 text-xs font-semibold uppercase tracking-wide">내용</label>
          <Textarea v-model="activeMemo.content" rows="6" placeholder="메모 내용을 입력하세요..." :readonly="memoDialogMode === 'view'" class="w-full resize-none" />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <Button v-if="memoDialogMode === 'view'" label="편집" icon="pi pi-pencil" severity="secondary" @click="memoDialogMode = 'edit'" />
          <Button v-if="memoDialogMode !== 'view'" label="저장" icon="pi pi-check" @click="saveMemo" />
          <Button label="닫기" severity="secondary" text @click="memoDialogVisible = false" />
        </div>
      </template>
    </Dialog>
  </div>
</template>

<style>
.dashboard-calendar .fc-toolbar-title {
  font-size: 0.9rem !important;
  font-weight: 700;
  color: #374151;
}
.dashboard-calendar .fc-button {
  background: #f1f5f9 !important;
  border: none !important;
  color: #64748b !important;
  font-size: 0.75rem !important;
  padding: 2px 8px !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}
.dashboard-calendar .fc-button:hover {
  background: #e2e8f0 !important;
}
.dashboard-calendar .fc-daygrid-day-number,
.dashboard-calendar .fc-col-header-cell-cushion {
  font-size: 0.72rem;
  color: #6b7280;
  text-decoration: none !important;
}
.dashboard-calendar .fc-daygrid-day.fc-day-today {
  background: #eff6ff !important;
}
.dashboard-calendar .fc-event {
  border-radius: 4px !important;
  font-size: 0.65rem !important;
  padding: 1px 4px !important;
}
.dashboard-calendar .fc-daygrid-event-harness {
  margin-top: 1px;
}

.memo-list-enter-active,
.memo-list-leave-active {
  transition: all 0.25s ease;
}
.memo-list-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}
.memo-list-leave-to {
  opacity: 0;
  transform: translateX(16px);
}
</style>
