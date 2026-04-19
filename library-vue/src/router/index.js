import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layouts/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/books',
        name: 'Books',
        component: () => import('@/views/books/index.vue'),
        meta: { title: '图书列表' }
      },
      {
        path: '/borrows',
        name: 'Borrows',
        component: () => import('@/views/borrows/index.vue'),
        meta: { title: '我的借阅' }
      },
      {
        path: '/categories',
        name: 'Categories',
        component: () => import('@/views/categories/index.vue'),
        meta: { title: '分类管理', requiresAdmin: true }
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('@/views/users/index.vue'),
        meta: { title: '读者管理', requiresAdmin: true }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: '/messages',
        name: 'Messages',
        component: () => import('@/views/messages/index.vue'),
        meta: { title: '我的消息' }
      },
      {
        path: '/reservations',
        name: 'Reservations',
        component: () => import('@/views/reservations/index.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: '/donations',
        name: 'Donations',
        component: () => import('@/views/donations/index.vue'),
        meta: { title: '图书捐赠' }
      },
      {
        path: '/admin/donations',
        name: 'AdminDonations',
        component: () => import('@/views/admin/donations.vue'),
        meta: { title: '捐赠审核', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title + ' - 图书管理系统' || '图书管理系统'

  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userInfo.role !== 1) {
    next('/home')
  } else {
    next()
  }
})

export default router