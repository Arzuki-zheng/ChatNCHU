import axios from 'axios'
import { useChatStore } from '@/stores/chat'
import { useUserStore } from '@/stores/user' // 引入 User Store

export function useChatApi() {
  const store = useChatStore()
  const userStore = useUserStore() // 取得 User Store

  const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || '/api/v1',
  })

  // === 關鍵修改：請求攔截器 ===
  api.interceptors.request.use(
    (config) => {
      // 如果使用者已登入，把 Token 塞進 Header
      if (userStore.token) {
        config.headers.Authorization = `Bearer ${userStore.token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    },
  )
  // ==========================

  const sendMessage = async (userQuery: string) => {
    // ... (維持原本的邏輯)
    store.isLoading = true
    store.addMessage({
      /* ... */
    })

    try {
      const response = await api.post('/chat', { query: userQuery })
      store.addMessage({
        /* ... */
      })
    } catch (error) {
      // ...
    } finally {
      store.isLoading = false
    }
  }

  return { sendMessage }
}
