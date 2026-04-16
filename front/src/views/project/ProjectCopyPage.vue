<script setup>
import { useProjectStore } from '@/stores/project';
import { storeToRefs } from 'pinia';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const projectStore = useProjectStore();
const toast = useToast();
const copyOptions = ref([]);
const { pms } = storeToRefs(projectStore);

// 상위 프로젝트 옵션 - 기존 프로젝트 목록 활용
const projectOptions = ref([]);

onMounted(async () => {
  await projectStore.fetchUserPm();
  await projectStore.findProject(route.params.id);
  await projectStore.fetchProjects({});
  const project = projectStore.projectInfo;
  projectOptions.value = projectStore.projects.map((p) => ({
    label: p.title,
    value: p.id,
    startDate: p.startDate,
    endDate: p.endDate
  }));

  form.title = `${project.title}_복사본`;
  form.identifier = '';
  form.description = project.description ?? '';
  form.startDate = project.startDate ? new Date(project.startDate) : null;
  form.endDate = project.endDate ? new Date(project.endDate) : null;
  form.pmId = project.pmId;
  form.isPublic = project.isPublic === 'J1';
  form.parentId = project.parentId;
});

const selectedParent = computed(() => projectOptions.value.find((p) => p.value === form.parentId) ?? null);

const copyOptionList = [
  { value: 'members', label: '구성원' },
  { value: 'versions', label: '버전' },
  { value: 'issues', label: '일감' },
  { value: 'docs', label: '문서' },
  { value: 'wiki', label: '위키' }
];

const form = reactive({
  title: '',
  identifier: '',
  description: '',
  startDate: null,
  endDate: null,
  pmId: null,
  isPublic: true,
  parentId: null
});

const errors = reactive({
  title: '',
  identifier: '',
  startDate: '',
  endDate: '',
  date: '',
  pmId: ''
});

const validate = () => {
  let valid = true;
  errors.title = '';
  errors.identifier = '';
  errors.startDate = '';
  errors.endDate = '';
  errors.pmId = '';

  if (!form.title.trim()) {
    errors.title = '프로젝트명을 입력해주세요.';
    valid = false;
  } else if (form.title.length > 20) {
    errors.title = '프로젝트 명은 20자 이내로 입력해주세요.';
  }
  if (!form.identifier.trim()) {
    errors.identifier = '식별자를 입력해주세요.';
    valid = false;
  } else if (!/^[a-z0-9_]+$/.test(form.identifier)) {
    errors.identifier = '영문 소문자(a-z), 숫자, 대시(_)만 가능합니다.';
    valid = false;
  } else if (form.identifier.length > 20) {
    errors.identifier = '식별자는 20자 이내로 입력해주세요.';
  }
  if (!form.startDate) {
    errors.startDate = '시작일을 선택해주세요.';
    valid = false;
  }
  if (!form.endDate) {
    errors.endDate = '마감일을 선택해주세요.';
    valid = false;
  }
  if (errors.pmId) {
    errors.pmId = 'PM/PL을 지정해주세요.';
    valid = false;
  }
  return valid;
};

const checkIdentifier = async () => {
  if (!form.identifier.trim()) return;
  const isDuplicate = await projectStore.checkIdentifier(form.identifier);
  if (isDuplicate) {
    errors.identifier = '이미 사용중인 식별자입니다.';
  } else {
    errors.identifier = '';
  }
};

const checkTitle = async () => {
  if (!form.title.trim()) return;
  const isDuplicate = await projectStore.checkTitle(form.title);
  if (isDuplicate) {
    errors.title = '이미 사용중인 프로젝트명입니다.';
  } else {
    errors.title = '';
  }
};

