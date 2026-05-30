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
