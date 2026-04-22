import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useWorkflowStore = defineStore('workflow', {
  // state
  state: () => ({
    statusList: [],
    workflowMatrices: {
      Z0: {},
      Z1: {},
      Z2: {}
    }
  }),
  // getters
  // actions
  actions: {
    // 업무흐름 조회
    async findWorkflowList(params) {
      const response = await axios.get('/workflow/list', { params });

      // 데이터가 아예 없거나 빈 배열일 때
      if (!response.data || response.data.length === 0) {
        const defaultMatrix = this.generateDefaultMatrix(params.conditionType);
        this.workflowMatrices[params.conditionType] = defaultMatrix;
        return defaultMatrix;
      }

      // 데이터가 있을 때는 기존처럼 변환
      const matrix = {};
      response.data.forEach((item) => {
        if (!matrix[item.beforeCode]) matrix[item.beforeCode] = {};
        matrix[item.beforeCode][item.afterCode] = item.isAllow === 'R1';
      });

      this.workflowMatrices[params.conditionType] = matrix;
      return matrix;
    },

    // 일감 유형 공통 코드 조회
    async findTaskStatus(groupValue) {
      const response = await axios.get(`/workflow/taskStatus/${groupValue}`);
      this.statusList = response.data;
    },

    // 기본 매트릭스 만드는 함수
    generateDefaultMatrix(type) {
      const matrix = {};

      if (!this.statusList || this.statusList.length === 0) return {};

      this.statusList.forEach((before, bIdx) => {
        matrix[before.key] = {};
        this.statusList.forEach((after, aIdx) => {
          matrix[before.key][after.key] = bIdx === aIdx;
        });
      });
      return matrix;
    },

    // 업무흐름 등록
    async insertWorkflow(payload) {
      await axios.post('/workflow/create', payload);
    },

    // 업무흐름 복사
    async copyWorkflow(payload) {
      await axios.post('/workflow/copy', payload);
    }
  }
  // ,persist: true
});
