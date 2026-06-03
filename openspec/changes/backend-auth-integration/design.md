# 接入后端 + 用户认证 — 方案设计文档

> 创建日期：2026-06-03
> 状态：设计完成，待实施

---

## 一、项目概述

### 1.1 背景

当前 Vue 3 学习笔记应用的数据全部存储在浏览器 localStorage 中，无后端服务。本次改造将接入 Java Spring Boot 后端，实现用户名密码认证，并将数据存储迁移到 MySQL 数据库。

### 1.2 目标

- 实现用户注册、登录、退出登录功能
- 笔记和分类数据从 localStorage 迁移到后端数据库
- 每个用户的数据相互隔离
- 前端通过 RESTful API 与后端交互

### 1.3 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Element Plus + Pinia + Vue Router |
| 后端 | Java 17 + Spring Boot 3 + MyBatis-Plus |
| 数据库 | MySQL 8 |
| 接口风格 | RESTful JSON API |
| 认证方式 | JWT（jjwt 库） |
| 密码加密 | BCrypt（jbcrypt 库） |

---

## 二、后端设计

### 2.1 项目结构

```
my-project-vue/backend/
├── pom.xml
└── src/main/
    ├── java/com/notes/
    │   ├── NotesApplication.java              # 启动类
    │   ├── config/
    │   │   ├── WebConfig.java                 # CORS 配置
    │   │   └── MyBatisPlusConfig.java         # 分页插件
    │   ├── common/
    │   │   ├── Result.java                    # 统一响应体
    │   │   ├── BusinessException.java         # 业务异常
    │   │   └── GlobalExceptionHandler.java    # 全局异常处理
    │   ├── auth/
    │   │   ├── AuthController.java            # 认证接口
    │   │   ├── AuthService.java               # 认证业务逻辑
    │   │   ├── TokenUtil.java                 # JWT 生成/解析
    │   │   ├── AuthInterceptor.java           # 认证拦截器
    │   │   └── UserContext.java               # ThreadLocal 当前用户
    │   ├── user/
    │   │   ├── User.java                      # 实体类
    │   │   ├── UserMapper.java                # MyBatis-Plus Mapper
    │   │   └── UserService.java               # 用户服务
    │   ├── note/
    │   │   ├── Note.java                      # 实体类
    │   │   ├── NoteMapper.java
    │   │   ├── NoteService.java
    │   │   └── NoteController.java
    │   └── category/
    │       ├── Category.java
    │       ├── CategoryMapper.java
    │       ├── CategoryService.java
    │       └── CategoryController.java
    └── resources/
        └── application.yml                    # 配置文件
```

### 2.2 数据库设计

#### 用户表 (users)

```sql
CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 分类表 (categories)

```sql
CREATE TABLE categories (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    color      VARCHAR(20)  DEFAULT '#409EFF',
    user_id    BIGINT       NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);
```

#### 笔记表 (notes)

```sql
CREATE TABLE notes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    category_id BIGINT       COMMENT '可为空，表示未分类',
    user_id     BIGINT       NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id)
);
```

### 2.3 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

错误响应示例：

```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

### 2.4 认证流程

