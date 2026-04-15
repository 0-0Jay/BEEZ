<script setup>
import { useAuthStore } from '@/stores/auth';
import axios from '@/stores/AxiosInstance';
import { useDocumentStore } from '@/stores/document';
import { useToast } from 'primevue/usetoast';
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const docStore = useDocumentStore();
const authstore = useAuthStore();
const userId = authstore.user.id;
const toast = useToast();

const projectId = route.params.projectId;
const docId = route.params.docId;

//즐겨찾기 토글
const toggleFavorite = async () => {
  await docStore.toggleFavorite(userId, docId);
};

onMounted(async () => {
  await docStore.fetchDocumentDetail(docId);
  await docStore.fetchDocumentList(projectId, userId);
  const listDoc = docStore.documentList.find((d) => d.id == docId);

  if (listDoc && docStore.currentDocument) {
    docStore.currentDocument.isMarked = listDoc.isMarked;
  }
});

const goToList = () => {
  router.push({ name: 'DocumentList', params: { projectId: projectId } });
};

const goToEdit = () => {
  router.push({ name: 'DocumentEdit', params: { projectId, docId } });
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  if (isNaN(date)) {
    const parts = dateStr.split(' ')[0].split('/');
    if (parts.length === 3) return `20${parts[0]}-${parts[1]}-${parts[2]}`;
    return dateStr;
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const downloadFile = async (fileDetailId) => {
  try {
    // 1. try 블록 시작
    const response = await axios.get(`/document/download/${fileDetailId}`, {
      responseType: 'blob'
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;

    const disposition = response.headers['content-disposition'];

    // 2. const 대신 let으로 선언하여 값 변경 가능하게 수정
    let fileName = 'downloaded_file';

    if (disposition && disposition.includes('filename="')) {
      fileName = decodeURIComponent(disposition.split('filename="')[1].replace('"', ''));
    }

    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    // 3. try와 짝을 이루는 catch 블록
    toast.add({
      severity: 'error',
      summary: '다운로드 실패',
      detail: '오류가 발생하여 다운로드 할 수 없습니다.',
      life: 3000
    });
    console.error('Download error:', error);
  }
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
            <!-- <div class="doc-title">{{ docStore.currentDocument.title }}</div> -->
            <div class="doc-title">
              <!-- 별표 버튼 추가 -->
              <span class="star" :class="{ active: docStore.currentDocument.isMarked === 'Y' }" @click="toggleFavorite">
                {{ docStore.currentDocument.isMarked === 'Y' ? '★' : '☆' }}
              </span>
              {{ docStore.currentDocument.title }}
            </div>
            <div class="doc-meta">
              <span>작성자 - {{ docStore.currentDocument.userName }}</span>
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
          <template v-if="docStore.currentDocument.fileList?.length">
            <div v-for="(file, i) in docStore.currentDocument.fileList" :key="i" class="file-row">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-meta">용량 {{ (file.size / 1024).toFixed(1) }}KB</span>
              <button class="btn-download" @click="downloadFile(file.id)">다운로드</button>
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
  grid-template-columns: 200px 1fr auto;
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

.btn-download {
  background: #f0f0f0;
  border: 1px solid #bbb;
  color: #555;
  height: 26px;
  padding: 0 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.btn-download:hover {
  background: #e5e5e5;
}

.star {
  color: #ccc;
  cursor: pointer;
  font-size: 20px;
  margin-right: 6px;
  transition: color 0.2s;
}
.star.active {
  color: #e8920e;
}
.star:hover {
  color: #e8920e;
}
</style>
