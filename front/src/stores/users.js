import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useUsersStore = defineStore('users', {
  // state
  state: () => ({
    userList: []
  }),
  // getters
  // actions
  actions: {
    async fetchUsers(search = {}) {
      const response = await axios.get('/users/list', { params: search });
      this.userList = response.data;
      // console.log(this.userList);
    }
  },
  persist: true
});
