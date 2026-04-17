<script setup>
import ScheduleAddModal from '@/components/calendar/ScheduleAddModal.vue';
import ScheduleDetailModal from '@/components/calendar/ScheduleDetailModal.vue';
import { useAuthStore } from '@/stores/auth';
import { useCalendarStore } from '@/stores/calendar';
import { useProjectStore } from '@/stores/project';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import FullCalendar from '@fullcalendar/vue3';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';

const calendarStore = useCalendarStore();
const authStore = useAuthStore();
const projectStore = useProjectStore();
const toast = useToast();

const project = computed(() => projectStore.selectedProject);
const userId = computed(() => authStore.user?.id);
const role = computed(() => authStore.user?.role);

// ── 관리자 여부 ──────────────────────────────────────
const isAdmin = computed(() => role.value === 'ROLE0001' || role.value === 'ROLE0002');

// ── 색상 ────────────────────────────────────────────
const MY_COLOR = '#FCD34D';
const TEAM_COLOR = '#D97706';

// ── 필터 ────────────────────────────────────────────
const filters = ref({ mine: true, team: true });
const searchQuery = ref('');

// ── 유틸 ────────────────────────────────────────────
function formatDate(d) {
  if (!d) return '';
  // 문자열이면 앞 10자리(YYYY-MM-DD)만 사용
  if (typeof d === 'string') return d.substring(0, 10);
  // 숫자(타임스탬프)이면 Date로 변환
  const date = typeof d === 'number' ? new Date(d) : d;
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

const addOneDay = (dateStr) => {
  if (!dateStr) return undefined;
  const d = new Date(dateStr);
  d.setDate(d.getDate() + 1);
  return d.toISOString().split('T')[0];
};

const subtractOneDay = (dateStr) => {
  if (!dateStr) return undefined;
  const d = new Date(dateStr);
  d.setDate(d.getDate() - 1);
  return d.toISOString().split('T')[0];
};

// ── 이벤트 데이터 ────────────────────────────────────
const allEvents = computed(() =>
  calendarStore.scheduleList.map((ev) => {
    const isTeam = ev.type === 'D1';
    const isOwn = String(ev.userId) === String(userId.value);

    return {
      ...ev,
      start: ev.startDate,
      end: ev.endDate ? addOneDay(ev.endDate) : addOneDay(ev.startDate),
      backgroundColor: isTeam ? TEAM_COLOR : MY_COLOR,
      borderColor: isTeam ? TEAM_COLOR : MY_COLOR,
      // 관리자는 모든 일정 드래그 가능, 일반 유저는 자기 일정만 드래그 가능
      editable: isAdmin.value ? true : isOwn
    };
  })
);

// ── 필터된 이벤트 ────────────────────────────────────
const filteredEvents = computed(() =>
  allEvents.value.filter((ev) => {
    if (ev.type === 'D0' && !filters.value.mine) return false;
    if (ev.type === 'D1' && !filters.value.team) return false;
    if (searchQuery.value.trim()) return ev.title.toLowerCase().includes(searchQuery.value.trim().toLowerCase());
    return true;
  })
);

// ── 모달 ─────────────────────────────────────────────
const detailVisible = ref(false);
const addVisible = ref(false);
const selectedEvent = ref(null);

const allTypeOptions = [
  { label: '내 일정', value: 'D0' },
  { label: '팀 일정', value: 'D1' }
];

// 관리자만 팀 일정 유형 선택 가능
const typeOptions = computed(() => (isAdmin.value ? allTypeOptions : allTypeOptions.filter((o) => o.value === 'D0')));

const defaultNew = () => ({
  title: '',
  start: null,
  end: null,
  type: 'D0',
  content: '',
  userId: userId?.value,
  projectId: project.value?.id
});
const newEvent = ref(defaultNew());

const openAddModal = () => {
  newEvent.value = defaultNew();
  addVisible.value = true;
};

const saveNewEvent = async () => {
  if (!newEvent.value.title.trim() || !newEvent.value.start) return;
  const data = {
    title: newEvent.value.title,
    startDate: formatDate(newEvent.value.start),
    endDate: formatDate(newEvent.value.end),
    type: newEvent.value.type,
    content: newEvent.value.content,
    userId: newEvent.value.userId,
    projectId: newEvent.value.projectId
  };

  await calendarStore.insertSchedule(data);
  await calendarStore.findScheduleList(project.value?.id, userId?.value);
  addVisible.value = false;
  toast.add({
    severity: 'success',
    summary: '일정 추가',
    detail: '일정을 추가하였습니다.',
    life: 3000,
    closable: false
  });
};

const deleteEvent = async () => {
  await calendarStore.deleteSchedule(selectedEvent.value.id);
  await calendarStore.findScheduleList(project.value?.id, userId?.value);
  detailVisible.value = false;
  toast.add({
    severity: 'success',
    summary: '일정 삭제',
    detail: '일정을 삭제하였습니다.',
    life: 3000,
    closable: false
  });
};

const updateEvent = async (updated) => {
  const data = {
    id: updated.id,
    title: updated.title,
    startDate: updated.start,
    endDate: updated.end,
    type: updated.type,
    content: updated.content,
    userId: updated.userId,
    projectId: updated.projectId
  };

  await calendarStore.updateSchedule(data);
  await calendarStore.findScheduleList(project.value?.id, userId?.value);
  const original = calendarStore.scheduleList.find((s) => String(s.id) === String(updated.id));
  toast.add({
    severity: 'success',
    summary: '일정 변경',
    detail: '일정을 변경하였습니다.',
    life: 3000,
    closable: false
  });
  if (original) {
    selectedEvent.value = {
      id: original.id,
      title: original.title,
      startStr: original.startDate,
      endStr: original.endDate,
      backgroundColor: original.type === 'D0' ? MY_COLOR : TEAM_COLOR,
      extendedProps: {
        type: original.type,
        content: original.content,
        userId: original.userId,
        projectId: original.projectId
      }
    };
  }
};

// ── FullCalendar 옵션 ────────────────────────────────
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locale: 'ko',
  headerToolbar: { left: 'prev', center: 'title', right: 'next' },
  events: filteredEvents.value,
  editable: true, // 개별 이벤트의 editable 필드가 우선 적용됨
  droppable: true,
  dayMaxEvents: 3,
  eventDisplay: 'block',
  fixedWeekCount: false,
  contentHeight: 500,

  eventClick(info) {
    const original = calendarStore.scheduleList.find((s) => String(s.id) === String(info.event.id));
    selectedEvent.value = {
      id: info.event.id,
      title: info.event.title,
      startStr: info.event.startStr,
      endStr: original.endDate,
      backgroundColor: info.event.backgroundColor,
      extendedProps: info.event.extendedProps
    };
    detailVisible.value = true;
  },

  async eventDrop(info) {
    const isTeam = info.event.extendedProps.type === 'D1';
    const isOwn = String(info.event.extendedProps.userId) === String(userId.value);

    // 권한 외 드래그 시도 방어 (editable 설정만으로 충분하지만 서버 요청 이중 차단)
    if (isTeam && !isAdmin.value) {
      info.revert();
      return;
    }
    if (!isOwn && !isAdmin.value) {
      info.revert();
      return;
    }

    const data = {
      id: info.event.id,
      title: info.event.title,
      startDate: info.event.startStr,
      endDate: info.event.endStr ? subtractOneDay(info.event.endStr) : info.event.startStr,
      type: info.event.extendedProps.type,
      content: info.event.extendedProps.content,
      userId: info.event.extendedProps.userId,
      projectId: info.event.extendedProps.projectId
    };

    try {
      await calendarStore.updateSchedule(data);
      await calendarStore.findScheduleList(project.value?.id, userId?.value);
      toast.add({
        severity: 'success',
        summary: '일정 이동',
        detail: '일정이 이동되었습니다.',
        life: 3000,
        closable: false
      });
    } catch {
      info.revert();
    }
  }
}));

