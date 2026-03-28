<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  task: { type: Object, default: null } // { name: '...' }
});

const emit = defineEmits(['update:visible', 'confirm', 'cancel']);

const STATUS_OPTIONS = [
  { label: '신규', value: '신규', color: '#6366F1' },
  { label: '진행', value: '진행', color: '#D97706' },
  { label: '해결', value: '해결', color: '#16A34A' },
  { label: '완료', value: '완료', color: '#2563EB' },
  { label: '반려', value: '반려', color: '#DC2626' }
];

const selectedStatus = ref('');

watch(
  () => props.visible,
  (val) => {
    if (val) selectedStatus.value = '';
  }
);

const hasStatus = computed(() => !!selectedStatus.value);
const statusColor = computed(() => STATUS_OPTIONS.find((s) => s.value === selectedStatus.value)?.color ?? '');

function handleConfirm() {
  if (!hasStatus.value) return;
  emit('confirm', { task: props.task, status: selectedStatus.value });
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
          <span class="w-8 h-8 rounded-lg bg-blue-100 flex items-center justify-center shrink-0">
            <i class="pi pi-arrow-right text-blue-600 text-sm"></i>
          </span>
          <h2 class="text-base font-bold text-stone-900 tracking-tight">진행상태 변경</h2>
        </div>
        <button class="w-7 h-7 rounded-md flex items-center justify-center text-stone-400 hover:text-stone-700 hover:bg-stone-200 transition-colors cursor-pointer bg-transparent border-none" @click="handleCancel">
          <i class="pi pi-times text-sm"></i>
        </button>
      </div>

      <!-- 본문 -->
      <div class="px-7 py-6 flex flex-col gap-5">
        <!-- 일감명 -->
        <div class="flex items-stretch border border-stone-200 rounded-lg overflow-hidden bg-stone-50 text-sm">
          <span class="px-3.5 py-2.5 font-semibold text-stone-500 border-r border-stone-200 min-w-[110px] shrink-0">일감명</span>
          <span class="px-3.5 py-2.5 text-stone-700">{{ task?.name }}</span>
        </div>

        <!-- 진행상태 드롭다운 -->
        <div class="flex flex-col gap-1.5">
          <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
            진행상태
            <span class="text-red-500 text-base leading-none">•</span>
          </label>
          <div class="relative">
            <span v-if="hasStatus" class="absolute left-3 top-1/2 -translate-y-1/2 w-2 h-2 rounded-full pointer-events-none z-10" :style="{ background: statusColor }"></span>
            <select
              v-model="selectedStatus"
              :class="[
                'h-9 w-full border rounded-lg text-base outline-none appearance-none cursor-pointer transition-colors pr-8',
                hasStatus ? 'pl-7 border-blue-400 bg-blue-50 text-stone-700' : 'pl-3 border-stone-200 bg-stone-50 text-stone-400 focus:border-blue-400 focus:bg-blue-50'
              ]"
            >
              <option value="" disabled>진행상태를 선택하세요</option>
              <option v-for="opt in STATUS_OPTIONS" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
            <i class="pi pi-chevron-down absolute right-3 top-1/2 -translate-y-1/2 text-stone-400 text-xs pointer-events-none"></i>
          </div>
        </div>

        <!-- 확인 문구 -->
        <p class="text-center text-base font-bold text-stone-700">진행 상태를 변경하시겠습니까?</p>
      </div>

      <!-- 푸터 -->
      <div class="flex items-center justify-end gap-2.5 px-7 py-5 border-t border-stone-100 bg-stone-50">
        <Button label="취소" class="!px-5 !py-2 !text-base !font-semibold !text-stone-500 !bg-stone-100 !border !border-stone-200 !rounded-lg hover:!bg-stone-200 transition-colors !shadow-none" @click="handleCancel" />
        <Button
          label="확인"
          icon="pi pi-check"
          :disabled="!hasStatus"
          class="!px-5 !py-2 !text-base !font-bold !text-white !bg-blue-600 !border-blue-600 !rounded-lg !shadow !shadow-blue-200 hover:!bg-blue-700 transition-colors disabled:!opacity-40 disabled:!cursor-not-allowed"
          @click="handleConfirm"
        />
      </div>
    </div>
  </Dialog>
</template>
