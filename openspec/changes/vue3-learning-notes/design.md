# Design Document: Vue 3 学习笔记项目

## 技术栈

- **前端框架**: Vue 3 (Composition API)
- **路由**: Vue Router 5 (History 模式)
- **状态管理**: Pinia 3
- **UI 组件库**: Element Plus (全量引入)
- **构建工具**: Vite
- **语言**: TypeScript

## 项目结构

```
src/
├── assets/            # 静态资源
├── components/        # 公共组件
│   ├── NoteCard.vue       # 笔记卡片组件
│   ├── SearchBar.vue      # 搜索栏组件
│   ├── NoteForm.vue       # 笔记表单组件
│   └── CategoryList.vue   # 分类列表组件
├── composables/       # 自定义组合式函数
│   ├── useSearch.ts       # 搜索逻辑复用
│   └── useStorage.ts      # localStorage 响应式封装
├── router/            # 路由配置
│   └── index.ts           # 路由表定义
├── stores/            # Pinia 状态管理
│   ├── notes.ts           # 笔记状态
│   ├── categories.ts      # 分类状态
│   └── settings.ts        # 应用设置状态
├── views/             # 页面视图
│   ├── HomeView.vue       # 首页
│   ├── NoteListView.vue   # 笔记列表
│   ├── NoteDetailView.vue # 笔记详情
│   ├── NoteEditView.vue   # 笔记编辑/新建
│   ├── CategoryView.vue   # 分类管理
│   ├── SettingsView.vue   # 设置页面
│   └── NotFoundView.vue   # 404 页面
├── App.vue            # 根组件（整体布局）
├── main.ts            # 入口文件
└── style.css          # 全局样式
learn/                 # 学习笔记目录
├── 01-setup.md
├── 02-ref-reactive.md
├── 03-component-props-emit.md
├── 04-computed-watch.md
├── 05-vue-router.md
├── 06-pinia-basics.md
└── 07-composables.md
```

## 架构设计

### 1. 布局架构

```
┌─────────────────────────────────────────────────┐
│                   App.vue                        │
│  ┌──────────┬──────────────────────────────────┐ │
│  │ el-aside │           el-main                │ │
│  │ (240px)  │  ┌────────────────────────────┐  │ │
│  │          │  │       RouterView           │  │ │
│  │  导航菜单 │  │    <Transition fade>       │  │ │
│  │          │  │       页面内容              │  │ │
│  │          │  └────────────────────────────┘  │ │
│  └──────────┴──────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

### 2. 状态管理架构

```
┌─────────────────────────────────────────────────────┐
│                    Pinia Stores                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │ notesStore  │  │categoryStore│  │settingsStore│ │
│  │             │  │             │  │             │ │
│  │ - notes[]   │  │ - categories│  │ - theme     │ │
│  │ - CRUD ops  │  │ - CRUD ops  │  │ - other     │ │
│  └──────┬──────┘  └──────┬──────┘  └─────────────┘ │
│         │                │                          │
│         └────────────────┼──────────────────────────┘
│                          │
│              localStorage 持久化
└─────────────────────────────────────────────────────┘
```

### 3. 数据模型

```typescript
// 笔记模型
interface Note {
  id: string
  title: string
  content: string
  categoryId: string | null
  createdAt: number
  updatedAt: number
}

// 分类模型
interface Category {
  id: string
  name: string
  color: string
}

// 应用设置模型
interface Settings {
  theme: 'light' | 'dark'
}
```

### 4. 路由设计

| 路径 | 视图 | 说明 |
|------|------|------|
| `/` | HomeView | 首页 |
| `/notes` | NoteListView | 笔记列表 |
| `/note/:id` | NoteDetailView | 笔记详情 |
| `/note/:id/edit` | NoteEditView | 编辑笔记 |
| `/note/new` | NoteEditView | 新建笔记 |
| `/category/:id/notes` | CategoryView → RouterView | 分类下的笔记（嵌套路由） |
| `/:pathMatch(.*)*` | NotFoundView | 404 页面 |

### 5. 组件通信策略

- **父子组件**: Props 传递数据，Emit 触发事件
- **跨组件状态**: Pinia Store 统一管理
- **主题切换**: provide/inject 注入当前主题
- **路由参数**: useRoute 获取动态参数

### 6. 样式方案

- Element Plus 组件样式（全量引入）
- 全局 CSS 变量控制主题
- Scoped 样式隔离组件样式
- CSS Transition 实现路由切换动画

## 设计决策

### 决策 1: Element Plus 全量引入 vs 按需引入

**选择**: 全量引入

**理由**: 学习项目优先考虑开发便利性，全量引入无需配置 unplugin-vue-components，减少初期配置复杂度。

### 决策 2: localStorage 持久化方案

**选择**: 手动在 Store action 中同步 localStorage

**理由**: 
- 不引入额外依赖（如 pinia-plugin-persistedstate）
- 更好地理解持久化原理
- 可以自定义序列化/反序列化逻辑

### 决策 3: 学习笔记存放位置

**选择**: 项目根目录 learn/ 文件夹

**理由**: 
- 与 src 代码分离，不影响构建
- 可直接在编辑器中阅读
- 方便版本控制

## 实现顺序

1. **Phase 1**: 项目基础搭建（Element Plus + 布局 + 清理模板）
2. **Phase 2**: 状态管理（三个 Store + localStorage 持久化）
3. **Phase 3**: 路由系统（完整路由表 + 守卫 + 404 + 动画）
4. **Phase 4**: 笔记核心功能（列表 + 详情 + CRUD）
5. **Phase 5**: 分类管理（分类 CRUD + 嵌套路由）
6. **Phase 6**: 进阶功能（Composables + 主题切换）
7. **Phase 7**: 学习笔记撰写
