<script setup lang="ts">
import { useSettingsStore } from '../stores/settings'

const settingsStore = useSettingsStore()
</script>

<template>
  <div class="settings-view">
    <h1>设置</h1>

    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>主题设置</span>
        </div>
      </template>

      <el-form label-position="left" label-width="100px">
        <el-form-item label="主题模式">
          <el-switch
            v-model="settingsStore.theme"
            active-value="dark"
            inactive-value="light"
            active-text="深色"
            inactive-text="浅色"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>数据管理</span>
        </div>
      </template>
      <p class="info-text">笔记和分类数据存储在浏览器 localStorage 中。</p>
      <el-button type="danger" @click="() => {
        localStorage.clear()
        location.reload()
      }">
        清除所有数据
      </el-button>
    </el-card>
  </div>
</template>

<style scoped>
.settings-view {
  max-width: 600px;
}

h1 {
  margin-bottom: 24px;
  color: #303133;
}

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
}

.info-text {
  color: #606266;
  margin-bottom: 16px;
}
</style>
