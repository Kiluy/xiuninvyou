import { defineStore } from 'pinia'
import type { ChatMessage, ChatSession } from '../types/chat'

const API = 'http://localhost:8080/api'

export const useChatStore = defineStore('chat', {
  state: () => ({
    session: null as ChatSession | null,
    messages: [] as ChatMessage[],
    loading: false
  }),
  actions: {
    async ensureSession() {
      if (this.session) return
      const res = await fetch(`${API}/chat/sessions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: '默认会话' })
      })
      this.session = await res.json()
    },
    async send(content: string) {
      if (!content.trim()) return
      await this.ensureSession()
      this.messages.push({ role: 'user', content })
      this.messages.push({ role: 'assistant', content: '' })
      const assistant = this.messages[this.messages.length - 1]
      this.loading = true

      const res = await fetch(`${API}/chat/stream`, {
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
    }
  }
})
