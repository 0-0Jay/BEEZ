import axios from 'axios';
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
      try {
        const res = await axios.get(`/api/chat/${projectId}`);
        this.chatlist = res.data;
        return this.chatlist;
      } catch (err) {
        console.log(err);
      }
    }
  },
  persist: true
});
