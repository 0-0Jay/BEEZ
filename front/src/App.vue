<script setup>
import { useAlertStore } from '@/stores/alert';
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
  <router-view />
</template>

<style scoped></style>
