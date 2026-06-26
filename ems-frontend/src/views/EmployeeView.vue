<template>
  <div class="employee-page">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="stat-card primary">
          <div class="stat-icon"><el-icon><UserFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ Math.round(totalCountDisplay) }}</div>
            <div class="stat-label">员工总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card success">
          <div class="stat-icon"><el-icon><CircleCheckFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ Math.round(activeCountDisplay) }}</div>
            <div class="stat-label">在职员工</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card warning">
          <div class="stat-icon"><el-icon><OfficeBuilding /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ Math.round(departmentCountDisplay) }}</div>
            <div class="stat-label">部门数量</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card danger">
          <div class="stat-icon"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">¥ {{ formatSalary(totalSalaryDisplay) }}</div>
            <div class="stat-label">薪资总支出</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <PageHeader
            title="员工管理"
            subtitle="管理系统内所有员工信息"
            :icon="UserFilled"
          />
          <div class="header-actions">
            <el-button size="large" :icon="List" @click="logDialogVisible = true">操作日志</el-button>
            <el-button type="success" size="large" :icon="Upload" @click="handleImport">导入</el-button>
            <el-button type="warning" size="large" :icon="Download" @click="handleExport">导出</el-button>
            <el-button type="primary" size="large" :icon="Plus" @click="handleAdd">新增员工</el-button>
          </div>
        </div>
      </template>

      <div class="search-section">
        <div class="search-header">
          <div class="search-title">
            <el-icon><Search /></el-icon>
            <span>搜索筛选</span>
            <span v-if="!searchExpanded" class="search-tags">
              <el-tag v-if="queryForm.name" size="small" type="info">姓名: {{ queryForm.name }}</el-tag>
              <el-tag v-if="queryForm.department" size="small" type="info">部门: {{ queryForm.department }}</el-tag>
              <el-tag v-if="queryForm.status !== undefined" size="small" type="info">状态: {{ queryForm.status === 1 ? '在职' : '离职' }}</el-tag>
            </span>
          </div>
          <el-button link :icon="searchExpanded ? ArrowUp : ArrowDown" @click="searchExpanded = !searchExpanded">
            {{ searchExpanded ? '收起' : '展开' }}
          </el-button>
        </div>
        <el-collapse-transition>
          <el-form v-show="searchExpanded" :model="queryForm" class="search-form">
            <el-form-item label="姓名">
              <el-input
                v-model="queryForm.name"
                placeholder="请输入姓名"
                clearable
                :prefix-icon="Search"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="工号">
              <el-input
                v-model="queryForm.employeeNo"
                placeholder="请输入工号"
                clearable
                :prefix-icon="Postcard"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="部门">
              <el-select
                v-model="queryForm.department"
                placeholder="请选择部门"
                clearable
                filterable
                :prefix-icon="OfficeBuilding"
                @change="handleDepartmentFilterChange"
              >
                <el-option
                  v-for="dept in departmentOptions"
                  :key="dept.code"
                  :label="dept.name"
                  :value="dept.code"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="岗位">
              <el-select
                v-model="queryForm.position"
                placeholder="请选择岗位"
                clearable
                filterable
                :prefix-icon="User"
                @change="handleSearch"
              >
                <el-option
                  v-for="pos in filteredPositionOptions"
                  :key="pos.code"
                  :label="pos.name"
                  :value="pos.code"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryForm.status" placeholder="全部状态" clearable @change="handleSearch">
                <el-option label="在职" :value="1" />
                <el-option label="离职" :value="0" />
              </el-select>
            </el-form-item>
            <div class="search-actions">
              <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
              <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
            </div>
          </el-form>
        </el-collapse-transition>
      </div>

      <div class="toolbar">
        <el-button
          v-if="isAdmin()"
          type="warning"
          :disabled="selectedIds.length === 0"
          :icon="Refresh"
          @click="transferDialogVisible = true"
        >
          批量调岗
        </el-button>
        <el-button
          v-if="isAdmin()"
          type="success"
          :disabled="selectedIds.length === 0"
          :icon="Money"
          @click="salaryDialogVisible = true"
        >
          批量调薪
        </el-button>
        <el-button
          v-if="isAdmin()"
          type="danger"
          :disabled="selectedIds.length === 0"
          :icon="Delete"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
        <span v-if="selectedIds.length > 0" class="selected-tip">已选择 {{ selectedIds.length }} 项</span>
        <div class="toolbar-right">
          <el-dropdown trigger="click" :hide-on-click="false" @visible-change="(visible: boolean) => { if (!visible) saveColumnVisible() }">
            <el-button :icon="Setting">列设置</el-button>
            <template #dropdown>
              <el-dropdown-menu class="column-setting-dropdown">
                <el-dropdown-item v-for="(label, key) in columnLabelMap" :key="key" @click.stop>
                  <el-checkbox v-model="columnVisible[key]" :label="label" />
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <EmployeeTable
        :table-data="tableData"
        :loading="loading"
        :department-options="departmentOptions"
        :position-options-all="positionOptionsAll"
        :rank-options="rankOptions"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
        @sort="handleSort"
        @workflow="handleWorkflow"
      />

      <div v-if="tableData.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无员工数据" :image-size="120">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增员工</el-button>
        </el-empty>
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :prev-text="'上一页'"
          :next-text="'下一页'"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <EmployeeForm
      v-model:visible="dialogVisible"
      :is-edit="isEdit"
      :model-value="currentEmployee || undefined"
      :detail="currentDetail || undefined"
      :department-options="departmentOptions"
      :position-options-all="positionOptionsAll"
      :rank-options="rankOptions"
      @submit="handleFormSubmit"
    />

    <EmployeeDetail
      v-model:visible="detailDrawerVisible"
      :employee-id="detailEmployeeId"
      :department-options="departmentOptions"
      :position-options="positionOptionsAll"
    />

    <OperationLogDialog v-model:visible="logDialogVisible" />

    <BatchTransferDialog
      v-model:visible="transferDialogVisible"
      :ids="selectedIds"
      :department-options="departmentOptions"
      :position-options-all="positionOptionsAll"
      @success="handleBatchOpSuccess"
    />

    <BatchSalaryDialog
      v-model:visible="salaryDialogVisible"
      :ids="selectedIds"
      @success="handleBatchOpSuccess"
    />

    <WorkflowDialog
      v-model:visible="workflowDialogVisible"
      :type="workflowType"
      :employee="workflowEmployee || {} as any"
      @success="handleWorkflowSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCountUp } from '@/composables/useCountUp'
