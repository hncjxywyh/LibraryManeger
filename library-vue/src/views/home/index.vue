<template>
  <div class="home-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">书阁首页</h1>
      <div class="title-decoration"></div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-books">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
              </svg>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statsData.totalBooks }}</p>
              <p class="stat-label">藏书总量</p>
            </div>
          </div>
          <div class="stat-decoration left"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-borrowing">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9 12l2 2 4-4"/>
                <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statsData.borrowingCount }}</p>
              <p class="stat-label">在借图书</p>
            </div>
          </div>
          <div class="stat-decoration left"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-readers">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4z"/>
                <path d="M6 20v-2c0-2.21 3.58-4 6-4s6 1.79 6 4v2H6z"/>
              </svg>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statsData.readerCount }}</p>
              <p class="stat-label">读者总数</p>
            </div>
          </div>
          <div class="stat-decoration left"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-overdue">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
              </svg>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statsData.overdueCount }}</p>
              <p class="stat-label">逾期提醒</p>
            </div>
          </div>
          <div class="stat-decoration left"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: var(--spacing-lg)">
      <el-col :span="14">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">近期借阅</span>
              <div class="header-line"></div>
            </div>
          </template>
          <el-table :data="recentBorrows" style="width: 100%" stripe>
            <el-table-column prop="bookTitle" label="书名" min-width="120">
              <template #default="{ row }">
                <span class="book-title">{{ row.bookTitle }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="borrowDate" label="借阅日期" width="110" />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small" class="status-tag">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷入口</span>
              <div class="header-line"></div>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/books')" class="action-btn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" style="margin-right: 8px">
                <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
              </svg>
              检索图书
            </el-button>
            <el-button type="success" @click="$router.push('/borrows')" class="action-btn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" style="margin-right: 8px">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
              我的借阅
            </el-button>
            <el-button type="warning" v-if="userStore.isAdmin" @click="$router.push('/categories')" class="action-btn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" style="margin-right: 8px">
                <path d="M10 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/>
              </svg>
              分类管理
            </el-button>
          </div>

          <div class="quote-section">
            <div class="quote-decoration">"</div>
            <p class="quote-text">书卷多情似故人，晨昏忧乐每相亲</p>
            <p class="quote-author">— 于谦《观书》</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrow, userStats, stats } from '@/api'

const userStore = useUserStore()

const statsData = reactive({
  totalBooks: 0,
  borrowingCount: 0,
  readerCount: 0,
  overdueCount: 0
})

const recentBorrows = ref([])

const getStatusType = (status) => {
  const types = { 1: 'primary', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '借阅中', 2: '已归还', 3: '逾期' }
  return texts[status] || ''
}

onMounted(async () => {
  try {
    const [borrowRes, userStatsRes, statsOverviewRes] = await Promise.all([
      borrow.list({ pageNum: 1, pageSize: 5 }),
      userStats.get(),
      stats.overview()
    ])

    recentBorrows.value = borrowRes.data.records

    if (userStatsRes.data) {
      statsData.borrowingCount = userStatsRes.data.currentBorrowCount || 0
      statsData.overdueCount = userStatsRes.data.overdueCount || 0
    }

    if (statsOverviewRes.data) {
      statsData.totalBooks = statsOverviewRes.data.totalBooks || 0
      statsData.readerCount = statsOverviewRes.data.readerCount || 0
    }
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.home-container {
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

/* 统计卡片 */
.stats-row {
  margin-bottom: var(--spacing-md);
}

.stat-card {
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
  background: #fff;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-border);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary), transparent);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  position: relative;
  z-index: 1;
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

.stat-books {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
}

.stat-borrowing {
  background: linear-gradient(135deg, var(--color-success) 0%, #1E4620 100%);
}

.stat-readers {
  background: linear-gradient(135deg, var(--color-warning) 0%, #8B6914 100%);
}

.stat-overdue {
  background: linear-gradient(135deg, var(--color-danger) 0%, #5C0000 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-family: var(--font-family);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  line-height: 1.2;
}

.stat-label {
  font-family: var(--font-family);
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 6px 0 0 0;
}

.stat-decoration {
  position: absolute;
  bottom: 10px;
  opacity: 0.1;
}

.stat-decoration.left {
  right: 10px;
  width: 40px;
  height: 40px;
  border-right: 2px solid var(--color-primary);
  border-bottom: 2px solid var(--color-primary);
}

/* 内容卡片 */
.content-card {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
  background: #fff;
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

.book-title {
  font-family: var(--font-family);
}

.status-tag {
  font-family: var(--font-family);
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.action-btn {
  width: 100%;
  height: 44px;
  font-family: var(--font-family);
  letter-spacing: 2px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 名言引用 */
.quote-section {
  text-align: center;
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.03) 0%, rgba(139, 69, 19, 0.08) 100%);
  border-radius: var(--border-radius-md);
  position: relative;
}

.quote-decoration {
  font-family: var(--font-family-heading);
  font-size: 48px;
  color: var(--color-primary);
  opacity: 0.2;
  position: absolute;
  top: 0;
  left: var(--spacing-md);
  line-height: 1;
}

.quote-text {
  font-family: var(--font-family);
  font-size: 15px;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-sm) 0;
  font-style: italic;
}

.quote-author {
  font-family: var(--font-family);
  font-size: 12px;
  color: var(--color-text-secondary);
  margin: 0;
}
</style>
