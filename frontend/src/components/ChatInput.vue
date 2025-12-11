<template>
  <div class="chat-view">
    
    <div class="messages-container" ref="messagesContainer">
        
        <div v-if="!chatStore.currentChatId && !chatStore.isLoading" class="welcome-message">
            請在左側點擊 **"+ 新對話"** 開始聊天。
        </div>
        
      <div v-for="msg in chatStore.messages" :key="msg.id" :class="['message-bubble', msg.role.toLowerCase()]">
        
        <div class="sender-name">{{ msg.role === 'USER' ? userStore.userInfo?.name || '你' : 'NCHU AI' }}</div>
        
        <div class="message-text">{{ msg.content }}</div>
      </div>
        
    </div>

    <div class="input-area">
      <textarea
        v-model="currentMessage"
        @keydown.enter.prevent="handleSendMessage"
        :placeholder="chatStore.currentChatId ? '輸入訊息，按下 Enter 傳送...' : '請先建立一個新對話'"
        rows="2"
        :disabled="!chatStore.currentChatId"
      ></textarea>
      <button 
        :disabled="!currentMessage.trim() || chatStore.isLoading || !chatStore.currentChatId" 
        @click="handleSendMessage">
        {{ chatStore.isLoading ? '傳送中...' : '送出' }}
      </button>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import { useChatStore } from '@/stores/chat'; 
import { useUserStore } from '@/stores/user'; 

const chatStore = useChatStore();
const userStore = useUserStore();

const currentMessage = ref('');
const messagesContainer = ref<HTMLElement | null>(null);

// 滾動到底部的函式
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 當 messages 陣列變動時，滾動到底部
watch(
  () => chatStore.messages.length, 
  () => {
    scrollToBottom();
  }
);


/**
 * 處理訊息發送，呼叫 Store 中的 action
 */
const handleSendMessage = () => {
    const text = currentMessage.value.trim();
    if (!text || !chatStore.currentChatId) return;

    // 呼叫 Store 中的業務邏輯 (它會處理 API 呼叫、新增訊息到列表)
    chatStore.sendMessage(text);
    
    // 清空輸入框
    currentMessage.value = '';
};
</script>

<style scoped>
.chat-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
  background-color: #f4f7f9;
}

.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding-bottom: 20px;
}

/* 歡迎訊息樣式 */
.welcome-message {
    text-align: center;
    padding: 50px;
    color: #64748b;
    font-size: 1.1rem;
}

.message-bubble {
  max-width: 80%;
  margin-bottom: 15px;
  padding: 10px 15px;
  border-radius: 18px;
  line-height: 1.5;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.message-bubble.user {
  background-color: #2563eb;
  color: white;
  margin-left: auto; /* 靠右對齊 */
  border-bottom-right-radius: 4px; /* 尖角 */
}

.message-bubble.assistant {
  background-color: #ffffff;
  color: #333;
  margin-right: auto; /* 靠左對齊 */
  border-bottom-left-radius: 4px; /* 尖角 */
  border: 1px solid #e2e8f0;
}

.sender-name {
  font-size: 0.8em;
  font-weight: 600;
  margin-bottom: 5px;
  opacity: 0.7;
}

.message-bubble.user .sender-name {
    color: rgba(255, 255, 255, 0.8);
}

.input-area {
  display: flex;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
  background-color: white;
  /* 確保輸入區域是固定的，不會被滾動 */
  flex-shrink: 0; 
}

textarea {
  flex-grow: 1;
  padding: 10px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  resize: none;
  font-family: inherit;
  font-size: 1rem;
}

button {
  padding: 10px 20px;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
  /* 確保按鈕不會因為 Flex 而變形 */
  flex-shrink: 0;
}

button:hover:not(:disabled) {
  background-color: #1d4ed8;
}

button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}
</style>