<template>
  <div class="salary-page ems-page">
    <div class="page-header">
      <div class="header-left">
        <div class="title-icon">
        <el-icon><Money /></el-icon>
        </div>
        <div class="title-text">
          <h1>薪酬福利</h1>
          <p class="subtitle">薪资结算 · 社保公积金配置</p>
        </div>
      </div>
      <div class="header-right">
        <template v-if="activeTab === 'salary'">
          <el-button v-permission="'salary:manage'" type="primary" :icon="MagicStick" @click="handleGenerate">生成工资</el-button>
          <el-button v-permission="'salary:confirm'" type="warning" :disabled="!selectedIds.length" :icon="CircleCheck" @click="handleBatchConfirm">批量确认</el-button>
          <el-button v-permission="'salary:pay'" type="success" :disabled="!selectedIds.length" :icon="Wallet" @click="handleBatchPay">批量发放</el-button>
        </template>
        <template v-else>
          <el-button v-permission="'salary:manage'" type="primary" :icon="Edit" @click="handleEditSs">编辑配置</el-button>
        </template>
      </div>
    </div>

    <div class="content-card">
      <el-tabs v-model="activeTab" class="salary-tabs">
        <el-tab-pane label="薪资结算" name="salary">
          <div class="search-section">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="员工">
                <el-select v-model="searchForm.employeeId" placeholder="选择员工" clearable filterable style="width: 160px">
                  <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="月份">
                <el-date-picker v-model="searchForm.yearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 130px">
                  <el-option :value="0" label="草稿" />
                  <el-option :value="1" label="已确认" />
                  <el-option :value="2" label="已发放" />
                </el-select>
              </el-form-item>
              <div class="search-actions">
                <el-button type="primary" :icon="Search" @click="loadSalaryData">查询</el-button>
                <el-button :icon="RefreshRight" @click="resetSearch">重置</el-button>
              </div>
            </el-form>
          </div>

          <div v-if="selectedIds.length > 0" class="selected-bar">
            <span class="selected-info">
              <el-icon class="selected-icon"><Check /></el-icon>
              已选择 <em>{{ selectedIds.length }}</em> 条记录
            </span>
            <el-button type="primary" link size="small" @click="selectedIds = []">取消选择</el-button>
          </div>

          <el-table :data="tableData" border v-loading="salaryLoading" class="salary-table" @selection-change="handleSelectionChange">
            <el-table-column v-if="hasPermission(['salary:confirm', 'salary:pay'])" type="selection" width="55" />
            <el-table-column prop="employeeId" label="员工" width="140">
              <template #default="{ row }">
                <div class="employee-cell">
                  <el-avatar :size="28" class="emp-avatar">{{ getEmployeeName(row.employeeId)?.charAt(0) }}</el-avatar>
                  <span class="emp-name">{{ getEmployeeName(row.employeeId) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="yearMonth" label="月份" width="100" align="center" />
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
              <template #default="{ row }">
                <span class="text-danger">{{ formatMoney(row.deduction) }}</span>
              </template>
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
            <el-table-column prop="actualSalary" label="实发工资" width="130" align="right" fixed="right">
              <template #default="{ row }">
                <span class="actual-salary">{{ formatMoney(row.actualSalary) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center" fixed="right">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" effect="light" size="small" round>
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right" align="center">
              <template #default="{ row }">
                <el-button v-permission="'salary:manage'" size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button v-if="row.status === 0 && hasPermission('salary:confirm')" size="small" type="success" link @click="handleConfirm(row)">确认</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="tableData.length === 0 && !salaryLoading" class="empty-state">
            <el-empty description="暂无薪资数据" :image-size="120">
              <el-button v-permission="'salary:manage'" type="primary" :icon="MagicStick" @click="handleGenerate">生成工资</el-button>
            </el-empty>
          </div>

          <div class="pagination-wrapper">
            <div class="pagination-left">
              <span class="pagination-total">
                共 <em class="ems-mono">{{ pagination.total }}</em> 条记录
              </span>
              <span v-if="selectedIds.length > 0" class="pagination-selected">
                已选 <em class="ems-mono">{{ selectedIds.length }}</em> 项
              </span>
            </div>
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]"
              background
              layout="sizes, prev, pager, next, jumper"
              @size-change="loadSalaryData"
              @current-change="loadSalaryData"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="社保配置" name="social-security">
          <div class="ss-toolbar">
            <el-form :inline="true" class="ss-month-form">
              <el-form-item label="配置月份">
                <el-date-picker v-model="ssYearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" @change="loadSsConfig" />
              </el-form-item>
            </el-form>
          </div>

          <div v-if="ssConfig" class="ss-grid">
            <div class="ss-card pension">
              <div class="ss-card-header">
                <div class="ss-icon-wrap">
                  <el-icon><Coin /></el-icon>
                </div>
                <div class="ss-card-title">基本养老保险</div>
              </div>
              <div class="ss-card-body">
                <div class="ss-item">
                  <span class="ss-label">缴费基数</span>
                  <span class="ss-value ems-mono">¥{{ (ssConfig.pensionBase || 0).toFixed(2) }}</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">公司比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.pensionCompanyRate || 0 }}%</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">个人比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.pensionPersonalRate || 0 }}%</span>
                </div>
              </div>
            </div>

            <div class="ss-card medical">
              <div class="ss-card-header">
                <div class="ss-icon-wrap">
                  <el-icon><FirstAidKit /></el-icon>
                </div>
                <div class="ss-card-title">基本医疗保险</div>
              </div>
              <div class="ss-card-body">
                <div class="ss-item">
                  <span class="ss-label">缴费基数</span>
                  <span class="ss-value ems-mono">¥{{ (ssConfig.medicalBase || 0).toFixed(2) }}</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">公司比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.medicalCompanyRate || 0 }}%</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">个人比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.medicalPersonalRate || 0 }}%</span>
                </div>
              </div>
            </div>

            <div class="ss-card unemployment">
              <div class="ss-card-header">
                <div class="ss-icon-wrap">
                  <el-icon><Briefcase /></el-icon>
                </div>
                <div class="ss-card-title">失业保险</div>
              </div>
              <div class="ss-card-body">
                <div class="ss-item">
                  <span class="ss-label">缴费基数</span>
                  <span class="ss-value ems-mono">¥{{ (ssConfig.unemploymentBase || 0).toFixed(2) }}</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">公司比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.unemploymentCompanyRate || 0 }}%</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">个人比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.unemploymentPersonalRate || 0 }}%</span>
                </div>
              </div>
            </div>

            <div class="ss-card injury">
              <div class="ss-card-header">
                <div class="ss-icon-wrap">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="ss-card-title">工伤保险</div>
              </div>
              <div class="ss-card-body">
                <div class="ss-item">
                  <span class="ss-label">缴费基数</span>
                  <span class="ss-value ems-mono">¥{{ (ssConfig.injuryBase || 0).toFixed(2) }}</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">公司比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.injuryCompanyRate || 0 }}%</span>
                </div>
              </div>
            </div>

            <div class="ss-card maternity">
              <div class="ss-card-header">
                <div class="ss-icon-wrap">
                  <el-icon><Cherry /></el-icon>
                </div>
                <div class="ss-card-title">生育保险</div>
              </div>
              <div class="ss-card-body">
                <div class="ss-item">
                  <span class="ss-label">缴费基数</span>
                  <span class="ss-value ems-mono">¥{{ (ssConfig.maternityBase || 0).toFixed(2) }}</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">公司比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.maternityCompanyRate || 0 }}%</span>
                </div>
              </div>
            </div>

            <div class="ss-card housing">
              <div class="ss-card-header">
                <div class="ss-icon-wrap">
                  <el-icon><OfficeBuilding /></el-icon>
                </div>
                <div class="ss-card-title">住房公积金</div>
              </div>
              <div class="ss-card-body">
                <div class="ss-item">
                  <span class="ss-label">缴费基数</span>
                  <span class="ss-value ems-mono">¥{{ (ssConfig.housingFundBase || 0).toFixed(2) }}</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">公司比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.housingFundCompanyRate || 0 }}%</span>
                </div>
                <div class="ss-item">
                  <span class="ss-label">个人比例</span>
                  <span class="ss-value ems-mono">{{ ssConfig.housingFundPersonalRate || 0 }}%</span>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <el-empty description="暂无配置数据" :image-size="120">
              <el-button type="primary" :icon="Edit" @click="handleEditSs">新增配置</el-button>
            </el-empty>
          </div>
        </el-tab-pane>
      </el-tabs>
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

    <el-dialog v-model="generateDialogVisible" title="生成工资" width="420px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="月份">
          <el-date-picker v-model="generateForm.yearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" :icon="MagicStick" @click="submitGenerate">生成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="ssDialogVisible" title="编辑社保公积金配置" width="680px">
      <el-form :model="ssForm" label-width="130px">
        <el-divider content-position="left">养老保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="ssForm.pensionBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="ssForm.pensionCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="ssForm.pensionPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">医疗保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="ssForm.medicalBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="ssForm.medicalCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="ssForm.medicalPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">失业保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="ssForm.unemploymentBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="ssForm.unemploymentCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="ssForm.unemploymentPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">工伤保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="ssForm.injuryBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="ssForm.injuryCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">生育保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="ssForm.maternityBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="ssForm.maternityCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">住房公积金</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="ssForm.housingFundBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="ssForm.housingFundCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="ssForm.housingFundPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ssDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSsForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Money, Wallet, Search, RefreshRight, MagicStick, CircleCheck, Check, Edit,
  Coin, FirstAidKit, Briefcase, Warning, Cherry, OfficeBuilding,
} from '@element-plus/icons-vue'
import { salaryApi, type SalaryRecord } from '@/api/salary'
import { employeeApi, type Employee } from '@/api/employee'
import { socialSecurityApi, type SocialSecurityConfig } from '@/api/socialSecurity'
import { hasPermission } from '@/utils/permission'

