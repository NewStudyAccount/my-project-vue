import httpRequest from '@/utils/request'
import type { Note, NoteForm, NotePageResult } from '@/types/api'

// 获取笔记列表
export function getNotesApi(params: {
  keyword?: string
  categoryId?: string
  page?: number
  pageSize?: number
}) {
  return httpRequest<NotePageResult>({
    url: '/notes',
    params,
  })
}

// 获取单条笔记
export function getNoteApi(id: string) {
  return httpRequest<Note>({
    url: `/notes/${id}`,
  })
}

// 创建笔记
export function createNoteApi(data: NoteForm) {
  return httpRequest<Note>({
    url: '/notes',
    method: 'post',
    data,
  })
}

// 更新笔记
export function updateNoteApi(id: string, data: NoteForm) {
  return httpRequest<Note>({
    url: `/notes/${id}`,
    method: 'put',
    data,
  })
}

// 删除笔记
export function deleteNoteApi(id: string) {
  return httpRequest<null>({
    url: `/notes/${id}`,
    method: 'delete',
  })
}
