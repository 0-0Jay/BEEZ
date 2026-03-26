<script setup>
import { computed, ref } from 'vue';

// ── 예시 데이터 ────────────────────────────────────────────────
const tasks = ref([
  {
    id: 'TASK-001',
    projectId: 'PRJ-001',
    versionId: 'VER-1.2',
    title: '로그인 페이지 보안 취약점 수정',
    description: 'SQL Injection 방어 로직 추가 및 입력값 검증 강화',
    userId: '김민준',
    type: '결함',
    category: '보안',
    workflow: '진행 중',
    priority: '높음',
    plannedStart: '2025-03-01',
    plannedEnd: '2025-03-15',
    actualStart: '2025-03-02',
    actualEnd: null,
    estimatedTime: 16,
    progress: 65,
    status: '1',
    parentId: null,
    isPublic: '1',
    creator: '이서연',
    fileId: 'FILE-012'
  },
  {
    id: 'TASK-002',
    projectId: 'PRJ-001',
    versionId: 'VER-1.3',
    title: '대시보드 차트 컴포넌트 신규 개발',
    description: '실시간 데이터 시각화를 위한 Chart.js 기반 컴포넌트 구현',
    userId: '박지수',
    type: '새기능',
    category: '프론트',
    workflow: '검토 대기',
    priority: '보통',
    plannedStart: '2025-03-10',
    plannedEnd: '2025-03-28',
    actualStart: '2025-03-11',
    actualEnd: null,
    estimatedTime: 24,
    progress: 90,
    status: '1',
    parentId: null,
    isPublic: '1',
    creator: '최동현',
    fileId: null
  },
  {
    id: 'TASK-003',
    projectId: 'PRJ-002',
    versionId: 'VER-2.0',
    title: 'REST API 응답 성능 최적화',
    description: '주요 엔드포인트 쿼리 튜닝 및 캐싱 레이어 도입',
    userId: '이서연',
    type: '지원',
    category: '백',
    workflow: '완료',
    priority: '긴급',
    plannedStart: '2025-02-20',
    plannedEnd: '2025-03-05',
    actualStart: '2025-02-21',
    actualEnd: '2025-03-04',
    estimatedTime: 32,
    progress: 100,
    status: '1',
    parentId: null,
    isPublic: '0',
    creator: '김민준',
    fileId: 'FILE-009'
  }
]);

// ── 필터 상태 ───────────────────────────────────────────────────
const filters = ref({
  workflow: null,
  userId: null,
  plannedStart: null,
  plannedEnd: null,
  type: null,
  category: null,
  priority: null,
  title: '',
  includeSubProject: false
});

// ── 드롭다운 옵션 ───────────────────────────────────────────────
const workflowOptions = ['대기', '진행 중', '검토 대기', '완료', '반려'];
const userOptions = ['김민준', '박지수', '이서연', '최동현', '홍길동'];
const typeOptions = ['결함', '새기능', '지원', '테스트', '기타'];
const categoryOptions = ['보안', '백', '프론트', '디자인', '인프라', '기타'];
const priorityOptions = ['긴급', '높음', '보통', '낮음'];

// ── 담당자 드롭다운 검색 ─────────────────────────────────────────
const userSearch = ref('');
const userDropdownOpen = ref(false);
const filteredUsers = computed(() => userOptions.filter((u) => u.includes(userSearch.value)));
function selectUser(u) {
  filters.value.userId = u;
  userSearch.value = u;
  userDropdownOpen.value = false;
}
function clearUser() {
  filters.value.userId = null;
  userSearch.value = '';
}

// ── 필터 초기화 ─────────────────────────────────────────────────
function resetFilters() {
  filters.value = {
    workflow: null,
    userId: null,
    plannedStart: null,
    plannedEnd: null,
    type: null,
    category: null,
    priority: null,
    title: '',
    includeSubProject: false
  };
  userSearch.value = '';
}

// ── 페이징 ─────────────────────────────────────────────────────
const currentPage = ref(1);
const pageSize = 10;
const pageBlockSize = 10;

const totalPages = computed(() => Math.max(1, Math.ceil(tasks.value.length / pageSize)));
const currentBlock = computed(() => Math.ceil(currentPage.value / pageBlockSize));
const blockStart = computed(() => (currentBlock.value - 1) * pageBlockSize + 1);
const blockEnd = computed(() => Math.min(blockStart.value + pageBlockSize - 1, totalPages.value));
const pageNumbers = computed(() => {
  const arr = [];
  for (let i = blockStart.value; i <= blockEnd.value; i++) arr.push(i);
  return arr;
});
const hasPrevBlock = computed(() => currentBlock.value > 1);
const hasNextBlock = computed(() => blockEnd.value < totalPages.value);

