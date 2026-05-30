## Why

系统性重新学习 Vue 3 生态（Composition API + Vue Router + Pinia），需要一个结构化的学习项目来承载实践和笔记。当前项目已通过 create-vue 脚手架初始化，包含 Vue 3、Vue Router 5、Pinia 3，但只有默认模板代码，缺少实际业务功能和学习路径。

## What Changes

- 引入 Element Plus 组件库，替换默认样式
- 搭建应用整体布局（侧边导航 + 内容区域）
- 实现笔记 CRUD 功能（列表、详情、新建、编辑、删除）
- 实现搜索与分类过滤功能
- 配置完整路由系统（动态路由、嵌套路由、路由守卫、404）
- 使用 Pinia 管理应用状态（笔记、分类、设置），持久化至 localStorage
- 抽取自定义 Composables 复用逻辑
- 添加路由切换动画与主题切换功能
- 在 `learn/` 目录下按知识点撰写 Markdown 学习笔记

## Capabilities

### New Capabilities

- `project-setup`: 项目基础搭建——安装 Element Plus、配置全局布局（el-container 侧边栏 + 主内容区）、清理默认模板
- `note-management`: 笔记核心功能——笔记列表展示、搜索过滤、新建/编辑表单、详情查看、删除确认，使用 Element Plus 组件
- `category-management`: 分类管理——分类列表、分类 CRUD、嵌套路由展示分类下的笔记、标签系统
- `routing-system`: 路由配置——动态路由（/note/:id）、嵌套路由（/category/:id/notes）、路由守卫（离开编辑页提醒）、404 捕获、路由切换过渡动画
- `state-management`: Pinia 状态管理——notesStore、categoryStore、settingsStore，Store 间互相调用，localStorage 持久化
- `advanced-patterns`: 进阶模式——useSearch / useStorage 等自定义 Composables、provide/inject 主题注入、Transition 过渡动画
- `learning-notes`: 学习笔记体系——在 learn/ 目录按知识点创建 Markdown 笔记（ref/reactive、组件通信、路由、Pinia、Composables 等）

### Modified Capabilities

## Impact

- **新增依赖**: element-plus
- **新增目录**: `src/stores/`、`src/composables/`、`src/learn/`
- **修改文件**: `main.ts`（注册 Element Plus）、`App.vue`（整体布局重写）、`router/index.ts`（完整路由表）
- **新增视图**: HomeView、NoteListView、NoteDetailView、NoteEditView、CategoryView、SettingsView、NotFoundView
- **新增组件**: NoteCard、SearchBar、NoteForm、CategoryList 等
