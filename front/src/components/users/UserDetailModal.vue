<script setup>
import { useAuthStore } from '@/stores/auth';
import { useUsersStore } from '@/stores/users';
import { useToast } from 'primevue';
import { reactive, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true },
  userData: Object
});

const emit = defineEmits(['update:visible', 'saved']);
const usersStore = useUsersStore();
const authStore = useAuthStore();
const toast = useToast();

const userForm = reactive({
  id: '',
  name: '',
  email: '',
  status: 'H1',
  newPassword: '',
  confirmPassword: ''
});

const submitted = ref(false);
const isEmailChecked = ref(true); // 수정 시 초기값은 본인 이메일이므로 true
const emailError = ref('');

const errors = reactive({
  name: '',
  email: '',
  password: ''
});

watch(
  () => props.visible,
  (newVal) => {
    if (newVal && props.userData) {
      userForm.id = props.userData.id;
      userForm.name = props.userData.name;
      userForm.email = props.userData.email;
      userForm.status = props.userData.status || 'H1';
      userForm.newPassword = '';
      userForm.confirmPassword = '';
      submitted.value = false;
      isEmailChecked.value = true; // 초기화 시 true
      emailError.value = '';
      resetErrors();
    }
  }
);

// 이메일 변경 감지 (원래 이메일과 다르면 중복체크 다시 해야 함)
watch(
  () => userForm.email,
  (newEmail) => {
    if (newEmail === props.userData?.email) {
      isEmailChecked.value = true;
      emailError.value = '';
    } else {
      isEmailChecked.value = false;
      emailError.value = '';
    }
  }
);

const resetErrors = () => {
  Object.keys(errors).forEach((key) => (errors[key] = ''));
};

const close = () => {
  submitted.value = false;
  emit('update:visible', false);
};

const verifyEmail = async () => {
  if (!userForm.email || !userForm.email.includes('@')) {
    emailError.value = '올바른 이메일 형식을 입력해 주세요.';
    return;
  }

  // 중복 체크 시작 전 기존 에러 초기화
  emailError.value = '';
  errors.email = '';

  if (userForm.email === props.userData?.email) {
    isEmailChecked.value = true;
    return;
  }

  try {
    const isDuplicate = await usersStore.checkEmailExists(userForm.email);
    if (isDuplicate) {
      emailError.value = '이미 사용 중인 이메일입니다.';
      isEmailChecked.value = false;
    } else {
      isEmailChecked.value = true;
      emailError.value = '';
    }
  } catch (err) {
    emailError.value = '중복 체크 중 오류 발생';
  }
};

const validate = () => {
  let isValid = true;
  resetErrors();

  if (!userForm.name?.trim()) {
    errors.name = '이름을 입력해 주세요.';
    isValid = false;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!userForm.email?.trim()) {
    errors.email = '이메일을 입력해 주세요.';
    isValid = false;
  } else if (!emailRegex.test(userForm.email)) {
    errors.email = '이메일 형식이 올바르지 않습니다.';
    isValid = false;
  } else if (!isEmailChecked.value) {
    // 이메일 형식은 맞는데 중복 체크를 안 한 경우
    isValid = false;
  }

  return isValid;
};

const onSave = async () => {
  submitted.value = true;
  const isValid = validate();

  if (!isValid) {
    toast.add({
      severity: 'warn',
      summary: '입력 확인',
      detail: '입력 항목 및 이메일 중복 확인을 체크해 주세요.',
      life: 3000
    });
    return;
  }

  try {
    const payload = {
      name: userForm.name,
      email: userForm.email,
      status: userForm.status,
      newPassword: userForm.newPassword === '' ? null : userForm.newPassword
    };

    await usersStore.updateUser(payload, userForm.id);

    toast.add({
      severity: 'success',
      summary: '수정 완료',
      detail: '사용자 정보 수정이 완료되었습니다.',
      life: 3000,
      closable: false
    });
    emit('saved');
    close();
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || '사용자 정보 수정 중 오류가 발생했습니다.';

    toast.add({
      severity: 'error',
      summary: '수정 실패',
      detail: errorMsg,
      life: 3000,
      closable: false
    });
  }
};
</script>

