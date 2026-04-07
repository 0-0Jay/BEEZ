<script setup>
import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import { useProjectStore } from '@/stores/project';
import { Client } from '@stomp/stompjs';
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';

/* ---------------- 상태 ---------------- */
const isOpen = ref(false);
const message = ref('');
const messages = ref([]);
const replyTarget = ref(null);
const messagesContainer = ref(null);
const unreadCount = ref(0);
const hasNewMessage = ref(false); // "새로운 채팅이 있습니다." 배너 표시 여부
const chatStore = useChatStore();
const projectStore = useProjectStore();
const authStore = useAuthStore();
const project = computed(() => projectStore.selectedProject);
const projectId = computed(() => project.value?.id);
const userId = computed(() => authStore.user.id ?? '');

let stompClient = null;
let currentSubscription = null;

/* ---------------- 채팅창 토글 ---------------- */
const toggleChat = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    unreadCount.value = 0;
    scrollToBottom();
  }
};

/* ---------------- 스크롤 하단 여부 판단 ---------------- */
const isNearBottom = () => {
  const el = messagesContainer.value;
  if (!el) return true;
  return el.scrollHeight - el.scrollTop - el.clientHeight < 100; // 하단에서 100px 이내면 "최하단"으로 간주
};

/* ---------------- 스크롤 ---------------- */
const scrollToBottom = async () => {
  await nextTick();
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    hasNewMessage.value = false;
  }
};

const scrollToMessage = async (id) => {
  await nextTick();
  const el = document.getElementById(`msg-${id}`);
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' });
    el.classList.add('highlight');
    setTimeout(() => el.classList.remove('highlight'), 1500);
  }
};

/* ---------------- 새 메시지 수신 처리 ---------------- */
const handleNewMessage = (mapped) => {
  messages.value.push(mapped);
  linkReplies();

  if (isOpen.value) {
    if (isNearBottom()) {
      scrollToBottom();
    } else {
      hasNewMessage.value = true;
    }
  } else {
    unreadCount.value++;
  }
};

/* ---------------- 답장 ---------------- */
const setReply = (msg) => {
  replyTarget.value = msg;
};
const cancelReply = () => {
  replyTarget.value = null;
};

/* ---------------- 메시지 변환 ---------------- */
const mapMessage = (chat) => ({
  id: chat.id || Date.now(),
  text: chat.content,
  userId: chat.userId,
  userName: chat.name || null,
  time: chat.createdOn ? new Date(chat.createdOn).toLocaleTimeString() : new Date().toLocaleTimeString(),
  isMine: chat.userId === userId.value,
  parentId: chat.parentId,
  replyTo: null
});

/* ---------------- 부모 메시지 연결 ---------------- */
const linkReplies = () => {
  const map = {};
  messages.value.forEach((m) => (map[m.id] = m));
  messages.value.forEach((m) => {
    if (m.parentId && map[m.parentId]) {
      m.replyTo = {
        id: map[m.parentId].id,
        userName: map[m.parentId].userName,
        text: map[m.parentId].text
      };
    }
  });
};

/* ---------------- 메시지 전송 ---------------- */
const sendMessage = () => {
  if (!message.value.trim()) return;

  stompClient.publish({
    destination: `/send/chat/${projectId.value}`,
    body: JSON.stringify({
      userId: userId.value,
      content: message.value,
      parentId: replyTarget.value ? replyTarget.value.id : null
    })
  });

  message.value = '';
  replyTarget.value = null;
};

// 웹소캣 구독
const subscribeToProject = (id) => {
  // 기존 구독 해제
  if (currentSubscription) {
    currentSubscription.unsubscribe();
    currentSubscription = null;
  }

  if (!id || !stompClient?.connected) return;

  currentSubscription = stompClient.subscribe(`/chat/${id}`, (msg) => {
    const data = JSON.parse(msg.body).body;
    const mapped = mapMessage({ ...data, userName: data.userName || data.userId });
    handleNewMessage(mapped);
  });
};

