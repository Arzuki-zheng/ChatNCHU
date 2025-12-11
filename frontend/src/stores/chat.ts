// D:\git\ChatNCHU\frontend\src\stores\chat.ts

import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';
import { useUserStore } from './user'; 

// --- 介面定義 (與後端 Java Entity 對應) ---
interface Source {
    title: string;
    // 如果後端返回連結，可以加上 url?: string;
}
// 1. 後端訊息實體 (Message Entity)
interface Message {
  id: number;
  content: string; 
  role: 'USER' | 'ASSISTANT'; 
  createdAt: string; 
  sources?: Source[]; 
}

// 2. 後端對話實體 (Conversation Entity)
interface ChatSession {
    id: number;           
    title: string;        
    createdAt: string;    
}

// 3. 後端 API 響應的通用結構
interface BackendApiResponse<T> {
    status: number; 
    message: string;
    data: T; 
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const useChatStore = defineStore('chat', () => {
    
    // 狀態
    const messages = ref<Message[]>([]);
    const isLoading = ref(false);
    const sessions = ref<ChatSession[]>([]); 
    const currentChatId = ref<number | null>(null);

    const userStore = useUserStore();

    // 輔助函式：建立授權 Header (安全檢查)
    const getAuthHeader = () => {
        const token = userStore.jwtToken; 
        if (!token) throw new Error("JWT Token not found in userStore. Please log in.");
        return {
            'Authorization': `Bearer ${token}` 
        };
    };

    // 動作：加載歷史會話
    const fetchSessions = async () => {
        if (!userStore.isLoggedIn || !userStore.jwtToken) return;
        try {
            const response = await axios.get<BackendApiResponse<ChatSession[]>>(
                `${API_BASE_URL}/chat/list`, 
                { headers: getAuthHeader() }
            );
            
            if (response.data.status === 200) {
                sessions.value = response.data.data || [];
                
                // 修正錯誤：安全檢查 sessions.value.length
                if (!currentChatId.value && sessions.value.length > 0) {
                    const firstSession = sessions.value[0];
                    if (firstSession) {
                        await loadSession(firstSession.id);
                    }
                } else if (sessions.value.length === 0) {
                    currentChatId.value = null;
                }
            }
        } catch (error) {
            console.error('❌ 加載會話列表失敗:', error);
        }
    };

    // 動作：載入特定會話的訊息
    const loadSession = async (chatId: number) => {
        if (!userStore.jwtToken) return;
        currentChatId.value = chatId;
        messages.value = [];
        isLoading.value = true;
        try {
            const response = await axios.get<BackendApiResponse<Message[]>>(
                `${API_BASE_URL}/chat/${chatId}/messages`,
                { headers: getAuthHeader() }
            );
            
            if (response.data.status === 200) {
                messages.value = response.data.data || [];
            }
        } catch (error) {
            console.error(`❌ 載入會話 ${chatId} 訊息失敗:`, error);
        } finally {
            isLoading.value = false;
        }
    };

    // 動作：創建新會話
    const startNewChat = async () => {
        if (!userStore.isLoggedIn || !userStore.jwtToken) return null;
        isLoading.value = true;
        try {
            const response = await axios.post<BackendApiResponse<ChatSession>>(
                `${API_BASE_URL}/chat/new`, 
                { title: '新的對話' }, 
                { headers: getAuthHeader() }
            );
            
            if (response.data.status === 200) {
                const newSession = response.data.data;

                sessions.value.unshift(newSession);
                currentChatId.value = newSession.id;
                messages.value = [];
                return newSession.id;
            }
            return null;
            
        } catch (error) {
            console.error('❌ 創建新會話失敗:', error);
            return null;
        } finally {
            isLoading.value = false;
        }
    };
    
    // 動作：發送訊息到 LLM (修正 Message 結構錯誤)
    const sendMessage = async (text: string) => {
        if (isLoading.value || !currentChatId.value || !userStore.jwtToken) return;

        // 1. 新增使用者訊息到前端列表 (僅包含 Message 介面屬性)
        const userMessage: Message = {
            id: Date.now(),
            content: text,
            role: 'USER',
            createdAt: new Date().toISOString(),
            // 這裡不再有 sender, text, timestamp 屬性 (解決 TS2353 錯誤)
        };
        messages.value.push(userMessage);

        isLoading.value = true;

        try {
            // 2. 呼叫後端 API
            const response = await axios.post<BackendApiResponse<Message[]>>(
                `${API_BASE_URL}/chat/${currentChatId.value}/message`, 
                { content: text }, 
                { headers: getAuthHeader() }
            );

            if (response.data.status === 200) {
                const resultMessages = response.data.data;
                
                // 找到 AI 回覆並添加到列表
                const aiMsg = resultMessages.find(m => m.role === 'ASSISTANT');
                
                if (aiMsg) {
                    messages.value.push(aiMsg); 
                }
            } else {
                console.error('LLM 服務失敗:', response.data.message);
            }

        } catch (error) {
            console.error('❌ 聊天 API 呼叫失敗:', error);
            // 處理錯誤訊息 (僅包含 Message 介面屬性)
            messages.value.push({
                id: Date.now() + 1,
                content: '連線錯誤，無法取得 AI 回覆。',
                role: 'ASSISTANT',
                createdAt: new Date().toISOString(),
            });
        } finally {
            isLoading.value = false;
        }
    };


    return {
        messages,
        isLoading,
        sessions,
        currentChatId,
        fetchSessions,
        loadSession,
        startNewChat,
        sendMessage,
    };
});