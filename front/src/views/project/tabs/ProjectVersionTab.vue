<script setup>
import VersionFormModal from '@/components/project/VersionFormModal.vue';
import { useAuthStore } from '@/stores/auth';
import { useVersionStore } from '@/stores/version';
import { storeToRefs } from 'pinia';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const visible = ref(false); // 컨펌창
const modalVisible = ref(false); //추가 모달
const confirmMsg = ref(''); // 에러메시지
const toast = useToast(); // 토스트메시지
const versionOptions = ref([]); // 버전명 Select 옵션
// const statusOptions = ref([]); // 상태 Select 옵션
const selectedRow = ref(null);
const editRow = ref(null);
const projectInfo = ref(null);

// --- 1. 스토어 연결 ---
const versionStore = useVersionStore();
const { versions, loading, commonCodeList } = storeToRefs(versionStore);

// --- 2. 필터 상태 ---
const filters = reactive({
  projectId: route.params.id,
  id: null,
  status: null
});

// --- 3. 조회 로직 ---
onMounted(async () => {
  await versionStore.findCommonCodeList();
  projectInfo.value = await versionStore.findProject(route.params.id);
  await versionStore.fetchVersions(filters);

  // Select 옵션 구성
  versionOptions.value = versionStore.versions.map((p) => ({
    label: p.name,
    value: p.id
  }));
});

const statusOptions = computed(() => (commonCodeList.value ?? []).filter((c) => c.cgroup === '0N'));

// 필터 적용한 목록 조회
const fetchVersions = () => {
  versionStore.fetchVersions({
    ...filters
  });
};

//필터 초기화
const resetFilters = () => {
  Object.assign(filters, {
    projectId: route.params.id,
    id: null,
    status: null
  });
  fetchVersions(); // 초기화 후 재조회
};

const openEditModal = (row) => {
  editRow.value = row;
  modalVisible.value = true;
};

// 모달 닫힐 때 초기화
const closeModal = () => {
  editRow.value = null;
  modalVisible.value = false;
};

//삭제
const openDeleteConfirm = (row) => {
  selectedRow.value = row;
  confirmMsg.value = '정말 삭제하시겠습니까?';
  visible.value = true;
};

const handleDelConfirm = async () => {
  visible.value = false;
  if (selectedRow.value) {
    try {
      await versionStore.deleteVersion(selectedRow.value.id, selectedRow.value.projectId, selectedRow.value.name);
      selectedRow.value = null;
      fetchVersions();
      toast.add({ severity: 'success', summary: '삭제 완료', detail: '버전이 삭제되었습니다.', life: 2000 });
    } catch (error) {
      toast.add({ severity: 'error', summary: '삭제 실패', detail: error.message, life: 2000 });
    }
  }
};

// 날짜 포맷 변환 함수
const formatDate = (date) => {
  if (!date) return null;
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};
</script>

<template>
  <div class="bg-[#ffffff] h-full">
    <div class="flex justify-end mb-4">
      <Button v-if="!projectInfo?.parentId" label="버전 추가" icon="pi pi-plus" severity="contrast" outlined @click="modalVisible = true" />
    </div>
    <!-- 조회 영역 -->
    <div class="flex bg-[#E8E5DC] px-10 py-8 rounded-lg mb-8 shadow-sm border border-[#C7C7C2]">
      <div class="flex items-center flex-1 flex-wrap gap-y-3">
        <label class="filter-label mr-5">버전명</label>
        <Select v-model="filters.id" :options="versionOptions" optionLabel="label" optionValue="value" placeholder="선택" class="filter-input w-80 mr-10" />
        <label class="filter-label mr-5">상태</label>
        <Select v-model="filters.status" :options="statusOptions" optionLabel="name" optionValue="id" placeholder="선택" class="filter-input w-50 mr-10" />
      </div>

      <div class="flex gap-2 ml-auto">
        <Button label="초기화" severity="secondary" raised @click="resetFilters" />
        <Button label="조회" icon="pi pi-search" raised @click="fetchVersions" />
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#C7C7C2] overflow-hidden">
      <DataTable :value="versions" :loading="loading" dataKey="id" :rowHover="true" tableStyle="width: 100%">
        <template #empty>
          <div class="text-center py-10 text-gray-400">등록된 버전이 없습니다.</div>
        </template>
        <!-- 버전명, 기본버전여부, 마감일, 설명, 상태, 공유여부 -->
        <Column field="name" header="버전명" headerClass="table-header" style="width: 15%" />
        <Column field="description" header="설명" headerClass="table-header" style="width: 25%" />
        <Column field="isDefault" header="기본버전" headerClass="table-header" style="width: 8%">
          <template #body="{ data }">
            <i v-if="data.isDefault" class="pi pi-check" style="color: #22c55e" />
          </template>
        </Column>
        <Column field="statusName" header="상태" headerClass="table-header" style="width: 10%" />
        <Column field="isShareName" header="공유" headerClass="table-header" style="width: 10%" />
        <Column header="시작일" headerClass="table-header" style="width: 11%">
          <template #body="{ data }">
            <span>{{ formatDate(data.startDate) }}</span>
          </template>
        </Column>
        <Column header="마감일" headerClass="table-header" style="width: 11%">
          <template #body="{ data }">
            <span>{{ formatDate(data.endDate) }}</span>
          </template>
        </Column>
        <Column headerClass="table-header" v-if="!projectInfo?.parentId" style="width: 10%">
          <template #body="{ data }">
            <div class="flex gap-2">
              <Button label="편집" icon="pi pi-pencil" text size="small" class="text-[#6B6B63]" @click="openEditModal(data)" />
              <Button label="삭제" icon="pi pi-trash" text size="small" class="text-red-400" @click="openDeleteConfirm(data)" />
            </div>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="handleDelConfirm">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>
  <VersionFormModal
    v-model:visible="modalVisible"
    :editData="editRow"
    @saved="fetchVersions"
    @update:visible="
      (val) => {
        if (!val) closeModal();
      }
    "
  />
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