function goPage(p) {
  currentPage.value = p;
}
function prevBlock() {
  if (hasPrevBlock.value) currentPage.value = blockStart.value - 1;
}
function nextBlock() {
  if (hasNextBlock.value) currentPage.value = blockEnd.value + 1;
}

const pagedTasks = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return tasks.value.slice(start, start + pageSize);
});

// ── 유틸 ───────────────────────────────────────────────────────
function progressColor(p) {
  if (p >= 100) return '#C97700';
  if (p >= 60) return '#F5A623';
  if (p >= 30) return '#FFDA7A';
  return '#E5E2D9';
}

const priorityStyle = {
  긴급: { bg: '#3D2000', text: '#FFF0C2' },
  높음: { bg: '#9A5800', text: '#FFFBF0' },
  보통: { bg: '#E5E2D9', text: '#3A3835' },
  낮음: { bg: '#F2F0EB', text: '#6E6B65' }
};

const workflowStyle = {
  대기: { bg: '#F2F0EB', text: '#6E6B65' },
  '진행 중': { bg: '#FFF0C2', text: '#9A5800' },
  '검토 대기': { bg: '#FFDA7A', text: '#3D2000' },
  완료: { bg: '#C97700', text: '#FFFBF0' },
  반려: { bg: '#3A3835', text: '#FFF0C2' }
};
</script>

