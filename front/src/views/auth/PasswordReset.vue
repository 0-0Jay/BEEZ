<script setup>
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'primevue';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const newPassword = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);
const isDone = ref(false);
const isInvalid = ref(false);
const fieldErrors = ref({ newPassword: '', confirmPassword: '' });
const token = route.query.token;

// 비밀번호 강도
const strengthScore = computed(() => {
  const pw = newPassword.value;
  if (!pw) return 0;
  let score = 0;
  // 나중에 수정하기 -> 영문+숫자+특수문자 조합 8자 이상
  if (pw.length >= 4) score++;
  if (/[A-Z]/.test(pw)) score++;
  if (/[0-9]/.test(pw)) score++;
  if (/[^A-Za-z0-9]/.test(pw)) score++;
  return score;
});

const strengthLabel = computed(() => {
  if (!newPassword.value) return '';
  return ['', '매우 약함', '약함', '보통', '강함'][strengthScore.value] || '강함';
});

const strengthColor = computed(() => {
  return ['#e5e2d9', '#f44336', '#ff9800', '#f5a623', '#4caf50'][strengthScore.value] || '#e5e2d9';
});

function clearError(field) {
  fieldErrors.value[field] = '';
}

async function handleSubmit() {
  fieldErrors.value = { newPassword: '', confirmPassword: '' };
  let hasError = false;

  if (!newPassword.value) {
    fieldErrors.value.newPassword = '값을 입력해 주세요.';
    hasError = true;
  } else if (newPassword.value.length < 4) {
    // 나중에 수정하기 -> 영문+숫자+특수문자 조합 8자 이상
    fieldErrors.value.newPassword = '비밀번호는 4자 이상이어야 합니다.';
    hasError = true;
  }

  if (!confirmPassword.value) {
    fieldErrors.value.confirmPassword = '값을 입력해 주세요.';
    hasError = true;
  } else if (newPassword.value !== confirmPassword.value) {
    fieldErrors.value.confirmPassword = '비밀번호가 일치하지 않습니다.';
    hasError = true;
  }

  if (hasError) return;

  isLoading.value = true;
  try {
    const result = await authStore.resetPassword({
      newPassword: newPassword.value,
      token
    });

    if (result.success) {
      isDone.value = true;
      toast.add({
        severity: 'success',
        summary: '재설정 완료',
        detail: '비밀번호 재설정을 완료하였습니다.',
        life: 3000,
        closable: false
      });
      // 입력값 초기화
      newPassword.value = '';
      confirmPassword.value = '';
    } else {
      if (result.message.includes('유효시간') || result.message.includes('잘못된')) {
        isInvalid.value = true;
      } else {
        toast.add({
          severity: 'error',
          summary: '변경 실패',
          detail: result.message,
          life: 3000,
          closable: false
        });
      }
    }
  } catch (err) {
    toast.add({
      severity: 'error',
      summary: '오류',
      detail: '서버와 통신 중 오류가 발생했습니다.',
      life: 3000,
      closable: false
    });
  } finally {
    isLoading.value = false;
  }
}
</script>

