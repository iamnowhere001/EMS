<template>
  <div class="employee-page ems-page">
    <el-row :gutter="14" class="stat-row">
      <el-col :span="6">
        <div class="stat-card primary stat-anim" style="animation-delay: 0ms">
          <div class="stat-glow"></div>
          <div class="stat-icon"><el-icon><UserFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value ems-mono">{{ Math.round(totalCountDisplay) }}</div>
            <div class="stat-label">员工总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card success stat-anim" style="animation-delay: 80ms">
          <div class="stat-glow"></div>
          <div class="stat-icon"><el-icon><CircleCheckFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value ems-mono">{{ Math.round(activeCountDisplay) }}</div>
            <div class="stat-label">在职员工</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card warning stat-anim" style="animation-delay: 160ms">
          <div class="stat-glow"></div>
          <div class="stat-icon"><el-icon><OfficeBuilding /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value ems-mono">{{ Math.round(departmentCountDisplay) }}</div>
            <div class="stat-label">部门数量</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card danger stat-anim" style="animation-delay: 240ms">
          <div class="stat-glow"></div>
          <div class="stat-icon"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value ems-mono">¥ {{ formatSalary(totalSalaryDisplay) }}</div>
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
            <el-icon class="search-title-icon"><Search /></el-icon>
            <span>搜索筛选</span>
            <span v-if="!searchExpanded && activeFilterCount > 0" class="filter-count-badge">
              {{ activeFilterCount }}
            </span>
          </div>
          <div class="search-header-right">
            <div v-if="!searchExpanded && activeFilterCount > 0" class="active-filter-tags">
              <el-tag v-if="queryForm.name" size="small" type="primary" effect="light" closable @close="handleClearFilter('name')">
                姓名: {{ queryForm.name }}
              </el-tag>
              <el-tag v-if="queryForm.department" size="small" type="success" effect="light" closable @close="handleClearFilter('department')">
                {{ departmentNameMap[queryForm.department] || queryForm.department }}
              </el-tag>
              <el-tag v-if="queryForm.status !== undefined" size="small" type="warning" effect="light" closable @close="handleClearFilter('status')">
                {{ queryForm.status === 1 ? '在职' : '离职' }}
              </el-tag>
              <el-button v-if="activeFilterCount > 2" type="primary" link size="small" @click="searchExpanded = true">
                全部 {{ activeFilterCount }} 个条件
              </el-button>
            </div>
            <el-button link :icon="searchExpanded ? ArrowUp : ArrowDown" @click="searchExpanded = !searchExpanded">
              {{ searchExpanded ? '收起' : '高级筛选' }}
            </el-button>
          </div>
        </div>
        <el-collapse-transition>
          <el-form v-show="searchExpanded" :model="queryForm" class="search-form">
            <div class="search-form-grid">
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
                  placeholder="全部部门"
                  clearable
                  filterable
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
                  placeholder="全部岗位"
                  clearable
                  filterable
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
            </div>
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
        <div class="pagination-left">
          <span class="pagination-total">
            共 <em class="ems-mono">{{ total }}</em> 条记录
          </span>
          <span v-if="selectedIds.length > 0" class="pagination-selected">
            已选 <em class="ems-mono">{{ selectedIds.length }}</em> 项
          </span>
        </div>
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          background
          layout="sizes, prev, pager, next, jumper"
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

const activeFilterCount = computed(() => {
  let count = 0
  if (queryForm.name) count++
  if (queryForm.employeeNo) count++
  if (queryForm.department) count++
  if (queryForm.position) count++
  if (queryForm.status !== undefined) count++
  return count
})

const handleClearFilter = (field: string) => {
  if (field === 'name') queryForm.name = ''
  if (field === 'employeeNo') queryForm.employeeNo = ''
  if (field === 'department') queryForm.department = ''
  if (field === 'position') queryForm.position = ''
  if (field === 'status') queryForm.status = undefined
  handleSearch()
}

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
  height: calc(100vh - 56px);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  box-sizing: border-box;
}

