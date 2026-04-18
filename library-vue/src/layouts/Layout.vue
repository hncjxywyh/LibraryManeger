<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header class="navbar">
        <Navbar />
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: var(--color-sidebar-bg);
  overflow-x: hidden;
}

.navbar {
  background: var(--color-navbar-bg);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  border-bottom: 1px solid var(--color-border-light);
}

.main-content {
  background: var(--color-content-bg);
  padding: var(--spacing-lg);
  overflow-y: auto;
}
</style>