/* ---------------- 웹소켓 연결 ---------------- */
const connect = () => {
  stompClient = new Client({
    brokerURL: 'ws://localhost:8888/ws/chat',
    reconnectDelay: 5000,
    onConnect: () => {
      subscribeToProject(projectId.value);
    }
  });
  stompClient.activate();
};

/* ---------------- 초기 메시지 조회 ---------------- */
const loadMessages = async () => {
  try {
    const res = await chatStore.findChatList(projectId.value);
    messages.value = res.map(mapMessage);
    linkReplies();
    scrollToBottom();
  } catch (e) {
    console.error(e);
  }
};

// 프로젝트 변경
watch(
  () => projectId.value,
  async (projectId) => {
    messages.value = null;
    hasNewMessage.value = false;
    unreadCount.value = 0;

    subscribeToProject(projectId);
    await loadMessages();
  },
  { immediate: true }
);

/* ---------------- lifecycle ---------------- */
onMounted(() => {
  loadMessages();
  connect();
});

onUnmounted(() => {
  currentSubscription?.unsubscribe();
  stompClient?.deactivate();
});
</script>

<template>
  <div>
    <!-- 채팅창 -->
    <Transition name="chat-slide">
      <div v-if="isOpen" class="chat-window fixed bottom-8 right-[90px] w-[340px] h-[480px] bg-white rounded-[20px] flex flex-col overflow-hidden">
        <!-- 헤더 -->
        <div class="flex items-center justify-between px-[18px] py-4 shrink-0 bg-[#f5a623]">
          <p class="text-[15px] font-bold text-white m-0">{{ project.title }}</p>
          <button class="close-btn w-[30px] h-[30px] rounded-full flex items-center justify-center border-none cursor-pointer text-white" @click="toggleChat">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M1 1L13 13M13 1L1 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
          </button>
        </div>

        <!-- 메시지 영역 (relative로 배너 기준점) -->
        <div class="relative flex-1 overflow-hidden">
          <div ref="messagesContainer" class="messages-area h-full px-[14px] py-4 overflow-y-auto bg-[#fafafa] flex flex-col gap-[10px] scroll-smooth">
            <div v-for="msg in messages" :id="`msg-${msg.id}`" :key="msg.id" class="flex" :class="msg.isMine ? 'justify-end' : 'justify-start'">
              <div class="flex flex-col gap-1 max-w-[78%]" :class="msg.isMine ? 'items-end' : 'items-start'">
                <!-- 답장 인용 블록 -->
                <button
                  v-if="msg.replyTo"
                  class="reply-quote flex items-center gap-[7px] px-[10px] py-[6px] pl-2 rounded-[10px] border-none cursor-pointer text-left w-full"
                  :class="msg.isMine ? 'bg-[rgba(245,166,35,0.13)]' : 'bg-black/5'"
                  @click="scrollToMessage(msg.replyTo.id)"
                >
                  <div class="w-[3px] self-stretch rounded-sm shrink-0" :class="msg.isMine ? 'bg-[#f5a623]' : 'bg-[#bbb]'"></div>
                  <div class="flex flex-col gap-[2px] flex-1 min-w-0">
                    <span class="text-[11px] font-bold" :class="msg.isMine ? 'text-[#e8970f]' : 'text-[#888]'">{{ msg.replyTo.userName }}</span>
                    <span class="text-[12px] text-[#777] truncate">{{ msg.replyTo.text }}</span>
                  </div>
                  <svg class="text-[#ccc] shrink-0" width="12" height="12" viewBox="0 0 24 24" fill="none">
                    <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                </button>

                <!-- 말풍선 + 답장 버튼 -->
                <div class="bubble-group flex items-center gap-1" :class="msg.isMine ? 'flex-row' : 'flex-row-reverse'">
                  <button class="reply-btn p-[5px] rounded-full border-none cursor-pointer flex items-center justify-center shrink-0 text-[#ccc] opacity-0 transition-all duration-150" @click="setReply(msg)" title="답장">
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
                      <path d="M9 17L4 12L9 7M4 12H15C17.2091 12 19 13.7909 19 16V20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                    </svg>
                  </button>
                  <div class="bubble px-[14px] py-[10px] rounded-[18px] text-[14px] leading-[1.5] break-words" :class="msg.isMine ? 'bg-[#f5a623] text-white rounded-br-[4px]' : 'bg-white text-[#1a1a1a] rounded-bl-[4px] bubble-theirs'">
                    {{ msg.text }}
                  </div>
                </div>

                <span class="text-[11px] text-[#bbb]">
                  {{ msg.isMine ? `나 · ${msg.time}` : `${msg.userName ?? msg.userId} · ${msg.time}` }}
                </span>
              </div>
            </div>
          </div>

          <!-- 새 메시지 배너 -->
          <Transition name="banner-slide">
            <button
              v-if="hasNewMessage"
              class="new-msg-banner absolute bottom-3 left-1/2 -translate-x-1/2 flex items-center gap-2 px-4 py-[7px] rounded-full border-none cursor-pointer text-white text-[13px] font-semibold whitespace-nowrap"
              @click="scrollToBottom"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M12 5V19M12 19L5 12M12 19L19 12" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              새로운 채팅이 있습니다.
            </button>
          </Transition>
        </div>

        <!-- 답장 미리보기 바 -->
        <Transition name="preview-slide">
          <div v-if="replyTarget" class="flex items-center gap-2 px-[14px] py-2 bg-[#fff8ee] border-t border-[#fce8b8] shrink-0">
            <div class="w-[3px] h-8 bg-[#f5a623] rounded-sm shrink-0"></div>
            <div class="flex flex-col gap-[2px] flex-1 min-w-0">
              <span class="text-[11px] font-bold text-[#f5a623]">{{ replyTarget.userName }}에게 답장</span>
              <span class="text-[12px] text-[#999] truncate">{{ replyTarget.text }}</span>
            </div>
            <button class="bg-transparent border-none text-[#bbb] cursor-pointer p-1 rounded-full flex items-center justify-center shrink-0 hover:text-[#888] transition-colors duration-150" @click="cancelReply">
              <svg width="12" height="12" viewBox="0 0 14 14" fill="none">
                <path d="M1 1L13 13M13 1L1 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
              </svg>
            </button>
          </div>
        </Transition>

        <!-- 입력창 -->
        <div class="flex items-center gap-2 px-[14px] py-3 border-t border-[#f0f0f0] bg-white shrink-0">
          <input
            v-model="message"
            type="text"
            placeholder="메시지를 입력하세요..."
            class="msg-input flex-1 border-[1.5px] border-[#ebebeb] rounded-full px-4 py-[9px] text-[14px] text-[#1a1a1a] outline-none bg-[#fafafa] transition-colors duration-200 placeholder:text-[#c0c0c0] focus:border-[#f5a623] focus:bg-white"
            @keyup.enter="sendMessage"
          />
          <button class="send-btn w-10 h-10 rounded-full border-none cursor-pointer flex items-center justify-center shrink-0 transition-all duration-150 hover:scale-105 active:scale-95 bg-[#f5a623]" @click="sendMessage">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M22 2L11 13M22 2L15 22L11 13L2 9L22 2Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </button>
        </div>
      </div>
    </Transition>

    <!-- 플로팅 버튼 -->
    <button class="toggle-btn fixed bottom-7 right-7 w-14 h-14 rounded-full border-none cursor-pointer flex items-center justify-center bg-[#f5a623] transition-all duration-200 hover:scale-[1.08] active:scale-95" @click="toggleChat">
      <Transition name="icon-swap" mode="out-in">
        <svg v-if="!isOpen" key="chat" width="26" height="26" viewBox="0 0 24 24" fill="none">
          <path
            d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z"
            stroke="white"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            fill="rgba(255,255,255,0.15)"
          />
        </svg>
        <svg v-else key="close" width="22" height="22" viewBox="0 0 24 24" fill="none">
          <path d="M18 6L6 18M6 6L18 18" stroke="white" stroke-width="2.5" stroke-linecap="round" />
        </svg>
      </Transition>

      <Transition name="badge-pop">
        <span v-if="!isOpen && unreadCount > 0" class="absolute -top-1 -right-1 min-w-[20px] h-5 px-1 rounded-full bg-red-500 text-white text-[11px] font-bold flex items-center justify-center border-2 border-white leading-none">
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </span>
      </Transition>
    </button>
  </div>
