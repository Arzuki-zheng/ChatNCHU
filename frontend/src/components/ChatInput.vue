<script setup lang="ts">
import { ref } from 'vue'
import { useChatStore } from '@/stores/chat'
import { useChatApi } from '@/composables/useChatApi'
const store = useChatStore()
const { sendOnce, stop } = useChatApi()
const text = ref('')

async function onSend() {
  if (!text.value.trim() || store.sending) return
  const t = text.value
  text.value = ''
  await sendOnce(t)
}
function onKey(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    onSend()
  }
}
</script>

<template>
  <div class="flex items-end gap-2">
    <textarea
      v-model="text"
      @keydown="onKey"
      rows="1"
      placeholder="輸入訊息…"
      class="flex-1 resize-none rounded-2xl border bg-white px-4 py-3 shadow-sm focus:outline-none focus:ring-2 focus:ring-black/20"
    />
    <button class="btn" :disabled="store.sending" @click="onSend">送出</button>
    <button class="btn-ghost" :disabled="!store.sending" @click="stop">停止</button>
  </div>
</template>
