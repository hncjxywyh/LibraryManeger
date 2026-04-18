<template>
  <div class="books-container">
    <h1 class="page-title">图书列表</h1>

    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索书名、作者、ISBN"
          style="width: 300px"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.categoryId"
          placeholder="选择分类"
          clearable
          style="width: 200px; margin-left: 10px"
          @change="handleSearch"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        <el-button type="primary" style="margin-left: 10px" @click="handleSearch">
          搜索
        </el-button>
        <el-button v-if="userStore.isAdmin" type="success" @click="handleAdd">
          新增图书
        </el-button>
      </div>

      <el-table :data="books" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)">
        <el-table-column prop="title" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="price" label="价格" width="80" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="success" @click="handleBorrow(row)" v-if="!userStore.isAdmin">借书</el-button>
            <el-button link type="primary" v-if="userStore.isAdmin" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" v-if="userStore.isAdmin" @click="handleDelete(row)">删除</el-button>
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
        @size-change="loadBooks"
        @current-change="loadBooks"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="图书详情" width="600px">
      <el-descriptions :column="2" border v-if="currentBook">
        <el-descriptions-item label="书名">{{ currentBook.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentBook.author }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ currentBook.isbn }}</el-descriptions-item>
        <el-descriptions-item label="出版社">{{ currentBook.publisher }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentBook.price }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ currentBook.stock }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ currentBook.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleBorrow(currentBook)" v-if="!userStore.isAdmin && currentBook?.stock > 0">
          借书
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" :title="isEdit ? '编辑图书' : '新增图书'" width="600px">
      <el-form ref="bookFormRef" :model="bookForm" :rules="bookRules" label-width="80px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="bookForm.publisher" />
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
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
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
  description: ''
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
    await ElMessageBox.confirm(`确定要借阅《${row.title}》吗？`, '提示')
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

const handleAdd = () => {
  isEdit.value = false
  Object.assign(bookForm, {
    id: null, isbn: '', title: '', author: '', publisher: '',
    categoryId: null, price: 0, stock: 0, description: ''
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
    await ElMessageBox.confirm(`确定要删除《${row.title}》吗？`, '警告', { type: 'warning' })
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
.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.search-bar :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xs);
}

.search-bar :deep(.el-input__wrapper:hover),
.search-bar :deep(.el-input__wrapper.is-focus) {
  box-shadow: var(--shadow-sm);
}

.books-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
}

.books-container :deep(.el-table) {
  border-radius: var(--border-radius-md);
}

.books-container :deep(.el-table th) {
  background-color: var(--color-content-bg) !important;
  font-weight: 600;
  color: var(--color-text-primary);
}

.books-container :deep(.el-table td) {
  border-bottom-color: var(--color-border-light);
}

.books-container :deep(.el-button) {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.books-container :deep(.el-button:hover) {
  opacity: 0.85;
}

.books-container :deep(.el-pagination) {
  padding: var(--spacing-md) 0;
}
</style>