</template>

<style>
.chat-window {
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.12),
    0 4px 16px rgba(245, 166, 35, 0.15);
  font-family: 'Pretendard', 'Apple SD Gothic Neo', sans-serif;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  transition: background 0.2s;
}
.close-btn:hover {
  background: rgba(255, 255, 255, 0.35);
}

.messages-area::-webkit-scrollbar {
  width: 4px;
}
.messages-area::-webkit-scrollbar-track {
  background: transparent;
}
.messages-area::-webkit-scrollbar-thumb {
  background: #e0e0e0;
  border-radius: 4px;
}

.bubble-theirs {
  box-shadow:
    0 1px 6px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(0, 0, 0, 0.05);
}

.bubble-group:hover .reply-btn {
  opacity: 1;
}
.reply-btn:hover {
  color: #f5a623;
  background: rgba(245, 166, 35, 0.1);
}

.reply-quote {
  transition:
    opacity 0.15s,
    transform 0.15s;
}
.reply-quote:hover {
  opacity: 0.78;
  transform: translateY(-1px);
}

.send-btn {
  box-shadow: 0 2px 8px rgba(245, 166, 35, 0.4);
}
.send-btn:hover {
  background: #e8970f !important;
}

.toggle-btn {
  box-shadow:
    0 4px 20px rgba(245, 166, 35, 0.5),
    0 2px 8px rgba(0, 0, 0, 0.1);
}
.toggle-btn:hover {
  background: #e8970f !important;
  box-shadow:
    0 6px 24px rgba(245, 166, 35, 0.6),
    0 2px 10px rgba(0, 0, 0, 0.12);
}

