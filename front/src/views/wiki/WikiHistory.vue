<script setup>
import { useWikiStore } from '@/stores/wiki';
import * as Diff from 'diff';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const wikiStore = useWikiStore();

const versions = ref([]);
const openIndex = ref(null);

// WikiHistory.vue onMounted에 console.log 추가
onMounted(async () => {
  const { wikiId } = route.params;
  versions.value = await wikiStore.fetchWikiVersionList(wikiId);
  console.log('wikiId:', wikiId);
  console.log('버전 개수:', versions.value.length);
  // 각 버전의 wiki_id 확인
  versions.value.forEach((v, i) => {
    console.log(`버전 ${i}:`, v.versionId, '/ wikiId:', v.wikiId);
  });
});

const toggleAccordion = (index) => {
  openIndex.value = openIndex.value === index ? null : index;
};

const stripHtml = (html) => {
  const div = document.createElement('div');
  div.innerHTML = html ?? '';
  return div.innerText;
};

const getDiff = (index) => {
  if (index >= versions.value.length - 1) return [];
  const oldContent = stripHtml(versions.value[index + 1].content);
  const newContent = stripHtml(versions.value[index].content);
  return Diff.diffLines(oldContent, newContent);
};

const goBack = () => router.back();
</script>

<template>
  <div class="history-page">
    <div class="history-header">
      <button class="btn-back" @click="goBack">이전 페이지</button>
    </div>

    <div v-for="(version, index) in versions" :key="index" class="version-card">
      <!-- 카드 헤더 (클릭 시 아코디언) -->
      <div class="version-header" @click="toggleAccordion(index)">
        <div class="version-left">
          <span class="badge-version">{{ version.versionName }}</span>
          <div>
            <div class="version-title">{{ version.description }}</div>
          </div>
        </div>
        <div class="version-right">작성자 : {{ version.userName }} / 작성일 : {{ version.createdOn?.substring(0, 10) }}</div>
      </div>

      <!-- diff 뷰 (아코디언 펼침) -->
      <div v-if="openIndex === index && index < versions.length - 1" class="diff-view">
        <div class="diff-header">변경된 내용 (diff)</div>
        <div class="diff-body">
          <template v-for="(part, i) in getDiff(index)" :key="i">
            <div v-if="part.added" class="diff-added">
              <span v-for="line in part.value.split('\n').filter((l) => l)" :key="line"> + {{ line }} </span>
            </div>
            <div v-else-if="part.removed" class="diff-removed">
              <span v-for="line in part.value.split('\n').filter((l) => l)" :key="line"> - {{ line }} </span>
            </div>
          </template>
        </div>
      </div>

      <!-- 최초 버전은 diff 없이 안내 문구 -->
      <div v-else-if="openIndex === index" class="diff-view">
        <div class="diff-body" style="color: #aaa; text-align: center">최초 작성된 버전입니다.</div>
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
.diff-body {
  padding: 12px 16px;
  font-family: monospace;
  font-size: 13px;
  max-height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.diff-added {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.diff-added span {
  color: #4caf50;
  background: #4caf5015;
  padding: 1px 8px;
  border-radius: 2px;
}
.diff-removed {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.diff-removed span {
  color: #f44336;
  background: #f4433615;
  padding: 1px 8px;
  border-radius: 2px;
}

.history-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}
.history-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}
.btn-back {
  padding: 6px 16px;
  border: 1px solid #bbb;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
}
</style>
