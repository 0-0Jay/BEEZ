<script setup>
import { useVersionStore } from '@/stores/version';
import { storeToRefs } from 'pinia';
import { useToast } from 'primevue';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const visible = ref(false); // 모달
const confirmMsg = ref(''); // 에러메시지
const toast = useToast(); // 토스트메시지
const pendingDeleteId = ref(null); // 삭제할 id 임시저장
const versionOptions = ref([]); // 버전명 Select 옵션
const statusOptions = ref([]); // 상태 Select 옵션

// --- 1. 스토어 연결 ---
const versionStore = useVersionStore();
const { versions, loading } = storeToRefs(versionStore);

// --- 2. 필터 상태 ---
const filters = reactive({
  id: null,
  status: null
});

// --- 3. 조회 로직 ---
// onMounted(async () => {
//   await versionStore.
// })
</script>

<template>
  <div>
    <div class="flex justify-end mb-4">
      <Button label="버전 추가" icon="pi pi-plus" severity="contrast" outlined />
    </div>
    <!-- 조회 영역 -->
    <div class="flex bg-[#E8E5DC] px-10 py-8 rounded-lg mb-8 shadow-sm border border-[#C7C7C2]">
      <div class="flex items-center flex-1 flex-wrap gap-y-3">
        <label class="filter-label mr-5">버전명</label>
        <Select class="filter-input w-80 mr-10" placeholder="선택" />
        <label class="filter-label mr-5">상태</label>
        <Select class="filter-input w-50 mr-10" placeholder="선택" />
      </div>

      <div class="flex gap-2 ml-auto">
        <Button label="초기화" severity="secondary" raised />
        <Button label="조회" icon="pi pi-search" raised />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="versions" dataKey="id" :rowHover="true" tableStyle="width: 100%">
        <template #empty>
          <div class="text-center py-10 text-gray-400">등록된 버전이 없습니다.</div>
        </template>
        <!-- 버전명, 기본버전여부, 마감일, 설명, 상태, 공유여부 -->
        <Column field="name" header="버전명" headerClass="table-header" style="width: 20%" />
        <Column field="description" header="설명" headerClass="table-header" style="width: 30%" />
        <Column field="default" header="기본버전" headerClass="table-header" style="width: 10%" />
        <Column field="status" header="상태" headerClass="table-header" style="width: 10%" />
        <Column field="isPublic" header="공유" headerClass="table-header" style="width: 8%" />
        <Column field="endDate" header="마감일" headerClass="table-header" style="width: 12%" />
        <Column headerClass="table-header" style="width: 10%">
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
.filter-label {
  font-size: 1rem;
  font-weight: 600;
  color: #3a3b35;
  white-space: nowrap;
}

:deep(.filter-input) {
  height: 38px !important;
}
:deep(.filter-input .p-select-label) {
  display: flex;
  align-items: center;
}

:deep(.table-header) {
  background-color: #e8e5dc !important;
  color: #1a1816 !important;
  font-weight: 700 !important;
  text-align: center !important;
  padding: 1.25rem 0 !important;
}

:deep(.table-header .p-datatable-column-header-content) {
  justify-content: center;
}

:deep(.btn-register-outline) {
  border-color: #c7c7c2 !important;
  color: #3a3b35 !important;
  height: 36px !important;
}

:deep(.p-datatable-tbody > tr > td) {
  text-align: center !important;
  padding: 1rem 0 !important;
  border-bottom: 1px solid #f2f0eb !important;
}
</style>