```
┌─────────────────────────────────────────────────────────────┐
│                        登录流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  前端                        后端                            │
│  ────                        ────                            │
│  POST /api/auth/login  ───▶  1. 根据 username 查用户        │
│  { username, password }      2. BCrypt 校验密码              │
│                              3. 生成 JWT (userId, 24h过期)   │
│  ◀─── { token, user }  ────  4. 返回 token + 用户信息       │
│                                                             │
│  存 token 到 localStorage                                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                       后续请求流程                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  副端                        后端                            │
│  ────                        ────                            │
│  GET /api/notes        ───▶  AuthInterceptor 拦截           │
│  Header: Bearer <jwt>        1. 解析 JWT                    │
│                              2. 取 userId 放入 UserContext   │
│                              3. Controller 通过 UserContext  │
│                                 获取当前用户                 │
│  ◀─── { code, data }  ────  4. 查询该用户的数据返回         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.5 认证拦截器配置

```
拦截路径：/api/**
排除路径：
  - POST /api/auth/login    （登录）
  - POST /api/auth/register （注册）
```

### 2.6 密码加密方案

- 注册时：`BCrypt.hashpw(password, BCrypt.gensalt())` 加密存储
- 登录时：`BCrypt.checkpw(rawPassword, hashedPassword)` 校验
- 使用 `org.mindrot:jbcrypt:0.4` 轻量库

### 2.7 JWT 方案

- 使用 `io.jsonwebtoken:jjwt` 库（版本 0.12.6）
- Payload：`sub`(userId) + `claim:username` + `exp`(24小时)
- 签名：HMAC-SHA256，密钥硬编码（学习项目）

### 2.8 数据隔离

所有笔记和分类的查询都带 `WHERE user_id = ?` 条件，userId 从 `UserContext`（ThreadLocal）获取。确保不同用户只能访问自己的数据。

### 2.9 分类删除的级联处理

删除分类时，将该分类下所有笔记的 `category_id` 置为 `NULL`（在 Service 层事务中完成）。

---

## 三、API 接口设计

### 3.1 认证接口

#### POST /api/auth/register

请求：
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

响应（成功）：
```json
{ "code": 200, "message": "注册成功", "data": null }
```

响应（用户名已存在）：
```json
{ "code": 400, "message": "用户名已存在", "data": null }
```

#### POST /api/auth/login

请求：
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

响应（成功）：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": { "id": "1", "username": "zhangsan" }
  }
}
```

#### GET /api/auth/me

请求头：`Authorization: Bearer <token>`

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": { "id": "1", "username": "zhangsan" }
}
```

### 3.2 笔记接口

#### GET /api/notes

查询参数：
- `keyword` — 搜索关键词（模糊匹配标题和内容）
- `categoryId` — 分类筛选
- `page` — 页码（默认 1）
- `pageSize` — 每页数量（默认 10）

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "1",
        "title": "Vue 3 入门",
        "content": "...",
        "categoryId": "1",
        "categoryName": "Vue 基础",
        "createdAt": "2026-06-03T10:00:00",
        "updatedAt": "2026-06-03T10:00:00"
      }
    ],
    "total": 25
  }
}
```

#### GET /api/notes/:id

响应 data：单条笔记对象（同上）

#### POST /api/notes

请求：
```json
{ "title": "新笔记", "content": "内容", "categoryId": "1" }
```

响应 data：创建后的笔记对象（含后端生成的 id）

#### PUT /api/notes/:id

请求体同 POST。响应 data：更新后的笔记对象。

#### DELETE /api/notes/:id

响应 data：`null`

### 3.3 分类接口

#### GET /api/categories

响应 data：`[{ id, name, color }]`

#### POST /api/categories

请求：`{ name, color }`。响应 data：创建后的分类对象。

#### PUT /api/categories/:id

请求体同 POST。响应 data：更新后的分类对象。

#### DELETE /api/categories/:id

响应 data：`null`。删除时级联清空关联笔记的 category_id。

---

## 四、前端改造设计

### 4.1 新增文件

| 文件路径 | 说明 |
|----------|------|
| `src/utils/request.ts` | Axios 封装（拦截器、错误处理） |
| `src/types/api.ts` | 通用响应类型定义 |
| `src/api/auth.ts` | 认证接口封装 |
| `src/api/notes.ts` | 笔记接口封装 |
| `src/api/categories.ts` | 分类接口封装 |
| `src/stores/auth.ts` | 认证状态管理 |
| `src/views/Register.vue` | 注册页面 |

### 4.2 修改文件

| 文件路径 | 改动说明 |
|----------|----------|
| `src/router/index.ts` | 添加注册路由、路由守卫、meta 标记 |
| `src/stores/notes.ts` | localStorage → API 调用，action 改为 async |
| `src/stores/categories.ts` | localStorage → API 调用，action 改为 async |
| `src/views/Login.vue` | 重写，接入 auth store |
| `src/views/Register.vue` | 新建，注册表单 |
| `src/views/NoteListView.vue` | 异步加载 + loading 状态 |
| `src/views/NoteDetailView.vue` | 异步加载笔记详情 |
| `src/views/NoteEditView.vue` | 异步保存 + 错误处理 |
| `src/views/CategoryView.vue` | 异步 CRUD |
| `src/views/HomeView.vue` | 异步获取统计数据 |
| `src/views/SettingsView.vue` | 移除"清除数据"，添加退出登录 |
| `src/App.vue` | 启动时恢复登录状态，register 路由布局 |

### 4.3 可删除文件

| 文件路径 | 原因 |
|----------|------|
| `src/composables/useStorage.ts` | 未被使用 |
| `src/stores/counter.ts` | 未被使用 |

### 4.4 Axios 封装设计

```
src/utils/request.ts

axios.create({ baseURL: '/api', timeout: 10000 })

请求拦截器：
  → 读 localStorage('token')
  → 有值则设 Authorization: Bearer xxx

响应拦截器：
  → 成功：return response.data（脱壳，调用方直接拿 { code, message, data }）
  → 401：清 token，跳 /login
  → 其他错误：ElMessage.error(message)，return Promise.reject
```

### 4.5 Auth Store 设计

```
src/stores/auth.ts

State:
  token: string | null     ← 初始化从 localStorage 读
  user: { id, username } | null

Getters:
  isLoggedIn: computed ← !!token

Actions:
  login(username, password)
    → POST /api/auth/login
    → 存 token 到 state + localStorage
    → 存 user 到 state

  register(username, password)
    → POST /api/auth/register
    → 返回成功/失败

  logout()
    → 清 token (state + localStorage)
    → 清 user
    → router.push('/login')

  fetchCurrentUser()
    → GET /api/auth/me
    → 存 user（页面刷新时调用）

持久化策略：
  token → localStorage（跨刷新保持登录）
  user  → 不持久化（每次刷新重新 fetch）
```

### 4.6 路由守卫设计

```
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')            // 未登录 → 跳登录页
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')                 // 已登录 → 跳首页
  } else {
    next()                    // 放行
  }
})

路由 meta 配置：
  /login     → requiresAuth: false
  /register  → requiresAuth: false
  /          → requiresAuth: true
  /notes     → requiresAuth: true
  /note/*    → requiresAuth: true
  /categories → requiresAuth: true
  /settings  → requiresAuth: true
  404        → requiresAuth: false
```

### 4.7 Notes Store 改造

```
改造前（同步 localStorage）         改造后（异步 API）
──────────────────────            ─────────────────────
loadNotes()                       fetchNotes(params)
  → localStorage.getItem            → GET /api/notes
  → JSON.parse                      → 赋值 notes.value + total

addNote(note)                     addNote(note)
  → 生成 id (Date.now)              → POST /api/notes
  → notes.value.push                → 用返回的 note（后端生成 id）
  → persist()

updateNote(id, updates)           updateNote(id, updates)
  → find + merge                    → PUT /api/notes/:id
  → persist()                       → 替换数组中对应项

deleteNote(id)                    deleteNote(id)
  → filter out                      → DELETE /api/notes/:id
  → persist()                       → filter out

新增 loading: ref(false) 状态
新增 total: ref(0) 用于分页
删除 persist() 函数
删除 localStorage 相关代码
```

### 4.8 Categories Store 改造

同 Notes Store 模式：

```
fetchCategories()  → GET /api/categories
addCategory()      → POST /api/categories
updateCategory()   → PUT /api/categories/:id
deleteCategory()   → DELETE /api/categories/:id

删除级联清空笔记 categoryId 的逻辑（由后端处理）
```

### 4.9 各页面改造要点

| 页面 | 改造内容 |
|------|---------|
| NoteListView | `onMounted` 调 `fetchNotes()`；搜索/筛选改为调接口（加 debounce）；加 `v-loading` |
| NoteDetailView | `onMounted` 用路由参数 id 获取笔记详情 |
| NoteEditView | 保存改为 `await addNote()` / `await updateNote()`；loading + error 处理 |
| CategoryView | `onMounted` 调 `fetchCategories()`；CRUD 改为异步 |
| HomeView | 统计数据从 notes/categories 列表计算，或新增统计接口 |
| SettingsView | 移除"清除数据"按钮；添加退出登录按钮 |

### 4.10 App.vue 改造

```typescript
// 启动时恢复登录状态
const authStore = useAuthStore()
onMounted(async () => {
  if (authStore.isLoggedIn) {
    await authStore.fetchCurrentUser()
  }
})

// 布局判断扩展（注册页也用无侧边栏布局）
const isFullPage = computed(() =>
  route.path === '/login' || route.path === '/register'
)
```

---

## 五、Maven 依赖清单

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.7</version>
    </dependency>

    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>

    <!-- BCrypt -->
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>
```

---

## 六、application.yml 配置

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/notes_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
```

---

## 七、Vite 代理配置

```typescript
// vite.config.ts — 在 server 块中添加
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

---

## 八、开发计划

### 阶段一：后端基础

- [ ] 搭建 Spring Boot 项目（Spring Initializr，选 Web + MySQL Driver）
- [ ] 放到 `my-project-vue/backend/` 目录
- [ ] 配置 `application.yml`（数据库连接、端口 8080）
- [ ] 创建数据库 `notes_db`，执行建表 SQL
- [ ] 实现 `Result` 统一响应类
- [ ] 实现 `BusinessException` + 全局异常处理
- [ ] 实现 `WebConfig` CORS 配置
- [ ] 实现 `MyBatisPlusConfig` 分页插件

### 阶段二：后端认证模块

- [ ] 实现 `User` 实体 + `UserMapper`
- [ ] 实现注册接口（BCrypt 加密）
- [ ] 实现登录接口（JWT 生成）
- [ ] 实现 `TokenUtil` 工具类
- [ ] 实现 `AuthInterceptor` + `UserContext`
- [ ] 实现 `/api/auth/me` 接口
- [ ] 配置拦截器（排除 auth 路径）

### 阶段三：后端数据模块

- [ ] 实现 `Note` 实体 + `NoteMapper` + `NoteService` + `NoteController`
- [ ] 实现 `Category` 实体 + `CategoryMapper` + `CategoryService` + `CategoryController`
- [ ] 所有查询带 `user_id` 隔离
- [ ] 删除分类时级联清空笔记的 `category_id`
- [ ] 笔记列表支持分页、搜索、分类筛选

### 阶段四：前端基础设施

- [ ] 安装 axios
- [ ] 创建 `src/utils/request.ts`（Axios 封装）
- [ ] 创建 `src/types/api.ts`（类型定义）
- [ ] 创建 `src/api/auth.ts`、`src/api/notes.ts`、`src/api/categories.ts`
- [ ] 配置 Vite proxy

### 阶段五：前端认证模块

- [ ] 创建 `src/stores/auth.ts`
- [ ] 重写 `src/views/Login.vue`（接入 auth store）
- [ ] 新建 `src/views/Register.vue`
- [ ] 添加路由守卫 + meta 配置
- [ ] 改造 `App.vue`（启动恢复登录状态、register 布局）

### 阶段六：前端数据层改造

- [ ] 改造 `src/stores/notes.ts`（localStorage → API）
- [ ] 改造 `src/stores/categories.ts`（localStorage → API）
- [ ] 改造 `NoteListView.vue`（异步加载 + loading）
- [ ] 改造 `NoteDetailView.vue`（异步加载）
- [ ] 改造 `NoteEditView.vue`（异步保存）
- [ ] 改造 `CategoryView.vue`（异步 CRUD）
- [ ] 改造 `HomeView.vue`（异步统计）
- [ ] 改造 `SettingsView.vue`（退出登录）

### 阶段七：联调优化

- [ ] 前后端联调测试
- [ ] 处理边界情况（网络错误、token 过期等）
- [ ] 清理无用文件（useStorage.ts、counter.ts）
- [ ] 代码整理和注释

---

## 九、学习重点

| 知识点 | 实践位置 |
|--------|----------|
| Axios 封装 | `utils/request.ts` — 请求/响应拦截器 |
| 异步状态管理 | Store 中的 loading、error 处理 |
| 路由守卫 | `router/index.ts` — beforeEach 全局守卫 |
| 表单校验 | Element Plus 表单验证规则 |
| Token 认证流程 | 登录 → 存 token → 请求带 token → 拦截器校验 |
| Store 异步改造 | action 从同步改为 async/await |
| JWT 原理 | 生成、解析、过期处理 |
| BCrypt 密码加密 | 单向加密、登录校验 |
| 拦截器模式 | Spring MVC HandlerInterceptor |
| ThreadLocal | UserContext 存储当前请求用户 |
