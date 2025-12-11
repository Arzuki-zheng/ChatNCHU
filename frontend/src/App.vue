<template>
  <div class="app-container">
    <div v-if="!userStore.isLoggedIn" class="login-overlay">
      <div class="login-card">
        <div class="login-logo">NCHU</div>
        <h3>歡迎使用興大校務通</h3>
        <p>請使用學校 Google 帳號登入以存取服務</p>
          <GoogleLogin :callback="handleCredentialResponse" />
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
            <span class="text">開始對話!</span>
          </RouterLink>

          <RouterLink to="/about" class="nav-item" active-class="active">
            <span class="icon">ℹ️</span>
            <span class="text">關於平台</span>
          </RouterLink>
           
  <button class="nav-item " active-class="active" @click="chatStore.startNewChat" :disabled="chatStore.isLoading">
    <span class="icon">+</span>
    <span class="text">新對話</span>
  </button>

<nav class="session-links">
            <div 
                v-for="session in chatStore.sessions" 
                :key="session.id"
                :class="['nav-item', { 'active': session.id === chatStore.currentChatId }]"
                @click="chatStore.loadSession(session.id)">
                {{ session.title }}
            </div>
            <div v-if="chatStore.sessions.length === 0 && !chatStore.isLoading" class="no-session-msg">
                沒有歷史會話，請開始一個新對話。
            </div>
        </nav>
        </nav>
        <div class="sidebar-footer">
        </div>   
        <div class="user-profile">
    
    <div class="avatar-container">
        <div class="avatar-circle" v-if="userStore.userInfo">
            
            <img 
                :src="userStore.userInfo.picture" 
                :alt="userStore.userInfo.name" 
                v-if="userStore.userInfo.picture" 
            />
            <span v-else>
                {{ userStore.userInfo.name.charAt(0) }} 
            </span>
        </div>
        <div class="avatar-circle" v-else>U</div>
    </div>

    <div class="user-info-text" v-if="userStore.userInfo">
        <span class="hi-message">Hi {{ userStore.userInfo.name }}</span>
    </div>

</div>
      </aside>

      <main class="main-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { decodeCredential } from 'vue3-google-login'
// 1. 匯入型別定義
import type { CallbackTypes } from 'vue3-google-login'
import { RouterLink, RouterView } from 'vue-router'
import { useUserStore } from '@/stores/user'
// 這裡不需要 import GoogleLogin，因為 main.ts 已經全域引用了，如果報錯可以刪掉這行
import { GoogleLogin } from 'vue3-google-login'
import axios from 'axios'
import { useChatStore } from '@/stores/chat'
const userStore = useUserStore()
// 程式碼中缺少這一行
const chatStore = useChatStore() // 正確的實例名稱
// 從 Vite 環境變數中取得後端 API 基礎 URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

// 👇 定義登入成功回傳的資料格式
interface GoogleLoginResponse {
    // 雖然我們主要用 id_token，但 access_token 可能仍然存在
    access_token?: string 
    id_token: string // <-- 關鍵：ID Token 是標準 JWT 格式
    token_type?: string
    expires_in?: number
    scope?: string
}

// 假設後端返回的成功資料格式
// App.vue <script setup>

// 修正後端返回的成功資料格式，將 'code' 改為 'status'
interface BackendUserResponse {
    status: number
    message?: string
    mes: string
    data: {
        id?: number
        userId?: number
        message: string
        name: string
        email: string
        picture?: string
    }
}

/**
 * 處理 Google 登入成功後的回調
 * @param response 包含 Google Access Token 的響應物件
 */

