<script setup>
import ChatRoom from '@/components/chat/ChatRoom.vue';
import DeleteModal from '@/components/DeleteModal.vue';
import WorkflowChangeModal from '@/components/task/WorkflowChangeModal.vue';
import WorkflowRejectModal from '@/components/task/WorkflowRejectModal.vue';
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

// 삭제 모달
const deleteModalVisible = ref(false);
const target = {
  id: '1111',
  name: '삭제할 오브젝트 이름'
};
function doDelete(id) {
  console.log(id);
  // 여기에 삭제 로직
}
function showDelModal() {
  deleteModalVisible.value = true;
}

// 진행상태 반려 모달
const rejectModalVisible = ref(false);
const taskRequest = {
  id: '1111',
  name: '반려할 일감',
  workflow: '완료'
};
function doReject(id) {
  console.log(id);
  // 여기에 반려 로직
}
function showRejectModal() {
  rejectModalVisible.value = true;
}

// 진행상태 변경 모달
const changeModalVisible = ref(false);
const flowChange = {
  id: '1111',
  name: '요청할 일감',
  workflow: '완료'
};
function doChange(id) {
  console.log(id);
  // 여기에 반려 로직
}
function showChangeModal() {
  changeModalVisible.value = true;
}
</script>

<template>
  <div>

    <div class="grid grid-cols-12 gap-8">
      <div class="col-span-12 xl:col-span-6 h-50">
        <InputText type="text" v-model="content" placeholder="알림 내용 작성해보기" /><br />
        <Button icon="pi pi-bell" label="알림을 전송해보자!" @Click="sendTest"></Button>
      </div>
      <ChatRoom></ChatRoom>
    </div>
    <div>
      <Button label="삭제" @click="showDelModal()"></Button>
      <DeleteModal v-model:visible="deleteModalVisible" :target="target" @delete="(task) => doDelete(task.id)" />
    </div>
    <div>
      <Button label="반려" @click="showRejectModal()"></Button>
      <WorkflowRejectModal v-model:visible="rejectModalVisible" :task="taskRequest" @reject="(task) => doReject(task)" />
    </div>
    <div>
      <Button label="변경" @click="showChangeModal()"></Button>
      <WorkflowChangeModal v-model:visible="changeModalVisible" :task="flowChange" @confirm="(task) => doChange(task)" />
    </div>
  </div>
</template>
