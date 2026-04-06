<script setup>
import Checkbox from 'primevue/checkbox';
import { ref } from 'vue';

// 권한 항목
const permissionItems = ref([
  { key: 'project', label: '프로젝트' },
  { key: 'user', label: '사용자' },
  { key: 'group', label: '그룹' },
  { key: 'board', label: '게시판' },
  { key: 'calendar', label: '달력' },
  { key: 'document', label: '문서' },
  { key: 'file', label: '파일' },
  { key: 'gantt', label: '간트차트' },
  { key: 'worklog', label: '일감관리' },
  { key: 'notice', label: '공지사항' },
  { key: 'storage', label: '저장소' },
  { key: 'timespent', label: '소요시간' },
  { key: 'roadmap', label: '로드맵' },
  { key: 'wiki', label: '위키' },
  { key: 'changelog', label: '변경 로그' }
]);

const permissionTypes = ['조회', '등록', '수정', '삭제'];

// 권한 초기화 함수
function createPermissions() {
  const obj = {};
  permissionItems.value.forEach((item) => {
    obj[item.key] = Object.fromEntries(permissionTypes.map((type) => [type, false]));
  });
  return obj;
}

const permissions = ref(createPermissions());

// 폼 데이터
const roleName = ref('');
const canAssignManager = ref(false);
const workflowCopy = ref('');

// 옵션
const workflowOptions = [
  { value: '', label: '선택' },
  { value: 'default', label: '기본' },
  { value: 'custom', label: '커스텀' }
];

// 전체 선택 / 해제
function setAllPermissions(value) {
  permissionItems.value.forEach((item) => {
    permissionTypes.forEach((type) => {
      permissions.value[item.key][type] = value;
    });
  });
}

const selectAll = () => setAllPermissions(true);
const deselectAll = () => setAllPermissions(false);

// 저장
function onSave() {
  const payload = {
    roleName: roleName.value,
    canAssignManager: canAssignManager.value,
    workflowCopy: workflowCopy.value,
    permissions: permissions.value
  };

  console.log('저장 데이터:', payload);
  alert('저장되었습니다.');
}

// 취소
function onCancel() {
  alert('취소되었습니다.');
}
</script>

<template>
  <div class="p-8 bg-white">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-8">새 역할 추가</h1>

    <div class="form-bar mb-6">
      <div class="flex items-center gap-3">
        <label class="form-label"> 역할 이름 <span class="text-red-500">*</span> </label>
        <InputText v-model="roleName" placeholder="역할 이름을 입력해 주세요." class="role-input" />
      </div>

      <div class="flex items-center gap-2">
        <Checkbox v-model="canAssignManager" binary inputId="canAssignManager" />
        <label for="canAssignManager" class="form-label cursor-pointer whitespace-nowrap"> 담당자 지정 가능 여부 </label>
      </div>

      <div class="flex items-center gap-3">
        <label class="form-label whitespace-nowrap">업무 흐름 복사</label>
        <Select v-model="workflowCopy" :options="workflowOptions" optionLabel="label" optionValue="value" placeholder="선택" class="role-pv-select" />
      </div>
    </div>

    <div class="flex justify-end gap-2 mb-2 text-sm">
      <button @click="selectAll" class="text-[#3A3B35] hover:underline font-medium">모두 선택</button>
      <span class="text-[#C7C7C2]">|</span>
      <button @click="deselectAll" class="text-[#3A3B35] hover:underline font-medium">선택 해제</button>
    </div>

    <DataTable :value="permissionItems" dataKey="key" class="perm-table">
      <Column field="label" header="권한" headerClass="perm-header" bodyClass="perm-body-label" style="width: 140px" />

      <Column v-for="type in permissionTypes" :key="type" :header="type" headerClass="perm-header" bodyClass="perm-body-cell">
        <template #body="{ data }">
          <Checkbox v-model="permissions[data.key][type]" binary />
        </template>
      </Column>
    </DataTable>

    <div class="flex justify-center gap-3 mt-6">
      <Button label="취소" severity="secondary" raised @click="onCancel" />
      <Button label="등록" raised @click="onSave" />
    </div>
  </div>
</template>

<style scoped>
.form-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 2rem;
  background: #f8f9fb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 1.25rem 1.75rem;
}

.form-label {
  font-size: 15px;
  color: #1a1816;
  white-space: nowrap;
}

.role-input {
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 8px 14px;
  font-size: 14px;
  color: #1a1816;
  width: 220px;
  outline: none;
  transition: border-color 0.2s;
  background: #fff;
}
.role-input:focus {
  border-color: #fd9e0f;
}
.role-input::placeholder {
  color: #b0b0a8;
}

:deep(.perm-table) {
  border: 1px solid #5b6e96;
  border-radius: 10px;
  overflow: hidden;
  font-size: 13px;
}

:deep(.perm-header) {
  background-color: #5b6e96 !important;
  color: #dde3f0 !important;
  font-weight: 700 !important;
  text-align: center !important;
  padding: 17.5px 0 !important;
  font-size: 14px;
}
:deep(.perm-header .p-datatable-column-header-content) {
  justify-content: center;
}

:deep(.perm-body-label) {
  text-align: center !important;
  padding: 7px 12px !important;
  color: #3a3b35;
  font-weight: 500;
  border-right: 1px solid #f2f0eb !important;
  border-bottom: 1px solid #f2f0eb !important;
}

:deep(.perm-body-cell) {
  text-align: center !important;
  padding: 10px 0 !important;
  border-bottom: 1px solid #f2f0eb !important;
}

:deep(.p-datatable-tbody > tr:hover > td) {
  background-color: #f8f9fb !important;
}

:deep(.p-datatable-tbody > tr:last-child > td) {
  border-bottom: none !important;
}
</style>
