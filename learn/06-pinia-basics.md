# 06 - Pinia 状态管理

## 创建 Store

使用 `defineStore` 创建 store：

```typescript
// stores/notes.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNotesStore = defineStore('notes', () => {
  // State
  const notes = ref([])

  // Getters
  const notesCount = computed(() => notes.value.length)

  // Actions
  function addNote(note) {
    notes.value.push({
      ...note,
      id: Date.now().toString(),
      createdAt: Date.now(),
    })
  }

  function deleteNote(id) {
    notes.value = notes.value.filter(n => n.id !== id)
  }

  return {
    notes,
    notesCount,
    addNote,
    deleteNote,
  }
})
```

## 在组件中使用

```vue
<script setup>
import { useNotesStore } from '@/stores/notes'

const notesStore = useNotesStore()

// 访问 state
console.log(notesStore.notes)

// 调用 action
notesStore.addNote({ title: '新笔记', content: '内容' })

// 访问 getter
console.log(notesStore.notesCount)
</script>
```

## Store 间调用

```typescript
import { useNotesStore } from './notes'

export const useCategoryStore = defineStore('categories', () => {
  const categories = ref([])

  function deleteCategory(id) {
    // 调用其他 store
    const notesStore = useNotesStore()
    notesStore.notes
      .filter(n => n.categoryId === id)
      .forEach(n => notesStore.updateNote(n.id, { categoryId: null }))

    categories.value = categories.value.filter(c => c.id !== id)
  }

  return { categories, deleteCategory }
})
```

## localStorage 持久化

手动实现持久化：

```typescript
const STORAGE_KEY = 'my-app-notes'

function loadNotes() {
  const stored = localStorage.getItem(STORAGE_KEY)
  return stored ? JSON.parse(stored) : []
}

export const useNotesStore = defineStore('notes', () => {
  const notes = ref(loadNotes())

  function persist() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notes.value))
  }

  function addNote(note) {
    // ... 添加笔记
    persist() // 保存到 localStorage
  }

  return { notes, addNote }
})
```

## Vue 2 对比（Vuex vs Pinia）

| Vuex (Vue 2) | Pinia (Vue 3) |
|--------------|---------------|
| `state: () => ({})` | `const state = ref()` |
| `getters: {}` | `const getter = computed()` |
| `mutations: {}` | 直接在 action 中修改 state |
| `actions: {}` | `function action() {}` |
| `this.$store.state.xxx` | `store.xxx` |
| `this.$store.commit('mutation')` | `store.action()` |
| `this.$store.dispatch('action')` | `store.action()` |

## 优势

1. **更简洁**: 没有 mutations，直接修改 state
2. **TypeScript 支持更好**: 完整的类型推导
3. **模块化**: 每个 store 独立，不需要 modules 嵌套
4. **组合式 API 风格**: 与 Composition API 完美配合

---

## Pinia 数据存储位置

**默认存在内存中（浏览器 JS 运行时内存）**，不是 localStorage。

- 页面刷新 → 数据还在（SPA 没有真正刷新页面）
- 用户手动刷新浏览器（F5） → 数据丢失

### 三种存储方式对比

| 方式 | 存储位置 | 刷新后 | 关闭浏览器后 |
|------|---------|--------|-------------|
| Pinia（默认） | 内存 | ❌ 丢失 | ❌ 丢失 |
| localStorage | 浏览器本地 | ✅ 保留 | ✅ 保留 |
| sessionStorage | 浏览器本地 | ❌ 丢失 | ❌ 丢失 |

---

## 自动持久化

使用插件 **pinia-plugin-persistedstate**，一行配置搞定，不用每次手动存。

### 安装

```bash
npm install pinia-plugin-persistedstate
```

### 注册插件

```typescript
// main.ts
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
```

### Store 中开启

```typescript
export const useNotesStore = defineStore('notes', () => {
  const notes = ref([])
  const notesCount = computed(() => notes.value.length)

  function addNote(note) {
    notes.value.push(note)
    // 不用手动 persist() 了
  }

  return { notes, notesCount, addNote }
}, {
  persist: true  // ← 就这一行，自动持久化
})
```

### 原理

插件会在 store 数据变化时**自动**写入 `localStorage`，启动时**自动**从 `localStorage` 恢复。

### 进阶配置

```typescript
export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const theme = ref('light')
  return { token, theme }
}, {
  persist: {
    storage: sessionStorage,  // 改用 sessionStorage
    paths: ['token'],          // 只持久化 token，不存 theme
  }
})
```

---

## 何时需要持久化

**判断标准：刷新页面后，这个数据还需要吗？**

### 需要持久化

| 数据 | 原因 |
|------|------|
| 登录 token | 刷新后不能要求重新登录 |
| 用户主题/语言偏好 | 设置一次要一直生效 |
| 购物车 | 关掉页面再打开还在 |
| 阅读进度 | 看到哪了要记住 |

### 不需要持久化

| 数据 | 原因 |
|------|------|
| 页面列表数据 | 刷新后重新请求接口，数据最新 |
| 弹窗开关状态 | 临时 UI 状态，没意义 |
| 表单临时输入 | 用户刷新可能就是想清空 |
| 加载状态 (loading) | 刷新后重新加载 |

---

## localStorage vs sessionStorage

| | localStorage | sessionStorage |
|--|-------------|---------------|
| 生命周期 | **永久**，手动删除才消失 | **关闭标签页**就消失 |
| 典型场景 | token、主题偏好 | 一次性敏感数据、临时会话状态 |

### 选择依据

- **localStorage：** 跨标签页共享、长期有效（token、主题、语言、购物车）
- **sessionStorage：** 只在当前页面会话有效（表单草稿、临时筛选条件、敏感信息）

---

## 动态路由数据的持久化

后端返回的路由数据**通常不需要持久化**。

### 为什么不存路由数据

| 原因 | 说明 |
|------|------|
| 权限变更 | 后端改了权限，本地存的还是旧的 |
| 菜单更新 | 新增/删除了页面，本地数据过时 |
| 安全风险 | 存在本地容易被篡改 |

### 正确做法

页面刷新 → 重新请求后端获取路由 → 动态添加路由。

使用 `routesLoaded` 标记避免 SPA 内跳转时重复请求路由接口：

```typescript
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  if (userStore.token) {
    if (!userStore.routesLoaded) {
      // 有 token 但还没加载路由 → 请求后端
      const routes = await fetchUserRoutes()
      routes.forEach(route => {
        router.addRoute(route)  // 动态添加路由
      })
      userStore.routesLoaded = true  // 标记已加载
      next({ ...to, replace: true })  // 重新进入目标页
    } else {
      next()  // 已加载，直接放行
    }
  } else {
    next('/login')
  }
})
```

### routesLoaded 的生命周期

| 场景 | routesLoaded 变为 false |
|------|------------------------|
| 页面刷新（未持久化） | 自动变 false（内存重置） |
| 退出登录 | 手动设为 false |

**关键：不要持久化 `routesLoaded`，让刷新自然重置，保证每次刷新都重新请求最新路由。**

### routesLoaded 的作用

| 方案 | 页面跳转 | 刷新页面 |
|------|---------|---------|
| 不要标记，每次都请求 | ❌ 浪费请求 | ✅ 正确 |
| 有 `routesLoaded` 标记 | ✅ 不重复请求 | ✅ 正确 |

**`routesLoaded` 不是为了持久化，是为了避免 SPA 内跳转时重复请求路由接口。**
