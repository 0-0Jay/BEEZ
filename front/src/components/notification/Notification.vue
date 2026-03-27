<script setup>
import { useNotificationStore } from '@/stores/notification';
import Button from 'primevue/button';
import { computed, onMounted, onUnmounted, ref } from 'vue';

const userId = '20261111';
const isOpen = ref(false);
const panelRef = ref(null);
const btnRef = ref(null);
const notificationStore = useNotificationStore();

// ── 백엔드 데이터 ─────────────────────────────────────────────
const notifications = ref([]);

// ── link → 타입 유추 ─────────────────────────────────────────
function resolveType(link) {
  if (!link) return 'default';
  if (link.includes('task') || link.includes('issue')) return 'task';
  if (link.includes('comment')) return 'comment';
  if (link.includes('mention')) return 'mention';
  if (link.includes('status')) return 'status';
  return 'default';
}

const typeIcon = {
  task: 'pi pi-check-square',
  comment: 'pi pi-comment',
  mention: 'pi pi-at',
  status: 'pi pi-sync',
  default: 'pi pi-bell'
};

const typeBg = {
  task: 'bg-amber-100 text-amber-600',
  comment: 'bg-blue-100 text-blue-500',
  mention: 'bg-purple-100 text-purple-500',
  status: 'bg-green-100 text-green-600',
  default: 'bg-stone-100 text-stone-500'
};

// ── created_on → 상대 시간 변환 ───────────────────────────────
function formatTime(createdOn) {
  if (!createdOn) return '';
  const now = new Date();
  const diff = Math.floor((now - new Date(createdOn)) / 1000); // 초 단위

  if (diff < 60) return '방금 전';
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;
  return new Date(createdOn).toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
}

// ── 백엔드 데이터 → 컴포넌트 형식으로 매핑 ──────────────────────
function mapNotification(raw) {
  return {
    id: raw.id, // NOTI000 형식
    content: raw.content,
    time: formatTime(raw.created_on),
    isRead: raw.status === '1', // "0" 안읽음 / "1" 읽음
    link: raw.link
  };
}

// ── 안읽음 개수 (computed) ────────────────────────────────────
const unreadCount = computed(() => notifications.value.filter((n) => !n.isRead).length);

// 알림 목록
async function loadNotifications() {
  notifications.value = (await notificationStore.findNotificationList(userId)) ?? [];
}

function togglePanel() {
  if (!isOpen.value) loadNotifications(); // 열 때마다 최신화
  isOpen.value = !isOpen.value;
}

function markAsRead(n) {
  if (n.status) return;
  n.status = '1';
  notificationStore.readNotification(n.id);
}

function markAllAsRead() {
  notifications.value.forEach((n) => (n.status = '1'));
  notificationStore.readNotification();
}

async function deleteNotification(id, e) {
  e.stopPropagation();
  try {
    await notificationStore.deleteNotification(id);
    notifications.value = notifications.value.filter((n) => n.id !== id);
  } catch (err) {
    console.error('알림 삭제 실패', err);
  }
}

// ── 외부 클릭 시 닫기 ─────────────────────────────────────────
function handleClickOutside(e) {
  if (isOpen.value && panelRef.value && !panelRef.value.contains(e.target) && !btnRef.value.contains(e.target)) {
    isOpen.value = false;
  }
}

onMounted(() => {
  loadNotifications();
  document.addEventListener('mousedown', handleClickOutside);
});
onUnmounted(() => document.removeEventListener('mousedown', handleClickOutside));
</script>

