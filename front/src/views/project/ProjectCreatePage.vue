<script setup>
import { useProjectStore } from '@/stores/project';
import { storeToRefs } from 'pinia';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const projectStore = useProjectStore();
const toast = useToast();
const { pms } = storeToRefs(projectStore);

const projectOptions = ref([]);

onMounted(async () => {
  await projectStore.fetchUserPm();
  await projectStore.fetchProjects({});
  projectOptions.value = projectStore.projects.map((p) => ({
    label: p.title,
    value: p.id,
    startDate: p.startDate,
    endDate: p.endDate
  }));
});

const selectedParent = computed(() => projectOptions.value.find((p) => p.value === form.parentId) ?? null);

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
  if (!form.pmId) {
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
  if (!validate()) return;

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

  const id = await projectStore.createProject({
    title: form.title,
    identifier: form.identifier,
    description: form.description,
    startDate: formatDate(form.startDate),
    endDate: formatDate(form.endDate),
    pmId: form.pmId,
    isPublic: form.isPublic ? 'J1' : 'J0',
    parentId: form.parentId
  });

  sessionStorage.setItem(
    'project',
    JSON.stringify({
      selectedProject: {
        id: id
      }
    })
  );

  toast.add({ severity: 'success', summary: '등록 완료', detail: '프로젝트가 등록되었습니다.', life: 2000 });

  projectStore.selectedProject = {
    title: form.title,
    id: id,
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
  <div class="p-8 bg-[#ffffff] h-full">
    <!-- 타이틀 -->
    <div class="mb-4">
      <h1 class="text-2xl font-bold text-[#1A1816]">새 프로젝트 등록</h1>
    </div>

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
              <Select v-model="form.parentId" :options="projectOptions" optionLabel="label" optionValue="value" placeholder="선택" class="form-input w-150" showClear />
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
                <small v-if="selectedParent" class="text-[#9A9B90] mt-1"> 하위 프로젝트는 상위 프로젝트 기간({{ formatDate(selectedParent.startDate) }} ~ {{ formatDate(selectedParent.endDate) }})을 벗어날 수 없습니다. </small>
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
      <Button label="저장" class="btn-amber px-8" @click="handleSubmit" />
      <Button label="취소" class="btn-cancel px-8" @click="handleCancel" />
    </div>
  </div>
</template>

<style scoped>
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
  border-color: #5b6e96 !important;
  box-shadow: 0 0 0 2px rgba(91, 110, 150, 0.15) !important;
}

:deep(.p-textarea) {
  border-color: #c7c7c2 !important;
  background-color: #fafaf8 !important;
}

:deep(.p-textarea:focus) {
  border-color: #5b6e96 !important;
  box-shadow: 0 0 0 2px rgba(91, 110, 150, 0.15) !important;
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
