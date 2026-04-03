<script setup>
import { useTaskStore } from '@/stores/task';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import Select from 'primevue/select';
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: Boolean,
  taskId: { type: [String, Number], default: null }
});

const emit = defineEmits(['update:visible', 'confirm', 'cancel']);

const taskStore = useTaskStore();
const selectedTaskId = ref(null);
const selectedRelation = ref(null);

watch(
  () => props.visible,
  (v) => {
    if (v) {
      selectedTaskId.value = null;
      selectedRelation.value = null;
    }
  }
);

// 자기 자신 제외
const taskOptions = computed(() => taskStore.taskList.filter((t) => t.id !== props.taskId));

const relationOptions = computed(() => taskStore.commonCodeList.filter((r) => r.cgroup === '0W'));

const isConfirmDisabled = computed(() => !selectedTaskId.value || !selectedRelation.value);

const handleConfirm = () => {
  emit('confirm', { linkedTaskId: selectedTaskId.value, relationType: selectedRelation.value });
  emit('update:visible', false);
};

const handleCancel = () => {
  emit('cancel');
  emit('update:visible', false);
};
</script>

<template>
  <Dialog :visible="visible" modal header="연결된 일감 추가" :style="{ width: '480px' }" @update:visible="$emit('update:visible', $event)">
    <div class="flex flex-col gap-5 py-2">
      <!-- 일감 선택 -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-[#3A3B35]">연결할 일감 <span class="text-red-500">*</span></label>
        <Select v-model="selectedTaskId" :options="taskOptions" optionLabel="title" optionValue="id" placeholder="일감 선택" filter class="w-full">
          <template #option="{ option }">
            <span class="font-mono text-[#9A9B90] mr-2 text-sm">{{ option.id }}</span>
            <span class="text-[#1A1816]">{{ option.title }}</span>
          </template>
        </Select>
      </div>

      <!-- 관계 선택 -->
      <div class="flex flex-col gap-1.5">
        <label class="text-base font-semibold text-[#3A3B35]">관계 유형 <span class="text-red-500">*</span></label>
        <Select v-model="selectedRelation" :options="relationOptions" optionLabel="name" optionValue="id" placeholder="관계 선택" class="w-full" />
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-2">
        <Button label="확인" :disabled="isConfirmDisabled" @click="handleConfirm" />
        <Button label="취소" severity="secondary" raised @click="handleCancel" />
      </div>
    </template>
  </Dialog>
</template>
