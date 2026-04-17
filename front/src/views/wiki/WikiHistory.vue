<script setup>
import { useAuthStore } from '@/stores/auth';
import { useWikiStore } from '@/stores/wiki';
import * as Diff from 'diff';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const wikiStore = useWikiStore();
const authStore = useAuthStore();

const versions = ref([]);
const openIndex = ref(null);
const showRollbackModal = ref(false);
const targetVersion = ref(null);

onMounted(async () => {
  const { wikiId } = route.params;
  versions.value = await wikiStore.fetchWikiVersionList(wikiId);
  // 각 버전의 wiki_id 확인
  versions.value.forEach((v, i) => {});
});

const toggleAccordion = (index) => {
  openIndex.value = openIndex.value === index ? null : index;
};

const stripHtml = (html) => {
  if (!html) return '';
  return html
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/p>/gi, '\n')
    .replace(/<\/h[1-6]>/gi, '\n')
    .replace(/<\/div>/gi, '\n')
    .replace(/<[^>]+>/g, '') // 나머지 태그 제거
    .replace(/&nbsp;/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&amp;/g, '&')
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => line.length > 0)
    .join('\n');
};

const getDiff = (index) => {
  if (index >= versions.value.length - 1) return [];
  const oldContent = stripHtml(versions.value[index + 1].content);
  const newContent = stripHtml(versions.value[index].content);
  return Diff.diffLines(oldContent, newContent);
};

const confirmRollback = (version) => {
  targetVersion.value = version;
  showRollbackModal.value = true;
};

const executeRollback = async () => {
  const { wikiId, projectId } = route.params;

  // const latestVersion = versions.value[0];
  // let nextVersionNum = '1.0';
  // if (latestVersion?.versionName) {
  //   const match = latestVersion.versionName.match(/v(\d+\.\d+)/);
  //   if (match) {
  //     nextVersionNum = (parseFloat(match[1]) + 0.1).toFixed(1);
  //   }
  // }

  // 프로젝트명 추출 (versionName에서 v숫자 앞부분)
  // const projectTitle = latestVersion?.versionName?.replace(/v[\d.]+$/, '').trim() ?? '';
  // const newVersionName = `${projectTitle} v${nextVersionNum}`;

  const saveData = {
    wikiRequest: {
      id: wikiId,
      projectId
    },
    versionRequest: {
      content: targetVersion.value.content,
      userId: authStore.user.id,
      wikiId: wikiId,
      description: `${targetVersion.value.versionId} 버전으로 롤백`,
      // description: `${targetVersion.value.versionName} 버전으로 롤백`,
      // versionName: newVersionName,
      links: targetVersion.value.links ?? '[]',
      wikiInfo: targetVersion.value.wikiInfo ?? ''
    }
  };

  const result = await wikiStore.saveWiki(saveData);

  if (result) {
    showRollbackModal.value = false;
    alert('롤백이 완료되었습니다.');
    // 버전 목록 새로고침
    versions.value = await wikiStore.fetchWikiVersionList(wikiId);
  }
};

const goBack = () => router.back();
</script>

