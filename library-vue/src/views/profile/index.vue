<template>
  <div class="profile-container">
    <h1 class="page-title">个人中心</h1>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="个人信息" name="profile">
        <el-card>
          <el-form ref="formRef" :model="form" label-width="100px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdate">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="阅读统计" name="stats">
        <div class="stats-container">
          <el-row :gutter="20" style="margin-bottom: 20px">
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.totalBorrowCount }}</div>
                  <div class="stat-label">累计借阅</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.currentBorrowCount }}</div>
                  <div class="stat-label">当前借阅</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.returnedCount }}</div>
                  <div class="stat-label">已归还</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <div class="stat-card">
                  <div class="stat-value">{{ stats.overdueCount }}</div>
                  <div class="stat-label">逾期次数</div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-bottom: 20px">
            <el-col :span="12">
              <el-card body-style="padding: 10px;">
                <template #header>分类分布</template>
                <div class="chart-wrapper">
                  <v-chart :option="categoryChartOption" autoresize class="chart" />
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card body-style="padding: 10px;">
                <template #header>月度借阅趋势</template>
                <div class="chart-wrapper">
                  <v-chart :option="monthlyChartOption" autoresize class="chart" />
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-card>
            <template #header>借阅排行 Top10</template>
            <el-table :data="stats.topBooks" stripe>
              <el-table-column prop="title" label="书名" />
              <el-table-column prop="author" label="作者" />
              <el-table-column prop="borrowCount" label="借阅次数" width="120" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
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
    // Force next tick to ensure DOM is updated
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
    legend: { bottom: 0 },
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
      }
    }]
  }
})

const monthlyChartOption = computed(() => {
  const months = stats.value.monthlyStats?.map(item => item.month).reverse() || []
  const counts = stats.value.monthlyStats?.map(item => item.count).reverse() || []
  return {
    tooltip: { trigger: 'axis' },
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
      itemStyle: { color: '#409EFF' },
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
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  padding: 20px;
}

.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.profile-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
  margin-bottom: var(--spacing-lg);
}

.profile-container :deep(.el-card__header) {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border-light);
  font-weight: 600;
}

.profile-container :deep(.el-card__body) {
  padding: var(--spacing-lg);
}

.profile-container :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xs);
}

.profile-container :deep(.el-input__wrapper:hover),
.profile-container :deep(.el-input__wrapper.is-focus) {
  box-shadow: var(--shadow-sm);
}

.profile-container :deep(.el-button) {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.profile-container :deep(.el-button:hover) {
  opacity: 0.85;
}

.stats-container {
  width: 100%;
}

.stat-card {
  text-align: center;
  padding: 10px 0;
}

.chart-wrapper {
  height: 300px;
  width: 100%;
}

.chart {
  width: 100%;
  height: 100%;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}
</style>
