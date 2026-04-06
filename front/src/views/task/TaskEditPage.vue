<script setup>
import { useAuthStore } from '@/stores/auth';
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import DatePicker from 'primevue/datepicker';
import { computed, nextTick, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const taskStore = useTaskStore();
const projectStore = useProjectStore();
const authStore = useAuthStore();
const project = computed(() => projectStore.selectedProject);
const userId = computed(() => authStore.user.id);

const isEditMode = route.path.includes('/task/edit');
const isCopyMode = route.path.includes('/task/copy');
const isPopulated = isEditMode || isCopyMode;

const pageTitle = computed(() => {
  if (isEditMode) return '일감 수정';
  if (isCopyMode) return '일감 복사';
  return '새 일감 생성';
});

// 하위 일감 추가로 진입 시
const fixedParentId = route.query.parentId ?? null;
const fixedParentName = route.query.parentName ? decodeURIComponent(route.query.parentName) : null;

const commonCodes = computed(() => taskStore.commonCodeList);
const workflowOptions = computed(() => commonCodes.value.filter((w) => w.cgroup === '0Q'));
const userOptions = computed(() => taskStore.memberList);
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const priorityOptions = computed(() => commonCodes.value.filter((p) => p.cgroup === '0S'));
const parentTaskOptions = computed(() => [{ id: null, title: '없음' }, ...taskStore.taskList]);
const versionOptions = computed(() => taskStore.versionList);

function formatDate(d) {
  if (!d) return '';
  // 문자열이면 앞 10자리(YYYY-MM-DD)만 사용
  if (typeof d === 'string') return d.substring(0, 10);
  // 숫자(타임스탬프)이면 Date로 변환
  const date = typeof d === 'number' ? new Date(d) : d;
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

function parseDate(d) {
  if (!d) return null;
  if (d instanceof Date) return d;
  if (typeof d === 'number') return new Date(d);
  // "YYYY-MM-DD" 형태 문자열을 로컬 시간으로 파싱
  const s = String(d).substring(0, 10);
  const [y, mo, day] = s.split('-').map(Number);
  return new Date(y, mo - 1, day);
}

const task = computed(() => taskStore.task);

const form = reactive({
  creator: isPopulated ? task.value?.creator : userId.value,
  projectId: isPopulated ? task.value?.projectId : project.value?.id,
  isPublic: isPopulated ? (task.value?.isPublic ?? 'J1') : 'J1',
  title: isPopulated ? (task.value?.title ?? '') : '',
  type: isPopulated ? (task.value?.type ?? null) : null,
  workflow: isPopulated ? (task.value?.workflow ?? null) : null,
  priority: isPopulated ? (task.value?.priority ?? null) : null,
  description: isPopulated ? (task.value?.description ?? '') : '',
  parentId: fixedParentId ?? (isPopulated ? (task.value?.parentId ?? null) : null),
  category: isPopulated ? (task.value?.category ?? null) : null,
  userId: isPopulated ? (task.value?.userId ?? null) : null,
  versionId: isPopulated ? (task.value?.versionId ?? null) : null,
  plannedStart: isPopulated ? parseDate(task.value?.plannedStart) : null,
  plannedEnd: isPopulated ? parseDate(task.value?.plannedEnd) : null,
  actualStart: isPopulated ? parseDate(task.value?.actualStart) : null,
  actualEnd: isPopulated ? parseDate(task.value?.actualEnd) : null,
  estimatedTime: isPopulated ? (task.value?.estimatedTime ?? 0) : 0,
  progress: isPopulated ? (task.value?.progress ?? 0) : 0,
  reject: isPopulated ? (task.value?.reject ?? '') : '',
  attachments: isCopyMode ? [] : isPopulated ? (task.value?.fileList ?? []) : [],
  linkCopied: false,
  copySubTasks: false
});

// 수정 모드에서만 task.id 사용, 생성·복사는 null
const fileId = isEditMode ? (task.value?.fileId ?? null) : null;

// 수정 모드 전용 원본값 스냅샷
const originalValues = isEditMode
  ? {
      isPublic: task.value?.isPublic ?? 'J1',
      title: task.value?.title ?? '',
      type: task.value?.type ?? null,
      workflow: task.value?.workflow ?? null,
      priority: task.value?.priority ?? null,
      description: task.value?.description ?? '',
      parentId: task.value?.parentId ?? null,
      category: task.value?.category ?? null,
      userId: task.value?.userId ?? null,
      versionId: task.value?.versionId ?? null,
      plannedStart: formatDate(task.value?.plannedStart),
      plannedEnd: formatDate(task.value?.plannedEnd),
      actualStart: formatDate(task.value?.actualStart),
      actualEnd: formatDate(task.value?.actualEnd),
      estimatedTime: task.value?.estimatedTime ?? 0,
      progress: task.value?.progress ?? 0,
      attachments: (task.value?.fileList ?? []).map((f) => ({ ...f }))
    }
  : null;

// 수정 모드 전용: 변경된 필드만 추려 changeLog 반환
const buildChangeLog = () => {
  if (!isEditMode || !originalValues) return [];

  const scalarFields = ['isPublic', 'title', 'type', 'workflow', 'priority', 'description', 'parentId', 'category', 'userId', 'versionId', 'estimatedTime', 'progress', 'reject'];
  const dateFields = ['plannedStart', 'plannedEnd', 'actualStart', 'actualEnd'];
  const fieldMapper = {
    isPublic: 'is_public',
    title: 'title',
    type: 'type',
    workflow: 'workflow',
    priority: 'priority',
    description: 'description',
    parentId: 'parent_id',
    category: 'category',
    userId: 'user_id',
    versionId: 'version_id',
    estimatedTime: 'estimated_time',
    progress: 'progress',
    reject: 'reject',
    plannedStart: 'planned_start',
    plannedEnd: 'planned_end',
    actualStart: 'actual_start',
    actualEnd: 'actual_end'
  };

  const changeLog = [];

  scalarFields.forEach((field) => {
    const oldValue = originalValues[field] ?? '';
    const newValue = form[field] ?? '';
    if (String(oldValue) !== String(newValue)) {
      changeLog.push({ fieldName: fieldMapper[field], oldValue, newValue });
    }
  });

  dateFields.forEach((field) => {
    const oldValue = originalValues[field] ?? '';
    const newValue = formatDate(form[field]) ?? '';
    if (oldValue !== newValue) {
      changeLog.push({ fieldName: fieldMapper[field], oldValue, newValue });
    }
  });

  // 첨부파일 변경 감지
  const originalFileNames = originalValues.attachments.map((f) => f.name).join(', ') || '-';
  const currentFileNames = form.attachments.map((f) => f.name).join(', ') || '-';
  if (originalFileNames !== currentFileNames) {
    changeLog.push({ fieldName: 'attachments', oldValue: originalFileNames, newValue: currentFileNames });
  }

  return changeLog;
};

const errors = reactive({
  title: '',
  type: '',
  workflow: '',
  priority: '',
  category: '',
  userId: '',
  plannedStart: '',
  plannedEnd: '',
  reject: ''
});

const touched = reactive({
  title: true,
  type: true,
  workflow: true,
  priority: true,
  category: true,
  userId: true,
  plannedStart: true,
  plannedEnd: true,
  reject: true
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
  errors.reject = '';

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
  if (form.workflow === 'Q4' && !form.reject?.trim()) {
    errors.reject = '반려사유를 입력해주세요.';
    valid = false;
  }

  return valid;
};

const isSaveDisabled = computed(() => {
  const base = !form.title.trim() || !form.type || !form.workflow || !form.priority || !form.category || !form.userId || !form.plannedStart || !form.plannedEnd;
  if (form.workflow === 'Q4') return base || !form.reject?.trim();
  return base;
});

const handleFileChange = ({ files }) => {
  files.forEach((newFile) => {
    // 기존 파일(서버)과 새 파일(File) 모두 중복 체크
    const isDuplicate = form.attachments.some((f) => {
      if (f instanceof File) return f.name === newFile.name && f.size === newFile.size;
      return f.originalName === newFile.name && f.fileSize === newFile.size;
    });
    if (!isDuplicate) form.attachments.push(newFile);
  });
};

const deletedFileIds = reactive([]);
const removeFile = (index) => {
  const file = form.attachments[index];
  // 기존 파일(서버에서 온 객체)이면 삭제 id 추적
  if (!(file instanceof File) && file.id) {
    deletedFileIds.push(file.id);
  }
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
  formData.append('versionId', form.versionId ?? '');
  formData.append('plannedStart', formatDate(form.plannedStart));
  formData.append('plannedEnd', formatDate(form.plannedEnd));
  formData.append('actualStart', formatDate(form.actualStart));
  formData.append('actualEnd', formatDate(form.actualEnd));
  formData.append('estimatedTime', form.estimatedTime ?? 0);
  formData.append('progress', form.progress ?? 0);
  formData.append('creator', form.creator);
  formData.append('projectId', project.value.id);

  let nextId = '';
  if (isEditMode) {
    formData.append('id', task.value?.id);
    formData.append('editor', userId.value);
    formData.append('fileId', fileId);
    const changeLog = buildChangeLog();
    changeLog.forEach((entry, i) => {
      formData.append(`journals[${i}].fieldName`, entry.fieldName);
      formData.append(`journals[${i}].oldValue`, entry.oldValue ?? '');
      formData.append(`journals[${i}].newValue`, entry.newValue ?? '');
    });
    deletedFileIds.forEach((id) => formData.append('deletedFileIds', id));
    form.attachments.filter((f) => f instanceof File).forEach((file) => formData.append('attachments', file));
    await taskStore.updateTask(formData);
    nextId = task.value?.id;
  } else if (isCopyMode) {
    formData.append('linkCopied', form.linkCopied);
    formData.append('copySubTasks', form.copySubTasks);
    formData.append('originTask', task.value?.id);
    form.attachments.forEach((file) => formData.append('attachments', file));
    nextId = await taskStore.insertTask(formData);
  } else {
    form.attachments.forEach((file) => formData.append('attachments', file));
    nextId = await taskStore.insertTask(formData);
  }
  await taskStore.findTaskDetail(nextId);
  router.push(`/task/${nextId}`);
};

const handleCancel = () => {
  if (isPopulated) {
    router.push(`/task/${task.value?.id}`);
  } else {
    router.push('/tasks');
  }
};

onMounted(async () => {
  await taskStore.findCateList();
  await taskStore.findTypeList();
  await taskStore.findCommonCodeList();
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
              <span v-if="fixedParentId" class="inline-flex items-center gap-1.5 text-base text-[#E8920E] font-medium"> {{ fixedParentName }} </span>
              <Select v-else v-model="form.parentId" :options="parentTaskOptions" optionLabel="title" optionValue="id" placeholder="선택" class="w-full" />
            </td>
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35]">목표 버전</td>
            <td class="px-6 py-3">
              <Select v-model="form.versionId" :options="versionOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
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
              <InputNumber v-model="form.estimatedTime" :min="0" placeholder="0" class="w-36" />
            </td>
          </tr>

          <!-- 반려 사유 -->
          <tr v-if="form.workflow === 'Q4'" class="divide-x divide-[#F2F0EB]">
            <td class="px-6 py-3 bg-[#F8F7F4] text-base font-semibold text-[#3A3B35] align-top pt-4">
              <span class="flex items-center gap-1">반려사유<span class="text-red-500 inline-block text-xl">*</span></span>
            </td>
            <td colspan="3" class="px-6 py-3">
              <Textarea v-model="form.reject" placeholder="반려사유를 입력해주세요." class="w-full" rows="3" autoResize @input="validate()" />
              <small v-if="touched.reject && errors.reject" class="text-red-500 mt-1 block text-xs">{{ errors.reject }}</small>
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

          <!-- 복사 옵션 -->
          <tr v-if="isCopyMode" class="divide-x divide-[#F2F0EB]">
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
      <Button label="저장" class="px-8" raised :disabled="isSaveDisabled" @click="handleSubmit" />
      <Button label="취소" class="px-8" raised severity="secondary" @click="handleCancel" />
    </div>
  </div>
</template>
