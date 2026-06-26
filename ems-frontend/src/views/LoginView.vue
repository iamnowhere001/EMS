<template>
  <div class="login-page">
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>
    <div class="login-box">
      <div class="login-left">
        <div class="brand">
          <div class="brand-icon-box">
            <el-icon class="brand-icon"><Management /></el-icon>
          </div>
          <span class="brand-name">EMS</span>
        </div>
        <div class="slogan">
          <h1 class="login-title">员工信息管理系统</h1>
          <p class="login-desc">Spring Boot + Vue3 + Element Plus 全栈后台管理解决方案</p>
        </div>
        <div class="login-features">
          <div class="feature-item" v-for="(item, index) in features" :key="index">
            <div class="feature-check">
              <el-icon><Check /></el-icon>
            </div>
            <span>{{ item }}</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-card">
          <div class="form-header">
            <div class="form-title">欢迎登录</div>
            <div class="form-subtitle">请使用管理员账号登录系统</div>
          </div>
          <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
            <el-form-item prop="username">
              <div class="input-label">用户名</div>
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item prop="password">
              <div class="input-label">密码</div>
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                :prefix-icon="Lock"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                立即登录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-tips">
            默认账号：<span>admin</span> / 密码：<span>123456</span>
          </div>
        </div>
      </div>
    </div>
    <div class="copyright">© 2024 EMS Employee Management System</div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Management, User, Lock, Check } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { STORAGE_KEYS } from '@/utils/constants'

const router = useRouter()
const formRef = ref<any>(null)
const loading = ref(false)

const features = [
  '员工信息统一管理',
  '数据可视化统计分析',
  'Excel 批量导入导出',
  '基于 JWT 的安全鉴权',
]

const form = reactive({
  username: 'admin',
  password: '123456',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const userInfo = await authApi.login(form)
    localStorage.setItem(STORAGE_KEYS.TOKEN, userInfo.token)
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, userInfo.refreshToken || '')
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(userInfo))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.35;
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: #00c9ff;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: #92fe9d;
  bottom: -80px;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 250px;
  height: 250px;
  background: #ff7e5f;
  top: 40%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 180px;
  height: 180px;
  background: #feb47b;
  bottom: 20%;
  left: 5%;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-30px) scale(1.05);
  }
}

.login-box {
  display: flex;
  width: 1000px;
  min-height: 580px;
  border-radius: 24px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.4);
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1;
  padding: 60px 60px 60px 70px;
  color: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
}

.login-left::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 1px;
  background: linear-gradient(180deg, transparent, rgba(255, 255, 255, 0.2), transparent);
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 80px;
}

.brand-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.brand-icon {
  font-size: 26px;
}

.brand-name {
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 2px;
}

.slogan {
  margin-bottom: 50px;
}

.login-title {
  font-size: 38px;
  font-weight: 700;
  margin-bottom: 18px;
  line-height: 1.2;
}

.login-desc {
  font-size: 15px;
  opacity: 0.75;
  line-height: 1.6;
  max-width: 360px;
}

.login-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.95;
}

.feature-check {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(146, 254, 157, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-check .el-icon {
  font-size: 14px;
  color: #92fe9d;
}

.login-right {
  width: 420px;
  padding: 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 44px 40px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

.form-header {
  margin-bottom: 36px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 13px;
  color: #909399;
}

.input-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
  font-weight: 500;
}

.login-form .el-form-item {
  margin-bottom: 22px;
}

.login-form .el-input {
  --el-input-height: 48px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: box-shadow 0.2s;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset, 0 0 0 4px rgba(64, 158, 255, 0.1);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  margin-top: 10px;
  border-radius: 10px;
  background: linear-gradient(90deg, #409eff 0%, #36cfc9 100%);
  border: none;
  transition: transform 0.2s, box-shadow 0.2s;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.35);
}

.login-tips {
  margin-top: 28px;
  text-align: center;
  font-size: 12px;
  color: #909399;
}

.login-tips span {
  color: #409eff;
  font-weight: 500;
}

.copyright {
  position: absolute;
  bottom: 24px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  z-index: 1;
}

/* 暗黑模式适配 */
html.dark .login-page {
  background: linear-gradient(135deg, #070b14 0%, #10141c 50%, #0a0e17 100%);
}

html.dark .login-box {
  background: rgba(30, 30, 35, 0.4);
  border-color: rgba(255, 255, 255, 0.08);
}

html.dark .login-card {
  background: rgba(30, 32, 35, 0.95);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.4);
}

html.dark .form-title {
  color: #e0e0e0;
}

html.dark .form-subtitle {
  color: #8d9099;
}

html.dark .input-label {
  color: #b0b3b8;
}

html.dark .login-form :deep(.el-input__wrapper) {
  background-color: #1d1e1f;
  box-shadow: 0 0 0 1px #414243 inset;
}

html.dark .login-form :deep(.el-input__inner) {
  color: #e0e0e0;
}

html.dark .login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset, 0 0 0 4px rgba(64, 158, 255, 0.15);
}

html.dark .login-tips {
  color: #8d9099;
}
</style>
