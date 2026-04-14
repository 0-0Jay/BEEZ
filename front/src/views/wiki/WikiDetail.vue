<script setup>
import { useWikiStore } from '@/stores/wiki';
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const wikiStore = useWikiStore(); //스토어 연결
const route = useRoute();
const router = useRouter();

//스토어랑 연결
const projectInfo = computed(() => wikiStore.projectInfo);
const wikiDetail = computed(() => wikiStore.wikiDetail);

const relatedLinks = computed(() => {
  if (!wikiDetail.value || !wikiDetail.value.links) return [];
  const rawLinks = wikiDetail.value.links;
  try {
    // 문자열이면 파싱하고, 이미 배열이면 그대로 반환
    return typeof rawLinks === 'string' ? JSON.parse(rawLinks) : rawLinks;
  } catch (e) {
    console.error('링크 데이터 파싱 실패:', e);
    return [];
  }
});

// 버튼 핸들러
const handleEdit = () => {
  const { projectId, wikiId } = route.params;
  router.push({ name: 'WikiEdit', params: { projectId, wikiId } });
};
const handleHistory = () => {
  const { projectId, wikiId } = route.params;
  router.push({ name: 'WikiHistory', params: { projectId, wikiId } });
};

//목차 데이터를 담을 상태
const tocList = ref([]);

//본문에서 제목을 추출하여 목차 리스트를 만드는 함수
const generateTOC = () => {
  if (!wikiDetail.value || !wikiDetail.value.content) return;

  const contentEL = document.createElement('div');
  contentEL.innerHTML = wikiDetail.value.content;

  const headings = contentEL.querySelectorAll('h2, h3');
  const newToc = [];
  let h2Count = 0;
  let h3Count = 0;

  //목차 생성용
  headings.forEach((heading, index) => {
    const id = `section-${index}`;
    heading.setAttribute('id', id);

    const rawTitle = heading.textContent;
    const cleanTitle = rawTitle.replace(/^[\d.]+\s*/, '').trim(); // 1.1.1. 방지 하기 위함

    if (heading.tagName === 'H2') {
      h2Count++;
      h3Count = 0; //제목 1 바뀌면 제목 2카운터 초기화
      newToc.push({ id, title: cleanTitle, number: `${h2Count}.`, sub: [] });
    } else if (heading.tagName === 'H3') {
      h3Count++;
      if (newToc.length === 0) {
        // 부모 H2 없으면 → H3를 그냥 최상위 항목으로 올려버림
        h2Count++;
        h3Count = 0;
        newToc.push({ id, title: cleanTitle, number: `${h2Count}.`, sub: [] });
      } else {
        const number = `${h2Count}.${h3Count}`;
        newToc[newToc.length - 1].sub.push({ id, title: cleanTitle, number });
      }
    }
  });

  tocList.value = newToc;
  wikiDetail.value.content = contentEL.innerHTML;
};

//페이지 로드 될 때 실행
onMounted(async () => {
  const { projectId, wikiId } = route.params;

  console.log('projectId:', projectId);
  console.log('wikiId:', wikiId);

  if (wikiId) {
    await wikiStore.fetchWikiDetail(wikiId);
  }

  if (projectId) {
    await wikiStore.fetchProjectData(projectId);
  }
});

// 3. 데이터가 나중에 로드되거나 변경될 때를 대비한 감시 (핵심)
watch(
  () => wikiDetail.value.content,
  (newVal) => {
    if (newVal) {
      // DOM 업데이트 이후에 실행되도록 nextTick 사용
      nextTick(() => {
        generateTOC();
      });
    }
  },
  { immediate: true }
);

// 링크 화면에 출력하게 하기
</script>

