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
    // 프로젝트 목록 조회
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
    omit: ['projects', 'loading'],
    storage: sessionStorage
  }
});
