<template>
  <div class="org-page ems-page" :class="{ 'dark-mode': isDark }">
    <div class="org-header">
      <div class="header-left">
        <div class="page-title">
          <div class="title-icon">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="title-text">
            <h1>组织架构</h1>
            <p class="subtitle">企业组织结构管理</p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <div class="view-toggle">
          <el-radio-group v-model="viewMode" size="default">
            <el-radio-button value="tree">
              <el-icon><Guide /></el-icon>
              <span>树形视图</span>
            </el-radio-button>
            <el-radio-button value="grid">
              <el-icon><Grid /></el-icon>
              <span>网格视图</span>
            </el-radio-button>
          </el-radio-group>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleAddDepartment" v-if="isAdmin()">
          新增部门
        </el-button>
      </div>
    </div>

    <div class="stats-bar">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ totalDepartments }}</span>
          <span class="stat-label">部门总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon employees">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ totalEmployees }}</span>
          <span class="stat-label">在职员工</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon levels">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ departmentLevels }}</span>
          <span class="stat-label">层级深度</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon leaders">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ leadershipCount }}</span>
          <span class="stat-label">管理人员</span>
        </div>
      </div>
    </div>

    <div class="org-tree-container" v-show="viewMode === 'tree'">
      <div class="tree-wrapper" ref="treeWrapperRef">
        <div class="tree-content">
          <OrgTreeNode
            v-for="(node, index) in orgTreeData"
            :key="node.id"
            :node="node"
            :level="0"
            :index="index"
            :total="orgTreeData.length"
            :default-expanded="true"
            @select="handleNodeSelect"
            @add="handleAddChild"
            @edit="handleEditNode"
            @delete="handleDeleteNode"
          />
        </div>
      </div>
    </div>

    <div class="org-grid-container" v-show="viewMode === 'grid'">
      <div class="grid-wrapper">
        <div
          v-for="(dept, index) in flatDepartments"
          :key="dept.id"
          class="department-card"
          :class="[`level-${dept.level}`, { 'has-children': dept.children?.length }]"
          :style="{ animationDelay: `${index * 50}ms` }"
          @click="handleNodeSelect(dept)"
        >
          <div class="card-header">
            <div class="dept-icon" :style="{ background: getDeptColor(dept.level) }">
              <el-icon><component :is="getDeptIcon(dept.level)" /></el-icon>
            </div>
            <div class="dept-level" v-if="dept.level > 0">
              <span>L{{ dept.level }}</span>
            </div>
          </div>
          <div class="card-body">
            <h3 class="dept-name">{{ dept.name }}</h3>
            <p class="dept-code">{{ dept.code }}</p>
            <div class="dept-stats">
              <span class="emp-count">
                <el-icon><User /></el-icon>
                {{ dept.employeeCount || 0 }} 人
              </span>
              <span class="sub-count" v-if="dept.children?.length">
                <el-icon><FolderOpened /></el-icon>
                {{ dept.children.length }} 子部门
              </span>
            </div>
          </div>
          <div class="card-actions" v-if="isAdmin()">
            <el-tooltip content="编辑" placement="top">
              <el-button size="small" circle :icon="Edit" @click.stop="handleEditNode(dept)" />
            </el-tooltip>
            <el-tooltip content="添加子部门" placement="top">
              <el-button size="small" circle :icon="Plus" @click.stop="handleAddChild(dept)" />
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑部门' : '新增部门'"
      width="520px"
      align-center
      class="org-dialog"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入部门编码" clearable :disabled="isEdit">
            <template #prepend>DEPT_</template>
          </el-input>
        </el-form-item>
        <el-form-item label="上级部门" prop="parentCode">
          <el-select v-model="form.parentCode" placeholder="请选择上级部门" clearable filterable class="full-width">
            <el-option
              v-for="dept in departments"
              :key="dept.code"
              :label="dept.name"
              :value="dept.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门负责人" prop="leaderId">
          <el-select v-model="form.leaderId" placeholder="请选择部门负责人" clearable filterable class="full-width">
            <el-option
              v-for="emp in employees"
              :key="emp.id"
              :label="emp.name"
              :value="emp.id"
            >
              <div class="emp-option">
                <el-avatar :size="24" :src="emp.avatar">
                  {{ emp.name?.charAt(0) }}
                </el-avatar>
                <span>{{ emp.name }}</span>
                <span class="emp-no">{{ emp.employeeNo }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" class="full-width" />
        </el-form-item>
        <el-form-item label="部门职能" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入部门职能描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :icon="Check" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Edit, Delete, Check, Connection, Guide, Grid,
  OfficeBuilding, UserFilled, TrendCharts, Star, User, FolderOpened
} from '@element-plus/icons-vue'
import { isAdmin } from '@/utils/auth'
import { departmentApi, type Department } from '@/api/department'
import { employeeApi } from '@/api/employee'
import { useTheme } from '@/composables/useTheme'

