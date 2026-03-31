<script setup>
import { useProjectStore } from '@/stores/project';
import { useToast } from 'primevue';
import { onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const projectStore = useProjectStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

// 상위 프로젝트 옵션 - 기존 프로젝트 목록 활용
const projectOptions = ref([]);

onMounted(async () => {
  await projectStore.findProject(route.params.id);
  await projectStore.fetchProjects({});
  const info = projectStore.projectInfo;
  form.title = info.title;
  form.identifier = info.identifier;
  form.description = info.description;
  form.startDate = formatDate(info.startDate);
  form.endDate = formatDate(info.endDate);
  form.isPublic = info.isPublic === 'J1';
  form.parentId = info.parentId;

  projectOptions.value = [
    { label: '없음', value: null },
    ...projectStore.projects
      .filter((p) => p.id !== route.params.id)
      .map((p) => ({
        label: p.title,
        value: p.id
      }))
  ];
});

const form = reactive({
  title: '',
  identifier: '',
  description: '',
  startDate: null,
  endDate: null,
  isPublic: true,
  parentId: null
});

const errors = reactive({
  title: '',
  identifier: '',
  startDate: '',
  endDate: '',
  date: ''
});

const validate = () => {
  let valid = true;
  errors.title = '';
  errors.identifier = '';
  errors.startDate = '';
  errors.endDate = '';

  if (!form.title.trim()) {
    errors.title = '프로젝트명을 입력해주세요.';
    valid = false;
  }
  if (!form.identifier.trim()) {
    errors.identifier = '식별자를 입력해주세요.';
    valid = false;
  } else if (!/^[a-z0-9_]+$/.test(form.identifier)) {
    errors.identifier = '영문 소문자(a-z), 숫자, 대시(_)만 가능합니다.';
    valid = false;
  }
  if (!form.startDate) {
    errors.startDate = '시작일을 선택해주세요.';
    valid = false;
  }
  if (!form.endDate) {
    errors.endDate = '마감일을 선택해주세요.';
    valid = false;
  }
  if (errors.date) {
    valid = false;
  }
  return valid;
};

watch(
  () => [form.startDate, form.endDate],
  () => {
    if (form.startDate && form.endDate) {
      const startDate = new Date(form.startDate);
      const endDate = new Date(form.endDate);
      if (startDate > endDate) {
        errors.date = '시작일이 마감일 이후일 수 없습니다.';
      } else {
        errors.date = '';
      }
    } else {
      errors.date = '';
    }
  }
);

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const visible = ref(false);
const confirmMsg = ref('');

const openUpdateConfirm = () => {
  confirmMsg.value = '정말 수정하시겠습니까?';
  visible.value = true;
};

const handleSubmit = async () => {
  visible.value = false;

  if (!validate()) return;
  await projectStore.updateProject(route.params.id, {
    title: form.title,
    identifier: form.identifier,
    description: form.description,
    startDate: formatDate(form.startDate),
    endDate: formatDate(form.endDate),
    isPublic: form.isPublic ? 'J1' : 'J0',
    parentId: form.parentId
  });
  toast.add({ severity: 'success', summary: '수정 완료', detail: '프로젝트가 수정되었습니다.', life: 2000 });
};

const handleCancel = () => {
  router.push('/project/list');
};
</script>

<template>
  <div>
    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <!-- 기본 정보 섹션 -->
      <div class="bg-[#F2F0EB] px-8 py-3 border-b border-[#C7C7C2]">
        <span class="text-lg font-bold text-[#1A1816]">기본 정보</span>
      </div>

      <div class="divide-y divide-[#F2F0EB]">
        <!-- 프로젝트명 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0"> 프로젝트명 <span class="text-red-500">*</span> </label>
          <div class="flex-1">
            <InputText v-model="form.title" placeholder="텍스트를 입력해 주세요." class="w-full form-input" />
            <small v-if="errors.title" class="text-red-500 mt-1 block">{{ errors.title }}</small>
          </div>
        </div>

        <!-- 설명 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0">설명</label>
          <div class="flex-1">
            <Textarea v-model="form.description" placeholder="텍스트를 입력해 주세요." class="w-full" rows="4" autoResize />
          </div>
        </div>

        <!-- 식별자 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0"> 식별자 <span class="text-red-500">*</span> </label>
          <div class="flex-1">
            <InputText v-model="form.identifier" placeholder="텍스트를 입력해 주세요." class="w-full form-input" />
            <small class="text-[#9A9B90] mt-1 block">영문 소문자(a-z), 숫자, 대시(_)만 가능합니다.</small>
            <small v-if="errors.identifier" class="text-red-500 block">{{ errors.identifier }}</small>
          </div>
        </div>

        <!-- 프로젝트 기간 -->
        <div class="flex items-start px-8 py-4">
          <label class="form-label w-36 pt-2 shrink-0"> 프로젝트 기간 <span class="text-red-500">*</span> </label>
          <div class="flex items-start gap-5">
            <div class="flex flex-col">
              <DatePicker v-model="form.startDate" dateFormat="yy-mm-dd" placeholder="시작일" class="form-input w-40" :class="{ 'p-invalid': errors.startDate }" />
              <small v-if="errors.startDate" class="text-red-500 mt-1">{{ errors.startDate }}</small>
              <small v-if="errors.date" class="text-red-500 mt-1">{{ errors.date }}</small>
            </div>
            <span class="text-xl text-[#6B6B63] mt-2 ml-10">~</span>
            <div class="flex flex-col">
              <DatePicker v-model="form.endDate" dateFormat="yy-mm-dd" placeholder="마감일" class="form-input w-40" :class="{ 'p-invalid': errors.endDate }" />
              <small v-if="errors.endDate" class="text-red-500 mt-1">{{ errors.endDate }}</small>
            </div>
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
    <div class="bg-white rounded-lg shadow-sm border border-[#C7C7C2] overflow-hidden mb-6 mt-6">
      <div class="bg-[#F2F0EB] px-8 py-3 border-b border-[#C7C7C2]">
        <span class="text-lg font-bold text-[#1A1816]">상위 프로젝트</span>
      </div>

      <div class="flex items-start px-8 py-4">
        <label class="form-label w-36 pt-2 shrink-0">상위 프로젝트</label>
        <div class="flex-1">
          <Select v-model="form.parentId" :options="projectOptions" optionLabel="label" optionValue="value" placeholder="선택" class="form-input w-64" />
        </div>
      </div>
    </div>

    <!-- 버튼 -->
    <div class="flex justify-center gap-3">
      <Button label="수정" class="btn-amber px-8" @click="openUpdateConfirm" />
      <Button label="취소" class="btn-cancel px-8" @click="handleCancel" />
    </div>

    <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="handleSubmit">
      <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
    </ConfirmDialog>
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
  background-color: #fafaf8 !important;
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
