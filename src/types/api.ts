// 用户信息
export interface UserInfo {
  id: string
  username: string
}

// 登录响应
export interface LoginData {
  token: string
  user: UserInfo
}

// 笔记
export interface Note {
  id: string
  title: string
  content: string
  categoryId: string | null
  categoryName: string
  createdAt: string
  updatedAt: string
}

// 笔记列表响应（分页）
export interface NotePageResult {
  list: Note[]
  total: number
}

// 笔记创建/更新请求
export interface NoteForm {
  title: string
  content: string
  categoryId: string | null
}

// 分类
export interface Category {
  id: string
  name: string
  color: string
}

// 分类创建/更新请求
export interface CategoryForm {
  name: string
  color: string
}