<template>
  <div class="h-[100dvh] flex items-center justify-center bg-[#f2f0eb] relative overflow-hidden">
    <div class="absolute inset-0 noise-overlay pointer-events-none"></div>

    <div class="reset-card w-full max-w-md mx-4 relative z-10">
      <div class="h-1.5 bg-[#f5a623] rounded-t-2xl"></div>

      <div class="bg-white rounded-b-2xl px-10 py-10 shadow-[0_8px_40px_rgba(61,32,0,0.1),0_2px_8px_rgba(61,32,0,0.06)]">
        <template v-if="isDone">
          <div class="flex flex-col items-center text-center py-4 gap-4">
            <div class="w-16 h-16 rounded-full flex items-center justify-center bg-[#fff8ec]">
              <i class="pi pi-check text-2xl text-[#e8920e]" />
            </div>
            <div>
              <h2 class="text-xl font-bold text-[#3a3835] m-0">변경 완료</h2>
              <p class="mt-2 text-sm text-[#9a9690] mb-0">비밀번호가 성공적으로 변경되었습니다.</p>
            </div>
            <Button label="로그인으로 돌아가기" class="w-full !bg-[#e8920e] !border-[#e8920e] hover:!bg-[#d07c0a] hover:!border-[#d07c0a] mt-2" @click="router.push('/')" />
          </div>
        </template>
        <template v-else-if="isInvalid">
          <div class="flex flex-col items-center text-center py-4 gap-4">
            <div class="w-16 h-16 rounded-full flex items-center justify-center bg-[#fff8ec]">
              <i class="pi pi-check text-2xl text-[#e8920e]" />
            </div>
            <div>
              <h2 class="text-xl font-bold text-[#3a3835] m-0">유효하지 않은 요청</h2>
              <p class="mt-2 text-sm text-[#9a9690] mb-0">링크가 만료되었거나 이미 사용되었습니다.<br />비밀번호 찾기를 다시 진행해 주세요.</p>
            </div>
            <Button label="다시 신청하기" class="w-full !bg-[#e8920e] !border-[#e8920e] mt-2" @click="router.push('/auth/pwRequest')" />
          </div>
        </template>
        <template v-else>
          <div class="text-center mb-8">
            <div class="w-16 h-16 mx-auto mb-3 overflow-hidden">
              <img src="/demo/images/logo.png" class="w-full h-full object-contain" alt="로고" />
            </div>
            <h2 class="text-xl font-bold text-[#3a3835] m-0">새 비밀번호 설정</h2>
            <div class="w-10 h-0.5 bg-[#f5a623] mx-auto mt-1.5 rounded-full"></div>
            <p class="text-sm text-[#9a9690] mb-0" style="margin-top: 15px">새로운 비밀번호를 입력해 주세요.</p>
          </div>

          <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
            <div class="field-row">
              <label class="field-label">새 비밀번호</label>
              <div class="flex flex-col gap-2">
                <div class="relative">
                  <i class="pi pi-lock absolute left-3 top-1/2 -translate-y-1/2 text-sm z-10 text-[#736f68]" />
                  <Password
                    v-model="newPassword"
                    :feedback="false"
                    toggleMask
                    placeholder="새 비밀번호를 입력해 주세요."
                    autocomplete="new-password"
                    :invalid="!!fieldErrors.newPassword"
                    :inputClass="`w-full !pl-9 !text-sm !bg-[#fafaf8] ${fieldErrors.newPassword ? '!border-[#E8920E]' : '!border-[#e5e2d9]'}`"
                    class="w-full"
                    @input="clearError('newPassword')"
                  />
                </div>
                <small v-if="fieldErrors.newPassword" class="flex items-center gap-1 text-[#F44336]"> <i class="pi pi-exclamation-circle text-xs" />{{ fieldErrors.newPassword }} </small>
                <template v-else-if="newPassword">
                  <div class="flex gap-1 mt-0.5">
                    <div v-for="i in 4" :key="i" class="h-1 flex-1 rounded-full transition-all duration-300" :style="{ background: i <= strengthScore ? strengthColor : '#e5e2d9' }" />
                  </div>
                  <small class="font-medium transition-colors duration-300" :style="{ color: strengthColor }">
                    {{ strengthLabel }}
                  </small>
                </template>
              </div>
            </div>

            <div class="field-row">
              <label class="field-label">비밀번호 확인</label>
              <div class="flex flex-col gap-2">
                <div class="relative">
                  <i class="pi pi-lock absolute left-3 top-1/2 -translate-y-1/2 text-sm z-10 text-[#736f68]" />
                  <Password
                    v-model="confirmPassword"
                    :feedback="false"
                    toggleMask
                    placeholder="비밀번호를 다시 입력해 주세요."
                    autocomplete="new-password"
                    :invalid="!!fieldErrors.confirmPassword"
                    :inputClass="`w-full !pl-9 !text-sm !bg-[#fafaf8] ${fieldErrors.confirmPassword ? '!border-[#E8920E]' : '!border-[#e5e2d9]'}`"
                    class="w-full"
                    @input="clearError('confirmPassword')"
                  />
                </div>
                <small v-if="fieldErrors.confirmPassword" class="flex items-center gap-1 text-[#F44336]"> <i class="pi pi-exclamation-circle text-xs" />{{ fieldErrors.confirmPassword }} </small>
                <small v-else-if="confirmPassword && newPassword === confirmPassword" class="flex items-center gap-1 text-[#4caf50]"> <i class="pi pi-check-circle text-xs" />비밀번호가 일치합니다. </small>
              </div>
            </div>

            <div class="flex gap-3 pt-2">
              <Button type="button" label="돌아가기" severity="secondary" outlined class="flex-1" @click="router.push('/')" />
              <Button type="submit" :label="isLoading ? '변경 중...' : '비밀번호 변경'" :loading="isLoading" class="flex-1 !bg-[#e8920e] !border-[#e8920e] hover:!bg-[#d07c0a] hover:!border-[#d07c0a]" />
            </div>
          </form>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.noise-overlay {
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
  opacity: 0.4;
}

.reset-card {
  animation: cardReveal 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
  filter: drop-shadow(0 8px 40px rgba(61, 32, 0, 0.08));
}

.field-row {
  display: grid;
  grid-template-columns: 72px 1fr;
  align-items: start;
  gap: 0 16px;
}

.field-label {
  padding-top: 10px;
  font-size: 0.875rem;
  font-weight: 600;
  color: #3a3835;
  white-space: nowrap;
}

:deep(.p-password) {
  width: 100%;
}
:deep(.p-password input) {
  width: 100%;
  font-size: 0.875rem;
  background-color: #fafaf8;
  border-color: #e5e2d9;
}
:deep(.p-password input:focus) {
  outline: none;
  box-shadow: 0 0 0 3px #fff0c2 !important;
  border-color: #f5a623 !important;
}

:deep(.p-password.p-invalid input) {
  border-color: #f5a623 !important;
}

:deep(.p-password input::placeholder) {
  color: #736f68;
}

@keyframes cardReveal {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
