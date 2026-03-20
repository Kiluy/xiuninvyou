import { createRouter, createWebHistory } from 'vue-router'
import ChatView from '../views/ChatView.vue'
import AdminView from '../views/AdminView.vue'
import LoginView from '../views/LoginView.vue'
import { requireLogin } from '../utils/api'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/chat' },
    { path: '/login', component: LoginView },
    { path: '/chat', component: ChatView },
    { path: '/admin', component: AdminView }
  ]
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !requireLogin()) return '/login'
})

export default router
