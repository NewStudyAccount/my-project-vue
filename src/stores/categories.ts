import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useNotesStore } from './notes'

export interface Category {
  id: string
  name: string
  color: string
}

const STORAGE_KEY = 'vue3-learning-categories'

function loadCategories(): Category[] {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored) {
    return JSON.parse(stored)
  }
  // 默认分类
  return [
    { id: '1', name: 'Vue 基础', color: '#409eff' },
    { id: '2', name: '组件通信', color: '#67c23a' },
    { id: '3', name: '路由管理', color: '#e6a23c' },
    { id: '4', name: '状态管理', color: '#f56c6c' },
  ]
}

export const useCategoryStore = defineStore('categories', () => {
  const categories = ref<Category[]>(loadCategories())

  function persist() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(categories.value))
  }

  function getCategoryById(id: string): Category | undefined {
    return categories.value.find((c) => c.id === id)
  }

  function getCategoryName(id: string | null): string {
    if (id === null) return '未分类'
    const category = categories.value.find((c) => c.id === id)
    return category ? category.name : '未分类'
  }

  function addCategory(category: Omit<Category, 'id'>) {
    const newCategory: Category = {
      ...category,
      id: Date.now().toString(),
    }
    categories.value.push(newCategory)
    persist()
    return newCategory
  }

  function updateCategory(id: string, updates: Partial<Omit<Category, 'id'>>) {
    const index = categories.value.findIndex((c) => c.id === id)
    if (index !== -1) {
      categories.value[index] = {
        ...categories.value[index],
        ...updates,
      }
      persist()
    }
  }

  function deleteCategory(id: string) {
    // 将该分类下的笔记 categoryId 设为 null
    const notesStore = useNotesStore()
    const notesInCategory = notesStore.getNotesByCategory(id)
    notesInCategory.forEach((note) => {
      notesStore.updateNote(note.id, { categoryId: null })
    })

    categories.value = categories.value.filter((c) => c.id !== id)
    persist()
  }

  return {
    categories,
    getCategoryById,
    getCategoryName,
    addCategory,
    updateCategory,
    deleteCategory,
  }
})
