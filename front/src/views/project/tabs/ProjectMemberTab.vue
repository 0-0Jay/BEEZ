<script setup>
import ProjectMemberModal from '@/components/project/ProjectMemberModal.vue';
import { useProjectStore } from '@/stores/project';
import { useToast } from 'primevue';
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const projectStore = useProjectStore();
const route = useRoute();
const projectId = route.params.id;
const pendingDeleteId = ref(null); // 삭제할 id 임시 저장
const visible = ref(false);
const confirmMsg = ref('');
const toast = useToast();
const modalVisible = ref(false); // 추가 모달

onMounted(async () => {
  await projectStore.fetchProjectMembers(projectId);
  await projectStore.fetchRoles();
  await projectStore.findProject(projectId);
});

const projectInfo = computed(() => projectStore.projectInfo);
const availableRoles = computed(() => projectStore.roles);
const userList = computed(() => projectStore.members.userList);
const groupList = computed(() => projectStore.members.groupList);
const getGroupMembers = (groupId) => {
  return projectStore.members.groupMemberList.filter((m) => m.groupId === groupId);
};

//상속 받은 역할 함수
function isInherited(member, roleId) {
  return member.roles?.some((r) => r.roleId === roleId && r.isInherited === 'P1') ?? false;
}

// ── 아코디언 상태 ─────────────────────────────────────────────
// expandedEditId: 현재 편집 아코디언이 열린 항목 id
const expandedEditId = ref(null);

// 편집 중인 역할 임시 상태 { [id]: Set<roleId> }
const editingRoles = ref({});

function openEdit(id, currentRoleIds) {
  if (expandedEditId.value === id) {
    expandedEditId.value = null;
    return;
  }
  expandedEditId.value = id;
  editingRoles.value[id] = new Set(currentRoleIds ?? []);
}

function closeEdit() {
  expandedEditId.value = null;
}

function isRoleChecked(id, roleId) {
  return editingRoles.value[id]?.has(roleId) ?? false;
}

function toggleRole(id, roleId) {
  if (!editingRoles.value[id]) editingRoles.value[id] = new Set();
  const s = editingRoles.value[id];
  if (s.has(roleId)) s.delete(roleId);
  else s.add(roleId);
}

async function saveEdit(member, isGroupMember = false) {
  const newRoles = [...(editingRoles.value[member.projectMemberId] ?? [])];

  let roleIds;
  if (isGroupMember) {
    // 그룹 멤버: 상속받은 역할 제외하고 직접 부여 역할만 저장
    roleIds = newRoles.filter((roleId) => !isInherited(member, roleId));
  } else {
    roleIds = newRoles;
  }

  await projectStore.updateProjectMember(member.projectMemberId, roleIds);
  closeEdit();
  await projectStore.fetchProjectMembers(projectId);
}

// 프로젝트 구성원 삭제
const openDeleteConfirm = (projectMemberId) => {
  pendingDeleteId.value = projectMemberId;
  confirmMsg.value = '정말 삭제하시겠습니까?';
  visible.value = true;
};

const handleDelConfirm = async () => {
  visible.value = false;
  if (pendingDeleteId.value !== null) {
    await projectStore.deleteProjectMember(pendingDeleteId.value);
    pendingDeleteId.value = null;
    await projectStore.fetchProjectMembers(projectId);
    toast.add({ severity: 'success', summary: '삭제 완료', detail: '구성원이 삭제되었습니다.', life: 2000 });
  }
};

// 구성원 추가 모달
const openAddModal = () => {
  modalVisible.value = true;
};

const closeAddModal = () => {
  modalVisible.value = false;
};

// 구성원 추가 완료 시 호출될 함수
const handleMemberSaved = async () => {
  await projectStore.fetchProjectMembers(projectId);
};
</script>