<template>
  <div class="task-page">
    <!-- ── 페이지 헤더 ───────────────────────────────────────── -->
    <div class="page-header">
      <div class="header-left">
        <span class="header-eyebrow">PRJ-001 · 버전 1.3</span>
        <h1 class="header-title">일감 목록</h1>
      </div>
      <button class="btn-add">
        <span class="btn-add-icon">＋</span>
        일감 추가
      </button>
    </div>

    <!-- ── 필터 카드 ────────────────────────────────────────── -->
    <div class="filter-card">
      <div class="filter-card-header">
        <span class="filter-card-title"><i class="pi pi-search" style="font-size: 0.8rem"></i> 검색 필터</span>
      </div>

      <div class="filter-grid">
        <!-- 진행 상태 -->
        <div class="filter-field">
          <label class="filter-label">진행 상태</label>
          <select v-model="filters.workflow" class="filter-select">
            <option :value="null">전체</option>
            <option v-for="w in workflowOptions" :key="w" :value="w">{{ w }}</option>
          </select>
        </div>

        <!-- 담당자 (검색 가능 드롭다운) -->
        <div class="filter-field" style="position: relative">
          <label class="filter-label">담당자</label>
          <div class="searchable-dropdown">
            <input v-model="userSearch" class="filter-input" placeholder="이름으로 검색" @focus="userDropdownOpen = true" @blur="setTimeout(() => (userDropdownOpen = false), 150)" />
            <button v-if="filters.userId" class="clear-btn" @click="clearUser">✕</button>
            <div v-if="userDropdownOpen" class="dropdown-list">
              <div v-for="u in filteredUsers" :key="u" class="dropdown-item" @mousedown.prevent="selectUser(u)">{{ u }}</div>
              <div v-if="filteredUsers.length === 0" class="dropdown-empty">결과 없음</div>
            </div>
          </div>
        </div>

        <!-- 마감일 범위 -->
        <div class="filter-field date-range-field">
          <label class="filter-label">마감일</label>
          <div class="date-range">
            <input v-model="filters.plannedStart" type="date" class="filter-input date-input" />
            <span class="date-sep">~</span>
            <input v-model="filters.plannedEnd" type="date" class="filter-input date-input" />
          </div>
        </div>

        <!-- 일감 유형 -->
        <div class="filter-field">
          <label class="filter-label">일감 유형</label>
          <select v-model="filters.type" class="filter-select">
            <option :value="null">전체</option>
            <option v-for="t in typeOptions" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>

        <!-- 일감 범주 -->
        <div class="filter-field">
          <label class="filter-label">일감 범주</label>
          <select v-model="filters.category" class="filter-select">
            <option :value="null">전체</option>
            <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>

        <!-- 우선순위 -->
        <div class="filter-field">
          <label class="filter-label">우선순위</label>
          <select v-model="filters.priority" class="filter-select">
            <option :value="null">전체</option>
            <option v-for="p in priorityOptions" :key="p" :value="p">{{ p }}</option>
          </select>
        </div>

        <!-- 제목 -->
        <div class="filter-field title-field">
          <label class="filter-label">제목</label>
          <input v-model="filters.title" class="filter-input" placeholder="일감 제목 검색..." />
        </div>
      </div>

      <!-- 하단 옵션 행 -->
      <div class="filter-footer">
        <label class="checkbox-label">
          <input v-model="filters.includeSubProject" type="checkbox" class="filter-checkbox" />
          <span class="checkbox-custom"></span>
          하위 프로젝트 일감 보기
        </label>
        <div class="filter-actions">
          <button class="btn-reset" @click="resetFilters">초기화</button>
          <button class="btn-search">조회</button>
        </div>
      </div>
    </div>

    <!-- ── 일감 목록 표 ─────────────────────────────────────── -->
    <div class="table-card">
      <div class="table-meta">
        <span class="table-count"
          >총 <strong>{{ tasks.length }}</strong
          >개</span
        >
      </div>

      <div class="table-wrapper">
        <table class="task-table">
          <thead>
            <tr>
              <th class="col-id">번호</th>
              <th class="col-type">유형</th>
              <th class="col-status">상태</th>
              <th class="col-priority">우선순위</th>
              <th class="col-title">제목</th>
              <th class="col-progress">진척도</th>
              <th class="col-assignee">담당자</th>
              <th class="col-due">마감일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(task, idx) in pagedTasks" :key="task.id" class="task-row" :class="{ 'row-even': idx % 2 === 0 }">
              <!-- 번호 -->
              <td class="col-id">
                <span class="task-id">{{ task.id }}</span>
              </td>

              <!-- 유형 -->
              <td class="col-type">
                <span class="type-badge">
                  {{ task.type }}
                </span>
              </td>

              <!-- 상태 -->
              <td class="col-status">
                <span
                  class="status-badge"
                  :style="{
                    background: workflowStyle[task.workflow]?.bg ?? '#F2F0EB',
                    color: workflowStyle[task.workflow]?.text ?? '#3A3835'
                  }"
                  >{{ task.workflow }}</span
                >
              </td>

              <!-- 우선순위 -->
              <td class="col-priority">
                <span
                  class="priority-badge"
                  :style="{
                    background: priorityStyle[task.priority]?.bg ?? '#E5E2D9',
                    color: priorityStyle[task.priority]?.text ?? '#3A3835'
                  }"
                  >{{ task.priority }}</span
                >
              </td>

              <!-- 제목 -->
              <td class="col-title">
                <a href="#" class="task-title-link">{{ task.title }}</a>
                <span class="task-category">{{ task.category }}</span>
              </td>

              <!-- 진척도 -->
              <td class="col-progress">
                <div class="progress-wrap">
                  <div class="progress-bar-bg">
                    <div
                      class="progress-bar-fill"
                      :style="{
                        width: task.progress + '%',
                        background: progressColor(task.progress)
                      }"
                    ></div>
                  </div>
                  <span class="progress-pct">{{ task.progress }}%</span>
                </div>
              </td>

              <!-- 담당자 -->
              <td class="col-assignee">
                <div class="assignee-wrap">
                  <span class="avatar">{{ task.userId.charAt(0) }}</span>
                  {{ task.userId }}
                </div>
              </td>

              <!-- 마감일 -->
              <td class="col-due">
                <span class="due-date">{{ task.plannedEnd }}</span>
              </td>
            </tr>

            <!-- 데이터 없을 때 -->
            <tr v-if="pagedTasks.length === 0">
              <td colspan="8" class="empty-row">일감이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ── 페이징 ──────────────────────────────────────────── -->
      <div class="pagination">
        <button class="page-btn nav-btn" :disabled="!hasPrevBlock" @click="prevBlock">‹</button>
        <button v-for="p in pageNumbers" :key="p" class="page-btn" :class="{ active: p === currentPage }" @click="goPage(p)">{{ p }}</button>
        <button class="page-btn nav-btn" :disabled="!hasNextBlock" @click="nextBlock">›</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ── 기본 변수 ──────────────────────────────────────────────── */
:root {
  --amber-50: #fffbf0;
  --amber-100: #fff0c2;
  --amber-200: #ffda7a;
  --amber-400: #f5a623;
  --amber-500: #e8920e;
  --amber-600: #c97700;
  --amber-700: #9a5800;
  --amber-900: #3d2000;
  --stone-50: #fafaf8;
  --stone-100: #f2f0eb;
  --stone-200: #e5e2d9;
  --stone-300: #c8c4b8;
  --stone-400: #9a9690;
  --stone-500: #6e6b65;
  --stone-700: #3a3835;
  --stone-900: #1a1816;
  --white: #ffffff;
}

