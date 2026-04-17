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
      <div class="panel status-panel">데이터를 불러오는 중입니다...</div>
    </template>

    <template v-else-if="!docStore.currentDocument">
      <div class="panel status-panel">문서를 찾을 수 없습니다.</div>
    </template>

    <template v-else>
      <div class="panel panel-header-dark">
        <div class="header-content">
          <div class="header-tags"></div>
          <h1 class="doc-title">
            <span class="star" :class="{ active: docStore.currentDocument.isMarked === 'Y' }" @click="toggleFavorite">
              {{ docStore.currentDocument.isMarked === 'Y' ? '★' : '☆' }}
            </span>

            <span v-if="docStore.currentDocument.editedOn" class="edited-badge">[수정]</span>

            {{ docStore.currentDocument.title }}

            <span class="badge" :class="docStore.currentDocument.doctype">
              {{ docStore.currentDocument.doctype }}
            </span>
          </h1>

          <div class="doc-meta">
            <span>작성자: {{ docStore.currentDocument.userName }}</span>
            <span class="divider">|</span>
            <span>작성일: {{ formatDate(docStore.currentDocument.createdOn) }}</span>
          </div>
        </div>
        <button class="btn btn-edit-top" @click="goToEdit">편집</button>
      </div>

      <div class="panel">
        <div class="card-header">상세 내용</div>
        <div class="panel-body">
          <div class="content-box">{{ docStore.currentDocument.content }}</div>
        </div>
      </div>

      <div class="panel panel-edited" v-if="docStore.currentDocument.editedOn">
        <div class="card-header">수정 사유</div>
        <div class="panel-body">
          <div class="history-info-header">
            <span class="history-user"
              ><strong>{{ docStore.currentDocument.userName }}</strong
              >님이 문서를 수정했습니다.</span
            >
            <span class="history-date">수정일: {{ formatDate(docStore.currentDocument.editedOn) }}</span>
          </div>
          <div class="content-box history-content-box">
            {{ docStore.currentDocument.changeReason }}
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="card-header">
          첨부파일 <span class="header-count">{{ docStore.currentDocument.fileList?.length || 0 }}</span>
        </div>
        <div class="panel-body">
          <div v-if="docStore.currentDocument.fileList?.length" class="file-grid">
            <div v-for="(file, i) in docStore.currentDocument.fileList" :key="i" class="file-item-card">
              <div class="file-icon">📄</div>
              <div class="file-info">
                <div class="file-name">{{ file.name }}</div>
                <div class="file-details">{{ (file.size / 1024).toFixed(1) }}KB | {{ formatDate(docStore.currentDocument.createdOn) }} 업로드</div>
              </div>
              <button class="btn-download-icon" @click="downloadFile(file.id)">다운로드</button>
            </div>
          </div>
          <div v-else class="file-empty">첨부된 파일이 없습니다.</div>
        </div>
      </div>

      <div class="footer-row">
        <button class="btn btn-list" @click="goToList('back')">목록으로 돌아가기</button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.doc-detail-page {
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: #ffffff;
  min-height: 100vh;
}

/* 공통 패널 구조 */
.panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

/* 상단 다크 헤더 패널 */
.panel-header-dark {
  background: #f2f3f8;
  padding: 30px;
  padding-bottom: 10px;
  padding-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #ffffff;
  border: none;
}

.header-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tag-type {
  background: #4a5568;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: bold;
}
.tag-status {
  background: #2f855a;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 11px;
}
.tag-version {
  background: #ed8936;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 11px;
}

.doc-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.doc-meta {
  font-size: 14px;
  color: #a0aec0;
  display: flex;
  gap: 12px;
}

.btn-edit-top {
  background: #ed8936;
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

/* 카드 섹션 헤더 */
.card-header {
  background: #5b6e96;
  color: #ffffff;
  padding: 12px 20px;
  font-weight: 700;
  font-size: 14px;
}

.header-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 6px;
  font-size: 12px;
}

.panel-body {
  padding: 24px;
}

/* 본문 영역 */
.content-box {
  line-height: 1.8;
  color: #4a5568;
  white-space: pre-line;
  font-size: 14px;
}

.panel-edited .card-header {
  background-color: #f2f3f8;
  color: #5b6e96;
}
.history-body {
  background-color: #ffffff;
}
.history-item {
  display: flex;
  gap: 20px;
}

