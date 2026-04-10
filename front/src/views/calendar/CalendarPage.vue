<script setup>
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import FullCalendar from '@fullcalendar/vue3';
import { computed, ref } from 'vue';

import ScheduleAddModal from '@/components/calendar/ScheduleAddModal.vue';
import ScheduleDetailModal from '@/components/calendar/ScheduleDetailModal.vue';

// ── 색상 ────────────────────────────────────────────
// DTO type 필드: 'mine' | 'team'
const MY_COLOR = '#FCD34D'; // 내 일정
const TEAM_COLOR = '#D97706'; // 팀 일정

// ── 필터 ────────────────────────────────────────────
const filters = ref({ mine: true, team: true });
const searchQuery = ref('');

// ── 유틸 ────────────────────────────────────────────
function relDate(offset) {
  const d = new Date();
  d.setDate(d.getDate() + offset);
  return d.toISOString().split('T')[0];
}
const toDateStr = (val) => {
  if (!val) return undefined;
  if (typeof val === 'string') return val;
  return new Date(val).toISOString().split('T')[0];
};

// ── 이벤트 데이터 ────────────────────────────────────
// CalendarResponse DTO 구조: { id, userId, type, title, content, projectId, startDate, endDate }
const allEvents = ref([
  { id: 'e1', title: '스프린트 계획', start: relDate(0), end: relDate(2), backgroundColor: MY_COLOR, borderColor: MY_COLOR, extendedProps: { type: 'mine', content: '2주 스프린트 계획 수립', userId: 'user1', projectId: 'proj1' } },
  { id: 'e2', title: '코드 리뷰', start: relDate(3), backgroundColor: MY_COLOR, borderColor: MY_COLOR, extendedProps: { type: 'mine', content: '', userId: 'user1', projectId: 'proj1' } },
  { id: 'e3', title: '주간 팀 회의', start: relDate(1), end: relDate(2), backgroundColor: TEAM_COLOR, borderColor: TEAM_COLOR, extendedProps: { type: 'team', content: '매주 월요일 진행', userId: 'user1', projectId: 'proj1' } },
  { id: 'e4', title: 'API 개발 일정', start: relDate(5), end: relDate(9), backgroundColor: TEAM_COLOR, borderColor: TEAM_COLOR, extendedProps: { type: 'team', content: 'REST API 설계 및 구현', userId: 'user1', projectId: 'proj1' } },
  { id: 'e5', title: 'QA 테스트', start: relDate(8), end: relDate(11), backgroundColor: TEAM_COLOR, borderColor: TEAM_COLOR, extendedProps: { type: 'team', content: '', userId: 'user1', projectId: 'proj1' } },
  { id: 'e6', title: '배포 준비', start: relDate(12), end: relDate(14), backgroundColor: MY_COLOR, borderColor: MY_COLOR, extendedProps: { type: 'mine', content: '운영 서버 배포 체크리스트', userId: 'user1', projectId: 'proj1' } },
  { id: 'e7', title: '디자인 리뷰', start: relDate(4), backgroundColor: TEAM_COLOR, borderColor: TEAM_COLOR, extendedProps: { type: 'team', content: '', userId: 'user1', projectId: 'proj1' } },
  { id: 'e8', title: 'UI 컴포넌트 작업', start: relDate(13), end: relDate(17), backgroundColor: MY_COLOR, borderColor: MY_COLOR, extendedProps: { type: 'mine', content: 'PrimeVue 컴포넌트 커스터마이징', userId: 'user1', projectId: 'proj1' } }
]);

// ── 필터된 이벤트 ────────────────────────────────────
const filteredEvents = computed(() =>
  allEvents.value.filter((ev) => {
    const { type } = ev.extendedProps;
    if (type === 'mine' && !filters.value.mine) return false;
    if (type === 'team' && !filters.value.team) return false;
    if (searchQuery.value.trim()) return ev.title.toLowerCase().includes(searchQuery.value.trim().toLowerCase());
    return true;
  })
);

// ── 모달 ─────────────────────────────────────────────
const detailVisible = ref(false);
const addVisible = ref(false);
const selectedEvent = ref(null);

const typeOptions = [
  { label: '내 일정', value: 'mine' },
  { label: '팀 일정', value: 'team' }
];

// CalendarRequest DTO 구조에 맞춘 기본값
const defaultNew = () => ({
  title: '',
  start: null,
  end: null,
  type: 'mine',
  content: '', // DTO: content
  userId: '', // DTO: userId
  projectId: '' // DTO: projectId
});
const newEvent = ref(defaultNew());

// 일정 추가 모달 열기 — 날짜 클릭이 아닌 버튼으로만 호출
const openAddModal = () => {
  newEvent.value = defaultNew();
  addVisible.value = true;
};

