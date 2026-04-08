<script setup>
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
  <div class="wrap">
    <!-- 검색 영역 -->
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
      <button class="btn-search">조회</button>
    </div>

    <!-- 즐겨찾기 문서 -->
    <div class="section-title">즐겨찾기 문서</div>
    <div class="fav-grid">
      <div class="fav-card active">
        <div class="fav-icon"><span class="radio-dot active"></span></div>
        <div class="fav-name">프로젝트 기획서</div>
        <div class="fav-meta">작성자: 곽현우 / 2026.03.19</div>
      </div>
      <div class="fav-card">
        <div class="fav-icon"><span class="radio-dot"></span></div>
        <div class="fav-name">프로젝트 기획서.pdf</div>
        <div class="fav-meta">작성자: 곽현우 / 2026.03.19</div>
      </div>
      <div class="fav-card">
        <div class="fav-icon"><span class="radio-dot"></span></div>
        <div class="fav-name">프로젝트 기획서.txt</div>
        <div class="fav-meta">작성자: 곽현우 / 2026.03.19</div>
      </div>
      <div class="fav-card">
        <div class="fav-icon"><span class="radio-dot"></span></div>
        <div class="fav-name">프로젝트 기획서</div>
        <div class="fav-meta">작성자: 곽현우 / 2026.03.19</div>
      </div>
    </div>

    <!-- 문서등록 버튼 -->
    <div class="clearfix">
      <button class="btn-register">문서등록</button>
    </div>

    <!-- 게시글 목록 -->
    <div class="board-wrap">
      <div class="board-header">
        <span>번호</span>
        <span>글 제목</span>
        <span>문서유형</span>
        <span>작성자</span>
        <span>작성일자</span>
      </div>
      <div class="board-row">
        <span class="num" style="display: flex; align-items: center; justify-content: center; gap: 4px"> <span class="star">☆</span> 5 </span>
        <span class="title">해당 프로젝트와 관련하여 작성된 문서의 목록을 조회할 수 있는 페이지</span>
        <span><span class="badge 기획안">기획안</span></span>
        <span>곽현우</span>
        <span class="date">2026.03.19</span>
      </div>
      <div class="board-row">
        <span class="num" style="display: flex; align-items: center; justify-content: center; gap: 4px"> <span class="star active">★</span> 4 </span>
        <span class="title">해당 프로젝트와 관련하여 작성된 문서의 목록을 조회할 수 있는 페이지</span>
        <span><span class="badge 기획안">기획안</span></span>
        <span>곽현우</span>
        <span class="date">2026.03.19</span>
      </div>
      <div class="board-row">
        <span class="num" style="display: flex; align-items: center; justify-content: center; gap: 4px"> <span class="star">☆</span> 3 </span>
        <span class="title">해당 프로젝트와 관련하여 작성된 문서의 목록을 조회할 수 있는 페이지</span>
        <span><span class="badge 기획서">기획서</span></span>
        <span>곽현우</span>
        <span class="date">2026.03.19</span>
      </div>
      <div class="board-row">
        <span class="num" style="display: flex; align-items: center; justify-content: center; gap: 4px"> <span class="star active">★</span> 2 </span>
        <span class="title">해당 프로젝트와 관련하여 작성된 문서의 목록을 조회할 수 있는 페이지</span>
        <span><span class="badge 기획안">기획안</span></span>
        <span>곽현우</span>
        <span class="date">2026.03.19</span>
      </div>
      <div class="board-row">
        <span class="num" style="display: flex; align-items: center; justify-content: center; gap: 4px"> <span class="star">☆</span> 1 </span>
        <span class="title">해당 프로젝트와 관련하여 작성된 문서의 목록을 조회할 수 있는 페이지</span>
        <span><span class="badge 기획안">기획안</span></span>
        <span>곽현우</span>
        <span class="date">2026.03.19</span>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination">
      <div class="page-btn arrow">‹</div>
      <div class="page-btn">1</div>
      <div class="page-btn active">2</div>
      <div class="page-btn">3</div>
      <div class="page-btn">4</div>
      <div class="page-btn">5</div>
      <div class="page-btn">6</div>
      <div class="page-btn">7</div>
      <div class="page-btn">8</div>
      <div class="page-btn">9</div>
      <div class="page-btn">10</div>
      <div class="page-btn arrow">›</div>
    </div>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
