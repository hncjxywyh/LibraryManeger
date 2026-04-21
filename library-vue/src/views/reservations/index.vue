<template>
  <div class="reservations-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">我的预约</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="content-card">
      <el-table :data="reservations" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)" class="classical-table">
        <el-table-column prop="bookTitle" label="图书名称" min-width="160">
          <template #default="{ row }">
            <span class="book-title-cell">{{ row.bookTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="bookAuthor" label="作者" width="120" />
        <el-table-column prop="createTime" label="预约时间" width="160" />
        <el-table-column prop="position" label="队列位置" width="100" align="center">
          <template #default="{ row }">
            <span class="position-cell">{{ row.position ? `第 ${row.position} 位` : '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="有效期" width="180">
          <template #default="{ row }">
            <span v-if="row.status === 2 && row.expireTime" class="expire-text">
              取书有效期至: {{ row.expireTime }}
            </span>
            <span v-else class="expire-text">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="success" @click="handlePickup(row)" v-if="row.status === 2" class="action-link">
              取书
            </el-button>
            <el-button link type="danger" @click="handleCancel(row)" v-if="row.status === 1" class="action-link">
              取消预约
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
        @size-change="loadReservations"
        @current-change="loadReservations"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyReservations, cancelReservation, pickupReservation } from '@/api/reservations'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const reservations = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = { 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '排队中', 2: '已通知', 3: '已取消', 4: '已失效' }
  return texts[status] || ''
}

const loadReservations = async () => {
  loading.value = true
  try {
    const res = await getMyReservations()
    reservations.value = res.data.records || res.data || []
    pagination.total = res.data.total || reservations.value.length
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePickup = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取书《${row.bookTitle}》吗？`, '取书确认')
    await pickupReservation(row.id)
    ElMessage.success('取书成功')
    loadReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消预约《${row.bookTitle}》吗？`, '取消确认')
    await cancelReservation(row.id)
    ElMessage.success('取消预约成功')
    loadReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadReservations()
})
</script>

<style scoped>
.reservations-container {
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

.position-cell {
  font-family: var(--font-family);
}

.expire-text {
  font-family: var(--font-family);
  font-size: 13px;
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