const { isDark } = useTheme()

const viewMode = ref<'tree' | 'grid'>('tree')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<any>()
const treeWrapperRef = ref<HTMLElement>()

const form = reactive<Department>({
  code: '',
  name: '',
  parentCode: '',
  leaderId: undefined,
  sort: 0,
  status: 1,
  description: '',
})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
}

const departments = ref<Department[]>([])
const employees = ref<any[]>([])
const orgTreeData = ref<Department[]>([])

const totalDepartments = computed(() => {
  let count = 0
  const countNodes = (nodes: Department[]) => {
    nodes.forEach(node => {
      count++
      if (node.children?.length) countNodes(node.children)
    })
  }
  countNodes(orgTreeData.value)
  return count
})

const totalEmployees = computed(() => {
  let count = 0
  const countNodes = (nodes: Department[]) => {
    nodes.forEach(node => {
      count += node.employeeCount || 0
      if (node.children?.length) countNodes(node.children)
    })
  }
  countNodes(orgTreeData.value)
  return count
})

const departmentLevels = computed(() => {
  let maxLevel = 0
  const findMaxLevel = (nodes: Department[], level = 0) => {
    nodes.forEach(node => {
      if (level > maxLevel) maxLevel = level
      if (node.children?.length) findMaxLevel(node.children, level + 1)
    })
  }
  findMaxLevel(orgTreeData.value)
  return maxLevel + 1
})

const leadershipCount = computed(() => {
  let count = 0
  const countNodes = (nodes: Department[]) => {
    nodes.forEach(node => {
      if (node.leaderId) count++
      if (node.children?.length) countNodes(node.children)
    })
  }
  countNodes(orgTreeData.value)
  return count
})

const flatDepartments = computed(() => {
  const result: any[] = []
  const flatten = (nodes: Department[], level = 0) => {
    nodes.forEach(node => {
      result.push({ ...node, level })
      if (node.children?.length) flatten(node.children, level + 1)
    })
  }
  flatten(orgTreeData.value)
  return result
})

const deptColors = [
  'linear-gradient(135deg, #6366f1, #8b5cf6)',
  'linear-gradient(135deg, #10b981, #059669)',
  'linear-gradient(135deg, #0ea5e9, #0284c7)',
  'linear-gradient(135deg, #f59e0b, #d97706)',
  'linear-gradient(135deg, #f43f5e, #e11d48)',
  'linear-gradient(135deg, #8b5cf6, #7c3aed)',
  'linear-gradient(135deg, #14b8a6, #0d9488)',
]

const getDeptColor = (level: number = 0) => {
  return deptColors[level % deptColors.length]
}

const getDeptIcon = (level: number) => {
  const icons = ['OfficeBuilding', 'Cpu', 'DataLine', 'Goods', 'Wallet', 'Edit', 'FolderOpened']
  return icons[level % icons.length]
}

const fetchData = async () => {
  loading.value = true
  try {
    const [treeRes, deptRes, empRes] = await Promise.all([
      departmentApi.tree(),
      departmentApi.list(),
      employeeApi.list(),
    ])
    orgTreeData.value = treeRes
    departments.value = deptRes
    employees.value = empRes
  } catch (error) {
    console.warn('数据加载失败', error)
    orgTreeData.value = []
  } finally {
    loading.value = false
  }
}

const handleNodeSelect = (node: Department) => {
  ElMessage.info(`选中部门: ${node.name}`)
}

