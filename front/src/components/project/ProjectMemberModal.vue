<script setup>
import { useProjectStore } from '@/stores/project';
import { useToast } from 'primevue';
import { computed, ref } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible', 'saved']);
const projectStore = useProjectStore();
const toast = useToast();
const submitted = ref(false);

const searchKeyword = ref('');
const selectedUsers = ref([]);
const selectedGroups = ref([]);
const selectedRoles = ref([]);

const roles = computed(() => projectStore.roles);
const filteredUsers = ref([]);
const filteredGroups = ref([]);

const close = () => emit('update:visible', false);
const handleSave = () => {};
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
          <label v-for="user in filteredUsers" :key="user.id" class="flex items-center gap-1.5 text-sm text-stone-700 cursor-pointer">
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
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="저장" raised @click="handleSave" />
      </div>
    </template>
  </Dialog>
</template>
