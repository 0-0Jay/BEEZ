<script setup>
import { useAlertStore } from '@/stores/alert';
import { Toast } from 'primevue';
import { watch } from 'vue';

const alertStore = useAlertStore();

watch(
  () => alertStore.message,
  (msg) => {
    if (msg) {
      alert(msg);
      alertStore.setAlert(null);
    }
  }
);
</script>

<template>
  <toast style="margin-top: 80px; margin-right: 10px" />
  <router-view />
</template>

<style scoped></style>
