# 02 - ref 与 reactive 响应式基础

## ref

`ref` 用于创建基本类型的响应式数据：

```typescript
import { ref } from 'vue'

const count = ref(0)
console.log(count.value) // 0

count.value++
console.log(count.value) // 1
```

在模板中使用时，Vue 会自动解包，不需要 `.value`：

```vue
<template>
  <p>{{ count }}</p>
  <button @click="count++">+1</button>
</template>
```

## reactive

`reactive` 用于创建对象类型的响应式数据：

```typescript
import { reactive } from 'vue'

const state = reactive({
  name: 'Vue 3',
  version: 3,
})

console.log(state.name) // 'Vue 3'
state.version = 4
```

## ref vs reactive 选择指南

| 场景 | 推荐使用 |
|------|----------|
| 基本类型（string, number, boolean） | `ref` |
| 对象/数组 | `reactive` 或 `ref` |
| 需要替换整个对象 | `ref`（可以 `.value = newObj`） |
| 不需要替换整个对象 | `reactive` |

## 注意事项

```typescript
// ❌ 错误：reactive 不能替换整个对象
let state = reactive({ count: 0 })
state = reactive({ count: 1 }) // 失去响应性

// ✅ 正确：修改属性
state.count = 1

// ✅ 正确：使用 ref
const state = ref({ count: 0 })
state.value = { count: 1 } // 保持响应性
```

## 丢失响应是什么意思

丢失响应 = **改了数据，页面不更新**。

### 直观示例

```vue
<script setup>
import { reactive, ref } from 'vue'

// ❌ reactive 丢失响应（必须用 let 才能重新赋值来演示）
let state = reactive({ count: 0 })

function breakReactivity() {
  state = { count: 999 }  // 数据变了，但页面还是显示 0
  console.log(state.count) // 999 ← JS 层面确实变了
}

// ✅ ref 不会丢
const count = ref(0)

function keepReactivity() {
  count.value = 999  // 页面更新为 999
}
</script>

<template>
  <p>state.count: {{ state.count }}</p>
  <button @click="breakReactivity()">点我试试（不会变）</button>

  <p>count: {{ count }}</p>
  <button @click="keepReactivity()">点我（会变）</button>
</template>
```

### 为什么会这样

Vue 靠一个"跟踪系统"知道你改了数据：

```
创建 reactive({ count: 0 })
          ↓
Vue 偷偷记下：这个对象的 count 属性我在盯着
          ↓
state.count = 1    ✅ Vue 知道你改了，更新页面
          ↓
state = { count: 999 }  ← 你把整个对象换掉了
          ↓
Vue 盯的是旧对象，新对象没人盯 → 页面不更新
```

就像你装了个摄像头对着你的猫，结果你把猫换成了狗，摄像头还在看原来那个空位。

### ref 为什么不会丢

```typescript
const count = ref(0)
count.value = 999

// 实际上 Vue 内部做了：
// 1. 拦截 .value 的赋值
// 2. 通知页面更新
// 3. 再真正存值

// 不管你怎么改 .value，Vue 都能拦住
```

### 总结

| | 改属性 | 整体替换 |
|---|---|---|
| `reactive` | ✅ 页面更新 | ❌ 页面不更新 |
| `ref` | ✅ 页面更新 | ✅ 页面更新 |

## Vue 2 对比

| Vue 2 (Options API) | Vue 3 (Composition API) |
|---------------------|------------------------|
| `data() { return { count: 0 } }` | `const count = ref(0)` |
| `this.count` | `count.value` |
| `this.$set(obj, key, value)` | 直接修改 `reactive` 对象属性 |
