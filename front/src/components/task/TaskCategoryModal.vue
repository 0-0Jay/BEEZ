<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'add' }, // 'add' | 'edit'
  data: { type: Object, default: null }
});

const emit = defineEmits(['update:visible', 'save', 'cancel']);

// ── 폼 상태 ──────────────────────────────────────────────────
const form = ref({ id: null, name: '', description: '' });

watch(
  () => props.visible,
  (val) => {
    if (val) {
      form.value = props.mode === 'edit' && props.data ? { ...props.data } : { id: null, name: '', description: '' };
    }
  }
);

const isSaveDisabled = computed(() => !form.value.name.trim());
const modalTitle = computed(() => (props.mode === 'add' ? '일감 범주 추가' : '일감 범주 수정'));

function handleSave() {
  if (isSaveDisabled.value) return;
  emit('save', { ...form.value });
}
function handleCancel() {
  emit('update:visible', false);
  emit('cancel');
}
</script>

<template>
  <!-- pt로 Dialog 내부 header 영역 완전히 숨김 -->
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
      <!-- 모달 헤더 -->
      <div class="flex items-center justify-between px-7 py-5 border-b border-stone-100 bg-stone-50">
        <div class="flex items-center gap-3">
          <span class="w-8 h-8 rounded-lg bg-amber-100 flex items-center justify-center shrink-0">
            <i class="pi pi-folder text-amber-600 text-sm"></i>
          </span>
          <h2 class="text-base font-bold text-stone-900 tracking-tight">{{ modalTitle }}</h2>
        </div>
        <button class="w-7 h-7 rounded-md flex items-center justify-center text-stone-400 hover:text-stone-700 hover:bg-stone-200 transition-colors cursor-pointer bg-transparent border-none" @click="handleCancel">
          <i class="pi pi-times text-sm"></i>
        </button>
      </div>

      <!-- 폼 영역 -->
      <div class="px-7 py-6 flex flex-col gap-5">
        <!-- 범주명 -->
        <div class="flex flex-col gap-1.5">
          <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
            범주명
            <span class="text-red-500 text-base leading-none">•</span>
          </label>
          <input
            v-model="form.name"
            type="text"
            placeholder="일감 범주 이름을 입력하세요"
            class="h-9 px-3 bg-stone-50 border border-stone-200 rounded-lg text-base text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors placeholder:text-stone-300"
          />
        </div>

        <!-- 설명 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-stone-600">설명</label>
          <textarea
            v-model="form.description"
            rows="4"
            placeholder="일감 범주에 대한 설명을 입력하세요 (선택)"
            class="px-3 py-2.5 bg-stone-50 border border-stone-200 rounded-lg text-base text-stone-700 outline-none focus:border-amber-400 focus:bg-amber-50 transition-colors resize-none placeholder:text-stone-300 leading-relaxed"
          ></textarea>
        </div>

        <!-- 필수 안내 -->
        <p class="text-base text-stone-400 flex items-center gap-1"><span class="text-red-500 text-base leading-none">•</span> 표시는 필수 입력 항목입니다.</p>
      </div>

      <!-- 버튼 영역 -->
      <div class="flex items-center justify-end gap-2.5 px-7 py-5 border-t border-stone-100 bg-stone-50">
        <Button label="취소" class="!px-5 !py-2 !text-base !font-semibold !text-stone-500 !bg-stone-100 !border !border-stone-200 !rounded-lg hover:!bg-stone-200 transition-colors !shadow-none" @click="handleCancel" />
        <Button
          label="저장"
          icon="pi pi-check"
          :disabled="isSaveDisabled"
          class="!px-5 !py-2 !text-base !font-bold !text-amber-50 !bg-amber-600 !border-amber-600 !rounded-lg !shadow !shadow-amber-200 hover:!bg-amber-700 transition-colors disabled:!opacity-40 disabled:!cursor-not-allowed"
          @click="handleSave"
        />
      </div>
    </div>
  </Dialog>
</template>
