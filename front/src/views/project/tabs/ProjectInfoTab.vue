<script setup>
import { useProjectStore } from '@/stores/project';
import { useVersionStore } from '@/stores/version';
import { storeToRefs } from 'pinia';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const projectStore = useProjectStore();
const versionStore = useVersionStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();
const visible = ref(false);
const confirmMsg = ref('');
const hasVersion = ref(false);
const { pms } = storeToRefs(projectStore);

const openUpdateConfirm = () => {
  confirmMsg.value = '정말 수정하시겠습니까?';
  visible.value = true;
};

// 상위 프로젝트 옵션 - 기존 프로젝트 목록 활용
const projectOptions = ref([]);

onMounted(async () => {
  await projectStore.findProject(route.params.id);
  await projectStore.fetchProjects({});
  const versions = await versionStore.fetchVersions({ projectId: route.params.id });
  hasVersion.value = versions.length > 0;
  const info = projectStore.projectInfo;
  form.title = info.title;
  form.identifier = info.identifier;
  form.description = info.description ?? '';
  form.startDate = info.startDate ? new Date(info.startDate) : null;
  form.endDate = info.endDate ? new Date(info.endDate) : null;
  form.isPublic = info.isPublic === 'J1';
  form.parentId = info.parentId;
  form.pmId = info.pmId;

  projectOptions.value = [
    { label: '없음', value: null },
    ...projectStore.projects
      .filter((p) => p.id !== route.params.id)
      .map((p) => ({
        label: p.title,
        value: p.id,
        startDate: p.startDate,
        endDate: p.endDate
      }))
  ];
});

const selectedParent = computed(() => projectOptions.value.find((p) => p.value && p.value === form.parentId) ?? null);

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
  if (errors.pmId) {
    errors.pmId = 'PM/PL을 지정해주세요.';
    valid = false;
  }
  return valid;
};

const checkIdentifier = async () => {
  if (!form.identifier.trim()) return;
  const isDuplicate = await projectStore.checkIdentifier(form.identifier, route.params.id);
  if (isDuplicate) {
    errors.identifier = '이미 사용중인 식별자입니다.';
  } else {
    errors.identifier = '';
  }
};

const checkTitle = async () => {
  if (!form.title.trim()) return;
  const isDuplicate = await projectStore.checkTitle(form.title, route.params.id);
  if (isDuplicate) {
    errors.title = '이미 사용중인 프로젝트명입니다.';
  } else {
    errors.title = '';
  }
};

