<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-family);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--color-text-primary);
  background-color: var(--color-content-bg);
}

#app {
  min-height: 100vh;
}

/* 全局Element Plus覆盖样式 */
.el-button {
  transition: all var(--transition-fast);
}

.el-button:hover {
  opacity: 0.9;
}

.el-input__wrapper,
.el-textarea__inner {
  transition: box-shadow var(--transition-fast), border-color var(--transition-fast);
}
</style>