const handleAddChild = (parent: Department) => {
  isEdit.value = false
  resetForm()
  form.parentCode = parent.code
  dialogVisible.value = true
}

const handleAddDepartment = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEditNode = (node: Department) => {
  isEdit.value = true
  Object.assign(form, node)
  dialogVisible.value = true
}

const handleDeleteNode = (node: Department) => {
  ElMessageBox.confirm(`确定删除部门"${node.name}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await departmentApi.delete(node.id!)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

const resetForm = () => {
  form.code = ''
  form.name = ''
  form.parentCode = ''
  form.leaderId = undefined
  form.sort = 0
  form.status = 1
  form.description = ''
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await departmentApi.update(form.id!, form)
      ElMessage.success('更新成功')
    } else {
      await departmentApi.create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.org-page {
  padding: 20px 24px 24px;
  min-height: calc(100vh - 56px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background:
    radial-gradient(at 0% 0%, rgba(99, 102, 241, 0.06) 0px, transparent 50%),
    radial-gradient(at 100% 0%, rgba(139, 92, 246, 0.05) 0px, transparent 50%),
    var(--bg-page);
}

.org-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--brand-500) 0%, var(--violet-500) 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  box-shadow: 0 8px 20px -6px rgba(99, 102, 241, 0.5);
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
  font-family: var(--font-sans);
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.view-toggle :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s var(--ease-out);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
}

.stat-icon.employees {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stat-icon.levels {
  background: linear-gradient(135deg, #0ea5e9, #0284c7);
}

.stat-icon.leaders {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: var(--font-sans);
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.stat-label {
  font-size: 12.5px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.org-tree-container {
  flex: 1;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  min-height: 400px;
}

.tree-wrapper {
  padding: 24px;
  overflow: auto;
  height: 100%;
}

.tree-content {
  display: flex;
  flex-direction: column;
  gap: 0;
  min-width: fit-content;
  min-height: 100%;
}

.org-grid-container {
  flex: 1;
  overflow: auto;
}

.grid-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 4px;
}

.department-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s var(--ease-out);
  position: relative;
  animation: cardFadeIn 0.5s var(--ease-out) both;
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.department-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--brand-300);
}

.department-card.level-0 {
  border-left: 4px solid var(--brand-500);
}

.department-card.level-1 {
  border-left: 4px solid var(--emerald-500);
}

.department-card.level-2 {
  border-left: 4px solid var(--sky-500);
}

.department-card.level-3 {
  border-left: 4px solid var(--amber-500);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.dept-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  box-shadow: 0 4px 12px -4px rgba(0, 0, 0, 0.15);
}

.dept-level span {
  font-size: 11px;
  font-weight: 600;
  padding: 4px 8px;
  background: var(--bg-soft);
  border-radius: var(--radius-full);
  color: var(--text-tertiary);
}

.card-body {
  margin-bottom: 12px;
}

.dept-name {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.dept-code {
  margin: 0 0 10px;
  font-size: 12px;
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

.dept-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.dept-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12.5px;
  color: var(--text-secondary);
  font-weight: 500;
}

.dept-stats .el-icon {
  font-size: 14px;
}

.card-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.department-card:hover .card-actions {
  opacity: 1;
}

.org-dialog :deep(.el-dialog) {
  border-radius: var(--radius-xl) !important;
}

.full-width {
  width: 100%;
}

.emp-option {
  display: flex;
  align-items: center;
  gap: 10px;
}

.emp-option .emp-no {
  margin-left: auto;
  font-size: 11px;
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

@media (max-width: 1200px) {
  .stats-bar {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .org-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .stats-bar {
    grid-template-columns: 1fr;
  }

  .grid-wrapper {
    grid-template-columns: 1fr;
  }
}

html.dark .stat-card {
  background: var(--bg-elevated);
  border-color: var(--border-default);
}

html.dark .department-card {
  background: var(--bg-elevated);
  border-color: var(--border-default);
}

html.dark .org-tree-container {
  background: var(--bg-elevated);
  border-color: var(--border-default);
}

html.dark .dept-stats span {
  color: var(--text-secondary);
}
</style>