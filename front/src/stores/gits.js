import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useGitStore = defineStore('git', {
  // state
  state: () => ({
    repoList: [],
    commitList: []
  }),
  // getters
  // actions
  actions: {
    // 프로젝트별 저장소 목록 조회
    async findReposByProjectId(projectId) {
      const response = await axios.get(`/gits/list/${projectId}`);
      this.repoList = response.data;
    },

    // 저장소 등록
    async insertRepositories(payload) {
      await axios.post('/gits/repo', payload);
    },

    // 동기화
    async updateSyncCommits(id) {
      const response = await axios.put(`/gits/sync/${id}`);
      return response.data;
    },

    // 저장소 삭제(해제)
    async deleteRepository(id) {
      const response = await axios.delete(`/gits/${id}`);
      return response.data;
    },

    // 일감 번호로 커밋 목록 조회
    async findCommitsByTaskId(taskId) {
      const response = await axios.get(`/gits/commits/${taskId}`);
      this.commitList = response.data;
    }
  }
  // ,persist: true
});
