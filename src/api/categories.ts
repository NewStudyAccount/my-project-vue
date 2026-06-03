import httpRequest from '@/utils/request'
import type { Category, CategoryForm } from '@/types/api'

// 获取分类列表
export function getCategoriesApi() {
  return httpRequest<Category[]>({
    url: '/categories',
  })
}

// 创建分类
export function createCategoryApi(data: CategoryForm) {
  return httpRequest<Category>({
    url: '/categories',
    method: 'post',
    data,
  })
}

// 更新分类
export function updateCategoryApi(id: string, data: CategoryForm) {
  return httpRequest<Category>({
    url: `/categories/${id}`,
    method: 'put',
    data,
  })
}

// 删除分类
export function deleteCategoryApi(id: string) {
  return httpRequest<null>({
    url: `/categories/${id}`,
    method: 'delete',
  })
}
