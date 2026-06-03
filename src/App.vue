<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import { computed, provide, onMounted } from 'vue'
import { useSettingsStore } from './stores/settings'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const settingsStore = useSettingsStore()
const authStore = useAuthStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/') return '/'
  if (path.startsWith('/notes') || path.startsWith('/note')) return '/notes'
  if (path.startsWith('/category')) return '/categories'
  if (path.startsWith('/settings')) return '/settings'
  return path
})

const isDark = computed(() => settingsStore.theme === 'dark')
provide('theme', isDark)

// 登录/注册页用全屏布局
const isFullPage = computed(() =>
  route.path === '/login' || route.path === '/register'
)

// 应用启动时，如果有 token 就恢复用户信息
onMounted(async () => {
  if (authStore.isLoggedIn) {
    await authStore.fetchCurrentUser()
  }
})
</script>

<template>
  <!-- 登录/注册页面：全屏显示，无侧边栏 -->
  <div v-if="isFullPage" class="login-layout" :class="{ 'is-dark': isDark }">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>

  <!-- 主应用布局：带侧边栏 -->
  <el-container v-else class="app-container" :class="{ 'is-dark': isDark }">
    <el-aside width="240px" class="app-aside">
      <div class="app-logo">
        <h2>Vue 3 学习笔记</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        :router="true"
        class="app-menu"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="/notes">
          <el-icon><Document /></el-icon>
          <template #title>笔记列表</template>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Folder /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <template #title>设置</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<style scoped>
.login-layout {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.login-layout.is-dark {
  background-color: #141414;
}

.app-container {
  height: 100vh;
}

.app-aside {
  background-color: #304156;
  color: #bfcbd9;
  overflow: hidden;
}

.app-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  border-bottom: 1px solid #3d4f65;
}

.app-logo h2 {
  margin: 0;
  font-size: 18px;
}

.app-menu {
  border-right: none;
  background-color: #304156;
}

.app-menu .el-menu-item {
  color: #bfcbd9;
}

.app-menu .el-menu-item:hover {
  background-color: #263445;
}

.app-menu .el-menu-item.is-active {
  color: #409eff;
  background-color: #263445;
}

.app-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 暗色主题 */
.is-dark .app-aside {
  background-color: #1d1e1f;
}

.is-dark .app-logo {
  border-bottom-color: #333;
}

.is-dark .app-menu {
  background-color: #1d1e1f;
}

.is-dark .app-menu .el-menu-item {
  color: #bfcbd9;
}

.is-dark .app-menu .el-menu-item:hover {
  background-color: #2d2d2d;
}

.is-dark .app-main {
  background-color: #141414;
}
</style>
