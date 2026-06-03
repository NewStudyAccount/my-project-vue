import axios from 'axios'
import type { AxiosRequestConfig, Method } from 'axios'
import { ElMessage } from 'element-plus'

// ============ axios 实例 ============
const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// ============ 请求拦截器 ============
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ============ 响应拦截器 ============
service.interceptors.response.use(
  response => {
    const res = response.data

    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res.data
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求地址不存在')
          break
        case 500:
          ElMessage.error(data?.message || '服务器错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// ============ 配置式请求函数 ============
export interface RequestConfig {
  url: string
  method?: Method
  params?: Record<string, any>
  data?: Record<string, any>
  headers?: Record<string, string>
}

function httpRequest<T = any>(config: RequestConfig): Promise<T> {
  return service({
    url: config.url,
    method: config.method || 'get',
    params: config.params,
    data: config.data,
    headers: config.headers,
  })
}

export default httpRequest

