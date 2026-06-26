<template>
  <el-dialog
    v-model="dialogVisible"
    title="批量调岗"
    width="520px"
    align-center
    :close-on-click-modal="false"
    @close="handleReset"
  >
    <div class="tip">
      <el-icon class="tip-icon"><InfoFilled /></el-icon>
      即将为 <b>{{ count }}</b> 名员工调整部门与职位
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="新部门" prop="department">
        <el-select
          v-model="form.department"
          placeholder="请选择新部门"
          filterable
          style="width: 100%"
          @change="handleDepartmentChange"
        >
          <el-option
            v-for="dept in departmentOptions"
            :key="dept.code"
            :label="dept.name"
            :value="dept.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="新职位" prop="position">
        <el-select
          v-model="form.position"
          placeholder="请先选择部门"
          filterable
          style="width: 100%"
          :disabled="!form.department"
        >
          <el-option
            v-for="pos in filteredPositions"
            :key="pos.code"
            :label="pos.name"
            :value="pos.code"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认调岗</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { employeeApi } from '@/api/employee'
import type { Dictionary } from '@/api/dictionary'

const props = defineProps<{
  visible: boolean
  ids: number[]
  departmentOptions: Dictionary[]
  positionOptionsAll: Dictionary[]
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
  department: '',
  position: '',
})

const rules: FormRules = {
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请选择职位', trigger: 'change' }],
}

const filteredPositions = computed(() => {
  if (!form.department) return []
  // 仅展示没有 dept 字段或 dept 匹配选中部门的职位
  return (props.positionOptionsAll || []).filter((p: any) => {
    const dept = (p as any).dept
    return !dept || dept === form.department
  })
})

const handleDepartmentChange = () => {
  form.position = ''
}

const handleReset = () => {
  form.department = ''
  form.position = ''
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await employeeApi.batchTransfer(props.ids, form.department, form.position)
    ElMessage.success(`已成功调整 ${res ?? count.value} 名员工的部门与职位`)
    dialogVisible.value = false
    emit('success')
  } catch (error: any) {
    ElMessage.error(error.message || '调岗失败')
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
  background: #ecf5ff;
  color: #409eff;
  border-radius: 6px;
  font-size: 13px;
}
.tip-icon { font-size: 16px; }
html.dark .tip {
  background: rgba(64, 158, 255, 0.1);
  color: #79bbff;
}
</style>
