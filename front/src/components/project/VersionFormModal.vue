<script setup>
import { useProjectStore } from '@/stores/project';
import { useVersionStore } from '@/stores/version';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  visible: { type: Boolean, required: true },
  editData: { type: Object, default: null }
});

const versionStore = useVersionStore();
const projectStore = useProjectStore();
const commonCodeList = computed(() => versionStore.commonCodeList);
const isEdit = computed(() => !!props.editData);
const emit = defineEmits(['update:visible', 'saved']);
const toast = useToast();
const route = useRoute();
const submitted = ref(false); // 등록 버튼 스위치

const projectStart = computed(() => (projectStore.selectedProject?.startDate ? new Date(projectStore.selectedProject.startDate) : null));
const projectEnd = computed(() => (projectStore.selectedProject?.endDate ? new Date(projectStore.selectedProject.endDate) : null));

const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

const form = reactive({
  projectId: route.params.id,
  name: '',
  description: '',
  startDate: null,
  endDate: null,
  status: null,
  isShareYn: false,
  isDefaultYn: false
});

const errors = reactive({
  name: '',
  startDate: '',
  endDate: '',
  status: ''
});

const statusOptions = computed(() => commonCodeList.value.filter((c) => c.cgroup === '0N'));

watch(
  () => [props.visible, props.editData],
  ([newVisible, newEditData]) => {
    if (newVisible && newEditData) {
      Object.assign(form, {
        ...newEditData,
        startDate: newEditData.startDate ? new Date(newEditData.startDate) : null,
        endDate: newEditData.endDate ? new Date(newEditData.endDate) : null,
        isShareYn: newEditData.isShare === 'O1',
        isDefaultYn: newEditData.isDefault === 'M1' || newEditData.isDefault === true
      });
    } else if (newVisible) {
      resetForm();
    }
  }
);

// 기본 버전 체크 시 공유 + 상태 자동 설정
watch(
  () => form.isDefaultYn,
  (checked) => {
    if (checked) {
      form.isShareYn = true;
      form.status = 'N1';
    }
  }
);

const resetForm = () => {
  Object.assign(form, {
    projectId: route.params.id,
    name: '',
    description: '',
    startDate: null,
    endDate: null,
    status: null,
    isShareYn: false,
    isDefaultYn: false
  });
  submitted.value = false;
};

const validate = () => {
  errors.name = '';
  errors.startDate = '';
  errors.endDate = '';
  errors.status = '';
  let valid = true;

  if (!form.name.trim()) {
    errors.name = '버전명을 입력해주세요.';
    valid = false;
  }
  if (!form.status) {
    errors.status = '상태를 선택해주세요.';
    valid = false;
  }
  if (!form.startDate) {
    errors.startDate = '시작일을 입력해주세요.';
    valid = false;
  } else if (projectStart.value && form.startDate < projectStart.value) {
    errors.startDate = `프로젝트 시작일(${formatDate(projectStart.value)}) 이후여야 합니다.`;
    valid = false;
  }
  if (!form.endDate) {
    errors.endDate = '마감일을 입력해주세요.';
    valid = false;
  } else if (projectEnd.value && form.endDate > projectEnd.value) {
    errors.endDate = `프로젝트 마감일(${formatDate(projectEnd.value)}) 이전이어야 합니다.`;
    valid = false;
  }

  return valid;
};

const handleRegister = async () => {
  if (!validate()) return;

  try {
    const payload = {
      ...form,
      startDate: formatDate(form.startDate),
      endDate: formatDate(form.endDate)
    };

    if (isEdit.value) {
      await versionStore.updateVersion(props.editData.id, payload);
      toast.add({ severity: 'success', summary: '수정 완료', detail: '버전이 수정되었습니다.', life: 2000 });
    } else {
      await versionStore.insertVersion(payload);
      toast.add({ severity: 'success', summary: '등록 완료', detail: '버전이 등록되었습니다.', life: 2000 });
    }
    emit('saved');
    close();
  } catch (error) {
    toast.add({ severity: 'error', summary: '실패', detail: error.message, life: 2000 });
  }
};

const close = () => {
  submitted.value = false;
  emit('update:visible', false);
};

onMounted(async () => {
  await versionStore.findCommonCodeList();
});
</script>

