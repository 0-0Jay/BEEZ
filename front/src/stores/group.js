import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useGroupStore = defineStore('group', {
  // state
  state: () => ({
    groupList: [],
    groupForm: {
      name: '',
      description: ''
    },
    selectedMembers: [],
    selectedProjects: []
  }),
  // getters
  // actions
  actions: {
    // 그룹 목록 조회
    async findGroup(params) {
      const response = await axios.get('/group/list', { params });
      this.groupList = response.data;
      // console.log(this.groupList);
    },

    // 그룹 상세 정보 조회
    async findGroupDetail(id) {
      try {
        const response = await axios.get(`/group/${id}`);
        const data = response.data;
        // console.log(data);
        // 기본 정보
        this.groupForm.name = data.groupInfo.NAME;
        this.groupForm.description = data.groupInfo.DESCRIPTION;

        // 소속 멤버 세팅
        this.selectedMembers = data.members.map((m) => ({
          id: m.uid,
          name: m.uname,
          email: m.uemail
        }));

        // 참여 프로젝트 세팅
        this.selectedProjects = data.projects.map((p) => ({
          id: p.projectId,
          title: p.projectName,
          role: p.roleId ? p.roleId.split(',') : []
        }));
      } catch (err) {
        console.error(err);
      }
    },

    // 그룹 등록
    async insertGroups() {
      const payload = {
        name: this.groupForm.name,
        description: this.groupForm.description,
        userIds: this.selectedMembers.map((m) => m.id),
        projects: this.selectedProjects.map((p) => ({
          projectId: p.id,
          roleIds: p.role
        }))
      };

      await axios.post('/group/create', payload);

      this.groupForm = { name: '', description: '' };
      this.selectedMembers = [];
      this.selectedProjects = [];
    },

    // 그룹 수정
    async updateGroup(id) {
      const payload = {
        id: id,
        name: this.groupForm.name,
        description: this.groupForm.description,
        userIds: this.selectedMembers.map((m) => m.id),
        projects: this.selectedProjects.map((p) => ({
          projectId: p.id,
          roleIds: p.role
        }))
      };

      await axios.put(`/group/${id}`, payload);
    },

    // 그룹 삭제
    async deleteGroup(id) {
      await axios.delete(`/group/${id}`);
    },
    // 그룹에 멤버 추가하는 함수
    addMembers(newMembers) {
      newMembers.forEach((member) => {
        const isExist = this.selectedMembers.some((m) => m.id === member.id);

        if (!isExist) {
          this.selectedMembers.push(member);
        }
      });
    },

    // 그룹에 멤버 삭제하는 함수
    removeMember(userId) {
      this.selectedMembers = this.selectedMembers.filter((m) => m.id !== userId);
    },

    // 그룹에 프로젝트 추가하는 함수
    addProjects(newProjects) {
      newProjects.forEach((project) => {
        if (!this.selectedProjects.some((p) => p.id === project.id)) {
          this.selectedProjects.push({
            id: project.id,
            title: project.title,
            role: project.role || []
          });
        }
      });
    },

    // 그룹에 프로젝트 삭제하는 함수
    removeProject(id) {
      this.selectedProjects = this.selectedProjects.filter((p) => p.id !== id);
    },

    // 데이터 전부 초기화
    resetSelectedData() {
      this.groupForm = { name: '', description: '' };
      this.selectedMembers = [];
      this.selectedProjects = [];
    }
  }
  // ,persist: true
});
