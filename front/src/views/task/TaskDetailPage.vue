<script setup>
import { useAuthStore } from '@/stores/auth';
import { useTaskStore } from '@/stores/task';
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const taskStore = useTaskStore();
const authStore = useAuthStore();

// ── Mock Data ──────────────────────────────────────────────────────────────
const currentUserId = '20261111';

const task = reactive({
  id: 'TASK-1042',
  title: '로그인 페이지 UI 개선 및 접근성 강화',
  isPublic: 'J0',
  status: 'active',
  workflow: '진행중',
  workflowColor: '#E8920E',
  priority: '높음',
  priorityColor: '#DC2626',
  category: '프론트엔드',
  type: '기능 개선',
  userId: '김민준',
  versionId: 'v2.1.0',
  progress: 65,
  plannedStart: '2026-03-15',
  plannedEnd: '2026-04-10',
  actualStart: '2026-03-17',
  actualEnd: '',
  estimatedTime: 480,
  spentTime: 312,
  description: '로그인 페이지의 UI를 전면 개편하고, WCAG 2.1 AA 기준에 맞게 접근성을 강화합니다.',
  modifiedBy: '이수연',
  modifiedMinutesAgo: 23,
  watchers: [
    { id: '20261111', name: '나' },
    { id: '20261112', name: '박지훈' }
  ]
});

const isWatcher = computed(() => task.watchers.some((w) => w.id === currentUserId));

// ── Sub Tasks ──────────────────────────────────────────────────────────────
const subTasks = ref([
  { id: 'TASK-1043', title: '로그인 폼 컴포넌트 리팩토링', workflow: '완료', plannedEnd: '2026-03-25', progress: 100 },
  { id: 'TASK-1044', title: '소셜 로그인 버튼 UI 추가', workflow: '진행중', plannedEnd: '2026-04-01', progress: 50 },
  { id: 'TASK-1045', title: '비밀번호 강도 표시기 구현', workflow: '진행중', plannedEnd: '2026-04-05', progress: 30 },
  { id: 'TASK-1046', title: '접근성 감사 및 ARIA 레이블 추가', workflow: '시작전', plannedEnd: '2026-04-10', progress: 0 }
]);

const subTaskDone = computed(() => subTasks.value.filter((t) => t.workflow === '완료').length);
const subTaskInProgress = computed(() => subTasks.value.filter((t) => t.workflow === '진행중').length);

// ── Linked Tasks ───────────────────────────────────────────────────────────
const linkedTasks = ref([
  { id: 'TASK-1030', title: '인증 서비스 API 리팩토링', workflow: '완료', plannedEnd: '2026-03-10', progress: 100 },
  { id: 'TASK-1038', title: 'JWT 토큰 갱신 로직 개선', workflow: '진행중', plannedEnd: '2026-04-15', progress: 70 }
]);

const linkedTaskDone = computed(() => linkedTasks.value.filter((t) => t.workflow === '완료').length);
const linkedTaskInProgress = computed(() => linkedTasks.value.filter((t) => t.workflow === '진행중').length);

// ── Tabs ───────────────────────────────────────────────────────────────────
const activeTab = ref('comments');

// ── Comments ───────────────────────────────────────────────────────────────
const comments = ref([
  {
    id: 1,
    author: '김민준',
    authorId: 'u1',
    time: '2시간 전',
    content: '로그인 폼 컴포넌트 리팩토링 완료했습니다. 리뷰 부탁드립니다.',
    editing: false,
    editContent: '',
    replies: [{ id: 101, author: '이수연', authorId: 'u2', time: '1시간 전', content: '확인했습니다! 몇 가지 피드백 남겼어요.', editing: false, editContent: '' }]
  },
  {
    id: 2,
    author: '박지훈',
    authorId: 'u3',
    time: '45분 전',
    content: '접근성 감사 기준 문서 공유드립니다. WCAG 2.1 체크리스트 참고해 주세요.',
    editing: false,
    editContent: '',
    replies: []
  }
]);

const newComment = ref('');
const replyingTo = ref(null);
const replyContent = ref('');

const addComment = () => {
  if (!newComment.value.trim()) return;
  comments.value.push({
    id: Date.now(),
    author: '나',
    authorId: currentUserId,
    time: '방금 전',
    content: newComment.value,
    editing: false,
    editContent: '',
    replies: []
  });
  newComment.value = '';
};

