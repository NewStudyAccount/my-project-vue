# 03 - 组件通信（Props/Emit）

## 为什么需要组件通信

Vue 的组件是**树形结构**，数据默认只能在自己的 `<script>` 里用：

```
App.vue
├── NoteCard.vue    ← 想用 App 里的笔记数据？拿不到
├── SearchBar.vue   ← 想通知 App 用户搜了什么？没办法
└── NoteForm.vue    ← 想告诉 App 表单提交了？没办法
```

所以需要一种机制让父子组件之间传数据。

## 数据流动方向

Vue 的数据是**单向流动**的：

```
        props（往下传）
父组件 ─────────────→ 子组件
       ←─────────────
        emit（往上传）
```

- **props**: 父组件把数据"传下去"给子组件
- **emit**: 子组件把事件"传上去"通知父组件

子组件**不能直接修改**父组件的数据，只能通知父组件"你该改了"。

---

## Props — 父传子

### 基本用法

父组件通过**标签属性**传数据：

```vue
<!-- 父组件 -->
<template>
  <NoteCard :note="noteData" />
  <!--        ↑ 属性名    ↑ 值 -->
</template>
```

子组件用 `defineProps` 接收：

```vue
<!-- 子组件 NoteCard.vue -->
<script setup lang="ts">
const props = defineProps<{
  note: {
    id: string
    title: string
    content: string
  }
}>()

// 用 props.note 访问
console.log(props.note.title)
</script>

<template>
  <!-- 模板里直接用，不需要 props. 前缀 -->
  <h3>{{ note.title }}</h3>
  <p>{{ note.content }}</p>
</template>
```

### 可选 props 和默认值

```typescript
// title 必传，count 可选
const props = defineProps<{
  title: string
  count?: number   // ? 表示可选
}>()

// 带默认值的写法
const props = withDefaults(
  defineProps<{
    title: string
    count?: number
  }>(),
  {
    count: 0,  // 不传时默认为 0
  }
)
```

### Props 的规则

```vue
<script setup lang="ts">
const props = defineProps<{ count: number }>()
</script>

<template>
  <!-- ✅ 读取 props -->
  <p>{{ count }}</p>

  <!-- ❌ 不能修改 props -->
  <!-- props.count = 999  // 警告！单向数据流 -->
</template>
```

**为什么不能改？** 因为 props 是父组件的数据，如果子组件能随便改，父组件不知道，数据就乱了。

---

## Emit — 子传父

### 基本用法

子组件想通知父组件"发生了某事"，用 `emit` 触发事件：

```vue
<!-- 子组件 -->
<script setup lang="ts">
const emit = defineEmits<{
  delete: [id: string]    // 事件名: [参数类型]
}>()

function handleClick() {
  emit('delete', '123')  // 触发 delete 事件，传参 '123'
}
</script>

<template>
  <button @click="handleClick">删除</button>
</template>
```

父组件监听事件：

```vue
<!-- 父组件 -->
<template>
  <NoteCard @delete="handleDelete" />
  <!--       ↑ 监听 delete 事件 -->
</template>

<script setup>
function handleDelete(id: string) {
  // 子组件 emit('delete', '123') 时，这里收到 id = '123'
  notesStore.deleteNote(id)
}
</script>
```

### 完整流程图解

```
子组件 NoteCard                    父组件 NoteListView
─────────────────                 ─────────────────────
用户点击删除按钮
      ↓
emit('delete', '123')
      ↓                            @delete="handleDelete"
      ──────── 事件传递 ─────────→  handleDelete('123')
                                         ↓
                                   notesStore.deleteNote('123')
                                         ↓
                                   notes 数组变了
                                         ↓
                                   页面自动更新
```

---

## 实际应用：本项目的例子

### 例子 1：NoteCard 组件

```vue
<!-- src/components/NoteCard.vue -->
<script setup lang="ts">
import { useCategoryStore } from '../stores/categories'

// 接收笔记数据
const props = defineProps<{
  note: {
    id: string
    title: string
    content: string
    categoryId: string | null
    createdAt: number
  }
}>()

// 声明可以触发的事件
const emit = defineEmits<{
  click: [id: string]  // 点击时把笔记 id 告诉父组件
}>()

const categoryStore = useCategoryStore()
</script>

<template>
  <el-card shadow="hover" @click="emit('click', note.id)">
    <template #header>
      <span>{{ note.title }}</span>
      <el-tag>{{ categoryStore.getCategoryName(note.categoryId) }}</el-tag>
    </template>
    <p>{{ note.content }}</p>
  </el-card>
</template>
```

父组件使用：

```vue
<!-- src/views/NoteListView.vue -->
<script setup>
import NoteCard from '../components/NoteCard.vue'

function goToNote(id: string) {
  router.push(`/note/${id}`)
}
</script>

<template>
  <NoteCard
    v-for="note in notes"
    :key="note.id"
    :note="note"
    @click="goToNote"
  />
  <!--
    :note="note"     → props 往下传
    @click="goToNote" → emit 往上传
  -->
</template>
```

### 例子 2：表单组件的双向模式

```vue
<!-- 子组件 NoteForm.vue -->
<script setup lang="ts">
// 接收值
defineProps<{
  title: string
}>()

// 通知父组件更新
const emit = defineEmits<{
  'update:title': [value: string]
}>()
</script>

<template>
  <input
    :value="title"
    @input="emit('update:title', $event.target.value)"
  />
</template>
```

父组件用 `v-model` 简化：

```vue
<!-- 父组件 -->
<template>
  <NoteForm v-model:title="noteTitle" />
  <!-- 等价于 -->
  <NoteForm
    :title="noteTitle"
    @update:title="noteTitle = $event"
  />
</template>
```

