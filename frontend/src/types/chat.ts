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

export interface GeneratedAsset {
  id?: number
  sessionId: number
  type: 'image'
  assetUrl: string
  prompt?: string
}
