<script setup>
import { useAuthStore } from '@/stores/auth';
import Button from 'primevue/button';
import DatePicker from 'primevue/datepicker';
import InputNumber from 'primevue/inputnumber';
import Select from 'primevue/select';
import Textarea from 'primevue/textarea';
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  task: { type: Object, default: null }, // task.title
  activityList: { type: Array, default: () => [] } // [{ id, name }]
});

const emit = defineEmits(['update:visible', 'confirm', 'cancel']);

const authStore = useAuthStore();
const user = computed(() => authStore.user);

const form = ref({
  taskStart: null,
  spent: null,
  activityType: null,
  progress: props.task?.progress,
  description: ''
});

watch(
  () => props.visible,
  (val) => {
    if (val) {
      form.value = {
        taskStart: null,
        spent: null,
        activityType: null,
        progress: props.task?.progress,
        description: ''
      };
    }
  }
);

const activityOptions = computed(() => props.activityList.map((a) => ({ label: a.name, value: a.id })));

const isValid = computed(() => form.value.taskStart && form.value.spent !== null && form.value.activityType !== null && form.value.progress !== null);

function handleConfirm() {
  if (!isValid.value) return;
  emit('confirm', {
    taskId: props.task?.id,
    userId: user.value?.id,
    taskStart: form.value.taskStart,
    spent: form.value.spent,
    activityType: form.value.activityType,
    progress: form.value.progress,
    description: form.value.description
  });
}

function handleCancel() {
  emit('update:visible', false);
  emit('cancel');
}
</script>

<template>
  <Dialog
    :visible="visible"
    :modal="true"
    :draggable="false"
    :closable="false"
    :style="{ width: '520px', padding: '0' }"
    :pt="{
      root: { class: 'rounded-xl overflow-hidden shadow-2xl shadow-stone-300/60 !p-0' },
      header: { style: 'display:none' },
      content: { class: '!p-0' },
      footer: { style: 'display:none' }
    }"
    @update:visible="(val) => emit('update:visible', val)"
  >
    <div class="bg-white rounded-xl overflow-hidden">
      <!-- 헤더 -->
      <div class="flex items-center justify-between px-7 py-5 border-b border-stone-100 bg-stone-50">
        <div class="flex items-center gap-3">
          <h2 class="text-base font-bold text-stone-900 tracking-tight">소요 시간 기록</h2>
        </div>
        <Button icon="pi pi-times" severity="secondary" text rounded @click="handleCancel" />
      </div>

      <!-- 본문 -->
      <div class="px-7 py-6 flex flex-col gap-5">
        <!-- 일감명 -->
        <div class="flex items-stretch border border-stone-200 rounded-lg overflow-hidden bg-stone-50 text-sm">
          <span class="px-3.5 py-2.5 font-semibold text-stone-500 border-r border-stone-200 min-w-[110px] shrink-0">일감명</span>
          <span class="px-3.5 py-2.5 text-stone-700">{{ task?.title }}</span>
        </div>

        <!-- 작업자 -->
        <div class="flex items-stretch border border-stone-200 rounded-lg overflow-hidden bg-stone-50 text-sm">
          <span class="px-3.5 py-2.5 font-semibold text-stone-500 border-r border-stone-200 min-w-[110px] shrink-0">작업자</span>
          <span class="px-3.5 py-2.5 text-stone-700">{{ user?.name }} ({{ user?.id }})</span>
        </div>

        <!-- 작업 일시 -->
        <div class="flex flex-col gap-1.5">
          <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
            작업 일시
            <span class="text-red-500 text-base leading-none">•</span>
          </label>
          <DatePicker v-model="form.taskStart" showTime hourFormat="24" dateFormat="yy-mm-dd" placeholder="작업 일시를 선택하세요" class="w-full" input-class="w-full" />
        </div>

        <!-- 소요 시간 / 진척도 (2열) -->
        <div class="grid grid-cols-2 gap-4">
          <!-- 소요 시간 -->
          <div class="flex flex-col gap-1.5">
            <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
              소요 시간 (분)
              <span class="text-red-500 text-base leading-none">•</span>
            </label>
            <InputNumber v-model="form.spent" :min="0" placeholder="분 단위로 입력" class="w-full" />
          </div>

          <!-- 진척도 -->
          <div class="flex flex-col gap-1.5">
            <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
              진척도 (%)
              <span class="text-red-500 text-base leading-none">•</span>
            </label>
            <InputNumber v-model="form.progress" :min="0" :max="100" showButtons mode="decimal" :step="10" placeholder="0 ~ 100" class="w-full" />
          </div>
        </div>

        <!-- 작업 종류 -->
        <div class="flex flex-col gap-1.5">
          <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
            작업 종류
            <span class="text-red-500 text-base leading-none">•</span>
          </label>
          <Select
            v-model="form.activityType"
            :options="activityOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="작업 종류를 선택하세요"
            class="w-full"
            :pt="{
              root: { class: 'w-full h-9 border border-stone-200 rounded-lg bg-stone-50 focus:border-orange-400 text-base' },
              label: { class: 'text-base text-stone-700 px-3 py-2' },
              trigger: { class: 'px-3' }
            }"
          />
        </div>

        <!-- 설명 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-stone-600">설명</label>
          <Textarea
            v-model="form.description"
            placeholder="작업 내용을 입력하세요..."
            :rows="3"
            :autoResize="false"
            class="w-full resize-none px-3 py-2.5 text-base border border-stone-200 rounded-lg outline-none focus:border-orange-400 bg-stone-50 text-stone-700 placeholder-stone-400"
          />
        </div>
      </div>

      <!-- 푸터 -->
      <div class="flex items-center justify-end gap-2.5 px-7 py-5 border-t border-stone-100 bg-stone-50">
        <Button label="취소" severity="secondary" raised @click="handleCancel" />
        <Button label="저장" raised :disabled="!isValid" @click="handleConfirm" />
      </div>
    </div>
  </Dialog>
</template>
