## ADDED Requirements

### Requirement: notesStore 笔记状态管理
系统 SHALL 使用 Pinia 管理笔记数据状态。

#### Scenario: Store 包含完整 CRUD actions
- **WHEN** 应用运行
- **THEN** notesStore 提供 addNote、updateNote、deleteNote、getNoteById 等 actions

#### Scenario: getter 派生计算状态
- **WHEN** 组件访问 notesStore
- **THEN** 提供 getNotesByCategory 等 getter，根据分类过滤笔记

#### Scenario: Store 间调用
- **WHEN** 删除笔记时
- **THEN** notesStore 的 deleteNote action 正常执行，不依赖其他 store 的配合

### Requirement: categoryStore 分类状态管理
系统 SHALL 使用 Pinia 管理分类数据状态。

#### Scenario: 分类 Store 提供 CRUD
- **WHEN** 应用运行
- **THEN** categoryStore 提供 addCategory、updateCategory、deleteCategory 等 actions

#### Scenario: 分类删除时更新关联笔记
- **WHEN** 调用 categoryStore.deleteCategory
- **THEN** 该分类下的笔记 categoryId 被设为 null（"未分类"），通过调用 notesStore 实现

### Requirement: settingsStore 设置状态管理
系统 SHALL 使用 Pinia 管理应用设置。

#### Scenario: 主题状态
- **WHEN** 用户切换主题
- **THEN** settingsStore 更新 theme 状态，应用对应 CSS 变量

### Requirement: localStorage 持久化
系统 SHALL 将所有 Pinia store 数据持久化到 localStorage。

#### Scenario: 数据持久化
- **WHEN** 用户添加、编辑或删除笔记/分类
- **THEN** 对应 store 数据自动同步写入 localStorage

#### Scenario: 页面刷新恢复数据
- **WHEN** 用户刷新页面
- **THEN** 各 store 从 localStorage 读取数据，恢复到上次状态

#### Scenario: localStorage 为空时使用默认值
- **WHEN** 首次访问应用（localStorage 无数据）
- **THEN** store 使用默认初始数据（示例笔记和默认分类）
