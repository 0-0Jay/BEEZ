<script setup>
import { useAuthStore } from '@/stores/auth';
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import DatePicker from 'primevue/datepicker';
import { computed, nextTick, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const taskStore = useTaskStore();
const projectStore = useProjectStore();
const authStore = useAuthStore();
const project = computed(() => projectStore.selectedProject);
const userId = computed(() => authStore.user.id ?? '20261111');

const pageTitle = computed(() => {
  if (route.path.includes('/task/edit')) return '일감 수정';
  if (route.path.includes('/task/copy')) return '일감 복사';
  return '새 일감 생성';
});

const isCopyPage = computed(() => route.path.includes('/task/copy'));

const workflowOptions = computed(() => taskStore.workflowList);
const userOptions = computed(() => taskStore.memberList);
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const priorityOptions = computed(() => taskStore.priorityList);
const parentTaskOptions = computed(() => [{ id: null, title: '없음' }, ...taskStore.taskList]);
const versionOptions = computed(() => taskStore.versionList);

const watcherSelectValue = ref(null);
const watcherOptions = computed(() => userOptions.value.filter((m) => !form.watchers.some((w) => w.id === m.id)));

const onWatcherSelect = (e) => {
  const selected = userOptions.value.find((m) => m.id === e.value);
  if (selected && !form.watchers.some((w) => w.id === selected.id)) {
    form.watchers.push(selected);
  }
  watcherSelectValue.value = null;
};

const removeWatcher = (id) => {
  form.watchers = form.watchers.filter((w) => w.id !== id);
};

// DatePicker Date 객체 → 'YYYY-MM-DD' 문자열 변환
function formatDate(d) {
  if (!d) return '';
  if (typeof d === 'string') return d;
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

const form = reactive({
  creator: userId.value,
  projectId: project.value.id,
  isPublic: 'J1',
  title: '',
  type: null,
  workflow: null,
  priority: null,
  description: '',
  parentId: null,
  category: null,
  userId: null,
  version: null,
  plannedStart: null,
  plannedEnd: null,
  actualStart: null,
  actualEnd: null,
  estimatedTime: null,
  progress: null,
  attachments: [],
  watchers: [],
  linkCopied: false,
  copySubTasks: false
});

const errors = reactive({
  title: '',
  type: '',
  workflow: '',
  priority: '',
  category: '',
  userId: '',
  plannedStart: '',
  plannedEnd: ''
});

const touched = reactive({
  title: true,
  type: true,
  workflow: true,
  priority: true,
  category: true,
  userId: true,
  plannedStart: true,
  plannedEnd: true
});

const validate = () => {
  let valid = true;
  errors.title = '';
  errors.type = '';
  errors.workflow = '';
  errors.priority = '';
  errors.category = '';
  errors.userId = '';
  errors.plannedStart = '';
  errors.plannedEnd = '';

  if (!form.title.trim()) {
    errors.title = '일감명을 입력해주세요.';
    valid = false;
  }
  if (!form.type) {
    errors.type = '일감유형을 선택해주세요.';
    valid = false;
  }
  if (!form.workflow) {
    errors.workflow = '일감상태를 선택해주세요.';
    valid = false;
  }
  if (!form.priority) {
    errors.priority = '우선순위를 선택해주세요.';
    valid = false;
  }
  if (!form.category) {
    errors.category = '일감 범주를 선택해주세요.';
    valid = false;
  }
  if (!form.userId) {
    errors.userId = '담당자를 선택해주세요.';
    valid = false;
  }
  if (!form.plannedStart) {
    errors.plannedStart = '예상 시작일을 선택해주세요.';
    valid = false;
  }
  if (!form.plannedEnd) {
    errors.plannedEnd = '예상 마감일을 선택해주세요.';
    valid = false;
  }

  return valid;
};

const isSaveDisabled = computed(() => {
  return !form.title.trim() || !form.type || !form.workflow || !form.priority || !form.category || !form.userId || !form.plannedStart || !form.plannedEnd;
});

const handleFileChange = ({ files }) => {
  files.forEach((newFile) => {
    const isDuplicate = form.attachments.some((f) => f.name === newFile.name && f.size === newFile.size);
    if (!isDuplicate) form.attachments.push(newFile);
  });
};

const removeFile = (index) => {
  form.attachments.splice(index, 1);
};

const handleSubmit = async () => {
  if (!validate()) return;

  const formData = new FormData();

  formData.append('isPublic', form.isPublic);
  formData.append('title', form.title);
  formData.append('type', form.type);
  formData.append('workflow', form.workflow);
  formData.append('priority', form.priority);
  formData.append('description', form.description ?? '');
  formData.append('parentId', form.parentId ?? '');
  formData.append('category', form.category);
  formData.append('userId', form.userId);
  formData.append('version', form.version ?? '');
  formData.append('plannedStart', formatDate(form.plannedStart));
  formData.append('plannedEnd', formatDate(form.plannedEnd));
  formData.append('actualStart', formatDate(form.actualStart));
  formData.append('actualEnd', formatDate(form.actualEnd));
  formData.append('estimatedTime', form.estimatedTime ?? '');
  formData.append('progress', form.progress ?? '');
  formData.append('linkCopied', form.linkCopied);
  formData.append('copySubTasks', form.copySubTasks);
  formData.append('creator', form.creator);
  formData.append('projectId', form.projectId);

  form.watchers.forEach((w) => {
    formData.append('watcherIds', w.id);
  });
  form.attachments.forEach((file) => {
    formData.append('attachments', file);
  });

  await taskStore.insertTask(formData);
  router.push('/tasks');
};

const handleCancel = () => {
  router.push('/tasks');
};

onMounted(async () => {
  await taskStore.findCateList();
  await taskStore.findPriorityList();
  await taskStore.findTypeList();
  await taskStore.findWorkflowList();
  await taskStore.findMember(project.value.id);
  await taskStore.findTaskList(project.value.id, userId.value);
  await taskStore.findVersionList(project.value.id);
  validate();
});
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">{{ pageTitle }}</h1>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] mb-6">
      <div class="bg-[#F2F0EB] px-8 py-3 border-b border-[#C7C7C2] rounded-t-lg">
        <span class="text-base font-bold text-[#1A1816]">일감 정보</span>
      </div>

      <table class="w-full">
        <colgroup>
          <col class="w-40" />
          <col />
          <col class="w-40" />
          <col />
        </colgroup>
        <tbody class="divide-y divide-[#F2F0EB]">
          <!-- 프로젝트 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">프로젝트</td>
            <td colspan="3" class="px-6 py-3 text-base text-[#1A1816] font-medium">
              <span class="inline-flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-[#E8920E] inline-block"></span>
                {{ project.title }}
              </span>
            </td>
          </tr>

          <!-- 비공개 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">비공개</td>
            <td colspan="3" class="px-6 py-3">
              <div class="flex items-center gap-2">
                <Checkbox :modelValue="form.isPublic === 'J0'" :binary="true" inputId="isPublic" @change="form.isPublic = form.isPublic === 'J0' ? 'J1' : 'J0'" />
                <label for="isPublic" class="text-base text-[#3A3B35] cursor-pointer">비공개 일감으로 설정</label>
              </div>
            </td>
          </tr>

          <!-- 일감명 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">일감명<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td colspan="3" class="px-6 py-3">
              <InputText v-model="form.title" placeholder="일감명을 입력해주세요." class="w-full" @input="validate()" />
              <small v-if="touched.title && errors.title" class="text-red-500 mt-1 block text-xs">{{ errors.title }}</small>
            </td>
          </tr>

          <!-- 일감유형 / 일감상태 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">일감유형<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <Select v-model="form.type" :options="typeOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" @change="nextTick(validate)" />
              <small v-if="touched.type && errors.type" class="text-red-500 mt-1 block text-xs">{{ errors.type }}</small>
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">일감상태<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <Select v-model="form.workflow" :options="workflowOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" @change="nextTick(validate)" />
              <small v-if="touched.workflow && errors.workflow" class="text-red-500 mt-1 block text-xs">{{ errors.workflow }}</small>
            </td>
          </tr>

          <!-- 우선순위 / 일감 범주 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">우선순위<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <Select v-model="form.priority" :options="priorityOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" @change="nextTick(validate)" />
              <small v-if="touched.priority && errors.priority" class="text-red-500 mt-1 block text-xs">{{ errors.priority }}</small>
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">일감 범주<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <Select v-model="form.category" :options="categoryOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" @change="nextTick(validate)" />
              <small v-if="touched.category && errors.category" class="text-red-500 mt-1 block text-xs">{{ errors.category }}</small>
            </td>
          </tr>

          <!-- 상위 일감 / 목표 버전 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">상위 일감</td>
            <td class="px-6 py-3">
              <Select v-model="form.parentId" :options="parentTaskOptions" optionLabel="title" optionValue="id" placeholder="선택" class="w-full" />
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">목표 버전</td>
            <td class="px-6 py-3">
              <Select v-model="form.version" :options="versionOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
            </td>
          </tr>

          <!-- 담당자 / 진척도 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">담당자<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <Select v-model="form.userId" :options="userOptions" optionLabel="name" optionValue="id" placeholder="검색 또는 선택" filter class="w-full" @change="nextTick(validate)" />
              <small v-if="touched.userId && errors.userId" class="text-red-500 mt-1 block text-xs">{{ errors.userId }}</small>
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">진척도 (%)</td>
            <td class="px-6 py-3">
              <div class="flex items-center gap-4">
                <InputNumber v-model="form.progress" :min="0" :max="100" placeholder="0" class="w-28 shrink-0" suffix="%" />
              </div>
            </td>
          </tr>

          <!-- 예상 시작일 / 예상 마감일 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">예상 시작일<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <DatePicker v-model="form.plannedStart" date-format="yy-mm-dd" placeholder="날짜 선택" show-button-bar class="w-full" input-class="w-full" @date-select="validate()" />
              <small v-if="touched.plannedStart && errors.plannedStart" class="text-red-500 mt-1 block text-xs">{{ errors.plannedStart }}</small>
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">
              <span class="flex items-center gap-1">예상 마감일<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td class="px-6 py-3">
              <DatePicker v-model="form.plannedEnd" date-format="yy-mm-dd" placeholder="날짜 선택" show-button-bar class="w-full" input-class="w-full" @date-select="validate()" />
              <small v-if="touched.plannedEnd && errors.plannedEnd" class="text-red-500 mt-1 block text-xs">{{ errors.plannedEnd }}</small>
            </td>
          </tr>

          <!-- 실제 시작일 / 실제 마감일 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">실제 시작일</td>
            <td class="px-6 py-3">
              <DatePicker v-model="form.actualStart" date-format="yy-mm-dd" placeholder="날짜 선택" show-button-bar class="w-full" input-class="w-full" />
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">실제 마감일</td>
            <td class="px-6 py-3">
              <DatePicker v-model="form.actualEnd" date-format="yy-mm-dd" placeholder="날짜 선택" show-button-bar class="w-full" input-class="w-full" />
            </td>
          </tr>

          <!-- 추정 시간 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">추정 시간 (분)</td>
            <td colspan="3" class="px-6 py-3">
              <InputNumber v-model="form.estimatedtime" :min="0" placeholder="0" class="w-36" />
            </td>
          </tr>

          <!-- 설명 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35] align-top pt-4">설명</td>
            <td colspan="3" class="px-6 py-3">
              <Textarea v-model="form.description" placeholder="일감에 대한 설명을 입력해주세요." class="w-full" rows="5" autoResize />
            </td>
          </tr>

          <!-- 첨부파일 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35] align-top pt-4">첨부파일</td>
            <td colspan="3" class="px-6 py-3">
              <FileUpload
                mode="basic"
                :multiple="true"
                :auto="false"
                choose-label="파일 추가"
                :pt="{
                  root: { class: 'inline-flex' },
                  chooseButton: { class: '!border-[#C7C7C2] !bg-[#FAFAF8] !text-[#3A3B35] hover:!bg-[#F2F0EB] !shadow-none !text-base' }
                }"
                @select="handleFileChange"
              >
                <template #chooseicon>
                  <i class="pi pi-paperclip"></i>
                </template>
              </FileUpload>
              <div v-if="form.attachments.length > 0" class="mt-3 flex flex-col gap-1.5">
                <div v-for="(file, index) in form.attachments" :key="index" class="inline-flex items-center gap-2 px-3 py-2 bg-[#F8F7F4] border border-[#E5E4DF] rounded-md text-base text-[#3A3B35] w-fit max-w-full">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-[#9A9B90] shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span class="truncate max-w-xs">{{ file.name }}</span>
                  <span class="text-[#9A9B90] text-xs shrink-0">({{ (file.size / 1024).toFixed(1) }}KB)</span>
                  <button type="button" class="ml-1 text-[#9A9B90] hover:text-red-500 transition-colors shrink-0" @click="removeFile(index)">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
              <small class="text-[#9A9B90] mt-2 block text-xs">파일당 최대 10MB / 여러 파일을 한 번에 선택하거나 반복 추가할 수 있습니다.</small>
            </td>
          </tr>

          <!-- 일감 관람자 -->
          <tr class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35] align-top pt-4">일감 관람자</td>
            <td colspan="3" class="px-6 py-3">
              <Select v-model="watcherSelectValue" :options="watcherOptions" optionLabel="name" optionValue="id" placeholder="관람자 검색 또는 선택" filter filterPlaceholder="이름으로 검색" class="w-64" @change="onWatcherSelect" />
              <div v-if="form.watchers.length > 0" class="flex flex-wrap gap-2 mt-3">
                <span v-for="w in form.watchers" :key="w.id" class="inline-flex items-center gap-1.5 px-3 py-1 bg-[#FFF3E0] border border-[#E8920E] rounded-full text-base text-[#E8920E] font-medium">
                  {{ w.name }}
                  <button type="button" class="hover:text-red-600 transition-colors leading-none" @click="removeWatcher(w.id)">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </span>
              </div>
            </td>
          </tr>

          <!-- 복사 옵션 -->
          <tr v-if="isCopyPage" class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">복사 옵션</td>
            <td colspan="3" class="px-6 py-3">
              <div class="flex items-center gap-6">
                <div class="flex items-center gap-2">
                  <Checkbox v-model="form.linkCopied" :binary="true" inputId="linkCopied" />
                  <label for="linkCopied" class="text-base text-[#3A3B35] cursor-pointer">복사한 일감 연결</label>
                </div>
                <div class="flex items-center gap-2">
                  <Checkbox v-model="form.copySubTasks" :binary="true" inputId="copySubTasks" />
                  <label for="copySubTasks" class="text-base text-[#3A3B35] cursor-pointer">하위 일감 복사</label>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 버튼 -->
    <div class="flex justify-center gap-3">
      <Button label="저장" class="px-8" :disabled="isSaveDisabled" @click="handleSubmit" />
      <Button label="취소" class="px-8" severity="secondary" @click="handleCancel" />
    </div>
  </div>
</template>
