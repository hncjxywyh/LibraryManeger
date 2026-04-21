<template>
  <div class="donations-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">捐赠审核</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="content-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="审核状态">
          <el-select v-model="filterStatus" placeholder="全部" clearable style="width: 120px" class="classical-select">
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadDonations" class="query-btn">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="donations" v-loading="loading" stripe class="classical-table">
        <el-table-column prop="donorName" label="捐赠者" width="100" />
        <el-table-column prop="contact" label="联系方式" width="120" />
        <el-table-column prop="bookName" label="图书名称" min-width="150">
          <template #default="{ row }">
            <span class="book-name-cell">{{ row.bookName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="publisher" label="出版社" width="120" />
        <el-table-column prop="quantity" label="数量" width="60" align="center" />
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewComment" label="审核备注" min-width="120">
          <template #default="{ row }">
            <span class="comment-cell">{{ row.reviewComment || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 1" class="action-link">
              通过
            </el-button>
            <el-button link type="danger" @click="handleReject(row)" v-if="row.status === 1" class="action-link">
              拒绝
            </el-button>
            <span v-else class="done-text">—</span>
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
        @size-change="loadDonations"
        @current-change="loadDonations"
      />
    </el-card>

    <el-dialog v-model="reviewDialogVisible" title="审核备注" width="400px" class="classical-dialog">
      <el-form>
        <el-form-item label="备注">
          <el-input v-model="reviewComment" type="textarea" :rows="3" placeholder="请输入审核备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllDonations, reviewDonation } from '@/api/donations'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const donations = ref([])
const filterStatus = ref('')
const reviewDialogVisible = ref(false)
const reviewComment = ref('')
const currentDonation = ref(null)
const reviewAction = ref('')

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '待审核', 2: '已通过', 3: '已拒绝' }
  return texts[status] || ''
}

const loadDonations = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: filterStatus.value || undefined
    }
    const res = await getAllDonations(params)
    donations.value = res.data.records || res.data || []
    pagination.total = res.data.total || donations.value.length
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleApprove = (row) => {
  currentDonation.value = row
  reviewAction.value = 'approve'
  reviewComment.value = ''
  reviewDialogVisible.value = true
}

const handleReject = (row) => {
  currentDonation.value = row
  reviewAction.value = 'reject'
  reviewComment.value = ''
  reviewDialogVisible.value = true
}

const confirmReview = async () => {
  try {
    const status = reviewAction.value === 'approve' ? 2 : 3
    await reviewDonation(currentDonation.value.id, { status, comment: reviewComment.value })
    ElMessage.success('审核完成')
    reviewDialogVisible.value = false
    loadDonations()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadDonations()
})
</script>

<style scoped>
.donations-container {
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

.filter-form {
  margin-bottom: var(--spacing-md);
}

.filter-form :deep(.el-form-item__label) {
  font-family: var(--font-family);
}

.classical-select :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
}

.query-btn {
  font-family: var(--font-family);
  letter-spacing: 2px;
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

.book-name-cell {
  font-family: var(--font-family);
  font-weight: 500;
}

.comment-cell {
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

.done-text {
  font-family: var(--font-family);
  color: var(--color-text-placeholder);
}

/* 弹窗样式 */
.classical-dialog :deep(.el-dialog__header) {
  font-family: var(--font-family-heading);
  letter-spacing: 3px;
}
</style>