const activeTab = ref('salary')
const salaryLoading = ref(false)
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

const ssConfig = ref<SocialSecurityConfig | null>(null)
const ssDialogVisible = ref(false)
const ssYearMonth = ref(new Date().toISOString().slice(0, 7))

const ssForm = reactive<SocialSecurityConfig>({
  yearMonth: '',
  pensionBase: 0,
  pensionCompanyRate: 16,
  pensionPersonalRate: 8,
  medicalBase: 0,
  medicalCompanyRate: 9.5,
  medicalPersonalRate: 2,
  unemploymentBase: 0,
  unemploymentCompanyRate: 0.5,
  unemploymentPersonalRate: 0.5,
  injuryBase: 0,
  injuryCompanyRate: 0.4,
  maternityBase: 0,
  maternityCompanyRate: 0.8,
  housingFundBase: 0,
  housingFundCompanyRate: 12,
  housingFundPersonalRate: 12,
})

const loadSalaryData = async () => {
  salaryLoading.value = true
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
    salaryLoading.value = false
  }
}

const loadEmployees = async () => {
  const res = await employeeApi.list()
  employees.value = res
}

const loadSsConfig = async () => {
  if (!ssYearMonth.value) return
  try {
    const res = await socialSecurityApi.getByYearMonth(ssYearMonth.value)
    ssConfig.value = res
  } catch (e) {
    ssConfig.value = null
  }
}

