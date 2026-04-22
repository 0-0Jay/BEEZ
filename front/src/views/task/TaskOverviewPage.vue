<script setup>
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import { computed, onMounted } from 'vue';

const taskStore = useTaskStore();
const projectStore = useProjectStore();
const project = computed(() => projectStore.selectedProject);
const taskOverview = computed(() => taskStore.overview);
const WORKFLOW_STATUSES = ['신규', '실행', '해결', '반려', '완료'];

// 특정 필드(field)를 기준으로 workflow 상태별 개수를 집계한 행 배열 반환
function aggregateByField(field) {
  const map = {};

  for (const task of taskOverview.value) {
    const key = task[field]?.trim() || '미지정';
    if (!map[key]) {
      map[key] = { label: key, 신규: 0, 실행: 0, 해결: 0, 반려: 0, 완료: 0, isTotal: false };
    }
    const wf = task.workflow?.trim();
    if (WORKFLOW_STATUSES.includes(wf)) {
      map[key][wf]++;
    }
  }

  // 각 행에 합계 추가
  const rows = Object.values(map).map((row) => ({
    ...row,
    합계: WORKFLOW_STATUSES.reduce((sum, s) => sum + row[s], 0)
  }));

  // 합계 행(총계)
  const totalRow = { label: '합계', isTotal: true, 신규: 0, 실행: 0, 해결: 0, 반려: 0, 완료: 0, 합계: 0 };
  for (const row of rows) {
    for (const s of WORKFLOW_STATUSES) totalRow[s] += row[s];
    totalRow['합계'] += row['합계'];
  }
  rows.push(totalRow);

  return rows;
}

// 표 정의
const overviewTables = computed(() => {
  const definitions = [
    { key: 'type', title: '일감 유형', labelHeader: '유형명', field: 'type' },
    { key: 'category', title: '일감 범주', labelHeader: '범주명', field: 'category' },
    { key: 'creator', title: '일감 생성자', labelHeader: '이름', field: 'creator' },
    { key: 'userId', title: '담당자', labelHeader: '이름', field: 'userId' },
    { key: 'priority', title: '우선순위', labelHeader: '우선순위', field: 'priority' },
    { key: 'versionId', title: '버전', labelHeader: '버전', field: 'versionId' }
  ];

  return definitions.map((def) => {
    const rows = aggregateByField(def.field);
    // 합계 행 제외한 총 건수
    const totalCount = rows.find((r) => r.isTotal)?.['합계'] ?? 0;
    return { ...def, rows, totalCount };
  });
});

onMounted(async () => {
  await taskStore.findTaskOverview(project.value?.id);
});
</script>
<template>
  <div class="min-h-screen bg-white p-6">
    <!-- 헤더 -->
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-gray-800">일감 보고서</h1>
      <p class="text-base text-gray-500 mt-1">프로젝트 내 전체 일감 현황 집계</p>
    </div>

    <!-- 6개 표 그리드 -->
    <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
      <div v-for="table in overviewTables" :key="table.key" class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <!-- 표 제목 -->
        <div class="px-5 py-3 border-b border-gray-100 bg-gray-50 flex items-center gap-2">
          <span class="text-base font-semibold text-gray-700">{{ table.title }}</span>
          <span class="ml-auto text-base text-gray-400">총 {{ table.totalCount }}건</span>
        </div>

        <!-- DataTable -->
        <DataTable
          :value="table.rows"
          class="text-base"
          :pt="{
            table: { class: 'w-full' },
            thead: { class: 'bg-slate-50' }
          }"
          showGridlines
          size="small"
        >
          <!-- 항목명 -->
          <Column
            field="label"
            :header="table.labelHeader"
            class="font-medium"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-4 py-2 text-left' },
              bodyCell: { class: 'px-4 py-2 text-gray-700 font-medium' }
            }"
          >
            <template #body="{ data }">
              <span :class="data.isTotal ? 'font-bold text-gray-900' : 'text-gray-700'">
                {{ data.label }}
              </span>
            </template>
          </Column>

          <!-- 신규 -->
          <Column
            field="신규"
            header="신규"
            class="text-center"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-3 py-2 text-center' },
              bodyCell: { class: 'px-3 py-2 text-center' }
            }"
          >
            <template #body="{ data }">
              <span :class="['inline-block min-w-6 text-center rounded px-1', data.isTotal ? 'font-bold text-gray-900' : data['신규'] > 0 ? 'text-blue-600' : 'text-gray-300']">
                {{ data['신규'] }}
              </span>
            </template>
          </Column>

          <!-- 실행 -->
          <Column
            field="실행"
            header="실행"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-3 py-2 text-center' },
              bodyCell: { class: 'px-3 py-2 text-center' }
            }"
          >
            <template #body="{ data }">
              <span :class="['inline-block min-w-6 text-center rounded px-1', data.isTotal ? 'font-bold text-gray-900' : data['실행'] > 0 ? 'text-amber-500' : 'text-gray-300']">
                {{ data['실행'] }}
              </span>
            </template>
          </Column>

          <!-- 해결 -->
          <Column
            field="해결"
            header="해결"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-3 py-2 text-center' },
              bodyCell: { class: 'px-3 py-2 text-center' }
            }"
          >
            <template #body="{ data }">
              <span :class="['inline-block min-w-6 text-center rounded px-1', data.isTotal ? 'font-bold text-gray-900' : data['해결'] > 0 ? 'text-violet-500' : 'text-gray-300']">
                {{ data['해결'] }}
              </span>
            </template>
          </Column>

          <!-- 반려 -->
          <Column
            field="반려"
            header="반려"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-3 py-2 text-center' },
              bodyCell: { class: 'px-3 py-2 text-center' }
            }"
          >
            <template #body="{ data }">
              <span :class="['inline-block min-w-6 text-center rounded px-1', data.isTotal ? 'font-bold text-gray-900' : data['반려'] > 0 ? 'text-red-500' : 'text-gray-300']">
                {{ data['반려'] }}
              </span>
            </template>
          </Column>

          <!-- 완료 -->
          <Column
            field="완료"
            header="완료"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-3 py-2 text-center' },
              bodyCell: { class: 'px-3 py-2 text-center' }
            }"
          >
            <template #body="{ data }">
              <span :class="['inline-block min-w-6 text-center rounded px-1', data.isTotal ? 'font-bold text-gray-900' : data['완료'] > 0 ? 'text-emerald-500' : 'text-gray-300']">
                {{ data['완료'] }}
              </span>
            </template>
          </Column>

          <!-- 합계 -->
          <Column
            field="합계"
            header="합계"
            :pt="{
              headerCell: { class: 'bg-slate-50 text-gray-600 text-base font-semibold px-3 py-2 text-center' },
              bodyCell: { class: 'px-3 py-2 text-center' }
            }"
          >
            <template #body="{ data }">
              <span :class="['inline-block min-w-6 text-center font-semibold', data.isTotal ? 'text-gray-900 font-bold' : 'text-gray-800']">
                {{ data['합계'] }}
              </span>
            </template>
          </Column>

          <!-- 데이터 없을 때 -->
          <template #empty>
            <div class="text-center py-6 text-gray-400 text-base">데이터가 없습니다.</div>
          </template>
        </DataTable>
      </div>
    </div>
  </div>
</template>