/* ── 페이지 레이아웃 ─────────────────────────────────────────── */
.task-page {
  min-height: 100vh;
  background: #fafaf8;
  padding: 32px 40px 60px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  color: #3a3835;
}

/* ── 헤더 ───────────────────────────────────────────────────── */
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 28px;
}
.header-eyebrow {
  display: block;
  font-size: 12px;
  letter-spacing: 0.08em;
  color: #9a9690;
  text-transform: uppercase;
  margin-bottom: 4px;
}
.header-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a1816;
  letter-spacing: -0.02em;
  margin: 0;
}

/* ── 일감 추가 버튼 ──────────────────────────────────────────── */
.btn-add {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #c97700;
  color: #fffbf0;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  letter-spacing: 0.01em;
  transition:
    background 0.15s,
    transform 0.1s;
  box-shadow: 0 2px 8px rgba(201, 119, 0, 0.25);
}
.btn-add:hover {
  background: #9a5800;
  transform: translateY(-1px);
}
.btn-add-icon {
  font-size: 18px;
  line-height: 1;
}

/* ── 필터 카드 ───────────────────────────────────────────────── */
.filter-card {
  background: #ffffff;
  border: 1px solid #e5e2d9;
  border-radius: 12px;
  padding: 24px 28px 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(26, 24, 22, 0.05);
}
.filter-card-header {
  margin-bottom: 18px;
}
.filter-card-title {
  font-size: 14px;
  font-weight: 700;
  color: #9a5800;
  letter-spacing: 0.03em;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px 18px;
}
.filter-field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.date-range-field {
  grid-column: span 2;
}
.title-field {
  grid-column: span 2;
}

.filter-label {
  font-size: 11.5px;
  font-weight: 600;
  color: #6e6b65;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}
.filter-select,
.filter-input {
  height: 38px;
  padding: 0 12px;
  background: #fafaf8;
  border: 1.5px solid #e5e2d9;
  border-radius: 7px;
  font-size: 13.5px;
  color: #3a3835;
  outline: none;
  transition: border-color 0.15s;
  width: 100%;
  box-sizing: border-box;
}
.filter-select:focus,
.filter-input:focus {
  border-color: #f5a623;
  background: #fffbf0;
}
.filter-select {
  cursor: pointer;
}

/* 날짜 범위 */
.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}
.date-input {
  flex: 1;
}
.date-sep {
  color: #9a9690;
  font-size: 13px;
  flex-shrink: 0;
}

/* 검색 가능 드롭다운 */
.searchable-dropdown {
  position: relative;
}
.clear-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #9a9690;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
  line-height: 1;
}
.dropdown-list {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: #ffffff;
  border: 1.5px solid #e5e2d9;
  border-radius: 7px;
  z-index: 50;
  box-shadow: 0 4px 16px rgba(26, 24, 22, 0.1);
  overflow: hidden;
}
.dropdown-item {
  padding: 9px 14px;
  font-size: 13.5px;
  color: #3a3835;
  cursor: pointer;
  transition: background 0.1s;
}
.dropdown-item:hover {
  background: #fff0c2;
  color: #9a5800;
}
.dropdown-empty {
  padding: 10px 14px;
  font-size: 13px;
  color: #9a9690;
}

/* 하단 행 */
.filter-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid #f2f0eb;
}

/* 체크박스 */
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13.5px;
  color: #6e6b65;
  cursor: pointer;
  user-select: none;
}
.filter-checkbox {
  display: none;
}
.checkbox-custom {
  width: 16px;
  height: 16px;
  border: 1.5px solid #c8c4b8;
  border-radius: 4px;
  background: #fafaf8;
  display: inline-block;
  position: relative;
  flex-shrink: 0;
  transition:
    background 0.15s,
    border-color 0.15s;
}
.filter-checkbox:checked + .checkbox-custom {
  background: #c97700;
  border-color: #c97700;
}
.filter-checkbox:checked + .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 4px;
  top: 1px;
  width: 5px;
  height: 9px;
  border: 2px solid #fffbf0;
  border-top: none;
  border-left: none;
  transform: rotate(45deg);
}

