export interface ChatSession {
  id: number
  title: string
}

export interface ChatMessage {
  id?: number
  role: 'user' | 'assistant' | 'system'
  type?: string
  content: string
}
