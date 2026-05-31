# 07 - 需求文档：接入后端 + 用户认证

## 一、项目背景

当前 Vue 3 学习笔记应用的数据全部存储在浏览器 localStorage 中，无后端服务。
本次需求将接入 Java Spring Boot 后端，实现基本的用户名密码认证，并将数据存储迁移到后端数据库。

**重点：学习前端 Vue 代码中如何与后端交互，后端不做安全框架集成，只做最基础的校验。**

---

## 二、技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Element Plus + Pinia + Vue Router |
| 后端 | Java + Spring Boot + MyBatis-Plus（或 JPA） |
| 数据库 | MySQL（或 H2 内存数据库，开发阶段用） |
| 接口风格 | RESTful JSON API |

---

## 三、功能需求

### 3.1 用户认证模块

#### 3.1.1 注册

- 输入：用户名、密码、确认密码
- 校验规则：
  - 用户名：3-20 位，字母数字下划线
  - 密码：6-20 位
  - 两次密码必须一致
  - 用户名不能重复
- 成功后跳转登录页

#### 3.1.2 登录

- 输入：用户名、密码
- 成功后：
  - 后端返回 token（简单 JWT 或自定义 token 字符串）
  - 前端存入 localStorage
  - 跳转首页
- 失败后：提示错误信息

#### 3.1.3 退出登录

- 清除前端 token
- 清除用户状态
- 跳转登录页

#### 3.1.4 路由守卫

- 未登录用户访问需认证的页面 → 重定向到 `/login`
- 已登录用户访问 `/login` → 重定向到首页

---

### 3.2 笔记模块（改造）

将现有 localStorage 存储改为接口请求：

| 操作 | 接口 | 说明 |
|------|------|------|
| 获取笔记列表 | `GET /api/notes` | 支持分页、搜索、分类筛选 |
| 获取单条笔记 | `GET /api/notes/:id` | |
| 创建笔记 | `POST /api/notes` | |
| 更新笔记 | `PUT /api/notes/:id` | |
| 删除笔记 | `DELETE /api/notes/:id` | |

### 3.3 分类模块（改造）

| 操作 | 接口 | 说明 |
|------|------|------|
| 获取分类列表 | `GET /api/categories` | |
| 创建分类 | `POST /api/categories` | |
| 更新分类 | `PUT /api/categories/:id` | |
| 删除分类 | `DELETE /api/categories/:id` | 删除时关联笔记的 categoryId 置空 |

---

## 四、页面规划

### 新增页面

| 页面 | 路由 | 说明 |
|------|------|------|
| 登录 | `/login` | 用户名 + 密码表单 |
| 注册 | `/register` | 用户名 + 密码 + 确认密码表单 |

### 改造页面

| 页面 | 改动 |
|------|------|
| NoteListView | 列表数据从接口获取，支持 loading 状态 |
| NoteDetailView | 详情数据从接口获取 |
| NoteEditView | 保存调用接口，错误处理 |
| CategoryView | CRUD 改为接口调用 |
| HomeView | 统计数据从接口获取 |
| SettingsView | 退出登录功能，移除"清除数据"按钮 |

---

## 五、前端改造要点

### 5.1 新增 Auth Store

```typescript
// stores/auth.ts
interface AuthState {
  token: string | null
  user: { id: string; username: string } | null
}
```

功能：
- `login(username, password)` — 调用登录接口，存 token
- `register(username, password)` — 调用注册接口
- `logout()` — 清除 token，跳转登录页
- `fetchCurrentUser()` — 用 token 获取用户信息
- `isLoggedIn` getter — 判断是否登录

持久化：token 存入 localStorage，user 不存（每次刷新重新获取）。

### 5.2 封装 Axios 请求

```typescript
// utils/request.ts
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器：自动带上 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // token 过期或无效 → 跳登录页
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request
```

### 5.3 Store 改造

以 notes store 为例，从 localStorage 改为接口调用：

```typescript
// 改造前：直接操作 localStorage
function addNote(note) {
  notes.value.push(note)
  persist()
}

// 改造后：调用接口
async function addNote(note) {
  const newNote = await request.post('/notes', note)
  notes.value.push(newNote)
}
```

### 5.4 Loading 与错误处理

所有接口调用需要处理：

| 状态 | 处理方式 |
|------|---------|
| 加载中 | 显示 loading（Element Plus `v-loading`） |
| 请求成功 | 更新 store 数据 |
| 请求失败 | `ElMessage.error()` 提示错误信息 |
| 401 未授权 | 自动跳转登录页 |

---

## 六、后端接口设计

### 6.1 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

错误响应：

```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

### 6.2 认证接口

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
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
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
    "user": {
      "id": "1",
      "username": "zhangsan"
    }
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
  "data": {
    "id": "1",
    "username": "zhangsan"
  }
}
```

### 6.3 笔记接口

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
  "data": {
    "list": [
      {
        "id": "1",
        "title": "Vue 3 入门",
        "content": "...",
        "categoryId": "1",
        "categoryName": "Vue 基础",
        "createdAt": "2026-05-31T10:00:00",
        "updatedAt": "2026-05-31T10:00:00"
      }
    ],
    "total": 25
  }
}
```

#### POST /api/notes

请求：
```json
{
  "title": "新笔记",
  "content": "内容",
  "categoryId": "1"
}
```

#### PUT /api/notes/:id

请求体同 POST。

#### DELETE /api/notes/:id

### 6.4 分类接口

#### GET /api/categories

#### POST /api/categories

请求：
```json
{
  "name": "新分类",
  "color": "#409EFF"
}
```

#### PUT /api/categories/:id

#### DELETE /api/categories/:id

---

## 七、数据库设计

### 用户表 (users)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(255) | 密码（BCrypt 加密存储） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 分类表 (categories)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(50) | 分类名称 |
| color | VARCHAR(20) | 颜色值 |
| user_id | BIGINT | 所属用户 |
| created_at | DATETIME | 创建时间 |

### 笔记表 (notes)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(200) | 标题 |
| content | TEXT | 内容 |
| category_id | BIGINT | 分类 ID，可为空 |
| user_id | BIGINT | 所属用户 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

---

## 八、开发计划

### 阶段一：后端基础

- [ ] 搭建 Spring Boot 项目
- [ ] 设计数据库表，初始化数据
- [ ] 实现注册/登录接口
- [ ] 实现简单的 token 生成与校验
- [ ] 实现笔记 CRUD 接口
- [ ] 实现分类 CRUD 接口

### 阶段二：前端改造

- [ ] 封装 Axios 请求工具（拦截器、错误处理）
- [ ] 新增 Auth Store（登录、注册、退出）
- [ ] 新增登录页、注册页
- [ ] 添加路由守卫
- [ ] 改造 Notes Store → 接口调用
- [ ] 改造 Categories Store → 接口调用
- [ ] 各页面添加 loading 状态和错误处理
- [ ] 移除 localStorage 手动持久化逻辑

### 阶段三：联调优化

- [ ] 前后端联调测试
- [ ] 处理边界情况（网络错误、token 过期等）
- [ ] 代码整理和注释

---

## 九、学习重点

本次需求重点学习以下前端知识点：

| 知识点 | 在哪学 |
|--------|--------|
| Axios 封装 | 请求/响应拦截器 |
| 异步状态管理 | loading、error 处理 |
| 路由守卫 | beforeEach 全局守卫 |
| 表单校验 | Element Plus 表单验证 |
| Token 认证流程 | 登录 → 存 token → 请求带 token → 校验 |
| Store 异步改造 | action 改为 async/await |
