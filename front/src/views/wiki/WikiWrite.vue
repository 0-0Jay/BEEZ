<script setup>
import { useWikiStore } from '@/stores/wiki';
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const wikiStore = useWikiStore(); //스토어 연결

const saveSuccess = ref(false); // 저장여부
const route = useRoute();
const userId = ref(''); //입력받을 작성자명
const wikiInfo = ref(''); //작성창 한줄 설명

//version id 별로,  wikiId별로 겹치지 않도록
const generateId = (prefix) => {
  const now = new Date();
  const timestamp =
    now.getFullYear() + String(now.getMonth() + 1).padStart(2, '0') + String(now.getDate()).padStart(2, '0') + String(now.getHours()).padStart(2, '0') + String(now.getMinutes()).padStart(2, '0') + String(now.getSeconds()).padStart(2, '0');
  return `${prefix}_${timestamp}`;
};
//스토어랑 연결
const projectInfo = computed(() => wikiStore.projectInfo);

//페이지 로드 될 때 실행
onMounted(async () => {
  const projectId = route.params.projectId;
  if (projectId) {
    await wikiStore.fetchProjectData(projectId);
  }
});

const errors = ref({
  //작성자명 작성해야 오류 안뜸
  userId: false
});

function validateForm() {
  //작성자명 작성해야함
  errors.value.userId = !userId.value || !userId.value.trim();
  return !errors.value.userId;
}

function handleEdit() {
  // 작성자명 안쓰면 안되고 쓰면 모달창 출력
  if (!validateForm()) return;
  showEditModal.value = true;
}
//--------------------------------------------------

//목차
const tocItems = ref([]);
const emptyTocSlots = computed(() => Math.max(0, 5 - tocItems.value.length)); // 목차 점 갯수

function onEditorInput(e) {
  // 본문에 입력할때 목차가 반영됨
  editorContent.value = e.target.innerHTML;
  updateTOC(e.target); // 목차 업데이트 함수 호출
}

function updateTOC(container) {
  // 목차 추출로직
  // 에디터 내의 h1, h2, h3 태그만 추출
  const headings = container.querySelectorAll('h2, h3'); //h1은 일부러 제외함
  const tempToc = [];

  headings.forEach((heading, index) => {
    // 목차 클릭 시 이동을 위해 ID가 없으면 생성
    if (!heading.id) heading.id = `section-${index}`;

    tempToc.push({
      //id부여
      id: heading.id,
      title: heading.innerText,
      level: heading.tagName // 'H1', 'H2' 등
    });
  });

  tocItems.value = tempToc; //목차 리스트 반영
}
//----------------------------------------------------
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

//--------------------------------------------------------------
async function confirmEdit() {
  if (!editReason.value.trim()) return;

  //1. 버전 번호 계산 로직
  let nextVersion = '1.0'; //기본값 최초 생성시

  //기존 버전 이름 있는지 확인
  if (wikiStore.wikiDetail && wikiStore.wikiDetail.versionName) {
    const currentName = wikiStore.wikiDetail.versionName;
    const versionMatch = currentName.match(/v(\d+\.\d+)/); // 숫자 추출

    if (versionMatch) {
      const currentNum = parseFloat(versionMatch[1]);
      nextVersion = (currentNum + 0.1).toFixed(1);
    }
  }

  const finalVersionName = `${projectInfo.value.title} v${nextVersion}`;
  const newVersionId = generateId('VER');

  //신규 위키는ID생성하고, 기존위키면 기존ID 사용
  const isNewWiki = !wikiStore.wikiDetail.id;
  const currentWikiId = isNewWiki ? null : wikiStore.wikiDetail.id;

  const commentRegex = new RegExp('<!--v-if-->', 'g'); // 에디터로 본문 작성하면 생기는 주석 삭제하고 DB에 저장하려함
  const cleanContent = editorContent.value ? editorContent.value.replace(commentRegex, '') : '';
  console.log('정제된 데이터 확인:', cleanContent);

  //유효한 링크만 필터링해서 JSON처리
  const validLinks = linkItems.value.filter((l) => l.title.trim() || l.url.trim());
  const linksJson = JSON.stringify(validLinks); // JSON문자열로 links를 변형하기 위함

  // 백엔드 wikiSaveRequdst 구조에 맞도록 조립 (2개 받아서 하나로 묶어야 됨)
  const saveData = {
    wikiRequest: {
      id: currentWikiId, //위키 ID (신규면 빈값 혹은 생성된 값)
      projectId: route.params.projectId
    },
    versionRequest: {
      versionId: newVersionId,
      content: cleanContent,
      userId: userId.value,
      wikiId: currentWikiId,
      description: editReason.value, //수정이유
      versionName: finalVersionName,
      links: linksJson, //JSON 문자열로 전송할 예정
      wikiInfo: wikiInfo.value //이건 back작업 마저 해야함
    }
  }; // saveData end

  const result = await wikiStore.saveWiki(saveData);

  console.log('저장 결과:', result);

  if (result) {
    showEditModal.value = false;
    saveSuccess.value = true;
    editReason.value = '';

    //입력값 초기화
    userId.value = '';

    setTimeout(() => {
      saveSuccess.value = false;
    }, 3000);
  }
  //------------------------------------------------------------
}

