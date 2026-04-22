<script setup>
import { defineEmits, defineProps } from 'vue';

const props = defineProps({
  matrix: Object,
  statusList: Array
});

const emit = defineEmits(['update:matrix']);

const thBase = 'bg-[#5b6e96] text-[#FFFFFF] font-bold text-center text-[13px]';
const tdLabelBase = 'bg-[#f8f9fb] text-[#3a3b35] font-medium text-center px-3 py-[7px] border-r whitespace-nowrap group-hover:bg-[#f0f3f9]';
const tdCellBase = 'text-center py-2 border-l border-[#f2f0eb] transition-colors duration-150 group-hover:brightness-[0.97]';

// 체크 여부 확인
const isChecked = (beforeCode, afterCode) => {
  return props.matrix?.[beforeCode]?.[afterCode] ?? false;
};

// 체크박스 클릭 시 함수
const setChecked = (beforeCode, afterCode, val) => {
  const updatedMatrix = { ...props.matrix };

  if (!updatedMatrix[beforeCode]) {
    updatedMatrix[beforeCode] = {};
  }

  updatedMatrix[beforeCode][afterCode] = val;

  emit('update:matrix', updatedMatrix);
};
</script>

<template>
  <div class="overflow-hidden border border-[#5b6e96] rounded-lg">
    <table class="w-full border-collapse bg-white table-fixed text-[13px]">
      <thead>
        <tr>
          <th class="w-36 px-3 py-[14px] border-r border-b border-[#4a5d80]" :class="thBase">일감 상태</th>
          <th :colspan="statusList.length" class="py-[14px] border-b border-[#4a5d80]" :class="thBase">허용되는 일감 상태</th>
        </tr>
        <tr>
          <th class="w-36 px-2 py-[10px] border-r border-[#4a5d80]" :class="thBase">✓ 일감 상태</th>
          <th v-for="afterStatus in statusList" :key="afterStatus.key" class="py-[10px] px-1 border-l border-[#4a5d80] min-w-[80px]" :class="thBase">✓ {{ afterStatus.label }}</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(beforeStatus, rowIdx) in statusList" :key="beforeStatus.key" class="group">
          <td :class="[tdLabelBase, rowIdx < statusList.length - 1 && 'border-b border-[#f2f0eb]']">
            <span class="mr-1 text-[#fd9e0f] text-[11px]">✓</span>
            {{ beforeStatus.label }}
          </td>

          <td v-for="afterStatus in statusList" :key="afterStatus.key" :class="[tdCellBase, rowIdx < statusList.length - 1 && 'border-b border-[#f2f0eb]', isChecked(beforeStatus.key, afterStatus.key) ? 'bg-green-50' : 'bg-white']">
            <Checkbox :modelValue="isChecked(beforeStatus.key, afterStatus.key)" @update:modelValue="(val) => setChecked(beforeStatus.key, afterStatus.key, val)" binary :disabled="beforeStatus.key === afterStatus.key" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
