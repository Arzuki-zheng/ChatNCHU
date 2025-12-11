<script setup lang="ts">
import { useChatStore } from '@/stores/chat'
const store = useChatStore()
const models = ['gpt-4o-mini', 'gpt-4o', 'my-backend-model']
</script>

<template>
  <header
    class="h-14 sticky top-0 z-10 backdrop-blur bg-white/70 border-b px-4 flex items-center justify-between"
  >
    <div class="font-medium">Chat 控制台</div>
    <div class="flex items-center gap-3">
      <select v-model="store.model" class="rounded-lg border px-3 py-2 bg-white">
        <option v-for="m in models" :key="m" :value="m">{{ m }}</option>
      </select>
      <button class="btn-ghost">設定</button>
    </div>
  </header>
</template>