import {
  Plus,
  Search,
  RefreshRight,
  Delete,
  View,
  Edit,
  UserFilled,
  User,
  OfficeBuilding,
  Postcard,
  CircleCheckFilled,
  Money,
  Upload,
  Download,
  ArrowUp,
  ArrowDown,
  List,
  Setting,
  Refresh,
} from '@element-plus/icons-vue'
import { employeeApi, type Employee, type EmployeeFormPayload, type EmployeeDetailVO, type EmployeeStatistics } from '@/api/employee'
import { dictionaryApi, type Dictionary } from '@/api/dictionary'
import EmployeeForm from '@/components/EmployeeForm.vue'
import EmployeeTable from '@/components/EmployeeTable.vue'
import EmployeeDetail from '@/components/EmployeeDetail.vue'
import PageHeader from '@/components/PageHeader.vue'
import { columnLabelMap, columnVisible, columnWidth, saveColumnVisible, saveColumnWidth, highlightRow } from '@/utils/tableConfig'
import OperationLogDialog from '@/components/OperationLogDialog.vue'
import BatchTransferDialog from '@/components/BatchTransferDialog.vue'
import BatchSalaryDialog from '@/components/BatchSalaryDialog.vue'
import WorkflowDialog from '@/components/WorkflowDialog.vue'
import { isAdmin } from '@/utils/auth'
import { formatSalary } from '@/utils/common'

