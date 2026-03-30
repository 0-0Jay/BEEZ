<!-- component/ConfirmDialog.vue -->
<script setup>
// 1. 부모에게서 받을 값
const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  confirmLabel: {
    type: String,
    default: '확인'
  },
  cancelLabel: {
    type: String,
    default: '취소'
  }
});

// 2. 부모에게 알릴 이벤트
const emit = defineEmits(['confirm', 'update:visible']);
</script>
<template>
  <Dialog header="확인" :visible="props.visible" modal :style="{ width: '350px' }" :closable="false" @update:visible="emit('update:visible', $event)">
    <div class="flex items-center justify-center">
      <i class="pi pi-exclamation-triangle mr-4" style="font-size: 2rem" />
      <slot />
    </div>
    <template #footer>
      <Button :label="confirmLabel" icon="pi pi-check" @click="emit('confirm')" severity="danger" outlined autofocus />
      <Button :label="cancelLabel" icon="pi pi-times" @click="emit('update:visible', false)" text severity="secondary" />
    </template>
  </Dialog>
</template>
