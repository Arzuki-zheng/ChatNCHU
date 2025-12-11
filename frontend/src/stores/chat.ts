import { defineStore } from 'pinia'
import { ref } from 'vue'

// 定義訊息介面
export interface Message {
  id: string
  role: 'user' | 'assistant'
  content: string
  sources?: Array<{ title: string; url: string; page?: number }> // RAG 專用欄位
  timestamp: number
}

export const useChatStore = defineStore('chat', () => {
  const messages = ref<Message[]>([])
  const isLoading = ref(false)
  const currentSessionId = ref<string | null>(null)

  // 增加訊息
  function addMessage(message: Message) {
    messages.value.push(message)
  }

  // 清空對話
  function clearChat() {
    messages.value = []
  }

  return { messages, isLoading, currentSessionId, addMessage, clearChat }
})
