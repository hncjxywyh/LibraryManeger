<template>
  <div class="users-container">
    <h1 class="page-title">读者管理</h1>

    <el-card>
      <el-table :data="users" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { user } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const users = ref([])

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await user.list()
    users.value = res.data.records
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await user.update(row.id, { status: newStatus })
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.users-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
}

.users-container :deep(.el-table th) {
  background-color: var(--color-content-bg) !important;
  font-weight: 600;
  color: var(--color-text-primary);
}

.users-container :deep(.el-table td) {
  border-bottom-color: var(--color-border-light);
}

.users-container :deep(.el-button) {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.users-container :deep(.el-button:hover) {
  opacity: 0.85;
}
</style>