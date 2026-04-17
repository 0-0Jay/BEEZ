<script setup>
//내코드
import { useAuthStore } from '@/stores/auth';
import { useDocumentStore } from '@/stores/document';
import { useToast } from 'primevue';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();

const authstore = useAuthStore();
const docStore = useDocumentStore();
const userId = computed(() => authstore.user?.id);
const projectId = route.params.projectId;
const toast = useToast();
const submitted = ref(false);

//글자수 카운트 계산
const contentCount = computed(() => form.value.content.length);

//입력시 500자 제한 로직
const handleInput = (e) => {
  if (e.target.value.length > 500) {
    form.value.content = e.target.value.slice(0, 500);
  }
};

const form = ref({
  title: '',
  doctype: '',
  content: '',
  attachments: []
});

// //파일 첨부 관련 로직

const fileInput = ref(null);
const triggerFileInput = () => fileInput.value?.click();

const onFileChange = (e) => {
  const MAX_SIZE = 10 * 1024 * 1024;

  Array.from(e.target.files).forEach((newFile) => {
    // 1. 파일 크기 체크
    if (newFile.size > MAX_SIZE) {
      toast.add({
        severity: 'error',
        summary: '업로드 불가',
        detail: `파일 크기가 10MB를 넘어서 업로드 할 수 없습니다\n파일명: ${newFile.name}`,
        life: 3000
      });
      return; // 10MB 초과 시 해당 파일은 추가하지 않고 건너뜀
    }

    // 2. 중복 체크 후 추가
    const isDuplicate = form.value.attachments.some((f) => f.name === newFile.name && f.size === newFile.size);

    if (!isDuplicate) {
      form.value.attachments.push(newFile);
    }
  });

  // input 초기화 (같은 파일을 다시 선택할 수 있도록)
  e.target.value = '';
};

const removeFile = (index) => form.value.attachments.splice(index, 1);

//문서 등록 실행
const submit = async () => {
  submitted.value = true;
  if (!form.value.title || !form.value.content) return;
  const formData = new FormData();

  formData.append('projectId', projectId);
  formData.append('userId', userId.value);
  formData.append('title', form.value.title);
  formData.append('content', form.value.content ?? '');
  formData.append('doctype', form.value.doctype);

  // formData.append('document', new Blob([JSON.stringify(docData)], { type: 'application/json' }));

  form.value.attachments.forEach((file) => {
    formData.append('files', file);
  });

  try {
    await docStore.writeDocument(projectId, formData);
    toast.add({ severity: 'success', summary: '등록 완료', detail: '작성한 문서가 등록되었습니다.', life: 1500 });
    setTimeout(() => goToList(), 1500);
  } catch (err) {
    console.error(err);
    toast.add({ severity: 'error', summary: '등록 실패', detail: '등록에 실패했습니다.', life: 3000 });
  }
};

const goToList = () => {
  router.push({ name: 'DocumentList', params: { projectId } });
};
</script>

<template>
  <div class="new-doc-page">
    <!-- 섹션 1: 문서 정보 -->
    <div class="panel">
      <div class="panel-title"><span class="badge-num">1</span> 새문서 등록</div>

      <div class="form-row">
        <div class="form-group full">
          <label class="form-label">문서 제목<span class="required">*</span></label>
          <input v-model="form.title" type="text" class="form-input" :class="{ 'is-error': submitted && !form.title }" placeholder="문서 글 제목을 입력해 주세요." />
          <span v-if="submitted && !form.title" class="inline-error">문서 제목을 입력해주세요.</span>
        </div>
      </div>

      <!-- 문서 유형 / 작성자 / 작성일 -->
      <div class="form-row">
        <div class="form-group">
          <span class="form-label">문서 유형<span class="required">*</span></span>
          <div class="select-wrap">
            <select
              v-model="form.doctype"
              class="form-select"
              :class="{
                'is-error': submitted && !form.doctype,
                'placeholder-style': form.doctype === ''
              }"
            >
              <option value="" disabled selected hidden>문서 유형을 선택해주세요.</option>
              <option value="기타">기타</option>
              <option value="기획서">기획서</option>
              <option value="설계서">설계서</option>
              <option value="회의록">회의록</option>
              <option value="보고서">보고서</option>
            </select>
            <span class="select-arrow">▼</span>
          </div>
        </div>

        <div class="form-group">
          <span class="form-label">작성일</span>
          <input class="form-input readonly" type="text" :value="new Date().toISOString().split('T')[0]" readonly />
        </div>
      </div>

      <!-- 문서 설명 -->
      <div class="form-row">
        <div class="form-group full align-top">
          <label class="form-label">문서 설명<span class="required">*</span></label>
          <div style="flex: 1; display: flex; flex-direction: column; gap: 4px; position: relative">
            <textarea v-model="form.content" class="textarea" :class="{ 'is-error': submitted && !form.content }" maxlength="500" @input="handleInput" placeholder="내용을 입력해주세요. (최대 500자)" />
            <div class="char-count">
              <span :class="{ 'text-error': contentCount >= 500 }">{{ contentCount }}</span> / 500
            </div>
            <span v-if="submitted && !form.content" class="inline-error">문서 설명을 입력해주세요.</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 섹션 2: 첨부 파일 -->
    <div class="panel">
      <div class="panel-title"><span class="badge-num">2</span> 첨부 파일</div>
      <button class="btn btn-primary pi pi-paperclip" @click="triggerFileInput">첨부파일 등록</button>
      <small class="text-[#9A9B90] mt-2 block text-base">파일당 최대 10MB / 여러 파일을 한 번에 선택하거나 반복 추가할 수 있습니다.</small>

      <input ref="fileInput" type="file" multiple style="display: none" @change="onFileChange" />
      <div class="attach-area">
        <span v-if="!form.attachments.length" class="attach-placeholder">파일을 첨부해주세요</span>

        <ul v-else class="file-list">
          <li v-for="(f, i) in form.attachments" :key="i">
            {{ f.name }}
            <button class="btn-remove" @click="removeFile(i)">✕</button>
          </li>
        </ul>
      </div>
    </div>

    <!-- 하단 버튼 -->
    <div class="footer-row">
      <button class="btn btn-cancel" @click="goToList">취소</button>
      <button class="btn btn-primary" @click="submit">등록</button>
    </div>
  </div>
