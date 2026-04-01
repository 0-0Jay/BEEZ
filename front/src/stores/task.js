import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useTaskStore = defineStore('task', {
  // state
  state: () => ({
    typeList: [],
    cateList: [],
    memberList: [],
    taskList: [],
    priorityList: [],
    workflowList: [],
    versionList: [],
    relationList: [],
    activityList: [],
    task: ref(null)
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
    },
    async findTaskDetail(taskId) {
      const res = await axios.get(`/task/${taskId}`);
      this.task = res.data;
    },
    async findRelationList() {
      const res = await axios.get(`/task/relation`);
      this.relationList = res.data;
    },
    async findActivityList() {
      const res = await axios.get(`/task/activity`);
      this.activityList = res.data;
    },
    async insertTaskReply(data) {
      const res = await axios.post(`/task/reply`, data);
    }
  },
  persist: true
});
