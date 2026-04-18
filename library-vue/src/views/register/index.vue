<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="title">用户注册</h1>
      <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
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
          />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input
            v-model="form.realName"
            placeholder="请输入真实姓名"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱（选填）"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
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
  password: '',
  realName: '',
  phone: '',
  email: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  padding: var(--spacing-lg);
}

.register-box {
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

.register-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-lg);
}

.register-form :deep(.el-input__wrapper) {
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-xs);
  transition: all var(--transition-fast);
}

.register-form :deep(.el-input__wrapper:hover),
.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: var(--shadow-sm);
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-base);
  cursor: pointer;
}

.register-btn:hover {
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