const resetSearch = () => {
  searchForm.employeeId = undefined
  searchForm.yearMonth = ''
  searchForm.status = undefined
  pagination.page = 1
  loadSalaryData()
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
  loadSalaryData()
}

const handleBatchConfirm = async () => {
  await ElMessageBox.confirm(`确定确认选中的 ${selectedIds.value.length} 条记录吗？`, '提示', { type: 'warning' })
  await salaryApi.batchConfirm(selectedIds.value)
  ElMessage.success('批量确认成功')
  loadSalaryData()
}

const handleBatchPay = async () => {
  await ElMessageBox.confirm(`确定发放选中的 ${selectedIds.value.length} 条记录吗？`, '提示', { type: 'warning' })
  await salaryApi.batchPay(selectedIds.value)
  ElMessage.success('批量发放成功')
  loadSalaryData()
}

const handleEdit = (row: SalaryRecord) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleConfirm = async (row: SalaryRecord) => {
  await ElMessageBox.confirm('确定确认该工资记录吗？', '提示', { type: 'warning' })
  await salaryApi.confirm(row.id!)
  ElMessage.success('确认成功')
  loadSalaryData()
}

const submitForm = async () => {
  await salaryApi.updateRecord(form)
  ElMessage.success('更新成功')
  dialogVisible.value = false
  loadSalaryData()
}

const handleEditSs = () => {
  if (ssConfig.value) {
    Object.assign(ssForm, ssConfig.value)
  } else {
    Object.assign(ssForm, {
      yearMonth: ssYearMonth.value,
      pensionBase: 0, pensionCompanyRate: 16, pensionPersonalRate: 8,
      medicalBase: 0, medicalCompanyRate: 9.5, medicalPersonalRate: 2,
      unemploymentBase: 0, unemploymentCompanyRate: 0.5, unemploymentPersonalRate: 0.5,
      injuryBase: 0, injuryCompanyRate: 0.4,
      maternityBase: 0, maternityCompanyRate: 0.8,
      housingFundBase: 0, housingFundCompanyRate: 12, housingFundPersonalRate: 12,
    })
  }
  ssDialogVisible.value = true
}

const submitSsForm = async () => {
  ssForm.yearMonth = ssYearMonth.value
  await socialSecurityApi.saveOrUpdate(ssForm)
  ElMessage.success('保存成功')
  ssDialogVisible.value = false
  loadSsConfig()
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
  return emp?.name || `员工#${id}`
}

