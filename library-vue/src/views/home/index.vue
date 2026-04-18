<template>
  <div class="home-container">
    <h1 class="page-title">首页</h1>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-primary)">
              <el-icon :size="30"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.totalBooks }}</p>
              <p class="stat-label">图书总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-success)">
              <el-icon :size="30"><List /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.borrowingCount }}</p>
              <p class="stat-label">在借数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-warning)">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.readerCount }}</p>
              <p class="stat-label">读者数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: var(--color-danger)">
              <el-icon :size="30"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.overdueCount }}</p>
              <p class="stat-label">逾期数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近借阅</span>
          </template>
          <el-table :data="recentBorrows" style="width: 100%">
            <el-table-column prop="bookTitle" label="书名" />
            <el-table-column prop="borrowDate" label="借书日期" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/books')">
              <el-icon><Search /></el-icon>
              搜索图书
            </el-button>
            <el-button type="success" @click="$router.push('/borrows')">
              <el-icon><List /></el-icon>
              我的借阅
            </el-button>
            <el-button type="warning" v-if="userStore.isAdmin" @click="$router.push('/categories')">
              <el-icon><FolderOpened /></el-icon>
              分类管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrow } from '@/api'

const userStore = useUserStore()

const stats = reactive({
  totalBooks: 0,
  borrowingCount: 0,
  readerCount: 0,
  overdueCount: 0
})

const recentBorrows = ref([])

const getStatusType = (status) => {
  const types = { 1: '', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '借阅中', 2: '已归还', 3: '逾期' }
  return texts[status] || ''
}

onMounted(async () => {
  try {
    const res = await borrow.list({ pageNum: 1, pageSize: 5 })
    recentBorrows.value = res.data.records
    stats.borrowingCount = res.data.total
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.stats-row {
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  transition: all var(--transition-base);
  cursor: pointer;
  border: 1px solid var(--color-border-light);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--color-border);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 6px 0 0 0;
  font-weight: 500;
}

.quick-actions {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.quick-actions .el-button {
  transition: all var(--transition-fast);
  cursor: pointer;
}

.quick-actions .el-button:hover {
  transform: translateY(-1px);
}

.home-container :deep(.el-card__header) {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border-light);
  font-weight: 600;
}

.home-container :deep(.el-card__body) {
  padding: var(--spacing-lg);
}
</style>