<script setup>
import { ref } from 'vue';

const props = defineProps({
  projectId: {
    type: String,
    required: true
  }
});

// TODO: 백엔드 연결
const versions = ref([]);
</script>

<template>
  <div>
    <div class="flex justify-end mb-4">
      <Button label="버전 추가" icon="pi pi-plus" outlined class="btn-register-outline" />
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="versions" dataKey="id" :rowHover="true">
        <template #empty>
          <div class="text-center py-10 text-gray-400">등록된 버전이 없습니다.</div>
        </template>

        <Column field="name" header="버전명" headerClass="table-header" />
        <Column field="description" header="설명" headerClass="table-header" />
        <Column field="status" header="상태" headerClass="table-header" />
        <Column headerClass="table-header" style="width: 160px">
          <template #body>
            <div class="flex gap-2">
              <Button label="편집" icon="pi pi-pencil" text size="small" class="text-[#6B6B63]" />
              <Button label="삭제" icon="pi pi-trash" text size="small" class="text-red-400" />
            </div>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<style scoped>
:deep(.table-header) {
  background-color: #f2f0eb !important;
  color: #3a3b35 !important;
  font-weight: 600 !important;
}

:deep(.btn-register-outline) {
  border-color: #c7c7c2 !important;
  color: #3a3b35 !important;
  height: 36px !important;
}
</style>
