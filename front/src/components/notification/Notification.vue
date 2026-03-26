<script setup>
import { useNotification } from '@/service/NotificationService';
import { onMounted, onUnmounted, ref } from 'vue';

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
});

const { notifications, isOpen, hasUnread, unreadCount, togglePanel, closePanel, fetchNotifications, subscribeSSE, unsubscribeSSE, deleteNotification, deleteAllNotifications, readNotification, markAllAsRead } = useNotification();
const wrapperRef = ref(null);

// 패널 외부 클릭 시 닫기
function handleOutsideClick(e) {
  if (wrapperRef.value && !wrapperRef.value.contains(e.target)) {
    closePanel();
  }
}

async function handleDelete(id) {
  await deleteNotification(id);
}

async function handleClearAll() {
  await deleteAllNotifications(props.userId);
}

async function handleRead(notification) {
  if (!notification.read) {
    await readNotification(notification.id);
  }
}

async function handleMarkAllAsRead() {
  markAllAsRead();
}

// 시간 포맷
function formatTime(iso) {
  if (!iso) return '';
  const date = new Date(iso);
  const now = new Date();
  const diff = Math.floor((now - date) / 1000);

  if (diff < 60) return '방금 전';
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
}

onMounted(async () => {
  document.addEventListener('click', handleOutsideClick);
  await fetchNotifications(props.userId);
  subscribeSSE(props.userId);
});

onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick);
  unsubscribeSSE();
});
</script>

<template>
  <div class="notification-wrapper" ref="wrapperRef">
    <Button icon="pi pi-bell" label="알림" plain text size="large" :class="{ active: isOpen }" @click="togglePanel" />
    <span v-if="hasUnread" class="unread-dot">
      <span v-if="unreadCount <= 9" class="unread-count">{{ unreadCount }}</span>
      <span v-else class="unread-count">9+</span>
    </span>

    <!-- 알림 패널 -->
    <Transition name="panel">
      <div v-if="isOpen" class="notification-panel" role="dialog" aria-label="알림 목록">
        <!-- 헤더 -->
        <div class="panel-header">
          <div class="panel-title">
            <i class="pi pi-bell" style="font-size: 1.25rem" />
            <span>알림</span>
            <span v-if="unreadCount > 0" class="header-badge">{{ unreadCount }}</span>
          </div>
          <div class="panel-actions">
            <button v-if="notifications.length > 0" class="action-btn read-all-btn" @click="handleMarkAllAsRead" title="모두 읽음">모두 읽음</button>
            <button v-if="notifications.length > 0" class="action-btn clear-btn" @click="handleClearAll" title="전체 삭제">전체 삭제</button>
          </div>
        </div>

        <!-- 알림 목록 -->
        <div class="panel-body">
          <TransitionGroup name="notification-list" tag="ul" class="notification-list">
            <li v-for="notification in notifications" :key="notification.id" class="notification-item" :class="{ read: notification.read, unread: !notification.read }" @click="handleRead(notification)">
              <!-- 내용 -->
              <div class="notif-content">
                <p class="notif-message">{{ notification.message }}</p>
                <span class="notif-time">{{ formatTime(notification.createdAt) }}</span>
              </div>

              <!-- 읽음 표시 점 -->
              <span v-if="!notification.read" class="unread-indicator" />

              <!-- 삭제 버튼 -->
              <button class="delete-btn" @click.stop="handleDelete(notification.id)" title="삭제">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                  <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
                  <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
                </svg>
              </button>
            </li>
          </TransitionGroup>

          <!-- 알림 없음 -->
          <div v-if="notifications.length === 0" class="empty-state">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" opacity="0.3">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="#666" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="#666" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              <line x1="1" y1="1" x2="23" y2="23" stroke="#666" stroke-width="1.5" stroke-linecap="round" />
            </svg>
            <p>새로운 알림이 없습니다</p>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.notification-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
}

/* 종 버튼 */
.bell-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: #555;
  cursor: pointer;
  transition:
    background 0.18s,
    color 0.18s;
}

.bell-btn:hover,
.bell-btn.active {
  background: #ebebeb;
  color: #222;
}

.bell-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.bell-icon.ringing {
  animation: ring 1.2s ease-in-out infinite;
  transform-origin: top center;
}

@keyframes ring {
  0%,
  100% {
    transform: rotate(0deg);
  }
  10% {
    transform: rotate(14deg);
  }
  20% {
    transform: rotate(-10deg);
  }
  30% {
    transform: rotate(8deg);
  }
  40% {
    transform: rotate(-6deg);
  }
  50% {
    transform: rotate(4deg);
  }
  60% {
    transform: rotate(-2deg);
  }
  70%,
  100% {
    transform: rotate(0deg);
  }
}

