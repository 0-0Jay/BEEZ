<script setup>
import { computed, ref } from 'vue';

// 헤더 데이터
const projectTitle = ref('임시 프로젝트 제목'); //프로젝트 제목 고정 또는 받아오기
const wiki_info = ref(''); //위키페이지 한줄 설명
const userId = ref(''); //일단 유저명 작성으로 넣어둠
const created_on = ref(null); //날짜도 오늘 날짜로 설정되도록
const saveSuccess = ref(false); //저장 성공 여부

const errors = ref({
  userId: false,
  created_on: false
});

function validateForm() {
  //작성자명이랑 날짜를 작성해야함
  errors.value.userId = !userId.value || !userId.value.trim();
  errors.value.created_on = !created_on.value; // 날짜가 선택되지 않았으면 true

  return !errors.value.userId && !errors.value.created_on;
}

function handleCancel() {
  // TODO: 취소 처리 (라우터 뒤로가기 등)
  console.log('취소');
}

function handleEdit() {
  if (!validateForm()) return;
  showEditModal.value = true;
}

//목차
const tocItems = ref([{ title: '' }]);
const emptyTocSlots = computed(() => Math.max(0, 7 - tocItems.value.length));

// 프로젝트 정보
const projectInfo = ref({
  status: '진행중',
  endDate: '2026.04.27',
  pm: '홍길동'
});

// 링크
const linkItems = ref([
  { title: '', url: '' },
  { title: '', url: '' }
]);

function addLinkItem() {
  linkItems.value.push({ title: '', url: '' });
}

function cancelLink() {
  linkItems.value = [{ title: '', url: '' }];
}

function registerLink() {
  const validLinks = linkItems.value.filter((l) => l.title || l.url);
  console.log('등록할 링크:', validLinks);
  // TODO: API 연동
}

// 편집 사유 모달
const showEditModal = ref(false);
const editReason = ref('');

function closeEditModal() {
  showEditModal.value = false;
  editReason.value = '';
}

function confirmEdit() {
  if (!editReason.value.trim()) return;
  console.log('편집 사유:', editReason.value);
  closeEditModal.value = false;
  showEditModal.value = false;
  editReason.value = '';
  saveSuccess.value = true;
  setTimeout(() => {
    saveSuccess.value = false;
  }, 3000);
  // TODO: 저장 API 연동
}

// 에디터
const editorRef = ref(null);
const editorContent = ref('');
const textStyle = ref('본문');

function onEditorInput(e) {
  editorContent.value = e.target.innerHTML;
}

function onEditorKeydown(e) {
  // TODO: 단축키 처리
}

function applyFormat(command) {
  // TODO: execCommand or 커스텀 에디터 명령 처리
  console.log('format:', command);
}
</script>

