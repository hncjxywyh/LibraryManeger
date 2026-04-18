<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">图书管理系统</h1>
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  padding: var(--spacing-lg);
}

.login-box {
  width: 100%;
  max-width: 420px;
  padding: var(--spacing-2xl);
  background: #fff;
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-xl);
}

.title {
  text-align: center;
  margin-bottom: var(--spacing-xl);
  color: var(--color-text-primary);
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.login-form {
  margin-top: var(--spacing-lg);
}

.login-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-lg);
}

.login-form :deep(.el-input__wrapper) {
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xs);
  transition: all var(--transition-fast);
}

.login-form :deep(.el-input__wrapper:hover),
.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: var(--shadow-sm);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-base);
  cursor: pointer;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.footer {
  text-align: center;
  margin-top: var(--spacing-xl);
  font-size: 14px;
  color: var(--color-text-secondary);
}

.footer a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
  transition: color var(--transition-fast);
}

.footer a:hover {
  color: var(--color-primary-dark);
}
</style>