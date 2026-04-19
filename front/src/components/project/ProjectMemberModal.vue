<script setup>
import { useProjectStore } from '@/stores/project';
import { useToast } from 'primevue';
import { computed, onUnmounted, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible', 'saved']);
const projectStore = useProjectStore();
const toast = useToast();

const selectedprojectId = projectStore.selectedProject.id;
const searchKeyword = ref('');
const selectedUsers = ref([]);
const selectedGroups = ref([]);
const selectedRoles = ref([]);
let timer = null;

// visible이 true가 될 때 초기 로드
watch(
  () => props.visible,
  async (val) => {
    if (val) {
      await projectStore.fetchSearchMembers();
    }
  }
);

const roles = computed(() => projectStore.roles);
const pmUsers = computed(() => projectStore.users.filter((u) => u.role === 'ROLE0002'));
const normalUsers = computed(() => projectStore.users.filter((u) => u.role !== 'ROLE0002'));
const filteredGroups = computed(() => projectStore.groups);

// 키워드 변경 시 자동 검색 (디바운스 적용)
watch(searchKeyword, (keyword) => {
  clearTimeout(timer);
  timer = setTimeout(async () => {
    await projectStore.fetchSearchMembers(keyword || null);
  }, 300);
});

//모달 닫힐 때 상태 초기화
const close = () => {
  searchKeyword.value = '';
  selectedUsers.value = [];
  selectedGroups.value = [];
  selectedRoles.value = [];
  emit('update:visible', false);
};

const handleSave = async () => {
  // 1. 프론트엔드 1차 체크 (서버에 가기 전에 미리 확인)
  if (selectedUsers.value.length === 0 && selectedGroups.value.length === 0) {
    toast.add({ severity: 'warn', summary: '구성원 미선택', detail: '추가할 사용자 또는 그룹을 선택해주세요.', life: 3000 });
    return;
  }

  if (selectedRoles.value.length === 0) {
    toast.add({ severity: 'warn', summary: '역할 미선택', detail: '최소 하나 이상의 역할을 선택해야 합니다.', life: 3000 });
    return;
  }

  const requestBody = {
    projectId: selectedprojectId,
    userIds: [...selectedUsers.value],
    groupIds: [...selectedGroups.value],
    roleIds: [...selectedRoles.value]
  };

  // 2. 에러 처리를 위한 try-catch 적용
  try {
    await projectStore.insertProjectMember(requestBody);

    // 성공 시 로직
    emit('saved'); // 부모 컴포넌트에 알림 (목록 갱신용)
    close(); // 모달 닫기
    toast.add({ severity: 'success', summary: '등록 완료', detail: '구성원이 성공적으로 추가되었습니다.', life: 2000 });
  } catch (error) {
    // 3. 프로시저에서 던진 ORA-20001, ORA-20002 등 서버 에러 처리
    console.error('저장 중 오류 발생:', error);

    // 서버에서 전달한 에러 메시지가 있다면 그걸 보여주고, 없으면 기본 메시지 출력
    const errorMessage = error.response?.data?.message || '저장 중 오류가 발생했습니다.';
    toast.add({ severity: 'error', summary: '저장 실패', detail: errorMessage, life: 3000 });
  }
};

onUnmounted(() => {
  if (timer) clearTimeout(timer);
});
</script>
<template>
  <Dialog :visible="visible" header="새 구성원" modal :style="{ width: '560px' }" @update:visible="close">
    <div class="flex flex-col gap-4 pt-1">
      <!-- 섹션 1: 사용자 및 그룹 찾기 -->
      <div class="bg-stone-50 rounded-xl p-4">
        <p class="text-xs font-medium text-stone-500 mb-3">사용자 및 그룹 찾기</p>

        <!-- 검색창 -->
        <div class="relative mb-4">
          <InputText v-model="searchKeyword" placeholder="검색어를 입력해주세요." class="w-full" />
          <i class="pi pi-search absolute right-3 top-1/2 -translate-y-1/2 text-stone-400" />
        </div>

        <!-- 사용자 목록 -->
        <p class="text-sm font-medium text-stone-700 mb-2">사용자</p>
        <div class="flex flex-wrap gap-x-5 gap-y-2 mb-4">
          <!-- ROLE0002 먼저 -->
          <label v-for="user in pmUsers" :key="user.id" class="flex items-center gap-1.5 text-sm text-stone-700 cursor-pointer">
            <Checkbox :value="user.id" v-model="selectedUsers" />
            {{ user.name }}
          </label>

          <!-- 구분선 (둘 다 있을 때만) -->
          <div v-if="pmUsers.length > 0 && normalUsers.length > 0" class="w-full border-t border-stone-200 my-1" />

          <!-- 나머지 -->
          <label v-for="user in normalUsers" :key="user.id" class="flex items-center gap-1.5 text-sm text-stone-700 cursor-pointer">
            <Checkbox :value="user.id" v-model="selectedUsers" />
            {{ user.name }}
          </label>
        </div>

        <Divider />

        <!-- 그룹 목록 -->
        <p class="text-sm font-medium text-stone-700 mb-2">그룹</p>
        <div class="flex flex-wrap gap-x-5 gap-y-2">
          <label v-for="group in filteredGroups" :key="group.id" class="flex items-center gap-1.5 text-sm text-stone-700 cursor-pointer">
            <Checkbox :value="group.id" v-model="selectedGroups" />
            {{ group.name }}
          </label>
        </div>
      </div>

      <!-- 섹션 2: 역할 -->
      <div class="bg-stone-50 rounded-xl p-4">
        <p class="text-xs font-medium text-stone-500 mb-3">역할</p>
        <div class="flex flex-wrap gap-x-5 gap-y-2">
          <label v-for="role in roles" :key="role.roleId" class="flex items-center gap-1.5 text-sm text-stone-700 cursor-pointer">
            <Checkbox :value="role.roleId" v-model="selectedRoles" />
            {{ role.roleName }}
          </label>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="저장" raised @click="handleSave" />
        <Button label="취소" severity="secondary" raised @click="close" />
      </div>
    </template>
  </Dialog>
</template>
