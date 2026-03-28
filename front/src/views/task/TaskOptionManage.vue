<script setup>
import TaskCategoryModal from '@/components/task/TaskCategoryModal.vue';
import TaskTypeModal from '@/components/task/TaskTypeModal.vue';
import { useTaskStore } from '@/stores/task';
import { computed, onMounted, ref } from 'vue';

const taskStore = useTaskStore();

const taskTypes = computed(() => taskStore.typeList);

const taskCategories = computed(() => taskStore.cateList);

const workflowClass = {
  Q0: { name: '신규', color: 'status-new' },
  Q1: { name: '진행', color: 'status-in-progress' },
  Q2: { name: '해결', color: 'status-resolved' },
  Q3: { name: '반려', color: 'status-rejected' },
  Q4: { name: '완료', color: 'status-done' }
};

const typeModalVisible = ref(false);
const typeModalMode = ref('add');
const typeModalData = ref(null);
const cateModalVisible = ref(false);
const cateModalMode = ref('add');
const cateModalData = ref(null);

// 일감 유형 추가
function openTypeAdd() {
  typeModalData.value = null;
  typeModalMode.value = 'add';
  typeModalVisible.value = true;
}
// 일감 유형 수정
function openTypeEdit(item) {
  typeModalData.value = { ...item };
  typeModalMode.value = 'edit';
  typeModalVisible.value = true;
}
// 일감 유형 저장
async function saveType(payload) {
  if (typeModalMode.value === 'add') {
    await taskStore.insertType(payload);
  } else {
    await taskStore.updateType(payload);
  }
  await taskStore.findTypeList();
  typeModalVisible.value = false;
}
// 일감 유형 삭제
async function deleteType(id) {
  if (confirm('해당 일감 유형을 삭제하시겠습니까?')) {
    await taskStore.deleteType(id);
    await taskStore.findTypeList();
  }
}

// 일감 범주 추가
function openCatAdd() {
  cateModalData.value = null;
  cateModalMode.value = 'add';
  cateModalVisible.value = true;
}
// 일감 범주 수정
function openCateEdit(item) {
  cateModalData.value = { ...item };
  cateModalMode.value = 'edit';
  cateModalVisible.value = true;
}
// 일감 범주 저장
async function saveCate(payload) {
  if (cateModalMode.value === 'add') {
    await taskStore.insertCate(payload);
  } else {
    await taskStore.updateCate(payload);
  }
  await taskStore.findCateList();
  cateModalVisible.value = false;
}
// 일감 범주 삭제
async function deleteCate(id) {
  if (confirm('해당 일감 범주를 삭제하시겠습니까?')) {
    await taskStore.deleteCate(id);
    await taskStore.findCateList();
  }
}

onMounted(async () => {
  await taskStore.findTypeList();
  await taskStore.findCateList();
  console.log(taskCategories);
});
</script>

<template>
  <div class="min-h-screen bg-stone-50 px-10 py-8 text-stone-700">
    <div class="mb-7">
      <h1 class="text-2xl font-bold tracking-tight text-stone-900">일감 유형 &amp; 범주 관리</h1>
    </div>

    <div class="flex flex-col gap-6">
      <div class="bg-white border border-stone-200 rounded-xl shadow-sm overflow-hidden">
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
                  <span class="inline-block px-3 py-0.5 rounded-full text-sm font-semibold whitespace-nowrap" :class="workflowClass[item.defaultStatus].color ?? 'bg-stone-100 text-stone-500'">
                    {{ workflowClass[item.defaultStatus].name }}
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

      <div class="bg-white border border-stone-200 rounded-xl shadow-sm overflow-hidden">
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
                    <Button icon="pi pi-pencil" text rounded class="!w-8 !h-8 !text-stone-400 hover:!text-amber-600 hover:!bg-amber-50 transition-colors" v-tooltip.top="'수정'" @click="openCateEdit(item)" />
                    <Button icon="pi pi-trash" text rounded class="!w-8 !h-8 !text-stone-400 hover:!text-red-500 hover:!bg-red-50 transition-colors" v-tooltip.top="'삭제'" @click="deleteCate(item.id)" />
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

    <TaskTypeModal v-model:visible="typeModalVisible" :mode="typeModalMode" :data="typeModalData" :existing-types="taskTypes" @save="saveType" @cancel="typeModalVisible = false" />
    <TaskCategoryModal v-model:visible="cateModalVisible" :mode="cateModalMode" :data="cateModalData" @save="saveCate" @cancel="cateModalVisible = false" />
  </div>
</template>

<style scoped>
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
