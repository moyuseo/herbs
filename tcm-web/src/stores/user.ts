import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserProfile } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<Record<string, any> | null>(null)

  async function login(phone: string, code: string) {
    const res = await loginApi(phone, code)
    token.value = res.token
    localStorage.setItem('token', res.token)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  async function fetchProfile() {
    const res = await getUserProfile()
    userInfo.value = res
  }

  return {
    token,
    userInfo,
    login,
    logout,
    fetchProfile,
  }
})
