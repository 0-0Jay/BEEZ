import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useTaskStore = defineStore('task', {
  // state
  state: () => ({
    typeList: [],
    cateList: [],
    memberList: [],
    taskList: [],
    priorityList: [],
    workflowList: [],
    versionList: []
  }),
  // getters
  // actions
  actions: {
    async findTypeList() {
      const res = await axios.get(`/task/type`);
      this.typeList = res.data;
    },
    async insertType(data) {
      const res = await axios.post(`/task/type`, data);
    },
    async updateType(data) {
      const res = await axios.put(`/task/type`, data);
    },
    async deleteType(id) {
      const res = await axios.delete(`/task/type/${id}`);
    },
    async findCateList() {
      const res = await axios.get(`/task/category`);
      this.cateList = res.data;
    },
    async insertCate(data) {
      const res = await axios.post(`/task/category`, data);
    },
    async updateCate(data) {
      const res = await axios.put(`/task/category`, data);
    },
    async deleteCate(id) {
      const res = await axios.delete(`/task/category/${id}`);
    },
    async findMember(projectId) {
      const res = await axios.get(`/task/member/${projectId}`);
      this.memberList = res.data;
    },
    async findTaskList(projectId, userId) {
      const res = await axios.get(`/task/${projectId}/${userId}`);
      this.taskList = res.data;
    },
    async findPriorityList() {
      const res = await axios.get(`/task/priority`);
      this.priorityList = res.data;
    },
    async findWorkflowList() {
      const res = await axios.get(`/task/workflow`);
      this.workflowList = res.data;
    },
    async findVersionList(projectId) {
      const res = await axios.get(`/task/version/${projectId}`);
      this.versionList = res.data;
    },
    async insertTask(data) {
      const res = await axios.post(`/task`, data, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
    }
  },
  persist: true
});
