<script setup>
import { useVersionStore } from '@/stores/version';
import { useToast } from 'primevue';
import { reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible', 'saved']);
const versionStore = useVersionStore();
const toast = useToast();
const route = useRoute();
const submitted = ref(false); // 등록 버튼 스위치

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

const statusOptions = [
  { code: 'N0', name: '닫힘', desc: '닫힌 버전' },
  { code: 'N1', name: '진행', desc: '진행 중인 버전' }
];

const handleRegister = async () => {
  submitted.value = true;
  if (!form.name) return;

  try {
    await versionStore.insertVersion({
      ...form,
      startDate: formatDate(form.startDate),
      endDate: formatDate(form.endDate)
    });
    toast.add({ severity: 'success', summary: '등록 완료', detail: '버전이 등록되었습니다.', life: 2000 });
    emit('saved');
    close();
  } catch (error) {
    toast.add({ severity: 'error', summary: '등록 실패', detail: '버전 등록 중 오류가 발생했습니다.', life: 2000 });
  }
};

const close = () => {
  submitted.value = false;
  emit('update:visible', false);
};
</script>

<template>
  <Dialog :visible="visible" header="버전 추가" modal :style="{ width: '600px' }" @update:visible="close">
    <div class="divide-y divide-[#F2F0EB]">
      <!-- 버전명 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">버전명 <span class="text-red-500">*</span></label>
        <div class="flex-1">
          <InputText v-model="form.name" placeholder="버전명을 입력해 주세요." class="w-full form-input" :class="{ 'p-invalid': submitted && !form.name }" />
          <small v-if="submitted && !form.name" class="text-red-500 mt-1 block">값을 입력해 주세요.</small>
        </div>
      </div>

      <!-- 설명 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">설명</label>
        <div class="flex-1">
          <Textarea v-model="form.description" placeholder="버전 설명을 입력해 주세요." class="w-full" rows="3" autoResize />
        </div>
      </div>

      <!-- 기간 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">기간 <span class="text-red-500">*</span></label>
        <div class="flex items-center gap-5">
          <DatePicker v-model="form.startDate" dateFormat="yy-mm-dd" placeholder="시작일" class="form-input w-36" />
          <span class="text-[#6B6B63] ml-14">~</span>
          <DatePicker v-model="form.endDate" dateFormat="yy-mm-dd" placeholder="마감일" class="form-input w-36" />
        </div>
      </div>

      <!-- 상태 -->
      <div class="flex items-start px-8 py-4">
        <label class="form-label w-32 pt-2 shrink-0">상태 <span class="text-red-500">*</span></label>
        <Select v-model="form.status" :options="statusOptions" optionLabel="name" optionValue="code" placeholder="선택" class="form-input w-50" />
      </div>

      <!-- 공유 여부 + 기본 버전 -->
      <div class="flex items-center px-8 py-4">
        <label class="form-label w-32 shrink-0">설정</label>
        <div class="flex gap-40">
          <div class="flex items-center gap-2">
            <Checkbox v-model="form.isShareYn" :binary="true" inputId="isShare" />
            <label for="isShare" class="text-sm text-[#3A3B35] cursor-pointer">공유</label>
          </div>
          <div class="flex items-center gap-2">
            <Checkbox v-model="form.isDefaultYn" :binary="true" inputId="isDefault" />
            <label for="isDefault" class="text-sm text-[#3A3B35] cursor-pointer">기본 버전으로 설정</label>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-2">
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="등록" raised @click="handleRegister" />
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