<template>
  <div class="wiki-editor-page">
    <!-- ① 상단 헤더 영역 -->
    <div class="header-section">
      <div class="header-left">
        <h1 class="project-title">{{ projectTitle || '뼈대 페이지' }}</h1>
        <input v-model="projectDescription" type="text" class="project-desc-input" placeholder="프로젝트 한 줄 설명을 입력하세요" />
      </div>

      <div class="header-fields">
        <!-- 작성자명 -->
        <div class="field-group">
          <label class="field-label required">작성자명</label>
          <input v-model="authorName" type="text" class="field-input" :class="{ 'is-error': errors.authorName }" placeholder="성함을 입력해 주세요." />
          <span v-if="errors.authorName" class="error-msg">성함입력은 필수 입니다.</span>
        </div>

        <!-- 작성일 -->
        <div class="field-group">
          <label class="field-label required">작성일</label>
          <input v-model="writtenDate" type="date" class="field-input" :class="{ 'is-error': errors.writtenDate }" placeholder="날짜를 선택해주세요." />
          <span v-if="errors.writtenDate" class="error-msg">날짜를 선택 해주세요.</span>
        </div>
      </div>

      <div class="header-actions">
        <!-- 저장 성공 토스트 -->
        <transition name="fade">
          <div v-if="saveSuccess" class="toast-success">편집 성공<br />위키 등록을 성공하였습니다.</div>
        </transition>
        <button class="btn btn-cancel" @click="handleCancel">취소</button>
        <button class="btn btn-edit" @click="handleEdit">등록</button>
      </div>
    </div>

    <!-- 본문 3단 그리드 -->
    <div class="content-grid">
      <!-- ② 목차 자동생성 -->
      <div class="panel toc-panel">
        <h3 class="panel-title">목차 - 자동생성</h3>
        <ul class="toc-list">
          <li v-for="(item, index) in tocItems" :key="index" class="toc-item">
            <a :href="`#section-${index}`">{{ index + 1 }}. {{ item.title }}</a>
          </li>
          <!-- 빈 항목 placeholder -->
          <li v-for="n in emptyTocSlots" :key="`empty-${n}`" class="toc-item empty">·</li>
        </ul>
      </div>

      <!-- ③ 프로젝트 정보 카드 -->
      <div class="panel info-panel">
        <h3 class="panel-title center">YEDAM PMS TOOL 프로젝트</h3>
        <div class="info-rows">
          <div class="info-row">
            <span class="info-label">상태 :</span>
            <span class="info-value status-badge">{{ projectInfo.status }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">종료일 :</span>
            <span class="info-value">{{ projectInfo.endDate }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">PM :</span>
            <span class="info-value">{{ projectInfo.pm }}</span>
          </div>
        </div>
      </div>

      <!-- ④ 링크 연결 패널 -->
      <div class="panel link-panel">
        <div class="link-form">
          <div v-for="(link, index) in linkItems" :key="index" class="link-item-group">
            <input v-model="link.title" type="text" class="field-input" placeholder="연결할 문서 제목을 작성해주세요." />
            <input v-model="link.url" type="text" class="field-input" placeholder="연결할 링크를 입력해주세요." />
          </div>

          <button class="btn btn-add-link" @click="addLinkItem">링크 추가</button>

          <div class="link-form-actions">
            <button class="btn btn-cancel" @click="cancelLink">취소</button>
            <button class="btn btn-primary" @click="registerLink">등록</button>
          </div>
        </div>

        <!-- 편집 사유 모달 -->
        <transition name="fade">
          <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
            <div class="modal-box">
              <div class="modal-header">
                <span>편집 사유(=버전설명)</span>
                <button class="modal-close" @click="closeEditModal">×</button>
              </div>
              <div class="modal-body">
                <textarea v-model="editReason" class="modal-textarea" placeholder="편집 사유를 입력해주세요." />
              </div>
              <div class="modal-footer">
                <button class="btn btn-cancel" @click="closeEditModal">취소</button>
                <button class="btn btn-primary" @click="confirmEdit">확인</button>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- ⑤ 본문 에디터 -->
    <div class="editor-section">
      <!-- 툴바 -->
      <div class="editor-toolbar">
        이 툴바는 뼈대만 있습니다
        <select v-model="textStyle" class="toolbar-select">
          <option value="본문">본문</option>
          <option value="제목1">제목1</option>
          <option value="제목2">제목2</option>
          <option value="제목3">제목3</option>
        </select>

        <div class="toolbar-divider" />

        <button class="toolbar-btn bold" title="굵게" @click="applyFormat('bold')"><b>B</b></button>
        <button class="toolbar-btn italic" title="기울임" @click="applyFormat('italic')"><i>I</i></button>
        <button class="toolbar-btn underline" title="밑줄" @click="applyFormat('underline')"><u>U</u></button>
        <button class="toolbar-btn strikethrough" title="취소선" @click="applyFormat('strikeThrough')"><s>S</s></button>
        <button class="toolbar-btn" title="글자색" @click="applyFormat('foreColor')">A</button>

        <div class="toolbar-divider" />

        <button class="toolbar-btn" title="링크" @click="applyFormat('link')">🔗</button>
        <button class="toolbar-btn" title="이미지" @click="applyFormat('image')">🖼</button>
        <button class="toolbar-btn" title="표" @click="applyFormat('table')">⊞</button>

        <div class="toolbar-divider" />

        <button class="toolbar-btn" title="글머리 기호" @click="applyFormat('insertUnorderedList')">:=</button>
        <button class="toolbar-btn" title="번호 목록" @click="applyFormat('insertOrderedList')">1=</button>
        <button class="toolbar-btn" title="체크리스트" @click="applyFormat('checklist')">☑</button>

        <div class="toolbar-divider" />
      </div>

      <!-- 에디터 본문 -->
      <div ref="editorRef" class="editor-body" contenteditable="true" @input="onEditorInput" @keydown="onEditorKeydown">
        <p v-if="!editorContent" class="editor-placeholder">텍스트를 입력하세요</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===================== 전체 레이아웃 ===================== */
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

/* ===================== ① 헤더 ===================== */
.header-section {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  background: #e4e4e4;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 16px 20px;
  position: relative;
}

.header-left {
  flex: 1;
}

.project-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
}

.project-desc-input {
  width: 100%;
  border: 1px solid #ffffff;
  background-color: #ffffff;
  border-radius: 4px;
  padding: 6px 10px;
  font-size: 13px;
  box-sizing: border-box;
}

.header-fields {
  display: flex;
  gap: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 160px;
}

.field-label {
  font-size: 12px;
  font-weight: 600;
}

.field-label.required::after {
  content: ' *';
  color: #e74c3c;
}

.field-input {
  border: 1px solid #3129292f;
  background-color: #ffffff;
  border-radius: 4px;
  padding: 6px 10px;
  font-size: 13px;
}

.field-input.is-error {
  border-color: #e74c3c;
}

.error-msg {
  font-size: 11px;
  color: #e74c3c;
}

.header-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  position: relative;
}

