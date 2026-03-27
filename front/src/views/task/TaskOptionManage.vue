<script setup>
import TaskCategoryModal from '@/components/task/TaskCategoryModal.vue';
import TaskTypeModal from '@/components/task/TaskTypeModal.vue';
import { ref } from 'vue';

// ── 일감 유형 예시 데이터 ────────────────────────────────────
const taskTypes = ref([
  { id: 'TT-001', name: '새기능', defaultStatus: '신규', description: '새로운 기능을 추가하거나 기존 기능을 개선하는 작업' },
  { id: 'TT-002', name: '결함', defaultStatus: '진행', description: '버그 또는 오류를 수정하는 작업' },
  { id: 'TT-003', name: '지원', defaultStatus: '신규', description: '사용자 문의 및 기술 지원 관련 작업' }
]);

// ── 일감 범주 예시 데이터 ────────────────────────────────────
const taskCategories = ref([
  { id: 'TC-001', name: '보안', description: '인증, 권한, 보안 취약점 등 보안 관련 작업' },
  { id: 'TC-002', name: '프론트', description: 'UI/UX 및 클라이언트 사이드 개발 작업' },
  { id: 'TC-003', name: '백', description: '서버, API, 데이터베이스 관련 개발 작업' }
]);

// ── 상태 클래스 맵핑 ─────────────────────────────────────────
const workflowClass = {
  신규: 'status-new',
  진행: 'status-in-progress',
  해결: 'status-resolved',
  반려: 'status-rejected',
  완료: 'status-done'
};

// ── 모달 상태 ────────────────────────────────────────────────
const typeModalVisible = ref(false);
const typeModalMode = ref('add');
const typeModalData = ref(null);

const catModalVisible = ref(false);
const catModalMode = ref('add');
const catModalData = ref(null);

// ── 일감 유형 CRUD ───────────────────────────────────────────
function openTypeAdd() {
  typeModalData.value = null;
  typeModalMode.value = 'add';
  typeModalVisible.value = true;
}
function openTypeEdit(item) {
  typeModalData.value = { ...item };
  typeModalMode.value = 'edit';
  typeModalVisible.value = true;
}
function saveType(payload) {
  if (typeModalMode.value === 'add') {
    taskTypes.value.push({ id: 'TT-' + String(taskTypes.value.length + 1).padStart(3, '0'), ...payload });
  } else {
    const idx = taskTypes.value.findIndex((t) => t.id === payload.id);
    if (idx !== -1) taskTypes.value[idx] = { ...payload };
  }
  typeModalVisible.value = false;
}
function deleteType(id) {
  if (confirm('해당 일감 유형을 삭제하시겠습니까?')) {
    taskTypes.value = taskTypes.value.filter((t) => t.id !== id);
  }
}

// ── 일감 범주 CRUD ───────────────────────────────────────────
function openCatAdd() {
  catModalData.value = null;
  catModalMode.value = 'add';
  catModalVisible.value = true;
}
function openCatEdit(item) {
  catModalData.value = { ...item };
  catModalMode.value = 'edit';
  catModalVisible.value = true;
}
function saveCat(payload) {
  if (catModalMode.value === 'add') {
    taskCategories.value.push({ id: 'TC-' + String(taskCategories.value.length + 1).padStart(3, '0'), ...payload });
  } else {
    const idx = taskCategories.value.findIndex((c) => c.id === payload.id);
    if (idx !== -1) taskCategories.value[idx] = { ...payload };
  }
  catModalVisible.value = false;
}
function deleteCat(id) {
  if (confirm('해당 일감 범주를 삭제하시겠습니까?')) {
    taskCategories.value = taskCategories.value.filter((c) => c.id !== id);
  }
}
</script>

