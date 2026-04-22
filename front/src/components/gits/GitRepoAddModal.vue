<script setup>
import { useGitStore } from '@/stores/gits';
import { useToast } from 'primevue';
import { reactive, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true },
  projectId: { type: String, required: true } // 어떤 프로젝트에 귀속될지 받아옴
});

const emit = defineEmits(['update:visible', 'saved']);
const toast = useToast();
const gitStore = useGitStore();

const initialState = {
  projectId: '',
  repoName: '',
  repoUrl: '',
  repoType: 'AA0'
};

const form = reactive({ ...initialState });
const submitted = ref(false);
const duplicateError = ref('');

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      form.projectId = props.projectId;
    } else {
      resetForm();
    }
  }
);

const resetForm = () => {
  Object.assign(form, initialState);
  submitted.value = false;
  duplicateError.value = '';
};

const close = () => {
  emit('update:visible', false);
};

const handleRegister = async () => {
  submitted.value = true;
  duplicateError.value = '';

  if (!form.repoName || !form.repoUrl) {
    return;
  }

  try {
    const payload = {
      projectId: form.projectId,
      repoName: form.repoName,
      repoUrl: form.repoUrl,
      repoType: form.repoType
    };

    await gitStore.insertRepositories(payload);

    toast.add({
      severity: 'success',
      summary: '등록 성공',
      detail: '저장소가 성공적으로 등록되었습니다.',
      life: 3000
    });

    emit('saved');
    close();
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || '';

    if (errorMsg.includes('이미 동일한') || errorMsg.includes('중복')) {
      duplicateError.value = errorMsg;
    } else if (errorMsg.includes('Forbidden')) {
      return;
    } else {
      toast.add({
        severity: 'error',
        summary: '등록 실패',
        detail: errorMsg || '저장소 등록 중 오류가 발생했습니다.',
        life: 3000
      });
    }
  }
};
</script>

<template>
  <Dialog :visible="visible" header="새 저장소 등록" modal :style="{ width: '450px' }" @update:visible="close">
    <div class="flex flex-col gap-5 pt-1">
      <div class="bg-amber-50 border border-amber-200 border-l-4 border-l-amber-400 rounded-r-lg px-4 py-3">
        <p class="flex items-center gap-2 text-base font-medium text-amber-700" style="margin: 0"># 등록 시 서버에 물리적 복제(Clone)가 진행됩니다.</p>
        <p class="text-base text-amber-700 ml-6"># 저장소 크기에 따라 수 분이 소요될 수 있습니다.</p>
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="font-semibold text-stone-700">연결된 프로젝트</label>
        <InputText :value="form.projectId" disabled class="w-full bg-stone-100 font-mono" />
      </div>

      <div class="flex flex-col gap-1.5">
        <label for="repoName" class="font-semibold text-stone-700"> 저장소 이름 <span class="text-lg text-red-500">*</span> </label>
        <InputText id="repoName" v-model="form.repoName" placeholder="예: BEEZ_Main_Backend" class="w-full" />
        <small v-if="submitted && !form.repoName" class="flex items-center gap-1 text-red-500 text-xs"> 이름을 입력해 주세요. </small>
        <small v-if="duplicateError && duplicateError.includes('이름')" class="text-red-500 text-xs">
          {{ duplicateError }}
        </small>
      </div>

      <div class="flex flex-col gap-1.5">
        <label for="repoUrl" class="font-semibold text-stone-700"> Remote URL <span class="text-lg text-red-500">*</span> </label>
        <InputText id="repoUrl" v-model="form.repoUrl" placeholder="https://github.com/user/repo.git" class="w-full" />
        <small v-if="submitted && !form.repoUrl" class="flex items-center gap-1 text-red-500 text-xs"> Git URL을 입력해 주세요. </small>
        <small v-if="duplicateError && duplicateError.includes('이름')" class="text-red-500 text-xs">
          {{ duplicateError }}
        </small>
        <p class="text-[11px] text-stone-400 mt-0.5">Public 저장소인 경우 별도 인증 없이 등록 가능합니다.</p>
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="font-semibold text-stone-700">저장소 유형</label>
        <div class="flex gap-4 p-2 bg-stone-50 rounded-lg border border-stone-200">
          <div class="flex items-center">
            <RadioButton v-model="form.repoType" inputId="typeNormal" name="repoType" value="AA0" />
            <label for="typeNormal" class="ml-2 cursor-pointer">일반저장소</label>
          </div>
          <div class="flex items-center">
            <RadioButton v-model="form.repoType" inputId="typeMain" name="repoType" value="AA1" />
            <label for="typeMain" class="ml-2 cursor-pointer font-bold">주저장소</label>
          </div>
        </div>
        <p v-if="form.repoType === 'AA1'" class="text-[11px] text-amber-600 italic">* 주저장소로 설정 시 기존 주저장소는 일반으로 변경됩니다.</p>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="등록 및 복제" raised @click="handleRegister" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
:deep(.p-button) {
  border-radius: 8px;
}
</style>
