// main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import vue3GoogleLogin from 'vue3-google-login'

import './assets/main.css'

// 🎯 1. 從環境變數中讀取 Client ID
const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID

if (!GOOGLE_CLIENT_ID) {
  console.error("❌ 錯誤：VITE_GOOGLE_CLIENT_ID 未在 .env 檔案中設定！請檢查您的 .env.development 檔案。")
}

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 🎯 2. 修正：只傳遞 clientId。
// 【注意】：這裡不再傳遞 scope 或 responseType，讓元件自己處理。
app.use(vue3GoogleLogin, {
clientId: GOOGLE_CLIENT_ID || '請在 .env 中設定 Client ID', 
})

app.mount('#app')