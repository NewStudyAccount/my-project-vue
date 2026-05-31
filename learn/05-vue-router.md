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

---

## 路由守卫详解

**作用：** 在路由跳转的前后"拦截"，决定是否放行或做额外操作。

### 典型使用场景

#### 1. 权限控制（最常见）

```typescript
router.beforeEach((to, from, next) => {
  const isLogin = !!localStorage.getItem('token')

  if (to.meta.requiresAuth && !isLogin) {
    next('/login')  // 未登录 → 跳登录页
  } else {
    next()          // 放行
  }
})
```

#### 2. 未保存提示

```typescript
// 用户编辑了表单但没保存，离开时弹确认框
onBeforeRouteLeave((to, from, next) => {
  if (hasUnsavedChanges.value) {
    confirm('确定离开？未保存的内容将丢失') ? next() : next(false)
  } else {
    next()
  }
})
```

#### 3. 页面标题

```typescript
router.afterEach((to) => {
  document.title = to.meta.title || '默认标题'
})
```

#### 4. 数据预加载

```typescript
beforeRouteEnter(to, from, next) {
  // 进入页面前先加载数据
  fetchNote(to.params.id).then(data => {
    next(vm => vm.note = data)
  })
}
```

### 守卫类型

| 类型 | 作用范围 |
|------|---------|
| `beforeEach` | 全局，**每次**跳转都触发 |
| `beforeEnter` | 单个路由配置上 |
| `onBeforeRouteLeave` | 组件内，离开当前页时 |
| `onBeforeRouteUpdate` | 组件内，路由参数变化时 |

### 核心概念

就像**门卫**：
- `next()` → 放行
- `next('/login')` → 拦截，改道
- `next(false)` → 拦截，取消跳转
