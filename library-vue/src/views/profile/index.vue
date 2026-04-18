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
  newPassword: ''
})

const passwordRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

onMounted(() => {
  Object.assign(form, userStore.userInfo)
})

const handleUpdate = () => {
  ElMessage.success('功能待开发')
}

const handleChangePassword = () => {
  ElMessage.success('功能待开发')
}
</script>

<style scoped>
.profile-container {
  max-width: 600px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}
</style>