<script setup>
import { computed, reactive, ref } from 'vue';

const loading = ref(false);

const filter = reactive({
  issueType: '',
  versionId: '',
  versionStatus: 'ALL',
  includeSubProject: true
});

const issueTypeOptions = [
  { label: '새기능', value: 'TYPE01' },
  { label: '버그', value: 'TYPE02' }
];

const versionStatusOptions = [
  { label: '전체', value: 'ALL' },
  { label: '진행중', value: 'N1' },
  { label: '닫힘', value: 'N0' }
];

// 버전 목록은 조회된 roadmapList에서 추출
const versionOptions = computed(() => {
  const seen = new Set();
  return roadmapList.value
    .filter((v) => {
      if (seen.has(v.versionId)) return false;
      seen.add(v.versionId);
      return true;
    })
    .map((v) => ({ label: v.versionName, value: v.versionId }));
});

// 목업 데이터
const roadmapList = ref([
  {
    versionId: 'VER001',
    versionName: 'Ver3.4',
    versionStatus: 'N1',
    endDate: '2026-03-18',
    description: '버전 설명이 들어가는 자리.',
    projectId: 'PROJ001',
    projectName: '테스트프로젝트',
    tasks: [
      { taskId: 'T001', taskNumber: 25, taskTitle: '일감명', taskType: '새기능', taskWorkflow: 'Q1', projectName: '테스트프로젝트' },
      { taskId: 'T002', taskNumber: 28, taskTitle: '일감명2', taskType: '새기능', taskWorkflow: 'Q2', projectName: '테스트프로젝트' },
      { taskId: 'T003', taskNumber: 12, taskTitle: '로그인 오류', taskType: '버그', taskWorkflow: 'Q3', projectName: '하위프로젝트A' }
    ]
  },
  {
    versionId: 'VER002',
    versionName: 'Ver1.1',
    versionStatus: 'N0',
    endDate: '2025-01-23',
    description: null,
    projectId: 'PROJ001',
    projectName: '테스트프로젝트',
    tasks: []
  }
]);

// 진척률 계산 (완료 Q3 / 전체)
const calcProgress = (tasks) => {
  if (!tasks || tasks.length === 0) return 0;
  const done = tasks.filter((t) => t.taskWorkflow === 'Q3').length;
  return Math.round((done / tasks.length) * 100);
};

