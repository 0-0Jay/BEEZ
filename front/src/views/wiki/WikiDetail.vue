<script setup>
import { useWikiStore } from '@/stores/wiki';
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const wikiStore = useWikiStore(); //스토어 연결
const route = useRoute();
//스토어랑 연결
const projectInfo = computed(() => wikiStore.projectInfo);
const wikiDetail = computed(() => wikiStore.wikiDetail);

//페이지 로드 될 때 실행
onMounted(async () => {
  const projectId = route.params.projectId;
  if (projectId) {
    await wikiStore.fetchProjectData(projectId);
    await wikiStore.fetchWikiDetail(wikiId);
  }
});

// 버튼 핸들러
const handleEdit = () => console.log('편집 페이지로 이동');
const handleHistory = () => console.log('히스토리 보기');
</script>

<template>
  <div class="wiki-editor-page p-10">
    <div class="section-marker header-margin">
      <div class="wiki-header-card">
        <div class="wiki-header-top">
          <div>
            <div class="wiki-header-left">
              <span class="badge-wiki">wiki</span>
              <span class="wiki-title">{{ projectInfo.title }}</span>
              <!-- 이거 백처리랑 wiki.js파일 작업 안되어 있음-->
              <!-- <span class="badge-version">{{ projectInfo. }}</span> -->
            </div>
            <p class="wiki-subtitle">{{ projectInfo.description }}</p>
          </div>
          <div class="wiki-header-right">
            <div class="btn-group">
              <button class="btn btn-primary" @click="handleEdit">편집</button>
              <button class="btn btn-secondary" @click="handleHistory">히스토리</button>
            </div>
            <div class="wiki-meta">
              <span>👤 작성자 {{ projectInfo.userId }} </span>
              <!-- 수정일 관련 데이터 처리 안해놓음-->
              <!-- <span>📅 {{ wikiData.updatedDate }} 수정</span> -->
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="section-marker">
        <div class="toc-card">
          <div class="toc-heading">목차</div>
          <ul class="toc-list">
            <template v-for="item in tocList" :key="item.id">
              <li>
                <a :href="`#${item.id}`">{{ item.title }}</a>
              </li>
              <ul v-if="item.sub" class="toc-list toc-sub">
                <li v-for="sub in item.sub" :key="sub.id">
                  <a :href="`#${sub.id}`">{{ sub.title }}</a>
                </li>
              </ul>
            </template>
          </ul>
        </div>
      </div>

      <div class="info-card">
        <div class="info-card-title">{{ projectInfo.title }}</div>
        <div class="info-row">
          <span class="info-label">상태 : </span>
          <span class="badge-status">{{ projectInfo.status }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">프로젝트번호 :</span>
          <span class="info-value status-badge">{{ projectInfo.id }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">프로젝트 설명 :</span>
          <span class="info-value status-badge">{{ projectInfo.description }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">프로젝트 생성자명 :</span>
          <span class="info-value status-badge">{{ projectInfo.userName }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">시작일 :</span>
          <span class="info-value status-badge">{{ projectInfo.startDate ? projectInfo.startDate.replace('T', ' ').substring(0, 10) : '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">종료일 :</span>
          <span class="info-value status-badge">{{ projectInfo.endDate ? projectInfo.endDate.replace('T', ' ').substring(0, 10) : '-' }}</span>
        </div>
      </div>

      <div class="section-marker">
        <div class="related-card">
          <span class="related-placeholder">관련 문서 링크</span>
        </div>
      </div>
    </div>

    <div class="section-marker">
      <div class="section" id="section1">
        <div class="section-title">1. 프로젝트 개요</div>
        <p class="body-text">
          앱 리디자인 프로젝트는 기존 모바일 앱의 사용자 경험(UX)을 전면 재설계하고 핵심 기능을 개선하기 위해 2025년 5월부터 시작된 프로젝트입니다. 낮은 온보딩 완료율과 사용자 이탈 문제를 해결하고, 월간 활성 사용자(MAU) 30% 증대를 목표로 합니다.
        </p>

        <div class="subsection-title" id="section1-1">1.1 사전배경</div>
        <p class="body-text">
          2024년 4분기 사용자 조사 결과, 기존 앱의 온보딩 완료율이 52%에 불과하고 앱스토어 평점이 3.8점으로 경쟁사 대비 낮은 수치를 기록했습니다. 주요 이탈 원인은 복잡한 초기 설정 플로우, 직관성이 낮은 홈 화면, 그리고 알림 과부하로 분석되었습니다.
        </p>

        <div class="subsection-title" id="section1-2">1.2 목표</div>
        <p class="body-text">이번 리디자인의 핵심 목표는 온보딩 완료율을 현재 52%에서 80% 이상으로 개선하고, 앱스토어 평점을 3.8점에서 4.5점 이상으로 높이며, MAU를 6개월 내 30% 증가시키는 것입니다.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.wiki-editor-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}

/* Reset & Base */
.wiki-wrap {
  max-width: 920px;
  margin: 24px auto;
  padding: 0 16px 60px;
  font-family: 'Malgun Gothic', sans-serif;
  color: #333;
  text-align: left;
}

/* Header */
.header-margin {
  margin-bottom: 14px;
}
.wiki-header-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 14px 20px;
}
.wiki-header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.wiki-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.badge-wiki {
  font-size: 11px;
  background: #e8f0fe;
  border: 1px solid #90b8f8;
  border-radius: 20px;
  padding: 2px 10px;
  color: #1a56c4;
  font-weight: bold;
}
.wiki-title {
  font-size: 20px;
  font-weight: bold;
  color: #111;
}
.badge-version {
  font-size: 11px;
  background: #c4780625;
  border: 1px solid #f5a623;
  border-radius: 20px;
  padding: 2px 10px;
  color: #e8920e;
  font-weight: bold;
}
.wiki-subtitle {
  font-size: 12px;
  color: #666;
  margin-top: 6px;
}

.wiki-header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}
.btn-group {
  display: flex;
  gap: 6px;
}
.btn {
  font-size: 12px;
  padding: 5px 14px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #ccc;
  transition: background 0.2s;
}
.btn-primary {
  background: #fff;
  color: #333;
}
.btn-secondary {
  background: #f0f0f0;
  color: #555;
}
.btn:hover {
  background: #f8f8f8;
}

.wiki-meta {
  display: flex;
  gap: 14px;
  font-size: 11px;
  color: #999;
}

.content-grid {
  display: grid;
  grid-template-columns: 450px 1fr 1fr;
  gap: 16px;
}

.panel {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 16px;
}

.panel-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 10px;
}

.panel-title.center {
  text-align: center;
}

/* ② 목차 */
.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toc-item a {
  color: #3d7eff;
  text-decoration: none;
  font-size: 13px;
}

.toc-item a:hover {
  text-decoration: underline;
}

.toc-item.empty {
  color: #aaa;
  font-size: 16px;
  line-height: 1.2;
}

/* ③ 프로젝트 정보 */
.info-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  font-weight: 600;
  min-width: 110px;
  font-size: 13px;
  white-space: nowrap; /* 라벨이 줄바꿈되지 않도록 설정 */
}

.info-value {
  background: #f0f0f0;
  border-radius: 4px;
  padding: 4px 12px;
  font-size: 13px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis; /* 내용이 너무 길면 ...으로 표시 */
  white-space: nowrap;
}

.status-badge {
  color: #555;
  flex: 1;
  background: #f0f0f0;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 13px;
  color: #555;
  /* 텍스트 정렬이 필요하다면 추가 */
  text-align: left;
}

/* ④ 링크 패널 */
.link-form {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.link-item-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.link-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 링크 아이템들을 감싸는 컨테이너 */
.link-items-container {
  max-height: 200px; /* 원하는 높이로 조절 (예: 아이템 2~3개 분량) */
  overflow-y: auto; /* 내용이 넘치면 세로 스크롤 생성 */
  padding-right: 4px; /* 스크롤바와 입력창 사이 간격 */
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 스크롤바 디자인 (선택 사항: 더 깔끔하게 보이게 함) */
.link-items-container::-webkit-scrollbar {
  width: 6px;
}

.link-items-container::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.link-items-container::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

/* 기존 스타일 유지 및 보정 */
.link-item-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  /* 여백이 너무 크면 스크롤이 금방 생기므로 적절히 조절 */
}

/* 편집 사유 모달 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-box {
  background: #fff;
  border-radius: 8px;
  min-width: 320px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  font-weight: 600;
  font-size: 14px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #888;
  line-height: 1;
}

.modal-body {
  padding: 16px;
}

.modal-textarea {
  width: 100%;
  height: 80px;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 8px;
  font-size: 13px;
  resize: vertical;
  box-sizing: border-box;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #eee;
}

/* TOC */
.toc-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 14px 30px;
  min-height: 100%;
}
.toc-heading {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 10px;
}
.toc-list {
  list-style: none;
  padding: 0;
}
.toc-list li {
  padding: 2px 0;
}
.toc-list a {
  font-size: 12px;
  color: #1a56c4;
  text-decoration: none;
  line-height: 1.7;
}
.toc-list a:hover {
  text-decoration: underline;
}
.toc-sub {
  padding-left: 14px;
}

/* Info Card */
.info-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px 24px;
  min-height: 100%;
}
.info-card-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #111;
}
.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 13px;
}
.info-label {
  color: #888;
  min-width: 52px;
  min-width: 120px;
  font-size: 13px;
  white-space: nowrap;
}
.badge-status {
  font-size: 11px;
  background: #eafaf1;
  border: 1px solid #82c99a;
  border-radius: 20px;
  padding: 2px 10px;
  color: #1e7e3e;
  font-weight: bold;
}

/* Related Card */
.related-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 180px;
}
.related-placeholder {
  font-size: 13px;
  color: #aaa;
  text-align: center;
}

/* Content Sections */
.section {
  margin-bottom: 2rem;
}
.section-title {
  font-size: 22px;
  font-weight: bold;
  color: #111;
  padding-bottom: 10px;
  border-bottom: 2px solid #e0e0e0;
  margin-bottom: 16px;
}
.subsection-title {
  font-size: 16px;
  font-weight: bold;
  color: #111;
  margin: 1.4rem 0 0.7rem;
  padding-left: 14px;
  border-left: 3px solid #4285f4;
}
.body-text {
  font-size: 14px;
  color: #333;
  line-height: 1.85;
}

/* Number Bubbles */
.section-marker {
  position: relative;
}
</style>