const loading = ref(false)
const tableData = ref<Employee[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

const statistics = reactive<EmployeeStatistics>({
  totalCount: 0,
  activeCount: 0,
  departmentCount: 0,
  totalSalary: 0,
})

const totalCountDisplay = useCountUp(computed(() => statistics.totalCount))
const activeCountDisplay = useCountUp(computed(() => statistics.activeCount))
const departmentCountDisplay = useCountUp(computed(() => statistics.departmentCount))
const totalSalaryDisplay = useCountUp(computed(() => statistics.totalSalary))

const queryForm = reactive({
  name: '',
  employeeNo: '',
  department: '',
  position: '',
  status: undefined as number | undefined,
  page: 1,
  size: 10,
  sortField: '',
  sortOrder: '',
})

const searchExpanded = ref(true)

const departmentOptions = ref<Dictionary[]>([])
const positionOptionsAll = ref<Dictionary[]>([])
const rankOptions = ref<Dictionary[]>([])

const departmentNameMap = computed(() => {
  return Object.fromEntries(departmentOptions.value.map((item) => [item.code, item.name]))
})

const positionNameMap = computed(() => {
  return Object.fromEntries(positionOptionsAll.value.map((item) => [item.code, item.name]))
})

// 岗位下拉：选部门时按部门过滤，未选时展示全部
const filteredPositionOptions = computed(() => {
  if (!queryForm.department) return positionOptionsAll.value
  return positionOptionsAll.value.filter((p: any) => {
    const dept = (p as any).dept
    return !dept || dept === queryForm.department
  })
})

// 切换部门时清空岗位，避免岗位与新部门不匹配
const handleDepartmentFilterChange = () => {
  queryForm.position = ''
  handleSearch()
}

const fetchDictionaries = async () => {
  try {
    const [deptRes, posRes, rankRes] = await Promise.all([
      dictionaryApi.listByType('department'),
      dictionaryApi.listByType('position'),
      dictionaryApi.listByType('rank')
    ])
    departmentOptions.value = deptRes
    positionOptionsAll.value = posRes
    rankOptions.value = rankRes
  } catch (error: any) {
    ElMessage.error(error.message || '加载字典失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await employeeApi.page({
      name: queryForm.name || undefined,
      employeeNo: queryForm.employeeNo || undefined,
      department: queryForm.department || undefined,
      position: queryForm.position || undefined,
      status: queryForm.status,
      page: queryForm.page,
      size: queryForm.size,
      sortField: queryForm.sortField || undefined,
      sortOrder: queryForm.sortOrder || undefined,
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const res = await employeeApi.statistics()
    Object.assign(statistics, res)
  } catch (error: any) {
    ElMessage.error(error.message || '统计查询失败')
  }
}

const handleSearch = () => {
  queryForm.page = 1
  fetchData()
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.employeeNo = ''
  queryForm.department = ''
  queryForm.position = ''
  queryForm.status = undefined
  queryForm.page = 1
  fetchData()
}

const handleSizeChange = (val: number) => {
  queryForm.size = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  queryForm.page = val
  fetchData()
}

const handleSelectionChange = (ids: number[]) => {
  selectedIds.value = ids
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 名员工吗？`, '提示', {
    type: 'warning',
  })
    .then(async () => {
      await employeeApi.deleteBatch(selectedIds.value)
      ElMessage.success('批量删除成功')
      fetchData()
      fetchStatistics()
    })
    .catch(() => {})
}

const handleExport = async () => {
  try {
    const res = await employeeApi.export()
    const blob = new Blob([res as unknown as BlobPart], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `员工信息_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error: any) {
    ElMessage.error(error.message || '导出失败')
  }
}

const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e: Event) => {
    const file = (e.target as HTMLInputElement).files?.[0]
    if (!file) return
    try {
      await employeeApi.import(file)
      ElMessage.success('导入成功')
      fetchData()
      fetchStatistics()
    } catch (error: any) {
      ElMessage.error(error.message || '导入失败')
    }
  }
  input.click()
}

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEmployee = ref<Employee | null>(null)
const currentDetail = ref<EmployeeDetailVO | null>(null)
const detailEmployeeId = ref<number | null>(null)
const detailDrawerVisible = ref(false)
const logDialogVisible = ref(false)
const transferDialogVisible = ref(false)
const salaryDialogVisible = ref(false)
const workflowDialogVisible = ref(false)
const workflowType = ref<'TRANSFER' | 'ADJUST_SALARY' | 'CONFIRM' | 'LEAVE'>('TRANSFER')
const workflowEmployee = ref<Employee | null>(null)

const handleBatchOpSuccess = () => {
  selectedIds.value = []
  fetchData()
  fetchStatistics()
}

const handleWorkflow = (payload: { type: string; employee: Employee }) => {
  workflowType.value = payload.type as any
  workflowEmployee.value = payload.employee
  workflowDialogVisible.value = true
}

const handleWorkflowSuccess = () => {
  fetchData()
  fetchStatistics()
}

const handleAdd = () => {
  isEdit.value = false
  currentEmployee.value = null
  currentDetail.value = null
  dialogVisible.value = true
}

