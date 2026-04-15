<script setup>
import { useGroupStore } from '@/stores/group';
import { computed, ref } from 'vue';

const groupStore = useGroupStore();
const groupNameError = ref('');

const isInvalid = computed(() => !!groupNameError.value);

const validate = () => {
  const name = groupStore.groupForm.name?.trim() || '';
  const desc = groupStore.groupForm.description || '';
  groupNameError.value = '';

  if (!name) {
    groupNameError.value = '그룹 이름을 입력해 주세요.';
  } else if (name.length > 20) {
    groupNameError.value = '그룹 이름은 20자 이내로 입력해 주세요.';
  }

  if (desc.length > 500) {
    return false;
  }

  return !groupNameError.value;
};

const setExternalError = (msg) => {
  groupNameError.value = msg;
};

defineExpose({ validate, setExternalError });
</script>

<template>
  <div class="border border-[#5B6E96] rounded-xl p-6 h-[620px] overflow-y-auto">
    <h2 class="font-medium text-[#1A1816] border-b border-[#C7C7C2] pb-7 mb-4">기본 정보</h2>

    <div class="mb-4">
      <label class="block font-medium text-[#1A1816] mb-1"> 그룹명 <span class="text-red-500">*</span> </label>

      <InputText v-model="groupStore.groupForm.name" placeholder="그룹명을 입력해 주세요." class="w-full text-sm" :class="{ 'p-invalid': isInvalid }" :maxlength="20" @input="groupNameError = ''" />

      <small v-if="isInvalid" class="text-red-500 mt-1 block"> {{ groupNameError }} </small>
    </div>

    <div>
      <label class="block font-medium text-[#1A1816] mb-1">설명</label>
      <Textarea v-model="groupStore.groupForm.description" placeholder="그룹에 대한 설명을 입력해주세요." rows="5" class="w-full h-[350px] text-sm resize-none" :maxlength="500" />
      <div class="flex items-center justify-between mt-2">
        <small v-if="groupStore.groupForm.description?.length > 500" class="text-red-500 text-xs">설명은 500자를 초과할 수 없습니다.</small>
        <small class="ml-auto text-xs" :class="groupStore.groupForm.description?.length > 500 ? 'text-red-500 font-semibold' : 'text-[#9A9B90]'">{{ groupStore.groupForm.description?.length }} / 500</small>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.p-inputtext.p-invalid) {
  border-color: #e8920e !important;
}

:deep(.p-inputtext.p-invalid::placeholder) {
  color: #736f68;
}

div::-webkit-scrollbar {
  width: 6px;
}
div::-webkit-scrollbar-thumb {
  background-color: #c7c7c2;
  border-radius: 10px;
}
</style>