onMounted(async () => {
  await calendarStore.findScheduleList(project.value?.id, userId?.value);
});
</script>

<template>
  <div class="h-full flex flex-col bg-gray-50 px-10 py-8 font-sans overflow-hidden">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900 tracking-tight">달력</h1>
    </div>

    <div class="flex flex-wrap items-center gap-3 mb-5 px-5 py-3.5 bg-white border border-gray-200 rounded-xl shadow-sm">
      <ToggleButton v-model="filters.mine" onLabel="내 일정" offLabel="내 일정" onIcon="pi pi-user" offIcon="pi pi-user" class="filter-toggle mine-toggle !text-sm !font-medium" />
      <ToggleButton v-model="filters.team" onLabel="팀 일정" offLabel="팀 일정" onIcon="pi pi-users" offIcon="pi pi-users" class="filter-toggle team-toggle !text-sm !font-medium" />

      <Divider layout="vertical" class="!mx-1 !h-6" />

      <div class="flex items-center gap-1 flex-1 min-w-[180px] max-w-xs">
        <IconField class="w-full">
          <InputIcon class="pi pi-search" />
          <InputText v-model="searchQuery" placeholder="일정 검색" class="w-full !text-sm" size="small" />
        </IconField>
        <Button v-if="searchQuery" icon="pi pi-times" severity="secondary" text rounded size="small" @click="searchQuery = ''" />
      </div>

      <div class="ml-auto">
        <Button label="일정 추가" icon="pi pi-plus" raised @click="openAddModal" />
      </div>
    </div>

    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-auto min-h-0">
      <FullCalendar :options="calendarOptions" />
    </div>

    <!-- role, userId 전달 -->
    <ScheduleDetailModal v-model:visible="detailVisible" :event="selectedEvent" :type-options="typeOptions" :role="role" :user-id="userId" @delete="deleteEvent" @update="updateEvent" />

    <ScheduleAddModal v-model:visible="addVisible" v-model:event="newEvent" :type-options="typeOptions" @save="saveNewEvent" />
  </div>
