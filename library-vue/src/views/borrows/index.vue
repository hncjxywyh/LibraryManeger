<template>
  <div class="borrows-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">{{ userStore.isAdmin ? '借阅管理' : '我的借阅' }}</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="content-card">
      <div class="filter-bar">
        <el-select
          v-model="filterStatus"
          placeholder="选择状态"
          clearable
          style="width: 150px"
          @change="loadBorrows"
          class="classical-select"
        >
          <el-option label="借阅中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="逾期" :value="3" />
        </el-select>
      </div>

      <el-table :data="borrows" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)" class="classical-table">
        <el-table-column prop="bookTitle" label="书名" min-width="160">
          <template #default="{ row }">
            <span class="book-title-cell">{{ row.bookTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userRealName" label="读者" width="100" v-if="userStore.isAdmin" />
        <el-table-column prop="borrowDate" label="借书日期" width="140" />
        <el-table-column prop="dueDate" label="应还日期" width="140" />
        <el-table-column prop="returnDate" label="实际还书日期" width="140">
          <template #default="{ row }">
            <span class="date-cell">{{ row.returnDate || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="success" @click="handleReturn(row)" v-if="row.status === 1" class="action-link">
              还书
            </el-button>
            <el-button link type="warning" @click="handleRenew(row)" v-if="row.status === 1" class="action-link">
              续借
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: var(--spacing-lg); justify-content: flex-end"
        @size-change="loadBorrows"
        @current-change="loadBorrows"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrow } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const borrows = ref([])
const filterStatus = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = { 1: 'primary', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '借阅中', 2: '已归还', 3: '逾期' }
  return texts[status] || ''
}

const loadBorrows = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: filterStatus.value ?? undefined
    }
    const res = await borrow.list(params)
    borrows.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
    ElMessage.error(error?.response?.data?.message || '加载借阅记录失败')
  } finally {
    loading.value = false
  }
}

const handleReturn = async (row) => {
  try {
    await borrow.return(row.id)
    ElMessage.success('还书成功')
    loadBorrows()
  } catch (error) {
    console.error(error)
    ElMessage.error(error?.response?.data?.message || '还书失败')
  }
}

const handleRenew = async (row) => {
  try {
    await borrow.renew(row.id)
    ElMessage.success('续借成功')
    loadBorrows()
  } catch (error) {
    console.error(error)
    ElMessage.error(error?.response?.data?.message || '续借失败')
  }
}

onMounted(() => {
  loadBorrows()
})
</script>

<style scoped>
.borrows-container {
  padding: var(--spacing-md);
}

/* 页面标题 */
.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-title {
  font-family: var(--font-family-heading);
  font-size: 26px;
  color: var(--color-text-primary);
  margin: 0;
  letter-spacing: 4px;
  position: relative;
  display: inline-block;
}

.title-decoration {
  width: 60px;
  height: 3px;
  background: linear-gradient(to right, var(--color-primary), transparent);
  margin-top: var(--spacing-sm);
}

/* 内容卡片 */
.content-card {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
  background: #fff;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.classical-select :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
}

/* 表格样式 */
.classical-table :deep(.el-table__header th) {
  background-color: var(--color-primary-bg) !important;
  font-family: var(--font-family-heading);
  font-weight: 500;
  color: var(--color-text-primary);
  letter-spacing: 1px;
}

.classical-table :deep(.el-table__row) {
  transition: background-color var(--transition-fast);
}

.classical-table :deep(.el-table__row:hover) {
  background-color: var(--color-primary-bg) !important;
}

.book-title-cell {
  font-family: var(--font-family);
  font-weight: 500;
}

.date-cell {
  font-family: var(--font-family);
  color: var(--color-text-secondary);
}

.status-tag {
  font-family: var(--font-family);
}

.action-link {
  font-family: var(--font-family);
  cursor: pointer;
}
</style>
