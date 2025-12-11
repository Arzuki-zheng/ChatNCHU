<template>
  <div class="chat-wrapper">
    <div class="messages-container" ref="messagesContainer">
      <div v-if="chatStore.messages.length === 0" class="empty-state">
        <h3>ChatNCHU 興大校務通</h3>
        <p>您可以詢問關於入學簡章、規章辦法等問題。</p>
      </div>

      <div v-for="msg in chatStore.messages" :key="msg.id" class="message-row">
        <div :class="['bubble', msg.role === 'user' ? 'bubble-user' : 'bubble-ai']">
          <div class="content">{{ msg.content }}</div>

          <div v-if="msg.sources && msg.sources.length" class="sources-list">
            <small>參考來源：</small>
            <div v-for="(source, idx) in msg.sources" :key="idx" class="source-item">
              📄 {{ source.title }}
            </div>
          </div>
        </div>
      </div>

      <div v-if="chatStore.isLoading" class="message-row">
        <div class="bubble bubble-ai typing-indicator">
          <span>.</span><span>.</span><span>.</span>
        </div>
      </div>
    </div>

    <div class="input-container">
      <div class="input-box-wrapper">
        <input
          type="text"
          class="chat-input"
          placeholder="輸入訊息..."
          v-model="inputMessage"
          @keyup.enter="handleSend"
          :disabled="chatStore.isLoading"
        />
        <button
          class="send-btn"
          @click="handleSend"
          :disabled="!inputMessage || chatStore.isLoading"
        >
          送出
        </button>
        <button class="stop-btn" v-if="chatStore.isLoading">停止</button>
      </div>
      <div class="footer-note">內容由 AI 生成，請以學校正式公告為準。</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { useChatStore } from '@/stores/chat'
import { useChatApi } from '@/composables/useChatApi'

// 初始化 Store 和 API
const chatStore = useChatStore()
const { sendMessage } = useChatApi()

const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

// 自動捲動到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 監聽訊息變化，有新訊息就捲動
watch(() => chatStore.messages.length, scrollToBottom)

const handleSend = async () => {
  if (!inputMessage.value.trim()) return

  const query = inputMessage.value
  inputMessage.value = '' // 清空輸入框

  await sendMessage(query)
}
</script>

<style scoped>
/* 讓 Chat View 佔滿父層容器 */
.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

/* 訊息區：彈性伸展，超過高度時捲動 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.empty-state {
  text-align: center;
  margin-top: 20vh;
  color: #64748b;
}

.empty-state h3 {
  margin-bottom: 1rem;
  color: #334155;
}

/* 訊息氣泡樣式 */
.message-row {
  display: flex;
  width: 100%;
}

.bubble {
  padding: 12px 18px;
  border-radius: 12px;
  max-width: 85%;
  line-height: 1.6;
  position: relative;
  word-wrap: break-word; /* 防止長單字破版 */
}

.bubble-user {
  margin-left: auto;
  background-color: #2563eb;
  color: white;
  border-bottom-right-radius: 2px;
}

.bubble-ai {
  margin-right: auto;
  background-color: #f1f5f9;
  color: #334155;
  border-bottom-left-radius: 2px;
}

/* 來源列表樣式 */
.sources-list {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  font-size: 0.85rem;
}

.source-item {
  color: #475569;
  margin-top: 4px;
}

/* 輸入區樣式 */
.input-container {
  padding: 1.5rem;
  background-color: white;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
}

.input-box-wrapper {
  display: flex;
  gap: 10px;
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 8px;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
}

.chat-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 1rem;
  padding: 0 10px;
  color: #334155;
}

.send-btn {
  background-color: #2563eb;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.send-btn:hover:not(:disabled) {
  background-color: #1d4ed8;
}

.send-btn:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

.stop-btn {
  background-color: transparent;
  border: 1px solid #cbd5e1;
  color: #64748b;
  padding: 8px 15px;
  border-radius: 8px;
  cursor: pointer;
}

.footer-note {
  text-align: center;
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 0.8rem;
}

/* 簡單的打字動畫 */
.typing-indicator span {
  animation: blink 1.4s infinite both;
  margin: 0 2px;
  font-size: 1.2rem;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}
.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes blink {
  0% {
    opacity: 0.2;
  }
  20% {
    opacity: 1;
  }
  100% {
    opacity: 0.2;
  }
}
</style>
