<script setup>
import ChatRoom from '@/components/chat/ChatRoom.vue';
import { useNotificationStore } from '@/stores/notification';
import { ref } from 'vue';

const notificationStore = useNotificationStore();
const content = ref('');

const sendTest = () => {
  const data = {
    userId: '20261111', // 알림을 수신할 사람
    content: content.value, // 알림 내용
    link: 'comment' // 알림을 클릭하면 이동할 링크(추후구현)
  };
  notificationStore.sendTest(data);
};
</script>

<template>
  <div class="grid grid-cols-12 gap-8">
    <div class="col-span-12 xl:col-span-6 h-2000">
      <InputText type="text" v-model="content" placeholder="알림 내용 작성해보기" /><br />
      <Button icon="pi pi-bell" label="알림을 전송해보자!" @Click="sendTest"></Button>
    </div>
    <ChatRoom></ChatRoom>
  </div>
</template>