// 에디터
const editorRef = ref(null);
const editorContent = ref('');
const textStyle = ref('본문');

// 반드시 ref로 감싸고 모든 속성을 초기화해야 합니다.
const activeStyles = ref({
  bold: false,
  italic: false,
  underline: false,
  strikeThrough: false
});

// function onEditorInput(e) {
//   editorContent.value = e.target.innerHTML;
// }

function onEditorKeydown(e) {
  // TODO: 단축키 처리
}

//에디터 함수
function applyFormat(command, value = null) {
  editorRef.value.focus();
  const selection = window.getSelection();
  if (!selection.rangeCount) return;
  const range = selection.getRangeAt(0);

  if (command === 'formatBlock') {
    let container = range.commonAncestorContainer;
    if (container.nodeType === 3) container = container.parentNode;
    const blockElement = container.closest('h1, h2, h3, p') || container;

    // 공통 로직: 새로운 태그 생성 (p 또는 h1, h2, h3)
    const newElement = document.createElement(value);

    // 제목일 경우에만 ID 부여 (목차용)
    if (value !== 'p') {
      newElement.id = `section-${Date.now()}`;
    }

    // 기존 내용을 새 엘리먼트로 옮기기 (비어있으면 공백 삽입)
    newElement.innerHTML = blockElement.innerHTML && blockElement.innerHTML !== '<br>' ? blockElement.innerHTML : '<br>';

    // 핵심: 기존 요소를 새 요소로 교체 (복사가 아니라 '교체'입니다)
    if (blockElement !== editorRef.value && blockElement.parentNode) {
      blockElement.parentNode.replaceChild(newElement, blockElement);
    } else {
      // 부모가 에디터 자체인 경우 안전하게 삽입
      range.deleteContents();
      range.insertNode(newElement);
    }

    // 작성 편의를 위해 제목(h) 뒤에 본문(p) 줄이 없다면 추가
    if (value !== 'p' && !newElement.nextSibling) {
      const nextLine = document.createElement('p');
      nextLine.innerHTML = '<br>';
      newElement.after(nextLine);
    }

    // 커서 위치를 새 요소 내부로 이동
    range.selectNodeContents(newElement);
    range.collapse(false);
    selection.removeAllRanges();
    selection.addRange(range);
  } else {
    document.execCommand(command, false, value);
  }

  onEditorInput({ target: editorRef.value });
}
</script>

