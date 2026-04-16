import axios from '@/stores/AxiosInstance';
import { jwtDecode } from 'jwt-decode';
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  // state
  state: () => ({
    accessToken: null, // 엑세스 토큰
    user: {
      id: '',
      name: '',
      email: '',
      role: ''
      // authorities: []
    }, // 사용자 정보
    projectPermissions: [],
    projectRoles: [],
    currentProjectId: null
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
          // console.log('디코딩된 토큰 내용:', decoded);

          this.user = {
            id: decoded.sub,
            name: decoded.name,
            email: decoded.email,
            // authorities: decoded.auth ? decoded.auth.split(',') : [],
            role: decoded.role
          };

          //console.log(this.user.authorities);
          return true;
        }
      } catch (error) {
        console.error('로그인 에러 발생:', error);
        throw error;
      }
    },

    logout() {
      this.accessToken = null;
      this.user = {
        id: '',
        name: '',
        email: '',
        role: ''
      };
      this.projectPermissions = [];
      this.projectRoles = [];
      this.currentProjectId = null;
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
    },

    hasPermissions(targetCode) {
      if (this.user?.role === 'ROLE0001') {
        return true;
      }

      if (!this.projectPermissions || this.projectPermissions.length === 0) {
        return false;
      }

      return this.projectPermissions.some((p) => p.id === targetCode);
    },

    async findPermissionsByProject(projectId) {
      if (this.currentProjectId === projectId) return;

      const response = await axios.get(`/project-auth/permissions/${projectId}`);
      this.projectPermissions = response.data;
      this.currentProjectId = projectId;
      console.log('프로젝트별 권한 -> ', this.projectPermissions);
    },

    async findRolesByProject(projectId) {
      const response = await axios.get(`/project-auth/roles/${projectId}`);
      this.projectRoles = response.data;
      console.log('프로젝트별 역할 -> ', this.projectRoles);
    }
  },
  persist: true
});
