<template>
  <el-dialog
    v-model="dialogVisible"
    title="修改密码"
    width="460px"
    align-center
    :close-on-click-modal="false"
    @close="handleReset"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" @keyup.enter="handleSubmit">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入原密码" :prefix-icon="Lock" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码（至少 6 位）" :prefix-icon="Lock" />
        <div class="password-strength">
          <div class="strength-bar" :class="strengthClass" :style="{ width: strengthPercent }" />
        </div>
        <div class="strength-text" :class="strengthClass">{{ strengthText }}</div>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码" :prefix-icon="Key" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :icon="Check" :loading="loading" @click="handleSubmit">确认修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Lock, Key, Check } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { useRouter } from 'vue-router'

const props = defineProps<{ visible: boolean }>()
const emit = defineEmits<{ (e: 'update:visible', val: boolean): void }>()

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_r, value, cb) => {
        if (value !== form.newPassword) {
          cb(new Error('两次输入的密码不一致'))
        } else {
          cb()
        }
      },
      trigger: 'blur',
    },
  ],
}

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const strengthLevel = computed(() => {
  const p = form.newPassword
  if (!p) return 0
  let level = 0
  if (p.length >= 6) level++
  if (p.length >= 10) level++
  if (/[A-Z]/.test(p) && /[a-z]/.test(p)) level++
  if (/\d/.test(p)) level++
  if (/[^A-Za-z0-9]/.test(p)) level++
  return Math.min(level, 4)
})

const strengthClass = computed(() => {
  return ['', 'weak', 'medium', 'strong', 'perfect'][strengthLevel.value]
})
const strengthPercent = computed(() => `${(strengthLevel.value / 4) * 100}%`)
const strengthText = computed(() => {
  if (!form.newPassword) return ''
  return ['', '弱：仅满足基本长度', '中：包含数字或字母', '强：大小写 + 数字', '完美：含特殊字符'][strengthLevel.value]
})

const handleReset = () => {
  form.oldPassword = ''
  form.newPassword = ''
  form.confirmPassword = ''
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    await userApi.changePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword,
    })
    ElMessage.success('密码修改成功，请重新登录')
    dialogVisible.value = false
    setTimeout(() => {
      localStorage.removeItem('ems_token')
      localStorage.removeItem('ems_refresh_token')
      localStorage.removeItem('ems_user')
      router.push('/login')
    }, 800)
  } catch (error: any) {
    ElMessage.error(error.message || '修改失败')
  } finally {
    loading.value = false
  }
}

watch(() => props.visible, (v) => {
  if (v) handleReset()
})
</script>

<style scoped>
.password-strength {
  margin-top: 6px;
  height: 4px;
  background: #ebeef5;
  border-radius: 2px;
  overflow: hidden;
}
.strength-bar {
  height: 100%;
  transition: width 0.3s, background 0.3s;
}
.strength-bar.weak { background: #f56c6c; }
.strength-bar.medium { background: #e6a23c; }
.strength-bar.strong { background: #67c23a; }
.strength-bar.perfect { background: linear-gradient(90deg, #67c23a, #409eff); }
.strength-text {
  font-size: 12px;
  margin-top: 4px;
  color: #909399;
}
.strength-text.weak { color: #f56c6c; }
.strength-text.medium { color: #e6a23c; }
.strength-text.strong { color: #67c23a; }
.strength-text.perfect { color: #409eff; }
</style>
