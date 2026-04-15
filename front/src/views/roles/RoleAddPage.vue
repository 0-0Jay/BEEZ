<script setup>
import { useRolesStore } from '@/stores/roles';
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const rolesStore = useRolesStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const roleName = ref('');
const isAssignee = ref(false);

const permissionItems = ref([]); // 메뉴
const permissions = ref({}); // 체크 상태
const permissionTypes = ['조회', '등록', '수정', '삭제'];

const roleNameError = ref('');

onMounted(async () => {
  // 역할 목록 가져오기
  await rolesStore.findRoles();
  // 권한 목록 가져오기
  await rolesStore.findPermissions();
  initPermissionMatrix();

  const copyId = route.query.copyId;
  if (copyId) {
    await loadCopyData(copyId);
  }
});

// 권한 목록 조립
const initPermissionMatrix = () => {
  const rawData = rolesStore.perList;
  // console.log(rawData);
  const matrix = {};

  rawData.forEach((p) => {
    if (!matrix[p.category]) {
      matrix[p.category] = {
        label: p.category,
        key: p.category,
        permissionIds: {}
      };

      permissions.value[p.category] = {
        조회: false,
        등록: false,
        수정: false,
        삭제: false
      };
    }

    matrix[p.category].permissionIds[p.action] = p.id;
  });

  permissionItems.value = Object.values(matrix);
  // console.log(permissionItems.value);
};

// 모두 선택
const selectAll = () => {
  permissionItems.value.forEach((item) => {
    permissionTypes.forEach((type) => {
      // 해당 메뉴에 그 권한 ID가 존재할 때만 true
      if (item.permissionIds[type]) {
        permissions.value[item.key][type] = true;
      }
    });
  });
};

// 선택 해제
const deselectAll = () => {
  Object.keys(permissions.value).forEach((key) => {
    permissionTypes.forEach((type) => {
      permissions.value[key][type] = false;
    });
  });
};

// 역할 등록
const onSave = async () => {
  roleNameError.value = '';

  if (!roleName.value.trim()) {
    roleNameError.value = '역할 이름을 입력해 주세요.';
    return;
  }

  try {
    const selectedIds = [];

    permissionItems.value.forEach((item) => {
      ['조회', '등록', '수정', '삭제'].forEach((type) => {
        if (permissions.value[item.key][type]) selectedIds.push(item.permissionIds[type]);
      });
    });

    const payload = {
      name: roleName.value,
      isAssignee: isAssignee.value ? 'Y1' : 'Y0',
      perIds: selectedIds
    };

    await rolesStore.insertRoles(payload);

    toast.add({
      severity: 'success',
      summary: '등록 완료',
      detail: '새 역할이 등록되었습니다.',
      life: 3000,
      closable: false
    });

    router.push('/roles/list');
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || '';

    if (errorMsg.includes('존재') || errorMsg.includes('exists')) {
      roleNameError.value = '이미 존재하는 역할 이름입니다.';
    } else {
      toast.add({
        severity: 'error',
        summary: '등록 실패',
        detail: errorMsg || '등록 중 오류가 발생했습니다.',
        life: 3000,
        closable: false
      });
    }
  }
};

// 취소
const onCancel = () => {
  router.push('/roles/list');
};

// 역할 데이터 복사
const loadCopyData = async (id) => {
  try {
    const data = await rolesStore.findRolesDetail(id);

    // 기본 정보
    roleName.value = `${data.name}_복사본`;
    isAssignee.value = data.isAssignee === 'Y1';

    // 권한 체크박스
    permissionItems.value.forEach((item) => {
      ['조회', '등록', '수정', '삭제'].forEach((type) => {
        const perIds = data.perIds || [];
        const currentId = item.permissionIds[type];
        // perIds 배열에 해당 ID가 있으면 true로 변경
        if (perIds.includes(currentId)) {
          permissions.value[item.key][type] = true;
        }
      });
    });

    toast.add({
      severity: 'info',
      summary: '복사 모드',
      detail: '원본 데이터를 불러왔습니다.',
      life: 3000,
      closable: false
    });
  } catch (err) {
    console.error('데이터 복사 실패:', err);
    toast.add({
      severity: 'error',
      summary: '오류',
      detail: '원본 데이터를 가져오지 못했습니다.',
      life: 3000,
      closable: false
    });
  }
};
</script>

<template>
  <div class="p-8 bg-white">
    <h1 class="text-2xl font-bold text-[#1A1816] mb-8">새 역할 추가</h1>

    <div class="form-bar mb-6">
      <div class="flex items-center gap-3">
        <label class="form-label"> 역할 이름 <span class="text-red-500">*</span> </label>
        <InputText v-model="roleName" placeholder="역할 이름을 입력해 주세요." name="role_name" autocomplete="on" class="role-input" :class="{ 'p-invalid': roleNameError }" />
        <small v-if="roleNameError" class="text-red-500 text-xs"> {{ roleNameError }} </small>
      </div>

      <div class="flex items-center gap-2">
        <Checkbox v-model="isAssignee" binary inputId="isAssignee" />
        <label for="isAssignee" class="form-label cursor-pointer whitespace-nowrap"> 담당자 지정 가능 여부 </label>
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
          <Checkbox v-model="permissions[data.key][type]" binary :disabled="!data.permissionIds[type]" />
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
  padding: 15px 0 !important;
  border-bottom: 1px solid #f2f0eb !important;
}

:deep(.p-datatable-tbody > tr:hover > td) {
  background-color: #f8f9fb !important;
}

:deep(.p-datatable-tbody > tr:last-child > td) {
  border-bottom: none !important;
}

:deep(.p-inputtext.p-invalid) {
  border-color: #f5a623;
}

:deep(.p-inputtext.p-invalid::placeholder) {
  color: #736f68;
}
</style>
