# Tasks: Vue 3 学习笔记项目

## Phase 1: 项目基础搭建

- [x] 1.1 安装 element-plus 依赖
- [x] 1.2 在 main.ts 中全量引入并注册 Element Plus
- [x] 1.3 删除脚手架默认组件（HelloWorld、TheWelcome、WelcomeItem、icons）
- [x] 1.4 重写 App.vue：使用 el-container 搭建侧边栏（240px el-aside）+ 主内容区（el-main）布局
- [x] 1.5 在侧边栏实现 el-menu 导航菜单（首页、笔记列表、分类管理、设置），高亮当前路由

## Phase 2: 状态管理

- [x] 2.1 创建 notesStore（src/stores/notes.ts）：定义 Note 接口，实现 state/getters/actions（addNote、updateNote、deleteNote、getNoteById、getNotesByCategory）
- [x] 2.2 创建 categoryStore（src/stores/categories.ts）：定义 Category 接口，实现 CRUD actions，deleteCategory 时将关联笔记 categoryId 设为 null
- [x] 2.3 创建 settingsStore（src/stores/settings.ts）：定义 Settings 接口，管理 theme 状态
- [x] 2.4 为所有 store 添加 localStorage 持久化（手动在 action 中读写），首次加载时使用默认示例数据

## Phase 3: 路由系统

- [x] 3.1 配置完整路由表（/、/notes、/note/:id、/note/:id/edit、/note/new、/category/:id/notes、/:pathMatch(.*)*）
- [x] 3.2 创建 HomeView、NoteListView、NoteDetailView、NoteEditView、CategoryView、SettingsView、NotFoundView 占位组件
- [x] 3.3 实现嵌套路由：CategoryView 包含 RouterView，展示分类下的笔记
- [x] 3.4 实现路由守卫：NoteEditView 的 beforeRouteLeave，未保存修改时弹出确认
- [x] 3.5 添加路由切换过渡动画（Transition fade 效果，300ms）

## Phase 4: 笔记核心功能

- [x] 4.1 实现 NoteListView：以 el-card 卡片列表展示笔记（标题、内容摘要前100字、分类、创建时间），空列表显示 el-empty
- [x] 4.2 创建 NoteCard 组件（src/components/NoteCard.vue）：展示单条笔记卡片，点击跳转详情
- [x] 4.3 创建 SearchBar 组件（src/components/SearchBar.vue）：搜索框 + 分类筛选下拉
- [x] 4.4 实现搜索过滤：输入关键词实时过滤笔记列表（标题+内容匹配）
- [x] 4.5 实现 NoteDetailView：展示笔记完整内容（标题、内容、分类、创建/更新时间），提供编辑和删除按钮
- [x] 4.6 实现 NoteEditView：表单编辑（标题、内容、分类选择），支持新建和编辑两种模式
- [x] 4.7 创建 NoteForm 组件（src/components/NoteForm.vue）：el-form 表单，标题必填校验
- [x] 4.8 实现删除功能：el-message-box 确认对话框，确认后删除并返回列表

## Phase 5: 分类管理

- [x] 5.1 实现 CategoryView：展示所有分类（名称、颜色标识、笔记数量），空列表提示
- [x] 5.2 实现分类 CRUD：新建/编辑分类对话框（名称+颜色选择），删除确认
- [x] 5.3 创建 CategoryList 组件（src/components/CategoryList.vue）：分类列表展示
- [x] 5.4 实现嵌套路由展示：点击分类跳转 /category/:id/notes，展示该分类下笔记

## Phase 6: 进阶功能

- [x] 6.1 创建 useSearch composable（src/composables/useSearch.ts）：封装搜索逻辑，接收数据源和搜索字段，返回 filteredResults
- [x] 6.2 创建 useStorage composable（src/composables/useStorage.ts）：响应式 localStorage 读写封装
- [x] 6.3 实现主题切换：settingsStore 管理主题状态，provide/inject 跨组件注入，CSS 变量控制明暗主题
- [x] 6.4 实现 SettingsView：主题切换开关

## Phase 7: 学习笔记

- [x] 7.1 创建 learn/01-setup.md：项目搭建与环境配置笔记
- [x] 7.2 创建 learn/02-ref-reactive.md：ref 与 reactive 响应式基础笔记
- [x] 7.3 创建 learn/03-component-props-emit.md：组件通信（Props/Emit）笔记
- [x] 7.4 创建 learn/04-computed-watch.md：计算属性与侦听器笔记
- [x] 7.5 创建 learn/05-vue-router.md：Vue Router 使用笔记
- [x] 7.6 创建 learn/06-pinia-basics.md：Pinia 状态管理笔记
- [x] 7.7 创建 learn/07-composables.md：自定义 Composables 笔记
