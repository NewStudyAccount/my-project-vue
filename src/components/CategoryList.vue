<script setup lang="ts">
import { useCategoryStore } from '../stores/categories'
import { useNotesStore } from '../stores/notes'

defineProps<{
  showActions?: boolean
}>()

const emit = defineEmits<{
  edit: [category: { id: string; name: string; color: string }]
  delete: [category: { id: string; name: string }]
  view: [id: string]
}>()

const categoryStore = useCategoryStore()
const notesStore = useNotesStore()

function getCategoryNoteCount(categoryId: string): number {
  return notesStore.getNotesByCategory(categoryId).length
}
</script>

<template>
  <div class="category-list">
    <el-empty v-if="categoryStore.categories.length === 0" description="暂无分类" />

    <div v-else class="category-grid">
      <el-card
        v-for="category in categoryStore.categories"
        :key="category.id"
        shadow="hover"
        class="category-card"
      >
        <template #header>
          <div class="category-card-header">
            <div class="category-info">
              <span
                class="color-dot"
                :style="{ backgroundColor: category.color }"
              />
              <span class="category-name">{{ category.name }}</span>
            </div>
            <div v-if="showActions" class="category-actions">
              <el-button size="small" @click="emit('view', category.id)">
                查看笔记
              </el-button>
              <el-button size="small" @click="emit('edit', category)">
                编辑
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="emit('delete', category)"
              >
                删除
              </el-button>
            </div>
          </div>
        </template>
        <div class="category-stats">
          <span class="note-count">{{ getCategoryNoteCount(category.id) }} 篇笔记</span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.category-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.category-name {
  font-weight: bold;
  color: #303133;
}

.category-actions {
  display: flex;
  gap: 4px;
}

.category-stats {
  color: #909399;
  font-size: 14px;
}
</style>
