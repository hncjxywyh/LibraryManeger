<template>
  <div class="navbar-container">
    <div class="left">
      <span class="username">欢迎，{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
    </div>
    <div class="right">
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
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

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
}

.username {
  color: #333;
  font-size: 14px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
}
</style>