<template>
  <div class="min-h-screen bg-stone-50 px-10 py-8 text-stone-700">
    <!-- ── 페이지 헤더 ──────────────────────────────────────── -->
    <div class="mb-7">
      <h1 class="text-2xl font-bold tracking-tight text-stone-900">일감 유형 &amp; 범주 관리</h1>
    </div>

    <!-- ── 상하 레이아웃 ───────────────────────────────────── -->
    <div class="flex flex-col gap-6">
      <!-- ══════════════ 위: 일감 유형 관리 ══════════════ -->
      <div class="bg-white border border-stone-200 rounded-xl shadow-sm overflow-hidden">
        <!-- 카드 헤더 -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-stone-100">
          <div>
            <p class="text-lg font-bold text-stone-800">일감 유형 관리</p>
            <p class="text-base text-stone-400 mt-0.5">
              총 <strong class="text-stone-600">{{ taskTypes.length }}</strong
              >개
            </p>
          </div>
          <Button
            label="일감 유형 추가"
            icon="pi pi-plus"
            class="!bg-amber-600 !border-amber-600 hover:!bg-amber-700 hover:!border-amber-700 !text-amber-50 !font-semibold !text-sm !px-4 !py-2 !rounded-lg !shadow-md !shadow-amber-200 transition-all duration-150 hover:!-translate-y-0.5"
            @click="openTypeAdd"
          />
        </div>

        <!-- 테이블 -->
        <div class="overflow-x-auto">
          <table class="w-full border-collapse">
            <thead>
              <tr class="bg-stone-100 border-b border-stone-200">
                <th class="px-5 py-3.5 text-left text-base font-bold uppercase tracking-wider text-stone-400 w-36">유형명</th>
                <th class="px-5 py-3.5 text-center text-base font-bold uppercase tracking-wider text-stone-400 w-32">초기 상태</th>
                <th class="px-5 py-3.5 text-left text-base font-bold uppercase tracking-wider text-stone-400">설명</th>
                <th class="px-5 py-3.5 text-center text-base font-bold uppercase tracking-wider text-stone-400 w-28">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in taskTypes" :key="item.id" class="border-b border-stone-100 last:border-none hover:bg-amber-50 transition-colors duration-100" :class="idx % 2 !== 0 ? 'bg-stone-50/60' : 'bg-white'">
                <td class="px-5 py-5">
                  <span class="text-base font-semibold text-stone-800">{{ item.name }}</span>
                </td>
                <td class="px-5 py-5 text-center">
                  <span class="inline-block px-3 py-0.5 rounded-full text-sm font-semibold whitespace-nowrap" :class="workflowClass[item.defaultStatus] ?? 'bg-stone-100 text-stone-500'">
                    {{ item.defaultStatus }}
                  </span>
                </td>
                <td class="px-5 py-5 text-stone-500 text-base leading-relaxed max-w-0">
                  <p class="truncate">{{ item.description }}</p>
                </td>
                <td class="px-5 py-5">
                  <div class="flex items-center justify-center gap-1.5">
                    <Button icon="pi pi-pencil" text rounded class="!w-8 !h-8 !text-stone-400 hover:!text-amber-600 hover:!bg-amber-50 transition-colors" v-tooltip.top="'수정'" @click="openTypeEdit(item)" />
                    <Button icon="pi pi-trash" text rounded class="!w-8 !h-8 !text-stone-400 hover:!text-red-500 hover:!bg-red-50 transition-colors" v-tooltip.top="'삭제'" @click="deleteType(item.id)" />
                  </div>
                </td>
              </tr>
              <tr v-if="taskTypes.length === 0">
                <td colspan="4" class="text-center py-14 text-base text-stone-400">등록된 일감 유형이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- ══════════════ 아래: 일감 범주 관리 ══════════════ -->
      <div class="bg-white border border-stone-200 rounded-xl shadow-sm overflow-hidden">
        <!-- 카드 헤더 -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-stone-100">
          <div>
            <p class="text-lg font-bold text-stone-800">일감 범주 관리</p>
            <p class="text-base text-stone-400 mt-0.5">
              총 <strong class="text-stone-600">{{ taskCategories.length }}</strong
              >개
            </p>
          </div>
          <Button
            label="일감 범주 추가"
            icon="pi pi-plus"
            class="!bg-amber-600 !border-amber-600 hover:!bg-amber-700 hover:!border-amber-700 !text-amber-50 !font-semibold !text-sm !px-4 !py-2 !rounded-lg !shadow-md !shadow-amber-200 transition-all duration-150 hover:!-translate-y-0.5"
            @click="openCatAdd"
          />
        </div>

        <!-- 테이블 -->
        <div class="overflow-x-auto">
          <table class="w-full border-collapse">
            <thead>
              <tr class="bg-stone-100 border-b border-stone-200">
                <th class="px-5 py-3.5 text-left text-base font-bold uppercase tracking-wider text-stone-400 w-36">범주명</th>
                <th class="px-5 py-3.5 text-left text-base font-bold uppercase tracking-wider text-stone-400">설명</th>
                <th class="px-5 py-3.5 text-center text-base font-bold uppercase tracking-wider text-stone-400 w-28">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in taskCategories" :key="item.id" class="border-b border-stone-100 last:border-none hover:bg-amber-50 transition-colors duration-100" :class="idx % 2 !== 0 ? 'bg-stone-50/60' : 'bg-white'">
                <td class="px-5 py-5">
                  <span class="text-base font-semibold text-stone-800">{{ item.name }}</span>
                </td>
                <td class="px-5 py-5 text-stone-500 text-base leading-relaxed max-w-0">
                  <p class="truncate">{{ item.description }}</p>
                </td>
                <td class="px-5 py-5">
                  <div class="flex items-center justify-center gap-1.5">
                    <Button icon="pi pi-pencil" text rounded class="!w-8 !h-8 !text-stone-400 hover:!text-amber-600 hover:!bg-amber-50 transition-colors" v-tooltip.top="'수정'" @click="openCatEdit(item)" />
                    <Button icon="pi pi-trash" text rounded class="!w-8 !h-8 !text-stone-400 hover:!text-red-500 hover:!bg-red-50 transition-colors" v-tooltip.top="'삭제'" @click="deleteCat(item.id)" />
                  </div>
                </td>
              </tr>
              <tr v-if="taskCategories.length === 0">
                <td colspan="3" class="text-center py-14 text-base text-stone-400">등록된 일감 범주가 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- ── 모달 ──────────────────────────────────────────────── -->
    <TaskTypeModal v-model:visible="typeModalVisible" :mode="typeModalMode" :data="typeModalData" :existing-types="taskTypes" @save="saveType" @cancel="typeModalVisible = false" />
    <TaskCategoryModal v-model:visible="catModalVisible" :mode="catModalMode" :data="catModalData" @save="saveCat" @cancel="catModalVisible = false" />
  </div>
</template>

<style scoped>
/* ── 상태 배지 색상 ── */
.status-new {
  background-color: #f1efe8;
  color: #444441;
}
.status-in-progress {
  background-color: #eeedfe;
  color: #3c3489;
}
.status-resolved {
  background-color: #e1f5ee;
  color: #085041;
}
.status-rejected {
  background-color: #faece7;
  color: #712b13;
}
.status-done {
  background-color: #eaf3de;
  color: #27500a;
}
</style>
