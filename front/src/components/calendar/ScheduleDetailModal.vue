<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  visible: Boolean,
  event: Object, // CalendarResponse 형태
  typeOptions: Array
});

const emit = defineEmits(['update:visible', 'delete', 'update']);

// 수정 모드 여부
const isEditing = ref(false);

// 수정용 로컬 복사본
const editForm = ref({});

// 이벤트가 바뀌면 편집폼 초기화, 수정모드 해제
watch(
  () => props.event,
  (ev) => {
    if (!ev) return;
    isEditing.value = false;
    editForm.value = {
      id: ev.id,
      title: ev.title,
      start: ev.startStr || ev.start,
      end: ev.endStr || ev.end || '',
      type: ev.extendedProps?.type ?? 'mine',
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
  emit('update', { ...editForm.value });
  isEditing.value = false;
};
</script>

<template>
  <Dialog :visible="visible" @update:visible="$emit('update:visible', $event)" modal :header="isEditing ? '일정 수정' : event?.title" :style="{ width: '380px' }" :closable="true" :draggable="false">
    <div v-if="event" class="flex flex-col gap-4 pt-1">
      <!-- 색상 바 -->
      <div class="h-1 rounded-full -mt-2 mb-1" :style="{ background: event.backgroundColor }" />

      <!-- ── 보기 모드 ── -->
      <template v-if="!isEditing">
        <!-- 날짜 -->
        <div class="flex items-center gap-3 text-sm text-gray-700">
          <i class="pi pi-calendar text-gray-400" />
          <span>{{ formatEventDate(event) }}</span>
        </div>

        <!-- 유형 -->
        <div class="flex items-center gap-3 text-sm text-gray-700">
          <i class="pi pi-tag text-gray-400" />
          <Tag :value="event.extendedProps?.type === 'mine' ? '내 일정' : '팀 일정'" :severity="event.extendedProps?.type === 'mine' ? 'warning' : 'contrast'" class="!text-xs" />
        </div>

        <!-- 메모 -->
        <div v-if="event.extendedProps?.content" class="flex items-start gap-3 text-sm text-gray-700">
          <i class="pi pi-file-edit text-gray-400 mt-0.5" />
          <span>{{ event.extendedProps.content }}</span>
        </div>
      </template>

      <!-- ── 수정 모드 ── -->
      <template v-else>
        <!-- 제목 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-500">제목 <span class="text-red-400">*</span></label>
          <InputText v-model="editForm.title" class="w-full" />
        </div>

        <!-- 시작일 / 종료일 -->
        <div class="grid grid-cols-2 gap-3">
          <div class="flex flex-col gap-1.5">
            <label class="text-xs font-semibold text-gray-500">시작일 <span class="text-red-400">*</span></label>
            <InputText v-model="editForm.start" placeholder="yyyy-MM-dd" class="w-full" />
          </div>
          <div class="flex flex-col gap-1.5">
            <label class="text-xs font-semibold text-gray-500">종료일</label>
            <InputText v-model="editForm.end" placeholder="yyyy-MM-dd" class="w-full" />
          </div>
        </div>

        <!-- 유형 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-500">유형</label>
          <SelectButton v-model="editForm.type" :options="typeOptions" optionLabel="label" optionValue="value" class="w-full" />
        </div>

        <!-- 메모 -->
        <div class="flex flex-col gap-1.5">
          <label class="text-xs font-semibold text-gray-500">메모</label>
          <Textarea v-model="editForm.content" rows="2" class="w-full resize-none" />
        </div>
      </template>
    </div>

    <template #footer>
      <div class="flex justify-between">
        <!-- 왼쪽: 삭제 -->
        <Button label="삭제" severity="danger" outlined size="small" @click="$emit('delete')" />

        <!-- 오른쪽: 상태에 따라 버튼 변경 -->
        <div class="flex gap-2">
          <template v-if="!isEditing">
            <Button label="닫기" severity="secondary" text size="small" @click="$emit('update:visible', false)" />
            <Button label="수정" severity="primary" outlined size="small" @click="startEdit" />
          </template>
          <template v-else>
            <Button label="취소" severity="secondary" text size="small" @click="cancelEdit" />
            <Button label="저장" severity="primary" size="small" :disabled="!editForm.title || !editForm.start" @click="saveEdit" />
          </template>
        </div>
      </div>
    </template>
  </Dialog>
</template>
