<script setup>
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import GitRepoAddModal from '@/components/gits/GitRepoAddModal.vue';
import { useAuthStore } from '@/stores/auth';
import { useGitStore } from '@/stores/gits';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const toast = useToast();
const gitStore = useGitStore();
const authStore = useAuthStore();
const route = useRoute();

const loading = ref(false);
const visible = ref(false);
const confirmMsg = ref('');
const selectedRepoId = ref(null);

const addModalVisible = ref(false);

const openAddModal = () => {
  addModalVisible.value = true;
};

const totalCount = computed(() => gitStore.repoList.length);

onMounted(async () => {
  await fetchRepos();
});

const fetchRepos = async () => {
  loading.value = true;
  try {
    const projectId = route.params.projectId;
    await gitStore.findReposByProjectId(projectId);
  } finally {
    loading.value = false;
  }
};

// 동기화 실행
const onSync = async (data) => {
  loading.value = true;
  console.log('지금 동기화 시도하는 ID:', data.id);
  console.log('ID의 타입:', typeof data.id);

  if (!data.id || typeof data.id === 'object') {
    console.error('에러 발생! ID가 정상이 아닙니다.');
    return;
  }
  try {
    const response = await gitStore.updateSyncCommits(data.id);
    const count = response;
    toast.add({
      severity: 'success',
      summary: '동기화 완료',
      detail: '최신 커밋 내역을 가져왔습니다.',
      life: 3000,
      closable: false
    });
    await fetchRepos();
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || '저장소 연결 상태를 확인하세요.';
    console.error('동기화 통신 에러:', err.response);
    toast.add({
      severity: 'error',
      summary: '동기화 실패',
      detail: errorMsg,
      life: 3000,
      closable: false
    });
  } finally {
    loading.value = false;
  }
};

// 삭제 컨펌
const openDeleteConfirm = (data) => {
  selectedRepoId.value = data.id;
  confirmMsg.value = `'${data.repoName}' 저장소 연결을 해제하시겠습니까?`;
  visible.value = true;
};

// 저장소 삭제
const onDelete = async () => {
  try {
    const response = await gitStore.deleteRepository(selectedRepoId.value);
    toast.add({
      severity: 'success',
      summary: '삭제(해제) 완료',
      detail: '저장소가 삭제(해제)되었습니다.',
      life: 3000,
      closable: false
    });

    await fetchRepos();
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || '저장소 등록 중 오류가 발생했습니다.';
    toast.add({
      severity: 'error',
      summary: '삭제(해제) 실패',
      detail: errorMsg,
      life: 3000,
      closable: false
    });
  } finally {
    visible.value = false;
  }
};
</script>

<template>
  <div class="p-8 bg-[#ffffff] h-full">
    <h1 class="text-2xl font-bold text-[#1A1816]">저장소 관리</h1>

    <div class="flex justify-between items-center mb-3" style="margin-top: 50px">
      <span class="text-sm text-[#3A3B35] font-medium">전체 {{ totalCount }}개</span>
      <Button label="새 저장소 등록" icon="pi pi-plus" :style="{ background: '#2D8FAD', borderColor: '#2D8FAD' }" @click="openAddModal" />
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-[#5B6E96] overflow-hidden mb-6">
      <DataTable :value="gitStore.repoList" :loading="loading" dataKey="id" :rowHover="true" paginator :rows="10">
        <template #empty>
          <div class="flex flex-col items-center justify-center py-10">
            <i class="pi pi-github text-4xl text-[#C7C7C2] mb-3"></i>
            <p class="text-[#6B6B63]">등록된 저장소가 없습니다.</p>
          </div>
        </template>

        <Column header="구분" headerClass="table-header" style="width: 100px">
          <template #body="{ data }">
            <Tag v-if="data.repoType === 'AA1'" value="주저장소" severity="success" />
            <Tag v-else value="일반" severity="warn" />
          </template>
        </Column>

        <Column header="저장소명" headerClass="table-header" style="width: 180px">
          <template #body="{ data }">
            <span class="text-[#1A1816]">{{ data.repoName }}</span>
          </template>
        </Column>

        <Column header="원격 URL" headerClass="table-header" style="width: 250px">
          <template #body="{ data }">
            <span class="text-gray-500 font-mono">{{ data.repoUrl }}</span>
          </template>
        </Column>

        <Column header="전체 커밋" headerClass="table-header" style="width: 100px">
          <template #body="{ data }">
            <Badge :value="data.totalCommitCount || 0" severity="secondary" />
          </template>
        </Column>

        <Column header="최근 동기화" headerClass="table-header" style="width: 180px">
          <template #body="{ data }">
            {{ data.lastSyncDate ? data.lastSyncDate : '기록 없음' }}
          </template>
        </Column>

        <Column header="동기화" headerClass="table-header" style="width: 80px">
          <template #body="{ data }">
            <Button icon="pi pi-refresh" class="p-button-rounded p-button-text p-button-info" @click="onSync(data)" v-tooltip="'지금 동기화'" />
          </template>
        </Column>

        <Column header="삭제" headerClass="table-header" style="width: 80px">
          <template #body="{ data }">
            <Button icon="pi pi-trash" class="p-button-rounded p-button-text p-button-danger" @click="openDeleteConfirm(data)" v-tooltip="'정보 변경 시 삭제 후 다시 등록해 주세요.'" />
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="onDelete()">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>

  <GitRepoAddModal v-model:visible="addModalVisible" :projectId="route.params.projectId" @saved="fetchRepos" />
</template>

<style scoped>
:deep(.table-header) {
  background-color: #5b6e96 !important;
  color: #dde3f0 !important;
  font-weight: 700 !important;
  text-align: center !important;
  padding: 1.25rem 0 !important;
}
:deep(.table-header .p-datatable-column-header-content) {
  justify-content: center;
}
:deep(.p-datatable-tbody > tr > td) {
  text-align: center !important;
  border-bottom: 1px solid #f2f0eb !important;
}
:deep(.p-paginator-page-selected) {
  background-color: #fd9e0f !important;
  color: #ffffff !important;
}
</style>