/* 읽지 않은 알림 뱃지 */
.unread-dot {
  position: absolute;
  top: 5px;
  right: -5px;
  min-width: 16px;
  height: 16px;
  background: #e53e3e;
  border-radius: 10px;
  border: 2px solid white;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pop-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.unread-count {
  font-size: 9px;
  font-weight: 700;
  color: white;
  line-height: 1;
  padding: 0 2px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

@keyframes pop-in {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 알림 패널 */
.notification-panel {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  width: 340px;
  max-height: 480px;
  background: #e0e0e0;
  border-radius: 16px;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.08),
    0 12px 32px -4px rgba(0, 0, 0, 0.14),
    0 0 0 1px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: 9999;
}

/* 패널 헤더 */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px 12px;
  background: #d4d4d4;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 700;
  color: #222;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

.header-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  background: #e53e3e;
  border-radius: 9px;
  font-size: 10px;
  font-weight: 700;
  color: white;
}

.panel-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  padding: 4px 8px;
  border-radius: 6px;
  border: none;
  font-size: 11px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

.read-all-btn {
  background: rgba(0, 0, 0, 0.07);
  color: #444;
}
.read-all-btn:hover {
  background: rgba(0, 0, 0, 0.13);
}

.clear-btn {
  background: rgba(229, 62, 62, 0.1);
  color: #c53030;
}
.clear-btn:hover {
  background: rgba(229, 62, 62, 0.18);
}

/* 패널 바디 */
.panel-body {
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.panel-body::-webkit-scrollbar {
  width: 4px;
}
.panel-body::-webkit-scrollbar-track {
  background: transparent;
}
.panel-body::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 2px;
}

/* 알림 목록 */
.notification-list {
  list-style: none;
  margin: 0;
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

/* 각 알림 아이템 */
.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
  position: relative;
  background: rgba(255, 255, 255, 0.45);
}

.notification-item:hover {
  background: rgba(255, 255, 255, 0.72);
}

/* 읽지 않은 알림 - 진하게 */
.notification-item.unread {
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.notification-item.unread .notif-message {
  color: #1a1a1a;
  font-weight: 600;
}

/* 읽은 알림 - 연하게 */
.notification-item.read {
  background: rgba(255, 255, 255, 0.2);
}

.notification-item.read .notif-message {
  color: #888;
  font-weight: 400;
}

.notification-item.read .notif-time {
  color: #aaa;
}

/* 타입 아이콘 */
.notif-icon {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1px;
}

.icon-info {
  background: #dbeafe;
  color: #2563eb;
}
.icon-success {
  background: #dcfce7;
  color: #16a34a;
}
.icon-warning {
  background: #fef9c3;
  color: #d97706;
}
.icon-error {
  background: #fee2e2;
  color: #dc2626;
}
.icon-default {
  background: rgba(0, 0, 0, 0.07);
  color: #555;
}

/* 알림 내용 */
.notif-content {
  flex: 1;
  min-width: 0;
}

.notif-message {
  font-size: 13px;
  line-height: 1.45;
  margin: 0 0 3px;
  word-break: keep-all;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  color: #333;
}

.notif-time {
  font-size: 11px;
  color: #999;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

/* 읽지 않음 표시 점 */
.unread-indicator {
  flex-shrink: 0;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #4f8ef7;
  margin-top: 5px;
  align-self: flex-start;
}

/* 삭제 버튼 */
.delete-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 6px;
  border: none;
  background: transparent;
  color: #bbb;
  cursor: pointer;
  opacity: 0;
  transition:
    opacity 0.15s,
    background 0.15s,
    color 0.15s;
  padding: 0;
  margin-top: 1px;
}

.notification-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: rgba(229, 62, 62, 0.1);
  color: #e53e3e;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 10px;
  color: #999;
  font-size: 13px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

/* 패널 트랜지션 */
.panel-enter-active {
  transition: all 0.22s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.panel-leave-active {
  transition: all 0.16s ease-in;
}
.panel-enter-from {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}
.panel-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.97);
}

/* 리스트 트랜지션 */
.notification-list-enter-active {
  transition: all 0.25s ease;
}
.notification-list-leave-active {
  transition: all 0.2s ease;
}
.notification-list-enter-from {
  opacity: 0;
  transform: translateX(12px);
}
.notification-list-leave-to {
  opacity: 0;
  transform: translateX(-8px);
  height: 0;
  margin: 0;
  padding: 0;
}
</style>
