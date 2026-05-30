import { ref, computed } from 'vue'
import type { Ref } from 'vue'

export function useSearch<T>(
  source: Ref<T[]>,
  searchFields: (keyof T)[]
) {
  const searchQuery = ref('')

  const filteredResults = computed(() => {
    const query = searchQuery.value.toLowerCase().trim()
    if (!query) {
      return source.value
    }

    return source.value.filter((item) =>
      searchFields.some((field) => {
        const value = item[field]
        if (typeof value === 'string') {
          return value.toLowerCase().includes(query)
        }
        return false
      })
    )
  })

  return {
    searchQuery,
    filteredResults,
  }
}
