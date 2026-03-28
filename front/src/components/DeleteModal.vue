<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  target: { type: Object, default: null }
});

const emit = defineEmits(['update:visible', 'confirm', 'cancel']);

const inputValue = ref('');

watch(
  () => props.visible,
  (val) => {
    if (val) inputValue.value = '';
  }
);

const isMatched = computed(() => inputValue.value === props.target?.name);
const showError = computed(() => inputValue.value.length > 0 && !isMatched.value);

function handleConfirm() {
  if (!isMatched.value) return;
  emit('delete', props.target);
}
function handleCancel() {
  emit('update:visible', false);
  emit('cancel');
}
</script>

<template>
  <Dialog
    :visible="visible"
    :modal="true"
    :draggable="false"
    :closable="false"
    :style="{ width: '480px', padding: '0' }"
    :pt="{
      root: { class: 'rounded-xl overflow-hidden shadow-2xl shadow-stone-300/60 !p-0' },
      header: { style: 'display:none' },
      content: { class: '!p-0' },
      footer: { style: 'display:none' }
    }"
    @update:visible="(val) => emit('update:visible', val)"
  >
    <div class="bg-white rounded-xl overflow-hidden">
      <div class="flex items-center justify-between px-7 py-5 border-b border-stone-100 bg-stone-50">
        <div class="flex items-center gap-3">
          <span class="w-8 h-8 rounded-lg bg-red-100 flex items-center justify-center shrink-0">
            <i class="pi pi-trash text-red-600 text-sm"></i>
          </span>
          <h2 class="text-base font-bold text-stone-900 tracking-tight">삭제 확인</h2>
        </div>
        <button class="w-7 h-7 rounded-md flex items-center justify-center text-stone-400 hover:text-stone-700 hover:bg-stone-200 transition-colors cursor-pointer bg-transparent border-none" @click="handleCancel">
          <i class="pi pi-times text-sm"></i>
        </button>
      </div>

      <div class="px-7 py-6 flex flex-col gap-5">
        <div class="flex flex-col gap-1">
          <p class="text-base text-stone-600 leading-relaxed">
            정말 삭제하시려면 아래에
            <strong class="text-stone-900 font-semibold">{{ target?.name }}</strong
            >을(를) 입력해주세요.
          </p>
          <p class="text-sm text-red-500 flex items-center gap-1">
            <i class="pi pi-exclamation-circle text-xs"></i>
            이 작업은 되돌릴 수 없습니다.
          </p>
        </div>

        <div class="flex flex-col gap-1.5">
          <input
            v-model="inputValue"
            type="text"
            :placeholder="target?.name"
            :class="[
              'h-9 px-3 border rounded-lg text-base text-stone-700 outline-none transition-colors',
              isMatched ? 'border-green-400 bg-green-50' : showError ? 'border-red-400 bg-red-50' : 'border-stone-200 bg-stone-50 focus:border-red-400 focus:bg-red-50'
            ]"
          />
          <p v-if="showError" class="text-sm text-red-500 flex items-center gap-1">
            <i class="pi pi-exclamation-circle text-xs"></i>
            이름을 정확하게 입력해 주세요.
          </p>
        </div>
      </div>

      <div class="flex items-center justify-end gap-2.5 px-7 py-5 border-t border-stone-100 bg-stone-50">
        <Button label="취소" class="!px-5 !py-2 !text-base !font-semibold !text-stone-500 !bg-stone-100 !border !border-stone-200 !rounded-lg hover:!bg-stone-200 transition-colors !shadow-none" @click="handleCancel" />
        <Button
          label="삭제"
          :disabled="!isMatched"
          class="!px-5 !py-2 !text-base !font-bold !text-white !bg-red-600 !border-red-600 !rounded-lg !shadow !shadow-red-200 hover:!bg-red-700 transition-colors disabled:!opacity-40 disabled:!cursor-not-allowed"
          @click="handleConfirm"
        />
      </div>
    </div>
  </Dialog>
</template>
