# 04 - 计算属性与侦听器

## computed 计算属性

`computed` 用于创建依赖其他响应式数据的计算值：

```typescript
import { ref, computed } from 'vue'

const firstName = ref('张')
const lastName = ref('三')

// 只读计算属性
const fullName = computed(() => firstName.value + lastName.value)

console.log(fullName.value) // '张三'
```

### 特点

- **缓存**: 只有依赖变化时才重新计算
- **只读**: 默认不能直接修改
- **响应式**: 依赖变化时自动更新

## watch 侦听器

`watch` 用于监听响应式数据变化并执行副作用：

```typescript
import { ref, watch } from 'vue'

const count = ref(0)

// 监听单个 ref
watch(count, (newValue, oldValue) => {
  console.log(`count 从 ${oldValue} 变为 ${newValue}`)
})

// 监听多个数据源
watch([firstName, lastName], ([newFirst, newLast]) => {
  console.log(`名字变为: ${newFirst}${newLast}`)
})
```

### watchEffect

`watchEffect` 自动收集依赖并执行：

```typescript
import { ref, watchEffect } from 'vue'

const count = ref(0)

watchEffect(() => {
  console.log(`count 的值是: ${count.value}`)
  // 自动追踪 count 作为依赖
})
```

## 实际应用

### 搜索过滤

```typescript
const searchQuery = ref('')
const notes = ref([...])

const filteredNotes = computed(() => {
  if (!searchQuery.value) return notes.value
  return notes.value.filter(note =>
    note.title.includes(searchQuery.value)
  )
})
```

### 数据持久化

```typescript
const notes = ref([])

watch(notes, (newNotes) => {
  localStorage.setItem('notes', JSON.stringify(newNotes))
}, { deep: true })
```

## Vue 2 对比

| Vue 2 (Options API) | Vue 3 (Composition API) |
|---------------------|------------------------|
| `computed: { fullName() { ... } }` | `const fullName = computed(() => ...)` |
| `watch: { count(newVal, oldVal) { ... } }` | `watch(count, (newVal, oldVal) => ...)` |
| `this.$watch('count', callback)` | `watch(count, callback)` |
