<!-- GroupInfoPanel.vue -->
<script setup>
import { useGroupStore } from '@/stores/group';

const groupStore = useGroupStore();
</script>

<template>
  <div class="border border-[#5B6E96] rounded-xl p-6 h-[600px] overflow-y-auto">
    <h2 class="font-medium text-[#1A1816] border-b border-[#C7C7C2] pb-7 mb-4">새 그룹</h2>

    <div class="mb-4">
      <label class="block font-medium text-[#1A1816] mb-1"> 그룹명 <span class="text-red-500">*</span> </label>
      <InputText placeholder="그룹명을 입력해 주세요." class="w-full text-sm" :maxlength="20" />
      <p class="text-xs text-[#6B6B63]" style="margin-top: 5px">필수, 최대 20자, 공백만 입력 불가</p>
    </div>

    <div>
      <label class="block font-medium text-[#1A1816] mb-1">설명</label>
      <Textarea placeholder="그룹에 대한 설명을 입력해주세요." rows="5" class="w-full h-[350px] text-sm resize-none" />
    </div>
  </div>
</template>
