<template>
  <div class="messages-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的消息</span>
          <el-button type="primary" link @click="markAllRead" v-if="messages.length > 0">
            全部标为已读
          </el-button>
        </div>
      </template>

      <el-empty v-if="messages.length === 0" description="暂无消息" />

      <div v-else class="message-list">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ unread: msg.isRead === 0 }"
          @click="handleRead(msg)"
        >
          <div class="message-icon">
            <el-icon v-if="msg.type === 1" color="#E6A23C"><Bell /></el-icon>
            <el-icon v-else-if="msg.type === 2" color="#409EFF"><InfoFilled /></el-icon>
            <el-icon v-else color="#67C23A"><CircleCheck /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-title">{{ msg.title }}</div>
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <div class="message-action" v-if="msg.isRead === 0">
            <el-tag size="small" type="danger">未读</el-tag>
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
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from '@/api'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Bell, InfoFilled, CircleCheck } from '@element-plus/icons-vue'

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
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  background: #ecf5ff;
}

.message-item.unread {
  background: #fef0f0;
  border-left: 3px solid #f56c6c;
}

.message-icon {
  margin-right: 12px;
  font-size: 20px;
}

.message-content {
  flex: 1;
}

.message-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.message-text {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.message-time {
  color: #999;
  font-size: 12px;
}

.message-action {
  margin-left: 12px;
}
</style>
