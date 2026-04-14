<script setup>
import { useAuthStore } from '@/stores/auth';
import { useDocumentStore } from '@/stores/document';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const authstore = useAuthStore();
const docStore = useDocumentStore();

const userId = computed(() => authstore.user?.id);
const projectId = route.params.projectId;
const docId = route.params.docId;
const toast = useToast();
const submitted = ref(false);

const form = ref({
  title: '',
  doctype: '기획서',
  content: '',
  editReason: '', // ← 수정 사유
  attachments: [] // 기존 파일 목록
});

const deletedFileIds = ref([]); // 삭제할 기존 파일 id추적

// 기존 파일 삭제
const removeExistingFile = (index) => {
  const file = form.value.attachments[index];
  console.log('삭제할 파일:', file); // ← 추가
  console.log('file.id:', file.id); // ← 추가
  if (file.id) deletedFileIds.value.push(file.id); // 삭제 id 추적
  form.value.attachments.splice(index, 1);
};

const fileInput = ref(null);
const triggerFileInput = (e) => {
  e.stopPropagation();
  e.preventDefault();
  fileInput.value?.click();
  document.activeElement?.blur();
};

// 기존 데이터 불러오기
onMounted(async () => {
  await docStore.fetchDocumentDetail(docId);
  const doc = docStore.currentDocument;
  form.value.title = doc.title;
  form.value.doctype = doc.doctype;
  form.value.content = doc.content ?? '';
  form.value.attachments = doc.fileList ?? [];
});

// 새 파일 추가
const newFiles = ref([]);
const onFileChange = (e) => {
  Array.from(e.target.files).forEach((newFile) => {
    const isDuplicate = newFiles.value.some((f) => f.name === newFile.name && f.size === newFile.size);
    if (!isDuplicate) newFiles.value.push(newFile);
  });
  e.target.value = '';
};

const removeNewFile = (index) => newFiles.value.splice(index, 1);

const submit = async () => {
  submitted.value = true;
  if (!form.value.title || !form.value.editReason) return;

  // UpdateRequest 형태로 만들기
  const updateRequest = {
    id: docId,
    userId: userId.value,
    title: form.value.title,
    content: form.value.content ?? '',
    doctype: form.value.doctype,
    changeReason: form.value.editReason
  };

  try {
    await docStore.updateDocument(updateRequest, newFiles.value, deletedFileIds.value);
    toast.add({ severity: 'success', summary: '수정 완료', detail: '문서가 수정되었습니다.', life: 1500 });
    setTimeout(() => {
      router.push({ name: 'DocumentDetail', params: { projectId, docId } });
    }, 1500);
  } catch (err) {
    console.error(err);
    toast.add({ severity: 'error', summary: '수정 실패', detail: '수정에 실패했습니다.', life: 3000 });
  }
};

const goToDetail = () => {
  router.push({ name: 'DocumentDetail', params: { projectId, docId } });
};
</script>

<template>
  <div class="new-doc-page">
    <!-- 섹션 1: 문서 정보 -->
    <div class="panel">
      <div class="panel-title"><span class="badge-num">1</span> 문서 편집</div>

      <!-- 문서 제목 -->
      <div class="form-row">
        <div class="form-group full">
          <label class="form-label">문서 제목<span class="required">*</span></label>
          <div style="flex: 1; display: flex; flex-direction: column; gap: 4px">
            <input v-model="form.title" type="text" class="form-input" :class="{ 'is-error': submitted && !form.title }" placeholder="문서 글 제목을 입력해 주세요." />
            <span v-if="submitted && !form.title" class="inline-error">문서 제목을 입력해주세요.</span>
          </div>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <span class="form-label">문서 유형<span class="required">*</span></span>
          <div class="select-wrap">
            <select v-model="form.doctype" class="form-select">
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
          <span class="form-label">수정일</span>
          <input class="form-input readonly" type="text" :value="new Date().toISOString().split('T')[0]" readonly />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group full align-top">
          <span class="form-label pt">문서 설명</span>
          <textarea v-model="form.content" class="textarea" />
        </div>
      </div>
    </div>

    <!-- 섹션 2: 수정 사유 -->
    <div class="panel">
      <div class="panel-title"><span class="badge-num">2</span> 수정 사유</div>
      <div class="form-row">
        <!-- 수정 사유 -->
        <div class="form-group full align-top">
          <span class="form-label pt">수정 사유<span class="required">*</span></span>
          <div style="flex: 1; display: flex; flex-direction: column; gap: 4px">
            <textarea v-model="form.editReason" class="textarea" :class="{ 'is-error': submitted && !form.editReason }" placeholder="수정 사유를 입력해주세요." />
            <span v-if="submitted && !form.editReason" class="inline-error">수정 사유를 입력해주세요.</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 섹션 3: 첨부 파일 -->
    <div class="panel">
      <div class="panel-title"><span class="badge-num">3</span> 첨부 파일</div>
      <button type="button" class="btn btn-attach" @click.stop.prevent="triggerFileInput">첨부파일 등록</button>
      <input ref="fileInput" type="file" multiple style="display: none" @change="onFileChange" />

      <!-- 기존 파일 -->
      <div class="attach-area">
        <span v-if="!form.attachments.length && !newFiles.length" class="attach-placeholder">파일을 첨부해주세요</span>
        <ul class="file-list">
          <li v-for="(f, i) in form.attachments" :key="'old-' + i">
            <span class="file-name">{{ f.name }}</span>
            <span class="file-badge">기존</span>
            <button type="button" class="btn-remove" @click="removeExistingFile(i)">✕</button>
          </li>
          <li v-for="(f, i) in newFiles" :key="'new-' + i">
            <span class="file-name">{{ f.name }}</span>
            <span class="file-badge new">신규</span>
            <button type="button" class="btn-remove" @click="removeNewFile(i)">✕</button>
          </li>
        </ul>
      </div>
    </div>

    <!-- 하단 버튼 -->
    <div class="footer-row">
      <button type="button" class="btn btn-cancel" @click="goToDetail">취소</button>
      <button type="button" class="btn btn-primary" @click="submit">문서수정</button>
    </div>
  </div>
</template>

<style scoped>
.new-doc-page {
  background: #f5f5f5;
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
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.file-name {
  flex: 1; /* 파일명이 남은 공간 차지 */
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
  background: #f0f0f0;
  border: 1px solid #bbb;
  color: #555;
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

.file-badge {
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 3px;
  background: #ddd;
  color: #666;
  margin-left: 6px;
}
.file-badge.new {
  background: #e8920e;
  color: #fff;
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.inline-error {
  font-size: 11px;
  color: #e74c3c;
}

.is-error {
  border-color: #e74c3c !important;
}
</style>
