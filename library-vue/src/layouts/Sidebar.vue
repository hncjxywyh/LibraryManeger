<template>
  <div class="sidebar-container">
    <div class="logo">
      <h2>图书管理</h2>
    </div>
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      :router="true"
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
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2b3a4a;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.el-menu {
  border: none;
}
</style>