</template>

<!-- 기존 <style> 블록 동일 유지 -->

<style>
.fc {
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif !important;
}
.fc-toolbar {
  padding: 16px 20px 0 !important;
}
.fc-toolbar-title {
  font-size: 17px !important;
  font-weight: 700 !important;
  color: #111827 !important;
}
.fc-button {
  background: #fff !important;
  border: 1.5px solid #e5e7eb !important;
  color: #374151 !important;
  border-radius: 8px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  box-shadow: none !important;
  transition: background 0.15s !important;
}
.fc-button:hover {
  background: #f3f4f6 !important;
}
.fc-col-header-cell {
  padding: 10px 0 !important;
  background: #fafafa !important;
  font-size: 11px !important;
  font-weight: 700 !important;
  color: #6b7280 !important;
  text-transform: uppercase !important;
  letter-spacing: 0.4px !important;
}
.fc-col-header-cell:first-child .fc-col-header-cell-cushion {
  color: #ef4444 !important;
}
.fc-col-header-cell:last-child .fc-col-header-cell-cushion {
  color: #3b82f6 !important;
}
.fc-daygrid-day:hover {
  background: #fafafa !important;
}
.fc-day-today {
  background: #e5e7eb !important; /* 오늘 날짜 배경 색 */
}
.fc-day-today .fc-daygrid-day-number {
  width: auto !important;
  height: auto !important;
  display: block !important;
  font-weight: 700 !important;
  font-size: 17px !important;
  color: #d97706 !important;
}
.fc-daygrid-day-number {
  font-size: 13px !important;
  color: #374151 !important;
  padding: 6px 8px !important;
}
.fc-day-sun .fc-daygrid-day-number {
  color: #ef4444 !important;
}
.fc-day-sat .fc-daygrid-day-number {
  color: #3b82f6 !important;
}
.fc-event {
  padding: 2px 6px !important;
  font-size: 12px !important;
  font-weight: 500 !important;
  border: none !important;
  border-radius: 6px !important;
  cursor: grab !important;
}
.fc-event:active {
  cursor: grabbing !important;
}
.fc-daygrid-more-link {
  font-size: 11px !important;
  font-weight: 600 !important;
  color: #d97706 !important;
}
.fc-popover {
  border-radius: 10px !important;
  border: 1px solid #e5e7eb !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1) !important;
}
.fc-popover-header {
  background: #f9fafb !important;
  border-radius: 10px 10px 0 0 !important;
  font-weight: 700 !important;
  font-size: 13px !important;
}

/* 내 일정 토글 활성 */
.mine-toggle.p-togglebutton.p-togglebutton-checked {
  background: #fffbeb !important;
  border-color: #fcd34d !important;
  color: #92400e !important;
}
/* 팀 일정 토글 활성 */
.team-toggle.p-togglebutton.p-togglebutton-checked {
  background: #fffbeb !important;
  border-color: #d97706 !important;
  color: #78350f !important;
}
</style>