<template>
  <div class="relative">
    <!-- 알림 아이콘 -->
    <div ref="btnRef" class="relative inline-flex">
      <Button icon="pi pi-bell" label="알림" plain text size="large" :class="isOpen ? '!text-amber-600' : ''" @click="togglePanel" />
      <!-- 안읽은 뱃지 -->
      <span
        v-if="unreadCount > 0"
        class="absolute top-1 -right-2 min-w-[18px] h-[18px] px-1 flex items-center justify-center rounded-full bg-amber-500 text-white text-[10px] font-bold leading-none shadow-sm shadow-amber-300 select-none pointer-events-none"
      >
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </div>

    <!-- 알림 패널 -->
    <Transition
      enter-active-class="transition-all duration-200 ease-out"
      enter-from-class="opacity-0 translate-y-1 scale-95"
      enter-to-class="opacity-100 translate-y-0 scale-100"
      leave-active-class="transition-all duration-150 ease-in"
      leave-from-class="opacity-100 translate-y-0 scale-100"
      leave-to-class="opacity-0 translate-y-1 scale-95"
    >
      <div v-if="isOpen" ref="panelRef" class="absolute right-0 top-[calc(100%+10px)] w-[380px] bg-white border border-stone-200 rounded-xl shadow-xl shadow-stone-200/80 z-50 overflow-hidden origin-top-right">
        <!-- 헤더 -->
        <div class="flex items-center justify-between px-5 py-4 border-b border-stone-100">
          <div class="flex items-center gap-2">
            <i class="pi pi-bell text-amber-500 text-base"></i>
            <span class="text-base font-bold text-stone-800 tracking-tight">알림</span>
            <span v-if="unreadCount > 0" class="px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 text-xs font-bold"> {{ unreadCount }}개 안읽음 </span>
          </div>
          <button v-if="unreadCount > 0" class="text-xs text-stone-400 hover:text-amber-600 font-medium transition-colors cursor-pointer bg-transparent border-none outline-none px-0 py-0" @click="markAllAsRead">모두 읽음</button>
        </div>

        <!-- 알림 목록 -->
        <ul class="max-h-[420px] overflow-y-auto divide-y divide-stone-50">
          <li
            v-for="n in notifications"
            :key="n.id"
            class="group flex items-start gap-3.5 px-5 py-4 transition-colors duration-100"
            :class="[n.isRead ? 'bg-white hover:bg-stone-50' : 'bg-amber-50/60 hover:bg-amber-50', n.link ? 'cursor-pointer' : 'cursor-default']"
            @click="markAsRead(n)"
          >
            <!-- 타입 아이콘 -->
            <div class="shrink-0 w-9 h-9 rounded-full flex items-center justify-center mt-0.5" :class="typeBg[n.type]">
              <i :class="[typeIcon[n.type], 'text-sm']"></i>
            </div>

            <!-- 알림 내용 -->
            <div class="flex-1 min-w-0">
              <p class="text-sm text-stone-700 leading-relaxed line-clamp-2" :class="n.isRead ? 'font-normal' : 'font-semibold'">
                {{ n.content }}
                <span v-if="!n.isRead" class="inline-block w-1.5 h-1.5 rounded-full bg-amber-500 shrink-0 ml-1 mb-px align-middle"></span>
              </p>
              <div class="flex items-center gap-2 mt-1.5">
                <p class="text-[11px] text-stone-400 font-medium">{{ n.time }}</p>
                <span class="text-[10px] font-mono text-stone-300">{{ n.id }}</span>
              </div>
            </div>

            <!-- 삭제 버튼 -->
            <button
              class="shrink-0 w-6 h-6 flex items-center justify-center rounded-full text-stone-300 hover:text-red-400 hover:bg-red-50 transition-all duration-150 cursor-pointer bg-transparent border-none outline-none opacity-0 group-hover:opacity-100 mt-0.5"
              title="알림 삭제"
              @click="deleteNotification(n.id, $event)"
            >
              <i class="pi pi-times text-[11px]"></i>
            </button>
          </li>

          <!-- 알림 없을 때 -->
          <li v-if="notifications.length === 0" class="flex flex-col items-center justify-center gap-2 py-14 text-stone-400">
            <i class="pi pi-bell-slash text-3xl opacity-40"></i>
            <span class="text-sm">알림이 없습니다</span>
          </li>
        </ul>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
ul::-webkit-scrollbar {
  width: 4px;
}
ul::-webkit-scrollbar-track {
  background: transparent;
}
ul::-webkit-scrollbar-thumb {
  background: #e7e5e4;
  border-radius: 99px;
}
ul::-webkit-scrollbar-thumb:hover {
  background: #d6d3d1;
}
</style>