.stat-row {
  margin-bottom: 0;
  flex-shrink: 0;
}

.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 18px;
  border-radius: var(--radius-lg);
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s var(--ease-out);
  overflow: hidden;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.stat-glow {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0.55;
  transition: opacity 0.3s;
}

.stat-card:hover .stat-glow { opacity: 1; }

.stat-card.primary { --accent-color: #6366f1; --accent-soft: rgba(99, 102, 241, 0.1); }
.stat-card.success { --accent-color: #10b981; --accent-soft: rgba(16, 185, 129, 0.1); }
.stat-card.warning { --accent-color: #f59e0b; --accent-soft: rgba(245, 158, 11, 0.1); }
.stat-card.danger  { --accent-color: #f43f5e; --accent-soft: rgba(244, 63, 94, 0.1); }

.stat-card.primary .stat-glow { background: radial-gradient(circle at 100% 0%, var(--accent-soft), transparent 60%); }
.stat-card.success .stat-glow { background: radial-gradient(circle at 100% 0%, var(--accent-soft), transparent 60%); }
.stat-card.warning .stat-glow { background: radial-gradient(circle at 100% 0%, var(--accent-soft), transparent 60%); }
.stat-card.danger  .stat-glow { background: radial-gradient(circle at 100% 0%, var(--accent-soft), transparent 60%); }

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 19px;
  background: var(--accent-color);
  box-shadow: 0 6px 14px -4px var(--accent-color);
  flex-shrink: 0;
  transition: transform 0.3s var(--ease-spring);
  position: relative;
  z-index: 1;
}

.stat-card:hover .stat-icon { transform: scale(1.06) rotate(-4deg); }

.stat-info { flex: 1; min-width: 0; position: relative; z-index: 1; }

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 4px;
  font-weight: 500;
}

.stat-anim {
  animation: statIn 0.5s var(--ease-out) both;
}

@keyframes statIn {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}

.main-card {
  border-radius: var(--radius-lg) !important;
  border: 1px solid var(--border-subtle) !important;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.main-card :deep(.el-card__header) {
  padding: 14px 20px;
}

.main-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 16px 20px;
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
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 12px;
  border: 1px solid var(--border-subtle);
  transition: all var(--dur-base) var(--ease-out);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.search-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  flex-shrink: 0;
}

.search-title-icon {
  color: var(--brand-500);
  font-size: 15px;
}

.filter-count-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  font-size: 11px;
  font-weight: 600;
  background: var(--brand-500);
  color: #fff;
  border-radius: var(--radius-full);
  font-family: var(--font-mono);
}

.search-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1;
  justify-content: flex-end;
}

.active-filter-tags {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-end;
  max-width: 560px;
  overflow: hidden;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
  padding-top: 12px;
  margin-top: 10px;
  border-top: 1px dashed var(--border-default);
}

.search-form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px 16px;
  flex: 1;
  min-width: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  margin-left: 0;
}

.search-form :deep(.el-form-item__label) {
  padding-bottom: 0;
  line-height: 32px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  width: auto !important;
  flex-shrink: 0;
}

.search-form :deep(.el-form-item__content) {
  width: 100% !important;
  min-width: 0;
}

.search-form .search-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
  padding-top: 0;
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
  border-radius: var(--radius-md);
}

.column-setting-dropdown :deep(.el-dropdown-menu__item) {
  padding: 8px 16px;
}

.selected-tip {
  font-size: 12.5px;
  background: var(--brand-50);
  color: var(--brand-700);
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-weight: 500;
  border: 1px solid var(--brand-100);
}

.empty-state {
  padding: 48px 0;
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

.pagination-selected {
  background: var(--brand-50);
  color: var(--brand-700);
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-size: 12px;
  border: 1px solid var(--brand-100);
}

.pagination-selected em {
  color: var(--brand-700);
}
</style>