const handleSubmit = async () => {
  // 1. 빈값/형식 체크
  if (!validate()) return;

  // 2. 중복 체크 (validate 통과 후에)
  const isIdentifierDuplicate = await projectStore.checkIdentifier(form.identifier);
  if (isIdentifierDuplicate) {
    errors.identifier = '이미 사용중인 식별자입니다.';
    return;
  }

  const isTitleDuplicate = await projectStore.checkTitle(form.title);
  if (isTitleDuplicate) {
    errors.title = '이미 사용중인 프로젝트명입니다.';
    return;
  }

  // 3. 생성
  const payload = {
    sourceProjectId: route.params.id,
    title: form.title,
    identifier: form.identifier,
    description: form.description,
    startDate: formatDate(form.startDate),
    endDate: formatDate(form.endDate),
    pmId: form.pmId,
    isPublic: form.isPublic ? 'J1' : 'J0',
    parentId: form.parentId,
    copyMembers: copyOptions.value.includes('members') ? 'Y' : 'N',
    copyVersions: copyOptions.value.includes('versions') ? 'Y' : 'N',
    copyIssues: copyOptions.value.includes('issues') ? 'Y' : 'N',
    copyDocs: copyOptions.value.includes('docs') ? 'Y' : 'N',
    copyWiki: copyOptions.value.includes('wiki') ? 'Y' : 'N'
  };

  const id = await projectStore.copyProject(payload);
  toast.add({ severity: 'success', summary: '복사 완료', detail: '프로젝트가 복사되었습니다.', life: 2000 });
  projectStore.selectedProject = {
    title: form.title,
    id: form.id,
    startDate: form.startDate,
    endDate: form.endDate
  };
  router.push(`/project/setting/${id}`);
};

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const handleCancel = () => {
  router.push('/project/list');
};
</script>

<template>
  <div class="p-8 bg-[#FAFAF8] min-h-screen">
    <!-- 타이틀 -->
    <div class="mb-4">
      <h1 class="text-2xl font-bold text-[#1A1816]">프로젝트 복사</h1>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] overflow-hidden mb-6">
      <!-- 기본 정보 섹션 -->
      <div class="bg-[#F2F0EB] px-8 py-3 border-b border-[#C7C7C2]">
        <span class="text-lg font-bold text-[#1A1816]">기본 정보</span>
      </div>

      <div class="divide-y divide-[#F2F0EB]">
        <!-- 프로젝트명 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0"> 프로젝트명 <span class="text-red-500">*</span> </label>
          <div class="flex-1">
            <InputText v-model="form.title" @blur="checkTitle" placeholder="텍스트를 입력해 주세요." class="w-full form-input" />
            <small v-if="errors.title" class="text-red-500 mt-1 block">{{ errors.title }}</small>
          </div>
        </div>

        <!-- 설명 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0">설명</label>
          <div class="flex-1">
            <Textarea v-model="form.description" placeholder="프로젝트에 대한 설명을 입력해 주세요." class="w-full" rows="5" autoResize :maxlength="500" />
            <div class="flex items-center justify-between mt-1">
              <small v-if="(form.description || '').length > 500" class="text-red-500 text-xs">설명은 500자를 초과할 수 없습니다.</small>
              <small class="ml-auto text-xs" :class="(form.description || '').length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'"> {{ (form.description || '').length }} / 500 </small>
            </div>
          </div>
        </div>

        <!-- 식별자 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0"> 식별자 <span class="text-red-500">*</span> </label>
          <div class="flex-1">
            <InputText v-model="form.identifier" @blur="checkIdentifier" placeholder="텍스트를 입력해 주세요." class="w-full form-input" />
            <small class="text-[#9A9B90] mt-1 block">영문 소문자(a-z), 숫자, 대시(_)만 가능합니다.</small>
            <small v-if="errors.identifier" class="text-red-500 block">{{ errors.identifier }}</small>
          </div>
        </div>

        <!-- 프로젝트 기간 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0"> 프로젝트 기간 <span class="text-red-500">*</span> </label>
          <div class="flex flex-col gap-1">
            <div class="flex items-start gap-5">
              <div class="flex flex-col">
                <DatePicker
                  v-model="form.startDate"
                  dateFormat="yy-mm-dd"
                  placeholder="시작일"
                  class="form-input w-50"
                  :minDate="selectedParent ? new Date(selectedParent.startDate) : undefined"
                  :maxDate="form.endDate ?? (selectedParent ? new Date(selectedParent.endDate) : undefined)"
                  showIcon
                  inputClass="w-full"
                />
                <small v-if="errors.startDate" class="text-red-500 mt-1">{{ errors.startDate }}</small>
              </div>
              <span class="text-xl text-[#6B6B63] mt-2">~</span>
              <div class="flex flex-col">
                <DatePicker
                  v-model="form.endDate"
                  dateFormat="yy-mm-dd"
                  placeholder="마감일"
                  class="form-input w-50"
                  :minDate="form.startDate ?? (selectedParent ? new Date(selectedParent.startDate) : undefined)"
                  :maxDate="selectedParent ? new Date(selectedParent.endDate) : undefined"
                  showIcon
                  inputClass="w-full"
                />
                <small v-if="errors.endDate" class="text-red-500 mt-1">{{ errors.endDate }}</small>
              </div>
            </div>
            <small v-if="selectedParent" class="text-[#9A9B90] mt-1"> 하위 프로젝트는 상위 프로젝트 기간({{ formatDate(selectedParent.startDate) }} ~ {{ formatDate(selectedParent.endDate) }})을 벗어날 수 없습니다. </small>
          </div>
        </div>

        <!-- PM/PL -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0">PM/PL <span class="text-red-500">*</span></label>
          <div class="flex-1">
            <Select v-model="form.pmId" :options="pms" optionLabel="name" optionValue="id" placeholder="선택" class="form-input w-64" />
            <small v-if="errors.pmId" class="text-red-500 block mt-1">{{ errors.pmId }}</small>
          </div>
        </div>

        <!-- 공개여부 -->
        <div class="flex items-center px-8 py-5">
          <label class="form-label w-36 shrink-0">공개여부</label>
          <div class="flex items-center gap-3">
            <Checkbox v-model="form.isPublic" :binary="true" inputId="isPublic" />
            <label for="isPublic" class="text-sm text-[#3A3B35] cursor-pointer">공개 프로젝트로 설정</label>
          </div>
        </div>
      </div>
    </div>

    <!-- 상위 프로젝트 섹션 -->
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] overflow-hidden mb-6">
      <div class="bg-[#F2F0EB] px-8 py-3 border-b border-[#C7C7C2]">
        <span class="text-lg font-bold text-[#1A1816]">상위 프로젝트</span>
      </div>

      <div class="flex items-start px-8 py-4">
        <label class="form-label w-36 pt-2 shrink-0">상위 프로젝트</label>
        <div class="flex-1">
          <Select v-model="form.parentId" :options="projectOptions" optionLabel="label" optionValue="value" placeholder="선택" class="form-input w-150" showClear />
        </div>
      </div>
    </div>

    <!-- 추가: 복사 옵션 섹션 -->
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] overflow-hidden mb-6">
      <div class="bg-[#F2F0EB] px-8 py-3 border-b border-[#C7C7C2]">
        <span class="text-lg font-bold text-[#1A1816]">복사</span>
      </div>
      <div class="px-8 py-4">
        <div class="flex gap-20 py-3 px-2">
          <div v-for="option in copyOptionList" :key="option.value" class="flex items-center gap-2">
            <Checkbox v-model="copyOptions" :value="option.value" :inputId="option.value" />
            <label :for="option.value" class="text-m text-[#3A3B35] cursor-pointer">{{ option.label }}</label>
          </div>
        </div>
        <small class="text-red-500 mt-1">함께 복사할 항목을 선택하세요.</small>
      </div>
    </div>

    <!-- 버튼 -->
    <div class="flex justify-center gap-3">
      <Button label="저장" class="btn-amber px-8" @click="handleSubmit" />
      <Button label="취소" class="btn-cancel px-8" @click="handleCancel" />
    </div>
  </div>
