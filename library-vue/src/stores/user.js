import { defineStore } from 'pinia'
import { auth, message as messageApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    unreadMessageCount: 0
  }),

  getters: {
    isAdmin: (state) => state.userInfo.role === 1,
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(loginForm) {
      const res = await auth.login(loginForm)
      this.token = res.data.token
      localStorage.setItem('token', this.token)
      await this.getInfo()
    },

    async getInfo() {
      const res = await auth.getInfo()
      this.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    async register(registerForm) {
      await auth.register(registerForm)
    },

    logout() {
      this.token = ''
      this.userInfo = {}
      this.unreadMessageCount = 0
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },

    async fetchUnreadMessageCount() {
      if (!this.token) return 0
      try {
        const res = await messageApi.getUnreadCount()
        this.unreadMessageCount = res.data || 0
        return this.unreadMessageCount
      } catch (e) {
        return 0
      }
    }
  }
})