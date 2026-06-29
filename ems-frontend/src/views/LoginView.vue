<template>
  <div class="login-page">
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
      <div class="shape shape-5"></div>
    </div>
    <div class="grid-overlay"></div>

    <div class="login-box">
      <!-- 左侧品牌区 -->
      <div class="login-left">
        <div class="brand">
          <div class="brand-icon-box">
            <el-icon class="brand-icon"><Management /></el-icon>
          </div>
          <span class="brand-name">EMS</span>
        </div>

        <div class="slogan">
          <div class="slogan-eyebrow">EMPLOYEE MANAGEMENT</div>
          <h1 class="login-title">员工信息<br />管理系统</h1>
          <p class="login-desc">面向现代企业的一体化人事管理平台 · 让组织运营更高效</p>
        </div>

        <div class="login-features">
          <div class="feature-item" v-for="(item, index) in features" :key="index">
            <div class="feature-check">
              <el-icon><Check /></el-icon>
            </div>
            <span>{{ item }}</span>
          </div>
        </div>

        <div class="left-footer">
          <span class="dot"></span>
          <span>v2.0 · Refined Edition</span>
        </div>
      </div>

      <!-- 右侧登录区 -->
      <div class="login-right">
        <div class="login-card">
          <div class="form-header">
            <div class="form-title">欢迎回来 👋</div>
            <div class="form-subtitle">请使用您的账号登录系统</div>
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
                <el-icon class="login-btn-icon" v-if="!loading"><ArrowRight /></el-icon>
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-tips">
            员工账号：<span>安之</span> · 密码：<span>123456</span>
            <br />
            管理账号：<span>superadmin</span> / <span>hradmin</span> / <span>deptmanager</span>
          </div>
        </div>
      </div>
    </div>

    <div class="copyright">© 2024 EMS Employee Management System · Crafted with care</div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Management, User, Lock, Check, ArrowRight } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { STORAGE_KEYS } from '@/utils/constants'
import { loadUserState } from '@/utils/permission'

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
  username: '安之',
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
    loadUserState()
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
  background:
    radial-gradient(at 20% 20%, rgba(99, 102, 241, 0.4) 0px, transparent 50%),
    radial-gradient(at 80% 80%, rgba(139, 92, 246, 0.32) 0px, transparent 50%),
    linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #0c0a1f 100%);
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
  font-family: var(--font-sans);
}

/* 装饰光斑 */
.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 12s ease-in-out infinite;
}

.shape-1 {
  width: 480px;
  height: 480px;
  background: #6366f1;
  top: -160px;
  left: -120px;
  animation-delay: 0s;
}

.shape-2 {
  width: 360px;
  height: 360px;
  background: #8b5cf6;
  bottom: -100px;
  right: 8%;
  animation-delay: 3s;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: #06b6d4;
  top: 35%;
  left: 18%;
  animation-delay: 5s;
  opacity: 0.4;
}

.shape-4 {
  width: 220px;
  height: 220px;
  background: #f472b6;
  bottom: 22%;
  left: 6%;
  animation-delay: 7s;
  opacity: 0.3;
}

.shape-5 {
  width: 280px;
  height: 280px;
  background: #34d399;
  top: 10%;
  right: 18%;
  animation-delay: 9s;
  opacity: 0.3;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-40px) scale(1.08);
  }
}

/* 网格纹理 */
.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(ellipse at center, #000 30%, transparent 80%);
  -webkit-mask-image: radial-gradient(ellipse at center, #000 30%, transparent 80%);
  pointer-events: none;
}

/* 登录卡片 */
.login-box {
  display: flex;
  width: 1020px;
  min-height: 580px;
  border-radius: 24px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow:
    0 40px 100px -20px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 1;
  animation: cardIn 0.7s var(--ease-out) both;
}

@keyframes cardIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 左侧 */
.login-left {
  flex: 1;
  padding: 56px 56px 56px 64px;
  color: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
  background:
    radial-gradient(at 0% 0%, rgba(99, 102, 241, 0.3) 0px, transparent 60%);
}

.login-left::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 1px;
  background: linear-gradient(180deg, transparent, rgba(255, 255, 255, 0.18), transparent);
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 72px;
  animation: fadeInLeft 0.6s 0.1s var(--ease-out) both;
}