/* 필터 버튼들 */
.filter-actions {
  display: flex;
  gap: 10px;
}
.btn-reset {
  padding: 8px 18px;
  background: #f2f0eb;
  color: #6e6b65;
  border: 1.5px solid #e5e2d9;
  border-radius: 7px;
  font-size: 13.5px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-reset:hover {
  background: #e5e2d9;
}
.btn-search {
  padding: 8px 22px;
  background: #f5a623;
  color: #fffbf0;
  border: none;
  border-radius: 7px;
  font-size: 13.5px;
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 0.02em;
  transition: background 0.15s;
  box-shadow: 0 2px 6px rgba(245, 166, 35, 0.3);
}
.btn-search:hover {
  background: #c97700;
}

/* ── 테이블 카드 ─────────────────────────────────────────────── */
.table-card {
  background: #ffffff;
  border: 1px solid #e5e2d9;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(26, 24, 22, 0.05);
  overflow: hidden;
}
.table-meta {
  padding: 16px 24px 12px;
  border-bottom: 1px solid #f2f0eb;
}
.table-count {
  font-size: 13px;
  color: #9a9690;
}
.table-count strong {
  color: #3a3835;
  font-weight: 700;
}

.table-wrapper {
  overflow-x: auto;
}

/* ── 표 ────────────────────────────────────────────────────── */
.task-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13.5px;
}

.task-table thead tr {
  background: #f2f0eb;
}
.task-table th {
  padding: 11px 16px;
  text-align: left;
  font-size: 11.5px;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: #6e6b65;
  white-space: nowrap;
  border-bottom: 1px solid #e5e2d9;
}

.task-row td {
  padding: 13px 16px;
  border-bottom: 1px solid #f2f0eb;
  vertical-align: middle;
  color: #3a3835;
}
.task-row:last-child td {
  border-bottom: none;
}
.task-row:hover td {
  background: #fffbf0;
}
.row-even td {
  background: #fafaf8;
}
.row-even:hover td {
  background: #fffbf0;
}

/* 열 너비 */
.col-id {
  width: 96px;
}
.col-type {
  width: 100px;
}
.col-status {
  width: 110px;
}
.col-priority {
  width: 88px;
}
.col-title {
  min-width: 220px;
}
.col-progress {
  width: 160px;
}
.col-assignee {
  width: 108px;
}
.col-due {
  width: 110px;
}

/* 셀 요소들 */
.task-id {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #9a5800;
  font-weight: 600;
  background: #fff0c2;
  padding: 2px 7px;
  border-radius: 4px;
  letter-spacing: 0.02em;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12.5px;
  color: #6e6b65;
}

.status-badge,
.priority-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.task-title-link {
  display: block;
  color: #1a1816;
  text-decoration: none;
  font-weight: 500;
  line-height: 1.4;
  transition: color 0.12s;
}
.task-title-link:hover {
  color: #c97700;
}
.task-category {
  display: inline-block;
  margin-top: 3px;
  font-size: 11px;
  color: #9a9690;
  background: #f2f0eb;
  padding: 1px 7px;
  border-radius: 4px;
}

/* 진척도 */
.progress-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-bar-bg {
  flex: 1;
  height: 7px;
  background: #e5e2d9;
  border-radius: 99px;
  overflow: hidden;
}
.progress-bar-fill {
  height: 100%;
  border-radius: 99px;
  transition: width 0.4s ease;
}
.progress-pct {
  font-size: 12px;
  font-weight: 600;
  color: #6e6b65;
  min-width: 36px;
  text-align: right;
}

/* 담당자 */
.assignee-wrap {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
}
.avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #ffda7a;
  color: #9a5800;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* 마감일 */
.due-date {
  font-size: 13px;
  color: #6e6b65;
}

/* 빈 행 */
.empty-row {
  text-align: center;
  padding: 48px;
  color: #9a9690;
  font-size: 14px;
}

/* ── 페이징 ─────────────────────────────────────────────────── */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  padding: 20px 24px;
  border-top: 1px solid #f2f0eb;
}
.page-btn {
  min-width: 34px;
  height: 34px;
  padding: 0 6px;
  border: 1.5px solid #e5e2d9;
  border-radius: 7px;
  background: #ffffff;
  color: #6e6b65;
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.12s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.page-btn:hover:not(:disabled) {
  background: #fff0c2;
  border-color: #f5a623;
  color: #9a5800;
}
.page-btn.active {
  background: #c97700;
  border-color: #c97700;
  color: #fffbf0;
  font-weight: 700;
}
.page-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}
.nav-btn {
  font-size: 16px;
}
</style>
