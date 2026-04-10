<!-- MemberPaner.vue -->
<script setup>
import { useGroupStore } from '@/stores/group';

const groupStore = useGroupStore();
</script>

<template>
  <div class="border border-[#5B6E96] rounded-xl p-6 h-[600px] overflow-y-auto">
    <div class="flex justify-between items-center border-b border-[#C7C7C2] pb-3 mb-4">
      <h2 class="text-sm font-medium text-[#1A1816]">구성원 추가</h2>
      <Button label="구성원 추가" icon="pi pi-plus" size="small" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" />
    </div>

    <div class="flex flex-col items-center justify-center h-48">
      <i class="pi pi-users text-3xl text-[#C7C7C2] mb-2" />
      <p class="text-xs text-[#6B6B63]">추가된 구성원이 없습니다.</p>
    </div>

    <div class="grid grid-cols-2 gap-2">
      <div class="border border-[#E0E0DA] rounded-lg p-3 relative">
        <button class="absolute top-2 right-2 text-[#6B6B63] hover:text-red-500 text-base leading-none" @click="groupStore.removeMember(member.id)">×</button>
        <p class="text-xs font-medium text-[#1A1816] mb-1">이름 :</p>
        <p class="text-xs text-[#6B6B63]">이메일 :</p>
      </div>
    </div>
  </div>
</template>
