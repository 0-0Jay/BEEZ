<script setup>
import { useAuthStore } from '@/stores/auth';
import { useWikiStore } from '@/stores/wiki';
import { computed, nextTick, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const wikiStore = useWikiStore(); //스토어 연결
const authStore = useAuthStore();

const saveSuccess = ref(false); // 저장여부
const route = useRoute();
const router = useRouter();
const userId = ref(''); //입력받을 작성자명
const wikiInfo = ref(''); //작성창 한줄 설명

const wikiId = computed(() => route.params.wikiId ?? null);
const isEditMode = computed(() => !!wikiId.value);

const isSameContent = computed(() => {
  if (!isEditMode.value) return false; // 신규 작성은 비교 불필요
  const originalContent = (wikiStore.wikiDetail.content ?? '').replace(/<!--v-if-->/g, '').trim();
  const currentContent = (editorContent.value ?? '').replace(/<!--v-if-->/g, '').trim();
  return originalContent === currentContent;
});

const showSameContentModal = ref(false);

//스토어랑 연결
const projectInfo = computed(() => wikiStore.projectInfo);

//페이지 로드 될 때 실행
onMounted(async () => {
  const projectId = route.params.projectId;
  if (projectId) {
    await wikiStore.fetchProjectData(projectId);

    if (isEditMode.value) {
      //기존 데이터 불러오기
      await wikiStore.fetchWikiDetail(wikiId.value);

      //에디터에 기존 내용 세팅
      editorContent.value = wikiStore.wikiDetail.content ?? '';
      await nextTick();
      if (editorRef.value) {
        editorRef.value.innerHTML = editorContent.value;
        updateTOC(editorRef.value);
      } //링크 데이터 복원
      const links = wikiStore.wikiDetail.links;
      if (links) {
        linkItems.value = typeof links === 'string' ? JSON.parse(links) : links;
      }
    } else {
      // 신규 작성 시 스토어 초기화
      wikiStore.wikiDetail = {};
    }
  }
});

const errors = ref({
  wikiInfo: false,
  content: false,
  contentSpecialOnly: false,
  editReason: false
});

const linkErrors = ref([]);

// 특수문자만 있는지 체크 함수
function isSpecialCharOnly(str) {
  return /^[^a-zA-Z0-9가-힣\s]+$/.test(str.trim());
}

// URL 유효성 체크 함수
function isValidUrl(str) {
  try {
    new URL(str);
    return true;
  } catch {
    return false;
  }
}

// 기존 validateForm 교체
function validateForm() {
  let valid = true;

  // 1. 한줄 설명 필수
  errors.value.wikiInfo = !wikiStore.wikiDetail.wikiInfo?.trim();
  if (errors.value.wikiInfo) valid = false;

  // 2. 본문 필수
  const plainText = editorRef.value?.innerText?.trim() ?? '';
  errors.value.content = !plainText;
  if (errors.value.content) valid = false;

  // 3. 본문 특수문자만 입력 체크
  if (!errors.value.content && isSpecialCharOnly(plainText)) {
    errors.value.contentSpecialOnly = true;
    valid = false;
  } else {
    errors.value.contentSpecialOnly = false;
  }
  // 4. 링크 유효성
  linkErrors.value = linkItems.value.map((link) => {
    const hasTitle = link.title.trim();
    const hasUrl = link.url.trim();

    if (!hasTitle && !hasUrl) return { title: false, url: false, invalidUrl: false }; // 둘 다 비어있으면 패스

    return {
      title: !hasTitle && !!hasUrl, // 주소만 있고 이름 없음
      url: !!hasTitle && !hasUrl, // 이름만 있고 주소 없음
      invalidUrl: !!hasUrl && !isValidUrl(link.url) // URL 형식 오류
    };
  });

  const hasLinkError = linkErrors.value.some((e) => e.title || e.url || e.invalidUrl);
  if (hasLinkError) valid = false;

  return valid;
} // validateForm end

function handleEdit() {
  // 작성자명 안쓰면 안되고 쓰면 모달창 출력
  if (!validateForm()) return;

  // 수정 모드이고 본문 내용이 동일하면 경고 모달 출력
  if (isEditMode.value && isSameContent.value) {
    showSameContentModal.value = true;
    return;
  }

  showEditModal.value = true;
}
//본문 - 텍스트 환경 복붙도 목차 생성 하도록
function onEditorPaste(e) {
  e.preventDefault();
  const text = e.clipboardData.getData('text/plain');
  const lines = text.split('\n');

  const fragment = document.createDocumentFragment();

  lines.forEach((line) => {
    const trimmed = line.trim();
    if (!trimmed) return;

    let el;

    // 1. / 2. / 3. 형태 → H2
    if (/^\d+\.\s/.test(trimmed) && !/^\d+\.\d+/.test(trimmed)) {
      el = document.createElement('h2');
      el.innerText = trimmed;

      // 1.1 / 1.2 / 2.1 형태 → H3
    } else if (/^\d+\.\d+\s/.test(trimmed)) {
      el = document.createElement('h3');
      el.innerText = trimmed;

      // 일반 텍스트 → p
    } else {
      el = document.createElement('p');
      el.innerText = trimmed;
    }

    fragment.appendChild(el);
  });

  editorRef.value.appendChild(fragment);
  onEditorInput({ target: editorRef.value });
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
  const headings = container.querySelectorAll('h2, h3');
  const tempToc = [];
  let h2Count = 0;
  let h3Count = 0;

  headings.forEach((heading, index) => {
    const id = `section-${index}`;
    heading.setAttribute('id', id);

    const rawTitle = heading.innerText;
    const cleanTitle = rawTitle.replace(/^[\d.]+\s*/, '').trim();

    if (heading.tagName === 'H2') {
      h2Count++;
      h3Count = 0;
      tempToc.push({ id, title: cleanTitle, number: `${h2Count}.`, sub: [] });
    } else if (heading.tagName === 'H3') {
      h3Count++;
      const number = `${h2Count}.${h3Count}`;
      if (tempToc.length > 0) {
        tempToc[tempToc.length - 1].sub.push({ id, title: cleanTitle, number });
      } else {
        tempToc.push({ id, title: cleanTitle, number, sub: [] });
      }
    }
  });

  tocItems.value = tempToc;
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
  if (!editReason.value.trim()) {
    errors.value.editReason = true;
    return;
  }
  errors.value.editReason = false;

  //신규 위키는ID생성하고, 기존위키면 기존ID 사용
  const currentWikiId = isEditMode.value ? wikiId.value : (wikiStore.wikiDetail.id ?? null);
  console.log('currentWikiId:', currentWikiId); // 확인용

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
      content: cleanContent,
      userId: authStore.user.id,
      wikiId: currentWikiId,
      description: editReason.value, //수정이유
      links: linksJson, //JSON 문자열로 전송할 예정
      wikiInfo: wikiStore.wikiDetail.wikiInfo
    }
  }; // saveData end

  const result = await wikiStore.saveWiki(saveData);

  console.log('저장 결과:', result);

  if (result) {
    showEditModal.value = false;
    saveSuccess.value = true;
    editReason.value = '';
    userId.value = '';

    setTimeout(() => {
      saveSuccess.value = false;
      router.push({
        name: 'WikiDetail',
        params: {
          projectId: route.params.projectId,
          wikiId: result // store에서 반환된 wikiId
        }
      });
    }, 1500);
  }
}

