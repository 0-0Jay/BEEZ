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
    versionList: [],
    commonCodeList: [],
    journalList: [],
    task: ref(null),
    overview: [],
    spent: []
  }),
  // getters
  // actions
  actions: {
    async findTypeList() {
      const res = await axios.get(`/type`);
      this.typeList = res.data;
    },
    async insertType(data) {
      const res = await axios.post(`/type`, data);
    },
    async updateType(data) {
      const res = await axios.put(`/type`, data);
    },
    async deleteType(id) {
      const res = await axios.delete(`/type/${id}`);
    },
    async findCateList() {
      const res = await axios.get(`/category`);
      this.cateList = res.data;
    },
    async insertCate(data) {
      const res = await axios.post(`/category`, data);
    },
    async updateCate(data) {
      const res = await axios.put(`/category`, data);
    },
    async deleteCate(id) {
      const res = await axios.delete(`/category/${id}`);
    },
    async findMember(projectId) {
      const res = await axios.get(`/task/member/${projectId}`);
      this.memberList = res.data;
    },
    async findTaskList(projectId, userId) {
      const res = await axios.get(`/task/${projectId}/${userId}`);
      this.taskList = res.data;
    },
    async findCommonCodeList() {
      const res = await axios.get(`/task/common`);
      this.commonCodeList = res.data;
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
      return res.data;
    },
    async updateTask(data) {
      const res = await axios.put(`/task`, data, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
    },
    async findTaskDetail(taskId) {
      const res = await axios.get(`/task/${taskId}`);
      this.task = res.data;
    },
    async insertTaskReply(data) {
      const res = await axios.post(`/task/reply`, data);
    },
    async findJournalDetail(id) {
      const res = await axios.get(`/task/${id}/journal`);
      this.journalList = res.data;
    },
    async deleteTask(id) {
      const res = await axios.delete(`/task/${id}`);
      this.task = ref(null);
    },
    async insertTaskTime(data) {
      const res = await axios.post(`/task/time`, data);
    },
    async insertTaskLink(data) {
      const res = await axios.post(`/task/link`, data);
    },
    async deleteTaskLink(id) {
      const res = await axios.delete(`/task/link/${id}`);
    },
    async findTaskOverview(id) {
      const res = await axios.get(`/task/overview/${id}`);
      this.overview = res.data;
    },
    async findSpentOverview(id) {
      const res = await axios.get(`/task/spent/${id}`);
      this.spent = res.data;
    }
    // async downloadFile(file) {
    //   const res = await axios.get(`/api/file/${file.storedName}`);
    // }
  },
  persist: {
    storage: sessionStorage
  }
});
