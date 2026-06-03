# 接入后端 + 用户认证

## 概述

将当前纯前端 localStorage 存储的 Vue 3 学习笔记应用，改造为前后端分离架构。接入 Java Spring Boot 后端，实现用户认证，数据迁移到 MySQL。

## 动机

- 学习前后端交互的核心模式（API 调用、Token 认证、异步状态管理）
- 当前 localStorage 存储无法持久化、无法多设备同步
- 需要用户体系实现数据隔离

## 范围

### 包含

- Spring Boot 后端项目搭建（在 `backend/` 目录）
- 用户注册、登录、退出登录
- JWT Token 认证（无 Spring Security）
- 笔记 CRUD 接口（分页、搜索、分类筛选）
- 分类 CRUD 接口
- 前端 Axios 封装 + Auth Store
- 前端路由守卫
- 前端 Store 从 localStorage 改为 API 调用
- 登录页重写 + 注册页新建

### 不包含

- Spring Security 集成
- Redis 缓存
- 文件上传
- 多端同步
- 前端单元测试

## 技术选型

| 决策 | 选择 | 理由 |
|------|------|------|
| HTTP 客户端 | Axios | Vue 生态主流，拦截器支持好 |
| 认证方式 | JWT（jjwt） | 无状态，无需 Redis，学习价值高 |
| 密码加密 | BCrypt（jbcrypt） | 轻量独立库，不依赖 Spring Security |
| ORM | MyBatis-Plus | 简化 CRUD，分页插件好用 |
| 数据库 | MySQL | 生产常用，学习价值高 |
| 跨域方案 | Vite proxy | 开发阶段最简单 |

## 风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 前后端联调耗时 | 中 | 先搭后端、逐模块联调 |
| JWT 密钥硬编码 | 低 | 学习项目可接受 |
| 无刷新 token 机制 | 低 | 24h 过期，过期重新登录 |
