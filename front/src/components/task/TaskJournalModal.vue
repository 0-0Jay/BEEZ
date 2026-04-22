<script setup>
import { useTaskStore } from '@/stores/task';
import Dialog from 'primevue/dialog';
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: Boolean,
  journalId: String
});

const emit = defineEmits(['update:visible', 'close']);

const taskStore = useTaskStore();
const journalDetails = computed(() => taskStore.journalList);
const typeOptions = computed(() => taskStore.typeList);
const categoryOptions = computed(() => taskStore.cateList);
const commonCodes = computed(() => taskStore.commonCodeList);
const loading = ref(false);
const fieldMapper = {
  is_public: '비공개',
  title: '일감명',
  type: '일감유형',
  workflow: '진행상태',
  priority: '우선순위',
  description: '설명',
  parent_id: '상위 일감',
  category: '일감 범주',
  user_id: '담당자',
  version_id: '목표 버전',
  estimated_time: '추정 시간',
  progress: '진척도',
  planned_start: '예상 시작일',
  planned_end: '예상 마감일',
  actual_start: '실제 시작일',
  actual_end: '실제 마감일',
  attachments: '첨부파일',
  reject: '반려사유'
};

const formatDate = (ts) => {
  if (!ts) return '-';
  return new Date(ts).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

const DATE_FIELDS = new Set(['planned_start', 'planned_end', 'actual_start', 'actual_end']);
const COMMON_CODE_FIELDS = new Set(['priority', 'is_public', 'workflow']);

const resolveValue = (fieldName, value) => {
  if (!value) return '-';

  if (DATE_FIELDS.has(fieldName)) {
    return formatDate(value);
  }

  if (COMMON_CODE_FIELDS.has(fieldName)) {
    return commonCodes.value.find((c) => c.id == value)?.name ?? value;
  }

  if (fieldName === 'category') {
    return categoryOptions.value.find((c) => c.id == value)?.name ?? value;
  }

  if (fieldName === 'type') {
    return typeOptions.value.find((t) => t.id == value)?.name ?? value;
  }

  return value;
};

watch(
  () => props.journalId,
  async (id) => {
    if (!id) return;
    loading.value = true;
    await taskStore.findJournalDetail(id);
    loading.value = false;
  }
);

const close = () => {
  emit('update:visible', false);
  emit('close');
};
</script>

<template>
  <Dialog :visible="visible" modal header="변경 이력 상세" :style="{ width: '600px' }" :draggable="false" @update:visible="close">
    <div v-if="loading" class="py-8 text-center text-base text-[#9A9B90]">불러오는 중...</div>

    <div v-else-if="journalDetails.length === 0" class="py-8 text-center text-base text-[#9A9B90]">변경된 항목이 없습니다.</div>

    <table v-else class="w-full text-base">
      <thead>
        <tr class="border-b border-[#F2F0EB] bg-[#F8F7F4]">
          <th class="px-4 py-2.5 text-left font-semibold text-[#6B6B63] w-1/3">변경 항목</th>
          <th class="px-4 py-2.5 text-left font-semibold text-[#6B6B63] w-1/3">변경 전</th>
          <th class="px-4 py-2.5 text-left font-semibold text-[#6B6B63] w-1/3">변경 후</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-[#F2F0EB]">
        <tr v-for="detail in journalDetails" :key="detail.id" class="hover:bg-[#FAFAF8]">
          <td class="px-4 py-3 font-semibold text-[#3A3B35]">{{ fieldMapper[detail.fieldName] }}</td>
          <td class="px-4 py-3 text-[#9A9B90] line-through">{{ resolveValue(detail.fieldName, detail.oldValue) }}</td>
          <td class="px-4 py-3 text-[#1A1816] font-medium">{{ resolveValue(detail.fieldName, detail.newValue) }}</td>
        </tr>
      </tbody>
    </table>

    <template #footer>
      <div class="flex justify-end pt-2">
        <Button label="닫기" severity="secondary" raised @click="close" />
      </div>
    </template>
  </Dialog>
</template>