const addReply = (comment) => {
  if (!replyContent.value.trim()) return;
  comment.replies.push({
    id: Date.now(),
    author: '나',
    authorId: currentUserId,
    time: '방금 전',
    content: replyContent.value,
    editing: false,
    editContent: ''
  });
  replyContent.value = '';
  replyingTo.value = null;
};

const startEdit = (item) => {
  item.editing = true;
  item.editContent = item.content;
};
const saveEdit = (item) => {
  item.content = item.editContent;
  item.editing = false;
};
const cancelEdit = (item) => {
  item.editing = false;
};
const deleteComment = (list, id) => {
  const idx = list.findIndex((c) => c.id === id);
  if (idx > -1) list.splice(idx, 1);
};

// ── History ────────────────────────────────────────────────────────────────
const history = ref([
  { id: 1, modifier: '이수연', modifierId: 'u2', time: '2026-03-30 14:23', field: '진행상태', before: '시작전', after: '진행중' },
  { id: 2, modifier: '김민준', modifierId: 'u1', time: '2026-03-28 10:05', field: '진척도', before: '30%', after: '65%' },
  { id: 3, modifier: '박지훈', modifierId: 'u3', time: '2026-03-25 16:40', field: '담당자', before: '이수연', after: '김민준' },
  { id: 4, modifier: '이수연', modifierId: 'u2', time: '2026-03-22 09:00', field: '예상 마감일', before: '2026-04-05', after: '2026-04-10' }
]);

// ── Time Logs ──────────────────────────────────────────────────────────────
const timeLogs = ref([
  { id: 1, date: '2026-03-30', spent: 120, description: '로그인 폼 리팩토링', progress: 65, type: '개발' },
  { id: 2, date: '2026-03-29', spent: 90, description: '소셜 로그인 버튼 UI 작업', progress: 50, type: '개발' },
  { id: 3, date: '2026-03-27', spent: 60, description: '요구사항 분석 및 설계', progress: 20, type: '분석' },
  { id: 4, date: '2026-03-26', spent: 42, description: '코드 리뷰 및 피드백 반영', progress: 15, type: '검토' }
]);

// ── Helpers ────────────────────────────────────────────────────────────────
const workflowBadge = (wf) => {
  const map = {
    완료: 'bg-emerald-100 text-emerald-700 border border-emerald-200',
    진행중: 'bg-amber-100 text-amber-700 border border-amber-200',
    시작전: 'bg-stone-100 text-stone-500 border border-stone-200',
    중단: 'bg-red-100 text-red-600 border border-red-200'
  };
  return map[wf] ?? 'bg-stone-100 text-stone-500 border border-stone-200';
};

const progressColor = (p) => {
  if (p === 100) return 'bg-emerald-500';
  if (p >= 60) return 'bg-amber-500';
  if (p >= 30) return 'bg-orange-400';
  return 'bg-stone-300';
};

const formatMinutes = (m) => {
  if (!m) return '-';
  const h = Math.floor(m / 60);
  const min = m % 60;
  return h > 0 ? `${h}시간 ${min > 0 ? min + '분' : ''}` : `${min}분`;
};

const goToTask = (id) => router.push(`/tasks/${id}`);
const goToEdit = () => router.push('/task/edit');
const goToCopy = () => router.push('/task/copy');

// 유틸
function progressBarColor(p) {
  if (p == 100) return '#22C55E';
  if (p >= 90) return '#86EFAC';
  if (p >= 60) return '#EAB308';
  if (p >= 30) return '#F97316';
  return '#EF4444';
}

const workflowClass = {
  Q0: 'status-new',
  Q1: 'status-in-progress',
  Q2: 'status-resolved',
  Q3: 'status-rejected',
  Q4: 'status-done'
};

const priorityClass = {
  S3: 'priority-urgent',
  S2: 'priority-high',
  S1: 'priority-mid',
  S0: 'priority-low'
};
</script>