// CalendarRequest DTO 형태로 변환하여 저장
const saveNewEvent = () => {
  if (!newEvent.value.title.trim() || !newEvent.value.start) return;
  const color = newEvent.value.type === 'mine' ? MY_COLOR : TEAM_COLOR;

  // 서버 전송 시 CalendarRequest DTO로 매핑:
  // { id, userId, type, title, content, projectId, startDate, endDate }
  allEvents.value.push({
    id: 'ev-' + Date.now(),
    title: newEvent.value.title,
    start: toDateStr(newEvent.value.start),
    end: toDateStr(newEvent.value.end),
    backgroundColor: color,
    borderColor: color,
    extendedProps: {
      type: newEvent.value.type,
      content: newEvent.value.content,
      userId: newEvent.value.userId,
      projectId: newEvent.value.projectId
    }
  });
  addVisible.value = false;
};

const formatEventDate = (ev) => {
  const s = ev.startStr || ev.start;
  const e = ev.endStr || ev.end;
  return !e || s === e ? s : `${s} ~ ${e}`;
};

const deleteEvent = () => {
  allEvents.value = allEvents.value.filter((e) => e.id !== selectedEvent.value.id);
  detailVisible.value = false;
};

// 일정 수정 저장 — CalendarRequest DTO 형태로 서버에 전송 가능
const updateEvent = (updated) => {
  const idx = allEvents.value.findIndex((e) => e.id === updated.id);
  if (idx === -1) return;
  const color = updated.type === 'mine' ? MY_COLOR : TEAM_COLOR;
  allEvents.value[idx] = {
    ...allEvents.value[idx],
    title: updated.title,
    start: updated.start,
    end: updated.end || undefined,
    backgroundColor: color,
    borderColor: color,
    extendedProps: {
      type: updated.type,
      content: updated.content,
      userId: updated.userId,
      projectId: updated.projectId
    }
  };
  // 수정된 selectedEvent도 갱신
  selectedEvent.value = { ...selectedEvent.value, ...allEvents.value[idx] };
};

// ── FullCalendar 옵션 ────────────────────────────────
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locale: 'ko',
  headerToolbar: { left: 'prev', center: 'title', right: 'next' },
  events: filteredEvents.value,
  editable: true,
  droppable: true,
  dayMaxEvents: 3,
  eventDisplay: 'block',
  fixedWeekCount: false,
  contentHeight: 'auto',

  eventClick(info) {
    selectedEvent.value = {
      id: info.event.id,
      title: info.event.title,
      startStr: info.event.startStr,
      endStr: info.event.endStr,
      backgroundColor: info.event.backgroundColor,
      extendedProps: info.event.extendedProps
    };
    detailVisible.value = true;
  },

  // 날짜 칸 클릭 시 일정 추가 모달 열기 제거 (버튼으로만 추가)
  // dateClick 핸들러 없음

  // ── 일정 드래그 이동 ──────────────────────────────
  // eventDrop: 사용자가 이벤트를 드래그해서 다른 날짜로 이동했을 때 호출됨
  // info.event.id       : 이동된 이벤트 ID
  // info.event.startStr : 이동 후 새 시작일 (yyyy-MM-dd)
  // info.event.endStr   : 이동 후 새 종료일 (yyyy-MM-dd, 없으면 빈 문자열)
  // info.revert()       : 이동을 취소하고 원래 위치로 되돌릴 때 호출
  // 서버 연동 시 여기서 CalendarRequest DTO를 만들어 PATCH/PUT API 호출
  eventDrop(info) {
    const ev = allEvents.value.find((e) => e.id === info.event.id);
    if (ev) {
      ev.start = info.event.startStr;
      ev.end = info.event.endStr || undefined;
    }
  }
  // ── 드래그 이동 끝 ───────────────────────────────
}));
</script>

<template>
  <div class="min-h-screen bg-gray-50 px-10 py-8 font-sans">
    <!-- 헤더 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900 tracking-tight">달력</h1>
    </div>

    <!-- 필터 카드 -->
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

      <!-- 일정 추가 버튼 — 클릭 시 모달 열기 (날짜 클릭과 무관) -->
      <div class="ml-auto">
        <Button label="일정 추가" icon="pi pi-plus" raised @click="openAddModal" />
      </div>
    </div>

    <!-- 캘린더 본체 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <FullCalendar :options="calendarOptions" />
    </div>

    <!-- 일정 상세 다이얼로그 -->
    <ScheduleDetailModal v-model:visible="detailVisible" :event="selectedEvent" :type-options="typeOptions" @delete="deleteEvent" @update="updateEvent" />

    <!-- 일정 추가 다이얼로그 -->
    <ScheduleAddModal v-model:visible="addVisible" v-model:event="newEvent" :type-options="typeOptions" @save="saveNewEvent" />
  </div>
</template>

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
  background: #fffbeb !important;
}
.fc-day-today .fc-daygrid-day-number {
  background: #fcd34d !important;
  color: #78350f !important;
  border-radius: 50% !important;
  width: 26px !important;
  height: 26px !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  font-weight: 700 !important;
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
