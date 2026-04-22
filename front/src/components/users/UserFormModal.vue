<script setup>
import { useUsersStore } from '@/stores/users';
import { useToast } from 'primevue';
import { reactive, ref, watch } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible', 'saved']);
const usersStore = useUsersStore();
const toast = useToast();

const initialState = {
  id: null,
  password: '',
  name: '',
  email: '',
  role: null
};

const form = reactive({ ...initialState });
const submitted = ref(false); // 등록 버튼 스위치

const roleOptions = [
  { code: 'ROLE0001', name: '관리자', desc: '전체 시스템 접근' },
  { code: 'ROLE0002', name: 'PM / PL', desc: '프로젝트 관리' }
];

const isEmailChecked = ref(false);
const emailError = ref('');

// 모달이 열릴 때마다 서버에서 사번/비번 가져오기
watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      try {
        await usersStore.getInitPw();
        const data = usersStore.initPw;
        form.password = data.password;
      } catch (err) {
        console.error('초기 정보 로드 실패:', err);
      }
    } else {
      // 모달 닫힐 때 초기화
      resetForm();
    }
  }
);

// 이메일 바뀌면 중복 체크 초기화
watch(
  () => form.email,
  () => {
    isEmailChecked.value = false;
    emailError.value = '';
  }
);

const resetForm = () => {
  Object.assign(form, {
    id: null,
    password: '',
    name: '',
    email: '',
    role: null
  });
  submitted.value = false;
};

const close = () => {
  Object.assign(form, initialState);
  submitted.value = false;
  isEmailChecked.value = false;
  emailError.value = '';
  emit('update:visible', false);
};

const verifyEmail = async () => {
  if (!form.email || !form.email.includes('@')) {
    emailError.value = '올바른 이메일 형식을 입력해 주세요.';
    return;
  }

  try {
    const isDuplicate = await usersStore.checkEmailExists(form.email);
    if (isDuplicate) {
      emailError.value = '이미 사용 중인 이메일입니다.';
      isEmailChecked.value = false;
    } else {
      emailError.value = '';
      isEmailChecked.value = true;
    }
  } catch (err) {
    emailError.value = '중복 체크 중 오류가 발생했습니다.';
  }
};

const handleRegister = async () => {
  submitted.value = true;

  // 유효성 검사
  if (!form.name || !form.email || !form.email.includes('@') || !isEmailChecked.value) {
    return;
  }

  try {
    await usersStore.insertUser(form);
    toast.add({
      severity: 'success',
      summary: '등록 완료',
      detail: `${form.name} 사용자가 등록되었습니다.`,
      life: 3000,
      closable: false
    });
    emit('saved');
    close();
    await usersStore.findUsers();
  } catch (error) {
    const errorMsg = err.response?.data?.message || err.response?.data || '사용자 등록 중 오류가 발생했습니다.';

    toast.add({
      severity: 'error',
      summary: '등록 실패',
      detail: errorMsg,
      life: 3000,
      closable: false
    });
  }
};
</script>

<template>
  <Dialog :visible="visible" header="사용자 등록" modal :style="{ width: '450px' }" @update:visible="close">
    <div class="flex flex-col gap-5 pt-1">
      <!-- 자동생성 안내 박스 -->
      <div class="bg-amber-50 border border-amber-200 border-l-4 border-l-amber-400 rounded-r-lg px-4 py-3">
        <p class="flex items-center gap-2 text-xs font-medium text-amber-700 mb-3">
          <i class="pi pi-info-circle text-amber-500" />
          사원번호와 초기 비밀번호는 자동으로 생성됩니다.
        </p>
        <div class="flex justify-around">
          <div class="flex flex-col items-center gap-1 text-center">
            <span class="text-xs uppercase tracking-widest text-stone-400 font-medium">사원번호</span>
            <span class="font-mono font-bold text-amber-600 tracking-wide">자동생성</span>
          </div>
          <div class="flex flex-col items-center gap-1 text-center">
            <span class="text-xs uppercase tracking-widest text-stone-400 font-medium">초기 비밀번호</span>
            <span class="font-mono font-bold text-stone-900 tracking-wide">{{ form.password || '로딩 중...' }}</span>
          </div>
        </div>
      </div>

      <!-- 이름 -->
      <div class="flex flex-col gap-1.5">
        <label for="name" class="text-sm font-semibold text-stone-700"> 이름 <span class="text-lg text-red-500">*</span> </label>
        <InputText id="name" v-model="form.name" placeholder="이름을 입력해 주세요." class="w-full" />
        <small v-if="submitted && !form.name" class="flex items-center gap-1 text-red-500 text-xs"> 값을 입력해 주세요. </small>
      </div>

      <!-- 이메일 -->
      <div class="flex flex-col gap-1.5">
        <label for="email" class="text-sm font-semibold text-stone-700"> 이메일 <span class="text-lg text-red-500">*</span> </label>
        <div class="flex gap-2">
          <InputText id="email" v-model="form.email" placeholder="example@example.com" class="w-full" />
          <Button :label="isEmailChecked ? '확인됨' : '중복 확인'" :icon="isEmailChecked ? 'pi pi-check' : ''" :class="isEmailChecked ? 'p-button-success' : 'p-button-outlined'" class="text-xs whitespace-nowrap" @click="verifyEmail" />
        </div>
        <div class="flex flex-col gap-1.5">
          <small v-if="submitted && !form.email" class="flex items-center gap-1 text-red-500 text-xs"> 값을 입력해 주세요. </small>

          <small v-else-if="submitted && !form.email.includes('@')" class="flex items-center gap-1 text-red-500 text-xs"> 이메일 형식이 올바르지 않습니다. </small>

          <small v-else-if="submitted && !isEmailChecked && !emailError" class="flex items-center gap-1 text-amber-600 text-xs font-medium"> 이메일 중복 확인을 진행해 주세요. </small>

          <small v-else-if="emailError" class="flex items-center gap-1 text-red-500 text-xs"> {{ emailError }} </small>

          <small v-else-if="isEmailChecked" class="flex items-center gap-1 text-green-600 text-xs font-medium"> 사용 가능한 이메일입니다. </small>
        </div>
      </div>

      <!-- 역할 -->
      <div class="flex flex-col gap-2">
        <div class="flex items-center justify-between">
          <label class="text-sm font-semibold text-stone-700">역할</label>
          <span class="text-base text-stone-400">
            미선택 시
            <span class="font-medium text-stone-500">일반 사용자</span>
            로 등록됩니다.
          </span>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div v-for="role in roleOptions" :key="role.code" class="flex items-center gap-3 px-4 py-3 rounded-xl border cursor-pointer transition-all duration-150" @click="form.role = form.role === role.code ? null : role.code">
            <RadioButton v-model="form.role" :value="role.code" />
            <div class="flex flex-col gap-0.5">
              <span class="text-base font-bold text-stone-900">{{ role.name }}</span>
              <span class="text-base text-stone-400">{{ role.desc }}</span>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-1.5 mt-1 text-base text-stone-400">
          <i class="pi pi-user text-xs" />
          현재 역할 :
          <span class="font-medium text-stone-600">
            {{ roleOptions.find((r) => r.code === form.role)?.name || '일반 사용자' }}
          </span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="취소" severity="secondary" raised @click="close" />
        <Button label="등록" raised @click="handleRegister" />
      </div>
    </template>
  </Dialog>
</template>