//------------------------------------------------------------
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

//에디터 함수 - API 쓸거면 필요없음
function applyFormat(command, value = null) {
  editorRef.value.focus();
  const selection = window.getSelection();
  if (!selection.rangeCount) return;
  const range = selection.getRangeAt(0);

  if (command === 'formatBlock') {
    let container = range.commonAncestorContainer;
    if (container.nodeType === 3) container = container.parentNode;
    const blockElement = container.closest('h1, h2, h3, p') || container;

    const newElement = document.createElement(value);

    if (value !== 'p') {
      newElement.id = `section-${Date.now()}`;

      // ✅ 현재 에디터에서 번호 계산
      const allHeadings = Array.from(editorRef.value.querySelectorAll('h2, h3'));
      let h2Count = 0;
      let h3Count = 0;
      let prefix = '';

      // 기존 제목들 순회해서 현재 위치의 번호 계산
      for (const h of allHeadings) {
        if (h === blockElement) break; // 교체 대상 직전까지만
        if (h.tagName === 'H2') {
          h2Count++;
          h3Count = 0;
        } else if (h.tagName === 'H3') {
          h3Count++;
        }
      }

      if (value === 'h2') {
        h2Count++;
        h3Count = 0;
        prefix = `${h2Count}. `;
      } else if (value === 'h3') {
        h3Count++;
        prefix = `${h2Count}.${h3Count} `;
      }

      // 기존 텍스트에서 번호 제거 후 새 번호 붙이기
      const rawText = blockElement.innerText?.replace(/^[\d.]+\s*/, '').trim() || '';
      newElement.innerText = prefix + rawText;
    } else {
      newElement.innerHTML = blockElement.innerHTML && blockElement.innerHTML !== '<br>' ? blockElement.innerHTML : '<br>';
    }

    if (blockElement !== editorRef.value && blockElement.parentNode) {
      blockElement.parentNode.replaceChild(newElement, blockElement);
    } else {
      range.deleteContents();
      range.insertNode(newElement);
    }

    if (value !== 'p' && !newElement.nextSibling) {
      const nextLine = document.createElement('p');
      nextLine.innerHTML = '<br>';
      newElement.after(nextLine);
    }

    range.selectNodeContents(newElement);
    range.collapse(false);
    selection.removeAllRanges();
    selection.addRange(range);
  } else {
    document.execCommand(command, false, value);
  }

  onEditorInput({ target: editorRef.value });
}

