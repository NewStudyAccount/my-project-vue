# 任务清单

## 阶段一：后端基础

- [x] 搭建 Spring Boot 项目（Spring Initializr，选 Web + MySQL Driver）
- [x] 放到 `my-project-vue/backend/` 目录
- [x] 配置 `application.yml`（数据库连接、端口 8080）
- [x] 创建数据库 `notes_db`，执行建表 SQL
- [x] 实现 `Result` 统一响应类
- [x] 实现 `BusinessException` + 全局异常处理
- [x] 实现 `WebConfig` CORS 配置
- [x] 实现 `MyBatisPlusConfig` 分页插件

## 阶段二：后端认证模块

- [x] 实现 `User` 实体 + `UserMapper`
- [x] 实现注册接口（BCrypt 加密）
- [x] 实现登录接口（JWT 生成）
- [x] 实现 `TokenUtil` 工具类
- [x] 实现 `AuthInterceptor` + `UserContext`
- [x] 实现 `/api/auth/me` 接口
- [x] 配置拦截器（排除 auth 路径）

## 阶段三：后端数据模块

- [x] 实现 `Note` 实体 + `NoteMapper` + `NoteService` + `NoteController`
- [x] 实现 `Category` 实体 + `CategoryMapper` + `CategoryService` + `CategoryController`
- [x] 所有查询带 `user_id` 隔离
- [x] 删除分类时级联清空笔记的 `category_id`
- [x] 笔记列表支持分页、搜索、分类筛选

## 阶段四：前端基础设施

- [x] 安装 axios
- [x] 创建 `src/utils/request.ts`（Axios 封装）
- [x] 创建 `src/types/api.ts`（类型定义）
- [x] 创建 `src/api/auth.ts`、`src/api/notes.ts`、`src/api/categories.ts`
- [x] 配置 Vite proxy

## 阶段五：前端认证模块

- [x] 创建 `src/stores/auth.ts`
- [x] 重写 `src/views/Login.vue`（接入 auth store）
- [x] 新建 `src/views/Register.vue`
- [x] 添加路由守卫 + meta 配置
- [x] 改造 `App.vue`（启动恢复登录状态、register 布局）

## 阶段六：前端数据层改造

- [ ] 改造 `src/stores/notes.ts`（localStorage → API）
- [ ] 改造 `src/stores/categories.ts`（localStorage → API）
- [ ] 改造 `NoteListView.vue`（异步加载 + loading）
- [ ] 改造 `NoteDetailView.vue`（异步加载）
- [ ] 改造 `NoteEditView.vue`（异步保存）
- [ ] 改造 `CategoryView.vue`（异步 CRUD）
- [ ] 改造 `HomeView.vue`（异步统计）
- [ ] 改造 `SettingsView.vue`（退出登录）

## 阶段七：联调优化

- [ ] 前后端联调测试
- [ ] 处理边界情况（网络错误、token 过期等）
- [ ] 清理无用文件（useStorage.ts、counter.ts）
- [ ] 代码整理和注释
