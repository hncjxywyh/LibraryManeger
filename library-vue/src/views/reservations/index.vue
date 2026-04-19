<template>
  <div class="reservations-container">
    <h1 class="page-title">我的预约</h1>

    <el-card>
      <el-table :data="reservations" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)">
        <el-table-column prop="bookTitle" label="图书名称" min-width="150" />
        <el-table-column prop="bookAuthor" label="作者" width="120" />
        <el-table-column prop="createTime" label="预约时间" width="160" />
        <el-table-column prop="position" label="队列位置" width="100">
          <template #default="{ row }">
            {{ row.position ? `第 ${row.position} 位` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="有效期" width="180">
          <template #default="{ row }">
            <span v-if="row.status === 2 && row.expireTime">
              取书有效期至: {{ row.expireTime }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" @click="handlePickup(row)" v-if="row.status === 2">
              取书
            </el-button>
            <el-button link type="danger" @click="handleCancel(row)" v-if="row.status === 1">
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
        style="margin-top: 20px; justify-content: flex-end"
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
    await ElMessageBox.confirm(`确定要取书《${row.bookTitle}》吗？`, '提示')
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
    await ElMessageBox.confirm(`确定要取消预约《${row.bookTitle}》吗？`, '提示')
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
.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.reservations-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
}

.reservations-container :deep(.el-table th) {
  background-color: var(--color-content-bg) !important;
  font-weight: 600;
  color: var(--color-text-primary);
}

.reservations-container :deep(.el-table td) {
  border-bottom-color: var(--color-border-light);
}

.reservations-container :deep(.el-button) {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.reservations-container :deep(.el-button:hover) {
  opacity: 0.85;
}

.reservations-container :deep(.el-pagination) {
  padding: var(--spacing-md) 0;
}
</style>