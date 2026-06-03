import httpRequest from '@/utils/request'
import type { LoginData, UserInfo } from '@/types/api'

// 注册
export function registerApi(username: string, password: string) {
  return httpRequest<null>({
    url: '/auth/register',
    method: 'post',
    data: { username, password },
  })
}

// 登录
export function loginApi(username: string, password: string) {
  return httpRequest<LoginData>({
    url: '/auth/login',
    method: 'post',
    data: { username, password },
  })
}

// 获取当前用户信息
export function getCurrentUserApi() {
  return httpRequest<UserInfo>({
    url: '/auth/me',
  })
}
