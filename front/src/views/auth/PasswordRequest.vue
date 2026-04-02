<script setup>
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'primevue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

const id = ref('');
const name = ref('');
const email = ref('');
const fieldErrors = ref({ id: '', name: '', email: '' });
const isLoading = ref(false);

function validateEmail(val) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val);
}

async function handleSubmit() {
  // 에러 초기화
  fieldErrors.value = { id: '', name: '', email: '' };

  let hasError = false;

  // 유효성 체크
  if (!id.value.trim()) {
    fieldErrors.value.id = '값을 입력해 주세요.';
    hasError = true;
  }
  if (!name.value.trim()) {
    fieldErrors.value.name = '값을 입력해 주세요.';
    hasError = true;
  }
  if (!email.value.trim()) {
    fieldErrors.value.email = '값을 입력해 주세요.';
    hasError = true;
  } else if (!validateEmail(email.value)) {
    fieldErrors.value.email = '올바른 이메일 형식이 아닙니다.';
    hasError = true;
  }

  if (hasError) return;

  // 서버 통신
  isLoading.value = true;
  try {
    const result = await authStore.requestPasswordReset({
      id: id.value,
      name: name.value,
      email: email.value
    });

    if (result.success) {
      toast.add({
        severity: 'success',
        summary: '전송 완료',
        detail: '메일함을 확인해 주세요!',
        life: 3000
      });
      // 입력값 초기화
      id.value = '';
      name.value = '';
      email.value = '';
    } else {
      toast.add({
        severity: 'error',
        summary: '확인 필요',
        detail: result.message,
        life: 3000
      });
    }
  } catch (err) {
    toast.add({
      severity: 'error',
      summary: '오류',
      detail: '서버와 통신 중 오류가 발생했습니다.',
      life: 3000
    });
  } finally {
    isLoading.value = false;
  }
}

function clearError(field) {
  fieldErrors.value[field] = '';
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-[#f2f0eb] relative overflow-hidden">
    <div class="absolute inset-0 noise-overlay pointer-events-none"></div>

    <div class="reset-card w-full max-w-[520px] mx-4 relative z-10">
      <div class="h-1.5 bg-[#f5a623] rounded-t-2xl"></div>

      <div class="bg-white rounded-b-2xl px-10 py-10 shadow-[0_8px_40px_rgba(61,32,0,0.1),0_2px_8px_rgba(61,32,0,0.06)]">
        <div class="text-center mb-5">
          <div class="w-16 h-16 mx-auto mb-3 overflow-hidden">
            <img src="/demo/images/logo.png" class="w-full h-full object-contain" alt="로고" />
          </div>
          <h2 class="text-xl font-bold text-[#3a3835] m-0">비밀번호 재설정</h2>
          <div class="w-10 h-0.5 bg-[#f5a623] mx-auto mt-1.5 rounded-full"></div>
          <p class="text-sm text-[#9a9690] mb-0" style="margin-top: 15px">등록된 이메일로 재설정 링크를 전송합니다.</p>
        </div>

        <!-- 폼 -->
        <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
          <div class="field-row">
            <label class="field-label">사원번호</label>
            <div class="flex flex-col gap-1">
              <div class="relative">
                <i class="pi pi-id-card absolute left-3 top-1/2 -translate-y-1/2 text-sm text-[#9A9690]" />
                <input
                  v-model="id"
                  @input="clearError('id')"
                  placeholder="사원번호를 입력해 주세요."
                  class="w-full pl-9 pr-4 py-2.5 rounded-lg text-sm outline-none transition-all duration-200 border bg-[#fafaf8] placeholder:text-[#262523]"
                  :class="fieldErrors.id ? 'border-[#f5a623]' : 'border-[#e5e2d9]'"
                />
              </div>
              <small v-if="fieldErrors.id" class="flex items-center gap-1 text-[#F44336]"> <i class="pi pi-exclamation-circle text-xs" />{{ fieldErrors.id }} </small>
            </div>
          </div>

          <div class="field-row">
            <label class="field-label">이메일</label>
            <div class="flex flex-col gap-1">
              <div class="relative">
                <i class="pi pi-envelope absolute left-3 top-1/2 -translate-y-1/2 text-sm text-[#9A9690]" />
                <input
                  v-model="email"
                  @input="clearError('email')"
                  type="email"
                  placeholder="이메일을 입력해 주세요."
                  class="w-full pl-9 pr-4 py-2.5 rounded-lg text-sm outline-none transition-all duration-200 border bg-[#fafaf8] placeholder:text-[#262523]"
                  :class="fieldErrors.email ? 'border-[#f5a623]' : 'border-[#e5e2d9]'"
                />
              </div>
              <small v-if="fieldErrors.email" class="flex items-center gap-1 text-[#F44336]"> <i class="pi pi-exclamation-circle text-xs" />{{ fieldErrors.email }} </small>
            </div>
          </div>

          <div class="field-row">
            <label class="field-label">이름</label>
            <div class="flex flex-col gap-1">
              <div class="relative">
                <i class="pi pi-user absolute left-3 top-1/2 -translate-y-1/2 text-sm text-[#9A9690]" />
                <input
                  v-model="name"
                  @input="clearError('name')"
                  placeholder="이름을 입력해 주세요."
                  class="w-full pl-9 pr-4 py-2.5 rounded-lg text-sm outline-none transition-all duration-200 border bg-[#fafaf8] placeholder:text-[#262523]"
                  :class="fieldErrors.name ? 'border-[#f5a623]' : 'border-[#e5e2d9]'"
                />
              </div>
              <small v-if="fieldErrors.name" class="flex items-center gap-1 text-[#F44336]"> <i class="pi pi-exclamation-circle text-xs" />{{ fieldErrors.name }} </small>
            </div>
          </div>

          <div class="flex gap-3 pt-2">
            <Button type="button" label="돌아가기" severity="secondary" outlined class="flex-1" @click="router.push('/')" />
            <Button type="submit" :label="isLoading ? '전송 중...' : 'URL 전송'" :loading="isLoading" class="flex-1 !bg-[#e8920e] !border-[#e8920e] hover:!bg-[#d07c0a] hover:!border-[#d07c0a]" />
          </div>
        </form>
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

input:focus {
  outline: none;
  box-shadow: 0 0 0 3px #fff0c2;
  border-color: #f5a623;
}

input::placeholder {
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
