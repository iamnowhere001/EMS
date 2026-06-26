<template>
  <div class="workflow-page">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <PageHeader
            title="入转调离流程"
            subtitle="员工调岗 / 调薪 / 转正 / 离职 申请与历史台账"
            :icon="Document"
          />
          <div class="header-actions">
            <el-radio-group v-model="filterType" size="default" @change="handleSearch">
              <el-radio-button value="">全部</el-radio-button>
              <el-radio-button value="TRANSFER">调岗</el-radio-button>
              <el-radio-button value="ADJUST_SALARY">调薪</el-radio-button>
              <el-radio-button value="CONFIRM">转正</el-radio-button>
              <el-radio-button value="LEAVE">离职</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div class="search-section">
        <el-form :inline="true" :model="query" class="search-form">
          <el-form-item label="关键字">
            <el-input v-model="query.keyword" placeholder="变更摘要 / 申请人" clearable style="width: 220px" @keyup.enter="handleSearch">
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </el-form-item>
          <div class="search-actions">
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
          </div>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" class="workflow-table">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.changeType)" effect="light" size="small" round>
              {{ typeLabel(row.changeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="员工" width="180">
          <template #default="{ row }">
            <div class="employee-cell">
              <span class="emp-name">{{ row.employeeName || `员工#${row.employeeId}` }}</span>
              <span class="emp-no" v-if="row.employeeNo">{{ row.employeeNo }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="changeSummary" label="变更摘要" min-width="350" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="summary-cell">{{ row.changeSummary }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="effectiveDate" label="生效日期" width="120" align="center" sortable>
          <template #default="{ row }">
            {{ formatDate(row.effectiveDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="applicant" label="申请人" width="100" align="center" />
        <el-table-column prop="approver" label="审批人" width="100" align="center" />
        <el-table-column prop="approveDate" label="审批日期" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.approveDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="isAdmin() && row.status === 1" type="danger" link :icon="RefreshLeft" @click="handleRevoke(row)">撤销</el-button>
            <el-tag v-else-if="row.status === 0" type="info" size="small" effect="plain">已撤销</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="tableData.length === 0 && !loading" description="暂无变更记录" :image-size="120" />

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :prev-text="'上一页'"
          :next-text="'下一页'"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Search, RefreshRight, RefreshLeft } from '@element-plus/icons-vue'
import { workflowApi, type WorkflowChange } from '@/api/workflow'
import { isAdmin } from '@/utils/auth'
import PageHeader from '@/components/PageHeader.vue'
import { formatDate } from '@/utils'

const filterType = ref<string>('')
const query = reactive({ keyword: '', page: 1, size: 10 })
const tableData = ref<WorkflowChange[]>([])
const total = ref(0)
const loading = ref(false)

const typeLabel = (t?: string) => {
  switch (t) {
    case 'TRANSFER': return '调岗'
    case 'ADJUST_SALARY': return '调薪'
    case 'CONFIRM': return '转正'
    case 'LEAVE': return '离职'
    default: return t || '-'
  }
}
const typeTag = (t?: string): 'success' | 'warning' | 'info' | 'danger' | 'primary' => {
  switch (t) {
    case 'TRANSFER': return 'primary'
    case 'ADJUST_SALARY': return 'success'
    case 'CONFIRM': return 'warning'
    case 'LEAVE': return 'danger'
    default: return 'info'
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await workflowApi.page({
      page: query.page,
      size: query.size,
      changeType: filterType.value || undefined,
      keyword: query.keyword || undefined,
    })
    tableData.value = res.records
    total.value = res.total
  } catch (e: any) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { query.page = 1; fetchData() }
const handleReset = () => { query.keyword = ''; filterType.value = ''; handleSearch() }
const handleSizeChange = () => fetchData()
const handleCurrentChange = () => fetchData()

const handleRevoke = async (row: WorkflowChange) => {
  try {
    await ElMessageBox.confirm('确定撤销此变更记录？', '提示', { type: 'warning' })
    await workflowApi.revoke(row.id!)
    ElMessage.success('已撤销')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('撤销失败')
  }
}

watch(filterType, () => handleSearch())
onMounted(fetchData)
</script>

<style scoped>
.workflow-page {
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  height: calc(100vh - 60px);
  overflow-y: auto;
  box-sizing: border-box;
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

.search-form {
  display: flex;
  align-items: center;
  gap: 12px;
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

.workflow-table {
  border-radius: 8px;
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid #f1f5f9;
}

.workflow-table :deep(.el-table__header-wrapper th.el-table__cell) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  font-size: 13px;
  height: 40px;
  border-bottom: 1px solid #f1f5f9;
}

.workflow-table :deep(.el-table__row) {
  height: 52px;
}

.employee-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.emp-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 13px;
}

.emp-no {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  color: #3b82f6;
  background: #eff6ff;
  padding: 0 6px;
  border-radius: 4px;
  width: fit-content;
}

.summary-cell {
  color: #475569;
  font-size: 13px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
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

html.dark .workflow-table {
  border-color: #2a2a2c;
}

html.dark .workflow-table :deep(.el-table__header-wrapper th.el-table__cell) {
  background: #141415;
  border-bottom-color: #2a2a2c;
}

html.dark .emp-name {
  color: #e2e8f0;
}

html.dark .emp-no {
  background: rgba(59, 130, 246, 0.1);
}
</style>
