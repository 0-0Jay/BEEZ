import axios from 'axios';
import { defineStore } from 'pinia';

export const useProjectStore = defineStore('project', {
  // state
  state: () => ({
    projects: [],
    loading: false
  }),
  // getters
  // actions
  actions: {
    // 예시 함수
    async fetchProjects() {
      this.loading = true;
      try {
        const response = await axios.get('/api/project/list');

        // 백엔드 데이터에 프론트용 하드코딩 데이터(일감 수, 진행률)를 합쳐서 저장
        this.projects = response.data.map((p) => ({
          id: p.id,
          title: p.title,
          pm: p.pmName,
          endDate: p.endDate,
          issueCount: '0/0', // 아직 DB 연동 전이라 하드코딩
          progress: 0 // 아직 로직 전이라 하드코딩
        }));
        return response.data;
      } catch (err) {
        console.error('데이터 로드 실패:', err);
      } finally {
        this.loading = false;
      }
    }
  },
  persist: true
});
