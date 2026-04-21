<template>
  <div class="categories-container ink-wash-bg">
    <div class="page-header">
      <h1 class="page-title">分类管理</h1>
      <div class="title-decoration"></div>
    </div>

    <el-card class="content-card">
      <div class="toolbar">
        <el-button type="success" @click="handleAdd" class="add-btn">新增分类</el-button>
      </div>

      <el-table :data="categories" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)" row-key="id" class="classical-table">
        <el-table-column prop="name" label="分类名称">
          <template #default="{ row }">
            <span class="category-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)" class="action-link">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)" class="action-link">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px" class="classical-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="category-form">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { category } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await category.list()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', sort: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await category.update(form.id, form)
    } else {
      await category.add(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadCategories()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '删除确认', { type: 'warning' })
    await category.delete(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.categories-container {
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

.toolbar {
  display: flex;
  justify-content: flex-start;
}

.add-btn {
  font-family: var(--font-family);
  letter-spacing: 2px;
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

.category-name {
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
}

.classical-dialog :deep(.el-dialog__body) {
  padding: var(--spacing-lg);
}

.category-form :deep(.el-form-item__label) {
  font-family: var(--font-family);
}
</style>