body {
  font-family: sans-serif;
  font-size: 14px;
  color: #1a1a1a;
  background: #f5f5f5;
}
.wrap {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

/* 검색 영역 */
.search-bar {
  background: #fff;
  border: 0.5px solid #ddd;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.search-input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 0.5px solid #ccc;
  border-radius: 8px;
  padding: 6px 12px;
  background: #f9f9f9;
}
.search-input-wrap input {
  border: none;
  outline: none;
  background: transparent;
  width: 100%;
  font-size: 13px;
  color: #1a1a1a;
}
.search-input-wrap svg {
  flex-shrink: 0;
  opacity: 0.4;
}
.label {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
}
.select-wrap {
  position: relative;
  display: inline-block;
}
.select-wrap select {
  appearance: none;
  -webkit-appearance: none;
  border: 0.5px solid #ccc;
  border-radius: 8px;
  padding: 6px 32px 6px 12px;
  font-size: 13px;
  background: #fff;
  color: #1a1a1a;
  cursor: pointer;
  min-width: 110px;
}
.select-wrap::after {
  content: '';
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border: 4px solid transparent;
  border-top: 5px solid #888;
  pointer-events: none;
}
.btn-search {
  background: #c0392b;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 7px 20px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}
.btn-search:hover {
  background: #a93226;
}

/* 섹션 타이틀 */
.section-title {
  font-size: 13px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.section-title::before {
  content: '';
  display: inline-block;
  width: 3px;
  height: 14px;
  background: #c0392b;
  border-radius: 2px;
}

/* 즐겨찾기 카드 */
.fav-grid {
  display: flex;
  gap: 10px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.fav-card {
  background: #fff;
  border: 0.5px solid #ddd;
  border-radius: 12px;
  padding: 12px 16px;
  min-width: 160px;
  flex: 1;
  cursor: pointer;
  transition: border-color 0.15s;
}
.fav-card:hover {
  border-color: #bbb;
}
.fav-card .fav-icon {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}
.radio-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #ccc;
  display: inline-block;
  flex-shrink: 0;
}
.fav-card.active .radio-dot {
  border-color: #c0392b;
  background: #c0392b;
}
.fav-card .fav-name {
  font-size: 13px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 4px;
}
.fav-card .fav-meta {
  font-size: 11px;
  color: #888;
}

/* 문서등록 버튼 */
.clearfix::after {
  content: '';
  display: block;
  clear: both;
}
.btn-register {
  float: right;
  margin-bottom: 8px;
  background: #f5f5f5;
  border: 0.5px solid #ccc;
  border-radius: 8px;
  padding: 6px 14px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}
.btn-register:hover {
  background: #fff;
}

/* 게시글 목록 */
.board-wrap {
  background: #fff;
  border: 0.5px solid #ddd;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
}
.board-header {
  display: grid;
  grid-template-columns: 60px 1fr 100px 80px 90px;
  background: #f9f9f9;
  border-bottom: 0.5px solid #ddd;
  padding: 8px 0;
}
.board-header span {
  font-size: 12px;
  color: #888;
  font-weight: 500;
  text-align: center;
}
.board-header span:nth-child(2) {
  text-align: left;
  padding-left: 8px;
}
.board-row {
  display: grid;
  grid-template-columns: 60px 1fr 100px 80px 90px;
  border-bottom: 0.5px solid #eee;
  padding: 10px 0;
  align-items: center;
  cursor: pointer;
  transition: background 0.1s;
}
.board-row:last-child {
  border-bottom: none;
}
.board-row:hover {
  background: #fafafa;
}
.board-row span {
  font-size: 13px;
  color: #1a1a1a;
  text-align: center;
}
.board-row .title {
  text-align: left;
  padding-left: 8px;
}
.board-row .num {
  color: #888;
  font-size: 12px;
}
.board-row .date {
  font-size: 12px;
  color: #888;
}
.star {
  font-size: 14px;
  cursor: pointer;
  color: #ccc;
}
.star.active {
  color: #e2b007;
}

/* 뱃지 */
.badge {
  display: inline-block;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}
.badge.기획안 {
  background: #eaf3de;
  color: #3b6d11;
}
.badge.기획서 {
  background: #e6f1fb;
  color: #185fa5;
}
.badge.견적서 {
  background: #faeeda;
  color: #854f0b;
}
.badge.회의록 {
  background: #fbeaf0;
  color: #993556;
}
.badge.보고서 {
  background: #fcebeb;
  color: #a32d2d;
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}
.page-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 0.5px solid #ddd;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  color: #888;
  background: #fff;
  user-select: none;
}
.page-btn:hover {
  background: #f5f5f5;
}
.page-btn.active {
  background: #c0392b;
  color: #fff;
  border-color: #c0392b;
}
.page-btn.arrow {
  font-size: 14px;
}
</style>
