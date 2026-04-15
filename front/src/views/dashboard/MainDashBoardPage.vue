<script setup>
// 메인 대시보드
import { useAuthStore } from '@/stores/auth';
import { useDashboardStore } from '@/stores/dashboard';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import FullCalendar from '@fullcalendar/vue3';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';

// ──────────────────────── 날짜 ────────────────────────
const now = new Date();
const todayLabel = now.toLocaleDateString('ko-KR', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
});

// ──────────────────────── 스토어 ────────────────────────
const userStore = useAuthStore();
const dashboardStore = useDashboardStore();
const user = computed(() => userStore.user);
const dashboard = computed(() => dashboardStore.mainDashboard);
const toast = useToast();

// ──────────────────────── 날짜 유틸 ────────────────────────
function formatDate(dateStr) {
  if (!dateStr) return '-';
  return String(dateStr).replaceAll('-', '.');
}

function formatShortDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${mm}.${dd}`;
}

function calcDday(dateStr) {
  if (!dateStr) return 0;
  const target = new Date(dateStr);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.ceil((target - today) / (1000 * 60 * 60 * 24));
}

function isToday(dateStr) {
  if (!dateStr) return false;
  const d = new Date(dateStr);
  const t = new Date();
  return d.getFullYear() === t.getFullYear() && d.getMonth() === t.getMonth() && d.getDate() === t.getDate();
}

// ──────────────────────── 반응형 데이터 ────────────────────────
const projects = computed(() => mapProjects(dashboard.value?.projectList).sort((a, b) => a.id.localeCompare(b.id)));
const myTasks = computed(() => mapTasks(dashboard.value?.taskList).sort((a, b) => a.id.localeCompare(b.id)));
const memos = computed(() => mapMemos(dashboard.value?.memoList).sort((a, b) => a.id.localeCompare(b.id)));

// ──────────────────────── 요약 통계 ────────────────────────
const summaryStats = computed(() => [
  { label: '참여 프로젝트', value: (dashboard.value?.projectList ?? []).length, icon: 'pi pi-briefcase' },
  { label: '담당 일감', value: (dashboard.value?.taskList ?? []).length, icon: 'pi pi-list' },
  {
    label: '오늘 마감',
    value: (dashboard.value?.taskList ?? []).filter((t) => isToday(t.plannedEnd)).length,
    icon: 'pi pi-clock'
  },
  {
    label: '완료된 일감',
    value: (dashboard.value?.taskList ?? []).filter((t) => t.workflow === '완료').length,
    icon: 'pi pi-check-circle'
  }
]);

// ──────────────────────── 일감 상태 ────────────────────────
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

const taskStatusCounts = computed(() =>
  statusOrder.map((s) => ({
    label: s,
    count: myTasks.value.filter((t) => t.status === s).length,
    cls: statusColorMap[s].cls
  }))
);

// ──────────────────────── 달력 ────────────────────────
const EVENT_COLORS = ['#F59E0B', '#FCD34D', '#D97706', '#92400E', '#78350F', '#FDE68A', '#B45309'];

// 일정 클릭 모달
const scheduleDialogVisible = ref(false);
const selectedSchedule = ref({ title: '', projectTitle: '', content: '', start: '', end: '' });

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locale: 'ko',
  height: 380,
  headerToolbar: { left: 'prev', center: 'title', right: 'next' },
  buttonText: { today: '오늘' },
  events: [],
  eventDisplay: 'block',
  dayMaxEvents: 2,
  fixedWeekCount: false,
  eventClick(info) {
    selectedSchedule.value = {
      title: info.event.title,
      projectTitle: info.event.extendedProps.projectTitle ?? '-',
      content: info.event.extendedProps.content ?? '',
      start: formatDate(info.event.startStr?.slice(0, 10)),
      end: formatDate(info.event.endStr?.slice(0, 10) || info.event.startStr?.slice(0, 10))
    };
    scheduleDialogVisible.value = true;
  }
});

// ──────────────────────── 메모 다이얼로그 ────────────────────────
const memoDialogVisible = ref(false);
const memoDialogMode = ref('view'); // 'view' | 'edit' | 'new'
const activeMemo = ref({ id: null, title: '', content: '' });

function openMemo(memo) {
  activeMemo.value = { ...memo };
  memoDialogMode.value = 'view';
  memoErrors.value = { title: '', content: '' };
  memoDialogVisible.value = true;
}
function openNewMemo() {
  activeMemo.value = { id: null, title: '', content: '' };
  memoDialogMode.value = 'new';
  memoErrors.value = { title: '', content: '' };
  memoDialogVisible.value = true;
}

const memoErrors = ref({ title: '', content: '' });

function validateMemo() {
  memoErrors.value.title = '';
  memoErrors.value.content = '';
  if (!activeMemo.value.title.trim()) {
    memoErrors.value.title = '제목을 입력해주세요.';
  } else if (activeMemo.value.title.length > 100) {
    memoErrors.value.title = '제목은 100자를 초과할 수 없습니다.';
  }
  if (activeMemo.value.content.length > 500) {
    memoErrors.value.content = '내용은 500자를 초과할 수 없습니다.';
  }
  return !memoErrors.value.title && !memoErrors.value.content;
}

async function saveMemo() {
  if (!validateMemo()) return;
  const payload = {
    id: activeMemo.value.id ?? null,
    userId: user.value?.id,
    projectId: dashboard.value?.projectId ?? null,
    title: activeMemo.value.title,
    content: activeMemo.value.content
  };

  if (activeMemo.value.id) {
    console.log(activeMemo);
    await dashboardStore.updateMemo(payload);
    const idx = memos.value.findIndex((m) => m.id === activeMemo.value.id);
    if (idx !== -1) memos.value[idx] = { ...memos.value[idx], ...activeMemo.value };
    toast.add({
      severity: 'success',
      summary: '메모 수정',
      detail: '메모를 수정하였습니다.',
      life: 3000,
      closable: false
    });
  } else {
    await dashboardStore.insertMemo(payload);
    toast.add({
      severity: 'success',
      summary: '새 메모 작성',
      detail: '새 메모를 작성하였습니다.',
      life: 3000,
      closable: false
    });
  }
  await dashboardStore.findMainDashboard(user.value?.id);
  memoDialogVisible.value = false;
}

async function deleteMemo(id) {
  await dashboardStore.deleteMemo(id);
  await dashboardStore.findMainDashboard(user.value?.id);
  toast.add({
    severity: 'success',
    summary: '메모 삭제',
    detail: '메모를 삭제하였습니다.',
    life: 3000,
    closable: false
  });
}

// ──────────────────────── DTO 매핑 ────────────────────────
function mapProjects(list) {
  return (list ?? []).map((p) => ({
    id: p.id,
    title: p.title,
    progress: p.process ?? 0,
    deadline: formatDate(p.endDate),
    dday: calcDday(p.endDate)
  }));
}

function mapTasks(list) {
  return (list ?? []).map((t) => ({
    id: t.id,
    title: t.title,
    project: t.projectTitle,
    status: t.workflow,
    progress: t.process ?? 0,
    deadline: formatDate(t.plannedEnd)
  }));
}

function mapMemos(list) {
  return (list ?? []).map((m) => ({
    id: m.id,
    title: m.title,
    content: m.content,
    date: formatShortDate(m.editedOn)
  }));
}

function mapScheduleEvents(list) {
  return (list ?? []).map((s, i) => ({
    id: s.id,
    title: s.title,
    start: s.startDate,
    end: s.endDate,
    color: EVENT_COLORS[i % EVENT_COLORS.length],
    extendedProps: {
      projectTitle: s.projectTitle,
      content: s.content
    }
  }));
}

// ──────────────────────── 마운트 ────────────────────────
const loading = ref(false);
onMounted(async () => {
  loading.value = true;
  await dashboardStore.findMainDashboard(user.value?.id);
  const d = dashboard.value;
  if (d) {
    calendarOptions.value = {
      ...calendarOptions.value,
      events: mapScheduleEvents(d.scheduleList)
    };
  }
  loading.value = false;
});
</script>

<template>
  <div v-if="loading" class="flex justify-center items-center py-20 text-stone-400">
    <i class="pi pi-spin pi-spinner text-2xl mr-2"></i>
    데이터 불러오는 중...
  </div>
  <div v-else class="min-h-screen bg-white p-5 flex flex-col gap-5">
    <div class="bg-[#F2F3F8] px-8 py-6 rounded-xl">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
        <div class="flex flex-col gap-2">
          <span class="text-amber-600 text-base font-semibold tracking-wide">
            {{ todayLabel }}
          </span>
          <span class="text-gray-700 text-3xl font-bold leading-tight">
            안녕하세요, <span class="text-amber-900">{{ user?.name }}</span> 님!
          </span>
        </div>

        <!-- 통계 4개 -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
          <div v-for="stat in summaryStats" :key="stat.label" class="flex flex-col items-center justify-center rounded-xl px-5 py-4 min-w-[110px]">
            <span class="text-gray-700 text-2xl font-bold">{{ stat.value }}</span>
            <span class="text-gray-700 text-base text-center mt-1 leading-tight">{{ stat.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 xl:grid-cols-2 gap-5 flex-1">
      <!-- 참여 프로젝트 현황 -->
      <Card class="rounded-2xl border-0 shadow-md overflow-hidden flex flex-col">
        <template #header>
          <div class="flex items-center gap-3 px-5 pt-3">
            <span class="text-gray-700 font-bold text-lg flex-1">참여 중인 프로젝트</span>
            <span>총 {{ projects.length }}개 프로젝트 참여 중</span>
          </div>
        </template>
        <template #content>
          <div class="flex flex-col gap-3 overflow-y-auto max-h-91 p-4 bg-white">
            <div v-for="proj in projects" :key="proj.id" class="flex flex-col gap-2 bg-white hover:bg-amber-100 transition-colors rounded-xl p-4 cursor-pointer shadow-sm">
              <div class="flex items-center justify-between gap-2">
                <span class="text-slate-800 font-semibold text-base truncate max-w-[200px]">{{ proj.title }}</span>
                <Tag :value="proj.dday === 0 ? 'D-day' : proj.dday > 0 ? `D-${proj.dday}` : `D+${Math.abs(proj.dday)}`" :severity="proj.dday <= 0 ? 'danger' : proj.dday <= 7 ? 'warning' : 'info'" />
              </div>
              <ProgressBar :value="proj.progress" />
              <div class="flex justify-between items-center">
                <span class="text-slate-500 text-base">마감 {{ proj.deadline }}</span>
                <span class="text-[#5B6E96] font-semibold text-base">{{ proj.progress }}%</span>
              </div>
            </div>

            <div v-if="projects.length === 0" class="text-center text-slate-400 text-base py-8">
              <i class="pi pi-briefcase text-3xl block mb-2 opacity-40" />
              참여 중인 프로젝트가 없습니다.
            </div>
          </div>
        </template>
      </Card>

      <!-- 나의 일감 현황 -->
      <Card class="rounded-2xl border-0 shadow-md overflow-hidden flex flex-col">
        <template #header>
          <div class="flex items-center gap-3 px-5 pt-3">
            <span class="text-gray-700 font-bold text-lg flex-1">나의 일감</span>
            <span>총 {{ myTasks.length }}개 일감 작업 중</span>
          </div>
        </template>
        <template #content>
          <div class="flex flex-col bg-white">
            <!-- 상태별 카운트 -->
            <div class="flex flex-wrap gap-2 px-4 pb-2">
              <div v-for="st in taskStatusCounts" :key="st.label" class="flex items-center gap-1.5 rounded-full px-3 py-1 text-base font-semibold" :class="st.cls">
                <span>{{ st.label }}</span>
                <span class="bg-white/50 rounded-full px-1.5 py-0.5 text-base">{{ st.count }}</span>
              </div>
            </div>

            <!-- 일감 목록 -->
            <div class="overflow-y-auto max-h-80 px-4 pb-4 flex flex-col gap-2">
              <div v-for="task in myTasks" :key="task.id" class="flex items-start gap-3 bg-white hover:bg-amber-100 transition-colors rounded-xl px-4 py-3 shadow-sm">
                <div class="flex-1 min-w-0">
                  <p class="text-slate-800 text-base font-semibold truncate">
                    {{ task.title }}
                    <span class="text-slate-400 font-normal text-base">({{ task.project }})</span>
                  </p>
                  <div class="flex items-center gap-2 mt-2">
                    <div class="flex-1">
                      <ProgressBar :value="task.progress" />
                    </div>
                    <span class="text-[#5B6E96] font-semibold text-base w-8 text-right shrink-0">{{ task.progress }}%</span>
                  </div>
                  <span class="text-slate-500 text-base mt-1 block">마감 {{ task.deadline }}</span>
                </div>
                <Tag :value="task.status" :severity="statusSeverity(task.status)" class="shrink-0 mt-0.5" />
              </div>

              <div v-if="myTasks.length === 0" class="text-center text-slate-400 text-base py-8">
                <i class="pi pi-list text-3xl block mb-2 opacity-40" />
                담당 일감이 없습니다.
              </div>
            </div>
          </div>
        </template>
      </Card>

      <!-- 메모 -->
      <Card class="rounded-2xl border-0 shadow-md overflow-hidden flex flex-col">
        <template #header>
          <div class="flex items-center gap-3 px-5 pt-3">
            <span class="text-gray-700 font-bold text-lg flex-1">메모</span>
            <Button label="메모 추가" size="small" raised @click="openNewMemo" />
          </div>
        </template>
        <template #content>
          <div class="overflow-y-auto max-h-100 p-4 flex flex-col gap-2 bg-white">
            <TransitionGroup name="memo-list">
              <div v-for="memo in memos" :key="memo.id" class="bg-amber-100 flex items-center justify-between gap-2 hover:bg-amber-400 transition-colors rounded-xl px-4 py-3 cursor-pointer group shadow-sm" @click="openMemo(memo)">
                <div class="flex items-center gap-2 min-w-0">
                  <i class="pi pi-file-edit text-[#5B6E96] text-base shrink-0" />
                  <span class="text-slate-700 text-base font-medium truncate">{{ memo.title }}</span>
                </div>
                <div class="flex items-center gap-2 shrink-0">
                  <span class="text-gray-700 text-base hidden group-hover:inline">{{ memo.date }}</span>
                  <Button icon="pi pi-times" text rounded severity="danger" size="small" class="w-7 h-7 p-0" @click.stop="deleteMemo(memo.id)" />
                </div>
              </div>
            </TransitionGroup>

            <div v-if="memos.length === 0" class="text-center text-slate-400 text-base py-8">
              <i class="pi pi-inbox text-3xl block mb-2 opacity-40" />
              메모가 없습니다.
            </div>
          </div>
        </template>
      </Card>

      <!-- 달력 -->
      <Card class="rounded-2xl border-0 shadow-md overflow-hidden flex flex-col">
        <template #content>
          <div class="bg-white dashboard-calendar h-110">
            <FullCalendar :options="calendarOptions" />
          </div>
        </template>
      </Card>
    </div>

    <!-- 메모 다이얼로그 -->
    <Dialog v-model:visible="memoDialogVisible" :header="memoDialogMode === 'new' ? '새 메모' : memoDialogMode === 'edit' ? '메모 수정' : '메모'" modal :style="{ width: '500px' }">
      <div class="flex flex-col gap-5 pt-2">
        <!-- view -->
        <template v-if="memoDialogMode === 'view'">
          <div class="flex flex-col gap-1">
            <label class="text-[#5B6E96] text-base font-bold">제목</label>
            <p class="text-slate-800 text-base font-semibold px-1">{{ activeMemo.title }}</p>
          </div>
          <div class="flex flex-col gap-1">
            <label class="text-[#5B6E96] text-base font-bold">내용</label>
            <p class="text-slate-700 text-base whitespace-pre-wrap leading-relaxed px-1 min-h-[80px]">
              {{ activeMemo.content || '내용 없음' }}
            </p>
          </div>
        </template>

        <!-- edit / new -->
        <template v-else>
          <div class="flex flex-col gap-1">
            <label class="text-[#5B6E96] text-base font-bold">제목</label>
            <InputText v-model="activeMemo.title" placeholder="메모 제목" class="w-full" :class="memoErrors.title ? 'p-invalid' : ''" />
            <div class="flex items-center justify-between mt-1">
              <small v-if="memoErrors.title" class="text-red-500 text-xs">
                {{ memoErrors.title }}
              </small>
              <small class="ml-auto text-xs" :class="activeMemo.title.length > 100 ? 'text-red-500 font-semibold' : 'text-slate-400'"> {{ activeMemo.title.length }} / 100 </small>
            </div>
          </div>
          <div class="flex flex-col gap-1">
            <label class="text-[#5B6E96] text-base font-bold">내용</label>
            <Textarea v-model="activeMemo.content" rows="7" placeholder="메모 내용을 입력하세요..." class="w-full resize-none" :class="memoErrors.content ? 'p-invalid' : ''" />
            <div class="flex items-center justify-between mt-1">
              <small v-if="memoErrors.content" class="text-red-500 text-xs">
                {{ memoErrors.content }}
              </small>
              <small class="ml-auto text-xs" :class="activeMemo.content.length > 500 ? 'text-red-500 font-semibold' : 'text-slate-400'"> {{ activeMemo.content.length }} / 500 </small>
            </div>
          </div>
        </template>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <Button v-if="memoDialogMode === 'view'" label="편집" raised @click="memoDialogMode = 'edit'" />
          <Button v-if="memoDialogMode !== 'view'" label="저장" raised @click="saveMemo" />
          <Button label="닫기" severity="secondary" raised @click="memoDialogVisible = false" />
        </div>
      </template>
    </Dialog>

    <!-- 일정 상세 다이얼로그 -->
    <Dialog v-model:visible="scheduleDialogVisible" header="일정 상세" modal :style="{ width: '480px' }">
      <div class="flex flex-col gap-5 pt-2">
        <div class="flex flex-col gap-1">
          <label class="text-[#5B6E96] text-base font-bold">프로젝트</label>
          <p class="text-slate-800 text-base font-semibold px-1">{{ selectedSchedule.projectTitle }}</p>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-[#5B6E96] text-base font-bold">일정 제목</label>
          <p class="text-slate-800 text-base font-semibold px-1">{{ selectedSchedule.title }}</p>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-[#5B6E96] text-base font-bold">내용</label>
          <p class="text-slate-700 text-base whitespace-pre-wrap leading-relaxed px-1 min-h-[60px]">
            {{ selectedSchedule.content || '내용 없음' }}
          </p>
        </div>
        <div class="flex gap-6">
          <div class="flex flex-col gap-1">
            <label class="text-[#5B6E96] text-base font-bold">시작일</label>
            <p class="text-slate-800 text-base px-1">{{ selectedSchedule.start }}</p>
          </div>
          <div class="flex flex-col gap-1">
            <label class="text-[#5B6E96] text-base font-bold">종료일</label>
            <p class="text-slate-800 text-base px-1">{{ selectedSchedule.end }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end">
          <Button label="닫기" severity="secondary" raised @click="scheduleDialogVisible = false" />
        </div>
      </template>
    </Dialog>
  </div>
</template>

<style>
.dashboard-calendar .fc-toolbar-title {
  font-size: 1rem !important;
  font-weight: 700;
  color: #5b6e96;
}
.dashboard-calendar .fc-button {
  background: #dde3f0 !important;
  border: none !important;
  color: #5b6e96 !important;
  font-size: 0.875rem !important;
  padding: 3px 10px !important;
  border-radius: 8px !important;
  box-shadow: none !important;
}
.dashboard-calendar .fc-button:hover {
  background: #5b6e96 !important;
  color: #dde3f0 !important;
}
.dashboard-calendar .fc-daygrid-day-number,
.dashboard-calendar .fc-col-header-cell-cushion {
  font-size: 0.8rem;
  color: #5b6e96;
  text-decoration: none !important;
  font-weight: 600;
}
.dashboard-calendar .fc-col-header-cell {
  background: #dde3f0;
}
.dashboard-calendar .fc-daygrid-day.fc-day-today {
  background: #f2f0eb !important;
}
.dashboard-calendar .fc-daygrid-day.fc-day-today .fc-daygrid-day-number {
  color: #b45309 !important;
  font-weight: 700;
}
.dashboard-calendar .fc-event {
  border-radius: 4px !important;
  font-size: 0.75rem !important;
  padding: 1px 5px !important;
  cursor: pointer;
}
.dashboard-calendar .fc-daygrid-event-harness {
  margin-top: 2px;
}
.dashboard-calendar .fc-scrollgrid {
  border-color: #dde3f0 !important;
}
.dashboard-calendar td,
.dashboard-calendar th {
  border-color: #dde3f0 !important;
}
.fc-day-sun .fc-daygrid-day-number {
  color: #ef4444 !important;
}
.fc-day-sat .fc-daygrid-day-number {
  color: #3b82f6 !important;
}
</style>
