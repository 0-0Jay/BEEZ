import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
  // state
  state: () => ({
    list: [],
    variance: null
  }),
  // getters
  // actions
  actions: {
    // 서버에서 알림 목록 조회
    async fetchNotifications(userId) {
      try {
        const res = await fetch(`http://localhost:8888/api/notifications/${userId}`);
        const data = await res.json();
        notifications.value = data;
      } catch (e) {
        console.error('알림 목록 조회 실패:', e);
      }
    },

    // 서버에서 단건 삭제
    async deleteNotification(id) {
      try {
        await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/notifications/${id}`, { method: 'DELETE' });
        removeNotification(id);
      } catch (e) {
        console.error('알림 삭제 실패:', e);
      }
    },

    // 서버에서 전체 삭제
    async deleteAllNotifications(userId) {
      try {
        await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/notifications/all/${userId}`, { method: 'DELETE' });
        clearAll();
      } catch (e) {
        console.error('전체 알림 삭제 실패:', e);
      }
    },

    // 서버에서 읽음 처리
    async readNotification(id) {
      try {
        await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/notifications/${id}/read`, { method: 'PATCH' });
        markAsRead(id);
      } catch (e) {
        console.error('읽음 처리 실패:', e);
      }
    },

    // SSE 구독
    subscribeSSE(userId) {
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
  },
  persist: true
});
