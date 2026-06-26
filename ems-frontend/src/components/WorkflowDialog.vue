<template>
  <el-dialog v-model="visible" :title="titleMap[type]" width="640px" align-center destroy-on-close @keydown.esc="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="workflow-form">
      <!-- 调岗 -->
      <template v-if="type === 'TRANSFER'">
        <el-form-item label="员工">
          <el-input :model-value="employeeName" disabled />
        </el-form-item>
        <el-form-item label="原部门">
          <el-input :model-value="deptName(form.fromDepartment)" disabled />
        </el-form-item>
        <el-form-item label="新部门" prop="toDepartment">
          <el-select v-model="form.toDepartment" placeholder="选择新部门" filterable>
            <el-option v-for="d in departmentOptions" :key="d.code" :label="d.name" :value="d.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="原职位">
          <el-input :model-value="posName(form.fromPosition)" disabled />
        </el-form-item>
        <el-form-item label="新职位" prop="toPosition">
          <el-select v-model="form.toPosition" placeholder="选择新职位" filterable :disabled="!form.toDepartment">
            <el-option v-for="p in filteredPositions" :key="p.code" :label="p.name" :value="p.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="新职级">
          <el-select v-model="form.toRank" placeholder="选择新职级" filterable clearable>
            <el-option v-for="r in rankOptions" :key="r.code" :label="r.name" :value="r.code" />
          </el-select>
        </el-form-item>
      </template>

      <!-- 调薪 -->
      <template v-else-if="type === 'ADJUST_SALARY'">
        <el-form-item label="员工">
          <el-input :model-value="employeeName" disabled />
        </el-form-item>
        <el-form-item label="原薪资">
          <el-input :model-value="`¥ ${formatSalary(form.fromSalary)}`" disabled />
        </el-form-item>
        <el-form-item label="新薪资" prop="toSalary">
          <el-input-number v-model="form.toSalary" :min="0" :precision="2" :step="500" class="full-width">
            <template #suffix>元/月</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="调整幅度" v-if="form.toSalary && form.fromSalary">
          <el-tag :type="adjustmentType">{{ adjustmentText }}</el-tag>
        </el-form-item>
      </template>

      <!-- 转正 -->
      <template v-else-if="type === 'CONFIRM'">
        <el-form-item label="员工">
          <el-input :model-value="employeeName" disabled />
        </el-form-item>
        <el-form-item label="转正日期" prop="effectiveDate">
          <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
      </template>

      <!-- 离职 -->
      <template v-else-if="type === 'LEAVE'">
        <el-form-item label="员工">
          <el-input :model-value="employeeName" disabled />
        </el-form-item>
        <el-form-item label="离职类型">
          <el-select v-model="form.leave!.leaveType" class="full-width">
            <el-option value="主动离职" label="主动离职" />
            <el-option value="被动辞退" label="被动辞退" />
            <el-option value="合同到期" label="合同到期" />
            <el-option value="协商解除" label="协商解除" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请日期" prop="applyDate">
          <el-date-picker v-model="form.leave!.applyDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
        <el-form-item label="最后工作日" prop="lastWorkDate">
          <el-date-picker v-model="form.leave!.lastWorkDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
        <el-form-item label="离职日期" prop="leaveDate">
          <el-date-picker v-model="form.leave!.leaveDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
        <el-form-item label="工作交接">
          <el-select v-model="form.leave!.handoverTo" filterable placeholder="交接人" class="full-width" clearable>
            <el-option v-for="e in handoverCandidates" :key="e.id" :label="`${e.name} (${e.employeeNo})`" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="交接说明">
          <el-input v-model="form.leave!.handoverNote" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <el-form-item label="离职原因">
          <el-input v-model="form.leave!.reason" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </template>

      <el-form-item label="生效日期" prop="effectiveDate" v-if="type !== 'CONFIRM' && type !== 'LEAVE'">
        <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" class="full-width" />
      </el-form-item>
      <el-form-item label="申请原因" v-if="type !== 'LEAVE'">
        <el-input v-model="form.reason" type="textarea" :rows="2" placeholder="选填" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" placeholder="选填" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { workflowApi, type WorkflowChange } from '@/api/workflow'
import { dictionaryApi, type Dictionary } from '@/api/dictionary'
import { employeeApi, type Employee } from '@/api/employee'
import { formatSalary } from '@/utils/common'
import { getCurrentUser } from '@/utils/auth'

const props = defineProps<{
  visible: boolean
  type: 'TRANSFER' | 'ADJUST_SALARY' | 'CONFIRM' | 'LEAVE'
  employee: Employee
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
  (e: 'success'): void
}>()

