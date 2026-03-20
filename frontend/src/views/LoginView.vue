<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')

async function submit(mode: 'login' | 'register') {
  const res = await fetch(`http://localhost:8080/api/auth/${mode}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: username.value, password: password.value })
  })
  if (!res.ok) {
    alert('登录失败，请检查账号密码')
    return
  }
  const json = await res.json()
  localStorage.setItem('token', json.token)
  localStorage.setItem('username', json.username)
  router.push('/chat')
}
</script>

<template>
  <main class="page">
    <section class="panel login">
      <h2>登录 / 注册</h2>
      <input v-model="username" placeholder="用户名" />
      <input v-model="password" type="password" placeholder="密码" />
      <div class="row">
        <button @click="submit('login')">登录</button>
        <button @click="submit('register')">注册</button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.login { max-width: 420px; margin: 40px auto; padding: 20px; display: grid; gap: 10px; }
input { border: 1px solid rgba(255,255,255,.3); background: rgba(0,0,0,.2); color: #fff; border-radius: 10px; padding: 10px 12px; }
.row { display: flex; gap: 8px; }
button { border: none; border-radius: 10px; color: #fff; background: #7a8dff; padding: 8px 12px; }
</style>
