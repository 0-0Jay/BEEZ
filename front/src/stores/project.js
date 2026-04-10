import axios from '@/stores/AxiosInstance';
import { groupByUserId } from '@/utils/memberUtils';
import { defineStore } from 'pinia';

export const useProjectStore = defineStore('project', {
  // state
  state: () => ({
    selectedProject: null,
    projects: [],
    loading: false,
    projectInfo: null,
    members: {
      userList: [],
      groupList: [],
      groupMemberList: []
    },
    roles: [],
    users: [],
    groups: []
  }),
  // getters
  // actions
  actions: {
    // 프로젝트 목록 조회
    async fetchProjects(filters = {}) {
      this.loading = true;
      try {
        const response = await axios.get('/project/list', {
          params: {
            id: filters.id ?? null,
            pmId: filters.pmId ?? null,
            startDate: filters.startDate ?? null,
            endDate: filters.endDate ?? null,
            isLock: filters.isLock ?? false
          }
        });

        // 백엔드 데이터에 프론트용 하드코딩 데이터(일감 수, 진행률)를 합쳐서 저장
        this.projects = response.data.map((p) => ({
          id: p.id,
          title: p.title,
          identifier: p.identifier,
          pm: p.pmName,
          pmId: p.pmId,
          endDate: p.endDate,
          isLock: p.isLock,
          parentId: p.parentId,
          level: p.level,
          issueCount: '0/0', // 아직 DB 연동 전이라 하드코딩
          progress: 0 // 아직 로직 전이라 하드코딩
        }));

        return response.data;
      } finally {
        this.loading = false;
      }
    },

    // 프로젝트 잠금보관
    async lockProject(id) {
      await axios.put(`/project/lock/${id}`);
    },

    // 프로젝트 잠금보관 해제
    async unlockProject(id) {
      await axios.put(`/project/unlock/${id}`);
    },

    // 프로젝트 삭제
    async deleteProject(id) {
      await axios.delete(`/project/delete/${id}`);
    },

    // 프로젝트 생성
    async createProject(data) {
      const response = await axios.post('/project', data);
      return response.data;
    },

    // 프로젝트 식별자 중복체크
    async checkIdentifier(identifier, projectId = null) {
      const response = await axios.get('/project/check/identifier', {
        params: { identifier, projectId }
      });
      return response.data;
    },

    // 프로젝트 이름 중복체크
    async checkTitle(title, projectId = null) {
      const response = await axios.get('/project/check/title', {
        params: { title, projectId }
      });
      return response.data;
    },

    // 프로젝트 단건 조회
    async findProject(id) {
      const response = await axios.get(`/project/${id}`);
      console.log(response.data);
      this.projectInfo = response.data;
    },

    // 프로젝트 수정
    async updateProject(id, formData) {
      const response = await axios.put(`/project/${id}`, formData);
      this.projectInfo = response.data;
    },

    // 프로젝트 구성원 조회
    async fetchProjectMembers(projectId) {
      const response = await axios.get(`/project/${projectId}/members`);

      this.members = {
        userList: groupByUserId(response.data.userList, 'userId'),
        groupList: groupByUserId(response.data.groupList, 'groupId'),
        groupMemberList: groupByUserId(response.data.groupMemberList, 'userId')
      };
    },

    // 프로젝트 구성원 삭제
    async deleteProjectMember(projectMemberId) {
      await axios.delete(`/project/member/${projectMemberId}`);
    },

    // 프로젝트 구성원 수정
    async updateProjectMember(projectMemberId, roleIds) {
      await axios.put(`/project/member/${projectMemberId}`, { roleIds });
    },

    // 역할 조회
    async fetchRoles() {
      const response = await axios.get(`/project/roles`);
      this.roles = response.data;
    },

    // 사용자 + 그룹 (검색)
    async fetchSearchMembers(keyword = null) {
      const response = await axios.get(`/project/members/search`, {
        params: { projectId: this.selectedProject.id, keyword }
      });
      this.users = response.data.users;
      this.groups = response.data.groups;
    },

    // 프로젝트 구성원 추가
    async insertProjectMember(requestBody) {
      const response = await axios.post(`/project/members`, requestBody);
      return response.data;
    }
  },
  persist: {
    omit: ['projects', 'loading'],
    storage: sessionStorage
  }
});
