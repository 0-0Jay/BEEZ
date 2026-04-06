<script setup>
import { useTaskStore } from '@/stores/task';
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'add' }, // 'add' | 'edit'
  data: { type: Object, default: null },
  existingTypes: { type: Array, default: () => [] }
});

const emit = defineEmits(['update:visible', 'save', 'cancel']);
const taskStore = useTaskStore();

// ── 드롭다운 옵션 ─────────────────────────────────────────────
const commonCodes = computed(() => taskStore.commonCodeList);
const workflowOptions = computed(() => commonCodes.value.filter((w) => w.cgroup === '0Q'));

const workflowCopyOptions = computed(() => props.existingTypes.filter((t) => props.mode === 'add' || t.id !== form.value.id).map((t) => ({ name: t.name, value: t.name })));

// ── 폼 상태 ──────────────────────────────────────────────────
const form = ref({ id: null, name: '', defaultStatus: null, description: '', copyFrom: null });

watch(
  () => props.visible,
  (val) => {
    if (val) {
      form.value = props.mode === 'edit' && props.data ? { ...props.data, copyFrom: null } : { id: null, name: '', defaultStatus: null, description: '', copyFrom: null };
    }
  }
);

const isSaveDisabled = computed(() => !form.value.name.trim() || !form.value.defaultStatus);
const modalTitle = computed(() => (props.mode === 'add' ? '일감 유형 추가' : '일감 유형 수정'));

function handleSave() {
  if (isSaveDisabled.value) return;
  const { copyFrom, ...payload } = form.value;
  emit('save', payload);
}
function handleCancel() {
  emit('update:visible', false);
  emit('cancel');
}
</script>

<template>
  <Dialog :visible="visible" :modal="true" :draggable="false" :closable="true" :header="modalTitle" :style="{ width: '520px' }" @update:visible="(val) => emit('update:visible', val)">
    <!-- 폼 영역 -->
    <div class="flex flex-col gap-5">
      <!-- 유형명 -->
      <div class="flex flex-col gap-1.5">
        <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
          유형명
          <span class="text-red-500 inline-block text-xl">*</span>
        </label>
        <InputText v-model="form.name" placeholder="일감 유형 이름을 입력하세요" class="w-full" />
      </div>

      <!-- 초기 상태 -->
      <div class="flex flex-col gap-1.5">
        <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
          초기 상태
          <span class="text-red-500 inline-block text-xl">*</span>
        </label>
        <Select v-model="form.defaultStatus" :options="workflowOptions" option-label="name" option-value="id" placeholder="초기 상태 선택" class="w-full" />
        <p v-if="!form.defaultStatus" class="text-base text-stone-400 mt-0.5">일감 생성 시 기본으로 지정될 상태입니다.</p>
      </div>

      <!-- 설명 -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-stone-600">설명</label>
        <Textarea v-model="form.description" rows="4" placeholder="일감 유형에 대한 설명을 입력하세요 (선택)" class="w-full resize-none" />
      </div>

      <!-- 업무흐름 복사하기 -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-stone-600">업무흐름 복사하기</label>
        <Select v-model="form.copyFrom" :options="[{ name: '복사 안 함', value: null }, ...workflowCopyOptions]" option-label="name" option-value="value" class="w-full" />
        <p class="text-base text-stone-400 mt-0.5">기존 유형의 업무흐름 설정을 가져옵니다.</p>
      </div>

      <!-- 필수 안내 -->
      <p class="text-base text-stone-400 flex items-center gap-1"><span class="text-red-500 inline-block text-xl">*</span> 표시는 필수 입력 항목입니다.</p>
    </div>

    <!-- 버튼 영역 -->
    <template #footer>
      <Button label="저장" raised :disabled="isSaveDisabled" @click="handleSave" />
      <Button label="취소" raised severity="secondary" @click="handleCancel" />
    </template>
  </Dialog>
</template>
