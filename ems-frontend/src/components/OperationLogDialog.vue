<template>
  <el-dialog
    v-model="dialogVisible"
    title="操作日志"
    width="720px"
    align-center
    class="log-dialog"
    @open="handleOpen"
  >
    <el-form :inline="true" :model="logQuery" class="log-search-form">
      <el-form-item label="模块">
        <el-select v-model="logQuery.module" placeholder="全部" clearable style="width: 120px">
          <el-option label="员工管理" value="员工管理" />
          <el-option label="字典管理" value="字典管理" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="logQuery.action" placeholder="全部" clearable style="width: 100px">
          <el-option label="新增" value="新增" />
          <el-option label="更新" value="更新" />
          <el-option label="删除" value="删除" />
          <el-option label="批量删除" value="批量删除" />
          <el-option label="排序" value="排序" />
          <el-option label="导入" value="导入" />
          <el-option label="导出" value="导出" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人">
        <el-input v-model="logQuery.operator" placeholder="请输入操作人" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="logQuery.startTime"
          type="datetime"
          placeholder="开始时间"
          style="width: 160px"
        />
        <span class="date-separator">-</span>
        <el-date-picker
          v-model="logQuery.endTime"
          type="datetime"
          placeholder="结束时间"
          style="width: 160px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
    <div v-loading="logLoading" class="log-timeline-wrapper">
      <el-timeline v-if="logData.length > 0">
        <el-timeline-item
          v-for="log in logData"
          :key="log.id"
          :type="getLogType(log.action)"
          :icon="getLogIcon(log.action)"
          :timestamp="formatRelativeTime(log.createTime)"
          placement="top"
        >
          <div class="log-card">
            <div class="log-card-header">
              <el-tag :type="getLogType(log.action)" effect="dark" size="small" round>
                {{ log.action }}
              </el-tag>
              <span class="log-operator">
                <el-icon><User /></el-icon>
                {{ log.operatorName }}
              </span>
            </div>
            <div class="log-card-content">{{ log.content }}</div>
            <div class="log-card-time" :title="formatDateTime(log.createTime)">
              <el-icon><Clock /></el-icon>
              {{ formatDateTime(log.createTime) }}
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无操作日志" :image-size="120" />
    </div>
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="logQuery.page"
        v-model:page-size="logQuery.size"
        :total="logTotal"
        :page-sizes="[10, 20, 50]"
        background
        layout="total, prev, pager, next"
        :prev-text="'上一页'"
        :next-text="'下一页'"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  RefreshRight,
  User,
  Clock,
  CirclePlus,
  EditPen,
  DeleteFilled,
  TopRight,
  BottomLeft,
} from '@element-plus/icons-vue'
import { operationLogApi, type OperationLog } from '@/api/operationLog'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const logLoading = ref(false)
const logData = ref<OperationLog[]>([])
const logTotal = ref(0)

const logQuery = reactive({
  page: 1,
  size: 10,
  module: '',
  action: '',
  operator: '',
  startTime: '',
  endTime: '',
})

const fetchLogs = async () => {
  logLoading.value = true
  try {
    const res = await operationLogApi.page({
      page: logQuery.page,
      size: logQuery.size,
      module: logQuery.module || undefined,
      action: logQuery.action || undefined,
      operator: logQuery.operator || undefined,
      startTime: logQuery.startTime || undefined,
      endTime: logQuery.endTime || undefined,
    })
    logData.value = res.records
    logTotal.value = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '查询日志失败')
  } finally {
    logLoading.value = false
  }
}

const handleOpen = () => {
  logQuery.page = 1
  fetchLogs()
}

const handleSearch = () => {
  logQuery.page = 1
  fetchLogs()
}

const handleReset = () => {
  logQuery.page = 1
  logQuery.size = 10
  logQuery.module = ''
  logQuery.action = ''
  logQuery.operator = ''
  logQuery.startTime = ''
  logQuery.endTime = ''
  fetchLogs()
}

const handlePageChange = (page: number) => {
  logQuery.page = page
  fetchLogs()
}

const handleSizeChange = (size: number) => {
  logQuery.size = size
  logQuery.page = 1
  fetchLogs()
}

const formatDateTime = (value?: string) => {
  if (!value) return '-'
  return value.replace('T', ' ')
}

const formatRelativeTime = (value?: string) => {
  if (!value) return '-'
  const time = new Date(value)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 30) return `${days} 天前`
  return formatDateTime(value)
}

const getLogType = (action?: string) => {
  switch (action) {
    case '新增':
      return 'success'
    case '更新':
      return 'primary'
    case '删除':
    case '批量删除':
      return 'danger'
    case '导入':
      return 'warning'
    case '导出':
      return 'info'
    default:
      return ''
  }
}

const getLogIcon = (action?: string) => {
  switch (action) {
    case '新增':
      return CirclePlus
    case '更新':
      return EditPen
    case '删除':
    case '批量删除':
      return DeleteFilled
    case '导入':
      return BottomLeft
    case '导出':
      return TopRight
    default:
      return Clock
  }
}
</script>

<style scoped>
.log-search-form {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.log-search-form .el-form-item {
  margin-bottom: 0;
  margin-right: 8px;
}

.date-separator {
  margin: 0 6px;
  color: #909399;
}

.log-timeline-wrapper {
  max-height: 520px;
  overflow-y: auto;
  padding: 8px 4px;
}

.log-card {
  background: #fff;
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  border: 1px solid #eef0f4;
  transition: transform 0.2s, box-shadow 0.2s;
}

.log-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
}

.log-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.log-operator {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.log-card-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 8px;
  padding: 8px 10px;
  background: #f8f9fb;
  border-radius: 6px;
}

.log-card-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.pagination-wrapper {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>
