<script setup>
import SubTaskAlertModal from '@/components/task/SubTaskAlertModal.vue';
import TaskTimeModal from '@/components/task/TaskTimeModal.vue';
import TaskUnlinkModal from '@/components/task/TaskUnlinkModal.vue';
import { useAuthStore } from '@/stores/auth';
import { useGitStore } from '@/stores/gits';
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import { useToast } from 'primevue';
import Button from 'primevue/button';
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const projectStore = useProjectStore();
const taskStore = useTaskStore();
const authStore = useAuthStore();
const gitStore = useGitStore();
const router = useRouter();
const route = useRoute();
const timeLogVisible = ref(false);
const deleteModalVisible = ref(false);
const linkModalVisible = ref(false);
const toast = useToast();

// 연결 끊기 모달
const unlinkModalVisible = ref(false);
const selectedLinkedTask = ref(null);

const openUnlinkModal = (linked) => {
  selectedLinkedTask.value = linked;
  unlinkModalVisible.value = true;
};

// 기본 정보
const projectId = computed(() => projectStore.selectedProject?.id);
const userId = computed(() => authStore.user?.id ?? null);
const taskId = computed(() => route.params.taskId);
const task = computed(() => taskStore.task);

watch(
  () => route.params.taskId,
  async (taskId) => {
    await taskStore.findTaskDetail(taskId);
    if (task.value?.status == 'T0') {
      router.replace('/error');
    }
  },
  { immediate: true }
);

const subTasks = computed(() => task.value?.childTaskList ?? []);
const subTaskDone = computed(() => subTasks.value.filter((t) => t.progress === 100).length);
const subTaskInProgress = computed(() => subTasks.value.filter((t) => t.progress !== 100).length);
const linkedTasks = computed(() => task.value?.linkedTaskList ?? []);
const linkedTaskDone = computed(() => linkedTasks.value.filter((t) => t.progress === 100).length);
const linkedTaskInProgress = computed(() => linkedTasks.value.filter((t) => t.progress !== 100).length);
const fileList = computed(() => task.value?.fileList ?? []);
const spentTime = computed(() => (task.value?.timeList ?? []).reduce((sum, log) => sum + (log.spent ?? 0), 0));
const history = computed(() => task.value?.journalList ?? []);
const timeLogs = computed(() => task.value?.timeList ?? []);
const category = computed(() => taskStore.cateList);
const type = computed(() => taskStore.typeList);
const commonCodes = computed(() => taskStore.commonCodeList);
const commitList = computed(() => gitStore.commitList);
const priority = computed(() => commonCodes.value.filter((p) => p.cgroup === '0S'));
const workflow = computed(() => commonCodes.value.filter((w) => w.cgroup === '0Q'));
const relation = computed(() => commonCodes.value.filter((r) => r.cgroup === '0W'));
const activity = computed(() => commonCodes.value.filter((a) => a.cgroup === '0U'));

// value → name 맵핑
const workflowMap = computed(() => Object.fromEntries(workflow.value.map((w) => [w.id, w.name])));
const priorityMap = computed(() => Object.fromEntries(priority.value.map((p) => [p.id, p.name])));
const taskTypeMap = computed(() => Object.fromEntries(type.value.map((t) => [t.id, t.name])));
const taskCateMap = computed(() => Object.fromEntries(category.value.map((c) => [c.id, c.name])));
const relationMap = computed(() => Object.fromEntries(relation.value.map((r) => [r.id, r.name])));
const activityMap = computed(() => Object.fromEntries(activity.value.map((a) => [a.id, a.name])));

// 수정/소요시간 권한
const canEdit = computed(() => {
  const uid = userId.value;
  const role = authStore.user?.role;
  return task.value?.creator === uid || task.value?.userId === uid || role === 'ROLE0001' || role === 'ROLE0002';
});

// 댓글 수정 권한
const canEditComment = (comment) => {
  const role = authStore.user?.role;
  return comment.userId === userId.value || role === 'ROLE0001';
};

// 최근 수정 정보
const latestJournal = computed(() => {
  const list = task.value?.journalList;
  if (!list?.length) return null;
  return list.reduce((a, b) => (new Date(a.createdOn) > new Date(b.createdOn) ? a : b));
});

const modifiedBy = computed(() => latestJournal.value?.name ?? '-');

const modifiedTimeAgo = computed(() => {
  if (!latestJournal.value?.createdOn) return '-';
  const diff = Date.now() - new Date(latestJournal.value.createdOn).getTime();
  const minutes = Math.max(0, Math.floor(diff / 60_000));
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  const years = Math.floor(days / 365);

  if (years >= 1) return `${years}년`;
  if (days >= 1) return `${days}일`;
  if (hours >= 1) return `${hours}시간`;
  return `${minutes}분`;
});

const fieldMapper = {
  is_public: '비공개',
  title: '일감명',
  type: '일감유형',
  workflow: '진행상태',
  priority: '우선순위',
  description: '설명',
  parent_id: '상위 일감',
  category: '일감 범주',
  user_id: '담당자',
  version_id: '목표 버전',
  estimated_time: '추정 시간',
  progress: '진척도',
  planned_start: '예상 시작일',
  planned_end: '예상 마감일',
  actual_start: '실제 시작일',
  actual_end: '실제 마감일',
  attachments: '첨부파일',
  reject: '반려사유'
};
const DATE_FIELDS = new Set(['planned_start', 'planned_end', 'actual_start', 'actual_end']);
const COMMON_CODE_FIELDS = new Set(['priority', 'is_public', 'workflow']);