// 지연일 계산
const calcDelay = (endDate) => {
  if (!endDate) return null;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const end = new Date(endDate);
  end.setHours(0, 0, 0, 0);
  const diff = Math.floor((today - end) / (1000 * 60 * 60 * 24));
  return diff > 0 ? diff : null;
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}/${String(d.getMonth() + 1).padStart(2, '0')}/${String(d.getDate()).padStart(2, '0')}`;
};

const workflowLabel = (workflow) => {
  const map = { Q0: '신규', Q1: '실행', Q2: '해결', Q3: '완료', Q4: '반려' };
  return map[workflow] || workflow;
};

const workflowSeverity = (workflow) => {
  const map = { Q0: 'secondary', Q1: 'info', Q2: 'success', Q3: 'contrast', Q4: 'danger' };
  return map[workflow] || 'secondary';
};

const resetFilters = () => {
  Object.assign(filter, { issueType: '', versionId: '', versionStatus: 'ALL', includeSubProject: true });
  // findRoadmapList();
};

// API 연동 시 사용
// const findRoadmapList = async () => {
//   loading.value = true;
//   try {
//     const res = await axios.get(`/api/project/${projectId}/roadmap`, { params: filter });
//     // 그룹핑 처리
//   } finally {
//     loading.value = false;
//   }
// };

// onMounted(() => {
//   findRoadmapList();
// });
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-6">로드맵</h1>

    <!-- 상단 버튼 -->
    <div class="flex justify-end gap-2 mb-4">
      <Button label="버전 추가" severity="secondary" raised />
      <Button label="버전 관리" :style="{ background: '#5B6E96', borderColor: '#5B6E96' }" />
    </div>

    <!-- 필터 -->
    <div class="bg-[#F2F3F8] px-6 py-4 rounded-lg mb-6 shadow-sm border border-[#ECEEF4] flex items-center flex-wrap gap-4">
      <div class="flex items-center gap-2">
        <label class="filter-label">일감유형</label>
        <Select v-model="filter.issueType" :options="issueTypeOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-36" />
      </div>

      <div class="flex items-center gap-2">
        <label class="filter-label">버전</label>
        <Select v-model="filter.versionId" :options="versionOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-36" />
      </div>

      <div class="flex items-center gap-2">
        <label class="filter-label">버전 상태</label>
        <Select v-model="filter.versionStatus" :options="versionStatusOptions" optionLabel="label" optionValue="value" class="filter-input w-36" />
      </div>

      <div class="flex items-center gap-2">
        <Checkbox v-model="filter.includeSubProject" :binary="true" inputId="subProject" />
        <label for="subProject" class="filter-label cursor-pointer">하위 프로젝트 보기</label>
      </div>

      <div class="ml-auto">
        <Button label="초기화" severity="secondary" raised @click="resetFilters" />
      </div>
    </div>

    <!-- 로드맵 목록 -->
    <div v-if="loading" class="flex justify-center py-20">
      <i class="pi pi-spin pi-spinner text-3xl text-[#5B6E96]"></i>
    </div>

    <div v-else class="flex flex-col gap-4">
      <div v-for="version in roadmapList" :key="version.versionId" class="bg-white rounded-xl border border-[#ECEEF4] shadow-sm overflow-hidden">
        <!-- 버전 헤더 -->
        <div class="bg-[#F2F3F8] px-5 py-3 flex items-center justify-between border-b border-[#ECEEF4]">
          <div class="flex items-center gap-3">
            <span class="text-base font-semibold text-[#1A1816]"> {{ version.projectName }} {{ version.versionName }} </span>
            <Tag :value="version.versionStatus === 'N1' ? '진행' : '닫힘'" :severity="version.versionStatus === 'N1' ? 'success' : 'secondary'" />
          </div>
          <Button label="편집" icon="pi pi-pencil" severity="secondary" size="small" text />
        </div>

        <!-- 버전 바디 -->
        <div class="px-5 py-4">
          <!-- 날짜 / 지연 -->
          <div class="text-sm text-[#6B6B63] mb-1">
            <span v-if="calcDelay(version.endDate)" class="text-[#D85A30] font-semibold mr-1"> {{ calcDelay(version.endDate) }}일 지연 </span>
            <span v-if="version.endDate">({{ formatDate(version.endDate) }})</span>
          </div>

          <!-- 설명 -->
          <div v-if="version.description" class="text-sm text-[#6B6B63] mb-3">
            {{ version.description }}
          </div>

          <!-- 일감 없음 -->
          <div v-if="version.tasks.length === 0" class="text-sm text-[#9A9B90] py-2">이 버전에 해당하는 일감 없음</div>

          <template v-else>
            <!-- 진척률 -->
            <div class="flex items-center gap-3 mb-1">
              <div class="flex-1 h-2 bg-[#ECEEF4] rounded-full overflow-hidden">
                <div class="h-full rounded-full bg-[#5B6E96]" :style="{ width: calcProgress(version.tasks) + '%' }" />
              </div>
              <span class="text-sm text-[#6B6B63] min-w-[36px] text-right"> {{ calcProgress(version.tasks) }}% </span>
            </div>
            <div class="text-xs text-[#9A9B90] mb-4">{{ version.tasks.length }} 일감 ({{ version.tasks.filter((t) => t.taskWorkflow === 'Q3').length }}건 완료 / {{ version.tasks.filter((t) => t.taskWorkflow !== 'Q3').length }}건 진행중)</div>

            <!-- 일감 목록 -->
            <div class="text-sm font-semibold text-[#1A1816] mb-2">연결된 일감</div>
            <div class="flex flex-col gap-2">
              <div v-for="task in version.tasks" :key="task.taskId" class="flex items-center justify-between px-4 py-2 bg-[#F2F3F8] rounded-lg border border-[#ECEEF4]">
                <div class="flex items-center gap-3">
                  <Tag :value="task.taskType" severity="info" />
                  <div>
                    <div class="text-sm text-[#1A1816]">{{ task.projectName }} - {{ task.taskType }} #{{ task.taskNumber }}: {{ task.taskTitle }}</div>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <Tag :value="workflowLabel(task.taskWorkflow)" :severity="workflowSeverity(task.taskWorkflow)" />
                  <Button icon="pi pi-ellipsis-h" severity="secondary" text size="small" />
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #000000c2;
  white-space: nowrap;
}
:deep(.filter-input) {
  height: 38px !important;
}
:deep(.filter-input .p-select-label) {
  display: flex;
  align-items: center;
}
</style>
