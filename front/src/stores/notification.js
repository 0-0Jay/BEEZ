import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
  // state
  state: () => ({
    list: []
  }),
  // getters
  // actions
  actions: {
    // 알림 목록
    async findNotificationList(userId) {
      const res = await axios.get(`/notification/${userId}`);
      this.list = res.data;
      return this.list;
    },
    // 알림 삭제
    async deleteNotification(id) {
      const res = await axios.delete(`/notification/${id}`);
      return res.data;
    },
    // 알림 읽음
    async readNotification(id) {
      const res = await axios.put(`/notification/${id}`);
      return res.data;
    },
    // 알림 전부 읽음
    async readAllNotification(userId) {
      const res = await axios.put(`/notification/${userId}/all`);
      return res.data;
    },
    // 알림 전송 테스트
    async sendTest(data) {
      const res = await axios.post(`/notification/test/${data.userId}`, data);
      return res.data;
    }
  },
  persist: true
});
