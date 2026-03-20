<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from 'vue'

const message = ref('')
let timer: number | null = null

function onError(event: Event) {
  const e = event as CustomEvent<{ message: string }>
  message.value = e.detail?.message || '请求失败'
  if (timer) window.clearTimeout(timer)
  timer = window.setTimeout(() => {
    message.value = ''
  }, 3000)
}

onMounted(() => window.addEventListener('app-error', onError as EventListener))
onBeforeUnmount(() => window.removeEventListener('app-error', onError as EventListener))
</script>

<template>
  <div v-if="message" class="toast">{{ message }}</div>
  <router-view />
</template>

<style scoped>
.toast {
  position: fixed;
  top: 12px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(201, 74, 102, 0.95);
  color: #fff;
  padding: 10px 14px;
  border-radius: 10px;
  z-index: 9999;
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.25);
}
</style>