const handleSubmit = async () => {
  visible.value = false;

  // 1. 빈값/형식 체크
  if (!validate()) return;

  // 2. 중복 체크 (validate 통과 후에)
  const isIdentifierDuplicate = await projectStore.checkIdentifier(form.identifier, route.params.id);
  if (isIdentifierDuplicate) {
    errors.identifier = '이미 사용중인 식별자입니다.';
    return;
  }

  const isTitleDuplicate = await projectStore.checkTitle(form.title, route.params.id);
  if (isTitleDuplicate) {
    errors.title = '이미 사용중인 프로젝트명입니다.';
    return;
  }

  // 3. 생성
  await projectStore.updateProject(route.params.id, {
    title: form.title,
    identifier: form.identifier,
    description: form.description,
    startDate: formatDate(form.startDate),
    endDate: formatDate(form.endDate),
    pmId: form.pmId,
    isPublic: form.isPublic ? 'J1' : 'J0',
    parentId: form.parentId
  });
  toast.add({ severity: 'success', summary: '수정 완료', detail: '프로젝트가 수정되었습니다.', life: 2000 });
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
  <div class="bg-[#ffffff] h-full">
    <div class="bg-white rounded-lg shadow-sm border border-[#D6E4EA] overflow-hidden mb-6">
      <!-- 기본 정보 섹션 -->
      <div class="bg-[#5B6E96] px-8 py-3 border-b border-[#D6E4EA] rounded-t-lg">
        <span class="text-base font-bold text-[#EBF0F8]">기본 정보</span>
      </div>

      <table class="w-full">
        <colgroup>
          <col class="w-40" />
          <col />
        </colgroup>
        <tbody class="divide-y divide-[#D6E4EA]">
          <!-- 프로젝트명 -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] w-40 align-middle">프로젝트명 <span class="text-red-500">*</span></td>
            <td class="px-6 py-4">
              <InputText v-model="form.title" @blur="checkTitle" placeholder="텍스트를 입력해 주세요." class="w-full form-input" :maxlength="30" />
              <small v-if="errors.title" class="text-red-500 mt-1 block">{{ errors.title }}</small>
              <small v-if="form.title.length > 30" class="text-red-500 text-xs">프로젝트명은 30자를 초과할 수 없습니다.</small>
            </td>
          </tr>

          <!-- 설명 -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-top pt-5">설명</td>
            <td class="px-6 py-4">
              <Textarea v-model="form.description" placeholder="프로젝트에 대한 설명을 입력해 주세요." class="w-full" rows="5" autoResize :maxlength="500" />
              <div class="flex items-center justify-between mt-1">
                <small v-if="form.description.length > 500" class="text-red-500 text-xs">설명은 500자를 초과할 수 없습니다.</small>
                <small class="ml-auto text-xs" :class="form.description.length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'">{{ form.description.length }} / 500</small>
              </div>
            </td>
          </tr>

          <!-- 식별자 -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-middle">식별자 <span class="text-red-500">*</span></td>
            <td class="px-6 py-4">
              <InputText v-model="form.identifier" @blur="checkIdentifier" placeholder="텍스트를 입력해 주세요." class="w-full form-input" :maxlength="30" />
              <small class="text-[#9A9B90] mt-1 block">영문 소문자(a-z), 숫자, 대시(_)만 가능합니다.</small>
              <small v-if="errors.identifier" class="text-red-500 block">{{ errors.identifier }}</small>
              <small v-if="form.identifier.length > 30" class="text-red-500 text-xs">식별자는 30자를 초과할 수 없습니다.</small>
            </td>
          </tr>

          <!-- 상위 프로젝트 -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-middle">상위 프로젝트</td>
            <td class="px-6 py-4">
              <Select v-model="form.parentId" :disabled="hasVersion" :options="projectOptions" optionLabel="label" optionValue="value" placeholder="선택" class="form-input w-150" showClear />
              <div v-if="hasVersion" class="mt-1">
                <small class="text-red-400">버전이 생성된 프로젝트는 상위 프로젝트를 선택할 수 없습니다.</small>
              </div>
            </td>
          </tr>

          <!-- 프로젝트 기간 -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-middle">프로젝트 기간 <span class="text-red-500">*</span></td>
            <td class="px-6 py-4">
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
                <small v-if="selectedParent" class="text-red-400 mt-1"> 하위 프로젝트는 상위 프로젝트 기간({{ formatDate(selectedParent.startDate) }} ~ {{ formatDate(selectedParent.endDate) }})을 벗어날 수 없습니다. </small>
              </div>
            </td>
          </tr>

          <!-- PM/PL -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-middle">PM/PL <span class="text-red-500">*</span></td>
            <td class="px-6 py-4">
              <Select v-model="form.pmId" :options="pms" optionLabel="name" optionValue="id" placeholder="선택" class="form-input w-64" />
              <small v-if="errors.pmId" class="text-red-500 block mt-1">{{ errors.pmId }}</small>
            </td>
          </tr>

          <!-- 공개여부 -->
          <tr>
            <td class="px-6 py-4 bg-[#F2F3F8] text-base font-semibold text-[#3A3B35] align-middle">공개여부</td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <Checkbox v-model="form.isPublic" :binary="true" inputId="isPublic" />
                <label for="isPublic" class="text-sm text-[#3A3B35] cursor-pointer">공개 프로젝트로 설정</label>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
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
