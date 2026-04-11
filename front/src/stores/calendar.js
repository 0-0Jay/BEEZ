import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useCalendarStore = defineStore('calendar', {
  // state
  state: () => ({
    scheduleList: []
  }),
  // getters
  // actions
  actions: {
    async findScheduleList(id, userId) {
      const res = await axios.get(`/calendar/${id}/${userId}`);
      this.scheduleList = res.data;
      return this.scheduleList;
    },
    async insertSchedule(data) {
      const res = await axios.post(`/calendar`, data);
    },
    async updateSchedule(data) {
      const res = await axios.put(`/calendar`, data);
    },
    async deleteSchedule(id) {
      const res = await axios.delete(`/calendar/${id}`);
    }
  },
  persist: {
    storage: sessionStorage
  }
});
