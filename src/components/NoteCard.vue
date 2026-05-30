<script setup lang="ts">
import { computed } from 'vue'
import { useCategoryStore } from '../stores/categories'

interface Note {
  id: string
  title: string
  content: string
  categoryId: string | null
  createdAt: number
  updatedAt: number
}

const props = defineProps<{
  note: Note
}>()

const emit = defineEmits<{
  click: [id: string]
}>()

const categoryStore = useCategoryStore()

const categoryName = computed(() => categoryStore.getCategoryName(props.note.categoryId))

const summary = computed(() => {
  const content = props.note.content
  return content.length > 100 ? content.substring(0, 100) + '...' : content
})

function formatDate(timestamp: number): string {
  return new Date(timestamp).toLocaleDateString('zh-CN')
}
</script>

<template>
  <el-card shadow="hover" class="note-card" @click="emit('click', note.id)">
    <template #header>
      <div class="note-card-header">
        <span class="note-title">{{ note.title }}</span>
        <el-tag size="small" :type="note.categoryId ? '' : 'info'">
          {{ categoryName }}
        </el-tag>
      </div>
    </template>
    <p class="note-summary">{{ summary }}</p>
    <div class="note-meta">
      <span class="note-date">{{ formatDate(note.createdAt) }}</span>
    </div>
  </el-card>
</template>

<style scoped>
.note-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.note-card:hover {
  transform: translateY(-2px);
}

.note-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-title {
  font-weight: bold;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.note-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.note-meta {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
