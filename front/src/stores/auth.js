import axios from '@/stores/AxiosInstance';
import { jwtDecode } from 'jwt-decode';
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  // state
  state: () => ({
    accessToken: null, // 엑세스 토큰
    user: null // 사용자 정보
  }),

  // getters
  getters: {
    // 로그인 여부
    isLoggedIn: (state) => !!state.accessToken
  },

  // actions
  actions: {
    async login(id, password) {
      try {
        const response = await axios.post('/auth/login', {
          id: id,
          password: password
        });

        const token = response.data.accessToken;

        if (token) {
          this.accessToken = token;

          const decoded = jwtDecode(token);

          this.user = {
            id: decoded.sub,
            roles: decoded.roles,
            name: decoded.name
          };

          // console.log(token, this.user);

          // localStorage.setItem('accessToken', token);
          // localStorage.setItem('user', JSON.stringify(this.user));
          return true;
        }
      } catch (error) {
        console.error('로그인 에러 발생:', error);
        throw error;
      }
    },

    logout() {
      this.accessToken = null;
      this.user = null;
      // localStorage.removeItem('accessToken');
      // localStorage.removeItem('user');
      sessionStorage.clear();
    },

    async requestPasswordReset(payload) {
      try {
        const response = await axios.post('/auth/reset-request', payload);
        console.log(response.data);

        return { success: true, message: response.data };
      } catch (err) {
        const errorMsg = err.response?.data?.message || '서버 오류가 발생했습니다.';
        return { success: false, message: errorMsg };
      }
    },

    async resetPassword(payload) {
      try {
        const response = await axios.post('/auth/reset-password', payload);
        console.log(response.data);

        return { success: true, message: response.data };
      } catch (err) {
        const errorMsg = err.response?.data?.message || '서버 오류가 발생했습니다.';
        return { success: false, message: errorMsg };
      }
    }
  },
  persist: true
});