<template>
  <div class="history-page">
    <div class="history-header">
      <Button label="이전 페이지" icon="pi pi-search" raised @click="goBack" />
    </div>

    <div v-for="(version, index) in versions" :key="index" class="version-card">
      <!-- 카드 헤더 (클릭 시 아코디언) -->
      <div class="version-header" @click="toggleAccordion(index)">
        <div class="version-left">
          <span :class="['badge-version', index === 0 ? 'badge-latest' : 'badge-old']">
            <!-- {{ version.versionName.match(/v[\d.]+/)?.[0] ?? version.versionName }} -->
            {{ version.versionId }}
          </span>
          <div class="version-description">{{ version.description }}</div>
        </div>
        <div class="version-right">
          작성자 : {{ version.userName }} / 작성일 : {{ version.createdOn?.substring(0, 10) }}
          <button v-if="index !== 0" class="btn-rollback" @click.stop="confirmRollback(version)">이 버전으로 롤백</button>
        </div>
      </div>

      <!-- diff 뷰 (아코디언 펼침) -->
      <div v-if="openIndex === index && index < versions.length - 1" class="diff-view">
        <div class="diff-header">변경된 내용 (diff)</div>
        <div class="diff-body">
          <template v-for="(part, i) in getDiff(index)" :key="i">
            <template v-if="part.added">
              <div v-for="(line, li) in part.value.split('\n').filter((l) => l.trim())" :key="`add-${i}-${li}`" class="diff-line diff-added">
                <span class="diff-prefix">+</span>
                <span class="diff-text">{{ line }}</span>
              </div>
            </template>
            <template v-else-if="part.removed">
              <div v-for="(line, li) in part.value.split('\n').filter((l) => l.trim())" :key="`rem-${i}-${li}`" class="diff-line diff-removed">
                <span class="diff-prefix">-</span>
                <span class="diff-text">{{ line }}</span>
              </div>
            </template>
            <template v-else>
              <div v-for="(line, li) in part.value.split('\n').filter((l) => l.trim())" :key="`ctx-${i}-${li}`" class="diff-line diff-context">
                <span class="diff-prefix"> </span>
                <span class="diff-text">{{ line }}</span>
              </div>
            </template>
          </template>
        </div>
      </div>

      <!-- 최초 버전은 diff 없이 안내 문구 -->
      <div v-else-if="openIndex === index" class="diff-view">
        <div class="diff-body" style="color: #aaa; text-align: center">최초 작성된 버전입니다.</div>
      </div>
    </div>

    <!--롤백기능-->
    <div v-if="showRollbackModal" class="modal-overlay" @click.self="showRollbackModal = false">
      <div class="modal-box">
        <div class="modal-header">
          <span>롤백 확인</span>
          <button class="modal-close" @click="showRollbackModal = false">×</button>
        </div>
        <div class="modal-body">
          <!-- <p>{{ targetVersion?.versionName }} 버전으로 롤백하시겠습니까?</p> -->
          <p>{{ targetVersion?.versionId }} 버전으로 롤백하시겠습니까?</p>
          <p style="font-size: 12px; color: #999; margin-top: 8px">현재 내용이 새 버전으로 저장되고, 선택한 버전의 내용으로 되돌아갑니다.</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showRollbackModal = false">취소</button>
          <button class="btn-rollback-confirm" @click="executeRollback">롤백</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.version-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}
.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
}
.version-header:hover {
  background: #f9f9f9;
}

.badge-version {
  background: #e8920e22;
  border: 1px solid #e8920e;
  border-radius: 20px;
  padding: 2px 12px;
  color: #e8920e;
  font-weight: bold;
  font-size: 13px;
  margin-right: 12px;
}

.diff-view {
  border-top: 1px solid #eee;
  background: #1e1e1e;
}
.diff-header {
  padding: 8px 16px;
  font-size: 12px;
  color: #aaa;
  border-bottom: 1px solid #333;
}
.history-page {
  padding: 20px;
  background: #f2f3f8;
  min-height: 100vh;
}
.history-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}
.btn-back {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  background: #e8920e;
  color: #fff;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
.btn-back:hover {
  background: #aa6f16;
}

.diff-body {
  padding: 12px 0;
  font-family: monospace;
  font-size: 13px;
  max-height: 400px;
  overflow-y: auto;
}
.diff-line {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 2px 16px;
  line-height: 1.6;
}
.diff-prefix {
  min-width: 12px;
  font-weight: bold;
  user-select: none;
}
.diff-text {
  white-space: pre-wrap;
  word-break: break-all;
  flex: 1;
}
.diff-added {
  background: #4caf5018;
  color: #4caf50;
}
.diff-removed {
  background: #f4433618;
  color: #f44336;
}
.diff-context {
  color: #888;
}

.version-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.version-description {
  font-size: 18px; /* 큰 글씨 */
  font-weight: 700; /* 볼드 */
  color: #1a1a1a;
}

.badge-version {
  border-radius: 20px;
  padding: 2px 12px;
  font-weight: bold;
  font-size: 13px;
  white-space: nowrap;
}

.badge-latest {
  background: #e8f5e9;
  border: 1px solid #4caf50;
  color: #2e7d32;
}

.badge-old {
  background: #e8eaf6;
  border: 1px solid #3949ab;
  color: #1a237e;
}

/* 롤백 모달 */
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
}
.modal-body {
  padding: 16px;
  font-size: 14px;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #eee;
}
.btn-cancel {
  padding: 6px 16px;
  border: 1px solid #bbb;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
}
/* 이 버전으로 롤백 버튼 → '프로젝트 등록' 버튼 스타일 */
.btn-rollback {
  padding: 6px 16px;
  border: 1px solid #1a1816;
  border-radius: 6px;
  background: #fff;
  color: #1a1816;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  margin-left: 12px;
}
.btn-rollback:hover {
  background: #f2f0eb;
}
.btn-rollback-confirm {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  background: #e8920e;
  color: #fff;
  cursor: pointer;
  font-size: 13px;
}
</style>
