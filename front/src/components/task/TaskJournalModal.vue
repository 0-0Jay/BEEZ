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
const loading = ref(false);

watch(
  () => props.journalId,
  async (id) => {
    if (!id) return;
    loading.value = true;
    await taskStore.findJournalList(id);
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
          <td class="px-4 py-3 font-semibold text-[#3A3B35]">{{ detail.fieldName }}</td>
          <td class="px-4 py-3 text-[#9A9B90] line-through">{{ detail.oldValue || '-' }}</td>
          <td class="px-4 py-3 text-[#1A1816] font-medium">{{ detail.newValue || '-' }}</td>
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
