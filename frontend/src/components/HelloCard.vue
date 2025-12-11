<script setup lang="ts">
defineProps<{ title: string; subtitle?: string }>()
</script>

<template>
  <article class="card">
    <h2>{{ title }}</h2>
    <p v-if="subtitle">{{ subtitle }}</p>
    <slot />
  </article>
</template>

<style scoped>
.card {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}
h2 {
  margin: 0 0 8px;
  font-size: 18px;
}
</style>