const visible = computed({
  get: () => props.visible,
  set: (v) => emit('update:visible', v),
})

const titleMap = {
  TRANSFER: '调岗申请',
  ADJUST_SALARY: '调薪申请',
  CONFIRM: '转正申请',
  LEAVE: '离职申请',
}

const employeeName = computed(() => props.employee ? `${props.employee.name} (${props.employee.employeeNo})` : '')

const deptName = (code?: string) => {
  if (!code) return '-'
  return departmentOptions.value.find(d => d.code === code)?.name || code
}
const posName = (code?: string) => {
  if (!code) return '-'
  return positionOptions.value.find(p => p.code === code)?.name || code
}

const formRef = ref<FormInstance>()
const submitting = ref(false)
const departmentOptions = ref<Dictionary[]>([])
const positionOptions = ref<Dictionary[]>([])
const rankOptions = ref<Dictionary[]>([])
const handoverCandidates = ref<Employee[]>([])

const filteredPositions = computed(() => {
  if (!form.toDepartment) return positionOptions.value
  return positionOptions.value.filter(p => (p as any).dept === form.toDepartment || !(p as any).dept)
})

const form = reactive<WorkflowChange>({
  employeeId: 0,
  changeType: 'TRANSFER',
  effectiveDate: new Date().toISOString().substring(0, 10),
  fromDepartment: '',
  toDepartment: '',
  fromPosition: '',
  toPosition: '',
  fromRank: '',
  toRank: '',
  fromSalary: 0,
  toSalary: 0,
  leave: {
    leaveType: '主动离职',
  },
})

const rules: FormRules = {
  effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
  toDepartment: [{ required: true, message: '请选择新部门', trigger: 'change' }],
  toPosition: [{ required: true, message: '请选择新职位', trigger: 'change' }],
  toSalary: [{ required: true, message: '请填写新薪资', trigger: 'change' }],
}

const adjustmentText = computed(() => {
  const oldS = form.fromSalary || 0
  const newS = form.toSalary || 0
  if (!oldS) return '-'
  const diff = ((newS - oldS) / oldS * 100).toFixed(2)
  return (newS >= oldS ? '+' : '') + diff + '%'
})

const adjustmentType = computed(() => {
  const oldS = form.fromSalary || 0
  const newS = form.toSalary || 0
  if (newS > oldS) return 'success'
  if (newS < oldS) return 'danger'
  return 'info'
})

const initForm = () => {
  const e = props.employee
  if (!e) return
  form.employeeId = e.id!
  form.changeType = props.type
  form.fromDepartment = e.department || ''
  form.toDepartment = e.department || ''
  form.fromPosition = e.position || ''
  form.toPosition = e.position || ''
  form.fromRank = e.rank || ''
  form.toRank = e.rank || ''
  form.fromSalary = e.salary || 0
  form.toSalary = e.salary || 0
  form.effectiveDate = new Date().toISOString().substring(0, 10)
  form.reason = ''
  form.remark = ''
  const u = getCurrentUser()
  form.applicant = u?.nickname || u?.username || 'admin'
  form.applyDate = new Date().toISOString().substring(0, 10)
  form.approver = 'admin'
  form.approveDate = new Date().toISOString().substring(0, 10)
  if (!form.leave) {
    form.leave = { leaveType: '主动离职' }
  } else {
    form.leave.leaveType = '主动离职'
    form.leave.applyDate = new Date().toISOString().substring(0, 10)
    form.leave.lastWorkDate = new Date().toISOString().substring(0, 10)
    form.leave.leaveDate = new Date().toISOString().substring(0, 10)
  }
}

const loadDictionaries = async () => {
  const [dept, pos, rank] = await Promise.all([
    dictionaryApi.listByType('department'),
    dictionaryApi.listByType('position'),
    dictionaryApi.listByType('rank'),
  ])
  departmentOptions.value = dept
  positionOptions.value = pos
  rankOptions.value = rank
}

const loadHandoverCandidates = async () => {
  const res = await employeeApi.page({ page: 1, size: 100, status: 1 })
  handoverCandidates.value = (res.records || []).filter(e => e.id !== props.employee?.id)
}

watch(() => props.visible, (v) => {
  if (v) {
    initForm()
    loadDictionaries()
    if (props.type === 'LEAVE') loadHandoverCandidates()
  }
})

const handleClose = () => {
  visible.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await workflowApi.submit(form)
    ElMessage.success('申请已提交')
    visible.value = false
    emit('success')
  } catch (e: any) {
    ElMessage.error(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.workflow-form { padding-right: 12px; }
.full-width { width: 100%; }
</style>
