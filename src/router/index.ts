import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('@/views/NoteListView.vue'),
    },
    {
      path: '/note/new',
      name: 'note-new',
      component: () => import('@/views/NoteEditView.vue'),
    },
    {
      path: '/note/:id',
      name: 'note-detail',
      component: () => import('@/views/NoteDetailView.vue'),
    },
    {
      path: '/note/:id/edit',
      name: 'note-edit',
      component: () => import('@/views/NoteEditView.vue'),
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('@/views/CategoryView.vue'),
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
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

export default router