.toast-success {
  background: #333;
  color: #fff;
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 12px;
  line-height: 1.5;
  text-align: center;
  white-space: nowrap;
}

/* ===================== 버튼 공통 ===================== */
.btn {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
}

.btn-cancel {
  background: #fff;
  border: 1px solid #bbb;
  color: #555;
}

.btn-cancel:hover {
  background: #f0f0f0;
}

.btn-edit {
  /* 선형 그라데이션: 위에서 아래로 옅은 오렌지 -> 진한 오렌지 */
  background: linear-gradient(180deg, #ff8d4b 0%, #f5a623 100%);
  color: #fff;
}
.btn-edit:hover {
  background: #f5a623;
}

.btn-primary {
  background: linear-gradient(180deg, #ff8d4b 0%, #f5a623 100%);
  color: #fff;
}

.btn-primary:hover {
  background: #f5a623;
}

.btn-add-link {
  background: #fff;
  border: 1px solid #bbb;
  color: #555;
  margin-top: 6px;
}

/* ===================== 3단 그리드 ===================== */
.content-grid {
  display: grid;
  grid-template-columns: 180px 1fr 1fr;
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
  margin: 0 0 12px;
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
  min-width: 60px;
  font-size: 13px;
}

.info-value {
  background: #f0f0f0;
  border-radius: 4px;
  padding: 4px 12px;
  font-size: 13px;
  flex: 1;
}

.status-badge {
  color: #555;
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
  margin-bottom: 4px;
}

.link-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
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

/* ===================== ⑤ 에디터 ===================== */
.editor-section {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 2px;
  padding: 8px 12px;
  border-bottom: 1px solid #e0e0e0;
  background: #fafafa;
}

.toolbar-select {
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 3px 8px;
  font-size: 13px;
  background: #fff;
  cursor: pointer;
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background: #ddd;
  margin: 0 4px;
}

.toolbar-btn {
  background: none;
  border: none;
  padding: 4px 8px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 13px;
  color: #444;
  line-height: 1;
}

.toolbar-btn:hover {
  background: #e8e8e8;
}

.toolbar-btn.bold {
  font-weight: 700;
}
.toolbar-btn.italic {
  font-style: italic;
}
.toolbar-btn.callout {
  color: #f5a623;
}
.toolbar-btn.code {
  font-family: monospace;
}

.editor-body {
  min-height: 200px;
  padding: 20px;
  font-size: 15px;
  line-height: 1.7;
  outline: none;
  position: relative;
}

.editor-placeholder {
  color: #bbb;
  pointer-events: none;
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 15px;
}

/* ===================== 트랜지션 ===================== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
