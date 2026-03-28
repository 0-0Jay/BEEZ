<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  task: { type: Object, default: null } // { name: '...', workflow: '...' }
});

const emit = defineEmits(['update:visible', 'reject', 'cancel']);

const reason = ref('');

watch(
  () => props.visible,
  (val) => {
    if (val) reason.value = '';
  }
);

const hasReason = computed(() => reason.value.trim().length > 0);
const showError = computed(() => !hasReason.value);

function handleConfirm() {
  if (!hasReason.value) return;
  emit('reject', { task: props.task, reason: reason.value });
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
      <!-- 헤더 -->
      <div class="flex items-center justify-between px-7 py-5 border-b border-stone-100 bg-stone-50">
        <div class="flex items-center gap-3">
          <span class="w-8 h-8 rounded-lg bg-amber-100 flex items-center justify-center shrink-0">
            <i class="pi pi-ban text-amber-600 text-sm"></i>
          </span>
          <h2 class="text-base font-bold text-stone-900 tracking-tight">진행상태 반려</h2>
        </div>
        <button class="w-7 h-7 rounded-md flex items-center justify-center text-stone-400 hover:text-stone-700 hover:bg-stone-200 transition-colors cursor-pointer bg-transparent border-none" @click="handleCancel">
          <i class="pi pi-times text-sm"></i>
        </button>
      </div>

      <!-- 본문 -->
      <div class="px-7 py-6 flex flex-col gap-5">
        <!-- 일감 정보 -->
        <div class="flex flex-col gap-2.5">
          <div class="flex items-stretch border border-stone-200 rounded-lg overflow-hidden bg-stone-50 text-sm">
            <span class="px-3.5 py-2.5 font-semibold text-stone-500 border-r border-stone-200 min-w-[110px] shrink-0">일감명</span>
            <span class="px-3.5 py-2.5 text-stone-700">{{ task?.name }}</span>
          </div>
          <div class="flex items-stretch border border-stone-200 rounded-lg overflow-hidden bg-stone-50 text-sm">
            <span class="px-3.5 py-2.5 font-semibold text-stone-500 border-r border-stone-200 min-w-[110px] shrink-0">요청 진행상태</span>
            <span class="px-3.5 py-2.5 text-stone-700 flex items-center gap-1.5">
              {{ task?.workflow }}
            </span>
          </div>
        </div>

        <!-- 반려사유 -->
        <div class="flex flex-col gap-1.5">
          <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
            반려사유
            <span class="text-red-500 text-base leading-none">•</span>
          </label>
          <textarea
            v-model="reason"
            rows="4"
            placeholder="반려사유를 입력해 주세요"
            :class="[
              'px-3 py-2.5 border rounded-lg text-base text-stone-700 outline-none resize-none transition-colors placeholder:text-stone-300 leading-relaxed',
              hasReason ? 'border-amber-400 bg-amber-50' : 'border-stone-200 bg-stone-50 focus:border-amber-400 focus:bg-amber-50'
            ]"
          ></textarea>
          <p v-if="showError" class="text-sm text-red-500 flex items-center gap-1">
            <i class="pi pi-exclamation-circle text-xs"></i>
            반려 사유는 반드시 입력해 주세요.
          </p>
        </div>

        <!-- 확인 문구 -->
        <p class="text-center text-base font-bold text-stone-700">정말 반려하시겠습니까?</p>
      </div>

      <!-- 푸터 -->
      <div class="flex items-center justify-end gap-2.5 px-7 py-5 border-t border-stone-100 bg-stone-50">
        <Button label="취소" class="!px-5 !py-2 !text-base !font-semibold !text-stone-500 !bg-stone-100 !border !border-stone-200 !rounded-lg hover:!bg-stone-200 transition-colors !shadow-none" @click="handleCancel" />
        <Button
          label="반려"
          icon="pi pi-ban"
          :disabled="!hasReason"
          class="!px-5 !py-2 !text-base !font-bold !text-amber-50 !bg-amber-600 !border-amber-600 !rounded-lg !shadow !shadow-amber-200 hover:!bg-amber-700 transition-colors disabled:!opacity-40 disabled:!cursor-not-allowed"
          @click="handleConfirm"
        />
      </div>
    </div>
  </Dialog>
</template>
