<template>
  <div class="sidebar-container">
    <!-- 顶部装饰 -->
    <div class="sidebar-header">
      <div class="logo-area">
        <div class="seal-logo">阁</div>
        <div class="logo-text">
          <h2>藏书阁</h2>
          <p>Library</p>
        </div>
      </div>
      <div class="header-decoration"></div>
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

      <el-menu-item index="/reservations">
        <el-icon><Clock /></el-icon>
        <span>我的预约</span>
      </el-menu-item>

      <el-menu-item index="/donations">
        <el-icon><Present /></el-icon>
        <span>图书捐赠</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/categories">
        <el-icon><FolderOpened /></el-icon>
        <span>分类管理</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/users">
        <el-icon><User /></el-icon>
        <span>读者管理</span>
      </el-menu-item>

      <el-menu-item v-if="userStore.isAdmin" index="/admin/donations">
        <el-icon><Tickets /></el-icon>
        <span>捐赠审核</span>
      </el-menu-item>

      <el-menu-item index="/profile">
        <el-icon><Avatar /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
    </el-menu>

    <!-- 底部装饰 -->
    <div class="sidebar-footer">
      <div class="footer-decoration"></div>
      <p class="footer-poem">腹有诗书气自华</p>
    </div>
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
  background: var(--color-sidebar-bg);
  position: relative;
}

/* 顶部区域 */
.sidebar-header {
  padding: var(--spacing-lg);
  text-align: center;
  position: relative;
}

.logo-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
}

.seal-logo {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-danger);
  color: #fff;
  font-family: var(--font-family-heading);
  font-size: 22px;
  border-radius: 4px;
  transform: rotate(-5deg);
  box-shadow: 0 3px 10px rgba(139, 0, 0, 0.3);
}

.logo-text h2 {
  color: var(--color-sidebar-text);
  font-family: var(--font-family-heading);
  font-size: 20px;
  margin: 0;
  letter-spacing: 4px;
}

.logo-text p {
  color: var(--color-text-placeholder);
  font-family: var(--font-family);
  font-size: 10px;
  margin: 2px 0 0 0;
  letter-spacing: 2px;
}

.header-decoration {
  width: 60%;
  height: 1px;
  background: linear-gradient(to right, transparent, var(--color-primary), transparent);
  margin: var(--spacing-md) auto 0;
  opacity: 0.5;
}

/* 菜单样式 */
.sidebar-menu {
  border: none;
  flex: 1;
  padding: 0 var(--spacing-sm);
}

.sidebar-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 4px 0;
  padding-left: var(--spacing-lg) !important;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
  position: relative;
  cursor: pointer;
  font-family: var(--font-family);
  letter-spacing: 1px;
}

.sidebar-menu :deep(.el-menu-item)::before {
  content: '';
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  background: var(--color-sidebar-text);
  border-radius: 50%;
  opacity: 0;
  transition: all var(--transition-fast);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: var(--color-sidebar-hover) !important;
}

.sidebar-menu :deep(.el-menu-item:hover)::before {
  opacity: 0.5;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(199, 84, 74, 0.2) 0%, transparent 100%) !important;
  border-left: 3px solid var(--color-sidebar-active);
  padding-left: calc(var(--spacing-lg) - 3px) !important;
  color: #fff !important;
}

.sidebar-menu :deep(.el-menu-item.is-active)::before {
  background: var(--color-sidebar-active);
  opacity: 1;
}

.sidebar-menu :deep(.el-menu-item.is-active) .el-icon {
  color: var(--color-sidebar-active);
}

/* 底部装饰 */
.sidebar-footer {
  padding: var(--spacing-lg);
  text-align: center;
  position: relative;
}

.footer-decoration {
  width: 80%;
  height: 1px;
  background: linear-gradient(to right, transparent, rgba(139, 69, 19, 0.3), transparent);
  margin: 0 auto var(--spacing-md);
}

.footer-poem {
  font-family: var(--font-family);
  font-size: 12px;
  color: var(--color-text-placeholder);
  margin: 0;
  letter-spacing: 2px;
}
</style>
