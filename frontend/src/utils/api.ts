export function userHeaders(extra: Record<string, string> = {}) {
  const token = localStorage.getItem('token') || ''
  return {
    ...extra,
    Authorization: token ? `Bearer ${token}` : ''
  }
}

export function requireLogin() {
  return !!localStorage.getItem('token')
}

export function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
}
