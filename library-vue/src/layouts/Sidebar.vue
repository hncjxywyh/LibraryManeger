<template>
  <div class="sidebar-container">
    <div class="logo">
      <h2>图书管理</h2>
    </div>
    <el-menu
      :default-active="activeMenu"
      background-color="var(--color-sidebar-bg)"
      text-color="var(--color-sidebar-text)"
      active-text-color="var(--color-sidebar-active)"
      :router="true"
      class="sidebar-menu"
    >
      <el-menu-item index="/home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <el-menu-item index="/books">
        <el-icon><Reading /></el-icon>
        <span>图书列表</span>
      </el-menu-item>

      <el-menu-item index="/borrows">
        <el-icon><List /></el-icon>
        <span>我的借阅</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/categories">
        <el-icon><FolderOpened /></el-icon>
        <span>分类管理</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/users">
        <el-icon><User /></el-icon>
        <span>读者管理</span>
      </el-menu-item>

      <el-menu-item index="/profile">
        <el-icon><Avatar /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.03);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
}

.logo h2 {
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 1px;
}

.sidebar-menu {
  border: none;
  flex: 1;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  padding-left: 16px !important;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  position: relative;
  cursor: pointer;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: var(--color-sidebar-hover) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: var(--color-primary) !important;
  color: #fff !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) .el-icon {
  color: #fff;
}
</style>