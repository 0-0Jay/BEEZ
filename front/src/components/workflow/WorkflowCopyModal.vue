<script setup>
import { useRolesStore } from '@/stores/roles';
import { useTaskStore } from '@/stores/task';
import { useWorkflowStore } from '@/stores/workflow';
import { useToast } from 'primevue';
import { onMounted, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible', 'copied']);

const wStore = useWorkflowStore();
const rStore = useRolesStore();
const tStore = useTaskStore();

const toast = useToast();

const issueTypeOptions = ref([]);
const roleOptions = ref([]);

// 원본
const sourceIssueType = ref(null);
const sourceRole = ref(null);

// 대상
const targetIssueTypes = ref([]);
const targetRoles = ref([]);

const submitted = ref(false);

const resetForm = () => {
  sourceIssueType.value = null;
  sourceRole.value = null;
  targetIssueTypes.value = [];
  targetRoles.value = [];
  submitted.value = false;
};

onMounted(async () => {
  // 역할 및 일감 유형 목록 가져오기
  await Promise.all([rStore.findRoles(), tStore.findTypeList()]);

  roleOptions.value = roleOptions.value = rStore.roleList.map((role) => ({
    label: role.name,
    value: role.id
  }));
  issueTypeOptions.value = tStore.typeList.map((type) => ({
    label: type.name,
    value: type.id
  }));
});

watch(
  () => props.visible,
  (newVal) => {
    if (!newVal) resetForm();
  }
);

const close = () => {
  emit('update:visible', false);
};

const handleCopy = async () => {
  submitted.value = true;

  if (!sourceIssueType.value || !sourceRole.value) return;
  if (!targetIssueTypes.value.length || !targetRoles.value.length) return;

  try {
    // 백엔드 프로시저가 단일 대상만 처리한다면 루프를 돌아야 할 수도 있고,
    // 리스트를 다 받는 API라면 한 번에 던지면 돼.
    const payload = {
      source: { roleId: sourceRole.value, typeId: sourceIssueType.value },
      targets: {
        roleIds: targetRoles.value,
        typeIds: targetIssueTypes.value
      }
    };

    await wStore.copyWorkflow(payload); // 스토어에 복사 액션 추가 필요

    toast.add({ severity: 'success', summary: '복사 성공', detail: '업무흐름이 복사되었습니다.' });
    emit('copied');
    close();
  } catch (error) {
    // 에러 처리...
  }
};
</script>

<template>
  <Dialog :visible="visible" header="업무흐름 복사" modal :style="{ width: '480px' }" @update:visible="close">
    <div class="flex flex-col gap-5 pt-1">
      <div class="bg-amber-50 border border-amber-200 border-l-4 border-l-amber-400 rounded-r-lg px-4 py-3">
        <p class="flex items-center gap-2 text-xs font-medium text-amber-700">
          <i class="pi pi-info-circle text-amber-500" />
          원본의 설정을 선택한 대상에게 동일하게 복사합니다.
        </p>
      </div>

      <div class="border border-stone-200 rounded-xl overflow-hidden">
        <div class="bg-stone-50 border-b border-stone-200 px-4 py-2">
          <span class="text-xs font-semibold text-stone-400 uppercase tracking-widest">원본</span>
        </div>
        <div class="flex flex-col gap-4 px-5 py-4">
          <div class="flex items-center gap-4">
            <label class="text-sm font-semibold text-stone-600 w-20 text-right shrink-0"> 일감 유형 </label>
            <div class="flex-1">
              <Select
                v-model="sourceIssueType"
                :options="issueTypeOptions"
                option-label="label"
                option-value="value"
                placeholder="--- 선택하세요 ---"
                class="w-full"
                :class="{
                  'p-invalid': submitted && !sourceIssueType,
                  '!border-[#E8920E]': submitted && !sourceIssueType
                }"
              />
              <small v-if="submitted && !sourceIssueType" class="flex items-center gap-1 mt-1 text-red-500 text-xs">
                <i class="pi pi-exclamation-circle text-xs" />
                값을 선택해 주세요.
              </small>
            </div>
          </div>

          <div class="flex items-center gap-4">
            <label class="text-sm font-semibold text-stone-600 w-20 text-right shrink-0"> 역할 </label>
            <div class="flex-1">
              <Select
                v-model="sourceRole"
                :options="roleOptions"
                option-label="label"
                option-value="value"
                placeholder="--- 선택하세요 ---"
                class="w-full"
                :class="{
                  'p-invalid': submitted && !sourceRole,
                  '!border-[#E8920E]': submitted && !sourceRole
                }"
              />
              <small v-if="submitted && !sourceRole" class="flex items-center gap-1 mt-1 text-red-500 text-xs">
                <i class="pi pi-exclamation-circle text-xs" />
                값을 선택해 주세요.
              </small>
            </div>
          </div>
        </div>
      </div>

      <div class="flex items-center gap-3">
        <div class="flex-1 h-px bg-stone-200" />
        <div class="flex items-center gap-1.5 text-xs text-stone-400 font-medium">
          <i class="pi pi-arrow-down text-amber-400 text-sm" />
          아래 대상에게 복사
        </div>
        <div class="flex-1 h-px bg-stone-200" />
      </div>

      <div class="border border-stone-200 rounded-xl overflow-hidden">
        <div class="bg-stone-50 border-b border-stone-200 px-4 py-2">
          <span class="text-xs font-semibold text-stone-400 uppercase tracking-widest">대상</span>
        </div>
        <div class="flex flex-col gap-4 px-5 py-4">
          <div class="flex items-start gap-4">
            <label class="text-sm font-semibold text-stone-600 w-20 text-right shrink-0 pt-2"> 일감 유형 </label>
            <div class="flex-1">
              <MultiSelect
                v-model="targetIssueTypes"
                :options="issueTypeOptions"
                option-label="label"
                option-value="value"
                placeholder="--- 선택하세요 ---"
                display="chip"
                class="w-full"
                :class="{
                  'p-invalid': submitted && !targetIssueTypes.length,
                  '!border-[#E8920E]': submitted && !targetIssueTypes.length
                }"
              />
              <small v-if="submitted && !targetIssueTypes.length" class="flex items-center gap-1 mt-1 text-red-500 text-xs">
                <i class="pi pi-exclamation-circle text-xs" />
                하나 이상 선택해 주세요.
              </small>
            </div>
          </div>

          <div class="flex items-start gap-4">
            <label class="text-sm font-semibold text-stone-600 w-20 text-right shrink-0 pt-2"> 역할 </label>
            <div class="flex-1">
              <MultiSelect
                v-model="targetRoles"
                :options="roleOptions"
                option-label="label"
                option-value="value"
                placeholder="--- 선택하세요 ---"
                display="chip"
                class="w-full"
                :class="{
                  'p-invalid': submitted && !targetRoles.length,
                  '!border-[#E8920E]': submitted && !targetRoles.length
                }"
              />
              <small v-if="submitted && !targetRoles.length" class="flex items-center gap-1 mt-1 text-red-500 text-xs">
                <i class="pi pi-exclamation-circle text-xs" />
                하나 이상 선택해 주세요.
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="복사" raised @click="handleCopy" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
:deep(.p-select),
:deep(.p-multiselect) {
  border-radius: 8px;
}

:deep(.p-select:hover),
:deep(.p-multiselect:hover) {
  border-color: #c8c4b8;
}

:deep(.p-select.p-focus),
:deep(.p-multiselect.p-focus) {
  border-color: #f5a623;
  box-shadow: 0 0 0 3px rgba(245, 166, 35, 0.15);
}

:deep(.p-multiselect-chip) {
  background-color: #fef3c7;
  color: #92400e;
  border-radius: 6px;
  font-size: 12px;
}

:deep(.p-select.p-invalid),
:deep(.p-multiselect.p-invalid) {
  border-color: #e8920e;
}
</style>
