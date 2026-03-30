import { defineStore } from 'pinia';

export const useUsersStore = defineStore('users', {
  // state
  state: () => ({
    userList: []
  }),
  // getters
  // actions
  actions: {
    async fetchUsers() {}
  },
  persist: true
});
