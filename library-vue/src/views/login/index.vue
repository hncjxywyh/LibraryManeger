<template>
  <div class="login-container">
    <!-- 古典装饰背景 -->
    <div class="ink-wash-background">
      <div class="cloud-decoration cloud-1"></div>
      <div class="cloud-decoration cloud-2"></div>
      <div class="cloud-decoration cloud-3"></div>
      <div class="bamboo-branch"></div>
    </div>

    <div class="login-box classical-border">
      <!-- 印章装饰 -->
      <div class="seal-badge">书</div>

      <div class="title-section">
        <h1 class="title">藏书阁</h1>
        <p class="subtitle">传承文化 · 品味书香</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <span class="input-icon">用户名</span>
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              class="classical-input"
            />
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-wrapper">
            <span class="input-icon">密　码</span>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              class="classical-input"
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登　录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="footer">
        <span>书卷多情似故人</span>
        <router-link to="/register" class="register-link">立即注册</router-link>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="footer-decoration">
      <span class="poem">书山有路勤为径，学海无涯苦作舟</span>
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
    ElMessage.error(error?.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #FAF6F0 0%, #F5F0E8 50%, #EDE6DC 100%);
  padding: var(--spacing-lg);
  position: relative;
  overflow: hidden;
}

/* 水墨背景装饰 */
.ink-wash-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.cloud-decoration {
  position: absolute;
  background: radial-gradient(ellipse at center, rgba(139, 69, 19, 0.06) 0%, transparent 70%);
  border-radius: 50%;
}

.cloud-1 {
  width: 600px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.cloud-2 {
  width: 400px;
  height: 300px;
  bottom: -50px;
  left: -50px;
}

.cloud-3 {
  width: 300px;
  height: 200px;
  top: 50%;
  left: 10%;
  opacity: 0.5;
}

.bamboo-branch {
  position: absolute;
  bottom: 0;
  left: 5%;
  width: 150px;
  height: 80%;
  background: linear-gradient(to top, transparent 60%, rgba(45, 80, 22, 0.08) 100%);
  clip-path: polygon(50% 0%, 60% 20%, 55% 25%, 65% 45%, 58% 50%, 70% 70%, 62% 75%, 75% 95%, 50% 100%, 25% 95%, 38% 75%, 30% 70%, 42% 50%, 35% 45%, 45% 25%, 40% 20%);
  opacity: 0.6;
}

/* 登录框 */
.login-box {
  width: 100%;
  max-width: 420px;
  padding: var(--spacing-2xl);
  background: rgba(255, 253, 248, 0.95);
  position: relative;
  z-index: 1;
}

/* 印章装饰 */
.seal-badge {
  position: absolute;
  top: -25px;
  right: 30px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-danger);
  color: #fff;
  font-family: var(--font-family-heading);
  font-size: 24px;
  border-radius: 4px;
  transform: rotate(12deg);
  box-shadow: 0 4px 12px rgba(139, 0, 0, 0.3);
}

/* 标题区域 */
.title-section {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.title {
  font-family: var(--font-family-heading);
  font-size: 36px;
  color: var(--color-primary-dark);
  margin: 0 0 var(--spacing-sm) 0;
  letter-spacing: 8px;
  text-shadow: 2px 2px 4px rgba(139, 69, 19, 0.1);
}

.subtitle {
  font-family: var(--font-family);
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
  letter-spacing: 2px;
}

/* 表单样式 */
.login-form {
  margin-top: var(--spacing-lg);
}

.login-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-lg);
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

.input-wrapper:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(139, 69, 19, 0.1);
}

.input-icon {
  font-family: var(--font-family);
  color: var(--color-text-secondary);
  font-size: 14px;
  min-width: 50px;
  text-align: justify;
  text-align-last: justify;
}

.classical-input :deep(.el-input__wrapper) {
  border: none;
  box-shadow: none;
  padding: 0;
  background: transparent;
}

.classical-input :deep(.el-input__inner) {
  font-family: var(--font-family);
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-family: var(--font-family);
  letter-spacing: 4px;
  border-radius: var(--border-radius-md);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  transition: all var(--transition-base);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 69, 19, 0.3);
}

/* 底部 */
.footer {
  text-align: center;
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-md);
  font-family: var(--font-family);
  font-size: 14px;
  color: var(--color-text-secondary);
}

.footer::before,
.footer::after {
  content: '';
  width: 40px;
  height: 1px;
  background: linear-gradient(to right, transparent, var(--color-border), transparent);
}

.register-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  transition: color var(--transition-fast);
}

.register-link:hover {
  color: var(--color-primary-dark);
}

/* 底部诗词装饰 */
.footer-decoration {
  position: absolute;
  bottom: var(--spacing-lg);
  text-align: center;
}

.poem {
  font-family: var(--font-family);
  font-size: 12px;
  color: var(--color-text-placeholder);
  letter-spacing: 2px;
}
</style>
