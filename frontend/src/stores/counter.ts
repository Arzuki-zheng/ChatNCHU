import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  const count = ref(0)
  const double = computed(() => count.value * 2)
  function inc() {
    count.value++
  }
  function reset() {
    count.value = 0
  }
  return { count, double, inc, reset }
})
