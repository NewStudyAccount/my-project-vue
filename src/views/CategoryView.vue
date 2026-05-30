<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useCategoryStore } from '../stores/categories'
import { useNotesStore } from '../stores/notes'

const router = useRouter()
const categoryStore = useCategoryStore()
const notesStore = useNotesStore()

const showDialog = ref(false)
const editingCategory = ref<{ id?: string; name: string; color: string }>({
  name: '',
  color: '#409eff',
})
const isEditing = ref(false)

function getCategoryNoteCount(categoryId: string): number {
  return notesStore.getNotesByCategory(categoryId).length
}

function openCreateDialog() {
  editingCategory.value = { name: '', color: '#409eff' }
  isEditing.value = false
  showDialog.value = true
}

function openEditDialog(category: { id: string; name: string; color: string }) {
  editingCategory.value = { ...category }
  isEditing.value = true
  showDialog.value = true
}

function handleSave() {
  if (!editingCategory.value.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }

  if (isEditing.value && editingCategory.value.id) {
    categoryStore.updateCategory(editingCategory.value.id, {
      name: editingCategory.value.name.trim(),
      color: editingCategory.value.color,
    })
    ElMessage.success('分类已更新')
  } else {
    categoryStore.addCategory({
      name: editingCategory.value.name.trim(),
      color: editingCategory.value.color,
    })
    ElMessage.success('分类已创建')
  }

  showDialog.value = false
}

async function handleDelete(category: { id: string; name: string }) {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.name}"吗？该分类下的笔记将变为未分类状态。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    categoryStore.deleteCategory(category.id)
    ElMessage.success('分类已删除')
  } catch {
    // 用户取消
  }
}

function viewCategoryNotes(categoryId: string) {
  router.push(`/category/${categoryId}/notes`)
}
</script>

<template>
  <div class="category-view">
    <div class="page-header">
      <h1>分类管理</h1>
      <el-button type="primary" @click="openCreateDialog">
        新建分类
      </el-button>
    </div>

    <div v-if="categoryStore.categories.length > 0" class="category-grid">
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
            <div class="category-actions">
              <el-button size="small" @click="viewCategoryNotes(category.id)">
                查看笔记
              </el-button>
              <el-button size="small" @click="openEditDialog(category)">
                编辑
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="handleDelete(category)"
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

    <el-empty v-else description="暂无分类，点击新建" />

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="showDialog"
      :title="isEditing ? '编辑分类' : '新建分类'"
      width="400px"
    >
      <el-form label-position="top">
        <el-form-item label="分类名称">
          <el-input
            v-model="editingCategory.name"
            placeholder="请输入分类名称"
          />
        </el-form-item>
        <el-form-item label="颜色标识">
          <el-color-picker v-model="editingCategory.color" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 嵌套路由：分类下的笔记 -->
    <router-view />
  </div>
</template>

<style scoped>
.category-view {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

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
