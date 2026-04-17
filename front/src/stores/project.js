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
    groups: [],
    logs: [],
    logTotal: 0,
    roadmapList: [],
    taskTypes: [],
    pms: []
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
            title: filters.title ?? null,
            pmId: filters.pmId ?? null,
            startDate: filters.startDate ?? null,
            endDate: filters.endDate ?? null,
            isLock: filters.isLock ?? false
          }
        });

        this.projects = response.data.map((p) => ({
          id: p.id,
          title: p.title,
          identifier: p.identifier,
          creator: p.creator,
          creatorName: p.creatorName,
          pm: p.pmName,
          pmId: p.pmId,
          startDate: p.startDate,
          endDate: p.endDate,
          isLock: p.isLock,
          parentId: p.parentId,
          level: p.level,
          totalTaskCount: p.totalTaskCount,
          completedTaskCount: p.completedTaskCount,
          progressRate: p.progressRate ?? 0
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

      const ownerId = this.projectInfo.userId;
      const pmId = this.projectInfo.pmId;

      const sortFixed = (list) => {
        return list.sort((a, b) => {
          const aFixed = a.userId === ownerId || a.userId === pmId ? 0 : 1;
          const bFixed = b.userId === ownerId || b.userId === pmId ? 0 : 1;
          return aFixed - bFixed;
        });
      };

      this.members = {
        userList: sortFixed(groupByUserId(response.data.userList, 'userId')),
        groupList: groupByUserId(response.data.groupList, 'groupId'),
        groupMemberList: groupByUserId(response.data.groupMemberList, 'userId')
      };
    },

    async deleteProjectMember(projectMemberId) {
      await axios.delete(`/project/member/${projectMemberId}`, {
        params: { projectId: this.selectedProject.id }
      });
    },

    // 프로젝트 구성원 수정
    async updateProjectMember(projectMemberId, roleIds) {
      await axios.put(`/project/member/${projectMemberId}`, { roleIds, projectId: this.selectedProject.id });
    },

    // 역할 조회
    async fetchRoles() {
      const response = await axios.get(`/project/roles`);
      this.roles = response.data;
    },

    // 사용자 + 그룹 (검색)
    async fetchSearchMembers(keyword = null) {
      console.log('selectedProject:', this.selectedProject);
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
    },

    // 로그 목록 조회(필터링)
    async fetchLogs(filters = {}) {
      this.loading = true;
      try {
        const response = await axios.get('/log/list', {
          params: {
            ...filters,
            projectId: this.selectedProject.id
          }
        });
        this.logs = response.data.list;
        this.logTotal = response.data.total;
      } finally {
        this.loading = false;
      }
    },

    // 로드맵 목록 조회
    async fetchRoadmaps(filters = {}) {
      this.loading = true;
      try {
        const response = await axios.get(`/project/${this.selectedProject.id}/roadmap`, { params: filters });
        this.roadmapList = response.data;
      } finally {
        this.loading = false;
      }
    },

    // 일감 유형 조회
    async fetchTaskTypes() {
      const response = await axios.get('/type');
      this.taskTypes = response.data;
    },

    // 프로젝트 복사
    async copyProject(payload) {
      const { data } = await axios.post('/project/copy', payload);
      return data;
    },

    // pm조회
    async fetchUserPm() {
      const response = await axios.get('/project/pm');
      this.pms = response.data;
    }
  },
  persist: {
    omit: ['projects', 'loading'],
    storage: sessionStorage
  }
});
