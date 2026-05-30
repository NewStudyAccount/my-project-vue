## ADDED Requirements

### Requirement: 安装并注册 Element Plus
系统 SHALL 安装 element-plus 依赖并在 main.ts 中全量引入注册。

#### Scenario: Element Plus 全局可用
- **WHEN** 应用启动
- **THEN** 所有 Element Plus 组件（el-button、el-input、el-menu 等）在任意组件中可直接使用，无需单独导入

### Requirement: 应用整体布局
系统 SHALL 使用 el-container 搭建侧边栏 + 主内容区的整体布局。

#### Scenario: 侧边栏展示导航菜单
- **WHEN** 用户访问任意页面
- **THEN** 左侧显示 240px 宽度的 el-aside 侧边栏，包含导航菜单项（首页、笔记列表、分类管理、设置）

#### Scenario: 主内容区展示路由视图
- **WHEN** 用户点击导航菜单项
- **THEN** 右侧 el-main 区域展示对应的 RouterView 页面内容

#### Scenario: 导航菜单高亮当前路由
- **WHEN** 用户位于某个页面
- **THEN** 侧边栏对应菜单项高亮显示

### Requirement: 清理默认模板
系统 SHALL 移除 create-vue 脚手架默认生成的模板组件和样式。

#### Scenario: 移除默认组件
- **WHEN** 项目搭建完成
- **THEN** HelloWorld、TheWelcome、WelcomeItem 及 icons 目录下的组件已被删除
