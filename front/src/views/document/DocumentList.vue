<script setup>
import { useAuthStore } from '@/stores/auth';
import { useDocumentStore } from '@/stores/document';
import { computed, onMounted, ref } from 'vue'; // 1. ref 임포트 추가
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const docStore = useDocumentStore();
const authStore = useAuthStore(); // 즐겨찾기 정보 위함

const projectId = route.params.projectId;
const userId = authStore.user.id; //즐겨찾기 위함

const activeFav = ref(null);
const searchKeyword = ref('');
const searchDoctype = ref('');

onMounted(async () => {
  await docStore.fetchDocumentList(projectId, userId);
});

const filteredList = computed(() => {
  return docStore.documentList.filter((doc) => {
    const matchKeyword = !searchKeyword.value || doc.title.includes(searchKeyword.value);
    const matchDoctype = !searchDoctype.value || doc.doctype === searchDoctype.value;
    return matchKeyword && matchDoctype;
  });
});

//즐겨찾기 문서만
const favList = computed(() => docStore.documentList.filter((doc) => doc.isMarked === 'Y'));

// 즐겨찾기 토글
const toggleFavorite = async (docId) => {
  await docStore.toggleFavorite(userId, docId);
};

// 문서 등록 페이지로 이동하는 함수
const goToWrite = () => {
  router.push({
    name: 'DocumentWrite',
    params: { projectId: projectId }
  });
};

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

// 페이지네이션
const currentPage = ref(1);
const pageSize = 10;
const totalPages = computed(() => Math.ceil(filteredList.value.length / pageSize));
const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredList.value.slice(start, start + pageSize);
});

// 날짜 포맷 함수 추가 (YYYY-MM-DD)
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);

  if (isNaN(date)) {
    // '26/04/10 14:27:18' -> '2026-04-10' (필요시 조정)
    const parts = dateStr.split(' ')[0].split('/');
    if (parts.length === 3) {
      return `20${parts[0]}-${parts[1]}-${parts[2]}`;
    }
    return dateStr;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
};
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
          <input v-model="searchKeyword" type="text" placeholder="검색어를 입력해주세요." />
        </div>
        <span class="label">문서 유형</span>
        <div class="select-wrap">
          <select v-model="searchDoctype">
            <option value="">선택</option>
            <option>기타</option>
            <option>기획서</option>
            <option>설계서</option>
            <option>회의록</option>
            <option>보고서</option>
          </select>
        </div>
        <button class="btn btn-edit">조회</button>
      </div>
    </div>

    <!-- 즐겨찾기 -->
    <template v-if="favList.length">
      <div class="section-title">즐겨찾기 문서</div>
      <div class="fav-grid">
        <div v-for="doc in favList" :key="doc.id" class="panel fav-card" :class="{ active: activeFav === doc.id }" @click="goToDetail(doc.id)">
          <div class="fav-icon">
            <span class="radio-dot" :class="{ active: activeFav === doc.id }"></span>
          </div>
          <div class="fav-name">{{ doc.title }}</div>
          <div class="fav-meta">작성자: {{ doc.userName }} / {{ formatDate(doc.createdOn) }}</div>
        </div>
      </div>
    </template>

    <!-- 목록 테이블 -->
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

      <template v-if="docStore.loading">
        <div class="board-empty">불러오는 중...</div>
      </template>

      <template v-else-if="pagedList.length === 0">
        <div class="board-empty">
          <div class="empty-title">등록된 문서가 없습니다.</div>
          <div class="empty-sub">문서등록 버튼을 눌러 문서를 작성할 수 있습니다.</div>
        </div>
      </template>

      <div v-for="(doc, index) in pagedList" :key="doc.id" class="board-row">
        <span class="num">
          <span class="star" :class="{ active: doc.isMarked === 'Y' }" @click.stop="toggleFavorite(doc.id)">
            {{ doc.isMarked === 'Y' ? '★' : '☆' }}
          </span>
          {{ filteredList.length - ((currentPage - 1) * pageSize + index) }}
        </span>
        <span class="title" @click="goToDetail(doc.id)" style="cursor: pointer; color: #3d7eff">
          {{ doc.title }}
        </span>
        <span>
          <span class="badge" :class="doc.doctype">{{ doc.doctype }}</span>
        </span>
        <span>{{ doc.userName }}</span>
        <span class="date">{{ formatDate(doc.createdOn) }}</span>
      </div>
    </div>

    <div class="pagination">
      <div class="page-btn arrow" @click="currentPage > 1 && currentPage--">‹</div>
      <div v-for="p in totalPages" :key="p" class="page-btn" :class="{ active: currentPage === p }" @click="currentPage = p">
        {{ p }}
      </div>
      <div class="page-btn arrow" @click="currentPage < totalPages && currentPage++">›</div>
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
.badge.기획서 {
  background: #fde8e8;
  color: #c0392b;
}
.badge.설계서 {
  background: #e6f1fb;
  color: #185fa5;
}
.badge.회의록 {
  background: #fef0e0;
  color: #c47000;
}
.badge.보고서 {
  background: #eaf6ee;
  color: #1e7e34;
}
.badge.기타 {
  background: #f0e8fb;
  color: #6b3fa0;
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

.board-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  gap: 8px;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: #555;
}

.empty-sub {
  font-size: 13px;
  color: #aaa;
}
</style>
