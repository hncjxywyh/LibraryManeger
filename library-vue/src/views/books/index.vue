<template>
  <div class="books-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">图书典藏</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="content-card">
      <div class="search-bar">
        <div class="search-input-wrapper">
          <el-input
            v-model="searchForm.keyword"
            placeholder="检索书名、作者、ISBN"
            style="width: 320px"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
            class="classical-search"
          >
            <template #prefix>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" style="color: var(--color-text-placeholder)">
                <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
              </svg>
            </template>
          </el-input>
        </div>
        <el-select
          v-model="searchForm.categoryId"
          placeholder="选择分类"
          clearable
          style="width: 180px"
          @change="handleSearch"
          class="classical-select"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch" class="search-btn">
          检　索
        </el-button>
        <el-button v-if="userStore.isAdmin" type="success" @click="handleAdd" class="add-btn">
          新增藏书
        </el-button>
      </div>

      <el-table :data="books" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)" class="classical-table">
        <el-table-column prop="title" label="书名" min-width="160">
          <template #default="{ row }">
            <span class="book-title-cell">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100" align="center" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="price" label="价格" width="80" align="center">
          <template #default="{ row }">
            <span class="price-cell">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" class="status-tag">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)" class="action-link">详阅</el-button>
            <el-button link type="success" @click="handleBorrow(row)" v-if="!userStore.isAdmin && row.stock > 0" class="action-link">借阅</el-button>
            <el-button link type="warning" @click="handleReserve(row)" v-if="!userStore.isAdmin && row.stock <= 0" class="action-link">预约</el-button>
            <el-button link type="primary" v-if="userStore.isAdmin" @click="handleEdit(row)" class="action-link">编辑</el-button>
            <el-button link type="danger" v-if="userStore.isAdmin" @click="handleDelete(row)" class="action-link">删除</el-button>
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
        @size-change="loadBooks"
        @current-change="loadBooks"
      />
    </el-card>

    <!-- 图书详情弹窗 -->
    <el-dialog v-model="detailVisible" title="典籍详情" width="650px" class="classical-dialog">
      <el-descriptions :column="2" border v-if="currentBook" class="book-descriptions">
        <el-descriptions-item label="书名">
          <span class="desc-value">{{ currentBook.title }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="作者">
          <span class="desc-value">{{ currentBook.author }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="ISBN">
          <span class="desc-value">{{ currentBook.isbn }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="出版社">
          <span class="desc-value">{{ currentBook.publisher || '未知' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="价格">
          <span class="desc-value price-highlight">¥{{ currentBook.price }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="库存">
          <span class="desc-value">{{ currentBook.stock }} 册</span>
        </el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">
          <span class="desc-value desc-intro">{{ currentBook.description || '暂无简介' }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleBorrow(currentBook)" v-if="!userStore.isAdmin && currentBook?.stock > 0">
          借阅此书
        </el-button>
        <el-button type="warning" @click="handleReserve(currentBook)" v-if="!userStore.isAdmin && currentBook?.stock <= 0">
          预约此书
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" :title="isEdit ? '编辑藏书' : '新增藏书'" width="600px" class="classical-dialog">
      <el-form ref="bookFormRef" :model="bookForm" :rules="bookRules" label-width="80px" class="book-form">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="bookForm.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" placeholder="请输入图书简介" />
        </el-form-item>
        <el-form-item label="状态" v-if="userStore.isAdmin">
          <el-switch v-model="bookForm.status" :active-value="1" :inactive-value="0" />
          <span style="margin-left: 10px">{{ bookForm.status === 1 ? '上架' : '下架' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { book, category, borrow } from '@/api'
import { createReservation } from '@/api/reservations'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const books = ref([])
const categories = ref([])
const detailVisible = ref(false)
const editVisible = ref(false)
const isEdit = ref(false)
const currentBook = ref(null)
const bookFormRef = ref()

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  keyword: '',
  categoryId: null
})

const bookForm = reactive({
  id: null,
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  categoryId: null,
  price: 0,
  stock: 0,
  description: '',
  status: 1
})

const bookRules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const loadBooks = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await book.list(params)
    books.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await category.list()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadBooks()
}

const handleDetail = (row) => {
  currentBook.value = row
  detailVisible.value = true
}

const handleBorrow = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要借阅《${row.title}》吗？`, '借阅确认')
    await borrow.borrow({ bookId: row.id })
    ElMessage.success('借书成功')
    detailVisible.value = false
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleReserve = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要预约《${row.title}》吗？图书到货后会通知您`, '预约确认')
    await createReservation(row.id)
    ElMessage.success('预约成功')
    detailVisible.value = false
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(bookForm, {
    id: null, isbn: '', title: '', author: '', publisher: '',
    categoryId: null, price: 0, stock: 0, description: '', status: 1
  })
  editVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(bookForm, row)
  editVisible.value = true
}

const handleSave = async () => {
  await bookFormRef.value.validate()
  try {
    if (isEdit.value) {
      await book.update(bookForm.id, bookForm)
    } else {
      await book.add(bookForm)
    }
    ElMessage.success('保存成功')
    editVisible.value = false
    loadBooks()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除《${row.title}》吗？`, '删除确认', { type: 'warning' })
    await book.delete(row.id)
    ElMessage.success('删除成功')
    loadBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadBooks()
  loadCategories()
})
</script>

<style scoped>
.books-container {
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

/* 搜索栏 */
.search-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.search-input-wrapper :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
  padding: 8px 15px;
}

.classical-select :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
}

.search-btn,
.add-btn {
  font-family: var(--font-family);
  letter-spacing: 2px;
  border-radius: var(--border-radius-md);
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

.book-title-cell {
  font-family: var(--font-family);
  font-weight: 500;
}

.price-cell {
  font-family: var(--font-family);
  color: var(--color-danger);
  font-weight: 500;
}

.status-tag {
  font-family: var(--font-family);
}

.action-link {
  font-family: var(--font-family);
  cursor: pointer;
}

/* 弹窗样式 */
.classical-dialog :deep(.el-dialog__header) {
  font-family: var(--font-family-heading);
  letter-spacing: 3px;
  border-bottom: 1px solid var(--color-border-light);
  padding-bottom: var(--spacing-md);
}

.classical-dialog :deep(.el-dialog__body) {
  padding: var(--spacing-lg);
}

.book-descriptions :deep(.el-descriptions__label) {
  font-family: var(--font-family);
  background-color: var(--color-primary-bg);
}

.book-descriptions :deep(.el-descriptions__content) {
  font-family: var(--font-family);
}

.desc-value {
  font-family: var(--font-family);
}

.price-highlight {
  color: var(--color-danger);
  font-weight: 600;
}

.desc-intro {
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.book-form :deep(.el-form-item__label) {
  font-family: var(--font-family);
}
</style>
