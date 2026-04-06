<script setup>
import Button from 'primevue/button';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  currentTask: {
    type: Object,
    default: null
  },
  linkedTask: {
    type: Object,
    default: null
  },
  relationLabel: {
    type: String,
    default: '-'
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
    header="일감 연결 끊기"
    :style="{ width: '480px' }"
    @update:visible="
      (val) => {
        if (!val) emit('cancel');
      }
    "
  >
    <!-- 모달 본문 -->
    <div class="flex flex-col gap-5">
      <!-- 안내 문구 -->
      <p class="text-base text-[#3A3B35]">정말 이 일감과 연결을 끊으시겠습니까?</p>

      <!-- 연결 정보 카드 -->
      <div class="rounded-lg border border-[#E5E4DF] bg-[#FAFAF8] overflow-hidden">
        <!-- 현재 일감 -->
        <div class="px-4 py-3 flex items-start gap-3 border-b border-[#F2F0EB]">
          <span class="mt-2.5 shrink-0 text-base font-semibold px-1.5 py-0.5 rounded bg-[#E8920E]/10 text-[#E8920E] border border-[#E8920E]/20">현재</span>
          <div class="min-w-0">
            <span class="block text-base font-mono text-[#9A9B90] mb-0.5">{{ currentTask?.id ?? '-' }}</span>
            <span class="block text-base font-semibold text-[#1A1816] truncate">{{ currentTask?.title ?? '-' }}</span>
          </div>
        </div>

        <!-- 관계 표시 -->
        <div class="px-4 py-2 flex items-center gap-2 bg-[#F8F7F4] border-b border-[#F2F0EB]">
          <div class="flex-1 h-px bg-[#E5E4DF]"></div>
          <span class="flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-white border border-[#E5E4DF] text-base font-semibold text-[#6B6B63]">
            <i class="pi pi-link"></i>
            {{ relationLabel }}
          </span>
          <div class="flex-1 h-px bg-[#E5E4DF]"></div>
        </div>

        <!-- 연결된 일감 -->
        <div class="px-4 py-3 flex items-start gap-3">
          <span class="mt-2.5 shrink-0 text-base font-semibold px-1.5 py-0.5 rounded bg-[#6B6B63]/10 text-[#6B6B63] border border-[#6B6B63]/20">연결</span>
          <div class="min-w-0">
            <span class="block text-base font-mono text-[#9A9B90] mb-0.5">{{ linkedTask?.id ?? '-' }}</span>
            <span class="block text-base font-semibold text-[#1A1816] truncate">{{ linkedTask?.title ?? '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 경고 문구 -->
      <div class="flex items-start gap-2">
        <i class="pi pi-exclamation-circle text-red-500 mt-1"></i>
        <p class="text-base text-red-500">이 작업은 되돌릴 수 없습니다.</p>
      </div>
    </div>

    <!-- 버튼 영역 -->
    <template #footer>
      <Button label="끊기" raised severity="danger" @click="emit('confirm', linkedTask?.relationId)" />
      <Button label="취소" raised severity="secondary" @click="emit('cancel')" />
    </template>
  </Dialog>
</template>
