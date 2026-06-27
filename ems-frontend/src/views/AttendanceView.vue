<template>
  <div class="attendance-page ems-page">
    <!-- 顶部：今日打卡 + 统计 -->
    <div class="top-section">
      <!-- 今日打卡卡片 -->
      <div class="checkin-card">
        <div class="checkin-left">
          <div class="date-display">
            <div class="date-day">{{ today.day }}</div>
            <div class="date-info">
              <div class="date-month">{{ today.month }}月</div>
              <div class="date-weekday">{{ today.weekday }}</div>
            </div>
          </div>
          <div class="clock-display">
            <div class="clock-time ems-mono">{{ currentTime }}</div>
            <div class="clock-label">当前时间</div>
          </div>
        </div>
        <div class="checkin-right">
          <div class="checkin-actions">
            <div class="checkin-btn-wrap in">
              <div class="checkin-btn" :class="{ done: todayStatus.checkedIn }" @click="handleCheckIn">
                <div class="btn-icon">
                  <el-icon><Sunrise /></el-icon>
                </div>
                <div class="btn-text">
                  <div class="btn-label">{{ todayStatus.checkedIn ? '已签到' : '签到' }}</div>
                  <div class="btn-time">{{ todayStatus.checkInTime || '上班打卡' }}</div>
                </div>
              </div>
              <div class="checkin-status-tag" :class="{ success: todayStatus.checkedIn }">
                <el-icon><CircleCheck v-if="todayStatus.checkedIn" /><Clock v-else /></el-icon>
                <span>{{ todayStatus.checkedIn ? '准时' : '待签到' }}</span>
              </div>
            </div>
            <div class="checkin-divider">
              <div class="divider-line"></div>
              <div class="divider-icon">
                <el-icon><ArrowRight /></el-icon>
              </div>
              <div class="divider-line"></div>
            </div>
            <div class="checkin-btn-wrap out">
              <div class="checkin-btn" :class="{ done: todayStatus.checkedOut }" @click="handleCheckOut">
                <div class="btn-icon">
                  <el-icon><Sunset /></el-icon>
                </div>
                <div class="btn-text">
                  <div class="btn-label">{{ todayStatus.checkedOut ? '已签退' : '签退' }}</div>
                  <div class="btn-time">{{ todayStatus.checkOutTime || '下班打卡' }}</div>
                </div>
              </div>
              <div class="checkin-status-tag" :class="{ success: todayStatus.checkedOut, pending: !todayStatus.checkedOut && todayStatus.checkedIn }">
                <el-icon><CircleCheck v-if="todayStatus.checkedOut" /><Clock v-else /></el-icon>
                <span>{{ todayStatus.checkedOut ? '完成' : todayStatus.checkedIn ? '进行中' : '待签退' }}</span>
              </div>
            </div>
          </div>
          <div class="work-hours-today" v-if="todayStatus.workHours">
            <span class="wh-label">今日工时</span>
            <span class="wh-value ems-mono">{{ todayStatus.workHours }}<em>h</em></span>
          </div>
        </div>
      </div>

      <!-- 本月统计 -->
      <div class="stats-card">
        <div class="stats-header">
          <div class="stats-title">
            <el-icon class="stats-icon"><DataAnalysis /></el-icon>
            <span>本月考勤</span>
          </div>
          <div class="stats-month">{{ currentMonth }}</div>
        </div>
        <div class="stats-grid">
          <div class="stat-item normal">
            <div class="stat-value ems-mono">{{ monthStats.normal || 0 }}</div>
            <div class="stat-label">正常</div>
          </div>
          <div class="stat-item late">
            <div class="stat-value ems-mono">{{ monthStats.late || 0 }}</div>
            <div class="stat-label">迟到</div>
          </div>
          <div class="stat-item early">
            <div class="stat-value ems-mono">{{ monthStats.early || 0 }}</div>
            <div class="stat-label">早退</div>
          </div>
          <div class="stat-item absent">
            <div class="stat-value ems-mono">{{ monthStats.absent || 0 }}</div>
            <div class="stat-label">缺勤</div>
          </div>
          <div class="stat-item leave">
            <div class="stat-value ems-mono">{{ monthStats.leave || 0 }}</div>
            <div class="stat-label">请假</div>
          </div>
          <div class="stat-item business">
            <div class="stat-value ems-mono">{{ monthStats.business || 0 }}</div>
            <div class="stat-label">出差</div>
          </div>
        </div>
        <div class="stats-footer">
          <div class="attendance-rate">
            <span class="rate-label">出勤率</span>
            <div class="rate-bar">
              <div class="rate-fill" :style="{ width: attendanceRate + '%' }"></div>
            </div>
            <span class="rate-value ems-mono">{{ attendanceRate }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 考勤记录表格 -->
    <div class="record-section">
      <div class="section-header">
        <div class="section-title">
          <span class="title-dot"></span>
          考勤记录
        </div>
        <div class="section-filters">
          <el-select v-model="searchForm.employeeId" placeholder="选择员工" clearable filterable size="default" class="filter-select">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
          <el-date-picker v-model="searchForm.yearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" size="default" class="filter-select" />
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable size="default" class="filter-select">
            <el-option :value="0" label="正常" />
            <el-option :value="1" label="迟到" />
            <el-option :value="2" label="早退" />
            <el-option :value="3" label="缺勤" />
            <el-option :value="4" label="请假" />
            <el-option :value="5" label="出差" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button :icon="RefreshRight" @click="resetSearch">重置</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" class="attendance-table" stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="employeeId" label="员工" min-width="140">
          <template #default="{ row }">
            <div class="employee-cell">
              <el-avatar :size="30" class="emp-avatar">{{ getEmployeeName(row.employeeId)?.charAt(0) }}</el-avatar>
              <div class="emp-info">
                <span class="emp-name">{{ getEmployeeName(row.employeeId) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="attendanceDate" label="日期" width="110" align="center" sortable>
          <template #default="{ row }">
            <div class="date-cell">
              <span class="date-main">{{ formatDate(row.attendanceDate) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="签到" width="130" align="center">
          <template #default="{ row }">
            <div class="time-cell" :class="{ late: row.status === 1 }">
              <el-icon class="time-icon"><Sunrise /></el-icon>
              <span class="time-text ems-mono">{{ formatTime(row.checkInTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="签退" width="130" align="center">
          <template #default="{ row }">
            <div class="time-cell" :class="{ early: row.status === 2 }">
              <el-icon class="time-icon"><Sunset /></el-icon>
              <span class="time-text ems-mono">{{ formatTime(row.checkOutTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="workHours" label="工时" width="90" align="center">
          <template #default="{ row }">
            <span class="work-hours" :class="{ positive: row.workHours >= 8 }">{{ row.workHours ? row.workHours + 'h' : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light" size="small" round>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.remark" class="remark-text">{{ row.remark }}</span>
            <span v-else class="remark-empty">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无考勤数据" :image-size="100" />
      </div>

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
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑考勤" width="520px">
      <el-form :model="form" label-width="90px" class="edit-form">
        <el-form-item label="员工">
          <el-select v-model="form.employeeId" placeholder="选择员工" filterable style="width: 100%">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.attendanceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="签到时间">
          <el-date-picker v-model="form.checkInTime" type="datetime" placeholder="选择签到时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="签退时间">
          <el-date-picker v-model="form.checkOutTime" type="datetime" placeholder="选择签退时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
            <el-option :value="0" label="正常" />
            <el-option :value="1" label="迟到" />
            <el-option :value="2" label="早退" />
            <el-option :value="3" label="缺勤" />
            <el-option :value="4" label="请假" />
            <el-option :value="5" label="出差" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Search, RefreshRight, Sunrise, Sunset, DataAnalysis, CircleCheck, ArrowRight } from '@element-plus/icons-vue'
import { attendanceApi, type Attendance } from '@/api/attendance'
import { employeeApi, type Employee } from '@/api/employee'

const loading = ref(false)
const tableData = ref<Attendance[]>([])
const employees = ref<Employee[]>([])
const dialogVisible = ref(false)
const currentTime = ref('')
let timer: number | null = null

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

const form = reactive<Attendance>({
  id: undefined,
  employeeId: 0,
  attendanceDate: '',
  checkInTime: '',
  checkOutTime: '',
  status: 0,
  remark: '',
})

const today = computed(() => {
  const now = new Date()
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return {
    day: now.getDate(),
    month: now.getMonth() + 1,
    weekday: weekdays[now.getDay()],
  }
})

const currentMonth = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月`
})

const todayStatus = reactive({
  checkedIn: false,
  checkedOut: false,
  checkInTime: '',
  checkOutTime: '',
  workHours: 0,
})

const monthStats = reactive({
  normal: 0,
  late: 0,
  early: 0,
  absent: 0,
  leave: 0,
  business: 0,
})

const attendanceRate = computed(() => {
  const total = monthStats.normal + monthStats.late + monthStats.early + monthStats.absent + monthStats.leave + monthStats.business
  if (total === 0) return 100
  return Math.round(((monthStats.normal + monthStats.late + monthStats.early) / total) * 100)
})

const updateTime = () => {
  const now = new Date()
  const h = String(now.getHours()).padStart(2, '0')
  const m = String(now.getMinutes()).padStart(2, '0')
  const s = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${h}:${m}:${s}`
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await attendanceApi.page({
      employeeId: searchForm.employeeId,
      yearMonth: searchForm.yearMonth,
      status: searchForm.status,
      page: pagination.page,
      size: pagination.size,
    })
    tableData.value = res.records
    pagination.total = res.total
    calculateStats(res.records)
  } finally {
    loading.value = false
  }
}

const calculateStats = (records: Attendance[]) => {
  monthStats.normal = 0
  monthStats.late = 0
  monthStats.early = 0
  monthStats.absent = 0
  monthStats.leave = 0
  monthStats.business = 0
  records.forEach(r => {
    switch (r.status) {
      case 0: monthStats.normal++; break
      case 1: monthStats.late++; break
      case 2: monthStats.early++; break
      case 3: monthStats.absent++; break
      case 4: monthStats.leave++; break
      case 5: monthStats.business++; break
    }
  })
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

const handleCheckIn = async () => {
  if (!searchForm.employeeId) {
    ElMessage.warning('请先选择员工')
    return
  }
  try {
    await attendanceApi.checkIn(searchForm.employeeId)
    ElMessage.success('签到成功')
    todayStatus.checkedIn = true
    const now = new Date()
    todayStatus.checkInTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleCheckOut = async () => {
  if (!searchForm.employeeId) {
    ElMessage.warning('请先选择员工')
    return
  }
  try {
    await attendanceApi.checkOut(searchForm.employeeId)
    ElMessage.success('签退成功')
    todayStatus.checkedOut = true
    const now = new Date()
    todayStatus.checkOutTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleEdit = (row: Attendance) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row: Attendance) => {
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  await attendanceApi.delete(row.id!)
  ElMessage.success('删除成功')
  loadData()
}

const submitForm = async () => {
  await attendanceApi.update(form)
  ElMessage.success('更新成功')
  dialogVisible.value = false
  loadData()
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'success', 1: 'warning', 2: 'warning', 3: 'danger', 4: 'info', 5: 'primary' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '正常', 1: '迟到', 2: '早退', 3: '缺勤', 4: '请假', 5: '出差' }
  return map[status] || '未知'
}

const getEmployeeName = (id: number) => {
  const emp = employees.value.find(e => e.id === id)
  return emp?.name || `员工#${id}`
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return '-'
  const t = timeStr.split(' ')[1] || timeStr.split('T')[1]
  if (!t) return timeStr
  return t.substring(0, 5)
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
  loadEmployees()
  loadData()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.attendance-page {
  height: calc(100vh - 56px);
  overflow-y: auto;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 18px 20px 24px;
}

/* ========== 顶部区域 ========== */
.top-section {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 16px;
  flex-shrink: 0;
}

/* ===== 打卡卡片 ===== */
.checkin-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 22px 28px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px -8px rgba(99, 102, 241, 0.45);
}

.checkin-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255,255,255,0.12) 0%, transparent 65%);
  border-radius: 50%;
}

.checkin-card::after {
  content: '';
  position: absolute;
  bottom: -40%;
  left: -5%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.4) 0%, transparent 65%);
  border-radius: 50%;
}

.checkin-left {
  display: flex;
  flex-direction: column;
  gap: 18px;
  position: relative;
  z-index: 1;
}

.date-display {
  display: flex;
  align-items: center;
  gap: 14px;
}

.date-day {
  font-size: 44px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.03em;
  font-family: var(--font-mono);
}

.date-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.date-month {
  font-size: 15px;
  font-weight: 600;
  opacity: 0.95;
}

.date-weekday {
  font-size: 12px;
  opacity: 0.75;
  font-weight: 500;
}

.clock-display {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.clock-time {
  font-size: 36px;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1;
  font-feature-settings: 'tnum';
}

.clock-label {
  font-size: 11px;
  opacity: 0.65;
  font-weight: 500;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.checkin-right {
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: relative;
  z-index: 1;
}

.checkin-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.checkin-btn-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.checkin-btn {
  width: 110px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 12px;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.25s var(--ease-out);
  backdrop-filter: blur(10px);
}

.checkin-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px -6px rgba(0, 0, 0, 0.25);
}

.checkin-btn.done {
  background: rgba(255, 255, 255, 0.2);
  cursor: default;
}

.checkin-btn.done:hover {
  transform: none;
  box-shadow: none;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.checkin-btn.done .btn-icon {
  background: rgba(134, 239, 172, 0.3);
}

.btn-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
}

.btn-label {
  font-size: 13px;
  font-weight: 600;
}

.btn-time {
  font-size: 11px;
  opacity: 0.75;
  font-family: var(--font-mono);
}

.checkin-status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  padding: 3px 10px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  font-weight: 500;
  opacity: 0.85;
}

.checkin-status-tag.success {
  background: rgba(134, 239, 172, 0.25);
  color: #bbf7d0;
  opacity: 1;
}

.checkin-status-tag.pending {
  background: rgba(251, 191, 36, 0.25);
  color: #fde68a;
  opacity: 1;
}

.checkin-divider {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding-top: 8px;
}

.divider-line {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
}

.divider-icon {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.work-hours-today {
  display: flex;
  align-items: baseline;
  justify-content: flex-end;
  gap: 8px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  backdrop-filter: blur(8px);
}

.wh-label {
  font-size: 11px;
  opacity: 0.75;
  font-weight: 500;
}

.wh-value {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.wh-value em {
  font-style: normal;
  font-size: 12px;
  opacity: 0.75;
  font-weight: 500;
  margin-left: 2px;
}

/* ===== 统计卡片 ===== */
.stats-card {
  background: var(--bg-elevated);
  border-radius: 16px;
  border: 1px solid var(--border-subtle);
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.3s var(--ease-out);
}

.stats-card:hover {
  box-shadow: var(--shadow-md);
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
}

.stats-icon {
  font-size: 18px;
  color: var(--brand-500);
}

.stats-month {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
  font-family: var(--font-mono);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  padding: 8px 4px;
  border-radius: 8px;
  transition: all 0.2s var(--ease-out);
}

.stat-item:hover {
  background: var(--bg-soft);
  transform: translateY(-1px);
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1;
}

.stat-label {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-tertiary);
}

.stat-item.normal .stat-value { color: var(--emerald-600); }
.stat-item.late .stat-value { color: var(--amber-600); }
.stat-item.early .stat-value { color: #f97316; }
.stat-item.absent .stat-value { color: var(--rose-600); }
.stat-item.leave .stat-value { color: var(--sky-600); }
.stat-item.business .stat-value { color: var(--violet-500); }

.stats-footer {
  padding-top: 10px;
  border-top: 1px dashed var(--border-default);
}

.attendance-rate {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rate-label {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
  flex-shrink: 0;
}

.rate-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-soft);
  border-radius: 3px;
  overflow: hidden;
}

.rate-fill {
  height: 100%;
  background: linear-gradient(90deg, #10b981, #34d399);
  border-radius: 3px;
  transition: width 0.6s var(--ease-out);
}

.rate-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--emerald-600);
  flex-shrink: 0;
  min-width: 42px;
  text-align: right;
}

/* ========== 记录区域 ========== */
.record-section {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.title-dot {
  width: 4px;
  height: 18px;
  border-radius: 2px;
  background: linear-gradient(180deg, var(--brand-500), var(--violet-500));
}

.section-filters {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-select {
  width: 140px;
}

.attendance-table {
  flex: 1;
  min-height: 0;
  border-radius: 10px;
  overflow: hidden;
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
  font-size: 13px;
  box-shadow: 0 2px 6px -2px rgba(99, 102, 241, 0.4);
}

.emp-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.emp-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.date-cell .date-main {
  font-weight: 500;
  font-size: 13px;
  color: var(--text-primary);
}

.time-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
}

.time-icon {
  font-size: 14px;
  color: var(--text-tertiary);
}

.time-text {
  font-weight: 500;
  color: var(--text-primary);
  font-feature-settings: 'tnum';
}

.time-cell.late .time-text {
  color: var(--amber-600);
  font-weight: 600;
}

.time-cell.late .time-icon {
  color: var(--amber-500);
}

.time-cell.early .time-text {
  color: #f97316;
  font-weight: 600;
}

.time-cell.early .time-icon {
  color: #f97316;
}

.work-hours {
  font-family: var(--font-mono);
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 13px;
}

.work-hours.positive {
  color: var(--emerald-600);
}

.remark-text {
  font-size: 12.5px;
  color: var(--text-secondary);
}

.remark-empty {
  color: var(--text-tertiary);
  font-size: 13px;
}

.empty-state {
  padding: 24px 0;
}

.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  gap: 16px;
  padding-top: 10px;
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

.edit-form {
  padding: 4px 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .top-section {
    grid-template-columns: 1fr;
  }
  .stats-grid {
    grid-template-columns: repeat(6, 1fr);
  }
}

@media (max-width: 768px) {
  .checkin-card {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px 20px;
  }
  .checkin-actions {
    width: 100%;
    justify-content: space-between;
  }
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .section-filters {
    width: 100%;
  }
}
</style>
