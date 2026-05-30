<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useNotesStore } from '../stores/notes'
import { useCategoryStore } from '../stores/categories'
import NoteCard from '../components/NoteCard.vue'

const router = useRouter()
const notesStore = useNotesStore()
const categoryStore = useCategoryStore()

const searchQuery = ref('')
const selectedCategory = ref<string | null>(null)

const filteredNotes = computed(() => {
  let result = [...notesStore.notes]

  // 按分类筛选
  if (selectedCategory.value !== null) {
    result = result.filter((n) => n.categoryId === selectedCategory.value)
  }

  // 按关键词搜索
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(
      (n) =>
        n.title.toLowerCase().includes(query) ||
        n.content.toLowerCase().includes(query)
    )
  }

  return result
})

function goToNote(id: string) {
  router.push(`/note/${id}`)
}
</script>

<template>
  <div class="note-list-view">
    <div class="page-header">
      <h1>笔记列表</h1>
      <el-button type="primary" @click="$router.push('/note/new')">
        新建笔记
      </el-button>
    </div>

    <div class="filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索笔记..."
        clearable
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="selectedCategory"
        placeholder="选择分类"
        clearable
        class="category-select"
      >
        <el-option
          v-for="cat in categoryStore.categories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
    </div>

    <div v-if="filteredNotes.length > 0" class="notes-grid">
      <!--
        props 往下传：:note="note"
        emit 往上传：@click="goToNote"
      -->
      <NoteCard
        v-for="note in filteredNotes"
        :key="note.id"
        :note="note"
        @click="goToNote"
      />
    </div>

    <el-empty v-else description="暂无笔记，点击新建" />
  </div>
</template>

<style scoped>
.note-list-view {
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

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
}

.category-select {
  width: 200px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
</style>
