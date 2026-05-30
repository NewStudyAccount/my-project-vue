# 07 - 自定义 Composables

## 什么是 Composable

Composable 是一个利用 Composition API 来封装和复用**有状态逻辑**的函数。

命名约定：以 `use` 开头，如 `useSearch`、`useStorage`。

## 创建 Composable

### useSearch - 搜索逻辑复用

```typescript
// composables/useSearch.ts
import { ref, computed } from 'vue'
import type { Ref } from 'vue'

export function useSearch<T>(
  source: Ref<T[]>,
  searchFields: (keyof T)[]
) {
  const searchQuery = ref('')

  const filteredResults = computed(() => {
    const query = searchQuery.value.toLowerCase().trim()
    if (!query) return source.value

    return source.value.filter((item) =>
      searchFields.some((field) => {
        const value = item[field]
        if (typeof value === 'string') {
          return value.toLowerCase().includes(query)
        }
        return false
      })
    )
  })

  return {
    searchQuery,
    filteredResults,
  }
}
```

### useStorage - localStorage 响应式封装

```typescript
// composables/useStorage.ts
import { ref, watch } from 'vue'

export function useStorage<T>(key: string, defaultValue: T) {
  const stored = localStorage.getItem(key)
  const data = ref(stored ? JSON.parse(stored) : defaultValue)

  watch(
    data,
    (newValue) => {
      localStorage.setItem(key, JSON.stringify(newValue))
    },
    { deep: true }
  )

  return data
}
```

## 在组件中使用

```vue
<script setup>
import { useNotesStore } from '@/stores/notes'
import { useSearch } from '@/composables/useSearch'

const notesStore = useNotesStore()

// 使用 composable
const { searchQuery, filteredResults } = useSearch(
  computed(() => notesStore.notes),
  ['title', 'content']
)
</script>

<template>
  <input v-model="searchQuery" placeholder="搜索..." />
  <div v-for="note in filteredResults" :key="note.id">
    {{ note.title }}
  </div>
</template>
```

## 优势

1. **逻辑复用**: 多个组件共享同一逻辑
2. **代码组织**: 相关逻辑集中在一起
3. **TypeScript 支持**: 完整的类型推导
4. **可测试**: 狯立的函数易于测试

## Vue 2 对比

| Mixins (Vue 2) | Composables (Vue 3) |
|----------------|---------------------|
| 命名冲突风险 | 明确的返回值 |
| 隐式依赖 | 显式参数传递 |
| 难以追踪来源 | 清晰的函数调用链 |
| 不支持类型推导 | 完整 TypeScript 支持 |

## 最佳实践

1. **命名以 `use` 开头**: `useSearch`、`useStorage`
2. **返回 ref**: 保持响应性
3. **接受 ref 参数**: 使用 `toRef` 或 `computed` 包装
4. **单一职责**: 每个 composable 只做一件事
5. **保持简单**: 逻辑复杂时拆分为多个 composable
