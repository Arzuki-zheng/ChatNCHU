import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

// 👇 定義 Google 給我們的使用者資料長什麼樣子
interface GoogleUserInfo {
  name: string
  picture: string
  email: string
  sub: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')

  // 👇 這裡改用我們定義好的 GoogleUserInfo，而不是 any
  const userInfo = ref<GoogleUserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
  }

  async function fetchGoogleUserInfo(accessToken: string) {
    try {
      const res = await axios.get('https://www.googleapis.com/oauth2/v3/userinfo', {
        headers: { Authorization: `Bearer ${accessToken}` },
      })
      userInfo.value = res.data
    } catch (error) {
      console.error('Failed to fetch user info', error)
    }
  }

  return { token, userInfo, isLoggedIn, setToken, fetchGoogleUserInfo }
})
