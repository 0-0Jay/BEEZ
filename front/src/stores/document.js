import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useDocumentStore = defineStore('document', {
  state: () => ({
    documentList: [], //문서 목록
    currentDocument: null, //상세 조회된 문서 데이터
    loading: false
  }),

  actions: {
    //문서 목록 조회
    async fetchDocumentList(projectId) {
      try {
        const response = await axios.get(`/document/list/${projectId}`);
        this.documentList = response.data;
      } catch (error) {
        console.error('목록 조회 실패:', error);
        throw error;
      }
    },

    //문서 작성
    async writeDocument(projectId, formData) {
      try {
        // 컨트롤러의 @PostMapping("/document/write/{projectId}")와 매칭
        const response = await axios.post(`/document/write`, formData, {
          // headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
      } catch (error) {
        console.error('문서 등록 실패:', error);
        throw error;
      }
    },

    //문서 상세 조회
    async fetchDocumentDetail(id) {
      // 파라미터 확인해
      try {
        this.currentDocument = null;
        const response = await axios.get(`/document/detail/${id}`);
        this.currentDocument = response.data;
      } catch (error) {
        console.error('상세 조회 실패:', error);
        throw error;
      }
    },

    //문서 수정
    async updateDocument(updateFormData) {
      try {
        // 컨트롤러의 @PutMapping("/document/update")와 매칭
        const response = await axios.put('/document/update', updateFormData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
      } catch (error) {
        console.error('문서 수정 실패:', error);
        throw error;
      }
    }
  }
});
