<script setup>
import { useProjectStore } from '@/stores/project';
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  taskId: { type: String, default: '' },
  title: { type: String, default: '' }
});

const emit = defineEmits(['update:visible', 'confirm', 'cancel']);
const projectStore = useProjectStore();
const projectId = computed(() => projectStore.selectedProject?.id);
const inputValue = ref('');

watch(
  () => props.visible,
  (val) => {
    if (val) inputValue.value = '';
  }
);

const isMatched = computed(() => inputValue.value === props.title);
const showError = computed(() => inputValue.value.length > 0 && !isMatched.value);

function handleConfirm() {
  if (!isMatched.value) return;
  emit('confirm', { id: props.taskId, projectId: projectId.value });
}
function handleCancel() {
  emit('update:visible', false);
  emit('cancel');
}
</script>

<template>
  <Dialog :visible="visible" :modal="true" :draggable="false" :closable="true" header="삭제 확인" :style="{ width: '480px' }" @update:visible="(val) => emit('update:visible', val)">
    <div class="flex flex-col gap-5">
      <p class="text-base text-stone-600 leading-relaxed">
        정말 삭제하시려면 아래에
        <strong class="text-stone-900 font-semibold">{{ title }}</strong
        >을(를) 입력해주세요.
      </p>

      <div class="flex flex-col gap-1.5">
        <InputText v-model="inputValue" :placeholder="title" :invalid="showError" class="w-full" />
        <p v-if="showError" class="text-sm text-red-500">이름을 정확하게 입력해 주세요.</p>
        <div class="flex items-start gap-2 pt-5">
          <i class="pi pi-exclamation-circle text-red-500 mt-1"></i>
          <p class="text-base text-red-500">이 일감에 소속된 모든 하위 일감이 함께 삭제됩니다.</p>
        </div>
        <div class="flex items-start gap-2">
          <i class="pi pi-exclamation-circle text-red-500 mt-1"></i>
          <p class="text-base text-red-500">이 작업은 되돌릴 수 없습니다.</p>
        </div>
      </div>
    </div>

    <template #footer>
      <Button label="삭제" raised severity="danger" :disabled="!isMatched" @click="handleConfirm" />
      <Button label="취소" raised severity="secondary" @click="handleCancel" />
    </template>
  </Dialog>
</template>
