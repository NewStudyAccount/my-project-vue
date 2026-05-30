## ADDED Requirements

### Requirement: 笔记列表展示
系统 SHALL 在 NoteListView 中以卡片列表形式展示所有笔记。

#### Scenario: 展示笔记列表
- **WHEN** 用户访问笔记列表页面
- **THEN** 以 Element Plus el-card 组件展示每条笔记的标题、内容摘要（前100字）、分类、创建时间

#### Scenario: 列表为空时提示
- **WHEN** 笔记列表为空
- **THEN** 显示 el-empty 组件，提示"暂无笔记，点击新建"

### Requirement: 笔记搜索过滤
系统 SHALL 支持按关键词实时搜索笔记。

#### Scenario: 按标题搜索
- **WHEN** 用户在搜索框输入关键词
- **THEN** 笔记列表实时过滤，只显示标题或内容包含关键词的笔记（computed + watch 实现）

#### Scenario: 按分类筛选
- **WHEN** 用户选择某个分类
- **THEN** 笔记列表只显示该分类下的笔记

#### Scenario: 清除搜索
- **WHEN** 用户清空搜索框
- **THEN** 恢复显示所有笔记

### Requirement: 新建笔记
系统 SHALL 支持通过表单新建笔记。

#### Scenario: 打开新建表单
- **WHEN** 用户点击"新建笔记"按钮
- **THEN** 跳转到编辑页面，表单为空，可填写标题、内容、选择分类

#### Scenario: 提交新笔记
- **WHEN** 用户填写标题（必填）和内容后点击保存
- **THEN** 创建新笔记，自动生成 id 和时间戳，跳转回笔记列表

#### Scenario: 标题为空时阻止提交
- **WHEN** 用户未填写标题就点击保存
- **THEN** 表单显示校验错误提示，不提交

### Requirement: 编辑笔记
系统 SHALL 支持编辑已有笔记。

#### Scenario: 加载已有笔记数据
- **WHEN** 用户访问 /note/:id/edit
- **THEN** 表单自动填充该笔记的标题、内容、分类

#### Scenario: 保存修改
- **WHEN** 用户修改笔记内容后点击保存
- **THEN** 更新笔记的 updatedAt 时间戳，跳转回笔记详情页

### Requirement: 查看笔记详情
系统 SHALL 支持查看单条笔记的完整内容。

#### Scenario: 展示笔记详情
- **WHEN** 用户点击某条笔记卡片
- **THEN** 跳转到 /note/:id，展示完整标题、内容、分类、创建时间、更新时间

### Requirement: 删除笔记
系统 SHALL 支持删除笔记并二次确认。

#### Scenario: 确认删除
- **WHEN** 用户在笔记详情页或列表中点击删除按钮
- **THEN** 弹出 el-message-box 确认对话框，确认后删除笔记并返回列表

#### Scenario: 取消删除
- **WHEN** 用户在确认对话框点击取消
- **THEN** 不执行删除操作，笔记保持不变