<template>
  <div class="min-h-screen bg-[#FAFAF8] p-8">
    <!-- 헤더 -->
    <div class="mb-6 flex items-start justify-between gap-4 flex-wrap">
      <div class="flex flex-col gap-2 min-w-0">
        <div class="flex items-center gap-3 flex-wrap">
          <span class="text-base font-mono font-semibold text-[#9A9B90] bg-[#F2F0EB] px-2.5 py-0.5 rounded border border-[#E5E4DF] shrink-0">{{ task.id }}</span>
          <span class="text-base font-semibold px-2.5 py-0.5 rounded-full shrink-0" :class="task.status === 'active' ? 'bg-emerald-100 text-emerald-700 border border-emerald-200' : 'bg-stone-100 text-stone-500 border border-stone-200'">{{
            task.status === 'active' ? '활성화' : '비활성화'
          }}</span>
          <span v-if="task.isPublic === 'J0'" class="text-base font-semibold px-2.5 py-0.5 rounded-full bg-red-50 text-red-500 border border-red-200 shrink-0">🔒 비공개</span>
        </div>
        <h1 class="text-2xl font-bold text-[#1A1816] leading-snug">{{ task.title }}</h1>
      </div>

      <!-- 액션 버튼 -->
      <div class="flex items-center gap-2 shrink-0">
        <button
          :disabled="!isWatcher"
          class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border transition-colors"
          :class="isWatcher ? 'border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F2F0EB]' : 'border-[#E5E4DF] text-[#C7C7C2] bg-[#FAFAF8] cursor-not-allowed'"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 4.411m0 0L21 21"
            />
          </svg>
          관람 끄기
        </button>
        <button @click="goToEdit" class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F2F0EB] transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
          </svg>
          편집
        </button>
        <button @click="goToCopy" class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F2F0EB] transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
          </svg>
          복사
        </button>
        <button class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border border-red-200 text-red-500 bg-white hover:bg-red-50 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
          삭제
        </button>
      </div>
    </div>

    <!-- 일감 정보 테이블 -->
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] mb-6">
      <table class="w-full">
        <colgroup>
          <col class="w-36" />
          <col />
          <col class="w-36" />
          <col />
        </colgroup>
        <tbody class="divide-y divide-[#F2F0EB]">
          <!-- 범주 / 유형 / 수정시간 -->
          <tr>
            <td colspan="4" class="px-6 py-3">
              <div class="flex items-center gap-3 flex-wrap">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-[#FFF3E0] border border-[#E8920E]/30 text-base font-semibold text-[#E8920E]">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                  </svg>
                  {{ task.category }}
                </span>
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-[#F2F0EB] border border-[#C7C7C2] text-base font-semibold text-[#3A3B35]">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  {{ task.type }}
                </span>
                <span class="h-4 w-px bg-[#C7C7C2]"></span>
                <span class="text-base text-[#9A9B90]">
                  <span class="font-medium text-[#6B6B63]">{{ task.modifiedBy }}</span
                  >이(가) <span class="font-medium text-[#6B6B63]">{{ task.modifiedMinutesAgo }}분</span> 전에 수정함
                </span>
              </div>
            </td>
          </tr>

          <!-- 진행상태 / 우선순위 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">진행상태</td>
            <td class="px-6 py-3">
              <span class="inline-block text-base font-semibold px-2.5 py-0.5 rounded-full" :class="workflowBadge(task.workflow)">{{ task.workflow }}</span>
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">우선순위</td>
            <td class="px-6 py-3">
              <span class="inline-flex items-center gap-1.5 text-base font-semibold" :style="{ color: task.priorityColor }">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-.293.707L14 13.414V19a1 1 0 01-.553.894l-4 2A1 1 0 018 21v-7.586L4.293 7.707A1 1 0 014 7V5z" />
                </svg>
                {{ task.priority }}
              </span>
            </td>
          </tr>

          <!-- 담당자 / 목표버전 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">담당자</td>
            <td class="px-6 py-3">
              <span class="inline-flex items-center gap-2">
                <span class="text-base text-[#1A1816] font-medium">{{ task.name }}({{ task.userId }})</span>
              </span>
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">목표 버전</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ task.versionId || '-' }}</td>
          </tr>

          <!-- 진척도 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">진척도</td>
            <td colspan="3" class="px-6 py-3">
              <div class="flex items-center gap-3">
                <div class="flex-1 max-w-xs h-2 rounded-full bg-[#F2F0EB] overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-500" :class="progressColor(task.progress)" :style="{ width: task.progress + '%' }"></div>
                </div>
                <span class="text-base font-semibold text-[#1A1816] w-10 shrink-0">{{ task.progress }}%</span>
              </div>
            </td>
          </tr>

          <!-- 예상 시작일 / 예상 마감일 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">예상 시작일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ task.plannedStart || '-' }}</td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">예상 마감일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ task.plannedEnd || '-' }}</td>
          </tr>

          <!-- 실제 시작일 / 실제 마감일 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">실제 시작일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ task.actualStart || '-' }}</td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">실제 마감일</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ task.actualEnd || '-' }}</td>
          </tr>

          <!-- 추정 시간 / 소요 시간 + 기록 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">추정 시간</td>
            <td class="px-6 py-3 text-base text-[#1A1816]">{{ formatMinutes(task.estimatedTime) }}</td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">소요 시간</td>
            <td class="px-6 py-3">
              <div class="flex items-center gap-3">
                <span class="text-base text-[#1A1816]">{{ formatMinutes(task.spentTime) }}</span>
                <button class="inline-flex items-center gap-1.5 px-3 py-1 text-base rounded border border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F2F0EB] transition-colors font-medium">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6l4 2m6-2a10 10 0 11-20 0 10 10 0 0120 0z" />
                  </svg>
                  소요시간 기록
                </button>
              </div>
            </td>
          </tr>

          <!-- 상위일감 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">진척도</td>
            <td colspan="3" class="px-6 py-3">
              <div class="flex items-center gap-3">
                <div class="flex-1 max-w-xs h-2 rounded-full bg-[#F2F0EB] overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-500" :class="progressColor(task.progress)" :style="{ width: task.progress + '%' }"></div>
                </div>
                <span class="text-base font-semibold text-[#1A1816] w-10 shrink-0">{{ task.progress }}%</span>
              </div>
            </td>
          </tr>

          <!-- 첨부파일 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">진척도</td>
            <td colspan="3" class="px-6 py-3">
              <div class="flex items-center gap-3">
                <div class="flex-1 max-w-xs h-2 rounded-full bg-[#F2F0EB] overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-500" :class="progressColor(task.progress)" :style="{ width: task.progress + '%' }"></div>
                </div>
                <span class="text-base font-semibold text-[#1A1816] w-10 shrink-0">{{ task.progress }}%</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 하위 일감 -->
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] mb-6">
      <div class="px-6 py-3 border-b border-[#C7C7C2] bg-[#F2F0EB] rounded-t-lg flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="text-base font-bold text-[#1A1816]">하위 일감</span>
          <span class="text-base font-semibold px-2 py-0.5 rounded-full bg-[#E8920E] text-white">{{ subTasks.length }}</span>
          <span class="text-base text-[#6B6B63]"
            >완료 <span class="font-semibold text-emerald-600">{{ subTaskDone }}</span> · 진행중 <span class="font-semibold text-amber-600">{{ subTaskInProgress }}</span></span
          >
        </div>
        <button class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F8F7F4] transition-colors font-medium">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
          하위 일감 추가
        </button>
      </div>

      <table class="w-full text-base">
        <thead>
          <tr class="border-b border-[#F2F0EB] bg-[#F8F7F4]">
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-28">번호</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63]">이름</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-28">상태</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-28">마감일</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-36">진척도</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#F2F0EB]">
          <tr v-for="sub in subTasks" :key="sub.id" class="hover:bg-[#FAFAF8] cursor-pointer transition-colors" @click="goToTask(sub.id)">
            <td class="px-6 py-3 font-mono text-base text-[#9A9B90]">{{ sub.id }}</td>
            <td class="px-6 py-3 text-[#1A1816] font-medium hover:text-[#E8920E] transition-colors">{{ sub.title }}</td>
            <td class="px-6 py-3">
              <span class="text-base font-semibold px-2 py-0.5 rounded-full" :class="workflowBadge(sub.workflow)">{{ sub.workflow }}</span>
            </td>
            <td class="px-6 py-3 text-[#6B6B63]">{{ sub.plannedEnd }}</td>
            <td class="px-6 py-3">
              <div class="flex items-center gap-2">
                <div class="flex-1 h-1.5 rounded-full bg-[#F2F0EB] overflow-hidden">
                  <div class="h-full rounded-full" :class="progressColor(sub.progress)" :style="{ width: sub.progress + '%' }"></div>
                </div>
                <span class="text-base text-[#6B6B63] w-10 text-right shrink-0">{{ sub.progress }}%</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 연결된 일감  -->
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] mb-6">
      <div class="px-6 py-3 border-b border-[#C7C7C2] bg-[#F2F0EB] rounded-t-lg flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="text-base font-bold text-[#1A1816]">연결된 일감</span>
          <span class="text-base font-semibold px-2 py-0.5 rounded-full bg-[#6B6B63] text-white">{{ linkedTasks.length }}</span>
          <span class="text-base text-[#6B6B63]"
            >완료 <span class="font-semibold text-emerald-600">{{ linkedTaskDone }}</span> · 진행중 <span class="font-semibold text-amber-600">{{ linkedTaskInProgress }}</span></span
          >
        </div>
        <button class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F8F7F4] transition-colors font-medium">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
          </svg>
          연결된 일감 추가
        </button>
      </div>

      <table class="w-full text-base">
        <thead>
          <tr class="border-b border-[#F2F0EB] bg-[#F8F7F4]">
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-28">번호</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63]">이름</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-28">상태</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-28">마감일</th>
            <th class="px-6 py-2.5 text-left font-semibold text-[#6B6B63] w-36">진척도</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#F2F0EB]">
          <tr v-for="linked in linkedTasks" :key="linked.id" class="hover:bg-[#FAFAF8] cursor-pointer transition-colors" @click="goToTask(linked.id)">
            <td class="px-6 py-3 font-mono text-base text-[#9A9B90]">{{ linked.id }}</td>
            <td class="px-6 py-3 text-[#1A1816] font-medium hover:text-[#E8920E] transition-colors">{{ linked.title }}</td>
            <td class="px-6 py-3">
              <span class="text-base font-semibold px-2 py-0.5 rounded-full" :class="workflowBadge(linked.workflow)">{{ linked.workflow }}</span>
            </td>
            <td class="px-6 py-3 text-[#6B6B63]">{{ linked.plannedEnd }}</td>
            <td class="px-6 py-3">
              <div class="flex items-center gap-2">
                <div class="flex-1 h-1.5 rounded-full bg-[#F2F0EB] overflow-hidden">
                  <div class="h-full rounded-full" :class="progressColor(linked.progress)" :style="{ width: linked.progress + '%' }"></div>
                </div>
                <span class="text-base text-[#6B6B63] w-10 text-right shrink-0">{{ linked.progress }}%</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ─── 보조 구역 ─────────────────────────────────────────────────── -->
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2]">
      <!-- 탭 헤더 -->
      <div class="flex border-b border-[#C7C7C2] bg-[#F2F0EB] rounded-t-lg overflow-hidden">
        <button
          v-for="tab in [
            { key: 'comments', label: '댓글' },
            { key: 'history', label: '변경 이력' },
            { key: 'timelog', label: '소요시간' }
          ]"
          :key="tab.key"
          class="px-6 py-3 text-base font-semibold transition-colors relative"
          :class="activeTab === tab.key ? 'text-[#E8920E] bg-white border-b-2 border-[#E8920E]' : 'text-[#6B6B63] hover:text-[#3A3B35] hover:bg-[#F8F7F4]'"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- ── 댓글 탭 ── -->
      <div v-if="activeTab === 'comments'" class="p-6">
        <!-- 댓글 입력 -->
        <div class="mb-7 pb-6 border-b border-[#F2F0EB]">
          <div class="mb-2 flex items-baseline gap-1.5">
            <span class="text-base font-semibold text-[#1A1816]">나</span>
            <span class="text-base text-[#9A9B90]">({{ currentUserId }})</span>
          </div>
          <textarea
            v-model="newComment"
            placeholder="댓글을 입력해주세요..."
            rows="3"
            class="w-full px-4 py-2.5 bg-[#FAFAF8] border border-[#C7C7C2] rounded-lg text-base text-[#1A1816] outline-none focus:border-[#E8920E] focus:ring-2 focus:ring-[#E8920E]/15 transition-all resize-none placeholder-[#9A9B90]"
          ></textarea>
          <div class="flex justify-end mt-2">
            <button @click="addComment" class="px-4 py-1.5 bg-[#E8920E] text-white text-base font-semibold rounded hover:bg-[#c97700] transition-colors disabled:opacity-40" :disabled="!newComment.trim()">댓글 작성</button>
          </div>
        </div>

        <!-- 댓글 목록 -->
        <div class="space-y-6">
          <div v-for="comment in comments" :key="comment.id" class="pb-6 border-b border-[#F2F0EB] last:border-none last:pb-0">
            <!-- 작성자 정보 -->
            <div class="flex items-baseline gap-1.5 mb-2">
              <span class="text-base font-semibold text-[#1A1816]">{{ comment.author }}</span>
              <span class="text-base text-[#9A9B90]">({{ comment.authorId }})</span>
              <span class="text-base text-[#C7C7C2] mx-0.5">·</span>
              <span class="text-base text-[#9A9B90]">{{ comment.time }}</span>
            </div>

            <!-- 본문 -->
            <div v-if="!comment.editing" class="text-base text-[#3A3B35] bg-[#FAFAF8] border border-[#F2F0EB] rounded-lg px-4 py-3 leading-relaxed">{{ comment.content }}</div>
            <div v-else>
              <textarea v-model="comment.editContent" rows="2" class="w-full px-3 py-2 text-base border border-[#C7C7C2] rounded-lg outline-none focus:border-[#E8920E] resize-none bg-[#FAFAF8]"></textarea>
              <div class="flex gap-2 mt-2 justify-end">
                <button @click="saveEdit(comment)" class="px-3 py-1 text-base bg-[#E8920E] text-white rounded font-semibold hover:bg-[#c97700]">저장</button>
                <button @click="cancelEdit(comment)" class="px-3 py-1 text-base border border-[#C7C7C2] text-[#6B6B63] rounded hover:bg-[#F2F0EB]">취소</button>
              </div>
            </div>

            <!-- 액션 -->
            <div class="flex items-center gap-3 mt-2 text-base text-[#9A9B90]">
              <button class="hover:text-[#E8920E] transition-colors" @click="replyingTo = replyingTo === comment.id ? null : comment.id">답글</button>
              <button class="hover:text-[#3A3B35] transition-colors" @click="startEdit(comment)">수정</button>
              <button class="hover:text-red-500 transition-colors" @click="deleteComment(comments, comment.id)">삭제</button>
            </div>

            <!-- 답글 입력 -->
            <div v-if="replyingTo === comment.id" class="mt-4 ml-6 pl-4 border-l-2 border-[#F2F0EB]">
              <div class="flex items-baseline gap-1.5 mb-2">
                <span class="text-base font-semibold text-[#1A1816]">나</span>
                <span class="text-base text-[#9A9B90]">({{ currentUserId }})</span>
              </div>
              <textarea v-model="replyContent" placeholder="답글을 입력해주세요..." rows="2" class="w-full px-3 py-2 text-base border border-[#C7C7C2] rounded-lg outline-none focus:border-[#E8920E] resize-none bg-[#FAFAF8]"></textarea>
              <div class="flex gap-2 mt-2 justify-end">
                <button @click="addReply(comment)" class="px-3 py-1 text-base bg-[#E8920E] text-white rounded font-semibold hover:bg-[#c97700]" :disabled="!replyContent.trim()">답글 작성</button>
                <button
                  @click="
                    replyingTo = null;
                    replyContent = '';
                  "
                  class="px-3 py-1 text-base border border-[#C7C7C2] text-[#6B6B63] rounded hover:bg-[#F2F0EB]"
                >
                  취소
                </button>
              </div>
            </div>

            <!-- 대댓글 목록 -->
            <div v-if="comment.replies.length > 0" class="mt-4 ml-6 pl-4 border-l-2 border-[#F2F0EB] space-y-4">
              <div v-for="reply in comment.replies" :key="reply.id">
                <!-- 작성자 정보 -->
                <div class="flex items-baseline gap-1.5 mb-2">
                  <span class="text-base font-semibold text-[#1A1816]">{{ reply.author }}</span>
                  <span class="text-base text-[#9A9B90]">({{ reply.authorId }})</span>
                  <span class="text-base text-[#C7C7C2] mx-0.5">·</span>
                  <span class="text-base text-[#9A9B90]">{{ reply.time }}</span>
                </div>
                <!-- 본문 -->
                <div v-if="!reply.editing" class="text-base text-[#3A3B35] bg-[#F8F7F4] border border-[#F2F0EB] rounded-lg px-4 py-3 leading-relaxed">{{ reply.content }}</div>
                <div v-else>
                  <textarea v-model="reply.editContent" rows="2" class="w-full px-3 py-2 text-base border border-[#C7C7C2] rounded-lg outline-none focus:border-[#E8920E] resize-none bg-[#FAFAF8]"></textarea>
                  <div class="flex gap-2 mt-2 justify-end">
                    <button @click="saveEdit(reply)" class="px-3 py-1 text-base bg-[#E8920E] text-white rounded font-semibold hover:bg-[#c97700]">저장</button>
                    <button @click="cancelEdit(reply)" class="px-3 py-1 text-base border border-[#C7C7C2] text-[#6B6B63] rounded hover:bg-[#F2F0EB]">취소</button>
                  </div>
                </div>
                <!-- 액션 -->
                <div class="flex items-center gap-3 mt-2 text-base text-[#9A9B90]">
                  <button class="hover:text-[#3A3B35] transition-colors" @click="startEdit(reply)">수정</button>
                  <button class="hover:text-red-500 transition-colors" @click="deleteComment(comment.replies, reply.id)">삭제</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ── 변경 이력 탭 ── -->
      <div v-if="activeTab === 'history'" class="p-6">
        <table class="w-full text-base">
          <thead>
            <tr class="border-b border-[#F2F0EB]">
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">수정자</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">수정시간</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">변경 항목</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">이전 값</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">변경 값</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#F2F0EB]">
            <tr v-for="h in history" :key="h.id" class="hover:bg-[#FAFAF8]">
              <td class="py-3 pr-4">
                <span class="text-base font-semibold text-[#1A1816]">{{ h.modifier }}</span>
                <span class="text-base text-[#9A9B90] ml-1">({{ h.modifierId }})</span>
              </td>
              <td class="py-3 pr-4 text-base text-[#6B6B63]">{{ h.time }}</td>
              <td class="py-3 pr-4">
                <span class="px-2 py-0.5 bg-[#F2F0EB] rounded text-base font-semibold text-[#3A3B35] border border-[#E5E4DF]">{{ h.field }}</span>
              </td>
              <td class="py-3 pr-4 text-base text-[#9A9B90] line-through">{{ h.before }}</td>
              <td class="py-3 text-base text-[#1A1816] font-semibold">{{ h.after }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ── 소요시간 탭 ── -->
      <div v-if="activeTab === 'timelog'" class="p-6">
        <div class="flex justify-between items-center mb-4">
          <div class="flex items-center gap-4">
            <div class="text-center">
              <div class="text-lg font-bold text-[#E8920E]">{{ formatMinutes(task.spentTime) }}</div>
              <div class="text-base text-[#9A9B90]">총 소요시간</div>
            </div>
            <div class="h-8 w-px bg-[#F2F0EB]"></div>
            <div class="text-center">
              <div class="text-lg font-bold text-[#3A3B35]">{{ formatMinutes(task.estimatedTime) }}</div>
              <div class="text-base text-[#9A9B90]">추정 시간</div>
            </div>
          </div>
          <button class="inline-flex items-center gap-1.5 px-3 py-1.5 text-base rounded border border-[#C7C7C2] text-[#3A3B35] bg-white hover:bg-[#F2F0EB] transition-colors font-medium">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
            소요시간 추가
          </button>
        </div>
        <table class="w-full text-base">
          <thead>
            <tr class="border-b border-[#F2F0EB]">
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">작업 일시</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">소요시간</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">설명</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">진척도</th>
              <th class="pb-3 text-left font-semibold text-[#6B6B63]">작업 종류</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#F2F0EB]">
            <tr v-for="log in timeLogs" :key="log.id" class="hover:bg-[#FAFAF8]">
              <td class="py-3 pr-4 text-base text-[#6B6B63]">{{ log.date }}</td>
              <td class="py-3 pr-4 text-base font-semibold text-[#E8920E]">{{ formatMinutes(log.spent) }}</td>
              <td class="py-3 pr-4 text-base text-[#1A1816]">{{ log.description }}</td>
              <td class="py-3 pr-4">
                <div class="flex items-center gap-2">
                  <div class="w-16 h-1.5 rounded-full bg-[#F2F0EB] overflow-hidden">
                    <div class="h-full rounded-full" :class="progressColor(log.progress)" :style="{ width: log.progress + '%' }"></div>
                  </div>
                  <span class="text-base text-[#6B6B63]">{{ log.progress }}%</span>
                </div>
              </td>
              <td class="py-3">
                <span class="px-2 py-0.5 bg-[#F2F0EB] rounded text-base font-semibold text-[#3A3B35] border border-[#E5E4DF]">{{ log.type }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style>
/* ── 우선순위 ── */
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

/* ── 상태 ── */
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