<template>
  <div class="bg-[#ffffff] h-full">
    <!-- 상단 버튼 영역 -->
    <div class="flex justify-end gap-2 mb-4">
      <Button label="구성원 추가" icon="pi pi-user-plus" outlined class="btn-outline" @click="openAddModal" />
    </div>

    <!-- 테이블 -->
    <div class="members-table">
      <!-- 헤더 -->
      <div class="table-header-row">
        <div class="col-name">그룹 / 사용자</div>
        <div class="col-role">역할</div>
        <div class="col-actions"></div>
      </div>

      <!-- 개별 사용자 -->
      <template v-for="user in userList" :key="user.projectMemberId">
        <div class="table-row">
          <div class="col-name">
            <span>{{ user.userName }}</span>
            <span v-if="user.userId === projectInfo.userId" class="fixed-badge">생성자</span>
            <span v-else-if="user.userId === projectInfo.pmId" class="fixed-badge">담당 PM/PL</span>
            <span v-else-if="user.fixedReason" class="fixed-badge">{{ user.fixedReason }}</span>
          </div>
          <div class="col-role text-gray">{{ user.roles?.map((r) => r.roleName).join(', ') || '-' }}</div>
          <div class="col-actions">
            <template v-if="user.userId !== projectInfo.userId && user.userId !== projectInfo.pmId && !user.fixedReason">
              <button
                class="action-btn edit-btn"
                @click="
                  openEdit(
                    user.projectMemberId,
                    user.roles?.map((r) => r.roleId)
                  )
                "
              >
                <i class="pi pi-pencil" /> 편집
              </button>
              <button class="action-btn delete-btn" @click="openDeleteConfirm(user.projectMemberId)"><i class="pi pi-trash" /> 삭제</button>
            </template>
          </div>
        </div>
        <Transition name="accordion">
          <div v-if="expandedEditId === user.projectMemberId" class="accordion-row">
            <div class="col-name"></div>
            <div class="col-role">
              <div class="role-checklist">
                <label v-for="role in availableRoles" :key="role.roleId" class="role-item">
                  <input type="checkbox" :checked="isRoleChecked(user.projectMemberId, role.roleId)" @change="toggleRole(user.projectMemberId, role.roleId)" />
                  <span>{{ role.roleName }}</span>
                </label>
              </div>
            </div>
            <div class="col-actions accordion-actions">
              <button class="btn-save" @click="saveEdit(user)">저장</button>
              <button class="btn-cancel" @click="closeEdit">취소</button>
            </div>
          </div>
        </Transition>
      </template>

      <!-- 그룹 -->
      <template v-for="group in groupList" :key="group.projectMemberId">
        <div class="table-row">
          <div class="col-name">
            <span>{{ group.groupName }}</span>
          </div>
          <div class="col-role text-gray">{{ group.roles?.map((r) => r.roleName).join(', ') || '-' }}</div>
          <div class="col-actions">
            <button
              class="action-btn edit-btn"
              @click="
                openEdit(
                  group.projectMemberId,
                  group.roles?.map((r) => r.roleId)
                )
              "
            >
              <i class="pi pi-pencil" /> 편집
            </button>
            <button class="action-btn delete-btn" @click="openDeleteConfirm(group.projectMemberId)"><i class="pi pi-trash" /> 삭제</button>
          </div>
        </div>
        <!-- 그룹 편집 아코디언 (멤버 목록 위) -->
        <Transition name="accordion">
          <div v-if="expandedEditId === group.projectMemberId" class="accordion-row">
            <div class="col-name"></div>
            <div class="col-role">
              <div class="role-checklist">
                <label v-for="role in availableRoles" :key="role.roleId" class="role-item">
                  <input type="checkbox" :checked="isRoleChecked(group.projectMemberId, role.roleId)" @change="toggleRole(group.projectMemberId, role.roleId)" />
                  <span>{{ role.roleName }}</span>
                </label>
              </div>
            </div>
            <div class="col-actions accordion-actions">
              <button class="btn-save" @click="saveEdit(group)">저장</button>
              <button class="btn-cancel" @click="closeEdit">취소</button>
            </div>
          </div>
        </Transition>

        <!-- 그룹 멤버 행들 -->
        <template v-for="member in getGroupMembers(group.groupId)" :key="member.projectMemberId">
          <div class="table-row member-row">
            <div class="col-name">
              <span class="member-indent">└ </span>
              <span>{{ member.userName }}</span>
            </div>
            <div class="col-role text-gray">{{ member.roles?.map((r) => r.roleName).join(', ') || '-' }}</div>
            <div class="col-actions">
              <button
                class="action-btn edit-btn"
                @click="
                  openEdit(
                    member.projectMemberId,
                    member.roles?.map((r) => r.roleId)
                  )
                "
              >
                <i class="pi pi-pencil" /> 편집
              </button>
            </div>
          </div>
          <!-- 멤버 편집 아코디언 -->
          <Transition name="accordion">
            <div v-if="expandedEditId === member.projectMemberId" class="accordion-row">
              <div class="col-name"></div>
              <div class="col-role">
                <div class="role-checklist">
                  <label v-for="role in availableRoles" :key="role.roleId" class="role-item" :class="{ 'role-inherited': isInherited(member, role.roleId) }">
                    <input type="checkbox" :checked="isRoleChecked(member.projectMemberId, role.roleId)" :disabled="isInherited(member, role.roleId)" @change="!isInherited(member, role.roleId) && toggleRole(member.projectMemberId, role.roleId)" />
                    <span>
                      {{ role.roleName }}
                      <span v-if="isInherited(member, role.roleId)" class="inherited-label"> ({{ group.groupName }} 그룹으로부터 상속) </span>
                    </span>
                  </label>
                </div>
              </div>
              <div class="col-actions accordion-actions">
                <button class="btn-save" @click="saveEdit(member, true, entry)">저장</button>
                <button class="btn-cancel" @click="closeEdit">취소</button>
              </div>
            </div>
          </Transition>
        </template>
      </template>

      <!-- 빈 상태 -->
      <div v-if="userList.length === 0 && groupList.length === 0" class="empty-state">등록된 구성원이 없습니다.</div>
    </div>
  </div>
  <ConfirmDialog v-model:visible="visible" confirmLabel="확인" @confirm="handleDelConfirm">
    <span class="text-gray-700 font-medium">{{ confirmMsg }}</span>
  </ConfirmDialog>
  <ProjectMemberModal
    v-model:visible="modalVisible"
    @saved="handleMemberSaved"
    @update:visible="
      (val) => {
        if (!val) closeAddModal();
      }
    "
  />
