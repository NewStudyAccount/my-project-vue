<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useNotesStore } from '../stores/notes'
import { useCategoryStore } from '../stores/categories'

const router = useRouter()
const route = useRoute()
const notesStore = useNotesStore()
const categoryStore = useCategoryStore()

const noteId = computed(() => route.params.id as string)
const note = computed(() => notesStore.getNoteById(noteId.value))

const categoryName = computed(() => {
  if (!note.value) return '未分类'
  return categoryStore.getCategoryName(note.value.categoryId)
})

function formatDate(timestamp: number): string {
  return new Date(timestamp).toLocaleString('zh-CN')
}

function handleEdit() {
  router.push(`/note/${noteId.value}/edit`)
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？删除后无法恢复。', '确认删除', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    notesStore.deleteNote(noteId.value)
    ElMessage.success('笔记已删除')
    router.push('/notes')
  } catch {
    // 用户取消删除
  }
}
</script>

<template>
  <div class="note-detail-view" v-if="note">
    <div class="page-header">
      <el-button @click="$router.push('/notes')">返回列表</el-button>
      <div class="actions">
        <el-button type="primary" @click="handleEdit">编辑</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </div>
    </div>

    <el-card>
      <template #header>
        <div class="note-header">
          <h1>{{ note.title }}</h1>
          <el-tag>{{ categoryName }}</el-tag>
        </div>
      </template>

      <div class="note-content">{{ note.content }}</div>

      <div class="note-meta">
        <span>创建时间：{{ formatDate(note.createdAt) }}</span>
        <span>更新时间：{{ formatDate(note.updatedAt) }}</span>
      </div>
    </el-card>
  </div>

  <div v-else class="not-found">
    <el-empty description="笔记不存在" />
    <el-button @click="$router.push('/notes')">返回笔记列表</el-button>
  </div>
</template>

<style scoped>
.note-detail-view {
  max-width: 800px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.actions {
  display: flex;
  gap: 8px;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-header h1 {
  margin: 0;
  color: #303133;
  flex: 1;
  margin-right: 16px;
}

.note-content {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
}

.note-meta {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 24px;
  color: #909399;
  font-size: 14px;
}

.not-found {
  text-align: center;
  padding: 40px;
}
</style>
