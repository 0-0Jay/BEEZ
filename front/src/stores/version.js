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
          statusName: p.statusName,
          isShare: p.isShare,
          isShareName: p.isShareName,
          isDefault: p.isDefault
        }));

        return response.data;
      } finally {
        this.loading = false;
      }
    },

    // 프로젝트 삭제
    async deleteVersion(id, projectId, name) {
      await axios.delete(`/project/version/delete/${id}`, {
        data: {
          projectId,
          name
        }
      });
    },

    // 프로젝트 생성
    async insertVersion(form) {
      const response = await axios.post('/project/version', form);
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
    omit: ['versions', 'loading']
  }
});
