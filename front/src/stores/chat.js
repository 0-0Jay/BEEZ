import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useChatStore = defineStore('chat', {
  // state
  state: () => ({
    chatlist: []
  }),
  // getters
  // actions
  actions: {
    async findChatList(projectId) {
      const res = await axios.get(`/chat/${projectId}`);
      this.chatlist = res.data;
      return this.chatlist;
    }
  },
  persist: {
    storage: sessionStorage
  }
});