</template>

<style scoped>
.new-doc-page {
  background: #f2f3f8;
  min-height: 100vh;
  padding: 24px;
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}

.panel {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 24px;
  margin-bottom: 16px;
}

.panel-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge-num {
  background: #e8920e;
  color: #fff;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.form-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.form-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 200px;
}

.form-group.full {
  flex: 1 1 100%;
}
.form-group.align-top {
  align-items: flex-start;
}

.form-label {
  white-space: nowrap;
  color: #666;
  font-size: 13px;
  min-width: 60px;
}

.form-label.pt {
  padding-top: 8px;
}

.required {
  color: #e8920e;
  margin-left: 2px;
}

.select-wrap {
  flex: 1;
  position: relative;
}

.form-select {
  width: 100%;
  height: 34px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0 10px;
  background: #fff;
  appearance: none;
  cursor: pointer;
  outline: none;
  color: #333;
}

/* 2. 아무것도 선택하지 않았을 때 (플레이스홀더 상태) */
.form-select.placeholder-style {
  color: #bbb; /* input의 placeholder 색상과 동일하게 설정 */
}

/* 3. input의 placeholder 색상도 명시적으로 맞추고 싶다면 */
.form-input::placeholder {
  color: #bbb;
}

.form-select:focus {
  border-color: #e8920e;
}

.select-arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #999;
  font-size: 11px;
}

.form-input {
  flex: 1;
  height: 34px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0 10px;
  background: #fff;
  outline: none;
  color: #333;
}

.form-input.readonly {
  background: #f5f5f5;
  color: #888;
  cursor: default;
}

.textarea {
  flex: 1;
  min-height: 80px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 8px 10px;
  resize: vertical;
  outline: none;
  font-family: inherit;
  color: #333;
}

.textarea:focus {
  border-color: #e8920e;
}

.attach-area {
  border: 1px dashed #ddd;
  border-radius: 4px;
  min-height: 60px;
  margin-top: 10px;
  background: #fafafa;
  padding: 12px;
  display: flex;
  align-items: flex-start;
}

.attach-placeholder {
  color: #bbb;
  font-size: 13px;
}

.file-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
}

.file-list li {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.btn-remove {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 12px;
}

.btn {
  height: 34px;
  padding: 0 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  border: none;
}

.btn-primary {
  background: #e8920e;
  color: #fff;
}
.btn-primary:hover {
  background: #d07d0b;
}

.btn-cancel {
  background: #fff;
  border: 1px solid #bbb;
  color: #555;
}

.btn-attach {
  background: #e8920e;
  border: 1px solid #d07d0b;
  color: #ffffff;
  height: 30px;
  padding: 0 14px;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
}

.footer-row {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.inline-error {
  font-size: 11px;
  color: #e74c3c;
}

.is-error {
  border-color: #e74c3c !important;
}

/* 스타일 하단에 추가 */
.char-count {
  align-self: flex-end; /* 오른쪽 정렬 */
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}

.text-error {
  color: #e74c3c;
  font-weight: bold;
}

/* textarea에 약간의 여백을 주어 카운트와 겹치지 않게 할 수도 있습니다 */
.textarea {
  padding-bottom: 20px;
}
</style>
