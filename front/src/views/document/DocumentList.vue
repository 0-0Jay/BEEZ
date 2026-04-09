<script setup>
import { ref } from 'vue'; // 1. ref 임포트 추가
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();

const activeFav = ref(1);

// 문서 등록 페이지로 이동하는 함수
const goToWrite = () => {
  // 현재 URL의 projectId를 가져오거나, 데이터에 있는 ID를 사용하세요.
  const projectId = route.params.projectId;

  router.push({
    name: 'DocumentWrite',
    params: { projectId: projectId }
  });
};

// [추가] 문서 상세 페이지 이동 함수  ###### 연결 테스트 떄문에 이렇게 적어 넣음
//실제로 기능 구현 할때는 @click 과 v-bind 써야함
const goToDetail = (docId) => {
  const projectId = route.params.projectId; // URL의 프로젝트 ID 사용

  router.push({
    name: 'DocumentDetail',
    params: {
      projectId: projectId,
      docId: docId
    }
  });
};

// 즐겨찾기 카드 선택
document.querySelectorAll('.fav-card').forEach((card) => {
  card.addEventListener('click', () => {
    document.querySelectorAll('.fav-card').forEach((c) => {
      c.classList.remove('active');
      c.querySelector('.radio-dot').classList.remove('active');
    });
    card.classList.add('active');
    card.querySelector('.radio-dot').classList.add('active');
  });
});

// 별표 토글
document.querySelectorAll('.star').forEach((star) => {
  star.addEventListener('click', (e) => {
    e.stopPropagation();
    if (star.textContent === '☆') {
      star.textContent = '★';
      star.classList.add('active');
    } else {
      star.textContent = '☆';
      star.classList.remove('active');
    }
  });
});

// 페이지 선택
document.querySelectorAll('.page-btn:not(.arrow)').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.page-btn').forEach((b) => b.classList.remove('active'));
    btn.classList.add('active');
  });
});
</script>

<template>
  <div class="wiki-list-page">
    <h1 class="text-2xl font-bold text-[#1A1816]">문서 목록</h1>

    <div class="panel search-panel">
      <div class="search-bar">
        <div class="search-input-wrap">
          <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input type="text" placeholder="검색어를 입력해주세요." />
        </div>
        <span class="label">문서 유형</span>
        <div class="select-wrap">
          <select>
            <option value="">선택</option>
            <option>기타</option>
            <option>기획서</option>
            <option>견적서</option>
            <option>회의록</option>
            <option>보고서</option>
          </select>
        </div>
        <button class="btn btn-edit">조회</button>
      </div>
    </div>

    <div class="section-title">즐겨찾기 문서 - 시간안에 안되면 날리겠음</div>
    <div class="fav-grid">
      <div v-for="i in 4" :key="i" class="panel fav-card" :class="{ active: activeFav === i }" @click="activeFav = i">
        <div class="fav-icon">
          <span class="radio-dot" :class="{ active: activeFav === i }"></span>
        </div>
        <div class="fav-name">프로젝트 기획안_{{ i }}</div>
        <div class="fav-meta">작성자: 곽현우 / 2026.03.19</div>
      </div>
    </div>

    <div class="table-actions">
      <button class="btn btn-edit" @click="goToWrite">문서등록</button>
    </div>
    <div class="panel table-panel">
      <div class="board-header">
        <span>번호</span>
        <span>글 제목</span>
        <span>문서유형</span>
        <span>작성자</span>
        <span>작성일자</span>
      </div>
      <div v-for="n in 5" :key="n" class="board-row">
        <span class="num">
          <span class="star" :class="{ active: n % 2 === 0 }">{{ n % 2 === 0 ? '★' : '☆' }}</span>
          {{ 6 - n }}
        </span>

        <span class="title" @click="goToDetail(n)" style="cursor: pointer; color: #3d7eff"> 해당 프로젝트와 관련하여 작성된 문서의 제목입니다. (ID: {{ n }}) </span>

        <span
          ><span class="badge" :class="n === 3 ? '기획서' : '기획안'">{{ n === 3 ? '기획서' : '기획안' }}</span></span
        >
        <span>곽현우</span>
        <span class="date">2026.03.19</span>
      </div>
    </div>

    <div class="pagination">
      <div class="page-btn arrow">‹</div>
      <div class="page-btn active">1</div>
      <div v-for="p in 4" :key="p + 1" class="page-btn">{{ p + 1 }}</div>
      <div class="page-btn arrow">›</div>
    </div>
  </div>
</template>

<style scoped>
/* 섹션 타이틀 스타일 수정 */
.h1 {
  margin: 1.5rem 0 0rem 0; /* 상단 여백 1.5rem, 하단 여백 0.5rem */
  font-family: inherit; /* 부모의 폰트 계승 */
  font-weight: 700; /* 굵게 */
  line-height: 1.5; /* 줄 간격 */
  color: #1a1816; /* var(--text-color) 대신 구체적인 색상 지정 */
  font-size: 16px; /* 적절한 크기 추가 */
}

/* WIKI 페이지 스타일 가이드 적용 */
.wiki-list-page {
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

.project-desc {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 패널 공통 (WIKI 코드의 .panel 스타일) */
.panel {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 16px;
}

/* 검색바 조정 */
.search-panel {
  padding: 12px 20px;
}
.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 6px 12px;
  background: #fff;
}
.search-input-wrap input {
  border: none;
  outline: none;
  width: 100%;
}

.select-wrap select {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 6px 12px;
  background: #fff;
}

/* 버튼 스타일 (WIKI 스타일 계승) */
.btn {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
}
.btn-edit {
  background: #e8920e;
  color: #fff;
}
.btn-cancel {
  background: #ffffff;
  border: 1px solid #bbb;
  color: #555;
}

/* 즐겨찾기 그리드 (화면 전체 활용) */
.fav-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  width: 100%; /* 부모 너비에 꽉 차게 설정 */
}

/* 카드의 최소 높이나 너비가 너무 작아지지 않게 조절 (선택 사항) */
.fav-card {
  cursor: pointer;
  transition: all 0.2s;
  min-width: 0; /* grid 내부에서 내용물이 박스를 뚫고 나가는 것 방지 */
}
.fav-card.active {
  border-color: #e8920e;
}
.radio-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #ddd;
}
.radio-dot.active {
  background: #e8920e;
  border-color: #e8920e;
}
.fav-name {
  font-weight: bold;
  margin: 4px 0;
}
.fav-meta {
  font-size: 12px;
  color: #888;
}

/* 테이블 스타일 */
.table-actions {
  display: flex;
  justify-content: flex-end;
}
.table-panel {
  padding: 0; /* 내부 행들이 꽉 차도록 */
  overflow: hidden;
}
.board-header,
.board-row {
  display: grid;
  grid-template-columns: 80px 1fr 120px 100px 120px;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}
.board-header {
  background: #fafafa;
  font-weight: bold;
  color: #666;
}
.board-row:last-child {
  border-bottom: none;
}
.board-row:hover {
  background: #f9f9f9;
}

.num {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.star {
  color: #ccc;
  cursor: pointer;
}
.star.active {
  color: #e8920e;
}
.badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.badge.기획안 {
  background: #eaf3de;
  color: #3b6d11;
}
.badge.기획서 {
  background: #e6f1fb;
  color: #185fa5;
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}
.page-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  border-radius: 4px;
}
.page-btn.active {
  background: #e8920e;
  color: #fff;
  border-color: #e8920e;
}
</style>
