import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Note {
  id: string
  title: string
  content: string
  categoryId: string | null
  createdAt: number
  updatedAt: number
}

const STORAGE_KEY = 'vue3-learning-notes'

function loadNotes(): Note[] {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored) {
    return JSON.parse(stored)
  }
  // 默认示例数据
  return [
    {
      id: '1',
      title: '欢迎使用 Vue 3 学习笔记',
      content: '这是一个用于学习 Vue 3 的笔记应用。你可以在这里记录学习过程中的各种知识点。\n\n支持的功能：\n- 创建、编辑、删除笔记\n- 按分类管理笔记\n- 搜索笔记内容\n- 切换明暗主题',
      categoryId: null,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      id: '2',
      title: 'Composition API 基础',
      content: 'Vue 3 的 Composition API 提供了更灵活的代码组织方式。\n\n核心概念：\n- ref: 创建基本类型的响应式数据\n- reactive: 创建对象类型的响应式数据\n- computed: 计算属性\n- watch: 侦听器\n\n与 Options API 相比，Composition API 可以更好地复用逻辑代码。',
      categoryId: '1',
      createdAt: Date.now() - 86400000,
      updatedAt: Date.now() - 86400000,
    },
  ]
}

export const useNotesStore = defineStore('notes', () => {
  const notes = ref<Note[]>(loadNotes())

  // 持久化到 localStorage
  function persist() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notes.value))
  }

  // getters
  const getNotesByCategory = computed(() => {
    return (categoryId: string | null) => {
      if (categoryId === null) {
        return notes.value.filter((n) => n.categoryId === null)
      }
      return notes.value.filter((n) => n.categoryId === categoryId)
    }
  })

  function getNoteById(id: string): Note | undefined {
    return notes.value.find((n) => n.id === id)
  }

  // actions
  function addNote(note: Omit<Note, 'id' | 'createdAt' | 'updatedAt'>) {
    const newNote: Note = {
      ...note,
      id: Date.now().toString(),
      createdAt: Date.now(),
      updatedAt: Date.now(),
    }
    notes.value.unshift(newNote)
    persist()
    return newNote
  }

  function updateNote(id: string, updates: Partial<Omit<Note, 'id' | 'createdAt'>>) {
    const index = notes.value.findIndex((n) => n.id === id)
    if (index !== -1) {
      notes.value[index] = {
        ...notes.value[index],
        ...updates,
        updatedAt: Date.now(),
      }
      persist()
    }
  }

  function deleteNote(id: string) {
    notes.value = notes.value.filter((n) => n.id !== id)
    persist()
  }

  return {
    notes,
    getNotesByCategory,
    getNoteById,
    addNote,
    updateNote,
    deleteNote,
  }
})