</template>

<style scoped>
.form-label {
  font-size: 1rem;
  font-weight: 550;
  color: #3a3b35;
}

:deep(.form-input) {
  height: 38px !important;
  border-color: #c7c7c2 !important;
  background-color: #ffffff !important;
}

:deep(.p-select-label) {
  line-height: normal !important;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  display: flex !important;
  align-items: center !important;
}

:deep(.form-input:focus) {
  border-color: #e8920e !important;
  box-shadow: 0 0 0 2px rgba(232, 146, 14, 0.15) !important;
}

:deep(.p-textarea) {
  border-color: #c7c7c2 !important;
  background-color: #fafaf8 !important;
}

:deep(.p-textarea:focus) {
  border-color: #e8920e !important;
  box-shadow: 0 0 0 2px rgba(232, 146, 14, 0.15) !important;
}

:deep(.btn-amber) {
  background-color: #e8920e !important;
  color: #ffffff !important;
  border: 1px solid transparent !important;
  box-shadow: none !important;
  height: 36px !important;
  min-height: 36px !important;
  box-sizing: border-box !important;
  padding-left: 24px;
  padding-right: 24px;
}

:deep(.btn-amber:hover) {
  background-color: #c97700 !important;
  border-color: #c97700 !important;
}

:deep(.btn-cancel) {
  color: #6b6b63 !important;
  border: 1px solid #c7c7c2 !important;
  height: 36px !important;
  min-height: 36px !important;
  box-sizing: border-box !important;
  padding-left: 24px;
  padding-right: 24px;
  background-color: white !important;
}

:deep(.btn-cancel:hover) {
  background-color: #e5e2d9 !important;
  color: #1a1816 !important;
}
</style>
