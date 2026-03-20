function emitError(message: string) {
  window.dispatchEvent(new CustomEvent('app-error', { detail: { message } }))
}

export function userHeaders(extra: Record<string, string> = {}) {
  const token = localStorage.getItem('token') || ''
  return {
    ...extra,
    Authorization: token ? `Bearer ${token}` : ''
  }
}

export async function apiFetch(url: string, init: RequestInit = {}) {
  const send = async () => fetch(url, {
    ...init,
    headers: userHeaders((init.headers as Record<string, string>) || {})
  })

  let res = await send()
  if (res.status === 401) {
    const oldToken = localStorage.getItem('token') || ''
    if (oldToken) {
      const refreshRes = await fetch('http://localhost:8080/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: oldToken })
      })

      if (refreshRes.ok) {
        const data = await refreshRes.json()
        localStorage.setItem('token', data.token)
        res = await send()
      } else {
        logout()
        window.location.href = '/login'
        return res
      }
    }
  }

  if (!res.ok) {
    try {
      const err = await res.clone().json()
      emitError(err.message || `请求失败(${res.status})`)
    } catch {
      emitError(`请求失败(${res.status})`)
    }
  }

  return res
}

export function requireLogin() {
  return !!localStorage.getItem('token')
}

export function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
}
