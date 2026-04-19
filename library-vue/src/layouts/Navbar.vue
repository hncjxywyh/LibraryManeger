<template>
  <div class="navbar-container">
    <div class="left">
      <span class="username">欢迎，{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
    </div>
    <div class="right">
      <el-badge :value="userStore.unreadMessageCount" :hidden="userStore.unreadMessageCount === 0" :max="99">
        <el-icon :size="22" style="margin-right: 20px; cursor: pointer" @click="$router.push('/messages')">
          <Bell />
        </el-icon>
      </el-badge>
      <el-dropdown @command="handleCommand">
        <span class="user-dropdown">
          <el-avatar :size="32" style="margin-right: 8px">
            {{ userStore.userInfo.realName?.charAt(0) || 'U' }}
          </el-avatar>
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
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { Bell, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  if (userStore.token) {
    userStore.fetchUnreadMessageCount()
  }
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
}

.username {
  color: var(--color-text-primary);
  font-size: 15px;
  font-weight: 500;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: background-color var(--transition-fast);
}

.user-dropdown:hover {
  background: var(--color-border-light);
}

.user-dropdown :deep(.el-avatar) {
  background: var(--color-primary);
  color: #fff;
  font-weight: 600;
}
</style>
