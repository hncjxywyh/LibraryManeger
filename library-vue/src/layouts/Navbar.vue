<template>
  <div class="navbar-container">
    <div class="left">
      <div class="welcome-section">
        <span class="greeting">书友</span>
        <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
      </div>
      <div class="time-decoration">
        <span class="date">{{ currentDate }}</span>
      </div>
    </div>
    <div class="right">
      <el-badge :value="userStore.unreadMessageCount" :hidden="userStore.unreadMessageCount === 0" :max="99">
        <el-icon :size="22" style="margin-right: 20px; cursor: pointer" @click="$router.push('/messages')" class="notification-icon">
          <Bell />
        </el-icon>
      </el-badge>
      <el-dropdown @command="handleCommand">
        <span class="user-dropdown">
          <div class="avatar-wrapper">
            <el-avatar :size="32">
              {{ userStore.userInfo.realName?.charAt(0) || 'U' }}
            </el-avatar>
          </div>
          <span class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { Bell, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const currentDate = ref('')

const updateDate = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const day = now.getDate()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[now.getDay()]
  currentDate.value = `${year}年${month}月${day}日 星期${weekday}`
}

let timer = null

onMounted(() => {
  updateDate()
  timer = setInterval(updateDate, 60000)
  if (userStore.token) {
    userStore.fetchUnreadMessageCount()
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.navbar-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--color-navbar-bg);
  padding: 0 var(--spacing-lg);
  height: 64px;
}

.left {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.welcome-section {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
}

.greeting {
  font-family: var(--font-family);
  font-size: 14px;
  color: var(--color-text-secondary);
}

.username {
  font-family: var(--font-family-heading);
  font-size: 18px;
  color: var(--color-text-primary);
}

.time-decoration {
  padding-left: var(--spacing-md);
  border-left: 1px solid var(--color-border);
}

.date {
  font-family: var(--font-family);
  font-size: 13px;
  color: var(--color-text-placeholder);
}

.notification-icon {
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
}

.notification-icon:hover {
  color: var(--color-primary);
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: background-color var(--transition-fast);
  gap: var(--spacing-sm);
}

.user-dropdown:hover {
  background: var(--color-border-light);
}

.avatar-wrapper :deep(.el-avatar) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: #fff;
  font-family: var(--font-family-heading);
  font-weight: 500;
}

.user-name {
  font-family: var(--font-family);
  font-size: 14px;
  color: var(--color-text-primary);
}
</style>
