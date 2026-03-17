import axios from 'axios';
import { defineStore } from 'pinia';

export const useExampleStore = defineStore('example', {
  // state
  state: () => ({
    list: [],
    variance: null
  }),
  // getters
  // actions
  actions: {
    // 예시 함수
    async example(data) {
      try {
        const reponse = await axios.post('/example', data);
        this.list = data.example;
        return response.data;
      } catch (err) {
        console.log(err);
      }
    }
  },
  persist: true
});