<template>
  <div class="wiki-editor-page">
    <!-- ① 상단 헤더 영역 -->
    <div class="header-section">
      <div class="header-left">
        <h1 class="project-title">{{ projectInfo.title || '프로젝트 제목 출력 부분' }}</h1>
        <input v-model="wikiStore.wikiDetail.wikiInfo" type="text" class="project-desc-input" placeholder="위키 관련 한 줄 설명을 입력하세요" />
      </div>

      <div class="header-fields">
        <!-- 작성자명 -->
        <div class="field-group">
          <label class="field-label required">작성자명</label>
          <input v-model="userId" type="text" class="field-input" :class="{ 'is-error': errors.userId }" placeholder="성함을 입력해 주세요." />
          <span v-if="errors.userId" class="error-msg">성함입력은 필수 입니다.</span>
        </div>

        <!-- 작성일 -->
      </div>

      <div class="header-actions">
        <!-- 저장 성공 토스트 -->
        <transition name="fade">
          <div v-if="saveSuccess" class="toast-success">편집 성공<br />위키 등록을 성공하였습니다.</div>
        </transition>
        <div class="link-form-actions">
          <button class="btn btn-cancel" @click="handleCancel">취소</button>
          <button class="btn btn-edit" @click="handleEdit">등록</button>
        </div>
      </div>
    </div>

    <!-- 본문 3단 그리드 -->
    <div class="content-grid">
      <!-- ② 목차 자동생성 -->
      <div class="panel info-panel">
        <h3 class="panel-title">목차 - 자동생성</h3>
        <ul class="toc-list">
          <li v-for="(item, index) in tocItems" :key="index" :class="['toc-item', item.level]">
            <a :href="`#${item.id}`">{{ index + 1 }}. {{ item.title }}</a>
          </li>
          <!-- 빈 항목 placeholder -->
          <li v-for="n in emptyTocSlots" :key="`empty-${n}`" class="toc-item empty">·</li>
        </ul>
      </div>

      <!-- ③ 프로젝트 정보 카드 -->
      <div class="panel info-panel">
        <h3 class="project-title">{{ projectInfo.title || '프로젝트 제목 출력 부분' }}</h3>
        <div class="info-rows">
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
          <div class="info-row">
            <span class="info-label">상태값 :</span>
            <span class="info-value status-badge">{{ projectInfo.status }}</span>
          </div>
        </div>
      </div>

      <!-- ④ 링크 연결 패널 -->
      <div class="panel link-panel">
        <div class="link-form">
          <div class="link-items-container">
            <div v-for="(link, index) in linkItems" :key="index" class="link-item-group">
              <input v-model="link.title" type="text" class="field-input" placeholder="연결할 문서 제목을 작성해주세요." />
              <input v-model="link.url" type="text" class="field-input" placeholder="연결할 링크를 입력해주세요." />
            </div>
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
                <span>버전설명</span>
                <button class="modal-close" @click="closeEditModal">×</button>
              </div>
              <div class="modal-body">
                <textarea v-model="editReason" class="modal-textarea" placeholder="등록 사유를 입력해주세요." />
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
        <select v-model="textStyle" class="toolbar-select" @change="applyFormat('formatBlock', textStyle)">
          <option value="p">본문</option>
          <option value="h2">제목1</option>
          <option value="h3">제목2</option>
        </select>

        <div class="toolbar-divider" />

        <button class="toolbar-btn bold" title="굵게" @click="applyFormat('bold')"><b>B</b></button>
        <button class="toolbar-btn italic" title="기울게" @click="applyFormat('italic')"><i>I</i></button>
        <button class="toolbar-btn underline" title="밑줄" @click="applyFormat('underline')"><u>U</u></button>
        <button class="toolbar-btn strikethrough" title="취소선" @click="applyFormat('strikeThrough')"><s>S</s></button>
        <button class="toolbar-btn" title="글자색" @click="applyFormat('foreColor')">A</button>

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
  background: #f2f0eb;
  border: 1px solid #c7c7c2;
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
  background: #ffffff;
  border: 1px solid #bbb;
  color: #555;
}

.btn-cancel:hover {
  background: #f0f0f0;
}

.btn-edit {
  /* 선형 그라데이션: 위에서 아래로 옅은 오렌지 -> 진한 오렌지 */
  background: #e8920e;
  color: #fff;
}
.btn-edit:hover {
  background: #f5a623;
}

.btn-primary {
  background: #e8920e;
  color: #f2f0eb;
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
  margin-bottom: 4px;
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
  color: #e8920e;
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
/* 활성화된 버튼 스타일 (확실한 눌림 표시) */
.toolbar-btn.is-active {
  /* 1. 배경색을 더 어둡고 푸른 계열로 바꿔서 강조 (선택사항) */
  background-color: #d1d9e6 !important;

  /* 2. 테두리를 파란색 계열로 주어 강조 */
  border: 1px solid #4a90e2 !important;

  /* 3. 글자색을 더 진하게 */
  color: #1a73e8 !important;

  /* 4. 안쪽 그림자를 더 깊게 주어 버튼이 눌린 입체감 표현 */
  box-shadow: inset 1px 1px 3px rgba(0, 0, 0, 0.2);

  /* 5. 약간의 여백 보정 (테두리가 생겨서 버튼이 흔들리는 것 방지) */
  padding: 3px 7px;
}

/* 마우스를 올렸을 때 더 밝은 회색으로 */
.toolbar-btn:hover:not(.is-active) {
  background-color: #ebebeb;
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
