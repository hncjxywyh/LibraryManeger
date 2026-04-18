<template>
  <div class="home-container">
    <h1 class="page-title">首页</h1>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
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
            <div class="stat-icon" style="background: #67C23A">
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
            <div class="stat-icon" style="background: #E6A23C">
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
            <div class="stat-icon" style="background: #F56C6C">
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
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  padding: 10px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>