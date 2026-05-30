# 01 - 项目搭建与环境配置

## 项目初始化

使用 `create-vue` 脚手架创建项目：

```bash
npm create vue@latest my-project-vue
```

选择以下配置：
- TypeScript: ✅
- Vue Router: ✅
- Pinia: ✅

## 安装 Element Plus

```bash
npm install element-plus
```

在 `main.ts` 中全量引入：

```typescript
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

app.use(ElementPlus)
```

## 项目结构

```
src/
├── assets/          # 静态资源（CSS、图片）
├── components/      # 公共组件
├── composables/     # 自定义组合式函数
├── router/          # 路由配置
├── stores/          # Pinia 状态管理
├── views/           # 页面视图
├── App.vue          # 根组件
└── main.ts          # 入口文件
```

## Vue 2 对比

| 方面 | Vue 2 | Vue 3 |
|------|-------|-------|
| 脚手架 | Vue CLI (`vue create`) | Vite (`npm create vue@latest`) |
| 构建工具 | Webpack | Vite |
| 语言 | JavaScript（可选 TS） | TypeScript（默认支持） |
| UI 库 | Element UI | Element Plus |