// WikiWrite.vue script에 추가
function handleCancel() {
  const projectId = route.params.projectId;
  if (isEditMode.value) {
    // 편집 모드면 → 상세 페이지로 돌아가기
    router.push({ name: 'WikiDetail', params: { projectId, wikiId: wikiId.value } });
  } else {
    // 신규 작성 모드면 → 이전 페이지로
    router.back();
  }
}
</script>

<template>
  <div class="wiki-editor-page">
    <!-- ① 상단 헤더 영역 -->
    <div class="flex justify-between items-end">
      <div class="header-left">
        <h1 class="text-2xl font-bold text-[#1A1816]">
          {{ isEditMode ? 'WIKI - 수정' : 'WIKI - 작성' }}
        </h1>
        <div>
          <label class="field-label required">위키 한 줄 설명</label>
          <input v-model="wikiStore.wikiDetail.wikiInfo" type="text" class="project-desc-input" :class="{ 'is-error': errors.wikiInfo }" placeholder="위키 관련 한 줄 설명을 입력하세요" />
          <p v-if="errors.wikiInfo" class="error-msg">한 줄 설명을 입력해주세요.</p>
        </div>
      </div>

      <div class="header-actions">
        <!-- 저장 성공 토스트 -->
        <transition name="fade">
          <div v-if="saveSuccess" class="toast-fixed">{{ isEditMode ? '수정되었습니다.' : '작성되었습니다.' }}<br />잠시 후 조회 페이지로 이동합니다.</div>
        </transition>
        <div class="link-form-actions">
          <button class="btn btn-cancel" @click="handleCancel">취소</button>
          <button class="btn btn-edit" @click="handleEdit">
            {{ isEditMode ? '수정' : '등록' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 본문 3단 그리드 -->
    <div class="content-grid">
      <!-- ② 목차 자동생성 -->
      <div class="panel info-panel">
        <h3 class="panel-title">목차 - 자동생성</h3>
        <ul class="toc-list">
          <template v-for="item in tocItems" :key="item.id">
            <li class="toc-item">
              <a :href="`#${item.id}`">{{ item.number }} {{ item.title }}</a>
            </li>
            <ul v-if="item.sub && item.sub.length > 0" class="toc-list toc-sub">
              <li v-for="sub in item.sub" :key="sub.id" class="toc-item toc-sub">
                <a :href="`#${sub.id}`">{{ sub.number }} {{ sub.title }}</a>
              </li>
            </ul>
          </template>
          <!-- 빈 항목 placeholder -->
          <li v-for="n in emptyTocSlots" :key="`empty-${n}`" class="toc-item empty">·</li>
        </ul>
      </div>

      <!-- ③ 프로젝트 정보 카드 -->
      <div class="panel info-panel">
        <h3 class="project-title">{{ projectInfo.title || '프로젝트 제목 출력 부분' }} - 기본정보</h3>
        <br />
        <div class="info-rows">
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
            <span class="info-value status-badge">{{ projectInfo.startDate ? projectInfo.startDate.replace('T', ' ').substring(0, 10) : '데이터가 없습니다' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">종료일 :</span>
            <span class="info-value status-badge">{{ projectInfo.endDate ? projectInfo.endDate.replace('T', ' ').substring(0, 10) : '데이터가 없습니다' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">상태값 :</span>
            <span class="info-value status-badge">{{ projectInfo.status || '데이터가 없습니다' }}</span>
          </div>
        </div>
      </div>

      <!-- ④ 링크 연결 패널 -->
      <div class="panel link-panel">
        <div class="link-form">
          <div class="link-items-container">
            <div class="toc-heading" style="margin-bottom: 12px">관련 URL 링크</div>
            <div v-for="(link, index) in linkItems" :key="index" class="link-item-group">
              <input v-model="link.title" type="text" class="field-input" :class="{ 'is-error': linkErrors[index]?.title }" placeholder="연결할 링크 이름을 작성해주세요." />
              <p v-if="linkErrors[index]?.title" class="error-msg">링크 이름을 입력해주세요.</p>

              <input v-model="link.url" type="text" class="field-input" :class="{ 'is-error': linkErrors[index]?.url || linkErrors[index]?.invalidUrl }" placeholder="링크 주소" />
              <p v-if="linkErrors[index]?.url" class="error-msg">링크 주소를 입력해주세요.</p>
              <p v-if="linkErrors[index]?.invalidUrl" class="error-msg">URL 주소에 맞게 작성해주세요.</p>
            </div>
          </div>

          <button class="btn btn-add-link" @click="addLinkItem">링크 추가</button>

          <div class="link-form-actions">
            <!--없어도 되는 코드들임-->
            <!-- <button class="btn btn-cancel" @click="cancelLink">취소</button> -->
            <!-- <button class="btn btn-primary" @click="registerLink">등록</button> -->
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
                <textarea v-model="editReason" class="modal-textarea" :class="{ 'is-error': errors.editReason }" placeholder="등록 사유를 입력해주세요." />
                <p v-if="errors.editReason" class="error-msg">버전 설명을 작성해주세요.</p>
              </div>
              <div class="modal-footer">
                <button class="btn btn-cancel" @click="closeEditModal">취소</button>
                <button class="btn btn-primary" @click="confirmEdit">확인</button>
              </div>
            </div>
          </div>
        </transition>

        <!-- 내용 동일 경고 모달 -->
        <transition name="fade">
          <div v-if="showSameContentModal" class="modal-overlay" @click.self="showSameContentModal = false">
            <div class="modal-box">
              <div class="modal-header">
                <span>내용 변경 없음</span>
                <button class="modal-close" @click="showSameContentModal = false">×</button>
              </div>
              <div class="modal-body" style="text-align: center; padding: 24px 16px">
                <p style="font-size: 14px; color: #333; line-height: 1.6">본문의 내용이 동일합니다.<br />버전업을 하시겠습니까?</p>
              </div>
              <div class="modal-footer">
                <button class="btn btn-cancel" @click="showSameContentModal = false">취소</button>
                <button
                  class="btn btn-primary"
                  @click="
                    () => {
                      showSameContentModal = false;
                      showEditModal = true;
                    }
                  "
                >
                  버전업 진행
                </button>
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
          <option value="h2">제목 (1. 2. 3. 형태)</option>
          <option value="h3">부제목(1.1 / 1.2 / 1.3형태)</option>
          <option value="p">본문</option>
        </select>

        <div class="toolbar-divider" />

        <button class="toolbar-btn bold" title="굵게" @click="applyFormat('bold')"><b>B</b></button>
        <button class="toolbar-btn italic" title="기울게" @click="applyFormat('italic')"><i>I</i></button>
        <button class="toolbar-btn underline" title="밑줄" @click="applyFormat('underline')"><u>U</u></button>
        <button class="toolbar-btn strikethrough" title="취소선" @click="applyFormat('strikeThrough')"><s>S</s></button>
        <div class="toolbar-divider" />
      </div>

      <!-- 에디터 본문 -->
      <div style="position: relative">
        <p v-if="!editorContent" class="editor-placeholder">툴바에서 '제목 / 부제목' 을 선택하여 입력할 수 있습니다. (번호는 자동 생성됩니다)</p>
        <div ref="editorRef" class="editor-body" contenteditable="true" @input="onEditorInput" @keydown="onEditorKeydown" @paste="onEditorPaste"></div>
        <p v-if="errors.content" class="error-msg" style="padding: 0 20px 10px">본문 내용을 입력해주세요.</p>
        <p v-if="errors.contentSpecialOnly" class="error-msg" style="padding: 0 20px 10px">특수문자만 입력하여 작성할 수 없습니다.</p>
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
}

.project-desc-input {
  width: 100%; /* 부모 너비에 맞게 꽉 채움 */
  max-width: 1100px; /* 너무 길어지는 것이 싫다면 최대 너비 제한 */
  border: 1px solid #ddd;
  background-color: #ffffff;
  border-radius: 4px;
  padding: 8px 12px;
  font-size: 14px;
  margin-top: 8px; /* WIKI 글자와의 간격 */
}

.project-desc-input.is-error {
  border-color: #e74c3c !important;
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
  z-index: 1;
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

/* 제목 태그들의 기본 마진을 0으로 초기화 */
h1,
h2,
h3,
h4,
h5,
h6 {
  margin: 0 !important;
}

/* 만약 위키 타이틀(WIKI)만 딱 붙이고 싶다면 h1만 타겟팅 하세요 */
h1 {
  margin-top: 0 !important;
  margin-bottom: 0 !important;
}

/* 기존 toast-success 유지하고 아래 추가 */
.toast-fixed {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  padding: 16px 28px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
  z-index: 9999;
  white-space: nowrap;
}

.toc-item.toc-sub a {
  padding-left: 16px;
  font-size: 12px;
  color: #555;
}

.field-label.required::after {
  content: ' *';
  color: #e74c3c;
}

.is-error {
  border-color: #e74c3c !important;
}

.error-msg {
  font-size: 11px;
  color: #e74c3c;
  margin-top: 2px;
}
</style>
