<template>
  <div class="pc-page ems-page">
    <div class="pc-page-bg"></div>

    <!-- Overview -->
    <div class="pc-overview">
      <div class="pc-hero" v-loading="heroLoading">
        <div class="pc-hero-bg"></div>
        <div class="pc-hero-content">
          <div class="pc-hero-left">
            <div class="pc-avatar-wrap">
              <el-avatar :size="76" class="pc-avatar">
                {{ empInitial }}
              </el-avatar>
              <span class="pc-avatar-status"></span>
            </div>
            <div class="pc-hero-info">
              <div class="pc-kicker">{{ todayText }} · 员工自助中心</div>
              <div class="pc-greeting">{{ greeting }}</div>
              <div class="pc-emp-name">{{ employee?.name || '—' }}</div>
              <div class="pc-emp-meta">
                <span class="pc-meta-pill">{{ employee?.department || '未分配部门' }}</span>
                <span class="pc-meta-pill">{{ employee?.position || '未设定岗位' }}</span>
                <span class="pc-meta-pill">工号 {{ employee?.employeeNo || '—' }}</span>
                <el-tag
                  :type="employee?.status === 1 ? 'success' : 'warning'"
                  size="small"
                  round
                  effect="light"
                >
                  {{ employee?.status === 1 ? '在职' : '离职' }}
                </el-tag>
              </div>
            </div>
          </div>
          <div class="pc-hero-right">
            <div class="pc-hero-stat">
              <div class="pc-hero-stat-value">{{ hireDays }}</div>
              <div class="pc-hero-stat-label">入职天数</div>
            </div>
            <div class="pc-hero-stat">
              <div class="pc-hero-stat-value">{{ profileCompleteness }}%</div>
              <div class="pc-hero-stat-label">资料完整度</div>
            </div>
            <div class="pc-hero-stat">
              <div class="pc-hero-stat-value">{{ employee?.rank || '—' }}</div>
              <div class="pc-hero-stat-label">职级</div>
            </div>
          </div>
        </div>

        <div class="pc-action-strip">
          <button class="pc-action-btn primary" type="button" @click="goCheckIn">
            <el-icon><CircleCheck /></el-icon>
            <span>签到打卡</span>
          </button>
          <button class="pc-action-btn" type="button" @click="goApplyLeave">
            <el-icon><EditPen /></el-icon>
            <span>申请请假</span>
          </button>
          <button class="pc-action-btn" type="button" @click="openEditDialog">
            <el-icon><User /></el-icon>
            <span>更新资料</span>
          </button>
          <button v-if="canViewSalary" class="pc-action-btn" type="button" @click="goSalary">
            <el-icon><CreditCard /></el-icon>
            <span>查看工资</span>
          </button>
        </div>
      </div>

      <div class="pc-stats">
      <div class="pc-stat-card stat-attendance" @click="activeActivityTab = 'attendance'">
        <div class="pc-stat-top">
          <div class="pc-stat-icon-wrap">
            <el-icon class="pc-stat-icon"><Clock /></el-icon>
          </div>
          <div class="pc-stat-label">本月出勤率</div>
        </div>
        <div class="pc-stat-value">{{ attendanceRate }}<span class="pc-stat-unit">%</span></div>
        <div class="pc-stat-bar">
          <div class="pc-stat-bar-fill" :style="{ width: attendanceRate + '%' }"></div>
        </div>
      </div>
      <div class="pc-stat-card stat-leave" @click="activeActivityTab = 'leave'">
        <div class="pc-stat-top">
          <div class="pc-stat-icon-wrap">
            <el-icon class="pc-stat-icon"><Document /></el-icon>
          </div>
          <div class="pc-stat-label">今年已请假</div>
        </div>
        <div class="pc-stat-value">{{ totalLeaveDays }}<span class="pc-stat-unit">天</span></div>
        <div class="pc-stat-sub">剩余年假 {{ annualLeaveRemaining }} 天</div>
      </div>
      <div v-if="canViewSalary" class="pc-stat-card stat-salary">
        <div class="pc-stat-top">
          <div class="pc-stat-icon-wrap">
            <el-icon class="pc-stat-icon"><Money /></el-icon>
          </div>
          <div class="pc-stat-label">最近工资</div>
        </div>
        <div class="pc-stat-value salary-value">{{ latestSalary ? '¥' + formatMoney(latestSalary.actualSalary) : '—' }}</div>
        <div class="pc-stat-sub" v-if="salaryCount > 0">{{ latestSalary?.yearMonth }} · {{ salaryCount }} 条记录</div>
      </div>
      <div class="pc-stat-card stat-contract">
        <div class="pc-stat-top">
          <div class="pc-stat-icon-wrap">
            <el-icon class="pc-stat-icon"><Tickets /></el-icon>
          </div>
          <div class="pc-stat-label">合同状态</div>
        </div>
        <div class="pc-stat-value">{{ contractStatus }}</div>
        <div class="pc-stat-sub" v-if="contractEndDays !== null">
          <span :class="contractEndDays <= 30 ? 'text-warning' : ''">
            {{ contractEndDays <= 0 ? '已到期' : `剩余 ${contractEndDays} 天` }}
          </span>
        </div>
      </div>
    </div>
    </div>

    <!-- Main Content Grid -->
    <div class="pc-content-grid">
      <!-- Left: Personal Info -->
      <div class="pc-panel">
        <div class="pc-panel-header">
          <div>
            <span class="pc-panel-title">个人信息</span>
            <p class="pc-panel-subtitle">{{ profileMissingText }}</p>
          </div>
          <el-button type="primary" link :icon="Edit" size="small" @click="openEditDialog">
            编辑资料
          </el-button>
        </div>
        <div class="pc-info-grid">
          <div class="pc-info-item">
            <span class="pc-info-label">手机号码</span>
            <span class="pc-info-value">{{ employee?.phone || '未填写' }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">电子邮箱</span>
            <span class="pc-info-value">{{ employee?.email || '未填写' }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">身份证号</span>
            <span class="pc-info-value masked">{{ maskIdCard(employee?.idCard) }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">银行卡号</span>
            <span class="pc-info-value masked">{{ maskBankCard(employee?.bankCard) }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">入职日期</span>
            <span class="pc-info-value">{{ employee?.hireDate || '—' }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">紧急联系人</span>
            <span class="pc-info-value">{{ employee?.emergencyContact || '未填写' }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">紧急联系电话</span>
            <span class="pc-info-value">{{ employee?.emergencyPhone || '未填写' }}</span>
          </div>
          <div class="pc-info-item">
            <span class="pc-info-label">现居地址</span>
            <span class="pc-info-value pc-info-value-wide">{{ employee?.currentAddress || '未填写' }}</span>
          </div>
        </div>
      </div>

      <!-- Right: Activity -->
      <div class="pc-panel">
        <div class="pc-panel-header">
          <div class="pc-tabs">
            <span
              class="pc-tab"
              :class="{ active: activeActivityTab === 'attendance' }"
              @click="activeActivityTab = 'attendance'"
            >出勤记录</span>
            <span
              class="pc-tab"
              :class="{ active: activeActivityTab === 'leave' }"
              @click="activeActivityTab = 'leave'"
            >请假记录</span>
            <span
              v-if="canViewSalary"
              class="pc-tab"
              :class="{ active: activeActivityTab === 'salary' }"
              @click="activeActivityTab = 'salary'"
            >工资条</span>
          </div>
          <span class="pc-panel-action" @click="viewAll(activeActivityTab)">查看全部 →</span>
        </div>

        <!-- Attendance Timeline -->
        <div class="pc-activity-list" v-if="activeActivityTab === 'attendance'" v-loading="attLoading">
          <template v-if="recentAttendances.length">
            <div class="pc-activity-item" v-for="item in recentAttendances" :key="item.id">
              <div class="pc-activity-icon" :class="item.status === 0 ? 'normal' : 'late'">
                <el-icon><Check v-if="item.status === 0" /><WarningFilled v-else /></el-icon>
              </div>
              <div class="pc-activity-body">
                <div class="pc-activity-title">{{ item.attendanceDate }}</div>
                <div class="pc-activity-desc">
                  签到 {{ item.checkInTime || '—' }} · 签退 {{ item.checkOutTime || '—' }}
                  <span v-if="item.status !== 0" class="tag-late">迟到</span>
                </div>
              </div>
              <div class="pc-activity-extra">{{ item.workHours ? item.workHours + 'h' : '—' }}</div>
            </div>
          </template>
          <div class="pc-empty" v-else>
            <el-icon class="pc-empty-icon"><Clock /></el-icon>
            <span>暂无考勤记录</span>
          </div>
        </div>

        <!-- Leave List -->
        <div class="pc-activity-list" v-if="activeActivityTab === 'leave'" v-loading="leaveLoading">
          <template v-if="recentLeaves.length">
            <div class="pc-activity-item" v-for="item in recentLeaves" :key="item.id">
              <div class="pc-activity-icon" :class="leaveStatusClass(item.status)">
                <el-icon><Document /></el-icon>
              </div>
              <div class="pc-activity-body">
                <div class="pc-activity-title">{{ item.leaveType }} · {{ item.days }}天</div>
                <div class="pc-activity-desc">{{ item.startDate }} ~ {{ item.endDate }}</div>
              </div>
              <el-tag
                :type="leaveStatusTag(item.status)"
                size="small"
                round
                effect="light"
              >{{ leaveStatusText(item.status) }}</el-tag>
            </div>
          </template>
          <div class="pc-empty" v-else>
            <el-icon class="pc-empty-icon"><Document /></el-icon>
            <span>暂无请假记录</span>
          </div>
        </div>

        <!-- Salary Slips -->
        <div class="pc-activity-list" v-if="activeActivityTab === 'salary'" v-loading="salaryLoading">
          <template v-if="recentSalaries.length">
            <div class="pc-activity-item" v-for="item in recentSalaries" :key="item.id">
              <div class="pc-activity-icon salary">
                <el-icon><Money /></el-icon>
              </div>
              <div class="pc-activity-body">
                <div class="pc-activity-title">{{ item.yearMonth }} 工资条</div>
                <div class="pc-activity-desc">
                  应发 ¥{{ formatMoney(item.baseSalary + item.performanceSalary + item.allowance + item.subsidy + item.overtimePay) }}
                  · 实发 <strong>¥{{ formatMoney(item.actualSalary) }}</strong>
                </div>
              </div>
              <el-tag
                :type="item.status === 2 ? 'success' : item.status === 1 ? '' : 'info'"
                size="small"
                round
                effect="light"
              >{{ item.status === 2 ? '已发放' : item.status === 1 ? '已确认' : '待确认' }}</el-tag>
            </div>
          </template>
          <div class="pc-empty" v-else>
            <el-icon class="pc-empty-icon"><Money /></el-icon>
            <span>暂无工资记录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Profile Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人资料"
      width="520px"
      :close-on-click-modal="false"
      destroy-on-close
      class="pc-edit-dialog"
    >
      <el-form :model="editForm" label-width="100px" label-position="left">
        <el-form-item label="手机号码">
          <el-input v-model="editForm.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="editForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="editForm.emergencyContact" placeholder="请输入紧急联系人姓名" />
        </el-form-item>
        <el-form-item label="紧急联系电话">
          <el-input v-model="editForm.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <el-form-item label="现居地址">
          <el-input v-model="editForm.currentAddress" type="textarea" :rows="2" placeholder="请输入现居地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Clock, Document, Money, Tickets, Edit, Check, WarningFilled,
  CircleCheck, EditPen, User, CreditCard
} from '@element-plus/icons-vue'
import { getCurrentUser } from '@/utils/auth'
import { hasPermission } from '@/utils/permission'
import { employeeApi, type Employee } from '@/api/employee'
import { attendanceApi, type Attendance } from '@/api/attendance'
import { leaveApi, type LeaveRequest } from '@/api/leave'
import { salaryApi, type SalaryRecord, type SalaryStructure } from '@/api/salary'

const router = useRouter()

// --------------- State ---------------
const heroLoading = ref(true)
const attLoading = ref(true)
const leaveLoading = ref(true)
const salaryLoading = ref(true)
const saving = ref(false)
const editDialogVisible = ref(false)
const activeActivityTab = ref<'attendance' | 'leave' | 'salary'>('attendance')

const employee = ref<Employee | null>(null)
const salaryStructure = ref<SalaryStructure | null>(null)
const recentAttendances = ref<Attendance[]>([])
const recentLeaves = ref<LeaveRequest[]>([])
const recentSalaries = ref<SalaryRecord[]>([])
const thisYearLeaves = ref<LeaveRequest[]>([])

const editForm = ref({
  phone: '',
  email: '',
  emergencyContact: '',
  emergencyPhone: '',
  currentAddress: ''
})

// --------------- Computed ---------------
const empId = computed(() => {
  const user = getCurrentUser()
  return user?.employeeId || 0
})
const canViewSalary = computed(() => hasPermission('salary:view'))

const empInitial = computed(() => {
  return employee.value?.name?.charAt(0) || '?'
})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const todayText = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const hireDays = computed(() => {
  if (!employee.value?.hireDate) return '—'
  const hire = new Date(employee.value.hireDate)
  const diff = Date.now() - hire.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
})

const attendanceRate = computed(() => {
  const monthRecords = recentAttendances.value
  if (!monthRecords.length) return 0
  const normal = monthRecords.filter(r => r.status === 0).length
  return Math.round((normal / monthRecords.length) * 100)
})

const profileCompleteness = computed(() => {
  const emp = employee.value
  if (!emp) return 0
  const fields = [
    emp.phone,
    emp.email,
    emp.emergencyContact,
    emp.emergencyPhone,
    emp.currentAddress,
    emp.hireDate,
    emp.department,
    emp.position
  ]
  const filled = fields.filter(Boolean).length
  return Math.round((filled / fields.length) * 100)
})

const profileMissingText = computed(() => {
  const missing = 100 - profileCompleteness.value
  if (missing <= 0) return '资料已完善，紧急联系信息可随时更新'
  if (missing <= 25) return '资料基本完整，建议补齐剩余联系信息'
  return '建议完善联系方式、紧急联系人和现居地址'
})

const totalLeaveDays = computed(() => {
  const approved = thisYearLeaves.value.filter(l => l.status === 1)
  return approved.reduce((sum, l) => sum + (l.days || 0), 0)
})

const annualLeaveRemaining = computed(() => {
  // Simplified: assume 10 days annual leave, minus used
  const annualUsed = thisYearLeaves.value
    .filter(l => l.status === 1 && l.leaveType === '年假')
    .reduce((sum, l) => sum + (l.days || 0), 0)
  return Math.max(0, 10 - annualUsed)
})

const latestSalary = computed(() => {
  return recentSalaries.value.length ? recentSalaries.value[0] : null
})

const salaryCount = computed(() => recentSalaries.value.length)

const contractStatus = computed(() => {
  if (!employee.value) return '—'
  return employee.value.status === 1 ? '有效' : '已终止'
})

const contractEndDays = computed(() => {
  // Simplified from hireDate — real system would use contract end date
  if (!employee.value?.hireDate) return null
  // Placeholder: contract typically renews after 3 years
  const hire = new Date(employee.value.hireDate)
  const contractEnd = new Date(hire)
  contractEnd.setFullYear(contractEnd.getFullYear() + 3)
  const diff = contractEnd.getTime() - Date.now()
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
})

// --------------- Methods ---------------
function formatMoney(v: number | undefined): string {
  if (v == null) return '0'
  return v.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function maskIdCard(card?: string): string {
  if (!card) return '—'
  if (card.length <= 8) return card
  return card.slice(0, 4) + '**********' + card.slice(-4)
}

function maskBankCard(card?: string): string {
  if (!card) return '—'
  if (card.length <= 4) return card
  return '****' + card.slice(-4)
}

function leaveStatusClass(status?: number): string {
  if (status === 1) return 'approved'
  if (status === 2) return 'rejected'
  if (status === 3) return 'cancelled'
  return 'pending'
}

function leaveStatusTag(status?: number): '' | 'success' | 'danger' | 'warning' | 'info' {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  if (status === 3) return 'info'
  return 'warning'
}

function leaveStatusText(status?: number): string {
  if (status === 0) return '待审批'
  if (status === 1) return '已批准'
  if (status === 2) return '已拒绝'
  if (status === 3) return '已撤销'
  return '未知'
}

function openEditDialog() {
  editForm.value = {
    phone: employee.value?.phone || '',
    email: employee.value?.email || '',
    emergencyContact: employee.value?.emergencyContact || '',
    emergencyPhone: employee.value?.emergencyPhone || '',
    currentAddress: employee.value?.currentAddress || ''
  }
  editDialogVisible.value = true
}

async function saveProfile() {
  if (!employee.value) return
  saving.value = true
  try {
    await employeeApi.updateMe({
      employee: {
        ...employee.value,
        phone: editForm.value.phone,
        email: editForm.value.email,
        emergencyContact: editForm.value.emergencyContact,
        emergencyPhone: editForm.value.emergencyPhone,
        currentAddress: editForm.value.currentAddress
      }
    })
    ElMessage.success('资料已更新')
    editDialogVisible.value = false
    await loadEmployee()
  } catch (e) {
    console.error(e)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

function goCheckIn() { router.push('/attendance') }
function goApplyLeave() { router.push('/leave') }
function goSalary() { router.push('/salary') }

function viewAll(tab: string) {
  if (tab === 'attendance') router.push('/attendance')
  else if (tab === 'leave') router.push('/leave')
  else if (canViewSalary.value) router.push('/salary')
}

// --------------- Data Loading ---------------
async function loadEmployee() {
  try {
    const emp = await employeeApi.me()
    employee.value = emp
  } catch (e) {
    console.error('Failed to load employee', e)
  } finally {
    heroLoading.value = false
  }
}

async function loadAttendance() {
  attLoading.value = true
  try {
    const list = await attendanceApi.list({ employeeId: empId.value })
    recentAttendances.value = (list || []).slice(0, 10)
  } catch (e) {
    console.error(e)
  } finally {
    attLoading.value = false
  }
}

async function loadLeaves() {
  leaveLoading.value = true
  try {
    const [recent, year] = await Promise.all([
      leaveApi.my({ employeeId: empId.value, page: 1, size: 5 }),
      leaveApi.my({ employeeId: empId.value, page: 1, size: 100 })
    ])
    recentLeaves.value = recent?.records || []
    thisYearLeaves.value = (year?.records || []).filter(l => {
      return l.createTime && l.createTime.startsWith(String(new Date().getFullYear()))
    })
  } catch (e) {
    console.error(e)
  } finally {
    leaveLoading.value = false
  }
}

async function loadSalary() {
  if (!canViewSalary.value || !empId.value) {
    salaryLoading.value = false
    return
  }
  salaryLoading.value = true
  try {
    const list = await salaryApi.recordList(empId.value)
    recentSalaries.value = (list || []).sort((a, b) =>
      b.yearMonth.localeCompare(a.yearMonth)
    ).slice(0, 5)
  } catch (e) {
    console.error(e)
  } finally {
    salaryLoading.value = false
  }
}

onMounted(() => {
  loadEmployee().then(() => {
    loadAttendance()
    loadLeaves()
    if (canViewSalary.value) loadSalary()
    else salaryLoading.value = false
  })
})
</script>

<style scoped>
/* ============================ Page Shell ============================ */
.pc-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  animation: pc-fade-in 0.5s ease-out;
}

@keyframes pc-fade-in {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ============================ Hero Banner ============================ */
.pc-hero {
  position: relative;
  border-radius: 20px;
  background: #fff;
  box-shadow:
    0 1px 3px rgba(0,0,0,0.04),
    0 4px 16px rgba(0,0,0,0.04),
    0 20px 48px -12px rgba(99,102,241,0.08);
  overflow: hidden;
  margin-bottom: 24px;
  animation: pc-card-up 0.5s ease-out 0.05s both;
}

:global(.dark .pc-hero) {
  background: linear-gradient(135deg, #1e293b 0%, #1a1f36 100%);
  box-shadow:
    0 1px 3px rgba(0,0,0,0.3),
    0 4px 16px rgba(0,0,0,0.2),
    0 20px 48px -12px rgba(99,102,241,0.15);
  border: 1px solid rgba(255,255,255,0.06);
}

.pc-hero-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 85% 20%, rgba(99,102,241,0.06) 0%, transparent 60%),
    radial-gradient(circle at 15% 70%, rgba(139,92,246,0.04) 0%, transparent 50%),
    radial-gradient(circle at 60% 90%, rgba(16,185,129,0.04) 0%, transparent 40%);
  pointer-events: none;
}

:global(.dark .pc-hero-bg) {
  background:
    radial-gradient(circle at 85% 20%, rgba(99,102,241,0.12) 0%, transparent 60%),
    radial-gradient(circle at 15% 70%, rgba(139,92,246,0.08) 0%, transparent 50%);
}

.pc-hero-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32px 36px;
  gap: 32px;
  flex-wrap: wrap;
}

.pc-hero-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.pc-avatar-wrap {
  position: relative;
}

.pc-avatar {
  background: linear-gradient(135deg, #6366f1, #8b5cf6) !important;
  color: #fff;
  font-size: 32px !important;
  font-weight: 700;
  box-shadow: 0 8px 24px -4px rgba(99,102,241,0.45);
  transition: transform 0.3s ease;
}

.pc-avatar:hover {
  transform: scale(1.05);
}

.pc-avatar-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 2px dashed rgba(99,102,241,0.25);
  animation: pc-spin 20s linear infinite;
  pointer-events: none;
}

@keyframes pc-spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

.pc-hero-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pc-greeting {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

:global(.dark .pc-greeting) {
  color: #94a3b8;
}

.pc-emp-name {
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
  line-height: 1.15;
}

:global(.dark .pc-emp-name) {
  color: #f1f5f9;
}

.pc-emp-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pc-meta-tag {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

:global(.dark .pc-meta-tag) {
  color: #94a3b8;
}

.pc-meta-dot {
  color: #cbd5e1;
  font-weight: 400;
}

:global(.dark .pc-meta-dot) {
  color: #475569;
}

.pc-hero-right {
  display: flex;
  gap: 32px;
}

.pc-hero-stat {
  text-align: center;
}

.pc-hero-stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  font-variant-numeric: tabular-nums;
}

:global(.dark .pc-hero-stat-value) {
  color: #e2e8f0;
}

.pc-hero-stat-label {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 3px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* ============================ Stats Row ============================ */
.pc-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.pc-stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03), 0 2px 8px rgba(0,0,0,0.03);
  cursor: pointer;
  transition: all 0.22s ease;
  position: relative;
  overflow: hidden;
}

:global(.dark .pc-stat-card) {
  background: #1e293b;
  border: 1px solid rgba(255,255,255,0.06);
  box-shadow: none;
}

.pc-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

:global(.dark .pc-stat-card:hover) {
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
  border-color: rgba(99,102,241,0.3);
}

.pc-stat-card:nth-child(1) { animation: pc-card-up 0.5s ease-out 0.1s both; }
.pc-stat-card:nth-child(2) { animation: pc-card-up 0.5s ease-out 0.15s both; }
.pc-stat-card:nth-child(3) { animation: pc-card-up 0.5s ease-out 0.2s both; }
.pc-stat-card:nth-child(4) { animation: pc-card-up 0.5s ease-out 0.25s both; }

@keyframes pc-card-up {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}

.pc-stat-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
}

.stat-attendance .pc-stat-icon-wrap { background: linear-gradient(135deg, #eef2ff, #e0e7ff); color: #6366f1; }
.stat-leave .pc-stat-icon-wrap      { background: linear-gradient(135deg, #ecfdf5, #d1fae5); color: #10b981; }
.stat-salary .pc-stat-icon-wrap     { background: linear-gradient(135deg, #fef3c7, #fde68a); color: #f59e0b; }
.stat-contract .pc-stat-icon-wrap   { background: linear-gradient(135deg, #fce7f3, #fbcfe8); color: #ec4899; }

:global(.dark .stat-attendance .pc-stat-icon-wrap) { background: rgba(99,102,241,0.15); color: #a5b4fc; }
:global(.dark .stat-leave .pc-stat-icon-wrap) { background: rgba(16,185,129,0.15); color: #6ee7b7; }
:global(.dark .stat-salary .pc-stat-icon-wrap) { background: rgba(245,158,11,0.15); color: #fcd34d; }
:global(.dark .stat-contract .pc-stat-icon-wrap) { background: rgba(236,72,153,0.15); color: #f9a8d4; }

.pc-stat-icon {
  font-size: 20px;
}

.pc-stat-body {
  margin-bottom: 8px;
}

.pc-stat-value {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

:global(.dark .pc-stat-value) {
  color: #f1f5f9;
}

.pc-stat-unit {
  font-size: 14px;
  font-weight: 600;
  color: #94a3b8;
  margin-left: 2px;
}

.pc-stat-label {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 5px;
  font-weight: 500;
}

:global(.dark .pc-stat-label) {
  color: #64748b;
}

.pc-stat-sub {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

.pc-stat-sub .text-warning {
  color: #f59e0b;
  font-weight: 600;
}

.pc-stat-bar {
  height: 3px;
  background: #f1f5f9;
  border-radius: 2px;
  margin-top: 10px;
  overflow: hidden;
}

:global(.dark .pc-stat-bar) {
  background: #334155;
}

.pc-stat-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  border-radius: 2px;
  transition: width 0.6s ease;
}

/* ============================ Content Grid ============================ */
.pc-content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.pc-panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03), 0 2px 8px rgba(0,0,0,0.03);
  overflow: hidden;
}

:global(.dark .pc-panel) {
  background: #1e293b;
  border: 1px solid rgba(255,255,255,0.06);
  box-shadow: none;
}

.pc-content-grid .pc-panel:first-child {
  animation: pc-card-up 0.5s ease-out 0.3s both;
}

.pc-content-grid .pc-panel:last-child {
  animation: pc-card-up 0.5s ease-out 0.35s both;
}

.pc-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 14px;
  border-bottom: 1px solid #f1f5f9;
}

:global(.dark .pc-panel-header) {
  border-bottom-color: rgba(255,255,255,0.06);
}

.pc-panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

:global(.dark .pc-panel-title) {
  color: #f1f5f9;
}

.pc-tabs {
  display: flex;
  gap: 0;
}

.pc-tab {
  font-size: 13px;
  font-weight: 500;
  color: #94a3b8;
  padding: 6px 14px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: relative;
}

.pc-tab:hover {
  color: #64748b;
  background: #f8fafc;
}

:global(.dark .pc-tab:hover) {
  color: #cbd5e1;
  background: rgba(255,255,255,0.04);
}

.pc-tab.active {
  color: #6366f1;
  background: #eef2ff;
  font-weight: 600;
}

:global(.dark .pc-tab.active) {
  color: #a5b4fc;
  background: rgba(99,102,241,0.15);
}

.pc-panel-action {
  font-size: 12px;
  color: #6366f1;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s;
}

.pc-panel-action:hover {
  color: #4f46e5;
}

/* Info Grid */
.pc-info-grid {
  padding: 16px 24px 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
}

.pc-info-item {
  padding: 12px 0;
  border-bottom: 1px solid #f8fafc;
}

:global(.dark .pc-info-item) {
  border-bottom-color: rgba(255,255,255,0.04);
}

.pc-info-item:nth-last-child(-n+2) {
  border-bottom: none;
}

.pc-info-item:last-child {
  grid-column: 1 / -1;
}

.pc-info-label {
  display: block;
  font-size: 11px;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.pc-info-value {
  display: block;
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

:global(.dark .pc-info-value) {
  color: #e2e8f0;
}

.pc-info-value.masked {
  font-family: 'SF Mono', 'Monaco', 'Menlo', monospace;
  letter-spacing: 0.5px;
}

.pc-info-value-wide {
  word-break: break-all;
}

/* Activity List */
.pc-activity-list {
  padding: 8px 16px 16px;
  max-height: 380px;
  overflow-y: auto;
}

.pc-activity-list::-webkit-scrollbar {
  width: 4px;
}

.pc-activity-list::-webkit-scrollbar-track { background: transparent; }

.pc-activity-list::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 4px;
}

:global(.dark .pc-activity-list::-webkit-scrollbar-thumb) {
  background: #334155;
}

.pc-activity-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 12px 8px;
  border-radius: 10px;
  transition: background 0.15s ease;
}

.pc-activity-item:hover {
  background: #f8fafc;
}

:global(.dark .pc-activity-item:hover) {
  background: rgba(255,255,255,0.03);
}

.pc-activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
}

.pc-activity-icon.normal { background: #ecfdf5; color: #10b981; }
.pc-activity-icon.late { background: #fef2f2; color: #ef4444; }
.pc-activity-icon.approved { background: #ecfdf5; color: #10b981; }
.pc-activity-icon.rejected { background: #fef2f2; color: #ef4444; }
.pc-activity-icon.cancelled { background: #f1f5f9; color: #94a3b8; }
.pc-activity-icon.pending { background: #fffbeb; color: #f59e0b; }
.pc-activity-icon.salary { background: #eef2ff; color: #6366f1; }

:global(.dark .pc-activity-icon.normal) { background: rgba(16,185,129,0.15); color: #6ee7b7; }
:global(.dark .pc-activity-icon.late) { background: rgba(239,68,68,0.15); color: #fca5a5; }
:global(.dark .pc-activity-icon.approved) { background: rgba(16,185,129,0.15); color: #6ee7b7; }
:global(.dark .pc-activity-icon.rejected) { background: rgba(239,68,68,0.15); color: #fca5a5; }
:global(.dark .pc-activity-icon.cancelled) { background: rgba(148,163,184,0.12); color: #64748b; }
:global(.dark .pc-activity-icon.pending) { background: rgba(245,158,11,0.15); color: #fcd34d; }
:global(.dark .pc-activity-icon.salary) { background: rgba(99,102,241,0.15); color: #a5b4fc; }

.pc-activity-body {
  flex: 1;
  min-width: 0;
}

.pc-activity-title {
  font-size: 13px;
  font-weight: 600;
  color: #0f172a;
}

:global(.dark .pc-activity-title) {
  color: #e2e8f0;
}

.pc-activity-desc {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 3px;
}

.pc-activity-desc strong {
  color: #10b981;
  font-weight: 700;
}

.pc-activity-extra {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  font-variant-numeric: tabular-nums;
  flex-shrink: 0;
}

.tag-late {
  background: #fef2f2;
  color: #ef4444;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 6px;
  font-weight: 600;
}

/* Empty */
.pc-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  color: #cbd5e1;
  gap: 10px;
}

.pc-empty-icon {
  font-size: 36px;
  opacity: 0.4;
}

.pc-empty span {
  font-size: 13px;
  color: #94a3b8;
}

/* ============================ Quick Actions ============================ */
.pc-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  animation: pc-card-up 0.5s ease-out 0.4s both;
}

.pc-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  transition: all 0.22s ease;
  box-shadow: 0 1px 2px rgba(0,0,0,0.02);
}

:global(.dark .pc-action-btn) {
  background: #1e293b;
  border-color: rgba(255,255,255,0.06);
  color: #cbd5e1;
}

.pc-action-btn:hover {
  color: #6366f1;
  border-color: #e0e7ff;
  background: #f8fafc;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(99,102,241,0.1);
}

:global(.dark .pc-action-btn:hover) {
  color: #a5b4fc;
  border-color: rgba(99,102,241,0.3);
  background: rgba(99,102,241,0.08);
  box-shadow: 0 4px 16px rgba(99,102,241,0.15);
}

.pc-action-btn .el-icon {
  font-size: 18px;
}

/* ============================ Edit Dialog ============================ */
.pc-edit-dialog :deep(.el-dialog) {
  border-radius: 16px;
}

.pc-edit-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
}

:global(.dark .pc-edit-dialog) :deep(.el-dialog__header) {
  border-bottom-color: rgba(255,255,255,0.06);
}

.pc-edit-dialog :deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 700;
}

/* ============================ Responsive ============================ */
@media (max-width: 960px) {
  .pc-hero-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .pc-hero-right {
    width: 100%;
    justify-content: flex-start;
  }

  .pc-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .pc-content-grid {
    grid-template-columns: 1fr;
  }

  .pc-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .pc-page {
    padding: 16px;
  }

  .pc-hero-content {
    padding: 24px 20px;
  }

  .pc-stats {
    grid-template-columns: 1fr;
  }

  .pc-hero-right {
    gap: 16px;
  }
}

/* ============================ Refined Experience ============================ */
.pc-page {
  --pc-bg: #f4f7fb;
  --pc-surface: rgba(255, 255, 255, 0.86);
  --pc-surface-strong: #ffffff;
  --pc-border: rgba(148, 163, 184, 0.18);
  --pc-text: #172033;
  --pc-muted: #74839a;
  --pc-soft: #edf2f8;
  --pc-accent: #4f7cff;
  --pc-accent-2: #1fbf9a;
  --pc-shadow: 0 18px 55px rgba(31, 41, 55, 0.08);
  --pc-shadow-soft: 0 10px 28px rgba(31, 41, 55, 0.06);
  position: relative;
  box-sizing: border-box;
  height: 100%;
  max-width: 1120px;
  padding: 18px 24px 28px;
  overflow-x: hidden;
  overflow-y: auto;
  scrollbar-gutter: stable;
  color: var(--pc-text);
}

:global(.dark .pc-page) {
  --pc-bg: #0b1220;
  --pc-surface: rgba(20, 29, 46, 0.82);
  --pc-surface-strong: #111827;
  --pc-border: rgba(148, 163, 184, 0.16);
  --pc-text: #edf4ff;
  --pc-muted: #8fa1ba;
  --pc-soft: rgba(148, 163, 184, 0.1);
  --pc-accent: #7aa2ff;
  --pc-accent-2: #45d6b6;
  --pc-shadow: 0 24px 70px rgba(0, 0, 0, 0.28);
  --pc-shadow-soft: 0 12px 36px rgba(0, 0, 0, 0.22);
}

.pc-page-bg {
  position: absolute;
  inset: 0;
  z-index: -1;
  border-radius: 28px;
  background:
    radial-gradient(circle at 12% 10%, rgba(79, 124, 255, 0.16), transparent 34%),
    radial-gradient(circle at 88% 18%, rgba(31, 191, 154, 0.13), transparent 30%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.52), rgba(255, 255, 255, 0));
}

:global(.dark .pc-page-bg) {
  background:
    radial-gradient(circle at 12% 8%, rgba(122, 162, 255, 0.18), transparent 35%),
    radial-gradient(circle at 90% 14%, rgba(69, 214, 182, 0.12), transparent 32%),
    linear-gradient(180deg, rgba(15, 23, 42, 0.35), rgba(15, 23, 42, 0));
}

.pc-overview {
  display: grid;
  grid-template-columns: minmax(0, 1.62fr) minmax(280px, 0.9fr);
  align-items: start;
  gap: 14px;
  margin-bottom: 14px;
}

.pc-hero,
.pc-panel,
.pc-stat-card {
  border: 1px solid var(--pc-border);
  background: var(--pc-surface);
  backdrop-filter: blur(18px);
  box-shadow: var(--pc-shadow-soft);
}

.pc-hero {
  display: flex;
  flex-direction: column;
  min-height: 252px;
  margin-bottom: 0;
  border-radius: 22px;
}

:global(.dark .pc-hero) {
  background: var(--pc-surface);
  border-color: var(--pc-border);
}

.pc-hero-bg {
  background:
    linear-gradient(135deg, rgba(79, 124, 255, 0.12), transparent 44%),
    radial-gradient(circle at 82% 26%, rgba(31, 191, 154, 0.16), transparent 34%),
    radial-gradient(circle at 16% 88%, rgba(251, 191, 36, 0.14), transparent 28%);
}

:global(.dark .pc-hero-bg) {
  background:
    linear-gradient(135deg, rgba(122, 162, 255, 0.14), transparent 46%),
    radial-gradient(circle at 84% 26%, rgba(69, 214, 182, 0.15), transparent 36%);
}

.pc-hero-content {
  align-items: flex-start;
  gap: 22px;
  padding: 22px 26px 14px;
}

.pc-hero-left {
  align-items: flex-start;
}

.pc-avatar {
  width: 64px !important;
  height: 64px !important;
  background: linear-gradient(135deg, #356dff, #20c997) !important;
  font-size: 28px !important;
  box-shadow: 0 18px 34px rgba(53, 109, 255, 0.28);
}

.pc-avatar-ring {
  display: none;
}

.pc-avatar-status {
  position: absolute;
  right: 2px;
  bottom: 4px;
  width: 14px;
  height: 14px;
  border-radius: 999px;
  border: 2px solid var(--pc-surface-strong);
  background: #22c55e;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.12);
}

.pc-kicker {
  width: fit-content;
  margin-bottom: 6px;
  padding: 4px 9px;
  border-radius: 999px;
  background: rgba(79, 124, 255, 0.1);
  color: var(--pc-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.pc-greeting {
  color: var(--pc-muted);
  font-size: 14px;
}

:global(.dark .pc-greeting) {
  color: var(--pc-muted);
}

.pc-emp-name {
  color: var(--pc-text);
  font-size: 29px;
  line-height: 1.08;
}

:global(.dark .pc-emp-name) {
  color: var(--pc-text);
}

.pc-emp-meta {
  gap: 6px;
  margin-top: 6px;
}

.pc-meta-pill {
  display: inline-flex;
  align-items: center;
  min-height: 22px;
  padding: 0 7px;
  border-radius: 999px;
  background: var(--pc-soft);
  color: var(--pc-muted);
  font-size: 11px;
  font-weight: 700;
}

.pc-emp-meta :deep(.el-tag) {
  height: 20px;
  padding: 0 6px;
  font-size: 11px;
}

.pc-hero-right {
  gap: 6px;
  padding: 5px;
  border: 1px solid var(--pc-border);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.42);
}

:global(.dark .pc-hero-right) {
  background: rgba(15, 23, 42, 0.36);
}

.pc-hero-stat {
  min-width: 76px;
  padding: 9px 8px;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.58);
}

:global(.dark .pc-hero-stat) {
  background: rgba(15, 23, 42, 0.52);
}

.pc-hero-stat-value {
  color: var(--pc-text);
  font-size: 19px;
}

:global(.dark .pc-hero-stat-value) {
  color: var(--pc-text);
}

.pc-hero-stat-label,
.pc-stat-label,
.pc-stat-sub,
.pc-info-label,
.pc-activity-desc,
.pc-empty span {
  color: var(--pc-muted);
}

.pc-action-strip {
  position: relative;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  margin: auto 18px 18px;
  padding: 9px;
  border: 1px solid var(--pc-border);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.5);
}

:global(.dark .pc-action-strip) {
  background: rgba(15, 23, 42, 0.42);
}

.pc-action-btn {
  border: 1px solid transparent;
  outline: none;
  min-height: 40px;
  padding: 0 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.72);
  color: var(--pc-text);
  box-shadow: none;
}

:global(.dark .pc-action-btn) {
  background: rgba(30, 41, 59, 0.72);
  color: var(--pc-text);
}

.pc-action-btn.primary {
  background: linear-gradient(135deg, var(--pc-accent), var(--pc-accent-2));
  color: #fff;
  box-shadow: 0 12px 24px rgba(79, 124, 255, 0.24);
}

.pc-action-btn:hover {
  border-color: rgba(79, 124, 255, 0.22);
  background: rgba(255, 255, 255, 0.94);
  color: var(--pc-accent);
}

:global(.dark .pc-action-btn:hover) {
  border-color: rgba(122, 162, 255, 0.28);
  background: rgba(30, 41, 59, 0.95);
  color: var(--pc-accent);
}

.pc-action-btn.primary:hover {
  color: #fff;
  filter: saturate(1.05) brightness(1.02);
}

.pc-stats {
  grid-template-columns: 1fr;
  align-self: start;
  gap: 10px;
  margin-bottom: 0;
}

.pc-stat-card {
  min-height: 0;
  padding: 10px 12px;
  border-radius: 20px;
  cursor: pointer;
}

:global(.dark .pc-stat-card) {
  background: var(--pc-surface);
  border-color: var(--pc-border);
}

.pc-stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 6px;
}

.pc-stat-icon-wrap {
  width: 26px;
  height: 26px;
  margin-bottom: 0;
  border-radius: 10px;
}

.pc-stat-icon {
  font-size: 16px;
}

.pc-stat-value {
  color: var(--pc-text);
  font-size: 22px;
  line-height: 1;
}

:global(.dark .pc-stat-value) {
  color: var(--pc-text);
}

.pc-stat-value.salary-value {
  font-size: 20px;
}

.pc-stat-label {
  margin-top: 0;
  font-size: 11px;
}

.pc-stat-sub {
  font-size: 10px;
}

.pc-stat-bar {
  margin-top: 6px;
}

.pc-panel {
  border-radius: 24px;
  box-shadow: var(--pc-shadow-soft);
}

:global(.dark .pc-panel) {
  background: var(--pc-surface);
  border-color: var(--pc-border);
}

.pc-content-grid {
  grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.05fr);
  gap: 14px;
  margin-bottom: 0;
}

.pc-panel-header {
  padding: 16px 18px 12px;
  border-bottom-color: var(--pc-border);
}

:global(.dark .pc-panel-header) {
  border-bottom-color: var(--pc-border);
}

.pc-panel-title {
  color: var(--pc-text);
  font-size: 16px;
}

:global(.dark .pc-panel-title) {
  color: var(--pc-text);
}

.pc-panel-subtitle {
  margin: 5px 0 0;
  color: var(--pc-muted);
  font-size: 12px;
}

.pc-info-grid {
  padding: 4px 18px 18px;
  gap: 8px;
}

.pc-info-item {
  padding: 11px;
  border: 1px solid var(--pc-border);
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.58);
}

.pc-info-label {
  font-size: 11px;
}

:global(.dark .pc-info-item) {
  border-color: var(--pc-border);
  background: rgba(15, 23, 42, 0.34);
}

.pc-info-item:nth-last-child(-n+2) {
  border-bottom: 1px solid var(--pc-border);
}

.pc-info-value {
  color: var(--pc-text);
  font-size: 13px;
}

:global(.dark .pc-info-value) {
  color: var(--pc-text);
}

.pc-tabs {
  padding: 3px;
  border-radius: 13px;
  background: var(--pc-soft);
}

.pc-tab {
  color: var(--pc-muted);
  padding: 6px 10px;
  border-radius: 10px;
}

.pc-tab.active {
  background: var(--pc-surface-strong);
  color: var(--pc-accent);
  box-shadow: 0 6px 16px rgba(31, 41, 55, 0.08);
}

:global(.dark .pc-tab.active) {
  background: rgba(122, 162, 255, 0.14);
  color: var(--pc-accent);
}

.pc-panel-action {
  color: var(--pc-accent);
}

.pc-activity-list {
  max-height: 390px;
  padding: 8px 12px 14px;
}

.pc-activity-item {
  padding: 11px 10px;
  border: 1px solid transparent;
}

.pc-activity-item:hover {
  border-color: var(--pc-border);
  background: rgba(248, 250, 252, 0.7);
}

:global(.dark .pc-activity-item:hover) {
  border-color: var(--pc-border);
  background: rgba(15, 23, 42, 0.42);
}

.pc-activity-title {
  color: var(--pc-text);
}

:global(.dark .pc-activity-title) {
  color: var(--pc-text);
}

@media (min-width: 961px) and (max-width: 1180px) {
  .pc-hero-content {
    flex-direction: column;
    flex-wrap: nowrap;
    gap: 8px;
    padding: 18px 22px 10px;
  }

  .pc-hero-left {
    width: 100%;
    gap: 16px;
  }

  .pc-hero-right {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    width: 100%;
    gap: 5px;
    padding: 4px;
  }

  .pc-hero-stat {
    min-width: 0;
    padding: 6px 4px;
  }

  .pc-hero-stat-value {
    font-size: 17px;
  }

  .pc-hero-stat-label {
    font-size: 11px;
  }

  .pc-action-strip {
    margin: 0 16px 16px;
    padding: 7px;
  }

  .pc-action-btn {
    min-height: 36px;
    padding: 0 8px;
    font-size: 12px;
  }
}

@media (max-width: 960px) {
  .pc-overview {
    grid-template-columns: 1fr;
  }

  .pc-stats {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .pc-page {
    padding: 18px;
  }

  .pc-action-strip,
  .pc-stats,
  .pc-info-grid {
    grid-template-columns: 1fr;
  }

  .pc-hero-right {
    flex-wrap: wrap;
  }
}
</style>
