<template>
  <el-dialog
    v-model="dialogVisible"
    title="批量调薪"
    width="540px"
    align-center
    :close-on-click-modal="false"
    @close="handleReset"
  >
    <div class="tip">
      <el-icon class="tip-icon"><InfoFilled /></el-icon>
      即将为 <b>{{ count }}</b> 名员工调整薪资
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="调整模式" prop="mode">
        <el-radio-group v-model="form.mode">
          <el-radio-button value="fixed">固定金额</el-radio-button>
          <el-radio-button value="percent">百分比</el-radio-button>
          <el-radio-button value="set">直接设置</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="amountLabel" prop="amount">
        <el-input-number
          v-model="form.amount"
          :min="0"
          :precision="form.mode === 'percent' ? 2 : 2"
          :step="form.mode === 'percent' ? 1 : 100"
          controls-position="right"
          style="width: 200px"
        />
        <span class="unit">{{ amountUnit }}</span>
        <span v-if="form.mode === 'fixed'" class="hint">（如：+500 加薪，-500 减薪）</span>
        <span v-else-if="form.mode === 'percent'" class="hint">（如：10 表示 +10%，-10 表示 -10%）</span>
        <span v-else class="hint">（覆盖所有选中员工的当前薪资）</span>
      </el-form-item>
      <el-form-item label="方向" prop="direction" v-if="form.mode !== 'set'">
        <el-radio-group v-model="form.direction">
          <el-radio value="up"><el-icon><CaretTop /></el-icon> 上调</el-radio>
          <el-radio value="down"><el-icon><CaretBottom /></el-icon> 下调</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="form.mode === 'fixed' && previewAmount !== null">
        <div class="preview">
          <el-icon class="preview-icon"><View /></el-icon>
          示例：当前薪资 ¥8,000 {{ form.direction === 'up' ? '+' : '-' }} ¥{{ formatNumber(form.amount) }}
          → <b>¥{{ formatNumber(previewAmount) }}</b>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认调薪</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { InfoFilled, CaretTop, CaretBottom, View } from '@element-plus/icons-vue'
import { employeeApi } from '@/api/employee'

const props = defineProps<{
  visible: boolean
  ids: number[]
}>()

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const formRef = ref<FormInstance>()
const loading = ref(false)
const count = computed(() => props.ids.length)

const form = reactive({
  mode: 'fixed' as 'fixed' | 'percent' | 'set',
  amount: 500,
  direction: 'up' as 'up' | 'down',
})

const rules: FormRules = {
  mode: [{ required: true, message: '请选择调整模式', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入调整值', trigger: 'blur' },
    {
      validator: (_r, value, cb) => {
        if (value === null || value === undefined || value === '') {
          cb(new Error('请输入调整值'))
        } else if (Number(value) <= 0) {
          cb(new Error('调整值必须大于 0'))
        } else {
          cb()
        }
      },
      trigger: 'blur',
    },
  ],
}

const amountLabel = computed(() => {
  if (form.mode === 'fixed') return '调整金额'
  if (form.mode === 'percent') return '调整百分比'
  return '新薪资'
})

const amountUnit = computed(() => (form.mode === 'percent' ? '%' : '元'))

const previewAmount = computed<number | null>(() => {
  if (form.mode !== 'fixed' || !form.amount) return null
  const sign = form.direction === 'up' ? 1 : -1
  return 8000 + sign * Number(form.amount)
})

const formatNumber = (n: number) => Number(n || 0).toLocaleString('zh-CN', { maximumFractionDigits: 2 })

const handleReset = () => {
  form.mode = 'fixed'
  form.amount = 500
  form.direction = 'up'
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    const amount = form.mode === 'set' || form.direction === 'up'
      ? Number(form.amount)
      : -Number(form.amount)
    const res = await employeeApi.batchAdjustSalary(props.ids, form.mode, amount)
    ElMessage.success(`已成功调整 ${res ?? count.value} 名员工的薪资`)
    dialogVisible.value = false
    emit('success')
  } catch (error: any) {
    ElMessage.error(error.message || '调薪失败')
  } finally {
    loading.value = false
  }
}

watch(() => props.visible, (v) => {
  if (v) handleReset()
})
</script>

<style scoped>
.tip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  margin-bottom: 16px;
  background: #fdf6ec;
  color: #e6a23c;
  border-radius: 6px;
  font-size: 13px;
}
.tip-icon { font-size: 16px; }
.unit {
  margin-left: 8px;
  color: #606266;
  font-size: 13px;
}
.hint {
  margin-left: 12px;
  color: #909399;
  font-size: 12px;
}
.preview {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f0f9eb;
  color: #67c23a;
  border-radius: 6px;
  font-size: 13px;
  width: 100%;
}
.preview-icon { font-size: 16px; }
html.dark .tip {
  background: rgba(230, 162, 60, 0.1);
  color: #eebe77;
}
html.dark .preview {
  background: rgba(103, 194, 58, 0.1);
  color: #95d475;
}
</style>
