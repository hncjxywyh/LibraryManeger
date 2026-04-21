<template>
  <div class="profile-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <div class="title-decoration"></div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="classical-tabs">
      <el-tab-pane label="个人信息" name="profile">
        <el-card class="content-card">
          <el-form ref="formRef" :model="form" label-width="100px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled class="disabled-input" />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdate" class="save-btn">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="content-card password-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">修改密码</span>
              <div class="header-line"></div>
            </div>
          </template>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" class="password-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" class="save-btn">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="阅读统计" name="stats">
        <div class="stats-container">
          <el-row :gutter="20" style="margin-bottom: var(--spacing-lg)">
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card-item">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.totalBorrowCount }}</div>
                  <div class="stat-label">累计借阅</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card-item">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.currentBorrowCount }}</div>
                  <div class="stat-label">当前借阅</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card-item">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.returnedCount }}</div>
                  <div class="stat-label">已归还</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover" class="stat-card-item">
                <div class="stat-card">
                  <div class="stat-value danger">{{ stats.overdueCount }}</div>
                  <div class="stat-label">逾期次数</div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-bottom: var(--spacing-lg)">
            <el-col :span="12">
              <el-card body-style="padding: 10px;" class="chart-card">
                <template #header>
                  <span class="chart-title">分类分布</span>
                </template>
                <div class="chart-wrapper">
                  <v-chart :option="categoryChartOption" autoresize class="chart" />
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card body-style="padding: 10px;" class="chart-card">
                <template #header>
                  <span class="chart-title">月度借阅趋势</span>
                </template>
                <div class="chart-wrapper">
                  <v-chart :option="monthlyChartOption" autoresize class="chart" />
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-card class="content-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">借阅排行 Top10</span>
                <div class="header-line"></div>
              </div>
            </template>
            <el-table :data="stats.topBooks" stripe class="classical-table">
              <el-table-column prop="title" label="书名">
                <template #default="{ row }">
                  <span class="book-title-cell">{{ row.title }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="author" label="作者" width="140" />
              <el-table-column prop="borrowCount" label="借阅次数" width="120" align="center" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { user as userApi, userStats as userStatsApi } from '@/api'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'
import 'echarts'

const userStore = useUserStore()

const activeTab = ref('profile')
const formRef = ref()
const passwordFormRef = ref()

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const stats = ref({
  totalBorrowCount: 0,
  currentBorrowCount: 0,
  returnedCount: 0,
  overdueCount: 0,
  categoryStats: [],
  monthlyStats: [],
  topBooks: [],
  loaded: false
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

onMounted(() => {
  Object.assign(form, userStore.userInfo)
})

const handleTabChange = async (tab) => {
  if (tab === 'stats' && !stats.value.loaded) {
    await nextTick()
    await loadStats()
  }
}

const loadStats = async () => {
  try {
    const res = await userStatsApi.get()
    stats.value = { ...res.data, loaded: true }
    await nextTick()
  } catch (e) {
    // error handled by interceptor
  }
}

const categoryChartOption = computed(() => {
  const data = stats.value.categoryStats?.map(item => ({
    name: item.categoryName,
    value: item.count
  })) || []
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { fontFamily: 'Noto Serif SC' } },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        fontFamily: 'Noto Serif SC'
      }
    }]
  }
})

const monthlyChartOption = computed(() => {
  const months = stats.value.monthlyStats?.map(item => item.month).reverse() || []
  const counts = stats.value.monthlyStats?.map(item => item.count).reverse() || []
  return {
    tooltip: { trigger: 'axis', textStyle: { fontFamily: 'Noto Serif SC' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      boundaryGap: false
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      smooth: true,
      data: counts,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#8B4513' },
      lineStyle: { width: 2 }
    }]
  }
})

const handleUpdate = async () => {
  try {
    await userApi.update(userStore.userInfo.id, {
      realName: form.realName,
      phone: form.phone,
      email: form.email
    })
    userStore.userInfo.realName = form.realName
    userStore.userInfo.phone = form.phone
    userStore.userInfo.email = form.email
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    ElMessage.success('修改成功')
  } catch (error) {
    // error already handled by interceptor
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await userApi.changePassword(userStore.userInfo.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    ElMessage.success('密码修改成功')
  } catch (error) {
    // validation error or API error handled by interceptor
  }
}

import { computed } from 'vue'
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
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

/* 标签页样式 */
.classical-tabs :deep(.el-tabs__item) {
  font-family: var(--font-family);
  font-size: 15px;
}

.classical-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: var(--color-border);
}

.classical-tabs :deep(.el-tabs__active-bar) {
  background-color: var(--color-primary);
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
.profile-form :deep(.el-form-item__label),
.password-form :deep(.el-form-item__label) {
  font-family: var(--font-family);
}

.disabled-input :deep(.el-input__wrapper) {
  background-color: var(--color-border-light);
}

.save-btn {
  font-family: var(--font-family);
  letter-spacing: 2px;
}

.password-card {
  margin-top: var(--spacing-lg);
}

/* 统计卡片 */
.stat-card-item {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
  transition: all var(--transition-base);
}

.stat-card-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card {
  text-align: center;
  padding: var(--spacing-md) 0;
}

.stat-value {
  font-family: var(--font-family);
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: var(--spacing-sm);
}

.stat-value.danger {
  color: var(--color-danger);
}

.stat-label {
  font-family: var(--font-family);
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* 图表卡片 */
.chart-card {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
}

.chart-card :deep(.el-card__header) {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border-light);
}

.chart-card :deep(.el-card__header span) {
  font-family: var(--font-family-heading);
  font-size: 14px;
  letter-spacing: 1px;
}

.chart-title {
  font-family: var(--font-family-heading);
  font-size: 14px;
  color: var(--color-text-primary);
}

.chart-wrapper {
  height: 280px;
  width: 100%;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 表格样式 */
.classical-table :deep(.el-table__header th) {
  background-color: var(--color-primary-bg) !important;
  font-family: var(--font-family-heading);
  font-weight: 500;
  color: var(--color-text-primary);
  letter-spacing: 1px;
}

.book-title-cell {
  font-family: var(--font-family);
  font-weight: 500;
}
</style>
