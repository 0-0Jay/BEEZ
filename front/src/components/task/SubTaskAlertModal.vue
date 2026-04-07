<script setup>
import Button from 'primevue/button';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['confirm', 'cancel']);
</script>

<template>
  <Dialog
    :visible="visible"
    :modal="true"
    :draggable="false"
    :closable="true"
    header="하위 일감 추가 경고"
    :style="{ width: '510px' }"
    @update:visible="
      (val) => {
        if (!val) emit('cancel');
      }
    "
  >
    <!-- 모달 본문 -->
    <div class="flex flex-col gap-5">
      <!-- 안내 문구 -->
      <p class="text-base text-[#3A3B35]">하위 일감을 추가하면 상위 일감의 진척도가 하위 일감을 기준으로 자동 계산됩니다.</p>
      <p class="text-lg text-[#3A3B35] text-center">정말 하위 일감을 추가하시겠습니까?</p>

      <!-- 경고 문구 -->
      <div class="flex items-start gap-2">
        <i class="pi pi-exclamation-circle text-orange-500 mt-1"></i>
        <p class="text-base text-orange-500">기존 진척도는 저장되며, 하위 일감을 모두 삭제하면 복구됩니다.</p>
      </div>
    </div>

    <!-- 버튼 영역 -->
    <template #footer>
      <Button label="생성" raised @click="emit('confirm')" />
      <Button label="취소" raised severity="secondary" @click="emit('cancel')" />
    </template>
  </Dialog>
</template>
