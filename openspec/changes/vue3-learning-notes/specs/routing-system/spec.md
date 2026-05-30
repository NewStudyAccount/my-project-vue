## ADDED Requirements

### Requirement: 基础路由配置
系统 SHALL 配置 Vue Router history 模式，包含所有页面路由。

#### Scenario: 首页路由
- **WHEN** 用户访问 /
- **THEN** 展示 HomeView 首页内容

#### Scenario: 笔记列表路由
- **WHEN** 用户访问 /notes
- **THEN** 展示 NoteListView 笔记列表

#### Scenario: 笔记详情路由
- **WHEN** 用户访问 /note/123
- **THEN** 展示 id 为 123 的笔记详情（NoteDetailView）

#### Scenario: 笔记编辑路由
- **WHEN** 用户访问 /note/123/edit
- **THEN** 展示 id 为 123 的笔记编辑表单（NoteEditView）

#### Scenario: 新建笔记路由
- **WHEN** 用户访问 /note/new
- **THEN** 展示空白笔记编辑表单（NoteEditView）

### Requirement: 嵌套路由
系统 SHALL 使用嵌套路由展示分类下的笔记。

#### Scenario: 分类笔记嵌套路由
- **WHEN** 用户访问 /category/:id/notes
- **THEN** CategoryView 保持分类侧边栏，RouterView 区域展示该分类的笔记列表

### Requirement: 路由守卫
系统 SHALL 在编辑页面配置离开守卫防止误操作。

#### Scenario: 编辑页离开提醒
- **WHEN** 用户在编辑页面修改了内容但未保存，尝试离开页面
- **THEN** 弹出确认对话框提示"未保存的更改将丢失"，用户确认后才允许离开

#### Scenario: 未修改时直接离开
- **WHEN** 用户在编辑页面未做任何修改，点击导航离开
- **THEN** 直接离开，不弹出确认对话框

### Requirement: 404 页面
系统 SHALL 处理未匹配路由。

#### Scenario: 访问不存在的路由
- **WHEN** 用户访问任意未定义的路由（如 /xyz）
- **THEN** 展示 NotFoundView 404 页面，提供返回首页链接

### Requirement: 路由切换过渡动画
系统 SHALL 为路由切换添加过渡动画。

#### Scenario: 页面切换动画
- **WHEN** 用户在不同路由间切换
- **THEN** 使用 Vue Transition 组件实现淡入淡出效果
