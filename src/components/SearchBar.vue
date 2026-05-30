<script setup lang="ts">
import { useCategoryStore } from '../stores/categories'

defineProps<{
  modelValue: string
  categoryFilter: string | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'update:categoryFilter': [value: string | null]
}>()

const categoryStore = useCategoryStore()
</script>

<template>
  <div class="search-bar">
    <el-input
      :model-value="modelValue"
      placeholder="搜索笔记..."
      clearable
      class="search-input"
      @update:model-value="emit('update:modelValue', $event)"
    >
      <template #prefix>
        <el-icon><Search /></el-icon>
      </template>
    </el-input>

    <el-select
      :model-value="categoryFilter"
      placeholder="选择分类"
      clearable
      class="category-select"
      @update:model-value="emit('update:categoryFilter', $event)"
    >
      <el-option
        v-for="cat in categoryStore.categories"
        :key="cat.id"
        :label="cat.name"
        :value="cat.id"
      />
    </el-select>
  </div>
</template>

<style scoped>
.search-bar {
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
</style>
