import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useDashboardStore = defineStore('dashboard', {
  // state
  state: () => ({
    mainDashboard: ref(null),
    projectDashboard: ref(null),
    projectInfo: ref(null)
  }),
  // getters
  // actions
  actions: {
    async findMainDashboard(id) {
      const res = await axios.get(`/dashboard/${id}`);
      this.mainDashboard = res.data;
    },
    async findProjectDashboard(projectId, id) {
      const res = await axios.get(`/dashboard/${projectId}/${id}`);
      this.projectDashboard = res.data;
    },
    async insertMemo(data) {
      const res = await axios.post(`/dashboard/memo`, data);
    },
    async updateMemo(data) {
      const res = await axios.put(`/dashboard/memo`, data);
    },
    async deleteMemo(id) {
      const res = await axios.delete(`/dashboard/memo/${id}`);
    }
  },
  persist: {
    storage: sessionStorage
  }
});
