import request from '@/utils/request'

export const auth = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  logout: () => request.post('/auth/logout'),
  getInfo: () => request.get('/auth/info')
}

export const book = {
  list: (params) => request.get('/books', { params }),
  detail: (id) => request.get(`/books/${id}`),
  add: (data) => request.post('/books', data),
  update: (id, data) => request.put(`/books/${id}`, data),
  delete: (id) => request.delete(`/books/${id}`)
}

export const category = {
  list: () => request.get('/categories'),
  add: (data) => request.post('/categories', data),
  update: (id, data) => request.put(`/categories/${id}`, data),
  delete: (id) => request.delete(`/categories/${id}`)
}

export const borrow = {
  list: (params) => request.get('/borrows', { params }),
  borrow: (data) => request.post('/borrows', data),
  return: (id) => request.put(`/borrows/${id}/return`),
  renew: (id) => request.put(`/borrows/${id}/renew`)
}

export const user = {
  list: (params) => request.get('/users', { params }),
  detail: (id) => request.get(`/users/${id}`),
  update: (id, data) => request.put(`/users/${id}`, data),
  changePassword: (id, data) => request.put(`/users/${id}/password`, data),
  delete: (id) => request.delete(`/users/${id}`)
}

export const message = {
  list: (params) => request.get('/messages', { params }),
  getUnreadCount: () => request.get('/messages/unread-count'),
  markRead: (id) => request.put(`/messages/${id}/read`),
  markAllRead: () => request.put('/messages/read-all')
}

export const userStats = {
  get: () => request.get('/user-stats')
}

export const stats = {
  overview: () => request.get('/stats/overview')
}