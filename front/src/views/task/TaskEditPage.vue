<script setup>
import { useAuthStore } from '@/stores/auth';
import { useProjectStore } from '@/stores/project';
import { useTaskStore } from '@/stores/task';
import { useToast } from 'primevue';
import DatePicker from 'primevue/datepicker';
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const taskStore = useTaskStore();
const projectStore = useProjectStore();
const authStore = useAuthStore();
const project = computed(() => projectStore.selectedProject);
const userId = computed(() => authStore.user.id);
const roleId = computed(() => [...(authStore.projectRoles ?? []), { id: authStore.user?.role }]);
const condition = computed(() => (taskStore.task?.creator == userId.value ? 'Z1' : taskStore.task?.userId == userId.value ? 'Z2' : 'Z0'));
const toast = useToast();

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
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const priorityOptions = computed(() => commonCodes.value.filter((p) => p.cgroup === '0S'));
const versionOptions = computed(() => taskStore.versionList);
const workflowOptions = computed(() => commonCodes.value.filter((w) => w.cgroup === '0Q'));

// 담당자 목록: isAssignee Y1만, 동일 id는 역할 병합
const userOptions = computed(() => taskStore.memberList.filter((m) => m.isAssignee === 'Y1'));

const userOptionGroups = computed(() => {
  const roleMap = new Map();
  userOptions.value.forEach((m) => {
    if (!roleMap.has(m.role)) roleMap.set(m.role, []);
    roleMap.get(m.role).push(m);
  });
  return Array.from(roleMap.entries()).map(([role, items]) => ({ label: role, items }));
});

const parentTaskName = computed(() => {
  if (fixedParentId) return fixedParentName;
  if (form.parentId) return taskStore.taskList.find((t) => t.id == form.parentId)?.title ?? String(form.parentId);
  return null;
});

// 상위 일감 전체 객체 (날짜 범위 검증용)
const parentTask = computed(() => {
  const pid = fixedParentId ?? form.parentId ?? null;
  if (!pid) return null;
  // taskList에서 먼저 찾고, 없으면 현재 로드된 task 활용 (하위 일감 추가 진입 시)
  return taskStore.taskList.find((t) => t.id == pid) ?? (String(taskStore.task?.id) === String(pid) ? taskStore.task : null);
});

const parentPlannedStart = computed(() => parseDate(parentTask.value?.plannedStart));
const parentPlannedEnd = computed(() => parseDate(parentTask.value?.plannedEnd));
const parentVersionId = computed(() => parentTask.value?.versionId ?? null);

// 기본 버전
const defaultVersionId = computed(() => taskStore.versionList.find((v) => v.defaultVersion !== null)?.id);

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
  return new Date(d);
}

const task = computed(() => taskStore.task);

const form = reactive({
  creator: isPopulated ? task.value?.creator : userId.value,
  projectId: isPopulated ? task.value?.projectId : project.value?.id,
  isPublic: isPopulated ? (task.value?.isPublic ?? 'J1') : 'J1',
  title: isPopulated ? (isCopyMode ? task.value?.title + '_복사본' : task.value?.title) : '',
  type: isPopulated ? (task.value?.type ?? null) : null,
  workflow: isPopulated ? (task.value?.workflow ?? null) : null,
  priority: isPopulated ? (task.value?.priority ?? null) : null,
  description: isPopulated ? (task.value?.description ?? '') : '',
  parentId: fixedParentId ?? (isPopulated ? (task.value?.parentId ?? null) : null),
  category: isPopulated ? (task.value?.category ?? null) : null,
  userId: isPopulated ? (task.value?.userId ?? null) : null,
  versionId: isPopulated ? (task.value?.versionId ?? null) : defaultVersionId.value,
  plannedStart: isPopulated ? parseDate(task.value?.plannedStart) : null,
  plannedEnd: isPopulated ? parseDate(task.value?.plannedEnd) : null,
  actualStart: isPopulated ? parseDate(task.value?.actualStart) : null,
  actualEnd: isPopulated ? parseDate(task.value?.actualEnd) : null,
  estimatedTime: isPopulated ? (task.value?.estimatedTime ?? 0) : 0,
  progress: isPopulated ? (isCopyMode ? 0 : task.value?.progress) : 0,
  reject: isPopulated ? (task.value?.reject ?? '') : '',
  attachments: isCopyMode ? [] : isPopulated ? (task.value?.fileList ?? []) : [],
  linkCopied: false,
  copySubTasks: false
});

