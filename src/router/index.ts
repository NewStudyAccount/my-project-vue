import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('@/views/NoteListView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/note/new',
      name: 'note-new',
      component: () => import('@/views/NoteEditView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/note/:id',
      name: 'note-detail',
      component: () => import('@/views/NoteDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/note/:id/edit',
      name: 'note-edit',
      component: () => import('@/views/NoteEditView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('@/views/CategoryView.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: ':id/notes',
          name: 'category-notes',
          component: () => import('@/views/NoteListView.vue'),
        },
      ],
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('@/views/SettingsView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { requiresAuth: false },
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // 未登录 → 跳登录页
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && authStore.isLoggedIn) {
    // 已登录 → 不用再去登录/注册页
    next('/')
  } else {
    next()
  }
})

export default router
