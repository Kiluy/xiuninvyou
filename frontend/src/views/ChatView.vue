<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '../stores/chat'

const router = useRouter()
const chat = useChatStore()
const input = ref('')
const listRef = ref<HTMLElement | null>(null)

const adminCommand = '/sudo-admin-777'

async function submit() {
  if (input.value.trim() === adminCommand) {
    input.value = ''
    router.push('/admin')
    return
  }
  const content = input.value
  input.value = ''
  await chat.send(content)
  await nextTick()
  listRef.value?.scrollTo({ top: listRef.value.scrollHeight, behavior: 'smooth' })
}
</script>

<template>
  <main class="page">
    <section class="panel chat">
      <header class="chat-header">💗 AI 虚拟伴侣</header>
      <div ref="listRef" class="msg-list">
        <div v-for="(msg, idx) in chat.messages" :key="idx" class="msg" :class="msg.role">
          {{ msg.content }}
        </div>
      </div>
      <form class="composer" @submit.prevent="submit">
        <input v-model="input" placeholder="输入消息（输入 /sudo-admin-777 进入后台）" />
        <button type="submit">发送</button>
      </form>
    </section>
  </main>
</template>

<style scoped>
.chat { display: grid; grid-template-rows: auto 1fr auto; min-height: calc(100vh - 32px); }
.chat-header { padding: 16px; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,.2); }
.msg-list { padding: 16px; overflow: auto; display: flex; flex-direction: column; gap: 10px; }
.msg { padding: 10px 12px; border-radius: 14px; max-width: min(80%, 560px); line-height: 1.45; }
.msg.user { align-self: flex-end; background: #4f6eff; }
.msg.assistant { align-self: flex-start; background: rgba(255,255,255,.2); }
.composer { display: flex; gap: 8px; padding: 12px; border-top: 1px solid rgba(255,255,255,.2); }
input { flex: 1; border: 1px solid rgba(255,255,255,.3); background: rgba(0,0,0,.2); color: #fff; border-radius: 10px; padding: 10px 12px; }
button { background: #7a8dff; border: none; color: #fff; border-radius: 10px; padding: 0 16px; }
@media (max-width: 768px) {
  .chat { min-height: calc(100vh - 16px); }
  .msg { max-width: 90%; }
}
</style>
