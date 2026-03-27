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
    async findNotificationList(userId) {
      const res = await axios.get(`/notification/${userId}`);
      this.list = res.data;
      return this.list;
    },
    async deleteNotification(id) {
      const res = await axios.get(`/notification/${id}`);
      return res.data;
    },
    async readNotificationList(id) {
      const res = await axios.get(`/notification/${id}`);
      return res.data;
    }
  },
  persist: true
});
