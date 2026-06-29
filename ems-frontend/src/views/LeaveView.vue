<template>
  <div class="leave-page ems-page">
    <!-- 顶部统计 -->
    <div v-if="canApproveLeave" class="top-section">
      <div class="stat-card purple" @click="switchTab('pending')">
        <div class="stat-icon"><el-icon><Bell /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value ems-mono">{{ stats.pending }}</div>
          <div class="stat-label">待审批</div>
        </div>
      </div>
      <div class="stat-card emerald" @click="switchTab('my')">
        <div class="stat-icon"><el-icon><Document /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value ems-mono">{{ stats.approved + stats.rejected + stats.cancelled }}</div>
          <div class="stat-label">已处理</div>
        </div>
      </div>
      <div class="stat-card sky">
        <div class="stat-icon"><el-icon><Calendar /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value ems-mono">{{ stats.approved }}</div>
          <div class="stat-label">已批准</div>
        </div>
      </div>
      <div class="stat-card amber">
        <div class="stat-icon"><el-icon><CloseBold /></el-icon></div>
        <div class="stat-body">
          <div class="stat-value ems-mono">{{ stats.rejected }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
    </div>

    <!-- 标签页 + 表格 -->
    <div class="table-section">
      <div class="section-header">
        <div class="section-title">
          <span class="title-dot"></span>
          {{ activeTab === 'my' ? '我的申请' : '待审批' }}
        </div>
        <div class="section-actions">
          <el-radio-group v-model="activeTab" size="default" @change="loadData">
            <el-radio-button value="my">我的申请</el-radio-button>
            <el-radio-button v-if="canApproveLeave" value="pending">待审批</el-radio-button>
            <el-radio-button v-if="canApproveLeave" value="all">全部</el-radio-button>
          </el-radio-group>
          <el-select v-model="searchStatus" placeholder="全部状态" clearable size="default" class="filter-select" @change="loadData">
            <el-option :value="0" label="待审批" />
            <el-option :value="1" label="已批准" />
            <el-option :value="2" label="已拒绝" />
            <el-option :value="3" label="已撤销" />
          </el-select>
          <el-date-picker v-model="searchMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" size="default" class="filter-select" @change="loadData" />
          <el-button v-permission="'leave:submit'" type="primary" :icon="Plus" @click="openSubmitDialog">申请请假</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" class="leave-table" stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="employeeId" label="申请人" min-width="120">
          <template #default="{ row }">
            <div class="employee-cell">
              <el-avatar :size="28" class="emp-avatar">{{ getEmployeeName(row.employeeId)?.charAt(0) }}</el-avatar>
              <span class="emp-name">{{ getEmployeeName(row.employeeId) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="leaveType" label="请假类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag effect="light" size="small" round>{{ row.leaveType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110" align="center" />
        <el-table-column prop="endDate" label="结束日期" width="110" align="center" />
        <el-table-column prop="days" label="天数" width="80" align="center">
          <template #default="{ row }">{{ row.days }} 天</template>
        </el-table-column>
        <el-table-column prop="reason" label="事由" min-width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light" size="small" round>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveRemark" label="审批备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 0 && activeTab === 'pending' && hasPermission('leave:approve')" size="small" type="success" link @click="handleApprove(row)">批准</el-button>
            <el-button v-if="row.status === 0 && activeTab === 'pending' && hasPermission('leave:reject')" size="small" type="danger" link @click="handleReject(row)">拒绝</el-button>
            <el-button v-if="row.status === 0 && row.employeeId === currentEmployeeId && hasPermission('leave:cancel')" size="small" type="warning" link @click="handleCancel(row)">撤销</el-button>
            <span v-if="row.status !== 0" class="no-action">—</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无请假记录" :image-size="100" />
      </div>

      <div class="pagination-wrapper">
        <div class="pagination-left">
          <span class="pagination-total">共 <em class="ems-mono">{{ pagination.total }}</em> 条记录</span>
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
    </div>

    <!-- 申请弹窗 -->
    <el-dialog v-model="submitDialogVisible" title="申请请假" width="540px" :close-on-click-modal="false">
      <el-form :model="submitForm" label-width="90px" class="edit-form">
        <el-form-item label="请假类型" required>
          <el-select v-model="submitForm.leaveType" placeholder="请选择请假类型" style="width:100%">
            <el-option value="年假" label="年假" />
            <el-option value="病假" label="病假" />
            <el-option value="事假" label="事假" />
            <el-option value="婚假" label="婚假" />
            <el-option value="产假" label="产假" />
            <el-option value="陪产假" label="陪产假" />
            <el-option value="丧假" label="丧假" />
            <el-option value="其他" label="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" required>
          <el-date-picker v-model="submitForm.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" style="width:100%" @change="calcDays" />
        </el-form-item>
        <el-form-item label="结束日期" required>
          <el-date-picker v-model="submitForm.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" style="width:100%" @change="calcDays" />
        </el-form-item>
        <el-form-item label="请假天数">
          <span class="days-display">{{ submitForm.days || 0 }} 天</span>
        </el-form-item>
        <el-form-item label="请假事由">
          <el-input v-model="submitForm.reason" type="textarea" rows="3" placeholder="请输入请假事由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLeave">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 审批弹窗 -->
    <el-dialog v-model="approveDialogVisible" :title="approveAction === 'approve' ? '批准申请' : '拒绝申请'" width="480px">
      <div class="approve-info" v-if="currentRow">
        <div class="approve-row"><span class="approve-label">申请人：</span>{{ getEmployeeName(currentRow.employeeId) }}</div>
        <div class="approve-row"><span class="approve-label">请假类型：</span>{{ currentRow.leaveType }}</div>
        <div class="approve-row"><span class="approve-label">请假时间：</span>{{ currentRow.startDate }} ~ {{ currentRow.endDate }}（{{ currentRow.days }}天）</div>
        <div class="approve-row"><span class="approve-label">请假事由：</span>{{ currentRow.reason || '无' }}</div>
      </div>
      <el-form label-width="80px" class="edit-form" style="margin-top:16px">
        <el-form-item label="审批备注">
          <el-input v-model="approveRemark" type="textarea" rows="2" :placeholder="approveAction === 'approve' ? '可选，如：同意' : '请填写拒绝原因'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button v-if="approveAction === 'approve'" type="success" @click="doApprove">确认批准</el-button>
        <el-button v-else type="danger" @click="doReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Bell, Document, Calendar, CloseBold } from '@element-plus/icons-vue'
import { leaveApi, type LeaveRequest } from '@/api/leave'
import { employeeApi, type Employee } from '@/api/employee'
import { hasPermission } from '@/utils/permission'
import { getCurrentUser } from '@/utils/auth'

const loading = ref(false)
const tableData = ref<LeaveRequest[]>([])
const employees = ref<Employee[]>([])
const activeTab = ref<'my' | 'pending' | 'all'>('my')
const searchStatus = ref<number | undefined>(undefined)
const searchMonth = ref<string>('')
const submitDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const approveAction = ref<'approve' | 'reject'>('approve')
const approveRemark = ref('')
const currentRow = ref<LeaveRequest | null>(null)
const canApproveLeave = computed(() => hasPermission(['leave:approve', 'leave:reject']))
const currentUser = getCurrentUser()

const stats = reactive({ pending: 0, approved: 0, rejected: 0, cancelled: 0 })

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const currentEmployeeId = computed(() => {
  return currentUser?.employeeId || 0
})

const submitForm = reactive<LeaveRequest>({
  employeeId: 0,
  leaveType: '',
  startDate: '',
  endDate: '',
  days: 0,
  reason: '',
})

const loadData = async () => {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'my') {
      res = await leaveApi.my({ employeeId: currentEmployeeId.value, status: searchStatus.value, page: pagination.page, size: pagination.size })
    } else if (activeTab.value === 'pending') {
      res = await leaveApi.page({ status: 0, month: searchMonth.value, page: pagination.page, size: pagination.size })
    } else {
      res = await leaveApi.page({ employeeId: undefined, status: searchStatus.value, month: searchMonth.value, page: pagination.page, size: pagination.size })
    }
    tableData.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  if (!canApproveLeave.value) return
  try {
    const res = await leaveApi.statistics()
    if (Array.isArray(res)) {
      res.forEach((item: any) => {
        switch (item.status) {
          case 0: stats.pending = item.count; break
          case 1: stats.approved = item.count; break
          case 2: stats.rejected = item.count; break
          case 3: stats.cancelled = item.count; break
        }
      })
    }
  } catch {}
}

const loadEmployees = async () => {
  try {
    if (canApproveLeave.value || hasPermission('employee:view')) {
      const res = await employeeApi.list()
      employees.value = res
      return
    }
    const emp = await employeeApi.me()
    employees.value = emp ? [emp] : []
  } catch {}
}

const switchTab = (tab: 'my' | 'pending' | 'all') => {
  activeTab.value = tab
  pagination.page = 1
  loadData()
}

const openSubmitDialog = () => {
  if (!currentEmployeeId.value) {
    ElMessage.warning('当前账号未绑定员工档案')
    return
  }
  Object.assign(submitForm, {
    employeeId: currentEmployeeId.value,
    leaveType: '',
    startDate: '',
    endDate: '',
    days: 0,
    reason: '',
  })
  submitDialogVisible.value = true
}

const calcDays = () => {
  if (submitForm.startDate && submitForm.endDate) {
    const start = new Date(submitForm.startDate)
    const end = new Date(submitForm.endDate)
    const diff = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1
    submitForm.days = Math.max(diff, 0)
  }
}

const submitLeave = async () => {
  if (!submitForm.leaveType) { ElMessage.warning('请选择请假类型'); return }
  if (!submitForm.startDate || !submitForm.endDate) { ElMessage.warning('请选择请假日期'); return }
  try {
    await leaveApi.submit(submitForm)
    ElMessage.success('申请提交成功')
    submitDialogVisible.value = false
    loadData()
    loadStats()
  } catch (e) { console.error(e) }
}

const handleApprove = (row: LeaveRequest) => {
  currentRow.value = row
  approveAction.value = 'approve'
  approveRemark.value = ''
  approveDialogVisible.value = true
}

const handleReject = (row: LeaveRequest) => {
  currentRow.value = row
  approveAction.value = 'reject'
  approveRemark.value = ''
  approveDialogVisible.value = true
}

const doApprove = async () => {
  if (!currentRow.value) return
  try {
    await leaveApi.approve(currentRow.value.id!, currentEmployeeId.value, approveRemark.value)
    ElMessage.success('已批准')
    approveDialogVisible.value = false
    loadData()
    loadStats()
  } catch (e) { console.error(e) }
}

const doReject = async () => {
  if (!currentRow.value) return
  if (!approveRemark.value) { ElMessage.warning('请填写拒绝原因'); return }
  try {
    await leaveApi.reject(currentRow.value.id!, currentEmployeeId.value, approveRemark.value)
    ElMessage.success('已拒绝')
    approveDialogVisible.value = false
    loadData()
    loadStats()
  } catch (e) { console.error(e) }
}

const handleCancel = async (row: LeaveRequest) => {
  await ElMessageBox.confirm('确定撤销该请假申请吗？', '提示', { type: 'warning' })
  try {
    await leaveApi.cancel(row.id!, currentEmployeeId.value)
    ElMessage.success('已撤销')
    loadData()
    loadStats()
  } catch (e) { console.error(e) }
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '待审批', 1: '已批准', 2: '已拒绝', 3: '已撤销' }
  return map[status] || '未知'
}

const getEmployeeName = (id: number) => {
  const emp = employees.value.find(e => e.id === id)
  return emp?.name || `员工#${id}`
}

onMounted(() => {
  loadEmployees()
  loadData()
  loadStats()
})
</script>

<style scoped>
.leave-page {
  height: calc(100vh - 56px);
  overflow-y: auto;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 18px 20px 24px;
}
.top-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  flex-shrink: 0;
}
.stat-card {
  border-radius: 14px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.25s var(--ease-out);
  border: 1px solid var(--border-subtle);
  background: var(--bg-elevated);
  box-shadow: var(--shadow-sm);
}
.stat-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.stat-card.purple { border-left: 4px solid #8b5cf6; }
.stat-card.emerald { border-left: 4px solid #10b981; }
.stat-card.sky { border-left: 4px solid #0ea5e9; }
.stat-card.amber { border-left: 4px solid #f59e0b; }
.stat-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
}
.purple .stat-icon { background: rgba(139,92,246,0.12); color: #8b5cf6; }
.emerald .stat-icon { background: rgba(16,185,129,0.12); color: #10b981; }
.sky .stat-icon { background: rgba(14,165,233,0.12); color: #0ea5e9; }
.amber .stat-icon { background: rgba(245,158,11,0.12); color: #f59e0b; }
.stat-body { display: flex; flex-direction: column; gap: 2px; }
.stat-value { font-size: 26px; font-weight: 800; line-height: 1; color: var(--text-primary); font-feature-settings: 'tnum'; }
.stat-label { font-size: 12.5px; color: var(--text-secondary); font-weight: 500; }
.table-section {
  flex: 1;
  background: var(--bg-elevated);
  border-radius: 14px;
  border: 1px solid var(--border-subtle);
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
  box-shadow: var(--shadow-sm);
}
.section-header {
  display: flex; justify-content: space-between; align-items: center;
  gap: 16px; flex-shrink: 0; flex-wrap: wrap;
}
.section-title {
  display: flex; align-items: center; gap: 10px;
  font-size: 15px; font-weight: 700; color: var(--text-primary);
}
.title-dot {
  width: 4px; height: 18px; border-radius: 2px;
  background: linear-gradient(180deg, var(--brand-500), var(--violet-500));
}
.section-actions { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.filter-select { width: 130px; }
.leave-table { flex: 1; min-height: 0; border-radius: 10px; overflow: hidden; }
.employee-cell { display: flex; align-items: center; gap: 10px; }
.emp-avatar {
  background: linear-gradient(135deg, var(--brand-500), var(--violet-500)) !important;
  color: #fff; font-weight: 600; font-size: 12px;
  box-shadow: 0 2px 6px -2px rgba(99,102,241,0.4);
}
.emp-name { font-weight: 600; color: var(--text-primary); font-size: 13px; }
.days-display { font-size: 15px; font-weight: 700; color: var(--brand-500); }
.approve-info { display: flex; flex-direction: column; gap: 8px; padding: 12px 16px; background: var(--bg-soft); border-radius: 10px; }
.approve-row { font-size: 13.5px; color: var(--text-secondary); }
.approve-label { font-weight: 600; color: var(--text-primary); }
.no-action { color: var(--text-tertiary); font-size: 13px; }
.empty-state { padding: 24px 0; }
.pagination-wrapper {
  display: flex; align-items: center; justify-content: space-between;
  flex-shrink: 0; gap: 16px; padding-top: 10px;
  border-top: 1px dashed var(--border-default);
}
.pagination-left {
  display: flex; align-items: center; gap: 14px;
  font-size: 12.5px; color: var(--text-secondary); font-weight: 500; flex-shrink: 0;
}
.pagination-left em { font-style: normal; color: var(--text-primary); font-weight: 700; font-size: 14px; margin: 0 2px; }
.edit-form { padding: 4px 0; }
@media (max-width: 1200px) {
  .top-section { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .top-section { grid-template-columns: 1fr; }
  .section-header { flex-direction: column; align-items: flex-start; }
  .section-actions { width: 100%; }
}
</style>
