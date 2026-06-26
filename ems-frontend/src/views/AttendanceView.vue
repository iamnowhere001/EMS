<template>
  <div class="attendance-page ems-page">
    <div class="page-header">
      <h2>考勤管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleCheckIn">签到</el-button>
        <el-button type="success" @click="handleCheckOut">签退</el-button>
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
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="employeeId" label="员工ID" width="100" />
      <el-table-column prop="attendanceDate" label="日期" width="120" />
      <el-table-column prop="checkInTime" label="签到时间" width="180" />
      <el-table-column prop="checkOutTime" label="签退时间" width="180" />
      <el-table-column prop="workHours" label="工时(小时)" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="编辑考勤" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker v-model="form.attendanceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="签到时间">
          <el-date-picker v-model="form.checkInTime" type="datetime" placeholder="选择签到时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="签退时间">
          <el-date-picker v-model="form.checkOutTime" type="datetime" placeholder="选择签退时间" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态">
            <el-option :value="0" label="正常" />
            <el-option :value="1" label="迟到" />
            <el-option :value="2" label="早退" />
            <el-option :value="3" label="缺勤" />
            <el-option :value="4" label="请假" />
            <el-option :value="5" label="出差" />
          </el-select>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { attendanceApi, type Attendance } from '@/api/attendance'
import { employeeApi, type Employee } from '@/api/employee'

const loading = ref(false)
const tableData = ref<Attendance[]>([])
const employees = ref<Employee[]>([])
const dialogVisible = ref(false)

const searchForm = reactive({
  employeeId: undefined as number | undefined,
  yearMonth: '',
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

const loadData = async () => {
  loading.value = true
  try {
    const res = await attendanceApi.page({
      employeeId: searchForm.employeeId,
      yearMonth: searchForm.yearMonth,
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
  pagination.page = 1
  loadData()
}

const handleCheckIn = async () => {
  if (!searchForm.employeeId) {
    ElMessage.warning('请先选择员工')
    return
  }
  await attendanceApi.checkIn(searchForm.employeeId)
  ElMessage.success('签到成功')
  loadData()
}

const handleCheckOut = async () => {
  if (!searchForm.employeeId) {
    ElMessage.warning('请先选择员工')
    return
  }
  await attendanceApi.checkOut(searchForm.employeeId)
  ElMessage.success('签退成功')
  loadData()
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

onMounted(() => {
  loadEmployees()
  loadData()
})
</script>

<style scoped>
.attendance-page {
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