---

## 完整案例：NoteListView → NoteCard → NoteDetailView

这个例子展示了三个文件之间 props/emit 的数据流动。

### 文件关系

```
NoteListView.vue（列表页）  ←── 父组件
    │
    ├── NoteCard.vue（卡片组件）  ←── 子组件
    │       │
    │       │  用户点击卡片
    │       │  emit('click', id)
    │       ↓
    └── router.push(`/note/${id}`)
            │
            ↓
        NoteDetailView.vue（详情页）  ←── 另一个页面
```

### NoteCard.vue（子组件）

```vue
<!-- src/components/NoteCard.vue -->
<script setup lang="ts">
import { computed } from 'vue'
import { useCategoryStore } from '../stores/categories'

// ① defineProps 接收父组件传来的数据
const props = defineProps<{
  note: {
    id: string
    title: string
    content: string
    categoryId: string | null
    createdAt: number
  }
}>()

// ② defineEmits 声明可以触发的事件
const emit = defineEmits<{
  click: [id: string]  // 点击事件，参数是笔记 id
}>()

const categoryStore = useCategoryStore()

const summary = computed(() => {
  const content = props.note.content
  return content.length > 100 ? content.substring(0, 100) + '...' : content
})
</script>

<template>
  <!-- ③ 点击时 emit 通知父组件 -->
  <el-card shadow="hover" @click="emit('click', note.id)">
    <template #header>
      <span>{{ note.title }}</span>
    </template>
    <p>{{ summary }}</p>
  </el-card>
</template>
```

### NoteListView.vue（父组件）

```vue
<!-- src/views/NoteListView.vue -->
<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useNotesStore } from '../stores/notes'
import NoteCard from '../components/NoteCard.vue'

const router = useRouter()
const notesStore = useNotesStore()

// ④ emit 触发后，父组件执行跳转
function goToNote(id: string) {
  router.push(`/note/${id}`)
}
</script>

<template>
  <div class="notes-grid">
    <!--
      ⑤ 把数据通过 props 传给子组件
      ⑥ 监听子组件的 emit 事件
    -->
    <NoteCard
      v-for="note in notesStore.notes"
      :key="note.id"
      :note="note"
      @click="goToNote"
    />
  </div>
</template>
```

### 数据流完整图解

```
NoteListView                          NoteCard
─────────────                        ──────────
notesStore.notes (数据在这)
       │
       │ ① :note="note"  (props 往下传)
       ├────────────────────────→  props.note (子组件拿到数据)
       │                               │
       │                          用户点击卡片
       │                               │
       │ ② @click="goToNote"  (emit 往上传)
       ←────────────────────────  emit('click', '123')
       │
       ↓
goToNote('123')
       │
       ↓
router.push('/note/123')   (跳转到详情页)
       │
       ↓
NoteDetailView.vue 展示笔记详情
```

### NoteDetailView.vue（详情页）

```vue
<!-- src/views/NoteDetailView.vue -->
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useNotesStore } from '../stores/notes'

const route = useRoute()
const notesStore = useNotesStore()

// 从 URL 参数获取笔记 id，不是通过 props
const noteId = computed(() => route.params.id as string)
const note = computed(() => notesStore.getNoteById(noteId.value))
</script>

<template>
  <div v-if="note">
    <h1>{{ note.title }}</h1>
    <p>{{ note.content }}</p>
  </div>
</template>
```

**注意**：NoteDetailView 和 NoteListView 不是父子关系，是两个独立页面，通过路由切换。数据存在 store 里，两个页面都能访问。

---

## 什么时候该拆组件

拆组件不是越多越好，看实际需求：

| 情况 | 建议 | 原因 |
|------|------|------|
| 只在一个地方用 | **不拆** | 直接写更简单 |
| 两个地方要用同样的卡片 | **拆** | 复用，改一处全生效 |
| 单个组件超过 200 行 | **拆** | 太长不好维护 |
| 逻辑独立，想单独测试 | **拆** | 方便调试 |

**初学阶段**：先不拆，一个文件写完能跑就行。等发现"这段代码我在别处也写过"，再拆也不迟。

---

## 常见错误

### 错误 1：子组件直接改 props

```vue
<script setup>
const props = defineProps<{ count: number }>()

// ❌ 错误：直接修改 props
props.count = 999

// ✅ 正确：通知父组件改
const emit = defineEmits<{ 'update:count': [value: number] }>()
emit('update:count', 999)
</script>
```

### 错误 2：emit 的事件名拼错

```vue
<!-- 子组件 -->
<script setup>
const emit = defineEmits<{ delete: [id: string] }>()
emit('Delete', '123')  // ❌ 大小写不匹配，父组件收不到
emit('delete', '123')  // ✅
</script>
```

### 错误 3：忘记声明 emit 类型

```vue
<script setup>
// ❌ 没有类型声明，IDE 不提示，容易拼错
const emit = defineEmits()
emit('click', id)

// ✅ 声明类型，IDE 自动提示
const emit = defineEmits<{
  click: [id: string]
}>()
</script>
```

---

## Vue 2 对比

| Vue 2 | Vue 3 |
|-------|-------|
| `props: { title: String }` | `defineProps<{ title: string }>()` |
| `this.$emit('change', value)` | `emit('change', value)` |
| `this.$emit('update:modelValue', val)` | `emit('update:modelValue', val)` |
| `this.$props.title` | `props.title` |
| `v-model` 只能用一次 | `v-model:title` 可以多个 |

---

## 总结

```
父 → 子：用 props（标签属性传值）
子 → 父：用 emit（触发事件通知）

记住：
1. props 只读，不能在子组件里改
2. emit 只是通知，不直接改数据
3. 数据改不改，由父组件决定
```