<template>
  <Dialog :visible="visible" :header="isEdit ? '버전 수정' : '버전 추가'" modal :style="{ width: '700px' }" @update:visible="close">
    <div class="divide-y divide-[#F2F0EB]">
      <!-- 버전명 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">버전명 <span class="text-red-500">*</span></label>
        <div class="flex-1">
          <InputText v-model="form.name" placeholder="버전명을 입력해 주세요." class="w-full form-input" />
          <small v-if="errors.name" class="text-red-500 mt-1 block">{{ errors.name }}</small>
        </div>
      </div>

      <!-- 설명 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">설명</label>
        <div class="flex-1">
          <Textarea v-model="form.description" placeholder="버전 설명을 입력해 주세요." class="w-full" rows="3" autoResize />
        </div>
      </div>

      <!-- 공유 여부 + 기본 버전 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 shrink-0 pt-1">설정</label>
        <div class="flex flex-col gap-1">
          <div class="flex gap-40">
            <div class="flex items-center gap-2">
              <Checkbox v-model="form.isDefaultYn" :binary="true" inputId="isDefault" />
              <label for="isDefault" class="text-sm text-[#3A3B35] cursor-pointer">기본 버전으로 설정</label>
            </div>
            <div class="flex items-center gap-2">
              <Checkbox v-model="form.isShareYn" :binary="true" inputId="isShare" :disabled="form.isDefaultYn" />
              <label for="isShare" class="text-sm text-[#3A3B35] cursor-pointer">공유</label>
            </div>
          </div>
          <small v-if="form.isDefaultYn" class="text-red-400">기본 버전은 필수로 공유됩니다.</small>
        </div>
      </div>

      <!-- 상태 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">상태<span class="text-red-500">*</span></label>
        <div class="flex flex-col gap-1">
          <Select v-model="form.status" :options="statusOptions" optionLabel="name" optionValue="id" placeholder="선택" class="form-input w-50" :disabled="form.isDefaultYn" />
          <small v-if="form.isDefaultYn" class="text-red-400 mt-1 block">기본 버전은 진행 상태로 고정됩니다.</small>
          <small v-if="errors.status" class="text-red-500 mt-1 block">{{ errors.status }}</small>
        </div>
      </div>

      <!-- 기간 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">기간 <span class="text-red-500">*</span></label>
        <div class="flex items-start gap-5">
          <div class="flex flex-col w-42">
            <DatePicker v-model="form.startDate" dateFormat="yy-mm-dd" placeholder="시작일" class="form-input w-50" :minDate="projectStart" :maxDate="form.endDate || projectEnd" showIcon inputClass="w-full" />
            <small class="text-red-400 mt-2 block w-100"> 버전은 프로젝트 기간({{ formatDate(projectStart) }} ~ {{ formatDate(projectEnd) }}) 내에 설정해주세요. </small>
            <small v-if="errors.startDate" class="text-red-500 block">{{ errors.startDate }}</small>
          </div>
          <span class="text-[#6B6B63] pt-2 shrink-0">~</span>
          <div class="flex flex-col w-42">
            <DatePicker v-model="form.endDate" dateFormat="yy-mm-dd" placeholder="마감일" class="form-input w-50" :minDate="form.startDate || projectStart" :maxDate="projectEnd" showIcon inputClass="w-full" />
            <small v-if="errors.endDate" class="text-red-500 block">{{ errors.endDate }}</small>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-2">
        <Button :label="isEdit ? '수정' : '저장'" raised @click="handleRegister" />
        <Button label="취소" severity="secondary" raised @click="close" />
      </div>
    </template>
  </Dialog>
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

:deep(.form-input:focus) {
  border-color: #e8920e !important;
  box-shadow: 0 0 0 2px rgba(232, 146, 14, 0.15) !important;
}

:deep(.p-select-label) {
  display: flex !important;
  align-items: center !important;
}

:deep(.p-textarea) {
  border-color: #c7c7c2 !important;
  background-color: #fafaf8 !important;
}

:deep(.p-textarea:focus) {
  border-color: #e8920e !important;
  box-shadow: 0 0 0 2px rgba(232, 146, 14, 0.15) !important;
}

:deep(.p-radiobutton .p-radiobutton-box.p-highlight) {
  border-color: #f5a623;
  background: #f5a623;
}
</style>