<template>
  <Dialog :visible="visible" header="사용자 정보 수정" modal :style="{ width: '450px' }" @update:visible="close">
    <div class="flex flex-col gap-5 pt-1">
      <div class="flex flex-col gap-1.5">
        <label class="text-sm font-semibold text-stone-700">사원번호</label>
        <InputText v-model="userForm.id" disabled class="w-full bg-stone-100 border-stone-200 font-mono" />
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="text-sm font-semibold text-stone-700"> 이름 <span class="text-red-500">*</span> </label>
        <InputText v-model="userForm.name" placeholder="이름을 입력해 주세요." class="w-full" />
        <small v-if="submitted && errors.name" class="flex items-center gap-1 text-red-500 text-xs"> {{ errors.name }} </small>
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="text-sm font-semibold text-stone-700"> 이메일 <span class="text-red-500">*</span> </label>
        <div class="flex gap-2">
          <InputText v-model="userForm.email" placeholder="example@example.com" class="w-full" />
          <Button :label="isEmailChecked ? '확인됨' : '중복 확인'" :icon="isEmailChecked ? 'pi pi-check' : ''" :class="isEmailChecked ? 'p-button-success' : 'p-button-outlined'" class="text-xs whitespace-nowrap" @click="verifyEmail" />
        </div>
        <div class="flex flex-col gap-1">
          <small v-if="submitted && !userForm.email" class="text-red-500 text-xs"> 이메일을 입력해 주세요. </small>

          <small v-else-if="submitted && errors.email" class="text-red-500 text-xs">
            {{ errors.email }}
          </small>

          <small v-else-if="emailError" class="text-red-500 text-xs">
            {{ emailError }}
          </small>

          <small v-else-if="submitted && !isEmailChecked" class="text-amber-600 text-xs font-medium"> 이메일 중복 확인을 진행해 주세요. </small>

          <small v-else-if="isEmailChecked && userForm.email !== props.userData?.email" class="text-green-600 text-xs font-medium"> 사용 가능한 이메일입니다. </small>
        </div>
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="text-sm font-semibold text-stone-700">비밀번호 재설정</label>
        <Password v-model="userForm.newPassword" toggleMask :feedback="false" placeholder="새 비밀번호 입력" fluid :class="{ 'p-invalid': submitted && errors.password }" />
        <small class="text-stone-400 text-xs font-medium">※ 비밀번호는 8자 이상 입력해주세요.</small>
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="text-sm font-semibold text-stone-700">비밀번호 확인</label>
        <Password v-model="userForm.confirmPassword" toggleMask :feedback="false" placeholder="비밀번호 재입력" fluid :class="{ 'p-invalid': submitted && errors.password }" />
        <small v-if="submitted && errors.password" class="flex items-center gap-1 text-red-500 text-xs"> <i class="pi pi-exclamation-circle text-xs" /> {{ errors.password }} </small>
        <small v-else class="text-stone-400 text-xs font-medium">※ 입력하지 않으면 기존 비밀번호가 유지됩니다.</small>
      </div>

      <div class="flex items-center justify-between p-4 rounded-xl border border-stone-200 bg-stone-50 mt-2">
        <div class="flex flex-col">
          <span class="text-sm font-bold text-stone-900">계정 활성화 상태</span>
          <span class="text-xs text-stone-400 font-medium">현재: {{ userForm.status === 'H1' ? '활성' : '비활성' }}</span>
        </div>
        <div class="flex items-center gap-3">
          <ToggleSwitch v-model="userForm.status" trueValue="H1" falseValue="H0" />
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="저장" raised @click="onSave" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
:deep(.p-password),
:deep(.p-password-input) {
  width: 100%;
}

:deep(.p-toggleswitch.p-toggleswitch-checked .p-toggleswitch-slider) {
  background: #f5a623;
}
</style>
