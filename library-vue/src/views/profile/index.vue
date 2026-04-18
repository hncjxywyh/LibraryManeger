<template>
  <div class="profile-container">
    <h1 class="page-title">个人中心</h1>

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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { user as userApi } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

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

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

onMounted(() => {
  Object.assign(form, userStore.userInfo)
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
  max-width: 600px;
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
</style>