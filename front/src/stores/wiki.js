import axios from '@/stores/AxiosInstance';
import { defineStore } from 'pinia';

export const useWikiStore = defineStore('wiki', {
  // state : 위키와 관련 된 모든 데이터 상태
  state: () => ({
    // 1. 프로젝트 기본 정보 (헤더용)
    projectInfo: {
      id: '',
      identifier: '',
      title: '',
      description: '',
      parentId: '',
      startDate: null,
      endDate: null,
      status: '',
      userId: '', // 사원번호
      userName: '' // 사원 명
    },
    // 2. 위키 본문 및 버전 정보 (조회/저장용)
    wikiDetail: {
      content: '',
      userId: '',
      createdOn: ''
    },
    loading: false
  }),

  // getters
  // actions - axios 사용해서 백엔드 api와 통신
  actions: {
    //프로젝트 정보 가져오기
    async fetchProjectData(projectId) {
      this.loading = true;
      const response = await axios.get(`/wiki/project/${projectId}`);
      this.projectInfo = response.data;

      this.loading = false;
      return response.data;
    },

    //위키 신규 등록 또는 편집 저장
    async saveWiki(saveData) {
      const response = await axios.post('/wiki/insert', saveData);
      this.wikiDetail.content = saveData.versionRequest.content;
      return response.data;
    },

    //특정 위키의 본문 내용 가져오기
    async fetchWikiDetail(wikiId) {
      const response = await axios.get(`/wiki/detail/${wikiId}`);
      this.wikiDetail = response.data;
      return response.data;
    }
  },

  persist: true
});
