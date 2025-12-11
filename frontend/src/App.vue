<template>
  <div class="app-container">
    <div v-if="!userStore.isLoggedIn" class="login-overlay">
      <div class="login-card">
        <div class="login-logo">NCHU</div>
        <h3>歡迎使用興大校務通</h3>
        <p>請使用學校 Google 帳號登入以存取服務</p>

        <GoogleLogin :callback="handleLoginSuccess" popup-type="TOKEN">
          <button class="google-btn">
            <img src="https://fonts.gstatic.com/s/i/productlogos/googleg/v6/24px.svg" alt="G" />
            使用 Google 帳號登入
          </button>
        </GoogleLogin>
      </div>
    </div>

    <div class="app-layout" :class="{ 'is-blurred': !userStore.isLoggedIn }">
      <aside class="sidebar">
        <div class="logo-area">
          <h2>NCHU</h2>
        </div>

        <nav class="nav-links">
          <RouterLink to="/chat" class="nav-item" active-class="active">
            <span class="icon">💬</span>
            <span class="text">開始對話</span>
          </RouterLink>

          <RouterLink to="/about" class="nav-item" active-class="active">
            <span class="icon">ℹ️</span>
            <span class="text">關於平台</span>
          </RouterLink>
        </nav>

        <div class="user-profile">
          <div class="avatar-circle" v-if="userStore.userInfo">
            <img :src="userStore.userInfo?.picture" :alt="userStore.userInfo?.name" />
          </div>
          <div class="avatar-circle" v-else>U</div>
        </div>
      </aside>

      <main class="main-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 這裡不需要 import GoogleLogin，因為 main.ts 已經全域引用了，如果報錯可以刪掉這行
import { GoogleLogin } from 'vue3-google-login'

const userStore = useUserStore()

// 👇 定義登入成功回傳的資料格式
interface GoogleLoginResponse {
  access_token: string
  token_type?: string
  expires_in?: number
  scope?: string
}

// 👇 這裡把 : any 改成 : GoogleLoginResponse
const handleLoginSuccess = (response: GoogleLoginResponse) => {
  console.log('Google Login Response:', response)

  if (response.access_token) {
    userStore.setToken(response.access_token)
    userStore.fetchGoogleUserInfo(response.access_token)
  }
}
</script>

<style scoped>
.app-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

/* 佈局設定 */
.app-layout {
  display: flex;
  width: 100%;
  height: 100%;
  transition: filter 0.3s ease; /* 模糊過渡動畫 */
}

/* 模糊效果 class */
.app-layout.is-blurred {
  filter: blur(8px) grayscale(40%);
  pointer-events: none; /* 禁止點擊背景 */
}

/* --- 登入遮罩樣式 --- */
.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999; /* 確保在最上層 */
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.2); /* 淡淡的黑底 */
}

.login-card {
  background: white;
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
  width: 100%;
  max-width: 400px;
  animation: slideUp 0.5s ease-out;
}

.login-logo {
  font-size: 2rem;
  font-weight: 900;
  color: #1e293b;
  margin-bottom: 1rem;
}

.login-card h3 {
  margin-bottom: 0.5rem;
  color: #334155;
}

.login-card p {
  color: #64748b;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

.google-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  background: white;
  border: 1px solid #cbd5e1;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  color: #334155;
  transition: all 0.2s;
}

.google-btn:hover {
  background-color: #f8fafc;
  border-color: #94a3b8;
}

.google-btn img {
  width: 20px;
  margin-right: 10px;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* --- 側邊欄樣式 (維持原樣，微調 Avatar) --- */
.sidebar {
  width: 260px;
  background-color: #1e293b;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 1rem;
  border-right: 1px solid #334155;
}
.logo-area {
  padding: 1rem 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid #334155;
  text-align: center;
  font-weight: bold;
  letter-spacing: 1px;
}
.nav-links {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex: 1;
}
.nav-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  color: #94a3b8;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s;
}
.nav-item:hover {
  background-color: #334155;
  color: white;
}
.nav-item.active {
  background-color: #2563eb;
  color: white;
}
.icon {
  margin-right: 10px;
}
.user-profile {
  padding-top: 1rem;
  border-top: 1px solid #334155;
  display: flex;
  justify-content: center;
}
.avatar-circle {
  width: 40px;
  height: 40px;
  background-color: #475569;
  border-radius: 50%;
  overflow: hidden; /* 確保圖片是圓的 */
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
.avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
/* --- 主內容區樣式 --- */
.main-content {
  flex: 1;
  position: relative;
  overflow: hidden;
  background-color: #ffffff;
}
</style>
