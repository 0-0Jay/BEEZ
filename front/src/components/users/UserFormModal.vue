<script setup>
import { useUsersStore } from '@/stores/users';
import { reactive, ref } from 'vue';

const props = defineProps({
  visible: { type: Boolean, required: true }
});

const emit = defineEmits(['update:visible', 'saved']);
const usersStore = useUsersStore();

const initialState = {
  emp_code: 'EMP-2026-0047',
  temp_pw: 'R9wx465',
  name: '',
  email: '',
  role: null
};

const form = reactive({ ...initialState });
const submitted = ref(false);

const close = () => {
  Object.assign(form, initialState);
  submitted.value = false;
  emit('update:visible', false);
};

const handleRegister = async () => {
  submitted.value = true;
  if (!form.name || !form.email.includes('@')) return;
  try {
    console.log('등록 데이터:', form);
    emit('saved');
    close();
  } catch (error) {
    console.error('등록 실패:', error);
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
            <span class="text-xs uppercase tracking-widest text-stone-400 font-medium">사원번호(자동생성)</span>
            <span class="font-mono font-bold text-stone-900 tracking-wide">{{ form.emp_code }}</span>
          </div>
          <div class="flex flex-col items-center gap-1 text-center">
            <span class="text-xs uppercase tracking-widest text-stone-400 font-medium">초기 비밀번호</span>
            <span class="font-mono font-bold text-stone-900 tracking-wide">{{ form.temp_pw }}</span>
          </div>
        </div>
      </div>

      <!-- 이름 -->
      <div class="flex flex-col gap-1.5">
        <label for="name" class="text-sm font-semibold text-stone-700"> 이름 <span class="text-amber-500">*</span> </label>
        <InputText id="name" v-model="form.name" placeholder="이름을 입력해 주세요." class="w-full" :class="{ 'p-invalid': submitted && !form.name }" />
        <small v-if="submitted && !form.name" class="flex items-center gap-1 text-red-500 text-xs">
          <i class="pi pi-exclamation-circle text-xs" />
          값을 입력해 주세요.
        </small>
      </div>

      <!-- 이메일 -->
      <div class="flex flex-col gap-1.5">
        <label for="email" class="text-sm font-semibold text-stone-700"> 이메일 <span class="text-amber-500">*</span> </label>
        <InputText id="email" v-model="form.email" placeholder="example@example.com" class="w-full" :class="{ 'p-invalid': submitted && (!form.email || !form.email.includes('@')) }" />
        <small v-if="submitted && !form.email" class="flex items-center gap-1 text-red-500 text-xs">
          <i class="pi pi-exclamation-circle text-xs" />
          값을 입력해 주세요.
        </small>
        <small v-else-if="submitted && !form.email.includes('@')" class="flex items-center gap-1 text-red-500 text-xs">
          <i class="pi pi-exclamation-circle text-xs" />
          XXX@XXX.XXX 형식으로 입력해 주세요.
        </small>
      </div>

      <!-- 역할 -->
      <!-- 역할 -->
      <div class="flex flex-col gap-2">
        <div class="flex items-center justify-between">
          <label class="text-sm font-semibold text-stone-700">역할</label>
          <span class="text-xs text-stone-400">
            미선택 시
            <span class="font-medium text-stone-500">일반 사용자</span>
            로 등록됩니다.
          </span>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <!-- 관리자 -->
          <div
            class="flex items-center gap-3 px-4 py-3 rounded-xl border cursor-pointer transition-all duration-150"
            :class="form.role === 'ADMIN' ? 'border-amber-400 bg-amber-50 shadow-[0_0_0_3px_rgba(245,166,35,0.12)]' : 'border-stone-200 bg-stone-50 hover:border-amber-200 hover:bg-amber-50'"
            @click="form.role = form.role === 'ADMIN' ? null : 'ADMIN'"
          >
            <RadioButton :modelValue="form.role" value="ADMIN" />
            <div class="flex flex-col gap-0.5">
              <span class="text-sm font-bold text-stone-900">관리자</span>
              <span class="text-xs text-stone-400">전체 시스템 접근</span>
            </div>
          </div>

          <!-- PM / PL -->
          <div
            class="flex items-center gap-3 px-4 py-3 rounded-xl border cursor-pointer transition-all duration-150"
            :class="form.role === 'PM / PL' ? 'border-amber-400 bg-amber-50 shadow-[0_0_0_3px_rgba(245,166,35,0.12)]' : 'border-stone-200 bg-stone-50 hover:border-amber-200 hover:bg-amber-50'"
            @click="form.role = form.role === 'PM / PL' ? null : 'PM / PL'"
          >
            <RadioButton :modelValue="form.role" value="PM / PL" />
            <div class="flex flex-col gap-0.5">
              <span class="text-sm font-bold text-stone-900">PM / PL</span>
              <span class="text-xs text-stone-400">프로젝트 관리</span>
            </div>
          </div>
        </div>

        <!-- 선택 상태 표시 -->
        <div class="flex items-center gap-1.5 mt-1 text-xs text-stone-400">
          <i class="pi pi-user text-xs" />
          현재 역할 :
          <span class="font-medium text-stone-600">
            {{ form.role === 'ADMIN' ? '관리자' : form.role === 'PM / PL' ? 'PM / PL' : '일반 사용자' }}
          </span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-2 pt-1">
        <Button label="취소" severity="secondary" text @click="close" />
        <Button label="등록" @click="handleRegister" class="!bg-stone-900 !border-stone-900 hover:!bg-amber-600 hover:!border-amber-600 transition-colors duration-150" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
:deep(.p-inputtext) {
  background-color: #fafaf8;
  border-color: #e5e2d9;
  border-radius: 8px;
}

:deep(.p-inputtext:hover) {
  border-color: #c8c4b8;
}
:deep(.p-inputtext:focus) {
  border-color: #f5a623;
  box-shadow: 0 0 0 3px rgba(245, 166, 35, 0.15);
}

:deep(.p-radiobutton .p-radiobutton-box.p-highlight) {
  border-color: #f5a623;
  background: #f5a623;
}
:deep(.p-radiobutton .p-radiobutton-box:not(.p-highlight):hover) {
  border-color: #f5a623;
}
</style>
