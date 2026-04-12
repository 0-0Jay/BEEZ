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
      createdOn: '',
      wikiInfo: '', // back작업 안되어있음 확인해야함
      links: ''
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
      const isNew = !saveData.wikiRequest.id; // wikiId 없으면 신규

      if (isNew) {
        // 신규 → insert
        const response = await axios.post('/wiki/insert', saveData);
        if (saveData.versionRequest) {
          this.wikiDetail.content = saveData.versionRequest.content;
          this.wikiDetail.id = saveData.wikiRequest.id;
        }
        return response.data;
      } else {
        // 수정 → update
        const response = await axios.post('/wiki/update', saveData);
        if (saveData.versionRequest) {
          this.wikiDetail.content = saveData.versionRequest.content;
        }
        return response.data;
      }
    },

    // 특정 위키의 본문 내용 가져오기
    async fetchWikiDetail(wikiId) {
      this.loading = true;
      try {
        const response = await axios.get(`/wiki/detail/${wikiId}`);

        // 백엔드 응답 데이터를 가공하기 위해 변수에 담음
        const data = response.data;

        // links가 문자열(JSON string)로 넘어올 경우 객체 배열로 변환
        if (data.links && typeof data.links === 'string') {
          try {
            data.links = JSON.parse(data.links);
          } catch (e) {
            console.error('링크 데이터 파싱 에러:', e);
            data.links = []; // 파싱 실패 시 빈 배열 처리
          }
        } else if (!data.links) {
          data.links = []; // 데이터가 없을 경우 빈 배열 처리
        }

        // 파싱된 data를 상태에 할당 (기존 response.data 사용 시 파싱 전 데이터가 들어감)
        this.wikiDetail = data;
        return data;
      } catch (error) {
        console.error('위키 상세 조회 실패:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    // fetchWikiDetail 아래에 추가
    async fetchWikiByProject(projectId) {
      this.loading = true;
      try {
        const response = await axios.get(`/wiki/project/${projectId}/wiki`);
        return response.data; // { id, ... } 또는 null
      } catch (error) {
        // 위키가 없을 때 404로 오는 경우 null 반환
        if (error.response && error.response.status === 404) {
          return null;
        }
        console.error('위키 존재 여부 확인 실패:', error);
        return null;
      } finally {
        this.loading = false;
      }
    },
    async fetchWikiVersionList(wikiId) {
      this.loading = true;
      try {
        const response = await axios.get(`/wiki/history/${wikiId}`);
        return response.data;
      } catch (error) {
        console.error('위키 히스토리 조회 실패:', error);
        return [];
      } finally {
        this.loading = false;
      }
    }
  }, //#### action end
  // 새로고침 시 데이터 유지를 위한 persist 설정
  persist: {
    storage: sessionStorage
  }
});
