import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useProjectStore = defineStore('project', {
  // state
  state: () => ({
    selectedProject: null,
    projects: [],
    loading: false,
    projectInfo: null
  }),
  // getters
  // actions
  actions: {
    async fetchProjects(filters = {}) {
      this.loading = true;
      try {
        const response = await axios.get('/project/list', {
          params: {
            id: filters.id ?? null,
            pmId: filters.pmId ?? null,
            startDate: filters.startDate ?? null,
            endDate: filters.endDate ?? null,
            isLock: filters.isLock ?? false
          }
        });

        // 백엔드 데이터에 프론트용 하드코딩 데이터(일감 수, 진행률)를 합쳐서 저장
        this.projects = response.data.map((p) => ({
          id: p.id,
          title: p.title,
          pm: p.pmName,
          pmId: p.pmId,
          endDate: p.endDate,
          isLock: p.isLock,
          issueCount: '0/0', // 아직 DB 연동 전이라 하드코딩
          progress: 0 // 아직 로직 전이라 하드코딩
        }));

        return response.data;
      } finally {
        this.loading = false;
      }
    },

    async lockProject(id) {
      await axios.put(`/project/lock/${id}`);
    },

    async unlockProject(id) {
      await axios.put(`/project/unlock/${id}`);
    },

    async deleteProject(id) {
      await axios.put(`/project/delete/${id}`);
    },

    async createProject(data) {
      const response = await axios.post('/project', data);
      return response.data;
    },

    async findProject(id) {
      const response = await axios.get(`/project/${id}`);
      console.log(response.data);
      this.projectInfo = response.data;
    },

    async updateProject(id, formData) {
      const response = await axios.put(`/project/${id}`, formData);
      this.projectInfo = response.data;
    }
  },
  persist: {
    omit: ['projects', 'loading'],
    storage: sessionStorage
  }
});
