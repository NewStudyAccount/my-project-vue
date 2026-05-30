## ADDED Requirements

### Requirement: useSearch Composable
系统 SHALL 提供 useSearch 自定义组合式函数封装搜索逻辑。

#### Scenario: 复用搜索逻辑
- **WHEN** NoteListView 和 CategoryView 需要搜索功能
- **THEN** 各自调用 useSearch 传入数据源和搜索字段，获取响应式 filteredResults

### Requirement: useStorage Composable
系统 SHALL 提供 useStorage 自定义组合式函数封装 localStorage 读写。

#### Scenario: 响应式 localStorage 读写
- **WHEN** 组件调用 useStorage('key', defaultValue)
- **THEN** 返回响应式 ref，值变化时自动同步到 localStorage

### Requirement: 主题切换（provide/inject）
系统 SHALL 支持通过 provide/inject 实现跨组件主题切换。

#### Scenario: 切换明暗主题
- **WHEN** 用户在设置页面切换主题
- **THEN** 应用整体切换为对应主题，所有组件通过 inject 获取当前主题

### Requirement: 路由过渡动画
系统 SHALL 使用 Vue Transition 组件实现路由切换动画。

#### Scenario: 淡入淡出效果
- **WHEN** 用户在不同路由间切换
- **THEN** 离开页面淡出，进入页面淡入，过渡时间 300ms