const handleEdit = async (row: Employee) => {
  isEdit.value = true
  currentEmployee.value = row
  try {
    currentDetail.value = await employeeApi.getDetail(row.id!)
  } catch {
    currentDetail.value = null
  }
  dialogVisible.value = true
}

const handleView = (row: Employee) => {
  detailEmployeeId.value = row.id ?? null
  detailDrawerVisible.value = true
}

const handleFormSubmit = async (data: EmployeeFormPayload) => {
  try {
    const emp = data.employee
    if (isEdit.value && emp.id) {
      await employeeApi.update(emp.id, data)
      ElMessage.success('更新成功')
      dialogVisible.value = false
      await fetchData()
      highlightRow(emp.id)
    } else {
      await employeeApi.save(data)
      ElMessage.success('新增成功')
      dialogVisible.value = false
      await fetchData()
      highlightRow(tableData.value[0]?.id)
    }
    fetchStatistics()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleDelete = (row: Employee) => {
  ElMessageBox.confirm(`确定删除员工 "${row.name}" 吗？`, '提示', {
    type: 'warning',
  })
    .then(async () => {
      if (!row.id) return
      await employeeApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchData()
      fetchStatistics()
    })
    .catch(() => {})
}

const handleSortChange = ({ prop, order }: { prop?: string; order?: string }) => {
  queryForm.sortField = prop || ''
  queryForm.sortOrder = order || ''
  queryForm.page = 1
  fetchData()
}

const handleSort = async (ids: number[]) => {
  try {
    if (queryForm.sortField) {
      queryForm.sortField = ''
      queryForm.sortOrder = ''
    }
    await employeeApi.sort(ids)
    ElMessage.success('排序已保存')
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '排序保存失败')
    fetchData()
  }
}

const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape' && dialogVisible.value) {
    dialogVisible.value = false
  }
  if (e.key === 'Enter' && e.ctrlKey && dialogVisible.value) {
    handleFormSubmit({ ...currentEmployee.value!, ...(e.target as any).formValues })
  }
}

onMounted(() => {
  fetchDictionaries()
  fetchData()
  fetchStatistics()
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.employee-page {
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  height: calc(100vh - 60px);
  min-height: auto;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  box-sizing: border-box;
}

.stat-row {
  margin-bottom: 8px;
  flex-shrink: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.stat-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-color: var(--accent-color);
}

.stat-card.primary { --accent-color: #3b82f6; }
.stat-card.success { --accent-color: #10b981; }
.stat-card.warning { --accent-color: #f59e0b; }
.stat-card.danger { --accent-color: #ef4444; }

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  background: var(--accent-color);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.1;
  font-family: 'Inter', -apple-system, sans-serif;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

.main-card {
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.main-card :deep(.el-card__header) {
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}

.main-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 14px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .header-actions {
  display: flex;
  gap: 8px;
}

.search-section {
  background: #f8fafc;
  border-radius: 10px;
  padding: 12px 16px;
  margin-bottom: 12px;
  border: 1px solid #e2e8f0;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.search-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

.search-title .el-icon {
  color: #3b82f6;
  font-size: 14px;
}

.search-tags {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: 10px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.search-form :deep(.el-form-item__label) {
  padding-bottom: 0;
  line-height: 32px;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  width: auto !important;
}

.search-form :deep(.el-form-item__content) {
  width: 150px;
}

.search-form .search-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.toolbar-right {
  margin-left: auto;
}

.column-setting-dropdown {
  padding: 8px 0;
  min-width: 160px;
  border-radius: 12px;
}

.column-setting-dropdown :deep(.el-dropdown-menu__item) {
  padding: 8px 16px;
}

.selected-tip {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  background: #eff6ff;
  padding: 4px 12px;
  border-radius: 20px;
  color: #3b82f6;
}

.empty-state {
  padding: 48px 0;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
}

html.dark .stat-card {
  background: #1e1e20;
  border-color: #2a2a2c;
}

html.dark .stat-value {
  color: #e2e8f0;
}

html.dark .stat-label {
  color: #94a3b8;
}

html.dark .main-card {
  background: #1e1e20;
  border-color: #2a2a2c;
}

html.dark .main-card :deep(.el-card__header) {
  background: #1e1e20;
  border-bottom-color: #2a2a2c;
}

html.dark .search-section {
  background: #141415;
  border-color: #2a2a2c;
}

html.dark .search-title {
  color: #e2e8f0;
}

html.dark .selected-tip {
  background: rgba(59, 130, 246, 0.1);
}

</style>
