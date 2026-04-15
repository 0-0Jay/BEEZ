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
    async fetchDocumentList(projectId, userId) {
      try {
        const response = await axios.get(`/document/list/${projectId}`, {
          params: { userId }
        });
        this.documentList = response.data;
      } catch (error) {
        console.error('목록 조회 실패:', error);
        throw error;
      }
    },

    // 즐겨찾기 토글 추가
    async toggleFavorite(userId, documentId) {
      try {
        await axios.post('/document/favorite', { userId, documentId });
        // 목록에서 해당 문서의 isMarked 즉시 반전 (API 재호출 없이)
        const doc = this.documentList.find((d) => d.id === documentId);
        if (doc) doc.isMarked = doc.isMarked === 'Y' ? 'N' : 'Y';

        // 상세 화면 업데이트 (추가)
        if (this.currentDocument?.id === documentId) {
          this.currentDocument.isMarked = this.currentDocument.isMarked === 'Y' ? 'N' : 'Y';
        }
      } catch (error) {
        console.error('즐겨찾기 실패:', error);
        console.log(this.currentDocument?.id, typeof this.currentDocument?.id);
        console.log(documentId, typeof documentId);
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
    async updateDocument(updateRequest, newFiles, deletedFileIds) {
      try {
        const formData = new FormData();

        const fileUpdates = (deletedFileIds || []).map((id) => ({
          targetFileDetailId: id,
          isDeleted: true
        }));
        console.log('deletedFileIds:', deletedFileIds); // ← 추가
        console.log('fileUpdates:', fileUpdates); // ← 추가

        updateRequest.fileUpdates = fileUpdates;

        formData.append('document', new Blob([JSON.stringify(updateRequest)], { type: 'application/json' }));

        //새파일 담기
        if (newFiles && newFiles.length > 0) {
          newFiles.forEach((file) => {
            formData.append('newFile', file);
          });
        }
        const response = await axios.put('/document/update', formData);
        return response.data;
      } catch (error) {
        console.error('문서 수정 실패:', error);
        throw error;
      }
    }
  }
});
