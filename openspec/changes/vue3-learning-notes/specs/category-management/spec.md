## ADDED Requirements

### Requirement: 分类列表展示
系统 SHALL 在 CategoryView 中展示所有分类及其笔记数量。

#### Scenario: 展示分类列表
- **WHEN** 用户访问分类管理页面
- **THEN** 以表格或卡片形式展示所有分类的名称、颜色标识、笔记数量

#### Scenario: 分类为空时提示
- **WHEN** 分类列表为空
- **THEN** 显示提示信息，引导用户新建分类

### Requirement: 分类 CRUD
系统 SHALL 支持分类的增删改操作。

#### Scenario: 新建分类
- **WHEN** 用户点击"新建分类"并填写名称和颜色
- **THEN** 创建新分类，自动分配 id，更新分类列表

#### Scenario: 编辑分类
- **WHEN** 用户点击某分类的编辑按钮并修改名称或颜色
- **THEN** 更新分类信息，关联该分类的笔记自动反映新分类名

#### Scenario: 删除分类
- **WHEN** 用户删除某分类
- **THEN** 弹出确认对话框，确认后该分类下的笔记变为"未分类"状态

### Requirement: 分类下的笔记视图
系统 SHALL 支持通过嵌套路由查看某分类下的所有笔记。

#### Scenario: 查看分类下笔记
- **WHEN** 用户在分类管理页面点击某分类
- **THEN** 跳转到 /category/:id/notes，展示该分类下的所有笔记列表
