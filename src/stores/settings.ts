import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export interface Settings {
  theme: 'light' | 'dark'
}

const STORAGE_KEY = 'vue3-learning-settings'

function loadSettings(): Settings {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored) {
    return JSON.parse(stored)
  }
  return {
    theme: 'light',
  }
}

export const useSettingsStore = defineStore('settings', () => {
  const settings = loadSettings()
  const theme = ref<'light' | 'dark'>(settings.theme)

  function persist() {
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({ theme: theme.value })
    )
  }

  function setTheme(newTheme: 'light' | 'dark') {
    theme.value = newTheme
    persist()
  }

  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    persist()
  }

  // 监听主题变化，自动持久化
  watch(theme, () => {
    persist()
  })

  return {
    theme,
    setTheme,
    toggleTheme,
  }
})
