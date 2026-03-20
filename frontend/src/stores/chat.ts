import { defineStore } from 'pinia'
import type { ChatMessage, ChatSession, GeneratedAsset } from '../types/chat'
import { apiFetch } from '../utils/api'

const API = 'http://localhost:8080/api'

export const useChatStore = defineStore('chat', {
  state: () => ({
    session: null as ChatSession | null,
    sessions: [] as ChatSession[],
    messages: [] as ChatMessage[],
    assets: [] as GeneratedAsset[],
    loading: false
  }),
  actions: {
    async loadSessions() {
      const res = await apiFetch(`${API}/sessions`)
      this.sessions = await res.json()
      if (!this.session && this.sessions.length > 0) {
        this.session = this.sessions[0]
        await this.loadMessages(this.session.id)
      }
    },
    async ensureSession() {
      if (this.session) return
      const res = await apiFetch(`${API}/chat/sessions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: `会话 ${new Date().toLocaleTimeString()}` })
      })
      this.session = await res.json()
      this.sessions.unshift(this.session)
    },
    async loadMessages(sessionId: number) {
      const [msgRes, assetRes] = await Promise.all([
        apiFetch(`${API}/chat/sessions/${sessionId}/messages`),
        apiFetch(`${API}/assets/${sessionId}`)
      ])
      this.messages = await msgRes.json()
      this.assets = await assetRes.json()
    },
    async selectSession(s: ChatSession) {
      this.session = s
      await this.loadMessages(s.id)
    },
    async send(content: string) {
      if (!content.trim()) return
      await this.ensureSession()
      this.messages.push({ role: 'user', content })
      this.messages.push({ role: 'assistant', content: '' })
      const assistant = this.messages[this.messages.length - 1]
      this.loading = true

      const res = await apiFetch(`${API}/chat/stream`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: this.session?.id, content })
      })

      const reader = res.body?.getReader()
      if (!reader) return
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })
        const chunks = buffer.split('\n\n')
        buffer = chunks.pop() || ''
        for (const chunk of chunks) {
          const line = chunk.split('\n').find((l) => l.startsWith('data: '))
          if (!line) continue
          const payload = JSON.parse(line.replace('data: ', ''))
          if (payload.text) assistant.content += payload.text
        }
      }
      this.loading = false
      await this.loadSessions()
      if (this.session) await this.loadMessages(this.session.id)
    }
  }
})