const handleCredentialResponse = async (response: { credential?: string }) => {
    
    // 🎯 新版 GIS 中，ID Token 位於 credential 欄位
    if (response.credential) {
        
        // 1. 取得 ID Token
        const googleToken = response.credential.trim() 
        
        // 2. 呼叫後端業務邏輯
        try {
            // 呼叫後端 API
            const backendResponse = await axios.post<BackendUserResponse>(
                `${API_BASE_URL}/auth/google-login`,
                {}, 
                {
                    headers: {
                        // 帶上 ID Token，供後端 Spring Security 進行 JWT 驗證
                        'Authorization': `Bearer ${googleToken}` 
                    }
                }
            )

            if (backendResponse.data && backendResponse.data.status === 200) { 
                const backendData = backendResponse.data.data

                // 🌟 關鍵修正：建立一個新的物件，確保它完全符合 user.ts 的 UserDataFromBackend 介面
                if (backendData.userId) { // 確保我們有有效的 ID

                    const userStoreData = {
                        // 確保將後端返回的 userId 作為 id 欄位傳遞
                        id: backendData.userId, 
                        name: backendData.name,
                        email: backendData.email,
                        picture: backendData.picture || "", // 確保 picture 是 string
                        // ... 如果 UserDataFromBackend 有其他必填欄位，在這裡加入
                    };
                    
                    // 由於我們確保了 userStoreData 符合 UserDataFromBackend { id: number, ... }
                    userStore.setUserInfo(userStoreData,googleToken); // 傳遞符合要求的數據
                    
                    console.log('✅ 後端驗證並登入成功:', userStoreData.name)
                } else {
                    console.error('❌ 後端資料錯誤: 缺少用戶識別 ID (userId)。')
                    alert('登入失敗：後端未返回有效的用戶 ID。');
                }

            } else {
                console.error('❌ 後端業務邏輯失敗:', backendResponse.data.mes) 
                alert(`登入失敗: ${backendResponse.data.mes}`);
            }

        } catch (error) {
            console.error('❌ 連線/驗證錯誤:', error)
            alert('登入失敗，請檢查後端服務是否運行或網路連線。')
        }
    } else {
        console.error('❌ Google 登入失敗：未取得 ID Token (credential 欄位為空)。')
        alert('登入失敗：Google 服務未返回身份驗證 Token。');
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
/* --- 側邊欄樣式 (修正 user-profile 布局) --- */
.user-profile {
    padding-top: 1rem;
    border-top: 1px solid #334155;
    display: flex; /* 啟用 Flex 布局 */
    justify-content: flex-start; /* 從左側開始排列 */
    align-items: center; /* 垂直居中 */
    gap: 10px; /* 頭像和文字之間的間距 */
}

/* 確保頭像容器為圓形並居中 */
.avatar-circle {
    width: 40px;
    height: 40px;
    background-color: #475569;
    border-radius: 50%;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    color: white; /* 確保首字母可見 */
}

.avatar-circle img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* 使用者名稱樣式 */
.user-info-text {
    flex-grow: 1; /* 佔用剩餘空間 */
    color: white; 
    font-size: 0.95rem;
    font-weight: 500;
    white-space: nowrap; /* 防止名稱換行 */
    overflow: hidden;
    text-overflow: ellipsis; /* 超出時顯示省略號 */
}
.new-chat-btn {
    display: flex; /* 讓內容在按鈕內水平居中對齊 */
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 10px 15px;
    margin-bottom: 20px; /* 與上方連結或下方會話列表保持距離 */
    border: none;
    border-radius: 8px; /* 圓角 */
    background-color: #007bff; /* 主色調：藍色 */
    color: white;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s ease;
}
.new-chat-btn:hover:not(:disabled) {
    background-color: #0056b3; /* Hover 變深 */
}

.new-chat-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.new-chat-btn .icon {
    margin-right: 8px;
    font-size: 1.2em;
}
.session-links {
    flex-grow: 1; /* 讓會話列表佔滿剩餘空間 */
    overflow-y: auto; /* 允許滾動條顯示歷史會話 */
    padding-top: 10px;
}

.session-header {
    font-size: 14px;
    color: #6c757d; /* 灰色標題 */
    padding: 5px 10px;
    margin-bottom: 5px;
    font-weight: 500;
    border-bottom: 1px solid #e9ecef; /* 分隔線 */
}

.session-item {
    display: block;
    padding: 10px 15px;
    margin-bottom: 4px;
    border-radius: 6px;
    cursor: pointer;
    white-space: nowrap; /* 防止標題換行 */
    overflow: hidden;
    text-overflow: ellipsis; /* 超出部分顯示省略號 */
    color: #333;
    background-color: transparent;
    transition: background-color 0.15s ease, color 0.15s ease;
}

.session-item:hover {
    background-color: #e2e6ea; /* 淺灰 Hover 背景 */
}

.session-item.active {
    background-color: #007bff; /* 活躍項目使用主色調 */
    color: white;
    font-weight: 600;
}

.session-item.active:hover {
    background-color: #007bff; /* 活躍項目 Hover 不變色 */
}

.no-session-msg {
    padding: 10px;
    font-size: 14px;
    color: #999;
    text-align: center;
}
</style>
