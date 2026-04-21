<template>
  <div class="donations-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">图书捐赠</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="donation-form-card content-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">捐赠表单</span>
          <div class="header-line"></div>
        </div>
      </template>
      <el-form ref="donationFormRef" :model="donationForm" :rules="donationRules" label-width="100px" class="donation-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="捐赠者姓名" prop="donorName">
              <el-input v-model="donationForm.donorName" placeholder="请输入捐赠者姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contact">
              <el-input v-model="donationForm.contact" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图书名称" prop="bookName">
              <el-input v-model="donationForm.bookName" placeholder="请输入图书名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="donationForm.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出版社">
              <el-input v-model="donationForm.publisher" placeholder="请输入出版社" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="donationForm.quantity" :min="1" :max="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="留言">
          <el-input v-model="donationForm.message" type="textarea" :rows="3" placeholder="请输入留言" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" class="submit-btn">提交捐赠</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="donation-records-card content-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">我的捐赠记录</span>
          <div class="header-line"></div>
        </div>
      </template>
      <el-table :data="donations" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)" class="classical-table">
        <el-table-column prop="bookName" label="图书名称" min-width="150">
          <template #default="{ row }">
            <span class="book-name-cell">{{ row.bookName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="120" />
        <el-table-column prop="quantity" label="数量" width="70" align="center" />
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewComment" label="审核备注" min-width="150">
          <template #default="{ row }">
            <span class="comment-cell">{{ row.reviewComment || '—' }}</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { submitDonation, getMyDonations } from '@/api/donations'
import { ElMessage } from 'element-plus'

const donationFormRef = ref()
const loading = ref(false)
const donations = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const donationForm = reactive({
  donorName: '',
  contact: '',
  bookName: '',
  author: '',
  publisher: '',
  quantity: 1,
  message: ''
})

const donationRules = {
  donorName: [{ required: true, message: '请输入捐赠者姓名', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  bookName: [{ required: true, message: '请输入图书名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

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
    const res = await getMyDonations()
    donations.value = res.data.records || res.data || []
    pagination.total = res.data.total || donations.value.length
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  await donationFormRef.value.validate()
  try {
    await submitDonation(donationForm)
    ElMessage.success('捐赠提交成功')
    handleReset()
    loadDonations()
  } catch (error) {
    console.error(error)
  }
}

const handleReset = () => {
  donationFormRef.value?.resetFields()
  Object.assign(donationForm, {
    donorName: '',
    contact: '',
    bookName: '',
    author: '',
    publisher: '',
    quantity: 1,
    message: ''
  })
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
  margin-bottom: var(--spacing-lg);
}

.content-card :deep(.el-card__header) {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border-light);
}

.content-card :deep(.el-card__body) {
  padding: var(--spacing-lg);
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.card-title {
  font-family: var(--font-family-heading);
  font-size: 16px;
  color: var(--color-text-primary);
  letter-spacing: 2px;
}

.header-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, var(--color-border), transparent);
}

/* 表单样式 */
.donation-form :deep(.el-form-item__label) {
  font-family: var(--font-family);
}

.submit-btn {
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
</style>
