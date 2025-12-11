<script setup lang="ts">
import { useChatStore } from '@/stores/chat'
const store = useChatStore()
</script>

<template>
  <aside class="h-full p-4 border-r card rounded-none">
    <div class="flex items-center gap-2 mb-4">
      <div class="size-8 rounded-xl bg-black"></div>
      <div class="font-semibold">ChatNCHU</div>
    </div>

    <button class="btn w-full" @click="store.clear()">🧹 新對話</button>

    <div class="mt-6 space-y-2 text-sm">
      <div class="text-neutral-500">最近對話</div>
      <div class="p-2 rounded-lg hover:bg-black/5 cursor-pointer">第一次對話</div>
      <div class="p-2 rounded-lg hover:bg-black/5 cursor-pointer">技術問題</div>
    </div>

    <div class="mt-auto pt-6 text-xs text-neutral-500">
      <div class="mb-2 font-medium">快捷鍵</div>
      <p>Enter 送出、Shift+Enter 換行</p>
    </div>
  </aside>
</template>
