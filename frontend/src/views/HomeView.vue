<script setup lang="ts">
import TheWelcome from '../components/TheWelcome.vue'
import HelloCard from '@/components/HelloCard.vue'
import { useCounterStore } from '@/stores/counter'
const counter = useCounterStore()
</script>

<template>
  <section>
    <h1>首頁</h1>
    <h1 class="text-2xl font-bold text-blue-600">Hello Tailwind v4</h1>

    <HelloCard title="你好！" subtitle="我是小卡片元件">
      <p>--可以加內容。</p>
    </HelloCard>

    <p>n = {{ counter.count }}，double = {{ counter.double }}</p>
    <button @click="counter.inc">+1</button>
    <button @click="counter.reset">重設</button>
  </section>
</template>

<style scoped>
h1 {
  margin-bottom: 8px;
}
button {
  margin-right: 8px;
}
</style>
