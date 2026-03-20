<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { apiFetch, logout } from '../utils/api'
import { useRouter } from 'vue-router'

type Profile = {
  id?: number
  displayName: string
  nickname: string
  systemPrompt: string
  modelProvider: string
  modelName: string
  temperature: number
}

const router = useRouter()

function doLogout(){
  logout()
  router.push('/login')
}

const form = ref({
  modelProvider: 'deepseek',
  modelName: 'deepseek-chat',
  temperature: 0.8,
  systemPrompt: '',
  apiBaseUrl: 'https://api.openai.com',
  apiKey: '',
  adminCommand: '/sudo-admin-777'
})

const profiles = ref<Profile[]>([])
const draftProfile = ref<Profile>({
  displayName: '默认女友',
  nickname: '小优',
  systemPrompt: '温柔陪伴',
  modelProvider: 'deepseek',
  modelName: 'deepseek-chat',
  temperature: 0.8
})

const memoryItems = ref<{ id?: number; category: string; content: string }[]>([])
const draftMemory = ref({ category: '喜好', content: '' })

const audits = ref<Array<{ id:number; method:string; path:string; status:number; userId:number|null; costMs:number; createdAt:string }>>([])

async function loadAudit(){
  const res = await apiFetch('http://localhost:8080/api/audit?limit=50')
  audits.value = await res.json()
}

async function loadAll() {
  const [cfg, ps, ms] = await Promise.all([
    apiFetch('http://localhost:8080/api/admin/config').then(r => r.json()),
    apiFetch('http://localhost:8080/api/profiles').then(r => r.json()),
    apiFetch('http://localhost:8080/api/memory').then(r => r.json())
  ])
  form.value = cfg
  profiles.value = ps
  memoryItems.value = ms
}

onMounted(async () => {
  await loadAll()
  await loadAudit()
})

async function saveConfig() {
  await apiFetch('http://localhost:8080/api/admin/config', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value)
  })
  alert('系统配置已保存')
}

async function addProfile() {
  const res = await apiFetch('http://localhost:8080/api/profiles', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(draftProfile.value)
  })
  profiles.value.push(await res.json())
}

async function removeProfile(id?: number) {
  if (!id) return
  await apiFetch(`http://localhost:8080/api/profiles/${id}`, { method: 'DELETE' })
  profiles.value = profiles.value.filter(p => p.id !== id)
}

async function addMemory() {
  const res = await apiFetch('http://localhost:8080/api/memory', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(draftMemory.value)
  })
  memoryItems.value.push(await res.json())
  draftMemory.value.content = ''
}
</script>

<template>
  <main class="page">
    <section class="panel admin">
      <h2>隐藏后台配置 <button class="danger" @click="doLogout">退出</button></h2>

      <h3>系统配置</h3>
      <label>模型服务商 <input v-model="form.modelProvider" /></label>
      <label>模型名称 <input v-model="form.modelName" /></label>
      <label>Temperature <input v-model.number="form.temperature" type="number" step="0.1" min="0" max="2" /></label>
      <label>API Base URL <input v-model="form.apiBaseUrl" /></label>
      <label>API Key <input v-model="form.apiKey" type="password" /></label>
      <label>隐藏口令 <input v-model="form.adminCommand" /></label>
      <label>System Prompt <textarea v-model="form.systemPrompt" rows="4"></textarea></label>
      <button @click="saveConfig">保存系统配置</button>

      <h3>AI 角色管理</h3>
      <label>显示名 <input v-model="draftProfile.displayName" /></label>
      <label>昵称 <input v-model="draftProfile.nickname" /></label>
      <label>角色 Prompt <textarea v-model="draftProfile.systemPrompt" rows="3"></textarea></label>
      <button @click="addProfile">新增角色</button>
      <ul>
        <li v-for="p in profiles" :key="p.id">
          {{ p.displayName }}（{{ p.nickname }}）
          <button class="danger" @click="removeProfile(p.id)">删除</button>
        </li>
      </ul>

      <h3>记忆金库</h3>
      <label>分类 <input v-model="draftMemory.category" /></label>
      <label>内容 <textarea v-model="draftMemory.content" rows="2"></textarea></label>
      <button @click="addMemory">新增记忆</button>
      <ul>
        <li v-for="m in memoryItems" :key="m.id">【{{ m.category }}】{{ m.content }}</li>
      </ul>

      <h3>审计日志（最近 50 条）</h3>
      <button @click="loadAudit">刷新审计</button>
      <div class="audit-list">
        <div class="audit-item" v-for="a in audits" :key="a.id">
          [{{ a.status }}] {{ a.method }} {{ a.path }} · user={{ a.userId ?? 'guest' }} · {{ a.costMs }}ms
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admin { max-width: 820px; padding: 16px; display: grid; gap: 10px; }
label { display: grid; gap: 6px; }
input, textarea { border: 1px solid rgba(255,255,255,.3); background: rgba(0,0,0,.2); color: #fff; border-radius: 10px; padding: 10px 12px; }
button { width: fit-content; border: none; border-radius: 10px; color: #fff; background: #7a8dff; padding: 8px 14px; }
.danger { margin-left: 8px; background: #c94a66; }
ul { list-style: none; padding: 0; margin: 0; display: grid; gap: 8px; }
li { background: rgba(255,255,255,.08); padding: 8px 10px; border-radius: 8px; }
.audit-list{display:grid; gap:6px; max-height:220px; overflow:auto;}
.audit-item{font-size:12px; background:rgba(255,255,255,.06); padding:6px 8px; border-radius:6px;}
</style>
