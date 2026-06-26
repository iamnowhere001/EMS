<template>
  <div class="salary-page ems-page">
    <div class="page-header">
      <h2>薪资管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleGenerate">生成工资</el-button>
        <el-button @click="handleBatchConfirm" :disabled="!selectedIds.length">批量确认</el-button>
        <el-button type="success" @click="handleBatchPay" :disabled="!selectedIds.length">批量发放</el-button>
      </div>
    </div>

    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="员工">
          <el-select v-model="searchForm.employeeId" placeholder="选择员工" clearable filterable>
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份">
          <el-date-picker v-model="searchForm.yearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option :value="0" label="草稿" />
            <el-option :value="1" label="已确认" />
            <el-option :value="2" label="已发放" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="employeeId" label="员工" width="120">
        <template #default="{ row }">{{ getEmployeeName(row.employeeId) }}</template>
      </el-table-column>
      <el-table-column prop="yearMonth" label="月份" width="100" />
      <el-table-column prop="baseSalary" label="基本工资" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.baseSalary) }}</template>
      </el-table-column>
      <el-table-column prop="performanceSalary" label="绩效工资" width="120" align="right">
        <template #default="{ row }">{{ formatMoney(row.performanceSalary) }}</template>
      </el-table-column>
      <el-table-column prop="allowance" label="津贴" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.allowance) }}</template>
      </el-table-column>
      <el-table-column prop="subsidy" label="补贴" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.subsidy) }}</template>
      </el-table-column>
      <el-table-column prop="overtimePay" label="加班费" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.overtimePay) }}</template>
      </el-table-column>
      <el-table-column prop="deduction" label="扣款" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.deduction) }}</template>
      </el-table-column>
      <el-table-column prop="socialSecurityPersonal" label="社保" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.socialSecurityPersonal) }}</template>
      </el-table-column>
      <el-table-column prop="housingFundPersonal" label="公积金" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.housingFundPersonal) }}</template>
      </el-table-column>
      <el-table-column prop="tax" label="个税" width="100" align="right">
        <template #default="{ row }">{{ formatMoney(row.tax) }}</template>
      </el-table-column>
      <el-table-column prop="actualSalary" label="实发工资" width="120" align="right">
        <template #default="{ row }">
          <span style="font-weight: bold; color: #67c23a">{{ formatMoney(row.actualSalary) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 0" size="small" type="primary" @click="handleConfirm(row)">确认</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <div class="pagination-left">
        <span class="pagination-total">
          共 <em class="ems-mono">{{ pagination.total }}</em> 条记录
        </span>
      </div>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        background
        layout="sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="dialogVisible" title="编辑工资" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="月份">
          <el-input v-model="form.yearMonth" disabled />
        </el-form-item>
        <el-form-item label="基本工资">
          <el-input-number v-model="form.baseSalary" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="绩效工资">
          <el-input-number v-model="form.performanceSalary" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="岗位津贴">
          <el-input-number v-model="form.allowance" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="补贴">
          <el-input-number v-model="form.subsidy" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="加班费">
          <el-input-number v-model="form.overtimePay" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="扣款">
          <el-input-number v-model="form.deduction" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="generateDialogVisible" title="生成工资" width="400px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="月份">
          <el-date-picker v-model="generateForm.yearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { salaryApi, type SalaryRecord } from '@/api/salary'
import { employeeApi, type Employee } from '@/api/employee'

const loading = ref(false)
const tableData = ref<SalaryRecord[]>([])
const employees = ref<Employee[]>([])
const dialogVisible = ref(false)
const generateDialogVisible = ref(false)
const selectedIds = ref<number[]>([])

const searchForm = reactive({
  employeeId: undefined as number | undefined,
  yearMonth: '',
  status: undefined as number | undefined,
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const form = reactive<SalaryRecord>({
  id: undefined,
  employeeId: 0,
  yearMonth: '',
  baseSalary: 0,
  performanceSalary: 0,
  allowance: 0,
  subsidy: 0,
  overtimePay: 0,
  deduction: 0,
  socialSecurityPersonal: 0,
  housingFundPersonal: 0,
  tax: 0,
  actualSalary: 0,
  status: 0,
  remark: '',
})

const generateForm = reactive({
  yearMonth: '',
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await salaryApi.recordPage({
      employeeId: searchForm.employeeId,
      yearMonth: searchForm.yearMonth,
      status: searchForm.status,
      page: pagination.page,
      size: pagination.size,
    })
    tableData.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const loadEmployees = async () => {
  const res = await employeeApi.list()
  employees.value = res
}

const resetSearch = () => {
  searchForm.employeeId = undefined
  searchForm.yearMonth = ''
  searchForm.status = undefined
  pagination.page = 1
  loadData()
}

const handleSelectionChange = (rows: SalaryRecord[]) => {
  selectedIds.value = rows.map(r => r.id!)
}

const handleGenerate = () => {
  generateForm.yearMonth = ''
  generateDialogVisible.value = true
}

const submitGenerate = async () => {
  if (!generateForm.yearMonth) {
    ElMessage.warning('请选择月份')
    return
  }
  await salaryApi.generate(generateForm.yearMonth)
  ElMessage.success('工资生成成功')
  generateDialogVisible.value = false
  loadData()
}

const handleBatchConfirm = async () => {
  await ElMessageBox.confirm(`确定确认选中的 ${selectedIds.value.length} 条记录吗？`, '提示', { type: 'warning' })
  await salaryApi.batchConfirm(selectedIds.value)
  ElMessage.success('批量确认成功')
  loadData()
}

const handleBatchPay = async () => {
  await ElMessageBox.confirm(`确定发放选中的 ${selectedIds.value.length} 条记录吗？`, '提示', { type: 'warning' })
  await salaryApi.batchPay(selectedIds.value)
  ElMessage.success('批量发放成功')
  loadData()
}

const handleEdit = (row: SalaryRecord) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleConfirm = async (row: SalaryRecord) => {
  await ElMessageBox.confirm('确定确认该工资记录吗？', '提示', { type: 'warning' })
  await salaryApi.confirm(row.id!)
  ElMessage.success('确认成功')
  loadData()
}

const submitForm = async () => {
  await salaryApi.updateRecord(form)
  ElMessage.success('更新成功')
  dialogVisible.value = false
  loadData()
}

const formatMoney = (value: number) => {
  if (value === null || value === undefined) return '¥0.00'
  return `¥${value.toFixed(2)}`
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '草稿', 1: '已确认', 2: '已发放' }
  return map[status] || '未知'
}

const getEmployeeName = (id: number) => {
  const emp = employees.value.find(e => e.id === id)
  return emp?.name || id
}

onMounted(() => {
  loadEmployees()
  loadData()
})
</script>

<style scoped>
.salary-page {
  min-height: calc(100vh - 56px);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.page-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}
.search-area {
  margin-bottom: 12px;
  padding: 14px 18px;
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-subtle);
}

.pagination-wrapper {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-default);
}

.pagination-left {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 12.5px;
  color: var(--text-secondary);
  font-weight: 500;
  flex-shrink: 0;
}

.pagination-left em {
  font-style: normal;
  color: var(--text-primary);
  font-weight: 700;
  font-size: 14px;
  margin: 0 2px;
}
</style>
