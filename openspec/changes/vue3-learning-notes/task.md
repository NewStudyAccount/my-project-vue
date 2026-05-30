# Task List: Vue 3 学习笔记项目

## Phase 1: 项目基础搭建

### Task 1.1: 安装并配置 Element Plus
- [ ] 安装 element-plus 依赖
- [ ] 在 main.ts 中全量引入 Element Plus
- [ ] 注册 Element Plus 插件
- **验证**: 任意组件中可使用 el-button 等组件

### Task 1.2: 清理默认模板
- [ ] 删除 src/components/HelloWorld.vue
- [ ] 删除 src/components/TheWelcome.vue
- [ ] 删除 src/components/WelcomeItem.vue
- [ ] 删除 src/components/icons/ 目录
- [ ] 清理 src/assets/base.css 中的默认样式
- [ ] 清理 src/assets/main.css 中的默认样式
- **验证**: 项目启动无报错，页面为空白

### Task 1.3: 搭建应用整体布局
- [ ] 重写 App.vue 使用 el-container 布局
- [ ] 添加 el-aside 侧边栏（240px 宽度）
- [ ] 添加 el-main 主内容区
- [ ] 在侧边栏添加 el-menu 导航菜单
- [ ] 菜单项包含：首页、笔记列表、分类管理、设置
- [ ] 配置菜单项路由跳转（router 模式）
- [ ] 实现当前路由高亮（default-active 绑定）
- **验证**: 页面显示左右布局，菜单可点击跳转，当前路由高亮

---

## Phase 2: 状态管理

### Task 2.1: 创建 notesStore
- [ ] 创建 src/stores/notes.ts
- [ ] 定义 Note 接口（id, title, content, categoryId, createdAt, updatedAt）
- [ ] 实现 state：notes 数组
- [ ] 实现 getters：getNoteById, getNotesByCategory
- [ ] 实现 actions：addNote, updateNote, deleteNote
- [ ] 添加 localStorage 持久化（读取 + 写入）
- [ ] 提供示例初始数据
- **验证**: Store 可正常读写，刷新页面数据不丢失

### Task 2.2: 创建 categoryStore
- [ ] 创建 src/stores/categories.ts
- [ ] 定义 Category 接口（id, name, color）
- [ ] 实现 state：categories 数组
- [ ] 实现 getters：getCategoryById
- [ ] 实现 actions：addCategory, updateCategory, deleteCategory
- [ ] deleteCategory 时将关联笔记的 categoryId 设为 null
- [ ] 添加 localStorage 持久化
- [ ] 提供默认分类（如：前端、后端、数据库、其他）
- **验证**: 分类 CRUD 正常，删除分类后关联笔记变为未分类

### Task 2.3: 创建 settingsStore
- [ ] 创建 src/stores/settings.ts
- [ ] 定义 Settings 接口（theme）
- [ ] 实现 state：settings 对象
- [ ] 实现 actions：toggleTheme, setTheme
- [ ] 添加 localStorage 持久化
- **验证**: 主题状态可切换并持久化

---

## Phase 3: 路由系统

### Task 3.1: 配置完整路由表
- [ ] 编辑 src/router/index.ts
- [ ] 配置 history 模式（createWebHistory）
- [ ] 添加路由：
  - `/` → HomeView
  - `/notes` → NoteListView
  - `/note/:id` → NoteDetailView
  - `/note/:id/edit` → NoteEditView
  - `/note/new` → NoteEditView
  - `/category/:id/notes` → CategoryView（嵌套路由）
  - `/:pathMatch(.*)*` → NotFoundView
- [ ] 使用路由懒加载（import()）
- **验证**: 访问各路由可正确渲染对应视图

### Task 3.2: 创建页面视图骨架
- [ ] 创建 src/views/HomeView.vue
- [ ] 创建 src/views/NoteListView.vue
- [ ] 创建 src/views/NoteDetailView.vue
- [ ] 创建 src/views/NoteEditView.vue
- [ ] 创建 src/views/CategoryView.vue
- [ ] 创建 src/views/SettingsView.vue
- [ ] 创建 src/views/NotFoundView.vue
- **验证**: 所有路由可访问，显示占位内容

### Task 3.3: 实现路由守卫
- [ ] 在 NoteEditView 中添加 beforeRouteLeave 守卫
- [ ] 检测表单是否有未保存的修改（dirty 状态）
- [ ] 有修改时弹出 el-message-box 确认对话框
- [ ] 确认后允许离开，取消则阻止导航
- **验证**: 编辑页修改后离开会弹出确认提示

### Task 3.4: 实现路由过渡动画
- [ ] 在 App.vue 的 RouterView 外包裹 Transition 组件
- [ ] 设置 name="fade"
- [ ] 定义 fade 过渡样式（opacity 0→1，300ms）
- **验证**: 页面切换有淡入淡出效果

---

## Phase 4: 笔记核心功能

### Task 4.1: 实现笔记列表页面
- [ ] 完善 NoteListView.vue
- [ ] 使用 notesStore 获取笔记列表
- [ ] 使用 el-card 展示每条笔记（标题、摘要、分类、时间）
- [ ] 点击卡片跳转到笔记详情
- [ ] 添加"新建笔记"按钮，跳转到 /note/new
- [ ] 列表为空时显示 el-empty 组件
- **验证**: 笔记以卡片形式展示，可点击查看详情

