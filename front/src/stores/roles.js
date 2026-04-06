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

    // 역할 등록
    async insertRoles(payload) {
      await axios.post('/roles/add', payload);
    },

    // 역할 수정
    async updateRoles(id, payload) {
      await axios.put(`/roles/${id}`, payload);
    },

    // 역할 삭제
    async deleteRoles(roleId) {
      await axios.delete(`/roles/${roleId}`);
    },

    // 역할 복사
    async copyRole(originId, newName) {
      await axios.post('/api/roles/copy', { originId, newName });
    },

    // 권한 목록 조회
    async fetchPermissions() {
      const response = await axios.get('/permission/list');
      this.perList = response.data;
      console.log(this.perList);
    }
  }
  // ,persist: true
});
