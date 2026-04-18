<template>
  <div class="borrows-container">
    <h1 class="page-title">{{ userStore.isAdmin ? '借阅管理' : '我的借阅' }}</h1>

    <el-card>
      <div class="filter-bar">
        <el-select
          v-model="filterStatus"
          placeholder="选择状态"
          clearable
          style="width: 150px"
          @change="loadBorrows"
        >
          <el-option label="借阅中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="逾期" :value="3" />
        </el-select>
      </div>

      <el-table :data="borrows" v-loading="loading" style="width: 100%; margin-top: 20px">
        <el-table-column prop="bookTitle" label="书名" min-width="150" />
        <el-table-column prop="userRealName" label="读者" width="100" v-if="userStore.isAdmin" />
        <el-table-column prop="borrowDate" label="借书日期" width="160" />
        <el-table-column prop="dueDate" label="应还日期" width="160" />
        <el-table-column prop="returnDate" label="实际还书日期" width="160">
          <template #default="{ row }">
            {{ row.returnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" @click="handleReturn(row)" v-if="row.status === 1">
              还书
            </el-button>
            <el-button link type="warning" @click="handleRenew(row)" v-if="row.status === 1">
              续借
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
        @size-change="loadBorrows"
        @current-change="loadBorrows"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { borrow } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const borrows = ref([])
const filterStatus = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = { 1: '', 2: 'success', 3: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { 1: '借阅中', 2: '已归还', 3: '逾期' }
  return texts[status] || ''
}

const loadBorrows = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: filterStatus.value?.toString()
    }
    const res = await borrow.list(params)
    borrows.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReturn = async (row) => {
  try {
    await borrow.return(row.id)
    ElMessage.success('还书成功')
    loadBorrows()
  } catch (error) {
    console.error(error)
  }
}

const handleRenew = async (row) => {
  try {
    await borrow.renew(row.id)
    ElMessage.success('续借成功')
    loadBorrows()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadBorrows()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}
</style>