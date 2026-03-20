export function userHeaders(extra: Record<string, string> = {}) {
  const userId = localStorage.getItem('userId') || ''
  return {
    ...extra,
    'X-User-Id': userId
  }
}

export function requireLogin() {
  return !!localStorage.getItem('userId')
}
