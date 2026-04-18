<template>
  <div class="categories-container">
    <h1 class="page-title">分类管理</h1>

    <el-card>
      <div class="toolbar">
        <el-button type="success" @click="handleAdd">新增分类</el-button>
      </div>

      <el-table :data="categories" v-loading="loading" stripe style="width: 100%; margin-top: var(--spacing-lg)" row-key="id">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
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
    await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '警告', { type: 'warning' })
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
.page-title {
  margin-bottom: var(--spacing-lg);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.categories-container :deep(.el-card) {
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border-light);
}

.categories-container :deep(.el-table th) {
  background-color: var(--color-content-bg) !important;
  font-weight: 600;
  color: var(--color-text-primary);
}

.categories-container :deep(.el-table td) {
  border-bottom-color: var(--color-border-light);
}

.categories-container :deep(.el-button) {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.categories-container :deep(.el-button:hover) {
  opacity: 0.85;
}
</style>