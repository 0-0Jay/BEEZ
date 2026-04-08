import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useUsersStore = defineStore('users', {
  // state
  state: () => ({
    userList: [],
    initPw: null
  }),
  // getters
  // actions
  actions: {
    // 사용자 목록 조회
    async findUsers(search = {}) {
      const response = await axios.get('/users/list', { params: search });
      this.userList = response.data;
      //console.log(this.userList);
    },

    // 초기 비밀번호 생성
    async getInitPw() {
      const response = await axios.get('/users/getInitPw');
      this.initPw = response.data;
      // console.log(this.initPw);
    },

    // 이메일 중복체크
    async checkEmailExists(email) {
      const response = await axios.get('/users/create/check-email', { params: { email } });
      return response.data;
    },

    // 사용자 등록
    async insertUser(payload) {
      await axios.post('/users/create', payload);
      // console.log(response);
    }
  }
  // persist: true
});
