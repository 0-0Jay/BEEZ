import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useVersionStore = defineStore('version', {
  // state
  state: () => ({
    selectedVersion: null,
    versions: [],
    loading: false,
    versionInfo: null
  }),
  // getters
  // actions
  actions: {
    // 버전 목록 조회
    async fetchVersions(filters = {}) {
      this.loading = true;
      try {
        const response = await axios.get('/project/version/list', {
          params: {
            projectId: filters.projectId ?? null,
            id: filters.id ?? null,
            status: filters.status ?? null
          }
        });

        this.versions = response.data.map((p) => ({
          id: p.id,
          projectId: p.projectId,
          name: p.name,
          description: p.description,
          startDate: p.startDate,
          endDate: p.endDate,
          status: p.status,
          isShare: p.isShare,
          isDefault: p.isDefault
        }));

        return response.data;
      } finally {
        this.loading = false;
      }
    },

    // 프로젝트 잠금보관
    async lockProject(id) {
      await axios.put(`/project/lock/${id}`);
    },

    // 프로젝트 잠금보관 해제
    async unlockProject(id) {
      await axios.put(`/project/unlock/${id}`);
    },

    // 프로젝트 삭제
    async deleteProject(id) {
      await axios.put(`/project/delete/${id}`);
    },

    // 프로젝트 생성
    async createProject(data) {
      const response = await axios.post('/project', data);
      return response.data;
    },

    // 프로젝트 단건 조회
    async findProject(id) {
      const response = await axios.get(`/project/${id}`);
      console.log(response.data);
      this.projectInfo = response.data;
    },

    // 프로젝트 수정
    async updateProject(id, formData) {
      const response = await axios.put(`/project/${id}`, formData);
      this.projectInfo = response.data;
    }
  },
  persist: {
    omit: ['projects', 'loading']
  }
});
