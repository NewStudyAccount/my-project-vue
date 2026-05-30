## ADDED Requirements

### Requirement: 学习笔记目录结构
系统 SHALL 在 learn/ 目录下按知识点创建 Markdown 学习笔记。

#### Scenario: 笔记文件存在
- **WHEN** 项目搭建完成
- **THEN** learn/ 目录下包含以下笔记文件：01-setup.md、02-ref-reactive.md、03-component-props-emit.md、04-computed-watch.md、05-vue-router.md、06-pinia-basics.md、07-composables.md

### Requirement: 每篇笔记包含对比说明
每篇学习笔记 SHALL 包含 Vue 2 与 Vue 3 的对比说明（适用于有 Vue 2 经验的学习者）。

#### Scenario: 笔记包含对比章节
- **WHEN** 用户阅读任意一篇学习笔记
- **THEN** 笔记中包含"Vue 2 对比"章节，说明相同功能在 Vue 2 Options API 中的写法

### Requirement: 笔记包含代码示例
每篇学习笔记 SHALL 包含可运行的代码示例。

#### Scenario: 代码示例可对照实现
- **WHEN** 用户阅读学习笔记
- **THEN** 笔记中的代码示例与项目中对应组件的实现一致，可直接对照学习
