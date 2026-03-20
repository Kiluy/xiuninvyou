<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '../stores/chat'
import { apiFetch, logout } from '../utils/api'

const router = useRouter()

function doLogout() {
  logout()
  router.push('/login')
}
const chat = useChatStore()
const input = ref('')
const listRef = ref<HTMLElement | null>(null)
const recording = ref(false)
let mediaRecorder: MediaRecorder | null = null
let chunks: Blob[] = []

const adminCommand = '/sudo-admin-777'

onMounted(async () => {
  await chat.loadSessions()
})

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

function speak(text: string) {
  const u = new SpeechSynthesisUtterance(text)
  u.lang = 'zh-CN'
  window.speechSynthesis.speak(u)
}

async function toggleRecord() {
  if (!recording.value) {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder = new MediaRecorder(stream)
    chunks = []
    mediaRecorder.ondataavailable = (e) => chunks.push(e.data)
    mediaRecorder.onstop = async () => {
      const blob = new Blob(chunks, { type: 'audio/webm' })
      const fd = new FormData()
      fd.append('file', blob, 'voice.webm')
      const res = await apiFetch('http://localhost:8080/api/voice/asr', { method: 'POST', body: fd })
      const json = await res.json()
      input.value = json.text || ''
    }
    mediaRecorder.start()
    recording.value = true
  } else {
    mediaRecorder?.stop()
    recording.value = false
  }
}
</script>

<template>
  <main class="page">
    <section class="panel chat-shell">
      <aside class="session-list">
        <h4>会话</h4>
        <button @click="chat.ensureSession">+ 新会话</button>
        <div v-for="s in chat.sessions" :key="s.id" class="session-item" :class="{ active: chat.session?.id === s.id }" @click="chat.selectSession(s)">
          {{ s.title }}
        </div>
      </aside>

      <section class="chat">
        <header class="chat-header">💗 AI 虚拟伴侣 <button class="logout" @click="doLogout">退出</button></header>
        <div ref="listRef" class="msg-list">
          <div v-for="(asset, idx) in chat.assets" :key="`asset-${idx}`" class="asset-card">
            <img :src="asset.assetUrl" alt="scene" />
            <small>{{ asset.prompt }}</small>
          </div>

          <div v-for="(msg, idx) in chat.messages" :key="idx" class="msg" :class="msg.role">
            <span>{{ msg.content }}</span>
            <button v-if="msg.role === 'assistant' && msg.content" class="tts" @click="speak(msg.content)">🔊</button>
          </div>
        </div>
        <form class="composer" @submit.prevent="submit">
          <input v-model="input" placeholder="输入消息（输入 /sudo-admin-777 进入后台）" />
          <button type="button" @click="toggleRecord">{{ recording ? '⏹' : '🎤' }}</button>
          <button type="submit">发送</button>
        </form>
      </section>
    </section>
  </main>
</template>

<style scoped>
.chat-shell { display: grid; grid-template-columns: 240px 1fr; min-height: calc(100vh - 32px); }
.session-list { border-right: 1px solid rgba(255,255,255,.2); padding: 12px; display: grid; gap: 8px; align-content: start; }
.session-item { padding: 8px; border-radius: 8px; cursor: pointer; background: rgba(255,255,255,.08); }
.session-item.active { background: rgba(122,141,255,.55); }
.chat { display: grid; grid-template-rows: auto 1fr auto; }
.chat-header { padding: 16px; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,.2); display:flex; justify-content:space-between; align-items:center; }
.logout{background:#c94a66;}
.msg-list { padding: 16px; overflow: auto; display: flex; flex-direction: column; gap: 10px; }
.asset-card { background: rgba(255,255,255,.08); border-radius: 12px; padding: 8px; }
.asset-card img { width: 100%; border-radius: 8px; max-height: 220px; object-fit: cover; }
.msg { padding: 10px 12px; border-radius: 14px; max-width: min(90%, 640px); line-height: 1.45; display: flex; align-items: center; gap: 8px; }
.msg.user { align-self: flex-end; background: #4f6eff; }
.msg.assistant { align-self: flex-start; background: rgba(255,255,255,.2); }
.tts { background: transparent; border: none; color: #fff; cursor: pointer; }
.composer { display: flex; gap: 8px; padding: 12px; border-top: 1px solid rgba(255,255,255,.2); }
input { flex: 1; border: 1px solid rgba(255,255,255,.3); background: rgba(0,0,0,.2); color: #fff; border-radius: 10px; padding: 10px 12px; }
button { background: #7a8dff; border: none; color: #fff; border-radius: 10px; padding: 8px 12px; }
@media (max-width: 768px) {
  .chat-shell { grid-template-columns: 1fr; }
  .session-list { border-right: none; border-bottom: 1px solid rgba(255,255,255,.2); }
  .msg { max-width: 95%; }
}
</style>