<template>
  <div class="wiki-editor-page p-10">
    <div class="section-marker header-margin">
      <div class="wiki-header-card">
        <div class="wiki-header-top">
          <div>
            <div class="wiki-header-left">
              <h1 class="text-2xl font-bold text-[#1A1816]">WIKI</h1>
              <!-- <p>설명 들어가면 좋을 것 같음 - wikiInfo 백단 처리</p> -->
              <span class="badge-status">{{ wikiDetail.versionName || '-' }}</span>
            </div>
            <p class="wiki-subtitle">{{ wikiDetail.wikiInfo || '위키 한 줄 설명 데이터가 없습니다' }}</p>
          </div>
          <div class="wiki-header-right">
            <div class="btn-group">
              <button class="btn btn-primary" @click="handleEdit">편집</button>
              <button class="btn btn-secondary" @click="handleHistory">히스토리</button>
            </div>
            <div class="wiki-meta">
              <span>작성자 - {{ projectInfo.userName }}</span>
              <span>작성일 - {{ wikiDetail.createdOn ? wikiDetail.createdOn.toString().substring(0, 10) : '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="section-marker">
        <div class="toc-card">
          <div class="toc-heading">목차</div>
          <ul class="toc-list" v-if="tocList.length > 0">
            <template v-for="item in tocList" :key="item.id">
              <li>
                <a :href="`#${item.id}`">{{ item.number }} {{ item.title }}</a>
              </li>
              <ul v-if="item.sub && item.sub.length > 0" class="toc-list toc-sub">
                <li v-for="sub in item.sub" :key="sub.id">
                  <a :href="`#${sub.id}`">{{ sub.number }} {{ sub.title }}</a>
                </li>
              </ul>
            </template>
          </ul>
          <p v-else class="related-placeholder" style="font-size: 12px">제목을 추가하면 목차가 생성됩니다.</p>
        </div>
      </div>

      <div class="info-card">
        <div class="info-card-title">프로젝트 - 기본정보</div>
        <br />
        <!-- <div class="info-row">
          <span class="info-label">상태 : </span>
          <span class="badge-status">{{ projectInfo.status || '데이터가 없습니다' }}</span>
        </div> -->

        <div class="info-row">
          <span class="info-label">프로젝트번호 :</span>
          <span class="info-value status-badge">{{ projectInfo.id || '데이터가 없습니다' }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">프로젝트 설명 :</span>
          <span class="info-value status-badge">{{ projectInfo.description || '데이터가 없습니다' }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">프로젝트 생성자명 :</span>
          <span class="info-value status-badge">{{ projectInfo.userName || '데이터가 없습니다' }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">시작일 :</span>
          <span class="info-value status-badge">
            {{ projectInfo.startDate ? projectInfo.startDate.replace('T', ' ').substring(0, 10) : '데이터가 없습니다' }}
          </span>
        </div>

        <div class="info-row">
          <span class="info-label">종료일 :</span>
          <span class="info-value status-badge">
            {{ projectInfo.endDate ? projectInfo.endDate.replace('T', ' ').substring(0, 10) : '데이터가 없습니다' }}
          </span>
        </div>
      </div>

      <div class="section-marker">
        <div class="related-card">
          <div v-if="relatedLinks.length > 0" class="link-list-container">
            <div class="toc-heading" style="margin-bottom: 12px">관련 문서 링크</div>
            <ul class="related-link-list">
              <li v-for="(link, index) in relatedLinks" :key="index" class="link-item">
                <i class="link-icon">🔗</i>
                <a :href="link.url" target="_blank" rel="noopener noreferrer" class="link-text"> {{ link.title }} </a>
              </li>
            </ul>
          </div>
          <span v-else class="related-placeholder">관련 문서 링크가 없습니다.</span>
        </div>
      </div>
    </div>

    <div class="section-marker">
      <div class="content-card">
        <div class="body-text" v-html="wikiDetail.content"></div>
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
  /* padding-bottom: 0px; */
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
/* .badge-wiki {
  font-size: 20px;
  background: #e8f0fe;
  border: 1px solid #90b8f8;
  border-radius: 20px;
  padding: 2px 10px;
  color: #1a56c4;
  font-weight: bold;
} */
.wiki-title {
  font-size: 20px;
  font-weight: bold;
  color: #111;
}
.badge-version {
  font-size: 11px;
  background: #06c4523d;
  border: 1px solid #0aeb19;
  border-radius: 20px;
  padding: 2px 10px;
  color: #27a33c;
  font-weight: bold;
}
.wiki-subtitle {
  font-size: 12px;
  font-weight: 600;
  color: #444;
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

/* 관련 문서 링크 스타일 */
.link-list-container {
  width: 100%;
}
.related-link-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.link-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
}
.link-icon {
  font-size: 12px;
}
.link-text {
  font-size: 13px;
  color: #1a56c4;
  text-decoration: none;
  transition: color 0.2s;
}
.link-text:hover {
  text-decoration: underline;
  color: #0d3da3;
}

.wiki-editor-page {
  /* 브라우저 화면 높이의 100%를 최소 높이로 잡음 */
  min-height: 100vh;
  /* 내용이 많아지면 자동으로 늘어남 */
  padding-bottom: 770x; /* 하단 여백을 주면 스크롤 확인이 더 쉽습니다 */
}

.content-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 24px 30px;
  min-height: 200px;
}

.content-card :deep(h1),
.content-card :deep(h2),
.content-card :deep(h3),
.content-card :deep(h4),
.content-card :deep(h5),
.content-card :deep(h6) {
  margin: 0 !important;
  padding: 0 !important;
}
</style>
