<script setup>
import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
</script>

<template>
  <div class="p-10">
    <h1 class="text-2xl font-bold">테스트 페이지 접속 성공!</h1>
    <p>당신은 {{ authStore.user?.role }} 권한을 가지고 있습니다.</p>
  </div>
</template>
