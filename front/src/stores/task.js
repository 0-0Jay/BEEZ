import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useTaskStore = defineStore('task', {
  // state
  state: () => ({
    typeList: [],
    cateList: []
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
    }
  },
  persist: true
});
