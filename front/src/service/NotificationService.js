import { computed, ref } from 'vue';

const notifications = ref([]);
const isOpen = ref(false);
let eventSource = null;

export function useNotification() {
  const unreadCount = computed(() => notifications.value.filter((n) => !n.read).length);

  const hasUnread = computed(() => unreadCount.value > 0);

  function togglePanel() {
    isOpen.value = !isOpen.value;
  }

  function closePanel() {
    isOpen.value = false;
  }

  function markAsRead(id) {
    const notification = notifications.value.find((n) => n.id === id);
    if (notification) notification.read = true;
  }

  function markAllAsRead() {
    notifications.value.forEach((n) => (n.read = true));
  }

  function removeNotification(id) {
    notifications.value = notifications.value.filter((n) => n.id !== id);
  }

  function clearAll() {
    notifications.value = [];
  }

  function addNotification(notification) {
    notifications.value.unshift({
      ...notification,
      read: false,
      createdAt: notification.createdAt || new Date().toISOString()
    });
  }

  // SSE 구독
  function subscribeSSE(userId) {
    if (eventSource) eventSource.close();

    eventSource = new EventSource(`http://localhost:8888/api/notifications/subscribe/${userId}`);

    eventSource.addEventListener('notification', (event) => {
      const data = JSON.parse(event.data);
      addNotification(data);
    });

    eventSource.addEventListener('error', () => {
      eventSource.close();
      // 재연결 시도 (5초 후)
      setTimeout(() => subscribeSSE(userId), 5000);
    });
  }

  function unsubscribeSSE() {
    if (eventSource) {
      eventSource.close();
      eventSource = null;
    }
  }

  // 서버에서 알림 목록 조회
  async function fetchNotifications(userId) {
    try {
      const res = await fetch(`http://localhost:8888/api/notifications/${userId}`);
      const data = await res.json();
      notifications.value = data;
    } catch (e) {
      console.error('알림 목록 조회 실패:', e);
    }
  }

  // 서버에서 단건 삭제
  async function deleteNotification(id) {
    try {
      await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/notifications/${id}`, { method: 'DELETE' });
      removeNotification(id);
    } catch (e) {
      console.error('알림 삭제 실패:', e);
    }
  }

  // 서버에서 전체 삭제
  async function deleteAllNotifications(userId) {
    try {
      await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/notifications/all/${userId}`, { method: 'DELETE' });
      clearAll();
    } catch (e) {
      console.error('전체 알림 삭제 실패:', e);
    }
  }

  // 서버에서 읽음 처리
  async function readNotification(id) {
    try {
      await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/notifications/${id}/read`, { method: 'PATCH' });
      markAsRead(id);
    } catch (e) {
      console.error('읽음 처리 실패:', e);
    }
  }

  return {
    notifications,
    isOpen,
    unreadCount,
    hasUnread,
    togglePanel,
    closePanel,
    markAsRead,
    markAllAsRead,
    removeNotification,
    clearAll,
    addNotification,
    subscribeSSE,
    unsubscribeSSE,
    fetchNotifications,
    deleteNotification,
    deleteAllNotifications,
    readNotification
  };
}
