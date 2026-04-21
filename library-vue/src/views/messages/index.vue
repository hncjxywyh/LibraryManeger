<template>
  <div class="messages-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">我的消息</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">消息中心</span>
          <div class="header-line"></div>
          <el-button type="primary" link @click="markAllRead" v-if="messages.length > 0" class="mark-read-btn">
            全部标为已读
          </el-button>
        </div>
      </template>

      <el-empty v-if="messages.length === 0" description="暂无消息" class="empty-state" />

      <div v-else class="message-list">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ unread: msg.isRead === 0 }"
          @click="handleRead(msg)"
        >
          <div class="message-icon">
            <svg v-if="msg.type === 1" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" style="color: var(--color-warning)">
              <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/>
            </svg>
            <svg v-else-if="msg.type === 2" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" style="color: var(--color-info)">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
            </svg>
            <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="currentColor" style="color: var(--color-success)">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <div class="message-content">
            <div class="message-title">{{ msg.title }}</div>
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <div class="message-action" v-if="msg.isRead === 0">
            <el-tag size="small" type="danger" class="unread-tag">未读</el-tag>
          </div>
        </div>
      </div>

      <el-pagination
        v-if="total > 0"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="loadMessages"
        style="margin-top: var(--spacing-lg); justify-content: center"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from '@/api'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const messages = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const loadMessages = async () => {
  const res = await message.list({ pageNum: pageNum.value, pageSize: pageSize.value })
  messages.value = res.data.records
  total.value = res.data.total
}

const handleRead = async (msg) => {
  if (msg.isRead === 0) {
    await message.markRead(msg.id)
    msg.isRead = 1
    const userStore = useUserStore()
    userStore.unreadMessageCount = Math.max(0, userStore.unreadMessageCount - 1)
  }
}

const markAllRead = async () => {
  await message.markAllRead()
  ElMessage.success('已全部标为已读')
  loadMessages()
  const userStore = useUserStore()
  userStore.unreadMessageCount = 0
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString()
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.messages-container {
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

.mark-read-btn {
  font-family: var(--font-family);
}

/* 空状态 */
.empty-state {
  padding: var(--spacing-xl);
}

/* 消息列表 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: var(--spacing-lg);
  background: var(--color-primary-bg);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 1px solid transparent;
}

.message-item:hover {
  background: #F5EFE6;
  border-color: var(--color-border);
}

.message-item.unread {
  background: #FDF5F5;
  border-left: 3px solid var(--color-danger);
}

.message-icon {
  margin-right: var(--spacing-md);
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.message-title {
  font-family: var(--font-family);
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}

.message-text {
  font-family: var(--font-family);
  color: var(--color-text-secondary);
  font-size: 14px;
  margin-bottom: var(--spacing-sm);
  line-height: 1.5;
}

.message-time {
  font-family: var(--font-family);
  color: var(--color-text-placeholder);
  font-size: 12px;
}

.message-action {
  margin-left: var(--spacing-md);
  flex-shrink: 0;
}

.unread-tag {
  font-family: var(--font-family);
}
</style>
