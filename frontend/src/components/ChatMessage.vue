<script setup lang="ts">
import type { Role } from '@/stores/chat'
defineProps<{ role: Role; content: string }>()
</script>

<template>
  <div class="flex" :class="role === 'user' ? 'justify-end' : 'justify-start'">
    <article
      class="max-w-[800px] w-fit card px-4 py-3 whitespace-pre-wrap"
      :class="
        role === 'user'
          ? 'bg-neutral-900 text-white rounded-br-md'
          : 'bg-white rounded-bl-md prose prose-neutral max-w-none [&_pre]:!bg-black/90 [&_code]:!text-white [&_a]:text-blue-600'
      "
    >
      <div class="text-[11px] opacity-70 mb-1">
        {{ role === 'user' ? 'You' : role === 'assistant' ? 'Assistant' : 'System' }}
      </div>
      <div v-html="content" />
    </article>
  </div>
</template>
