<script setup lang="ts">
import { useNotesStore } from '../stores/notes'
import { useCategoryStore } from '../stores/categories'
import { reactive, ref } from 'vue'


const notesStore = useNotesStore()
const categoryStore = useCategoryStore()





// ❌ reactive 丢失响应
let state = reactive({ count: 0 })

function breakReactivity() {
  state = { count: 999 }  // 数据变了，但页面还是显示 0
  console.log(state.count) // 999 ← JS 层面确实变了
}

// ✅ ref 不会丢
const count = ref(0)

function keepReactivity() {
  count.value++ // 页面更新为 999
}


</script>

<template>
  <div class="home-view">
    <h1>Vue 3 学习笔记</h1>
    <p class="subtitle">系统性学习 Vue 3 生态的笔记应用</p>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>笔记总数</span>
            </div>
          </template>
          <div class="stat-value">{{ notesStore.notes.length }}</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>分类数量</span>
            </div>
          </template>
          <div class="stat-value">{{ categoryStore.categories.length }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="quick-actions">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <el-space>
        <el-button type="primary" @click="$router.push('/note/new')">
          新建笔记
        </el-button>
        <el-button @click="$router.push('/notes')">
          查看笔记列表
        </el-button>
        <el-button @click="$router.push('/categories')">
          管理分类
        </el-button>
      </el-space>
    </el-card>


    <el-card>
      <p> 响应式测试</p>

      <p>state.count: {{ state.count }}</p>
      <button @click="breakReactivity()">点我试试（不会变）</button>

      <p>count: {{ count }}</p>
      <button @click="keepReactivity()">点我（会变）</button>

    </el-card>
  </div>
</template>

<style scoped>
.home-view {
  max-width: 800px;
}

h1 {
  margin-bottom: 8px;
  color: #303133;
}

.subtitle {
  color: #909399;
  margin-bottom: 24px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  margin-top: 20px;
}
</style>