const resolveValue = (fieldName, value) => {
  if (!value) return '-';
  if (DATE_FIELDS.has(fieldName)) return formatDate(value);
  if (COMMON_CODE_FIELDS.has(fieldName)) return commonCodes.value.find((c) => c.id == value)?.name ?? value;
  if (fieldName === 'category') return category.value.find((c) => c.id == value)?.name ?? value;
  if (fieldName === 'type') return type.value.find((t) => t.id == value)?.name ?? value;
  return value;
};

// 날짜 포맷
const formatDate = (ts) => {
  if (!ts) return '-';
  return new Date(ts).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

const formatDateTime = (ts) => {
  if (!ts) return '-';
  return new Date(ts).toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 시간 포맷
const formatHours = (h) => {
  if (!h && h !== 0) return '0시간';

  const totalHours = Math.round(h); // 필요하면 floor로 변경 가능
  const days = Math.floor(totalHours / 8);
  const hours = totalHours % 8;

  const parts = [];
  if (days > 0) parts.push(`${days}일`);
  if (hours > 0) parts.push(`${hours}시간`);

  return parts.length ? parts.join(' ') : '0시간';
};

// 파일 크기 포맷
const formatFileSize = (bytes) => {
  if (!bytes) return 0;
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 ** 2) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / 1024 ** 2).toFixed(1)} MB`;
};

// 댓글 트리 구성
const buildCommentTree = (list) => {
  if (!list?.length) return [];
  const roots = list.filter((r) => !r.parentId);
  return roots.map((r) => ({
    ...r,
    editing: false,
    editContent: '',
    replies: list.filter((c) => c.parentId === r.id).map((c) => ({ ...c, editing: false, editContent: '' }))
  }));
};

const localComments = ref([]);
watch(
  () => task.value?.replyList,
  (list) => {
    localComments.value = buildCommentTree(list);
  },
  { immediate: true }
);

// 댓글 / 답글 CRUD
const activeTab = ref('comments');

const commentData = ref({
  taskId: taskId.value,
  userId: userId.value,
  content: ''
});

const replyData = ref({
  taskId: taskId.value,
  userId: userId.value,
  content: '',
  parentId: null
});

// 답글 UI 상태 (어느 댓글에 답글 입력창을 열었는지)
const replyingTo = ref(null);

const addComment = async () => {
  if (!commentData.value.content.trim()) return;
  if (commentData.value.content.length > 500) {
    return;
  }
  await taskStore.insertTaskReply(commentData.value);
  commentData.value.content = '';
  await taskStore.findTaskDetail(taskId.value);
  toast.add({
    severity: 'success',
    summary: '작성 완료',
    detail: '새 댓글을 작성하였습니다.',
    life: 3000,
    closable: false
  });
};

const addReply = async (comment) => {
  if (!replyData.value.content.trim()) return;
  if (replyData.value.content.length > 500) {
    return;
  }
  replyData.value.parentId = comment.id;
  await taskStore.insertTaskReply(replyData.value);
  replyData.value.content = '';
  replyData.value.parentId = null;
  replyingTo.value = null;
  await taskStore.findTaskDetail(taskId.value);
  toast.add({
    severity: 'success',
    summary: '작성 완료',
    detail: '새 답글을 작성하였습니다.',
    life: 3000,
    closable: false
  });
};

const startEdit = (item) => {
  item.editing = true;
  item.editContent = item.content;
};

const saveEdit = async (item) => {
  await taskStore.updateTaskReply({
    id: item.id,
    taskId: taskId.value,
    userId: item.userId,
    content: item.editContent
  });
  item.editing = false;
  await taskStore.findTaskDetail(taskId.value);
  toast.add({
    severity: 'success',
    summary: '수정 완료',
    detail: '댓글이 수정되었습니다.',
    life: 3000,
    closable: false
  });
};

const cancelEdit = (item) => {
  item.editing = false;
};

const deleteComment = async (id) => {
  await taskStore.deleteTaskReply(id);
  await taskStore.findTaskDetail(taskId.value);
  toast.add({
    severity: 'success',
    summary: '삭제 완료',
    detail: '댓글이 삭제되었습니다.',
    life: 3000,
    closable: false
  });
};

const workflowClass = {
  Q0: 'status-new',
  Q1: 'status-in-progress',
  Q2: 'status-resolved',
  Q3: 'status-done',
  Q4: 'status-rejected'
};

const progressColor = (p) => {
  if (p == 100) return '#22C55E';
  if (p >= 90) return '#86EFAC';
  if (p >= 60) return '#EAB308';
  if (p >= 30) return '#F97316';
  return '#EF4444';
};

const priorityClass = {
  S3: 'priority-urgent',
  S2: 'priority-high',
  S1: 'priority-mid',
  S0: 'priority-low'
};

const goToTask = (id) => router.push(`/task/${id}`);
const goToEdit = () => router.push('/task/edit');
const goToCopy = () => router.push('/task/copy');
const goToAddSubTask = () => {
  router.push({
    path: '/task/create',
    query: {
      parentId: taskId.value,
      parentName: task.value?.title
    }
  });
};

// 하위 일감 최초 생성 경고 모달
const subTaskWarningVisible = ref(false);

const displayProgress = computed(() => (subTasks.value.length > 0 ? (task.value?.subProgress ?? 0) : (task.value?.progress ?? 0)));

const handleAddSubTask = () => {
  if (subTasks.value.length === 0 && task.value?.progress > 0) {
    subTaskWarningVisible.value = true;
  } else {
    goToAddSubTask();
  }
};

// 파일 다운로드
const downloadFile = async (id) => {
  const data = await taskStore.downloadFile(id);
  const url = window.URL.createObjectURL(new Blob([data]));
  const link = document.createElement('a');
  link.href = url;

  const disposition = data.headers['content-disposition'];
  const fileName = decodeURIComponent(disposition.split('filename="')[1].replace('"', ''));

  link.setAttribute('download', fileName);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

const loading = ref(false);
onMounted(async () => {
  loading.value = true;
  await Promise.all([taskStore.findCateList(), taskStore.findTypeList(), gitStore.findCommitsByTaskId(taskId.value), taskStore.findCommonCodeList(), taskStore.findTaskList(projectId.value, userId.value)]);
  loading.value = false;
});
</script>

<template>
  <div v-if="loading" class="flex justify-center items-center py-20 text-stone-400">
    <i class="pi pi-spin pi-spinner text-2xl mr-2"></i>
    데이터 불러오는 중...
  </div>
  <div v-else class="min-h-screen bg-white p-8">
    <!-- 헤더 -->
    <div class="mb-6 flex items-start justify-between gap-4 flex-wrap">
      <div class="flex flex-col gap-2 min-w-0">
        <div class="flex items-center gap-3 flex-wrap">
          <span class="text-base font-mono font-semibold text-[#9A9B90] bg-white px-2.5 py-0.5 rounded border border-[#E5E4DF] shrink-0">{{ task.id }}</span>
          <span v-if="task.isPublic === 'J0'" class="text-base font-semibold px-2.5 py-0.5 rounded-full bg-red-50 text-red-500 border border-red-200 shrink-0">비공개</span>
          <span v-else class="text-base font-semibold px-2.5 py-0.5 rounded-full bg-green-50 text-green-500 border border-green-200 shrink-0">공개</span>
        </div>
        <h1 class="text-2xl font-bold text-[#1A1816] leading-snug">{{ task.title }}</h1>
      </div>

      <!-- 액션 버튼 -->
      <div class="flex items-center gap-2 shrink-0">
        <Button severity="secondary" label="목록으로" raised @click="router.push(`/tasks`)"></Button>
        <Button v-if="canEdit" label="수정" icon="pi pi-pen-to-square" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" raised @click="goToEdit" />
        <Button label="복사" icon="pi pi-clone" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" raised @click="goToCopy" />
        <Button v-if="canEdit" label="삭제" icon="pi pi-trash" severity="danger" raised @Click="deleteModalVisible = true" />
      </div>
    </div>

    <!-- 일감 정보 테이블 -->
    <div class="bg-white rounded-lg shadow-sm border divide-[#D6E4EA] mb-6">
      <table class="table-fixed w-full">
        <colgroup>
          <col class="w-36" />
          <col />
          <col class="w-36" />
          <col />
        </colgroup>
        <tbody class="divide-y divide-[#D6E4EA]">
          <!-- 범주 / 유형 / 수정시간 -->
          <tr>
            <td colspan="4" class="px-6 py-3">
              <div class="flex items-center gap-3 flex-wrap">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-[#FFF3E0] border border-[#E8920E]/30 text-base font-semibold text-[#E8920E]">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                  </svg>
                  {{ taskCateMap[task.category] }}
                </span>
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-[#F2F3F8] border divide-[#D6E4EA] text-base font-semibold text-[#3A3B35]">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  {{ taskTypeMap[task.type] }}
                </span>
                <span class="h-4 w-px bg-[#C7C7C2]"></span>
                <span class="text-base text-[#9A9B90]">
                  <span class="font-medium text-[#6B6B63]">{{ modifiedBy }}</span
                  >이(가) <span class="font-medium text-[#6B6B63]">{{ modifiedTimeAgo }}</span> 전에 수정함
                </span>
              </div>
            </td>
          </tr>

          <!-- 진행상태 / 우선순위 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">진행상태</td>
            <td class="px-6 py-3">
              <span class="inline-block text-base font-semibold px-2.5 py-0.5 rounded-full" :class="workflowClass[task.workflow]">{{ workflowMap[task.workflow] }}</span>
            </td>
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">우선순위</td>
            <td class="px-6 py-3">
              <span class="inline-block px-2.5 py-0.5 rounded-full text-base font-semibold" :class="priorityClass[task.priority] ?? 'bg-stone-100 text-stone-400'">
                {{ priorityMap[task.priority] }}
              </span>
            </td>
          </tr>

          <!-- 반려 사유 -->
          <tr v-if="task.workflow == 'Q4'" class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">반려 사유</td>
            <td colspan="3" class="px-6 py-3">
              <span class="inline-flex items-center gap-2">
                <span class="text-base text-[#1A1816] font-medium">{{ task.reject }}</span>
              </span>
            </td>
          </tr>

          <!-- 담당자 / 목표버전 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">담당자</td>
            <td class="px-6 py-3">
              <span class="inline-flex items-center gap-2">
                <span class="text-base text-[#1A1816] font-medium">{{ task.name }} ( {{ task.userId }} ) </span>
              </span>
            </td>
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">목표 버전</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ task.versionName || '-' }}</td>
          </tr>

          <!-- 진척도 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">진척도</td>
            <td colspan="3" class="px-6 py-3">
              <ProgressBar
                class="w-150"
                :value="Number(displayProgress)"
                :pt="{
                  value: {
                    style: {
                      background: progressColor(displayProgress)
                    }
                  }
                }"
              ></ProgressBar>
            </td>
          </tr>

          <!-- 예상 시작일 / 예상 마감일 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">예상 시작일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ formatDate(task.plannedStart) }}</td>
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">예상 마감일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ formatDate(task.plannedEnd) }}</td>
          </tr>

          <!-- 실제 시작일 / 실제 마감일 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">실제 시작일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ formatDate(task.actualStart) }}</td>
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">실제 마감일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ formatDate(task.actualEnd) }}</td>
          </tr>

          <!-- 추정 시간 / 작업 + 기록 -->
          <tr v-if="subTasks.length === 0" class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">추정 시간</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ formatHours(task.estimatedTime) }}</td>
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">작업</td>
            <td class="px-6 py-3">
              <div class="flex items-center gap-3">
                <span class="text-base text-[#1A1816]">{{ formatHours(spentTime) }}</span>
                <Button v-if="canEdit" label="작업 기록" icon="pi pi-plus" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" raised @click="timeLogVisible = true" />
              </div>
            </td>
          </tr>

          <!-- 상위일감 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">상위 일감</td>
            <td colspan="3" class="px-6 py-3">
              <span v-if="task.parentId" class="inline-flex items-center gap-1.5 text-base text-[#E8920E] font-medium cursor-pointer hover:underline" @click="goToTask(task.parentId)"> {{ task.parentName }} ( {{ task.parentId }} ) </span>
              <span v-else class="text-base text-[#9A9B90]">-</span>
            </td>
          </tr>

          <!-- 설명 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">설명</td>
            <td colspan="3" class="px-6 py-3">
              <span class="flex items-center gap-2">
                <span class="text-base text-[#1A1816] font-medium text-wrap break-words min-w-0">{{ task.description }}</span>
              </span>
            </td>
          </tr>

          <!-- 첨부파일 -->
          <tr class="divide-x divide-[#D6E4EA]">
            <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">첨부파일</td>
            <td colspan="3" class="px-6 py-3">
              <div v-if="fileList.length" class="flex flex-wrap gap-2">
                <a
                  v-for="file in fileList"
                  :key="file.id"
                  @click="downloadFile(file.id)"
                  target="_blank"
                  class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border divide-[#D6E4EA] text-[#3A3B35] bg-[#FAFAF8] hover:bg-white transition-colors"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-[#9A9B90]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                  </svg>
                  <span class="font-medium">{{ file.name }}</span>
                  <span class="text-[#9A9B90]">({{ formatFileSize(file.size) }})</span>
                </a>
              </div>
              <span v-else class="text-base text-[#9A9B90]">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 하위 일감 -->
    <div class="bg-white rounded-lg shadow-sm border divide-[#D6E4EA] mb-6">
      <div class="px-6 py-3 border-b divide-[#D6E4EA] bg-white rounded-t-lg flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="text-base font-bold text-[#1A1816]">하위 일감</span>
          <span class="text-base font-semibold px-2 py-0.5 rounded-full bg-[#E8920E] text-white">{{ subTasks.length }}</span>
          <span class="text-base text-[#6B6B63]">
            완료 <span class="font-semibold text-emerald-600">{{ subTaskDone }}</span> · 진행중 <span class="font-semibold text-amber-600">{{ subTaskInProgress }}</span>
          </span>
        </div>
        <Button v-if="canEdit" label="하위 일감 추가" icon="pi pi-plus" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" raised @click="handleAddSubTask" />
      </div>

      <table class="w-full text-base">
        <thead>
          <tr class="border-b border-[#F2F0EB] bg-[#5B6E96]">
            <th class="px-6 py-2.5 text-left font-semibold text-[#DDE3F0] w-28">번호</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#DDE3F0]">이름</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-60">진척도</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-28">상태</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-36">마감일</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#D6E4EA]">
          <tr v-for="sub in subTasks" :key="sub.id" class="hover:bg-[#FAFAF8] cursor-pointer transition-colors" @click="goToTask(sub.id)">
            <td class="px-6 py-3 font-mono text-base text-[#9A9B90]">{{ sub.id }}</td>
            <td class="px-6 py-3 text-[#1A1816] font-medium hover:text-[#E8920E] transition-colors">{{ sub.title }}</td>
            <td class="px-6 py-3 text-center">
              <ProgressBar
                :value="Number(sub.progress)"
                :pt="{
                  value: {
                    style: {
                      background: progressColor(sub.progress)
                    }
                  }
                }"
              ></ProgressBar>
            </td>
            <td class="px-6 py-3 text-center">
              <span class="text-base font-semibold px-2 py-0.5 rounded-full" :class="workflowClass[sub.workflow]">{{ workflowMap[sub.workflow] }}</span>
            </td>
            <td class="px-6 py-3 text-[#6B6B63] text-center">{{ formatDate(sub.plannedEnd) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 연결된 일감 -->
    <div class="bg-white rounded-lg shadow-sm border divide-[#D6E4EA] mb-6">
      <div class="px-6 py-3 border-b divide-[#D6E4EA] bg-white rounded-t-lg flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="text-base font-bold text-[#1A1816]">연결된 일감</span>
          <span class="text-base font-semibold px-2 py-0.5 rounded-full bg-[#6B6B63] text-white">{{ linkedTasks.length }}</span>
          <span class="text-base text-[#6B6B63]">
            완료 <span class="font-semibold text-emerald-600">{{ linkedTaskDone }}</span> · 진행중 <span class="font-semibold text-amber-600">{{ linkedTaskInProgress }}</span>
          </span>
        </div>
        <Button v-if="canEdit" label="연결된 일감 추가" icon="pi pi-plus" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" raised @click="linkModalVisible = true" />
      </div>

      <table class="w-full text-base">
        <thead>
          <tr class="border-b border-[#F2F0EB] bg-[#5B6E96]">
            <th class="px-6 py-2.5 text-left font-semibold text-[#DDE3F0] w-28">번호</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#DDE3F0]">이름</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-28">관계</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-60">진척도</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-28">상태</th>
            <th class="px-6 py-2.5 text-center font-semibold text-[#DDE3F0] w-36">마감일</th>
            <th class="px-6 py-2.5 w-12"></th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#D6E4EA]">
          <tr v-for="linked in linkedTasks" :key="linked.id" class="hover:bg-[#FAFAF8] transition-colors">
            <td class="px-6 py-3 font-mono text-base text-[#9A9B90] cursor-pointer" @click="goToTask(linked.id)">{{ linked.id }}</td>
            <td class="px-6 py-3 text-[#1A1816] font-medium hover:text-[#E8920E] transition-colors cursor-pointer" @click="goToTask(linked.id)">{{ linked.title }}</td>
            <td class="px-6 py-3 text-center cursor-pointer" @click="goToTask(linked.id)">
              <span v-if="linked.relationType" class="px-2 py-0.5 bg-white rounded text-base font-semibold text-[#3A3B35] border border-[#E5E4DF]">{{ relationMap[linked.relationType] }}</span>
              <span v-else class="text-[#9A9B90]">-</span>
            </td>
            <td class="px-6 py-3 text-center cursor-pointer" @click="goToTask(linked.id)">
              <ProgressBar
                :value="Number(linked.progress)"
                :pt="{
                  value: {
                    style: {
                      background: progressColor(linked.progress)
                    }
                  }
                }"
              ></ProgressBar>
            </td>
            <td class="px-6 py-3 text-center cursor-pointer" @click="goToTask(linked.id)">
              <span class="text-base font-semibold px-2 py-0.5 rounded-full" :class="workflowClass[linked.workflow]">{{ workflowMap[linked.workflow] }}</span>
            </td>
            <td class="px-6 py-3 text-[#6B6B63] text-center cursor-pointer" @click="goToTask(linked.id)">{{ formatDate(linked.plannedEnd) || '-' }}</td>
            <!-- 연결 끊기 버튼 -->
            <td class="px-3 py-3 text-center">
              <button class="flex items-center justify-center w-7 h-7 rounded-md text-[#9A9B90] hover:text-red-500 hover:bg-red-50 transition-colors" title="연결 끊기" @click.stop="openUnlinkModal(linked)">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!--  보조 구역  -->
    <div class="bg-white rounded-lg shadow-sm border divide-[#D6E4EA]">
      <!-- 탭 헤더 -->
      <div class="flex border-b divide-[#D6E4EA] bg-white rounded-t-lg overflow-hidden">
        <button
          v-for="tab in [{ key: 'comments', label: '댓글' }, { key: 'history', label: '수정 이력' }, ...(subTasks.length === 0 ? [{ key: 'timelog', label: '작업 이력' }] : []), { key: 'commits', label: '커밋 내역' }]"
          :key="tab.key"
          class="px-6 py-3 text-base font-semibold transition-colors relative"
          :class="activeTab === tab.key ? 'text-[#E8920E] bg-white border-b-2 border-[#E8920E]' : 'text-[#6B6B63] hover:text-[#3A3B35] hover:bg-[#F2F3F8]'"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!--  댓글 탭  -->
      <div v-if="activeTab === 'comments'" class="p-6">
        <!-- 댓글 입력 -->
        <div class="mb-7 pb-6 border-b border-[#F2F0EB]">
          <div class="mb-2 flex items-baseline gap-1.5">
            <span class="text-base font-semibold text-[#1A1816]">{{ authStore.user?.name }}</span>
            <span class="text-base text-[#9A9B90]">({{ userId }})</span>
          </div>
          <textarea
            v-model="commentData.content"
            placeholder="댓글을 입력해주세요..."
            rows="3"
            class="w-full px-4 py-2.5 bg-[#FAFAF8] border divide-[#D6E4EA] rounded-lg text-base text-[#1A1816] outline-none focus:border-[#E8920E] focus:ring-2 focus:ring-[#E8920E]/15 transition-all resize-none placeholder-[#9A9B90]"
            :class="{ '!border-red-400 focus:!border-red-400 focus:!ring-red-400/15': commentData.content.length > 500 }"
          ></textarea>
          <div class="flex items-center justify-between mt-2">
            <small v-if="commentData.content.length > 500" class="text-red-500 text-xs">댓글은 500자를 초과할 수 없습니다.</small>
            <small class="ml-auto text-xs" :class="commentData.content.length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'">{{ commentData.content.length }} / 500</small>
          </div>
          <div class="flex justify-end mt-1">
            <Button label="댓글 작성" raised @click="addComment" />
          </div>
        </div>

        <!-- 댓글 목록 -->
        <div class="space-y-6">
          <div v-for="comment in localComments" :key="comment.id" class="pb-6 border-b border-[#F2F0EB] last:border-none last:pb-0">
            <!-- 작성자 정보 -->
            <div class="flex items-baseline gap-1.5 mb-2">
              <span class="text-base font-semibold text-[#1A1816]">{{ comment.name }}</span>
              <span class="text-base text-[#9A9B90]">({{ comment.userId }})</span>
              <span class="text-base text-[#C7C7C2] mx-0.5">·</span>
              <span class="text-base text-[#9A9B90]">{{ formatDateTime(comment.createdOn) }}</span>
              <span v-if="comment.editedOn" class="text-base text-[#C7C7C2]">(수정됨)</span>
            </div>

            <!-- 본문 -->
            <div v-if="!comment.editing" class="text-base text-[#3A3B35] bg-[#FAFAF8] border border-[#F2F0EB] rounded-lg px-4 py-3 leading-relaxed">{{ comment.content }}</div>
            <div v-else>
              <textarea
                v-model="comment.editContent"
                rows="2"
                class="w-full px-3 py-2 text-base border divide-[#D6E4EA] rounded-lg outline-none focus:border-[#E8920E] resize-none bg-[#FAFAF8]"
                :class="{ '!border-red-400 focus:!border-red-400': comment.editContent.length > 500 }"
              ></textarea>
              <div class="flex items-center justify-between mt-1">
                <small v-if="comment.editContent.length > 500" class="text-red-500 text-xs">댓글은 500자를 초과할 수 없습니다.</small>
                <small class="ml-auto text-xs" :class="comment.editContent.length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'">{{ comment.editContent.length }} / 500</small>
              </div>
              <div class="flex gap-2 mt-1 justify-end">
                <Button label="저장" raised @click="saveEdit(comment)" />
                <Button label="취소" severity="secondary" raised @click="cancelEdit(comment)" />
              </div>
            </div>

            <!-- 액션 -->
            <div class="flex items-center gap-2 mt-2">
              <Button label="답글" text size="small" @click="replyingTo = replyingTo === comment.id ? null : comment.id" />
              <template v-if="canEditComment(comment)">
                <Button label="수정" text size="small" @click="startEdit(comment)" />
                <Button label="삭제" text size="small" severity="danger" @click="deleteComment(comment.id)" />
              </template>
            </div>

            <!-- 답글 입력 -->
            <div v-if="replyingTo === comment.id" class="mt-4 ml-6 pl-4 border-l-2 border-[#F2F0EB]">
              <div class="flex items-baseline gap-1.5 mb-2">
                <span class="text-base font-semibold text-[#1A1816]">나</span>
                <span class="text-base text-[#9A9B90]">({{ userId }})</span>
              </div>
              <textarea
                v-model="replyData.content"
                placeholder="답글을 입력해주세요..."
                rows="2"
                class="w-full px-3 py-2 text-base border divide-[#D6E4EA] rounded-lg outline-none focus:border-[#E8920E] resize-none bg-[#FAFAF8]"
                :class="{ '!border-red-400 focus:!border-red-400': replyData.content.length > 500 }"
              ></textarea>
              <div class="flex items-center justify-between mt-1">
                <small v-if="replyData.content.length > 500" class="text-red-500 text-xs">답글은 500자를 초과할 수 없습니다.</small>
                <small class="ml-auto text-xs" :class="replyData.content.length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'">{{ replyData.content.length }} / 500</small>
              </div>
              <div class="flex gap-2 mt-1 justify-end">
                <Button label="답글 작성" raised @click="addReply(comment)" />
                <Button
                  label="취소"
                  severity="secondary"
                  raised
                  @click="
                    replyingTo = null;
                    replyData.content = '';
                  "
                />
              </div>
            </div>

            <!-- 대댓글 목록 -->
            <div v-if="comment.replies?.length > 0" class="mt-4 ml-6 pl-4 border-l-2 border-[#F2F0EB] space-y-4">
              <div v-for="reply in comment.replies" :key="reply.id">
                <div class="flex items-baseline gap-1.5 mb-2">
                  <span class="text-base font-semibold text-[#1A1816]">{{ reply.name }}</span>
                  <span class="text-base text-[#9A9B90]">({{ reply.userId }})</span>
                  <span class="text-base text-[#C7C7C2] mx-0.5">·</span>
                  <span class="text-base text-[#9A9B90]">{{ formatDateTime(reply.createdOn) }}</span>
                  <span v-if="reply.editedOn" class="text-base text-[#C7C7C2]">(수정됨)</span>
                </div>
                <div v-if="!reply.editing" class="text-base text-[#3A3B35] bg-[#F2F3F8] border border-[#F2F0EB] rounded-lg px-4 py-3 leading-relaxed">{{ reply.content }}</div>
                <div v-else>
                  <textarea v-model="reply.editContent" rows="2" class="w-full px-3 py-2 text-base border divide-[#D6E4EA] rounded-lg outline-none focus:border-[#E8920E] resize-none bg-[#FAFAF8]"></textarea>
                  <div class="flex gap-2 mt-2 justify-end">
                    <Button label="저장" raised @click="saveEdit(reply)" />
                    <Button label="취소" severity="secondary" raised @click="cancelEdit(reply)" />
                  </div>
                </div>
                <div class="flex items-center gap-2 mt-2">
                  <template v-if="canEditComment(reply)">
                    <Button label="수정" text raised size="small" @click="startEdit(reply)" />
                    <Button label="삭제" text raised size="small" severity="danger" @click="deleteComment(reply.id)" />
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 수정 이력 -->
      <div v-if="activeTab === 'history'" class="p-6">
        <DataTable :value="history" paginator :rows="10" paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown" stripedRows class="text-base" table-style="table-layout: fixed; width: 100%">
          <Column field="userId" header="수정자" :style="{ width: '160px' }">
            <template #body="{ data }">
              <span class="font-semibold text-[#1A1816]">{{ data.name }}</span>
              <span class="text-[#9A9B90] ml-1">({{ data.userId }})</span>
            </template>
          </Column>
          <Column field="fieldName" header="수정 항목" :style="{ width: '140px' }">
            <template #body="{ data }">
              <span class="font-semibold text-[#3A3B35]">{{ fieldMapper[data.fieldName] ?? data.fieldName }}</span>
            </template>
          </Column>
          <Column field="oldValue" header="수정 전" :style="{ width: '200px' }">
            <template #body="{ data }">
              <span class="block text-[#9A9B90] truncate line-through">{{ resolveValue(data.fieldName, data.oldValue) }}</span>
            </template>
          </Column>
          <Column field="newValue" header="수정 후" :style="{ width: '200px' }">
            <template #body="{ data }">
              <span class="block font-medium truncate text-[#1A1816]">{{ resolveValue(data.fieldName, data.newValue) }}</span>
            </template>
          </Column>
          <Column field="createdOn" header="수정일" :style="{ width: '160px' }">
            <template #body="{ data }">
              <span class="text-[#6B6B63]">{{ formatDateTime(data.createdOn) }}</span>
            </template>
          </Column>
        </DataTable>
      </div>

      <!--  작업 탭  -->
      <div v-if="activeTab === 'timelog'" class="p-6">
        <div class="flex justify-between items-center mb-4">
          <div class="flex items-center gap-4">
            <div class="text-center">
              <div class="text-lg font-bold text-[#E8920E]">{{ formatHours(spentTime) }}</div>
              <div class="text-base text-[#9A9B90]">총 작업</div>
            </div>
            <div class="h-8 w-px bg-white"></div>
            <div class="text-center">
              <div class="text-lg font-bold text-[#3A3B35]">{{ formatHours(task.estimatedTime) }}</div>
              <div class="text-base text-[#9A9B90]">추정 시간</div>
            </div>
          </div>
          <Button v-if="canEdit && subTasks.length == 0" label="작업 기록" icon="pi pi-plus" class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]" raised @click="timeLogVisible = true" />
        </div>
        <DataTable :value="timeLogs" stripedRows class="text-base" table-style="table-layout: fixed; width: 100%">
          <Column field="name" header="작업자" :style="{ width: '100px' }">
            <template #body="{ data }">
              <span class="font-medium text-[#1A1816] block">{{ data.name || '-' }}</span>
            </template>
          </Column>
          <Column field="taskStart" header="작업 일시" :style="{ width: '180px' }">
            <template #body="{ data }">
              <span class="text-[#6B6B63]">{{ formatDateTime(data.taskStart) }}</span>
            </template>
          </Column>
          <Column field="spent" header="작업" :style="{ width: '100px' }">
            <template #body="{ data }">
              <span class="font-semibold text-[#E8920E] block">{{ formatHours(data.spent) }}</span>
            </template>
          </Column>
          <Column field="description" header="설명">
            <template #body="{ data }">
              <p class="truncate" :title="data.description">{{ data.description || '-' }}</p>
            </template>
          </Column>
          <Column field="progress" header="진척도" :style="{ width: '160px' }">
            <template #body="{ data }">
              <ProgressBar :value="Number(data.progress)" :pt="{ value: { style: { background: progressColor(data.progress) } } }" />
            </template>
          </Column>
          <Column field="activityType" header="작업 종류" :style="{ width: '120px' }">
            <template #body="{ data }">
              <span class="px-2 py-0.5 bg-white rounded text-base font-semibold text-[#3A3B35] border border-[#E5E4DF]">
                {{ activityMap[data.activityType] || '-' }}
              </span>
            </template>
          </Column>
        </DataTable>
      </div>

      <!-- 커밋 로그 -->
      <div v-if="activeTab === 'commits'" class="p-6">
        <DataTable
          :value="commitList"
          paginator
          :rows="5"
          :rowsPerPageOptions="[5, 10, 20]"
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          stripedRows
          class="text-base"
          table-style="table-layout: fixed; width: 100%"
        >
          <template #empty>
            <div class="flex flex-col items-center justify-center py-10">
              <i class="pi pi-github text-4xl text-[#C7C7C2] mb-3"></i>
              <p class="text-[#6B6B63]">이 일감과 관련된 커밋 이력이 없습니다.</p>
            </div>
          </template>

          <Column field="repoName" header="저장소" :style="{ width: '120px' }">
            <template #body="{ data }">
              <span class="text-[#1A1816]">{{ data.repoName }}</span>
            </template>
          </Column>
          <Column field="commitSha" header="커밋 ID" :style="{ width: '100px' }">
            <template #body="{ data }">
              <code class="text-sm bg-white px-1.5 py-0.5 rounded text-[#E8920E] font-mono">
                {{ data.commitSha.substring(0, 7) }}
              </code>
            </template>
          </Column>
          <Column field="message" header="커밋 메시지" :style="{ width: '250px' }">
            <template #body="{ data }">
              <span class="text-[#3A3B35]">{{ data.message }}</span>
            </template>
          </Column>
          <Column field="author" header="작성자" :style="{ width: '200px' }">
            <template #body="{ data }">
              <span class="text-[#3A3B35]">{{ data.author }}</span>
            </template>
          </Column>
          <Column field="commitDate" header="커밋 날짜" :style="{ width: '200px' }">
            <template #body="{ data }">
              <span class="text-[#3A3B35]">{{ data.commitDate }}</span>
            </template>
          </Column>
        </DataTable>
      </div>
    </div>

    <!-- 모달 모음 -->
    <TaskTimeModal
      v-model:visible="timeLogVisible"
      :task="task"
      :activityList="activity"
      @confirm="
        async (data) => {
          await taskStore.insertTaskTime(data);
          timeLogVisible = false;
          await taskStore.findTaskDetail(taskId);
          toast.add({
            severity: 'success',
            summary: '작업 기록',
            detail: '소요시간 및 진척도가 반영되었습니다.',
            life: 3000,
            closable: false
          });
        }
      "
      @cancel="timeLogVisible = false"
    />
    <TaskLinkModal
      v-model:visible="linkModalVisible"
      :taskId="taskId"
      :relation="linkedTasks"
      @confirm="
        async (data) => {
          await taskStore.insertTaskLink(data);
          linkModalVisible = false;
          await taskStore.findTaskDetail(taskId);
          toast.add({
            severity: 'success',
            summary: '일감 연결',
            detail: '일감을 연결하였습니다.',
            life: 3000,
            closable: false
          });
        }
      "
      @cancel="linkModalVisible = false"
    />
    <DeleteModal
      v-model:visible="deleteModalVisible"
      :taskId="taskId"
      :title="task.title"
      @confirm="
        async (data) => {
          router.push(`/tasks`);
          await taskStore.deleteTask(data);
          await taskStore.findTaskList(projectId, userId);
          deleteModalVisible = false;
          toast.add({
            severity: 'success',
            summary: '일감 삭제',
            detail: '일감이 삭제되었습니다.',
            life: 3000,
            closable: false
          });
        }
      "
      @cancel="deleteModalVisible = false"
    />
    <SubTaskAlertModal
      v-model:visible="subTaskWarningVisible"
      @confirm="
        () => {
          subTaskWarningVisible = false;
          goToAddSubTask();
        }
      "
      @cancel="subTaskWarningVisible = false"
    />
    <TaskUnlinkModal
      v-model:visible="unlinkModalVisible"
      :currentTask="task"
      :linkedTask="selectedLinkedTask"
      :relationLabel="selectedLinkedTask?.relationType ? relationMap[selectedLinkedTask.relationType] : '-'"
      @confirm="
        async (data) => {
          await taskStore.deleteTaskLink(data);
          unlinkModalVisible = false;
          await taskStore.findTaskDetail(taskId);
          toast.add({
            severity: 'success',
            summary: '연결 해제',
            detail: '일감의 연결이 해제되었습니다.',
            life: 3000,
            closable: false
          });
        }
      "
      @cancel="unlinkModalVisible = false"
    />
  </div>
</template>

<style>
/*  우선순위  */
.priority-low {
  background-color: #eaf3de;
  color: #3b6d11;
}
.priority-mid {
  background-color: #e6f1fb;
  color: #0c447c;
}
.priority-high {
  background-color: #faeeda;
  color: #633806;
}
.priority-urgent {
  background-color: #fcebeb;
  color: #791f1f;
}

/*  상태  */
.status-new {
  background-color: #f1efe8;
  color: #444441;
}
.status-in-progress {
  background-color: #eeedfe;
  color: #3c3489;
}
.status-resolved {
  background-color: #e1f5ee;
  color: #085041;
}
.status-rejected {
  background-color: #faece7;
  color: #712b13;
}
.status-done {
  background-color: #eaf3de;
  color: #27500a;
}
</style>
