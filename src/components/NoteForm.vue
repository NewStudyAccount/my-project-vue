<script setup lang="ts">
import { useCategoryStore } from '../stores/categories'

const props = defineProps<{
  title: string
  content: string
  categoryId: string | null
}>()

const emit = defineEmits<{
  'update:title': [value: string]
  'update:content': [value: string]
  'update:categoryId': [value: string | null]
  save: []
  cancel: []
}>()

const categoryStore = useCategoryStore()

// 表单校验规则
const rules = {
  title: [{ required: true, message: '请输入笔记标题', trigger: 'blur' }],
}
</script>

<template>
  <el-form :rules="rules" label-position="top">
    <el-form-item label="标题" prop="title">
      <el-input
        :model-value="title"
        placeholder="请输入笔记标题"
        @update:model-value="emit('update:title', $event)"
      />
    </el-form-item>

    <el-form-item label="分类">
      <el-select
        :model-value="categoryId"
        placeholder="选择分类（可选）"
        clearable
        @update:model-value="emit('update:categoryId', $event)"
      >
        <el-option
          v-for="cat in categoryStore.categories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="内容">
      <el-input
        :model-value="content"
        type="textarea"
        :rows="12"
        placeholder="请输入笔记内容"
        @update:model-value="emit('update:content', $event)"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="emit('save')">保存</el-button>
      <el-button @click="emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>
