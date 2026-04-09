<script setup>
import MatrixTable from '@/components/roles/MatrixTable.vue';
import WorkflowCopyModal from '@/components/workflow/WorkflowCopyModal.vue';
import { useRolesStore } from '@/stores/roles';
import { useTaskStore } from '@/stores/task';
import { useWorkflowStore } from '@/stores/workflow';
import { useToast } from 'primevue';
import { computed, onMounted, reactive, ref, watch } from 'vue';

const wStore = useWorkflowStore();
const rStore = useRolesStore();
const tStore = useTaskStore();
const toast = useToast();

const visible = ref(false);

const statusList = ref([]);
const statusCodes = computed(() => statusList.value.map((s) => s.key));

const roleOptions = ref([]);
const issueTypeOptions = ref([]);

const selectedRole = ref(null);
const selectedIssueType = ref(null);

// 매트릭스 관리(Z0: 일반, Z1: 저자, Z2: 담당자)
const workflowMatrices = reactive({
  Z0: {},
  Z1: {},
  Z2: {}
});

// 데이터 조회
const loadWorkflowData = async () => {
  if (!selectedRole.value || !selectedIssueType.value) return;

  const params = {
    roleId: selectedRole.value,
    typeId: selectedIssueType.value
  };

  await Promise.all([wStore.findWorkflowList({ ...params, conditionType: 'Z0' }), wStore.findWorkflowList({ ...params, conditionType: 'Z1' }), wStore.findWorkflowList({ ...params, conditionType: 'Z2' })]);

  Object.assign(workflowMatrices, wStore.workflowMatrices);
};

// 셀렉트 값 변경 시 자동 재조회
watch([selectedRole, selectedIssueType], async () => {
  await loadWorkflowData();
});

onMounted(async () => {
  try {
    await wStore.findTaskStatus('0Q');
    statusList.value = wStore.statusList;

    if (statusList.value.length === 0) {
      console.warn('DB에 상태 코드 데이터가 없습니다.');
      return;
    }

    // 역할 및 일감 유형 목록 가져오기
    await Promise.all([rStore.findRoles(), tStore.findTypeList()]);

    roleOptions.value = roleOptions.value = rStore.roleList.map((role) => ({
      label: role.name,
      value: role.id
    }));
    issueTypeOptions.value = tStore.typeList.map((type) => ({
      label: type.name,
      value: type.id
    }));

    // 초기값 설정
    if (roleOptions.value.length > 0) selectedRole.value = roleOptions.value[0].value;
    if (issueTypeOptions.value.length > 0) {
      selectedIssueType.value = issueTypeOptions.value[0].value;
    }

    // 초기 매트릭스 데이터 로드
    await loadWorkflowData();
  } catch (error) {
    console.error('초기 데이터 로딩 실패:', error);
  }
});

const accordionSections = computed(() => [
  {
    value: 'Z1',
    label: '사용자가 저자일 때 허용되는 추가 상태',
    matrix: workflowMatrices.Z1
  },
  {
    value: 'Z2',
    label: '사용자가 담당자일 때 허용되는 추가 상태',
    matrix: workflowMatrices.Z2
  }
]);

// 전체 선택
const selectAll = () => {
  const types = ['Z0', 'Z1', 'Z2'];
  types.forEach((type) => {
    statusCodes.value.forEach((before) => {
      if (!workflowMatrices[type][before]) {
        workflowMatrices[type][before] = {};
      }
      statusCodes.value.forEach((after) => {
        workflowMatrices[type][before][after] = true;
      });
    });
  });
};

// 선택 해제
const deselectAll = () => {
  const types = ['Z0', 'Z1', 'Z2'];
  types.forEach((type) => {
    statusCodes.value.forEach((before) => {
      if (workflowMatrices[type][before]) {
        statusCodes.value.forEach((after) => {
          workflowMatrices[type][before][after] = false;
        });
      }
    });
  });
};

// 저장
const onSave = async () => {
  try {
    const details = [];
    const conditionTypes = ['Z0', 'Z1', 'Z2'];

    conditionTypes.forEach((cType) => {
      const matrix = workflowMatrices[cType];

      for (const beforeCode in matrix) {
        for (const afterCode in matrix[beforeCode]) {
          if (matrix[beforeCode][afterCode] === true) {
            details.push({
              beforeCode: beforeCode,
              afterCode: afterCode,
              isAllow: 'R1',
              conditionType: cType
            });
          }
        }
      }
    });

    const payload = {
      roleId: selectedRole.value,
      typeId: selectedIssueType.value,
      details: details
    };

    await wStore.insertWorkflow(payload);

    toast.add({
      severity: 'success',
      summary: '등록 완료',
      detail: '업무흐름이 성공적으로 등록되었습니다.',
      life: 3000,
      closable: false
    });

    await loadWorkflowData();
  } catch (err) {
    toast.add({
      severity: 'error',
      summary: '저장 실패',
      detail: err.response?.data || '처리 중 오류가 발생했습니다.',
      life: 3000
    });
  }
};

const openWorkflowCopyModal = () => {
  visible.value = true;
};
</script>

<template>
  <div class="min-h-screen bg-[#ffffff] px-8 py-6 text-[13px] text-[#1a1816]">
    <h1 class="text-2xl font-bold mb-4">업무흐름</h1>

    <div class="flex items-center justify-between mb-2">
      <p class="text-[#6b7280]">업무흐름을 수정하려면 역할과 일감 유형을 선택하세요.</p>
      <div class="flex gap-1">
        <Button label="복사" icon="pi pi-copy" text @click="openWorkflowCopyModal" />
      </div>
    </div>

    <div class="flex flex-wrap bg-[#F2F3F8] items-center gap-8 border rounded-[10px] px-7 py-5 mb-5">
      <div class="flex items-center gap-3">
        <label class="text-[15px] font-medium">역할:</label>
        <Select v-model="selectedRole" :options="roleOptions" optionLabel="label" optionValue="value" size="small" class="!w-32" placeholder="선택" />
      </div>

      <div class="flex items-center gap-3">
        <label class="text-[15px] font-medium">일감 유형:</label>
        <Select v-model="selectedIssueType" :options="issueTypeOptions" optionLabel="label" optionValue="value" size="small" class="!w-32" placeholder="선택" />
      </div>
    </div>

    <div class="mb-8" v-if="statusCodes.length > 0">
      <div class="flex items-center justify-between mb-3">
        <h3 class="text-lg font-semibold">기본 업무흐름</h3>

        <div class="flex items-center gap-2 text-sm">
          <button type="button" @click="selectAll" class="text-[#3A3B35] hover:underline font-medium">모두 선택</button>
          <span class="text-[#C7C7C2]">|</span>
          <button type="button" @click="deselectAll" class="text-[#3A3B35] hover:underline font-medium">선택 해제</button>
        </div>
      </div>
      <MatrixTable :matrix="workflowMatrices.Z0" :statusList="statusList" />
    </div>

    <Accordion multiple class="flex flex-col gap-4">
      <AccordionPanel v-for="section in accordionSections" :key="section.value" :value="section.value">
        <AccordionHeader>{{ section.label }}</AccordionHeader>
        <AccordionContent>
          <MatrixTable v-if="statusCodes.length > 0" v-model:matrix="section.matrix" :statusList="statusList" />
        </AccordionContent>
      </AccordionPanel>
    </Accordion>

    <div class="mt-8 flex justify-center">
      <Button label="저장" raised class="!text-white px-10 py-2" @click="onSave" />
    </div>
  </div>

  <WorkflowCopyModal v-model:visible="visible"></WorkflowCopyModal>
</template>
