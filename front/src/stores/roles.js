import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useRolesStore = defineStore('roles', {
  // state
  state: () => ({
    roleList: [],
    perList: []
  }),
  // getters
  // actions
  actions: {
    // 역할 목록 조회
    async fetchRoles() {
      const response = await axios.get('/roles/list');
      this.roleList = response.data;
      // console.log(this.roleList);
    },

    // 역할 상세 조회
    async fetchRolesDetail(id) {
      const response = await axios.get(`/roles/${id}`);
      return response.data;
    },

    // 역할 등록
    async insertRoles(payload) {
      await axios.post('/roles/add', payload);
    },

    // 역할 수정
    async updateRoles(payload, id) {
      await axios.put(`/roles/${id}`, payload);
    },

    // 역할 삭제
    async deleteRoles(id) {
      await axios.delete(`/roles/${id}`);
    },

    // 역할 복사
    async copyRole(originId, newName) {
      await axios.post('/api/roles/copy', { originId, newName });
    },

    // 권한 목록 조회
    async fetchPermissions() {
      const response = await axios.get('/permission/list');
      // console.log(response.data);
      this.perList = response.data;
    }
  }
  // ,persist: true
});
