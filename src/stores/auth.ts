import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, registerApi, getCurrentUserApi } from '@/api/auth'
import type { UserInfo } from '@/types/api'

const TOKEN_KEY = 'token'

export const useAuthStore = defineStore('auth', () => {
  // ============ State ============
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const user = ref<UserInfo | null>(null)

  // ============ Getters ============
  const isLoggedIn = computed(() => !!token.value)

  // ============ Actions ============

  /**
   * 登录
   */
  async function login(username: string, password: string) {
    const data = await loginApi(username, password)
    // 存 token
    token.value = data.token
    localStorage.setItem(TOKEN_KEY, data.token)
    // 存 user
    user.value = data.user
  }

  /**
   * 注册
   */
  async function register(username: string, password: string) {
    await registerApi(username, password)
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  /**
   * 获取当前用户信息（页面刷新时调用）
   */
  async function fetchCurrentUser() {
    try {
      const data = await getCurrentUserApi()
      user.value = data
    } catch {
      // token 无效，清除登录状态
      token.value = null
      user.value = null
      localStorage.removeItem(TOKEN_KEY)
    }
  }

  return {
    token,
    user,
    isLoggedIn,
    login,
    register,
    logout,
    fetchCurrentUser,
  }
})
