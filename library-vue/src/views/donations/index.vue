<template>
  <div class="donations-container">
    <h1 class="page-title">图书捐赠</h1>

    <el-card class="donation-form-card">
      <template #header>
        <div class="card-header">
          <span>捐赠表单</span>
        </div>
      </template>
      <el-form ref="donationFormRef" :model="donationForm" :rules="donationRules" label-width="100px">
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
          <el-button type="primary" @click="handleSubmit">提交捐赠</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="donation-records-card">
      <template #header>
        <div class="card-header">
          <span>我的捐赠记录</span>
        </div>
      </template>
      <el-table :data="donations" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)">
        <el-table-column prop="bookName" label="图书名称" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewComment" label="审核备注" min-width="150">
          <template #default="{ row }">
            {{ row.reviewComment || '-' }}
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
.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.donations-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
  margin-bottom: var(--spacing-lg);
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.donations-container :deep(.el-table th) {
  background-color: var(--color-content-bg) !important;
  font-weight: 600;
  color: var(--color-text-primary);
}

.donations-container :deep(.el-table td) {
  border-bottom-color: var(--color-border-light);
}

.donations-container :deep(.el-button) {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.donations-container :deep(.el-button:hover) {
  opacity: 0.85;
}

.donations-container :deep(.el-pagination) {
  padding: var(--spacing-md) 0;
}
</style>