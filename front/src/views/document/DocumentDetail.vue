<script setup>
import { defineEmits, defineProps } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();

const goToList = () => {
  const projectId = route.params.projectId;

  router.push({
    name: 'DocumentList',
    params: { projectId: projectId }
  });
};

defineEmits(['edit', 'back']);

const props = defineProps({
  doc: {
    type: Object,
    default: () => ({
      type: '기획서',
      title: '프로젝트 기획서',
      author: '홍길동',
      date: '2026.03.19',
      description: `앱 리디자인 프로젝트의 전반적인 범위, 목표, 일정, 팀 구성을 정의하는 핵심 기획 문서입니다.
2025년 3분기 출시를 목표로 MAU 30% 증대를 달성하기 위한 로드맵을 포함합니다.`,
      files: [
        { name: '프로젝트 기획서', size: '2.4 MB', uploadedAt: '2026.03.19' },
        { name: '참고사항.txt', size: '4.7 MB', uploadedAt: '2026.03.19' },
        { name: '프로젝트 정리표.xlsx', size: '3.1 MB', uploadedAt: '2026.03.19' }
      ]
    })
  }
});
</script>

<template>
  <div class="doc-detail-page">
    <!-- 섹션 1: 문서 헤더 -->
    <div class="panel">
      <div class="doc-header">
        <div>
          <div class="panel-label">{{ doc.type }}</div>
          <div class="doc-title">{{ doc.title }}</div>
          <div class="doc-meta">
            <span>작성자 {{ doc.author }}</span>
            <span>작성일 {{ doc.date }}</span>
          </div>
        </div>
        <button class="btn btn-edit" @click="$emit('edit', doc)">편집</button>
      </div>
    </div>

    <!-- 섹션 2: 내용 -->
    <div class="panel">
      <div class="panel-label">내용</div>
      <div class="content-box">{{ doc.description }}</div>
    </div>

    <!-- 섹션 3: 첨부파일 -->
    <div class="panel">
      <div class="panel-label">첨부파일</div>
      <div class="file-list">
        <div v-for="(file, i) in doc.files" :key="i" class="file-row">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-meta">용량 {{ file.size }} &nbsp; {{ file.uploadedAt }} 업로드</span>
        </div>
        <div v-if="!doc.files.length" class="file-empty">첨부파일이 없습니다.</div>
      </div>
    </div>

    <!-- 섹션 4: 목록 버튼 -->
    <div class="footer-row">
      <button class="btn btn-list" @click="goToList('back')">목록</button>
    </div>
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
