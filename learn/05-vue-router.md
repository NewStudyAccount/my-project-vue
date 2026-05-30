# 05 - Vue Router 使用

## 路由配置

在 `router/index.ts` 中定义路由：

```typescript
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
    },
    {
      path: '/note/:id',
      name: 'note-detail',
      component: () => import('../views/NoteDetailView.vue'),
    },
  ],
})

export default router
```

## 动态路由参数

```vue
<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
const noteId = route.params.id
</script>
```

## 编程式导航

```typescript
import { useRouter } from 'vue-router'

const router = useRouter()

// 字符串路径
router.push('/notes')

// 对象
router.push({ name: 'note-detail', params: { id: '123' } })

// 替换当前路由（不产生历史记录）
router.replace('/login')

// 返回
router.back()
```

## 嵌套路由

```typescript
const routes = [
  {
    path: '/categories',
    component: CategoryView,
    children: [
      {
        path: ':id/notes',
        component: NoteListView,
      },
    ],
  },
]
```

父组件使用 `<router-view>` 渲染子路由：

```vue
<template>
  <div>
    <h1>分类管理</h1>
    <!-- 子路由渲染在这里 -->
    <router-view />
  </div>
</template>
```

## 路由守卫

### 组件内守卫

```vue
<script setup>
import { onBeforeRouteLeave } from 'vue-router'

const isDirty = ref(false)

onBeforeRouteLeave((to, from, next) => {
  if (isDirty.value) {
    // 弹出确认对话框
    if (confirm('未保存的更改将丢失')) {
      next()
    } else {
      next(false)
    }
  } else {
    next()
  }
})
</script>
```

## 过渡动画

```vue
<template>
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
```

## Vue 2 对比

| Vue 2 | Vue 3 |
|-------|-------|
| `this.$router.push()` | `router.push()` |
| `this.$route.params.id` | `route.params.id` |
| `beforeRouteLeave()` | `onBeforeRouteLeave()` |
| `<router-link>` | `<router-link>`（不变） |
