<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  visible: Boolean,
  event: Object,
  typeOptions: Array,
  role: String, // 추가
  userId: [String, Number] // 추가
});

const emit = defineEmits(['update:visible', 'delete', 'update']);

const isEditing = ref(false);
const editForm = ref({});

// ── 권한 계산 ─────────────────────────────────────────
const isAdmin = computed(() => props.role === 'ROLE0001' || props.role === 'ROLE0002');

const isOwn = computed(() => String(props.event?.extendedProps?.userId) === String(props.userId));

// 팀 일정: 관리자만 수정/삭제 가능
// 내 일정: 본인이면 수정/삭제 가능
const canEdit = computed(() => {
  if (!props.event) return false;
  const isTeam = props.event.extendedProps?.type === 'D1';
  return isTeam ? isAdmin.value : isOwn.value || isAdmin.value;
});

// ── 날짜 변환 ─────────────────────────────────────────
const toDate = (str) => {
  if (!str) return null;
  const [y, m, d] = str.split('-').map(Number);
  return new Date(y, m - 1, d);
};

const toStr = (date) => {
  if (!date) return '';
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
};

watch(
  () => props.event,
  (ev) => {
    if (!ev) return;
    isEditing.value = false;
    editForm.value = {
      id: ev.id,
      title: ev.title,
      start: toDate(ev.startStr || ev.start),
      end: toDate(ev.endStr || ev.end) || null,
      type: ev.extendedProps?.type ?? 'D0',
      content: ev.extendedProps?.content ?? '',
      userId: ev.extendedProps?.userId ?? '',
      projectId: ev.extendedProps?.projectId ?? ''
    };
  },
  { immediate: true }
);

const formatEventDate = (ev) => {
  if (!ev) return '';
  const s = ev.startStr || ev.start;
  const e = ev.endStr || ev.end;
  return !e || s === e ? s : `${s} ~ ${e}`;
};

const startEdit = () => {
  isEditing.value = true;
};
const cancelEdit = () => {
  isEditing.value = false;
};

const saveEdit = () => {
  emit('update', {
    ...editForm.value,
    start: toStr(editForm.value.start),
    end: toStr(editForm.value.end)
  });
  isEditing.value = false;
};
</script>

<template>
  <Dialog :visible="visible" @update:visible="$emit('update:visible', $event)" modal :header="isEditing ? '일정 수정' : event?.title" :style="{ width: '400px' }" :closable="true" :draggable="false">
    <div v-if="event" class="flex flex-col gap-4 py-2">
      <!-- 보기 모드 -->
      <template v-if="!isEditing">
        <div class="flex items-center gap-3 text-base text-gray-700">
          <i class="pi pi-calendar text-gray-400" />
          <span>{{ formatEventDate(event) }}</span>
        </div>
        <div class="flex items-center gap-3 text-base text-gray-700">
          <i class="pi pi-tag text-gray-400" />
          <Tag :value="event.extendedProps?.type === 'D0' ? '내 일정' : '팀 일정'" :severity="event.extendedProps?.type === 'D0' ? '' : 'warn'" class="!text-base" />
        </div>
        <div v-if="event.extendedProps?.content" class="flex items-start gap-3 text-base text-gray-700">
          <i class="pi pi-file-edit text-gray-400 mt-0.5" />
          <span>{{ event.extendedProps.content }}</span>
        </div>
      </template>

      <!-- 수정 모드 -->
      <template v-else>
        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-gray-500">제목 <span class="text-red-400">*</span></label>
          <InputText v-model="editForm.title" class="w-full" />
        </div>

        <div class="grid grid-cols-2 gap-3">
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold text-gray-500">시작일 <span class="text-red-400">*</span></label>
            <DatePicker v-model="editForm.start" dateFormat="yy-mm-dd" class="w-full" />
          </div>
          <div class="flex flex-col gap-1.5">
            <label class="text-base font-semibold text-gray-500">종료일</label>
            <DatePicker v-model="editForm.end" dateFormat="yy-mm-dd" :minDate="editForm.start" class="w-full" />
          </div>
        </div>

        <!-- 유형 — 관리자만 노출 -->
        <div v-if="typeOptions?.length > 1" class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-gray-500">유형</label>
          <SelectButton v-model="editForm.type" :options="typeOptions" optionLabel="label" optionValue="value" class="w-full" />
        </div>

        <div class="flex flex-col gap-1.5">
          <label class="text-base font-semibold text-gray-500">메모</label>
          <Textarea v-model="editForm.content" rows="2" class="w-full resize-none" />
        </div>
      </template>
    </div>

    <template #footer>
      <div class="flex gap-2">
        <template v-if="!isEditing">
          <!-- 권한 있을 때만 수정/삭제 노출 -->
          <Button v-if="canEdit" label="수정" severity="primary" raised @click="startEdit" />
          <Button v-if="canEdit" label="삭제" severity="danger" raised @click="$emit('delete')" />
          <Button label="닫기" severity="secondary" raised @click="$emit('update:visible', false)" />
        </template>
        <template v-else>
          <Button label="저장" severity="primary" raised :disabled="!editForm.title || !editForm.start" @click="saveEdit" />
          <Button label="취소" severity="secondary" raised @click="cancelEdit" />
        </template>
      </div>
    </template>
  </Dialog>
</template>