.new-msg-banner {
  background: #f5a623;
  box-shadow:
    0 4px 16px rgba(245, 166, 35, 0.45),
    0 1px 4px rgba(0, 0, 0, 0.12);
  transition:
    transform 0.15s,
    box-shadow 0.15s;
}
.new-msg-banner:hover {
  box-shadow:
    0 6px 20px rgba(245, 166, 35, 0.55),
    0 2px 6px rgba(0, 0, 0, 0.14);
}

@keyframes highlight-pulse {
  0% {
    box-shadow: none;
  }
  25% {
    box-shadow: 0 0 0 3px rgba(245, 166, 35, 0.5);
  }
  100% {
    box-shadow: none;
  }
}
.highlight .bubble {
  animation: highlight-pulse 1.5s ease;
}

.badge-pop-enter-active {
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.badge-pop-leave-active {
  transition: all 0.15s ease-in;
}
.badge-pop-enter-from,
.badge-pop-leave-to {
  opacity: 0;
  transform: scale(0.4);
}

.banner-slide-enter-active {
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.banner-slide-leave-active {
  transition: all 0.18s ease-in;
}
.banner-slide-enter-from {
  opacity: 0;
  transform: translateX(-50%) translateY(12px);
}
.banner-slide-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(12px);
}

.chat-slide-enter-active {
  transition: all 0.28s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.chat-slide-leave-active {
  transition: all 0.2s ease-in;
}
.chat-slide-enter-from {
  opacity: 0;
  transform: translateX(20px) scale(0.95);
  transform-origin: bottom right;
}
.chat-slide-leave-to {
  opacity: 0;
  transform: translateX(16px) scale(0.96);
  transform-origin: bottom right;
}

.icon-swap-enter-active,
.icon-swap-leave-active {
  transition: all 0.18s ease;
}
.icon-swap-enter-from {
  opacity: 0;
  transform: rotate(-20deg) scale(0.7);
}
.icon-swap-leave-to {
  opacity: 0;
  transform: rotate(20deg) scale(0.7);
}

.preview-slide-enter-active {
  transition: all 0.2s ease;
}
.preview-slide-leave-active {
  transition: all 0.15s ease;
}
.preview-slide-enter-from,
.preview-slide-leave-to {
  opacity: 0;
  transform: translateY(6px);
}
</style>
