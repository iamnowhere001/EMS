<template>
  <div v-resize-column-directive class="table-resize-wrapper">
    <el-table
      ref="tableRef"
      :data="tableData"
      v-loading="loading"
      class="employee-table"
      height="100%"
      :border="true"
      :row-class-name="(row: Employee) => isHighlighted(row.id) ? 'employee-row highlight-row' : 'employee-row'"
      @selection-change="handleSelectionChange"
      @header-dragend="handleHeaderDragend"
      @sort-change="handleSortChange"
    >
      <el-table-column v-if="columnVisible.drag" column-key="drag" label="" :width="columnWidth.drag" align="center" class-name="drag-handle">
        <template #default>
          <el-icon class="drag-icon"><Rank /></el-icon>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.selection" column-key="selection" type="selection" :width="columnWidth.selection" align="center" />
      <el-table-column v-if="columnVisible.index" column-key="index" type="index" label="#" :width="columnWidth.index" align="center" />
      <el-table-column v-if="columnVisible.info" column-key="info" prop="name" label="员工信息" :min-width="columnWidth.info || 180" align="left">
        <template #default="{ row }">
          <div class="employee-info-cell">
            <el-avatar
              :size="30"
              :style="{ backgroundColor: getAvatarColor(row.name, row.gender) }"
              class="employee-avatar"
            >
              {{ row.name?.slice(0, 1) }}
            </el-avatar>
            <div class="employee-details">
              <span class="employee-name">{{ row.name }}</span>
              <span class="employee-dept-pos">
                {{ formatEmpty(departmentNameMap[row.department || ''] || row.department) }} ·
                {{ formatEmpty(positionNameMap[row.position || ''] || row.position) }}
              </span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.employeeNo" column-key="employeeNo" prop="employeeNo" label="工号" :width="columnWidth.employeeNo || 100" align="center">
        <template #default="{ row }">
          <span class="employee-no">{{ formatEmpty(row.employeeNo) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.department" column-key="department" prop="department" label="部门" :width="columnWidth.department || 110" align="center">
        <template #default="{ row }">
          <el-tag size="small" effect="plain" round>{{ formatEmpty(departmentNameMap[row.department || ''] || row.department) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.position" column-key="position" prop="position" label="岗位" :width="columnWidth.position || 110" align="center">
        <template #default="{ row }">
          <span class="position-cell">{{ formatEmpty(positionNameMap[row.position || ''] || row.position) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.age" column-key="age" prop="age" label="年龄" :width="columnWidth.age" align="center" sortable="custom">
        <template #default="{ row }">
          {{ formatEmpty(row.age) }}
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.gender" column-key="gender" prop="gender" label="性别" :width="columnWidth.gender" align="center">
        <template #default="{ row }">
          <el-tag :type="row.gender === 1 ? 'primary' : 'danger'" size="small" effect="light" round>
            <el-icon class="gender-icon"><Male v-if="row.gender === 1" /><Female v-else /></el-icon>
            {{ row.gender === 1 ? '男' : '女' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        v-if="columnVisible.rank"
        column-key="rank"
        prop="rank"
        label="职级"
        :width="columnWidth.rank"
        align="center"
        :filters="rankFilters"
        :filter-method="rankFilterMethod"
        filter-placement="bottom-start"
      >
        <template #default="{ row }">
          <el-tag v-if="row.rank" size="small" effect="dark" round>{{ row.rank }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.contact" column-key="contact" prop="phone" label="联系方式" :min-width="columnWidth.contact || 180" align="left">
        <template #default="{ row }">
          <div class="contact-cell">
            <div class="contact-item"><el-icon><Phone /></el-icon> {{ desensitizePhone(row.phone) }}</div>
            <div class="contact-item"><el-icon><Message /></el-icon> {{ desensitizeEmail(row.email) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.salary" column-key="salary" prop="salary" label="薪资" :width="columnWidth.salary" align="right" sortable="custom">
        <template #default="{ row }">
          <span class="salary">{{ row.salary || row.salary === 0 ? '¥ ' + formatSalary(row.salary) : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.hireDate" column-key="hireDate" prop="hireDate" label="入职日期" :width="columnWidth.hireDate" align="center" sortable="custom">
        <template #default="{ row }">
          {{ formatDate(row.hireDate) }}
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.workYears" column-key="workYears" prop="workYears" label="工龄" :width="columnWidth.workYears" align="center" sortable="custom">
        <template #default="{ row }">
          {{ formatWorkYears(row.hireDate) }}
        </template>
      </el-table-column>
      <el-table-column
        v-if="columnVisible.status"
        column-key="status"
        prop="status"
        label="状态"
        :width="columnWidth.status"
        align="center"
        :filters="statusFilters"
        :filter-method="statusFilterMethod"
        filter-placement="bottom-start"
      >
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="dark" size="small" round>
            {{ row.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="columnVisible.operation" column-key="operation" label="操作" :width="columnWidth.operation || 180" fixed="right" align="center">
        <template #default="{ row }">
          <div class="operation-buttons">
            <el-tooltip content="查看详情" placement="top">
              <el-button link type="primary" :icon="View" @click="$emit('view', row)">查看</el-button>
            </el-tooltip>
            <el-tooltip content="编辑" placement="top">
              <el-button link type="primary" :icon="Edit" @click="$emit('edit', row)">编辑</el-button>
            </el-tooltip>
            <el-dropdown v-if="isAdmin() && row.status === 1" trigger="click" size="small" @command="(c: string) => $emit('workflow', { type: c, employee: row })">
              <el-button link type="warning" :icon="More">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="TRANSFER">
                    <el-icon><Refresh /></el-icon> 调岗
                  </el-dropdown-item>
                  <el-dropdown-item command="ADJUST_SALARY">
                    <el-icon><Money /></el-icon> 调薪
                  </el-dropdown-item>
                  <el-dropdown-item command="CONFIRM">
                    <el-icon><CircleCheckFilled /></el-icon> 转正
                  </el-dropdown-item>
                  <el-dropdown-item command="LEAVE" divided>
                    <el-icon><SwitchButton /></el-icon> 离职
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-tooltip v-else-if="isAdmin()" content="删除" placement="top">
              <el-button link type="danger" :icon="Delete" @click="$emit('delete', row)" />
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import Sortable from 'sortablejs'
import {
  View,
  Edit,
  Delete,
  Phone,
  Message,
  Male,
  Female,
  Rank,
  More,
  Refresh,
  Money,
  CircleCheckFilled,
  SwitchButton,
} from '@element-plus/icons-vue'
import { type Employee } from '@/api/employee'
import { type Dictionary } from '@/api/dictionary'
import { vResizeColumn } from '@/directives/resizeColumn'
import { isAdmin } from '@/utils/auth'
import { columnLabelMap, columnVisible, columnWidth, saveColumnVisible, saveColumnWidth, loadColumnSettings, isHighlighted, highlightRow } from '@/utils/tableConfig'
import { getAvatarColor, formatSalary, formatEmpty, formatWorkYears, formatDate, desensitizePhone, desensitizeEmail } from '@/utils/common'

const vResizeColumnDirective = vResizeColumn

const props = defineProps<{
  tableData: Employee[]
  loading: boolean
  departmentOptions: Dictionary[]
  positionOptionsAll: Dictionary[]
  rankOptions?: Dictionary[]
}>()

const emit = defineEmits<{
  (e: 'view', row: Employee): void
  (e: 'edit', row: Employee): void
  (e: 'delete', row: Employee): void
  (e: 'selection-change', ids: number[]): void
  (e: 'sort-change', params: { prop?: string; order?: string }): void
  (e: 'sort', ids: number[]): void
  (e: 'workflow', payload: { type: string; employee: Employee }): void
}>()

const tableRef = ref<any>(null)
const sortableInstance = ref<Sortable | null>(null)

const departmentNameMap = computed(() => {
  return Object.fromEntries(props.departmentOptions.map((item) => [item.code, item.name]))
})

const positionNameMap = computed(() => {
  return Object.fromEntries(props.positionOptionsAll.map((item) => [item.code, item.name]))
})

// 职级筛选
const rankFilters = computed(() => {
  return (props.rankOptions || []).map((item) => ({ text: item.name, value: item.code }))
})

const rankFilterMethod = (value: string, row: Employee) => {
  return row.rank === value
}

// 状态筛选
const statusFilters = [
  { text: '在职', value: 1 },
  { text: '离职', value: 0 },
]

const statusFilterMethod = (value: number, row: Employee) => {
  return row.status === value
}



const handleHeaderDragend = (newWidth: number, _oldWidth: number, column: any) => {
  const key = column.columnKey
  if (key) {
    columnWidth[key] = newWidth
    saveColumnWidth()
  }
}

const handleSortChange = ({ prop, order }: { prop?: string; order?: string }) => {
  emit('sort-change', { prop, order })
}

const initSortable = () => {
  nextTick(() => {
    const tbody = tableRef.value?.$el.querySelector('.el-table__body-wrapper tbody')
    if (!tbody || sortableInstance.value) return
    sortableInstance.value = Sortable.create(tbody, {
      handle: '.drag-handle',
      animation: 150,
      onEnd: async (evt: any) => {
        const oldIndex = evt.oldIndex
        const newIndex = evt.newIndex
        if (oldIndex === newIndex) return
        const newOrder = [...props.tableData]
        const [moved] = newOrder.splice(oldIndex, 1)
        if (!moved) return
        newOrder.splice(newIndex, 0, moved)
        const ids = newOrder.map((row) => row.id!).filter(Boolean)
        if (ids.length === 0) return
        emit('sort', ids)
      },
    })
  })
}

const destroySortable = () => {
  sortableInstance.value?.destroy()
  sortableInstance.value = null
}



const handleSelectionChange = (selection: Employee[]) => {
  const ids = selection.map((item) => item.id!).filter(Boolean)
  emit('selection-change', ids)
}

onMounted(() => {
  loadColumnSettings()
  initSortable()
})

onUnmounted(() => {
  destroySortable()
})
</script>

<style scoped>
.table-resize-wrapper {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.employee-table {
  border-radius: var(--radius-md);
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-subtle);
}

.employee-table :deep(.el-table__cell) {
  padding: 5px 0 !important;
}

.employee-table :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.employee-table :deep(.el-table__fixed-right) {
  background: var(--bg-soft);
  z-index: 10;
  left: auto;
  right: 0;
  border-left: 1px solid var(--border-subtle);
}

.employee-table :deep(.el-table__fixed-right-patch) {
  background: var(--bg-soft);
}

.employee-table :deep(.el-table__fixed-right::before) {
  content: '';
  position: absolute;
  left: -1px;
  top: 0;
  bottom: 0;
  width: 1px;
  background: var(--border-subtle);
  z-index: 1;
}

.employee-table :deep(.el-table__fixed::before) {
  background: var(--border-subtle);
  width: 1px;
  bottom: 0;
  height: 100%;
}

.employee-table :deep(.el-table--border::after),
.employee-table :deep(.el-table--group::after),
.employee-table :deep(.el-table::before) {
  background: var(--border-subtle);
  height: 1px;
}

.drag-handle {
  cursor: grab;
}
.drag-handle :deep(.drag-icon) {
  color: var(--text-tertiary);
  font-size: 14px;
  transition: color var(--dur-fast);
}
.drag-handle:hover :deep(.drag-icon) {
  color: var(--brand-500);
}

.position-cell {
  color: var(--text-regular);
  font-size: 13px;
  font-weight: 500;
}

.gender-icon {
  margin-right: 2px;
  font-size: 12px;
}

.employee-info-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.employee-avatar {
  font-weight: 600;
  box-shadow: 0 2px 6px -1px rgba(0, 0, 0, 0.08);
  border: 2px solid var(--bg-elevated);
  flex-shrink: 0;
  transition: transform 0.25s var(--ease-spring);
}
.employee-info-cell:hover .employee-avatar {
  transform: scale(1.08);
}

.employee-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.employee-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
  line-height: 1.2;
  letter-spacing: -0.01em;
}

.employee-dept-pos {
  font-size: 11px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.employee-no {
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--brand-700);
  background: var(--brand-50);
  padding: 1px 6px;
  border-radius: var(--radius-xs);
  font-weight: 500;
  letter-spacing: 0.3px;
  border: 1px solid var(--brand-100);
}

.contact-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--text-secondary);
}

.contact-item .el-icon {
  color: var(--text-tertiary);
  font-size: 12px;
}

.salary {
  color: var(--rose-600);
  font-weight: 700;
  font-size: 13px;
  font-family: var(--font-sans);
  letter-spacing: -0.01em;
}

.operation-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2px;
}

.operation-buttons :deep(.el-button) {
  padding: 4px 6px;
  font-size: 12px;
  font-weight: 500;
  border-radius: var(--radius-xs);
  transition: all var(--dur-fast);
}

.operation-buttons :deep(.el-button:hover) {
  transform: translateY(-1px);
}
</style>
