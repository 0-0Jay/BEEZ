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
  task: { type: Object, default: null },
  activityList: { type: Array, default: () => [] }
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

const touched = ref({
  taskStart: true,
  spent: true,
  activityType: true,
  progress: true,
  description: true
});

const errors = ref({
  taskStart: '',
  spent: '',
  activityType: '',
  progress: '',
  description: ''
});

function validate() {
  errors.value.taskStart = form.value.taskStart === null ? '작업 일시를 입력해주세요.' : '';
  errors.value.spent = form.value.spent === null ? '소요시간을 입력해주세요.' : '';
  errors.value.activityType = form.value.activityType === null ? '작업 유형을 선택해주세요.' : '';
  errors.value.progress = form.value.progress === null ? '진척도를 입력해주세요.' : '';
  errors.value.description = form.value.description.length > 300 ? '설명은 300자를 초과할 수 없습니다.' : '';
}

watch(
  () => props.visible,
  (val) => {
    if (val) {
      form.value = {
        taskStart: null,
        spent: null,
        activityType: null,
        progress: props.task?.progress ?? null,
        description: ''
      };
      errors.value = { taskStart: '', spent: '', activityType: '', progress: '' };
    }
  }
);

const activityOptions = computed(() => props.activityList.map((a) => ({ label: a.name, value: a.id })));

function handleConfirm() {
  validate();

  const hasError = Object.values(errors.value).some((e) => e);
  if (hasError) return;

  emit('confirm', {
    taskId: props.task?.id,
    userId: user.value?.id,
    taskStart: form.value.taskStart,
    spent: form.value.spent,
    activityType: form.value.activityType,
    progress: form.value.progress,
    description: form.value.description
  });
  emit('update:visible', false);
}

function handleCancel() {
  emit('update:visible', false);
  emit('cancel');
}

const datePicker = ref(null);
const closeDatePicker = () => {
  datePicker.value.overlayVisible = false;
};
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
            <span class="text-red-500">*</span>
          </label>
          <DatePicker v-model="form.taskStart" showTime hourFormat="24" dateFormat="yy-mm-dd" placeholder="작업 일시를 선택하세요" class="w-full" input-class="w-full" ref="datePicker">
            <template #footer>
              <div class="flex justify-end p-2">
                <Button label="확인" raised @click="closeDatePicker" />
              </div>
            </template>
          </DatePicker>
          <small v-if="touched.taskStart && errors.taskStart" class="text-red-500 mt-1 block text-xs">
            {{ errors.taskStart }}
          </small>
        </div>

        <!-- 소요 시간 / 진척도 (2열) -->
        <div class="grid grid-cols-2 gap-4">
          <!-- 소요 시간 -->
          <div class="flex flex-col gap-1.5">
            <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
              소요 시간 (시간)
              <span class="text-red-500">*</span>
            </label>
            <InputNumber v-model="form.spent" :min="0" placeholder="시간 단위로 입력" class="w-full" />
            <small v-if="touched.spent && errors.spent" class="text-red-500 mt-1 block text-xs">
              {{ errors.spent }}
            </small>
          </div>

          <!-- 진척도 -->
          <div class="flex flex-col gap-1.5">
            <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
              진척도 (%)
              <span class="text-red-500">*</span>
            </label>
            <InputNumber v-model="form.progress" :min="0" :max="100" showButtons mode="decimal" :step="10" placeholder="0 ~ 100" class="w-full" />
            <small v-if="touched.progress && errors.progress" class="text-red-500 mt-1 block text-xs">
              {{ errors.progress }}
            </small>
          </div>
        </div>

        <!-- 작업 종류 -->
        <div class="flex flex-col gap-1.5">
          <label class="flex items-center gap-1 text-base font-semibold text-stone-600">
            작업 종류
            <span class="text-red-500">*</span>
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
          <small v-if="touched.activityType && errors.activityType" class="text-red-500 mt-1 block text-xs">
            {{ errors.activityType }}
          </small>
        </div>

        <!-- 설명 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-stone-600">설명</label>
          <Textarea
            v-model="form.description"
            placeholder="작업 내용을 입력하세요... (최대 300자)"
            :rows="3"
            :autoResize="false"
            class="w-full resize-none px-3 py-2.5 text-base border rounded-lg outline-none bg-stone-50 text-stone-700 placeholder-stone-400"
            :class="touched.description && errors.description ? 'border-red-400 focus:border-red-400' : 'border-stone-200 focus:border-orange-400'"
            @input="touched.description = true"
          />
          <div class="flex items-center justify-between">
            <small v-if="touched.description && errors.description" class="text-red-500 block text-xs">
              {{ errors.description }}
            </small>
            <small class="ml-auto text-xs" :class="form.description.length > 300 ? 'text-red-500 font-semibold' : 'text-stone-400'"> {{ form.description.length }} / 300 </small>
          </div>
        </div>
      </div>

      <!-- 푸터 -->
      <div class="flex items-center justify-end gap-2.5 px-7 py-5 border-t border-stone-100 bg-stone-50">
        <Button label="저장" raised @click="handleConfirm" />
        <Button label="취소" severity="secondary" raised @click="handleCancel" />
      </div>
    </div>
  </Dialog>
</template>
