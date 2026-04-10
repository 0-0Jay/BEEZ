<script setup>
import { useDocumentStore } from '@/stores/document';
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const docStore = useDocumentStore();

const projectId = route.params.projectId;
const docId = route.params.docId;

onMounted(async () => {
  await docStore.fetchDocumentDetail(docId);
});

const goToList = () => {
  router.push({ name: 'DocumentList', params: { projectId: projectId } });
};

const goToEdit = () => {
  router.push({ name: 'DocumentEdit', params: { projectId, docId } });
};

// 날짜 포맷 함수 추가 (YYYY-MM-DD)
const formatDate = (dateStr) => {
  if (!dateStr) return '';

  // 문자열이 '26/04/10 14:27:18...' 형식이거나 ISO 형식일 때를 모두 대응
  // 만약 DB에서 26/04/10 처럼 오면 연도 처리가 필요할 수 있습니다.
  const date = new Date(dateStr);

  // 날짜 객체가 유효하지 않을 경우 (예: '26/04/10' 형식이 Date에서 안 읽힐 때)
  // 단순 문자열 슬라이싱으로 처리하는 방법이 더 확실할 수 있습니다.
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
  <div class="doc-detail-page">
    <template v-if="docStore.loading">
      <div class="panel">불러오는 중...</div>
    </template>

    <template v-else-if="!docStore.currentDocument">
      <div class="panel">문서를 찾을 수 없습니다.</div>
    </template>

    <template v-else>
      <!-- 섹션 1: 문서 헤더 -->

      <div class="panel">
        <div class="doc-header">
          <div>
            <div class="panel-label">{{ docStore.currentDocument.doctype }}</div>
            <div class="doc-title">{{ docStore.currentDocument.title }}</div>
            <div class="doc-meta">
              <span>작성자 {{ docStore.currentDocument.userName }}</span>
              <span>작성일 {{ formatDate(docStore.currentDocument.createdOn) }}</span>
            </div>
          </div>
          <button class="btn btn-edit" @click="goToEdit">편집</button>
        </div>
      </div>

      <!-- 섹션 2: 내용 -->
      <div class="panel">
        <div class="panel-label">내용</div>
        <div class="content-box">{{ docStore.currentDocument.content }}</div>
      </div>

      <!-- 섹션 3: 첨부파일 -->
      <div class="panel">
        <div class="panel-label">첨부파일</div>
        <div class="file-list">
          <template v-if="docStore.currentDocument.files?.length">
            <div v-for="(file, i) in docStore.currentDocument.files" :key="i" class="file-row">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-meta">용량 {{ file.size }} &nbsp; {{ file.uploadedAt }} 업로드</span>
            </div>
          </template>
          <div v-else class="file-empty">첨부파일이 없습니다.</div>
        </div>
      </div>

      <!-- 섹션 4: 목록 버튼 -->
      <div class="footer-row">
        <button class="btn btn-list" @click="goToList('back')">목록</button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.doc-detail-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  font-family: 'Noto Sans KR', sans-serif;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}

/* 패널 공통 */
.panel {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 20px 24px;
}

/* 뱃지 번호 */
.badge-num {
  background: #e8920e;
  color: #fff;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.badge-num.small {
  width: 18px;
  height: 18px;
  font-size: 11px;
  margin-right: 6px;
}

/* 섹션 레이블 */
.panel-label {
  font-size: 12px;
  color: #888;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

/* 문서 헤더 */
.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.doc-title {
  font-size: 20px;
  font-weight: 500;
  margin-top: 4px;
}

.doc-meta {
  font-size: 13px;
  color: #888;
  margin-top: 10px;
  display: flex;
  gap: 20px;
}

/* 버튼 */
.btn {
  height: 32px;
  padding: 0 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  border: none;
}

.btn-edit {
  background: #e8920e;
  color: #fff;
}

.btn-edit:hover {
  background: #d07d0b;
}

/* 본문 영역 */
.content-box {
  border: 1px solid #ddd;
  border-radius: 4px;
  min-height: 160px;
  padding: 20px;
  background: #fff;
  color: #555;
  font-size: 13px;
  line-height: 1.8;
  white-space: pre-line;
}

/* 첨부파일 목록 */
.file-list {
  display: flex;
  flex-direction: column;
}

.file-row {
  display: grid;
  grid-template-columns: 200px 1fr;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  font-size: 13px;
}

.file-row:last-child {
  border-bottom: none;
}

.file-name {
  font-weight: 500;
  color: #333;
}

.file-meta {
  color: #888;
}

.file-empty {
  padding: 16px 0;
  color: #bbb;
  font-size: 13px;
}

/* 하단 목록 버튼 */
.footer-row {
  display: flex;
  justify-content: center;
}

.btn-list {
  background: #f0f0f0;
  border: 1px solid #bbb;
  color: #555;
  height: 34px;
  padding: 0 28px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.btn-list:hover {
  background: #e5e5e5;
}
</style>
