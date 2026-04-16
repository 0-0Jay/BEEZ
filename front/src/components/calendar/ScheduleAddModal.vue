<script setup>
const props = defineProps({
  visible: Boolean,
  event: Object, // CalendarRequest 형태
  typeOptions: Array
});

const emit = defineEmits(['update:visible', 'update:event', 'save']);

const updateField = (field, value) => {
  emit('update:event', { ...props.event, [field]: value });
};

const handleSave = () => {
  emit('save');
};
</script>

<template>
  <Dialog :visible="visible" @update:visible="$emit('update:visible', $event)" modal header="일정 추가" :style="{ width: '400px' }" :closable="true" :draggable="false">
    <div class="flex flex-col gap-4 py-2">
      <!-- 제목 -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-gray-500">제목 <span class="text-red-400">*</span></label>
        <InputText :value="event?.title" placeholder="일정 제목을 입력하세요" class="w-full" @input="updateField('title', $event.target.value)" />
      </div>

      <!-- 시작일 / 종료일 -->
      <div class="grid grid-cols-2 gap-3">
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-gray-500">시작일 <span class="text-red-400">*</span></label>
          <DatePicker :value="event?.start" dateFormat="yy-mm-dd" :maxDate="event?.end" placeholder="시작일" class="w-full" @update:modelValue="updateField('start', $event)" />
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-gray-500">종료일</label>
          <DatePicker :value="event?.end" dateFormat="yy-mm-dd" :minDate="event?.start" placeholder="종료일" class="w-full" @update:modelValue="updateField('end', $event)" />
        </div>
      </div>

      <!-- 유형 (DTO: type) — 관리자만 노출 -->
      <div v-if="typeOptions?.length > 1" class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-gray-500">유형</label>
        <SelectButton :value="event?.type" :options="typeOptions" optionLabel="label" optionValue="value" class="w-full" @update:modelValue="updateField('type', $event)" />
      </div>

      <!-- 메모 (DTO: content) -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-gray-500">메모</label>
        <Textarea :value="event?.content" placeholder="메모 (선택)" rows="2" class="w-full resize-none" @input="updateField('content', $event.target.value)" />
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2">
        <Button label="저장" :disabled="!event?.title || !event?.start" @click="handleSave" />
        <Button label="취소" severity="secondary" @click="$emit('update:visible', false)" />
      </div>
    </template>
  </Dialog>
</template>
