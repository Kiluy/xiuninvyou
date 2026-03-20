<script setup lang="ts">
import { onMounted, ref } from 'vue'

const form = ref({
  modelProvider: 'deepseek',
  modelName: 'deepseek-chat',
  temperature: 0.8,
  systemPrompt: '',
  adminCommand: '/sudo-admin-777'
})

onMounted(async () => {
  const res = await fetch('http://localhost:8080/api/admin/config')
  if (res.ok) form.value = await res.json()
})

async function save() {
  await fetch('http://localhost:8080/api/admin/config', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value)
  })
  alert('保存成功')
}
</script>

<template>
  <main class="page">
    <section class="panel admin">
      <h2>隐藏后台配置</h2>
      <label>模型服务商 <input v-model="form.modelProvider" /></label>
      <label>模型名称 <input v-model="form.modelName" /></label>
      <label>Temperature <input v-model.number="form.temperature" type="number" step="0.1" min="0" max="2" /></label>
      <label>隐藏口令 <input v-model="form.adminCommand" /></label>
      <label>System Prompt <textarea v-model="form.systemPrompt" rows="6"></textarea></label>
      <button @click="save">保存配置</button>
    </section>
  </main>
</template>

<style scoped>
.admin { max-width: 720px; padding: 16px; display: grid; gap: 12px; }
label { display: grid; gap: 6px; }
input, textarea { border: 1px solid rgba(255,255,255,.3); background: rgba(0,0,0,.2); color: #fff; border-radius: 10px; padding: 10px 12px; }
button { width: 140px; height: 40px; border: none; border-radius: 10px; color: #fff; background: #7a8dff; }
</style>