</template>

<style scoped>
/* ── 공통 변수 ── */
:root {
  --color-bg: #f2f0eb;
  --color-border: #c7c7c2;
  --color-text: #3a3b35;
  --color-link: #4a7fc1;
  --color-gray: #6b6b63;
  --color-accordion: #e8e6e0;
  --color-red: #e57373;
}

/* ── 버튼 ── */
.btn-outline {
  border: 1px solid var(--color-border) !important;
  color: var(--color-text) !important;
  background: white !important;
  height: 36px !important;
  font-size: 13px !important;
}

.btn-solid {
  background: #c0392b !important;
  border-color: #c0392b !important;
  color: white !important;
  height: 36px !important;
  font-size: 13px !important;
}

/* ── 테이블 전체 ── */
.members-table {
  background: white;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  overflow: hidden;
  font-size: 13px;
  color: var(--color-text);
}

/* ── 헤더 ── */
.table-header-row {
  display: grid;
  grid-template-columns: 1fr 220px 180px;
  background: var(--color-bg);
  padding: 10px 16px;
  font-weight: 600;
  border-bottom: 1px solid var(--color-border);
  color: var(--color-text);
}

/* ── 일반 행 ── */
.table-row {
  display: grid;
  grid-template-columns: 1fr 220px 180px;
  padding: 10px 16px;
  border-bottom: 1px solid #ebebeb;
  align-items: center;
  transition: background 0.15s;
}
.table-row:hover {
  background: #fafaf8;
}
.table-row:last-child {
  border-bottom: none;
}

.member-row {
  background: #f9f8f5;
}
.member-row:hover {
  background: #f2f0eb;
}

.fixed-badge {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f0eeea;
  color: var(--color-gray);
  border: 1px solid var(--color-border);
}

/* ── 칸 ── */
.col-name {
  display: flex;
  align-items: center;
  gap: 4px;
}
.col-role {
  display: flex;
  align-items: center;
}
.col-actions {
  display: flex;
  gap: 6px;
  align-items: center;
  justify-content: flex-end;
}

.text-gray {
  color: var(--color-gray);
}

.member-indent {
  color: var(--color-gray);
  margin-right: 2px;
  font-size: 12px;
}

/* ── 액션 버튼 ── */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.12s;
}
.edit-btn {
  color: var(--color-gray);
}
.edit-btn:hover {
  background: #f0eeea;
}
.delete-btn {
  color: var(--color-red);
}
.delete-btn:hover {
  background: #fdf0f0;
}

/* ── 아코디언 행 ── */
.accordion-row {
  display: grid;
  grid-template-columns: 1fr 220px 180px;
  padding: 12px 16px;
  background: var(--color-accordion);
  border-bottom: 1px solid var(--color-border);
  align-items: start;
}

.role-checklist {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.role-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
  color: var(--color-text);
}
.role-item input[type='checkbox'] {
  accent-color: #c0392b;
  width: 14px;
  height: 14px;
  cursor: pointer;
}
.role-inherited {
  opacity: 0.65;
  cursor: default;
}
.role-inherited input {
  cursor: not-allowed;
}

.inherited-label {
  font-size: 11px;
  color: var(--color-gray);
}

.accordion-actions {
  display: flex;
  gap: 6px;
  align-items: flex-start;
  justify-content: flex-end;
}

.btn-save,
.btn-cancel {
  padding: 4px 14px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid var(--color-border);
}
.btn-save {
  background: white;
  color: var(--color-text);
}
.btn-save:hover {
  background: #f0eeea;
}
.btn-cancel {
  background: white;
  color: var(--color-gray);
}
.btn-cancel:hover {
  background: #f0eeea;
}

/* ── 빈 상태 ── */
.empty-state {
  text-align: center;
  padding: 40px;
  color: #aaa;
  font-size: 13px;
}

/* ── 아코디언 트랜지션 ── */
.accordion-enter-active,
.accordion-leave-active {
  transition: all 0.2s ease;
  overflow: hidden;
}
.accordion-enter-from,
.accordion-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}
.accordion-enter-to,
.accordion-leave-from {
  opacity: 1;
  max-height: 200px;
}
</style>
