import { defineStore } from 'pinia';

export const useAlertStore = defineStore('alert', {
  // state
  state: () => ({
    message: null
  }),
  // getters
  // actions
  actions: {
    setAlert(msg) {
      this.message = msg;
    }
  },
  persist: {
    storage: sessionStorage
  }
});