// 일감유형 변경 시 defaultStatus로 workflow 자동 지정
if (!isEditMode) {
  watch(
    () => form.type,
    (newType) => {
      const selectedType = typeOptions.value.find((t) => t.id === newType);
      form.workflow = selectedType?.defaultStatus ?? null;
    },
    { immediate: true }
  );
}

// 생성/복사 모드에서 workflow 텍스트 표시용
const currentWorkflowName = computed(() => {
  if (!form.workflow) return null;
  return workflowOptions.value.find((w) => w.id === form.workflow)?.name ?? form.workflow;
});

const editWorkflowOptions = computed(() => {
  const workflowList = taskStore.workflow ?? [];
  const allowedAfters = workflowList
    .filter((w) => w.before === form.workflow && w.isAllow === 'R1')
    .map((w) => w.after)
    .sort((a, b) => a.localeCompare(b));
  const allowedIds = new Set([form.workflow, ...allowedAfters]);
  return workflowOptions.value.filter((w) => allowedIds.has(w.id));
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
      plannedStart: parseDate(task.value?.plannedStart),
      plannedEnd: parseDate(task.value?.plannedEnd),
      actualStart: parseDate(task.value?.actualStart),
      actualEnd: parseDate(task.value?.actualEnd),
      estimatedTime: task.value?.estimatedTime ?? 0,
      progress: task.value?.progress ?? 0,
      attachments: (task.value?.fileList ?? []).map((f) => ({ ...f })),
      reject: task.value?.reject ?? ''
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
    const oldValue = formatDate(originalValues[field]) ?? '';
    const newValue = formatDate(form[field]) ?? '';
    console.log(oldValue);
    console.log(newValue);
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
  actualStart: '',
  actualEnd: '',
  reject: '',
  description: ''
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
  actualStart: true,
  actualEnd: true,
  reject: true
});

const projectStart = parseDate(project.value?.startDate);
const projectEnd = parseDate(project.value?.endDate);
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
  errors.description = '';

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
  if (form.plannedStart && projectStart && form.plannedStart < projectStart) {
    errors.plannedStart = `프로젝트 시작일(${formatDate(projectStart)}) 이후여야 합니다.`;
    valid = false;
  }
  if (form.plannedEnd && projectEnd && form.plannedEnd > projectEnd) {
    errors.plannedEnd = `프로젝트 마감일(${formatDate(projectEnd)}) 이전이어야 합니다.`;
    valid = false;
  }
  if (form.plannedStart && parentPlannedStart.value && form.plannedStart < parentPlannedStart.value) {
    errors.plannedStart = `상위 일감 시작일(${formatDate(parentPlannedStart.value)}) 이후여야 합니다.`;
    valid = false;
  }
  if (form.plannedEnd && parentPlannedEnd.value && form.plannedEnd > parentPlannedEnd.value) {
    errors.plannedEnd = `상위 일감 마감일(${formatDate(parentPlannedEnd.value)}) 이전이어야 합니다.`;
    valid = false;
  }
  if (form.plannedStart && form.plannedEnd && form.plannedStart > form.plannedEnd) {
    errors.plannedEnd = '예상 마감일은 시작일 이후여야 합니다.';
    valid = false;
  }
  if (form.actualStart && form.actualEnd && form.actualStart > form.actualEnd) {
    errors.actualEnd = '실제 마감일은 시작일 이후여야 합니다.';
    valid = false;
  }
  if (form.workflow === 'Q4' && !form.reject?.trim()) {
    errors.reject = '반려사유를 입력해주세요.';
    valid = false;
  }
  if ((form.description ?? '').length > 500) {
    errors.description = '설명은 500자를 초과할 수 없습니다.';
    valid = false;
  }

  return valid;
};

