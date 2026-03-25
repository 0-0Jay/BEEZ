<script setup>
import { useChatStore } from '@/stores/chat';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { nextTick, onMounted, ref } from 'vue';

const chatStore = useChatStore();
const isOpen = ref(false);
const message = ref('');
const messagesContainer = ref(null);
const replyTarget = ref(null);

const stompClient = ref(null);
const projectId = 'testproject';

onMounted(async () => {
  const res = chatStore.findChatList(projectId);
  message.value = res;
  connect();
});

const connect = () => {
  const socket = new SockJS('http://localhost:8888/ws/chat');
  stompClient.value = Stomp.over(socket);

  stompClient.value.connect({}, () => {
    console.log('connected');

    // 구독
    stompClient.value.subscribe(`/chat/${projectId}`, (res) => {
      const msg = JSON.parse(res.body);

      messages.value.push({
        id: msg.id,
        text: msg.text,
        isMine: false,
        time: msg.createdOn,
        userId: msg.userId,
        userName: msg.userName,
        replyTo: msg.parentId
      });
    });
  });
};

const messages = ref([
  { id: 1, text: '안녕하세요! 무엇을 도와드릴까요? 😊', isMine: false, time: '오후 2:30', userId: 20261111, userName: '김개발', replyTo: null },
  { id: 2, text: '네, 주문 관련해서 문의드리고 싶어요.', isMine: true, time: '오후 2:31', userId: 20261111, userName: '김개발', replyTo: null },
  { id: 3, text: '물론이죠! 주문 번호를 알려주시겠어요?', isMine: false, time: '오후 2:31', userId: 20261111, userName: '김개발', replyTo: null },
  {
    id: 4,
    text: '주문 번호는 ORD-20240325-001 입니다!',
    isMine: true,
    time: '오후 2:32',
    userId: 20261111,
    userName: '김개발',
    replyTo: { id: 3, text: '물론이죠! 주문 번호를 알려주시겠어요?', userName: '김개발' }
  },
  {
    id: 5,
    text: '확인했습니다. 해당 주문은 현재 배송 중입니다.',
    isMine: false,
    time: '오후 2:33',
    userId: 20261111,
    userName: '김개발',
    replyTo: { id: 4, text: '주문 번호는 ORD-20240325-001 입니다!', userName: '김개발' }
  }
]);

const toggleChat = () => {
  isOpen.value = !isOpen.value;
};

const setReply = (msg) => {
  replyTarget.value = { id: msg.id, text: msg.text, userName: msg.userName };
  nextTick(() => document.querySelector('.msg-input')?.focus());
};

const cancelReply = () => {
  replyTarget.value = null;
};

const scrollToMessage = (targetId) => {
  const el = document.getElementById(`msg-${targetId}`);
  if (!el) return;
  el.scrollIntoView({ behavior: 'smooth', block: 'center' });
  el.classList.add('highlight');
  setTimeout(() => el.classList.remove('highlight'), 1500);
};

const sendMessage = async () => {
  if (!message.value.trim()) return;
  const time = new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
  messages.value.push({
    id: Date.now(),
    text: message.value,
    isMine: true,
    time,
    userId: 20261111,
    userName: '김개발',
    replyTo: replyTarget.value ? { ...replyTarget.value } : null
  });
  message.value = '';
  replyTarget.value = null;
  await nextTick();
  if (messagesContainer.value) messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
};
</script>

<template>
  <div>
    <!-- 채팅창 -->
    <Transition name="chat-slide">
      <div v-if="isOpen" class="chat-window fixed bottom-8 right-[90px] w-[340px] h-[480px] bg-white rounded-[20px] flex flex-col overflow-hidden">
        <!-- 헤더 -->
        <div class="flex items-center justify-between px-[18px] shrink-0 bg-[#f5a623]">
          <p class="text-[15px] font-bold text-white pt-4">프로젝트 이름</p>
          <button class="close-btn w-[30px] h-[30px] rounded-full flex items-center justify-center border-none cursor-pointer text-white" @click="toggleChat">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M1 1L13 13M13 1L1 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
          </button>
        </div>

        <!-- 메시지 영역 -->
        <div ref="messagesContainer" class="messages-area flex-1 px-[14px] py-4 overflow-y-auto bg-[#fafafa] flex flex-col gap-[10px] scroll-smooth">
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

              <span class="text-[11px] text-[#bbb]">{{ msg.userName }}({{ msg.userId }}) · {{ msg.time }}</span>
            </div>
          </div>
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
      <span v-if="!isOpen" class="absolute -top-0.5 -right-0.5 w-[18px] h-[18px] rounded-full bg-red-500 text-white text-[11px] font-bold flex items-center justify-center border-2 border-white">3</span>
    </button>
  </div>
</template>

<style>
/* ── Tailwind로 표현 불가한 것만 남김 ── */

/* 채팅창 그림자 */
.chat-window {
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.12),
    0 4px 16px rgba(245, 166, 35, 0.15);
  font-family: 'Pretendard', 'Apple SD Gothic Neo', sans-serif;
}

/* 헤더 닫기 버튼 호버 */
.close-btn {
  background: rgba(255, 255, 255, 0.2);
  transition: background 0.2s;
}
.close-btn:hover {
  background: rgba(255, 255, 255, 0.35);
}

/* 메시지 스크롤바 */
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

/* 상대방 말풍선 그림자 */
.bubble-theirs {
  box-shadow:
    0 1px 6px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(0, 0, 0, 0.05);
}

/* 말풍선 호버 시 답장 버튼 노출 */
.bubble-group:hover .reply-btn {
  opacity: 1;
}
.reply-btn:hover {
  color: #f5a623;
  background: rgba(245, 166, 35, 0.1);
}

/* 답장 인용 블록 호버 */
.reply-quote {
  transition:
    opacity 0.15s,
    transform 0.15s;
}
.reply-quote:hover {
  opacity: 0.78;
  transform: translateY(-1px);
}

/* 전송 버튼 그림자 & 호버 */
.send-btn {
  box-shadow: 0 2px 8px rgba(245, 166, 35, 0.4);
}
.send-btn:hover {
  background: #e8970f !important;
}

/* 토글 버튼 그림자 & 호버 */
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

/* 원본 메시지 하이라이트 */
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

/* Vue Transition */
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
.preview-slide-enter-from {
  opacity: 0;
  transform: translateY(6px);
}
.preview-slide-leave-to {
  opacity: 0;
  transform: translateY(6px);
}
</style>
