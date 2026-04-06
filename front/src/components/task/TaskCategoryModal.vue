<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'add' }, // 'add' | 'edit'
  data: { type: Object, default: null }
});

const emit = defineEmits(['update:visible', 'save', 'cancel']);

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
  <Dialog :visible="visible" :modal="true" :draggable="false" :closable="true" :header="modalTitle" :style="{ width: '480px' }" @update:visible="(val) => emit('update:visible', val)">
    <!-- 폼 영역 -->
    <div class="flex flex-col gap-5">
      <!-- 범주명 -->
      <div class="flex flex-col gap-1.5">
        <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
          범주명
          <span class="text-red-500 inline-block text-xl">*</span>
        </label>
        <InputText v-model="form.name" placeholder="일감 범주 이름을 입력하세요" class="w-full" />
      </div>

      <!-- 설명 -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-stone-600">설명</label>
        <Textarea v-model="form.description" rows="4" placeholder="일감 범주에 대한 설명을 입력하세요 (선택)" class="w-full resize-none" />
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
