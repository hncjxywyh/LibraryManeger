<template>
  <router-view />
</template>

<script setup>
</script>

<style>
@import './styles/variables.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-family-body);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--color-text-primary);
  background-color: var(--color-content-bg);
  line-height: 1.6;
}

#app {
  min-height: 100vh;
}

/* 全局Element Plus覆盖样式 */
.el-button {
  transition: all var(--transition-fast);
  font-family: var(--font-family);
}

.el-input__wrapper,
.el-textarea__inner {
  transition: box-shadow var(--transition-fast), border-color var(--transition-fast);
  font-family: var(--font-family);
}

/* 古典对话框样式 */
.el-dialog__header {
  font-family: var(--font-family-heading);
}

/* 古典表格样式 */
.el-table {
  font-family: var(--font-family);
}

/* 古典消息提示 */
.el-message {
  font-family: var(--font-family);
}

/* 古典下拉菜单 */
.el-dropdown-menu__item {
  font-family: var(--font-family);
}

/* 古典标签 */
.el-tag {
  font-family: var(--font-family);
}

/* 古典分页 */
.el-pagination {
  font-family: var(--font-family);
}

/* 古典描述列表 */
.el-descriptions__label,
.el-descriptions__content {
  font-family: var(--font-family);
}

/* 古典表单标签 */
.el-form-item__label {
  font-family: var(--font-family);
}
</style>