.history-info-header {
  display: flex;
  justify-content: flex-start; /* 왼쪽 정렬 */
  align-items: center;
  gap: 15px; /* 문구와 날짜 사이의 간격 */
  margin-bottom: 12px;
}

.history-date {
  font-size: 13px;
  color: #718096;
  margin-left: 5px; /* 추가적인 왼쪽 여백 */
}
.history-content-box {
  margin-top: 10px; /* 상단 문구와의 간격 확보 */
  padding: 16px; /* 박스 내부 여백 */
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #1a202c;
  line-height: 1.6; /* 줄 간격 조절로 가독성 향상 */
}

.user-avatar {
  width: 44px;
  height: 44px;
  background: #ffda7a;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 18px;
  flex-shrink: 0;
}
.history-date {
  font-size: 13px;
  color: #a0aec0;
  margin: 6px 0 12px;
}
.border-orange {
  border: 1px dashed #ed8936;
  padding: 15px;
  border-radius: 8px;
  background: #fff;
}

/* 첨부파일 그리드 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
.file-item-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #edf2f7;
  border-radius: 8px;
  background: #fff;
  justify-content: space-between;
}
.file-icon {
  font-size: 24px;
}

/* 파일 정보 영역 (이름이 길어질 경우 대비) */
.file-info {
  flex: 1; /* 남은 공간을 모두 차지하게 함 */
  min-width: 0; /* 텍스트 생략 기능을 위해 필요 */
}
.file-name {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}
.file-details {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
}
.btn-download-icon {
  flex-shrink: 0; /* 중요: 공간이 부족해도 버튼 크기가 줄어들지 않음 */
  white-space: nowrap; /* 글자가 절대 줄바꿈되지 않음 */
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  padding: 8px 14px; /* 가로 세로 여백을 넉넉히 주어 가독성 확보 */
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  color: #4a5568;
  min-width: 80px; /* 최소 너비를 지정하여 균형 유지 */
}

/* 푸터 */
.footer-row {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
.btn-list {
  background: #fff;
  border: 1px solid #cbd5e0;
  color: #718096;
  padding: 10px 30px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.star {
  cursor: pointer;
  color: #4a5568;
}
.star.active {
  color: #ed8936;
}

/* 뱃지 */
/* 기존 tag-type 대신 badge 스타일 사용 */
.badge {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  line-height: 1.4;
  border-width: 1px; /* 테두리 두께 */
  border-style: solid;
}

.badge {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
}
.badge.기획서 {
  background: #fde8e8;
  color: #c0392b;
  border-color: #f5b7b1;
}
.badge.설계서 {
  background: #e6f1fb;
  color: #185fa5;
  border-color: #aed6f1;
}
.badge.회의록 {
  background: #fef0e0;
  color: #c47000;
  border-color: #fad7a0;
}
.badge.보고서 {
  background: #eaf6ee;
  color: #1e7e34;
  border-color: #abebc6;
}
.badge.기타 {
  background: #f0e8fb;
  color: #6b3fa0;
  border-color: #d7bde2;
}

/* 추가적인 태그 스타일 (유지) */
.tag-status {
  background: #e2e8f0; /* 다크 헤더가 연한 색이므로 태그 배경을 살짝 조정 */
  color: #4a5568;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid #cbd5e0;
}

.tag-version {
  background: #ed8936;
  color: #fff;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

/* 헤더 텍스트 색상 가독성 조정 */
.panel-header-dark .doc-title {
  color: #1a1816; /* 배경이 f2f3f8(연한색)이므로 글자색을 어둡게 설정 */
}

.panel-header-dark .doc-meta {
  color: #718096;
  padding: 20px;
}

/* 제목 옆 수정 배지 스타일 */
.edited-badge {
  color: #ed8936; /* 상세 페이지의 포인트 컬러(주황색)와 맞춤 */
  font-size: 20px; /* 제목 크기에 맞춰 목록보다 조금 더 크게 조정 */
  font-weight: 600;
  margin-right: 4px;
}

/* 제목 정렬 보정 (이미 flex가 적용되어 있어 자연스럽게 배치됩니다) */
.doc-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #1a1816;
}
</style>