const handleFileChange = ({ files }) => {
  const MAX_SIZE = 10 * 1024 * 1024; // 10MB

  files.forEach((newFile) => {
    if (newFile.size > MAX_SIZE) {
      toast.add({
        severity: 'error',
        summary: '파일 첨부 실패',
        detail: `첨부파일 크기는 10MB를 넘길 수 없습니다!\n(실패한 파일 : ${newFile.name})`,
        life: 3000,
        closable: false
      });
      return;
    }

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
  if (!validate()) {
    return;
  }
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
    nextId = task.value?.id;
    if (changeLog.length == 0) {
      router.push(`/task/${nextId}`);
      return;
    }
    changeLog.forEach((entry, i) => {
      formData.append(`journals[${i}].fieldName`, entry.fieldName);
      formData.append(`journals[${i}].oldValue`, entry.oldValue ?? '');
      formData.append(`journals[${i}].newValue`, entry.newValue ?? '');
    });
    deletedFileIds.forEach((id) => formData.append('deletedFileIds', id));
    form.attachments.filter((f) => f instanceof File).forEach((file) => formData.append('attachments', file));
    await taskStore.updateTask(formData);
    toast.add({
      severity: 'success',
      summary: '수정 완료',
      detail: '일감이 수정되었습니다.',
      life: 3000,
      closable: false
    });
  } else if (isCopyMode) {
    formData.append('linkCopied', form.linkCopied);
    formData.append('copySubTasks', form.copySubTasks);
    formData.append('originTask', task.value?.id);
    form.attachments.forEach((file) => formData.append('attachments', file));
    nextId = await taskStore.insertTask(formData);
    toast.add({
      severity: 'success',
      summary: '복사 완료',
      detail: '일감이 복사되었습니다.',
      life: 3000,
      closable: false
    });
  } else {
    form.attachments.forEach((file) => formData.append('attachments', file));
    nextId = await taskStore.insertTask(formData);
    toast.add({
      severity: 'success',
      summary: '등록 완료',
      detail: '새 일감이 등록되었습니다.',
      life: 3000,
      closable: false
    });
  }
  router.push(`/task/${nextId}`);
};

const handleCancel = () => {
  if (isPopulated) {
    router.push(`/task/${task.value?.id}`);
  } else {
    router.push('/tasks');
  }
};

const loading = ref(false);
onMounted(async () => {
  loading.value = true;
  await Promise.all([taskStore.findVersionList(project.value.id), taskStore.findCateList(), taskStore.findTypeList(), taskStore.findMember(project.value.id), taskStore.findCommonCodeList()]);
  if (isEditMode) {
    console.log(roleId.value);
    await taskStore.findWorkflow({ roleId: roleId.value.map((r) => r.id), typeId: taskStore.task?.type, conditionType: condition.value });
  }
  loading.value = false;
});
</script>

<template>
  <div class="p-8 bg-white min-h-screen">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-[#1A1816]">{{ pageTitle }}</h1>
    </div>

    <div v-if="loading" class="flex justify-center items-center py-20 text-stone-400">
      <i class="pi pi-spin pi-spinner text-2xl mr-2"></i>
      데이터 불러오는 중...
    </div>
    <div v-else>
      <div class="bg-white rounded-lg shadow-sm border border-[#D6E4EA] mb-6">
        <div class="bg-[#5B6E96] px-8 py-3 border-b border-[#D6E4EA] rounded-t-lg">
          <span class="text-base font-bold text-[#FFFFFF]">일감 정보</span>
        </div>

        <table class="w-full">
          <colgroup>
            <col class="w-40" />
            <col />
            <col class="w-40" />
            <col />
          </colgroup>
          <tbody class="divide-y divide-[#D6E4EA]">
            <!-- 프로젝트 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">프로젝트</td>
              <td colspan="3" class="px-6 py-3 text-base text-[#1A1816] font-medium">
                <span class="inline-flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-[#E8920E] inline-block"></span>
                  {{ project.title }}
                </span>
              </td>
            </tr>

            <!-- 비공개 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">비공개</td>
              <td colspan="3" class="px-6 py-3">
                <div class="flex items-center gap-2">
                  <Checkbox :modelValue="form.isPublic === 'J0'" :binary="true" inputId="isPublic" @change="form.isPublic = form.isPublic === 'J0' ? 'J1' : 'J0'" />
                  <label for="isPublic" class="text-base text-[#3A3B35] cursor-pointer">비공개 일감으로 설정</label>
                </div>
              </td>
            </tr>

            <!-- 일감명 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">일감명<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td colspan="3" class="px-6 py-3">
                <InputText v-model="form.title" placeholder="일감명을 입력해주세요." class="w-full" />
                <small v-if="touched.title && errors.title" class="text-red-500 mt-1 block text-xs">{{ errors.title }}</small>
              </td>
            </tr>

            <!-- 일감유형 / 일감상태 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">일감유형<span v-if="!isEditMode" class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <!-- 수정 모드: 유형 변경 불가, 텍스트 표시 -->
                <template v-if="isEditMode">
                  <span class="text-base font-medium text-[#1A1816]">
                    {{ typeOptions.find((t) => t.id === form.type)?.name ?? '-' }}
                  </span>
                </template>
                <!-- 생성/복사 모드: Select -->
                <template v-else>
                  <Select v-model="form.type" :options="typeOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
                  <small v-if="touched.type && errors.type" class="text-red-500 mt-1 block text-xs">{{ errors.type }}</small>
                </template>
              </td>

              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">일감상태<span v-if="isEditMode" class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <!-- 수정 모드: 허용된 전환 상태만 Select로 선택 -->
                <template v-if="isEditMode">
                  <Select v-model="form.workflow" :options="editWorkflowOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
                  <small v-if="touched.workflow && errors.workflow" class="text-red-500 mt-1 block text-xs">{{ errors.workflow }}</small>
                </template>
                <!-- 생성/복사 모드: 유형 선택에 따라 자동 지정, 텍스트 표시 -->
                <template v-else>
                  <span v-if="currentWorkflowName" class="text-base font-medium text-[#1A1816]">{{ currentWorkflowName }}</span>
                  <span v-else class="text-base text-[#9A9B90]">일감유형을 선택하면 자동 지정됩니다.</span>
                </template>
              </td>
            </tr>

            <!-- 우선순위 / 일감 범주 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">우선순위<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <Select v-model="form.priority" :options="priorityOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
                <small v-if="touched.priority && errors.priority" class="text-red-500 mt-1 block text-xs">{{ errors.priority }}</small>
              </td>
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">일감 범주<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <Select v-model="form.category" :options="categoryOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
                <small v-if="touched.category && errors.category" class="text-red-500 mt-1 block text-xs">{{ errors.category }}</small>
              </td>
            </tr>

            <!-- 목표 버전 / 추정 시간-->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">목표 버전</td>
              <td class="px-6 py-3">
                <span v-if="parentTask" class="text-base font-medium text-[#1A1816]"> {{ versionOptions.find((v) => v.id === parentVersionId)?.name ?? '-' }}</span>
                <Select v-else v-model="form.versionId" :options="versionOptions" optionLabel="name" optionValue="id" placeholder="선택" class="w-full" />
              </td>
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">추정 시간 (시간)</td>
              <td colspan="3" class="px-6 py-3">
                <InputNumber v-model="form.estimatedTime" :min="0" placeholder="0" class="w-36" />
              </td>
            </tr>

            <!-- 담당자 / 진척도 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">담당자<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <Select v-model="form.userId" :options="userOptionGroups" optionGroupLabel="label" optionGroupChildren="items" optionLabel="name" optionValue="id" placeholder="검색 또는 선택" filter class="w-full">
                  <template #optiongroup="{ option }">
                    <div class="flex items-center gap-1.5 text-base font-semibold text-amber-600 py-0.5">
                      <i class="pi pi-users text-base"></i>
                      {{ option.label }}
                    </div>
                  </template>
                  <template #option="{ option }"> {{ option.name }} ({{ option.id }}) </template>
                </Select>
                <small v-if="touched.userId && errors.userId" class="text-red-500 mt-1 block text-xs">{{ errors.userId }}</small>
              </td>
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">진척도 (%)</td>
              <td class="px-6 py-3">
                <template v-if="isEditMode && task?.childTaskList?.length > 0">
                  <span class="text-base font-semibold text-[#1A1816]">{{ task?.subProgress ?? 0 }}%</span>
                  <p class="text-xs text-[#9A9B90] mt-1">하위 일감이 있는 경우 진척도는 자동으로 계산됩니다.</p>
                </template>
                <template v-else>
                  <div class="flex items-center gap-4">
                    <InputNumber v-model="form.progress" :min="0" :max="100" showButtons mode="decimal" :step="10" placeholder="0" suffix="%" />
                  </div>
                </template>
              </td>
            </tr>

            <!-- 예상 시작일 / 예상 마감일 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">예상 시작일<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <DatePicker
                  v-model="form.plannedStart"
                  date-format="yy-mm-dd"
                  :minDate="parentPlannedStart ?? projectStart"
                  :maxDate="form.plannedEnd ?? parentPlannedEnd ?? projectEnd"
                  placeholder="날짜 선택"
                  show-button-bar
                  class="w-full"
                  input-class="w-full"
                />
                <small v-if="touched.plannedStart && errors.plannedStart" class="text-red-500 mt-1 block text-xs">{{ errors.plannedStart }}</small>
              </td>
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">
                <span class="flex items-center gap-1">예상 마감일<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td class="px-6 py-3">
                <DatePicker
                  v-model="form.plannedEnd"
                  date-format="yy-mm-dd"
                  :minDate="form.plannedStart ?? parentPlannedStart ?? projectStart"
                  :maxDate="parentPlannedEnd ?? projectEnd"
                  placeholder="날짜 선택"
                  show-button-bar
                  class="w-full"
                  input-class="w-full"
                />
                <small v-if="touched.plannedEnd && errors.plannedEnd" class="text-red-500 mt-1 block text-xs">{{ errors.plannedEnd }}</small>
              </td>
            </tr>

            <!-- 실제 시작일 / 실제 마감일 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">실제 시작일</td>
              <td class="px-6 py-3">
                <DatePicker v-model="form.actualStart" date-format="yy-mm-dd" :maxDate="form.actualEnd" placeholder="날짜 선택" show-button-bar class="w-full" input-class="w-full" />
                <small v-if="touched.actualStart && errors.actualStart" class="text-red-500 mt-1 block text-xs">{{ errors.actualStart }}</small>
              </td>
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">실제 마감일</td>
              <td class="px-6 py-3">
                <DatePicker v-model="form.actualEnd" date-format="yy-mm-dd" :minDate="form.actualStart" placeholder="날짜 선택" show-button-bar class="w-full" input-class="w-full" />
                <small v-if="touched.actualEnd && errors.actualEnd" class="text-red-500 mt-1 block text-xs">{{ errors.actualEnd }}</small>
              </td>
            </tr>

            <!-- 상위 일감 -->
            <tr v-if="parentTaskName" class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">상위 일감</td>
              <td colspan="3" class="px-6 py-3">
                <span class="inline-flex items-center gap-1.5 text-base font-medium">
                  {{ parentTaskName }}
                  <span v-if="parentPlannedStart || parentPlannedEnd" class="text-[#6B6B63]"> ({{ formatDate(parentPlannedStart) || '??' }} ~ {{ formatDate(parentPlannedEnd) || '??' }}) </span>
                </span>
              </td>
            </tr>

            <!-- 반려 사유 -->
            <tr v-if="form.workflow === 'Q4'" class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-top pt-4">
                <span class="flex items-center gap-1">반려사유<span class="text-red-500 inline-block text-xl">*</span></span>
              </td>
              <td colspan="3" class="px-6 py-3">
                <Textarea v-model="form.reject" placeholder="반려사유를 입력해주세요." class="w-full" rows="3" autoResize />
                <small v-if="touched.reject && errors.reject" class="text-red-500 mt-1 block text-xs">{{ errors.reject }}</small>
              </td>
            </tr>

            <!-- 설명 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-top pt-4">설명</td>
              <td colspan="3" class="px-6 py-3">
                <Textarea v-model="form.description" placeholder="일감에 대한 설명을 입력해주세요." class="w-full" rows="5" autoResize />
                <div class="flex items-center justify-between mt-1">
                  <small v-if="errors.description" class="text-red-500 text-xs">
                    {{ errors.description }}
                  </small>
                  <small class="ml-auto text-xs" :class="(form.description ?? '').length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'"> {{ (form.description ?? '').length }} / 500 </small>
                </div>
              </td>
            </tr>

            <!-- 첨부파일 -->
            <tr class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-top pt-4">첨부파일</td>
              <td colspan="3" class="px-6 py-3">
                <FileUpload
                  mode="basic"
                  :multiple="true"
                  :auto="false"
                  class="!bg-[#2D8FAD] !border-[#2D8FAD] hover:!bg-[#257892]"
                  choose-label="파일 추가"
                  :pt="{
                    root: { class: 'inline-flex' },
                    chooseButton: { class: '!border-[#C7C7C2] !bg-[#FAFAF8] !text-[#3A3B35] hover:!bg-[#D6E4EA] !shadow-none !text-base' }
                  }"
                  @select="handleFileChange"
                >
                  <template #chooseicon>
                    <i class="pi pi-paperclip"></i>
                  </template>
                </FileUpload>
                <div v-if="form.attachments.length > 0" class="mt-3 flex flex-col gap-1.5">
                  <div v-for="(file, index) in form.attachments" :key="index" class="inline-flex items-center gap-2 px-3 py-2 bg-[#F2F3F8] border border-[#E5E4DF] rounded-md text-base text-[#3A3B35] w-fit max-w-full">
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
                <small class="text-[#9A9B90] mt-2 block text-base">파일당 최대 10MB / 여러 파일을 한 번에 선택하거나 반복 추가할 수 있습니다.</small>
              </td>
            </tr>

            <!-- 복사 옵션 -->
            <tr v-if="isCopyMode" class="divide-x divide-[#D6E4EA]">
              <td class="px-6 py-3 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35]">복사 옵션</td>
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
        <Button label="저장" class="px-8" raised @click="handleSubmit" />
        <Button label="취소" class="px-8" raised severity="secondary" @click="handleCancel" />
      </div>
    </div>
  </div>
</template>