### Task 4.2: 实现搜索与筛选功能
- [ ] 在 NoteListView 添加 SearchBar 搜索框
- [ ] 使用 useSearch composable 实现实时搜索
- [ ] 添加分类下拉筛选（el-select）
- [ ] 搜索结果实时更新
- [ ] 清空搜索框恢复全部显示
- **验证**: 输入关键词可实时过滤笔记列表

### Task 4.3: 实现笔记详情页面
- [ ] 完善 NoteDetailView.vue
- [ ] 根据路由参数 id 获取笔记数据
- [ ] 展示完整标题、内容、分类、创建时间、更新时间
- [ ] 添加"编辑"按钮跳转到编辑页
- [ ] 添加"删除"按钮（el-message-box 确认后删除）
- [ ] 笔记不存在时显示 404 提示
- **验证**: 可查看笔记详情，可编辑和删除

### Task 4.4: 实现笔记编辑页面
- [ ] 完善 NoteEditView.vue
- [ ] 区分新建模式（/note/new）和编辑模式（/note/:id/edit）
- [ ] 编辑模式自动加载笔记数据填充表单
- [ ] 使用 el-form 实现表单（标题、内容、分类选择）
- [ ] 标题字段必填校验
- [ ] 保存按钮调用 notesStore 的 addNote/updateNote
- [ ] 保存成功后跳转回列表/详情页
- [ ] 实现 dirty 状态追踪（用于路由守卫）
- **验证**: 可新建和编辑笔记，表单校验正常

---

## Phase 5: 分类管理

### Task 5.1: 实现分类管理页面
- [ ] 完善 CategoryView.vue
- [ ] 展示所有分类列表（名称、颜色标识、笔记数量）
- [ ] 添加"新建分类"按钮
- [ ] 使用 el-dialog 实现分类编辑弹窗
- [ ] 支持编辑分类名称和颜色
- [ ] 支持删除分类（确认后执行）
- [ ] 分类为空时显示引导提示
- **验证**: 分类 CRUD 功能完整

### Task 5.2: 实现分类笔记嵌套路由
- [ ] 在 CategoryView 中添加 RouterView 出口
- [ ] 创建分类笔记子视图组件
- [ ] 点击分类跳转到 /category/:id/notes
- [ ] 子视图展示该分类下的笔记列表
- [ ] 复用笔记卡片展示逻辑
- **验证**: 点击分类可查看该分类下的笔记

---

## Phase 6: 进阶功能

### Task 6.1: 创建 useSearch Composable
- [ ] 创建 src/composables/useSearch.ts
- [ ] 接收参数：数据源 ref、搜索字段数组
- [ ] 返回响应式 filteredResults
- [ ] 使用 computed 实现实时过滤
- [ ] 支持多字段搜索（标题、内容）
- **验证**: NoteListView 和 CategoryView 可复用搜索逻辑

### Task 6.2: 创建 useStorage Composable
- [ ] 创建 src/composables/useStorage.ts
- [ ] 接收参数：localStorage key、默认值
- [ ] 返回响应式 ref
- [ ] 值变化时自动同步到 localStorage
- [ ] 初始化时从 localStorage 读取
- **验证**: 可替代 Store 中手动的 localStorage 读写

### Task 6.3: 实现主题切换功能
- [ ] 在 App.vue 使用 provide 提供当前主题
- [ ] 在 SettingsView 添加主题切换开关
- [ ] 切换主题时更新 settingsStore
- [ ] 通过 CSS 变量切换明暗主题
- [ ] 子组件通过 inject 获取主题状态
- [ ] 主题状态持久化到 localStorage
- **验证**: 可切换明暗主题，刷新后保持

### Task 6.4: 创建公共组件
- [ ] 创建 src/components/NoteCard.vue（笔记卡片）
- [ ] 创建 src/components/SearchBar.vue（搜索栏）
- [ ] 创建 src/components/NoteForm.vue（笔记表单）
- [ ] 创建 src/components/CategoryList.vue（分类列表）
- [ ] 在各视图中复用这些组件
- **验证**: 组件可正常复用，功能不退化

---

## Phase 7: 学习笔记撰写

### Task 7.1: 创建学习笔记目录结构
- [ ] 创建 learn/ 目录
- [ ] 创建以下笔记文件：
  - 01-setup.md
  - 02-ref-reactive.md
  - 03-component-props-emit.md
  - 04-computed-watch.md
  - 05-vue-router.md
  - 06-pinia-basics.md
  - 07-composables.md
- **验证**: 所有笔记文件存在

### Task 7.2: 撰写学习笔记内容
- [ ] 每篇笔记包含：
  - 知识点说明
  - Vue 2 对比章节
  - 可运行的代码示例
  - 与项目实际代码的对照说明
- **验证**: 笔记内容完整，代码示例可运行

---

## 完成标准

所有任务完成后，项目应满足：
1. 应用布局正常，侧边导航可切换页面
2. 笔记 CRUD 功能完整（列表、详情、新建、编辑、删除）
3. 搜索和分类筛选功能正常
4. 分类管理功能完整，嵌套路由可查看分类笔记
5. 路由守卫防止误操作离开编辑页
6. 404 页面正常显示
7. 主题切换功能正常
8. 数据持久化到 localStorage，刷新不丢失
9. 学习笔记内容完整