onMounted(() => {
  loadEmployees()
  loadSalaryData()
  loadSsConfig()
})
</script>

<style scoped>
.salary-page {
  height: calc(100vh - 56px);
  overflow-y: auto;
  box-sizing: border-box;
  padding: 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background:
    radial-gradient(at 0% 0%, rgba(16, 185, 129, 0.05) 0px, transparent 50%),
    radial-gradient(at 100% 0%, rgba(14, 165, 233, 0.05) 0px, transparent 50%),
    var(--bg-page);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  box-shadow: 0 8px 20px -6px rgba(16, 185, 129, 0.5);
  transition: transform 0.3s var(--ease-spring);
}

.title-icon:hover {
  transform: scale(1.08) rotate(-4deg);
}

.title-text h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.header-right {
  display: flex;
  gap: 8px;
}

.content-card {
  flex: 1;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.salary-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.salary-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-subtle);
}

.salary-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.salary-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
  font-weight: 500;
}

.salary-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
  padding: 16px 20px;
}

.salary-tabs :deep(.el-tab-pane) {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.search-section {
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 12px;
  border: 1px solid var(--border-subtle);
}

.search-form {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
}

.search-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.selected-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  background: var(--brand-50);
  border: 1px solid var(--brand-100);
  border-radius: var(--radius-md);
  margin-bottom: 12px;
}

.selected-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--brand-700);
  font-weight: 500;
}

.selected-icon { font-size: 16px; }

.selected-info em {
  font-style: normal;
  font-weight: 700;
  font-size: 15px;
}

.salary-table {
  border-radius: var(--radius-md);
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-subtle);
}

.salary-table :deep(.el-table__body-wrapper) { overflow-x: auto; }

.salary-table :deep(.el-table__fixed-right) {
  background: var(--bg-soft);
  z-index: 10;
  left: auto;
  right: 0;
  border-left: 1px solid var(--border-subtle);
}

.employee-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.emp-avatar {
  background: linear-gradient(135deg, var(--brand-500), var(--violet-500)) !important;
  color: #fff;
  font-weight: 600;
  font-size: 12px;
}

.emp-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.actual-salary {
  font-weight: 700;
  color: var(--emerald-600);
  font-family: var(--font-mono);
}

.text-danger { color: var(--rose-500); }

.empty-state { padding: 32px 0; }

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

.pagination-selected {
  background: var(--brand-50);
  color: var(--brand-700);
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: 12px;
  border: 1px solid var(--brand-100);
}

.ss-toolbar {
  margin-bottom: 16px;
}

.ss-month-form {
  margin: 0;
}

.ss-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.ss-card {
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  padding: 18px;
  transition: all 0.25s var(--ease-out);
  position: relative;
  overflow: hidden;
}

.ss-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  opacity: 0;
  transition: opacity 0.25s;
}

.ss-card.pension::before { background: linear-gradient(90deg, #6366f1, #8b5cf6); }
.ss-card.medical::before { background: linear-gradient(90deg, #ec4899, #f43f5e); }
.ss-card.unemployment::before { background: linear-gradient(90deg, #f59e0b, #d97706); }
.ss-card.injury::before { background: linear-gradient(90deg, #ef4444, #dc2626); }
.ss-card.maternity::before { background: linear-gradient(90deg, #ec4899, #db2777); }
.ss-card.housing::before { background: linear-gradient(90deg, #0ea5e9, #0284c7); }

.ss-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
  border-color: transparent;
}

.ss-card:hover::before { opacity: 1; }

.ss-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px dashed var(--border-default);
}

.ss-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
}

.ss-card.pension .ss-icon-wrap { background: linear-gradient(135deg, #6366f1, #8b5cf6); }
.ss-card.medical .ss-icon-wrap { background: linear-gradient(135deg, #ec4899, #f43f5e); }
.ss-card.unemployment .ss-icon-wrap { background: linear-gradient(135deg, #f59e0b, #d97706); }
.ss-card.injury .ss-icon-wrap { background: linear-gradient(135deg, #ef4444, #dc2626); }
.ss-card.maternity .ss-icon-wrap { background: linear-gradient(135deg, #ec4899, #db2777); }
.ss-card.housing .ss-icon-wrap { background: linear-gradient(135deg, #0ea5e9, #0284c7); }

.ss-card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.ss-card-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ss-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ss-label {
  font-size: 12.5px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.ss-value {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

@media (max-width: 1200px) {
  .ss-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .ss-grid { grid-template-columns: 1fr; }
}
</style>
