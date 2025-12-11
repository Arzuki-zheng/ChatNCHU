// D:\git\ChatNCHU\frontend\src\stores\user.ts (修正後)

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 定義後端回傳的用戶資料結構
interface UserDataFromBackend {
  id: number
  name: string
  email: string
  picture: string
  // ...
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserDataFromBackend | null>(null)
  const jwtToken = ref<string | null>(null); // 🔥 新增：儲存 JWT Token
  
  const isLoggedIn = computed(() => !!userInfo.value)
  
  /**
   * 接收並設置後端驗證後的用戶資料
   */
  function setUserInfo(data: UserDataFromBackend, token: string) { // 🔥 接收 Token
    userInfo.value = data
    jwtToken.value = token; // 儲存 Token
  }

  return { 
    userInfo, 
    isLoggedIn, 
    jwtToken, // 導出 Token
    setUserInfo 
  }
})