import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import vue3GoogleLogin from 'vue3-google-login' // 1. 引入

import './assets/main.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 2. 設定 Google Login (請替換成你實際申請的 Client ID)
app.use(vue3GoogleLogin, {
  clientId: '787213252866-68o682e71a7lddneoc43lrsd1pc407l3.apps.googleusercontent.com',
})

app.mount('#app')
