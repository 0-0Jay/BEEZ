<script setup>
import { ref } from 'vue';

// 1. 상태 관리 (추후 API 데이터로 대체 가능)
const wikiData = ref({
  title: 'YEDAM PMS TOOL 프로젝트',
  version: 'v 3.0',
  subtitle: '사용자 경험 개선을 목표로 한 PMS TOOL 프로젝트의 전반적인 배경, 목표, 범위, 프로세스를 정리한 공식 위키페이지 입니다',
  author: '000',
  updatedDate: '2026.03.20',
  status: '진행중',
  endDate: '2026.04.27',
  pm: '000'
});

// 2. 목차 데이터
const tocList = ref([
  {
    id: 'section1',
    title: '1. 프로젝트 개요',
    sub: [
      { id: 'section1-1', title: '1.1 사전 배경' },
      { id: 'section1-2', title: '1.2 목표' }
    ]
  },
  { id: 'section2', title: '2. 팀구성', sub: [{ id: 'section2-1', title: '2.1 역할 및 담당자' }] },
  { id: 'section3', title: '3. 프로젝트 범위' },
  { id: 'section4', title: '4. 일정 및 마일스톤' },
  {
    id: 'section5',
    title: '5. 기술스택',
    sub: [
      { id: 'section5-1', title: '5.1 프론트엔드' },
      { id: 'section5-2', title: '5.2 백엔드' }
    ]
  },
  { id: 'section6', title: '6. 관련문서 링크' }
]);

// 버튼 핸들러
const handleEdit = () => console.log('편집 페이지로 이동');
const handleHistory = () => console.log('히스토리 보기');
</script>

<template>
  <div class="wiki-wrap">
    <div class="section-marker header-margin">
      <div class="wiki-header-card">
        <div class="wiki-header-top">
          <div>
            <div class="wiki-header-left">
              <span class="badge-wiki">wiki</span>
              <span class="wiki-title">{{ wikiData.title }}</span>
              <span class="badge-version">{{ wikiData.version }}</span>
            </div>
            <p class="wiki-subtitle">{{ wikiData.subtitle }}</p>
          </div>
          <div class="wiki-header-right">
            <div class="btn-group">
              <button class="btn btn-primary" @click="handleEdit">편집</button>
              <button class="btn btn-secondary" @click="handleHistory">히스토리</button>
            </div>
            <div class="wiki-meta">
              <span>👤 {{ wikiData.author }} 작성</span>
              <span>📅 {{ wikiData.updatedDate }} 수정</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="grid3-container">
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

      <div class="section-marker">
        <div class="info-card">
          <div class="info-card-title">{{ wikiData.title }}</div>
          <div class="info-row">
            <span class="info-label">상태</span>
            <span class="badge-status">{{ wikiData.status }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">종료일</span>
            <span>{{ wikiData.endDate }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">PM</span>
            <span>{{ wikiData.pm }}</span>
          </div>
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
  border: 1px solid #aaa;
  border-radius: 4px;
  padding: 2px 7px;
  color: #555;
  background: #f9f9f9;
}
.wiki-title {
  font-size: 20px;
  font-weight: bold;
  color: #111;
}
.badge-version {
  font-size: 11px;
  background: #e8f0fe;
  border: 1px solid #90b8f8;
  border-radius: 20px;
  padding: 2px 10px;
  color: #1a56c4;
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

/* Grid */
.grid3-container {
  display: grid;
  grid-template-columns: 185px 1fr 210px;
  gap: 12px;
  margin-bottom: 20px;
  position: relative;
}

/* TOC */
.toc-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 14px 16px;
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
  gap: 10px;
  margin-bottom: 10px;
  font-size: 13px;
}
.info-label {
  color: #888;
  min-width: 52px;
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