.brand-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--brand-500), var(--violet-500));
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 8px 20px -4px rgba(99, 102, 241, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.brand-icon {
  font-size: 24px;
  color: #fff;
}

.brand-name {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.7) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.slogan {
  margin-bottom: 40px;
  animation: fadeInLeft 0.6s 0.2s var(--ease-out) both;
}

.slogan-eyebrow {
  font-size: 11px;
  letter-spacing: 3px;
  color: rgba(165, 180, 252, 0.8);
  font-weight: 700;
  margin-bottom: 16px;
  font-family: var(--font-mono);
}

.login-title {
  font-size: 42px;
  font-weight: 800;
  margin: 0 0 18px;
  line-height: 1.15;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, #fff 0%, #c7d2fe 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.login-desc {
  font-size: 14.5px;
  opacity: 0.7;
  line-height: 1.6;
  max-width: 380px;
  margin: 0;
}

.login-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: auto;
  animation: fadeInLeft 0.6s 0.3s var(--ease-out) both;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.95;
  font-weight: 500;
}

.feature-check {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(165, 180, 252, 0.2);
  border: 1px solid rgba(165, 180, 252, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-check .el-icon {
  font-size: 13px;
  color: #a5b4fc;
}

.left-footer {
  margin-top: 28px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.5px;
  font-family: var(--font-mono);
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 8px #34d399;
  animation: pulse 1.6s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-12px); }
  to   { opacity: 1; transform: translateX(0); }
}

/* 右侧 */
.login-right {
  width: 440px;
  padding: 50px 56px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.04);
}

.login-card {
  animation: fadeInRight 0.6s 0.15s var(--ease-out) both;
}

@keyframes fadeInRight {
  from { opacity: 0; transform: translateX(12px); }
  to   { opacity: 1; transform: translateX(0); }
}

.form-header {
  margin-bottom: 32px;
}

.form-title {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  letter-spacing: -0.01em;
}

.form-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
  font-weight: 500;
}

.input-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input {
  --el-input-height: 46px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px !important;
  background: rgba(255, 255, 255, 0.06) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important;
  transition: all 0.25s var(--ease-out);
}

.login-form :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 0 0 1px rgba(165, 180, 252, 0.4) inset !important;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.08) !important;
  box-shadow:
    0 0 0 1px rgba(165, 180, 252, 0.8) inset,
    0 0 0 4px rgba(99, 102, 241, 0.18) !important;
}

.login-form :deep(.el-input__inner) {
  color: #fff;
  font-weight: 500;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

.login-form :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.5);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 600;
  margin-top: 8px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--brand-500) 0%, var(--violet-500) 100%);
  border: none;
  letter-spacing: 0.5px;
  box-shadow:
    0 4px 14px -2px rgba(99, 102, 241, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.18);
  transition: all 0.25s var(--ease-out);
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent, rgba(255, 255, 255, 0.15), transparent);
  transform: translateX(-100%);
  transition: transform 0.6s var(--ease-out);
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow:
    0 10px 24px -4px rgba(99, 102, 241, 0.6),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.login-btn:hover::before {
  transform: translateX(100%);
}

.login-btn-icon {
  margin-left: 6px;
  transition: transform 0.25s var(--ease-out);
}

.login-btn:hover .login-btn-icon {
  transform: translateX(3px);
}

.login-tips {
  margin-top: 24px;
  text-align: center;
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.5);
}

.login-tips span {
  color: #a5b4fc;
  font-weight: 600;
  font-family: var(--font-mono);
  margin: 0 2px;
}

.copyright {
  position: absolute;
  bottom: 20px;
  font-size: 11.5px;
  color: rgba(255, 255, 255, 0.35);
  z-index: 1;
  letter-spacing: 0.5px;
}

/* 响应式 */
@media (max-width: 900px) {
  .login-box {
    flex-direction: column;
    width: 100%;
    max-width: 480px;
  }
  .login-left {
    padding: 40px 32px;
  }
  .login-left::after { display: none; }
  .brand { margin-bottom: 32px; }
  .slogan { margin-bottom: 24px; }
  .login-title { font-size: 30px; }
  .login-features { display: none; }
  .login-right { width: 100%; padding: 36px 32px; }
}
</style>
