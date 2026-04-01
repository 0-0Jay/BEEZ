<script setup>
import { useAuthStore } from '@/stores/auth';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const store = useAuthStore();
const router = useRouter();

const id = ref('');
const password = ref('');
const rememberMe = ref(false);
const submitted = ref(false);
const loginErrorMsg = ref('');

onMounted(() => {
  const savedId = localStorage.getItem('savedEmployeeId');
  if (savedId) {
    id.value = savedId;
    rememberMe.value = true;
  }
});

async function validateAndLogin() {
  submitted.value = true;
  loginErrorMsg.value = '';

  if (!id.value.trim() || !password.value.trim()) return;

  try {
    const success = await store.login(id.value, password.value);
    if (success) {
      if (rememberMe.value) {
        localStorage.setItem('savedEmployeeId', id.value);
      } else {
        localStorage.removeItem('savedEmployeeId');
      }
      router.push('/main');
    }
  } catch (err) {
    loginErrorMsg.value = '사원번호 또는 비밀번호가 일치하지 않습니다.';
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-page relative overflow-hidden">
    <div class="absolute inset-0 pointer-events-none">
      <div class="noise-overlay"></div>
    </div>

    <div class="login-card relative z-10 w-full max-w-md mx-4">
      <div class="h-1.5 w-full rounded-t-2xl" style="background: #f5a623"></div>

      <div class="bg-white rounded-b-2xl shadow-card px-10 py-10">
        <div class="text-center mb-8">
          <div class="inline-flex items-center justify-center w-16 h-16 mb-3 overflow-hidden">
            <img src="/demo/images/logo.png" alt="팀 로고" class="w-full h-full object-contain" />
          </div>
          <h2 class="font-bold tracking-tight" style="color: #3a3835">로그인</h2>
          <div class="mt-1 h-0.5 w-10 mx-auto rounded-full" style="background: #f5a623"></div>
        </div>

        <form @submit.prevent="validateAndLogin">
          <!-- 사원번호 -->
          <div class="mb-5">
            <label for="employee-id" class="block text-sm font-semibold mb-1.5 tracking-wide" style="color: #3a3835"> 사원번호 </label>
            <div class="relative">
              <span class="absolute inset-y-0 left-3 flex items-center pointer-events-none">
                <i class="pi pi-user text-sm" :style="submitted && !id.trim() ? 'color: #E8920E' : 'color: #9A9690'"></i>
              </span>
              <input
                id="employee-id"
                v-model="id"
                @input="submitted = false"
                type="text"
                autocomplete="username"
                placeholder="사원번호를 입력해 주세요."
                autofocus
                class="w-full pl-9 pr-4 py-2.5 rounded-lg text-sm outline-none transition-all duration-200 border"
                :class="submitted && !id.trim() ? 'border-amber-err bg-amber-50-custom' : 'border-stone-200-custom bg-stone-50-custom'"
              />
            </div>
            <p v-if="submitted && !id.trim()" class="flex items-center gap-1 text-xs font-medium" style="color: #e8920e; margin-top: 3px">
              <i class="pi pi-exclamation-circle text-xs" />
              값을 입력해 주세요.
            </p>
          </div>

          <div class="mb-2">
            <label for="password" class="block text-sm font-semibold mb-1.5 tracking-wide" style="color: #3a3835"> 비밀번호 </label>
            <div class="relative">
              <span class="absolute inset-y-0 left-3 flex items-center pointer-events-none">
                <i class="pi pi-lock text-sm text-[#9A9690]"></i>
              </span>
              <input
                id="password"
                v-model="password"
                @input="submitted = false"
                type="password"
                autocomplete="current-password"
                placeholder="비밀번호를 입력해 주세요."
                class="w-full pl-9 pr-4 py-2.5 rounded-lg text-sm outline-none transition-all duration-200 border"
                :class="submitted && !password.trim() ? 'border-amber-err bg-amber-50-custom' : 'border-stone-200-custom bg-stone-50-custom'"
              />
            </div>
            <p v-if="submitted && !password.trim()" class="flex items-center gap-1 text-xs font-medium" style="color: #f44336; margin-top: 3px">
              <i class="pi pi-exclamation-circle text-xs" />
              값을 입력해 주세요.
            </p>
          </div>

          <p v-if="loginErrorMsg" class="flex items-center gap-1 text-xs font-medium" style="color: #f44336">
            <i class="pi pi-exclamation-circle text-xs" />
            {{ loginErrorMsg }}
          </p>

          <div class="flex items-center justify-between mb-7">
            <label class="flex items-center gap-2 cursor-pointer group select-none">
              <div class="relative">
                <input type="checkbox" v-model="rememberMe" class="sr-only peer" />
                <div class="w-4 h-4 rounded border transition-all duration-200 flex items-center justify-center" :style="rememberMe ? 'background: #F5A623; border-color: #F5A623;' : 'background: white; border-color: #C8C4B8;'">
                  <svg v-if="rememberMe" width="10" height="10" viewBox="0 0 10 10" fill="none">
                    <path d="M2 5l2.5 2.5L8 3" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                </div>
              </div>
              <span class="text-sm" style="color: #6e6b65">아이디 저장</span>
            </label>
            <button type="button" class="text-sm font-medium transition-colors duration-150 hover:underline" style="color: #c97700" @click="$router.push('/auth/pwRequest')">비밀번호 재설정</button>
          </div>

          <button
            type="submit"
            class="w-full py-3 rounded-xl font-bold text-sm tracking-widest text-white shadow-btn transition-all duration-200 hover:shadow-btn-hover hover:-translate-y-0.5 active:translate-y-0"
            style="background: #e8920e; letter-spacing: 0.15em"
          >
            로그인
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.bg-page {
  background-color: #f2f0eb;
}

.noise-overlay {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
  opacity: 0.4;
  pointer-events: none;
}

.login-card {
  filter: drop-shadow(0 8px 40px rgba(61, 32, 0, 0.1)) drop-shadow(0 2px 8px rgba(61, 32, 0, 0.06));
  animation: cardReveal 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.border-stone-200-custom {
  border-color: #e5e2d9;
}
.bg-stone-50-custom {
  background-color: #fafaf8;
}
.border-amber-err {
  border-color: #f5a623;
}
.bg-amber-50-custom {
  background-color: #fafaf8;
}

input:focus {
  outline: none;
  box-shadow: 0 0 0 3px #fff0c2;
}

input::placeholder {
  color: #736f68;
}

.shadow-btn {
  box-shadow:
    0 4px 16px rgba(245, 166, 35, 0.35),
    0 1px 4px rgba(245, 166, 35, 0.2);
}
.shadow-btn-hover {
  box-shadow:
    0 8px 24px rgba(245, 166, 35, 0.45),
    0 2px 8px rgba(245, 166, 35, 0.25);
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
