<template>
  <div class="org-setting-page ems-page">
    <div class="page-header">
      <div class="header-left">
        <div class="title-icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="title-text">
          <h1>组织设置</h1>
          <p class="subtitle">部门结构 · 岗位体系 · 职级序列</p>
        </div>
      </div>
      <div class="header-right">
        <el-button v-if="hasPermission('org:manage') && activeTab === 'department'" type="primary" :icon="Plus" @click="handleAddDept">
          新增部门
        </el-button>
        <el-button v-if="hasPermission('system:manage') && activeTab !== 'department'" type="primary" :icon="Plus" @click="handleAddDict">
          新增{{ dictTypeLabel }}
        </el-button>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card dept" @click="activeTab = 'department'">
        <div class="stat-left">
          <div class="stat-num">{{ totalDepartments }}</div>
          <div class="stat-label">部门总数</div>
        </div>
        <div class="stat-icon-wrap">
          <el-icon class="stat-icon"><OfficeBuilding /></el-icon>
        </div>
      </div>
      <div class="stat-card pos" @click="activeTab = 'position'">
        <div class="stat-left">
          <div class="stat-num">{{ positionCount }}</div>
          <div class="stat-label">岗位数</div>
        </div>
        <div class="stat-icon-wrap">
          <el-icon class="stat-icon"><User /></el-icon>
        </div>
      </div>
      <div class="stat-card rank" @click="activeTab = 'rank'">
        <div class="stat-left">
          <div class="stat-num">{{ rankCount }}</div>
          <div class="stat-label">职级数</div>
        </div>
        <div class="stat-icon-wrap">
          <el-icon class="stat-icon"><Medal /></el-icon>
        </div>
      </div>
      <div class="stat-card emp" @click="activeTab = 'department'">
        <div class="stat-left">
          <div class="stat-num">{{ totalEmployees }}</div>
          <div class="stat-label">在职员工</div>
        </div>
        <div class="stat-icon-wrap">
          <el-icon class="stat-icon"><UserFilled /></el-icon>
        </div>
      </div>
    </div>

    <div class="content-card">
      <el-tabs v-model="activeTab" class="org-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="部门结构" name="department">
          <div class="tab-toolbar">
            <el-radio-group v-model="viewMode" size="default">
              <el-radio-button value="tree">
                <el-icon><Guide /></el-icon>
                <span>树形视图</span>
              </el-radio-button>
              <el-radio-button value="grid">
                <el-icon><Grid /></el-icon>
                <span>网格视图</span>
              </el-radio-button>
              <el-radio-button value="list">
                <el-icon><List /></el-icon>
                <span>列表视图</span>
              </el-radio-button>
            </el-radio-group>
          </div>

          <div class="tree-view" v-show="viewMode === 'tree'">
            <div class="tree-scroll">
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
                @edit="handleEditDept"
                @delete="handleDeleteDept"
              />
            </div>
          </div>

          <div class="grid-view" v-show="viewMode === 'grid'">
            <div class="grid-wrapper">
              <div
                v-for="(dept, index) in flatDepartments"
                :key="dept.id"
                class="dept-card"
                :class="[`level-${dept.level}`]"
                :style="{ animationDelay: `${index * 40}ms` }"
                @click="handleNodeSelect(dept)"
              >
                <div class="dept-card-icon" :style="{ background: getDeptColor(dept.level) }">
                  <el-icon><OfficeBuilding /></el-icon>
                </div>
                <div class="dept-card-body">
                  <div class="dept-card-name">{{ dept.name }}</div>
                  <div class="dept-card-code ems-mono">{{ dept.code }}</div>
                  <div class="dept-card-stats">
                    <span><el-icon :size="12" /><User /> {{ getDepartmentEmployeeCount(dept) }} 人</span>
                    <span v-if="dept.children?.length">
                      <el-icon :size="12" /><FolderOpened /> {{ dept.children.length }} 子部门
                    </span>
                  </div>
                </div>
                <div class="dept-card-actions" v-if="hasPermission('org:manage')">
                  <el-button size="small" circle :icon="Edit" @click.stop="handleEditDept(dept)" />
                  <el-button size="small" circle :icon="Plus" @click.stop="handleAddChild(dept)" />
                </div>
              </div>
            </div>
          </div>

          <div class="list-view" v-show="viewMode === 'list'">
            <el-table :data="flatDepartments" v-loading="loading" class="dict-table">
              <el-table-column type="index" label="#" width="60" align="center" />
              <el-table-column label="部门名称" min-width="200">
                <template #default="{ row }">
                  <div class="dept-list-name">
                    <span class="level-badge" :style="{ background: getDeptColor(row.level) }">L{{ row.level }}</span>
                    <span class="name-text">{{ row.name }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="code" label="编码" min-width="140">
                <template #default="{ row }">
                  <span class="dict-code ems-mono">{{ row.code }}</span>
                </template>
              </el-table-column>
              <el-table-column label="负责人" min-width="120">
                <template #default="{ row }">{{ row.leaderName || '-' }}</template>
              </el-table-column>
              <el-table-column prop="employeeCount" label="人数" width="90" align="center">
                <template #default="{ row }">{{ getDepartmentEmployeeCount(row) }}</template>
              </el-table-column>
              <el-table-column prop="sort" label="排序" width="80" align="center">
                <template #default="{ row }"><span class="sort-value ems-mono">{{ row.sort }}</span></template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light" round>
                    {{ row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160" align="center" fixed="right" v-if="hasPermission('org:manage')">
                <template #default="{ row }">
                  <el-button size="small" type="primary" link :icon="Edit" @click="handleEditDept(row)">编辑</el-button>
                  <el-button size="small" type="danger" link :icon="Delete" @click="handleDeleteDept(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="岗位管理" name="position">
          <div class="dict-filter">
            <el-select v-model="positionDeptFilter" placeholder="按部门筛选" clearable style="width: 200px" @change="loadDictData">
              <el-option v-for="dept in departments" :key="dept.code" :label="dept.name" :value="dept.code" />
            </el-select>
          </div>
          <el-table :data="filteredPositions" v-loading="dictLoading" class="dict-table">
            <el-table-column type="index" label="#" width="60" align="center" />
            <el-table-column prop="name" label="岗位名称" min-width="180">
              <template #default="{ row }"><span class="dict-name">{{ row.name }}</span></template>
            </el-table-column>
            <el-table-column prop="code" label="编码" min-width="140">
              <template #default="{ row }"><span class="dict-code ems-mono">{{ row.code }}</span></template>
            </el-table-column>
            <el-table-column label="所属部门" min-width="160">
              <template #default="{ row }">
                <el-tag size="small" effect="light" type="primary">
                  {{ departmentNameMap[row.parentCode] || row.parentCode || '-' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="在岗人数" width="100" align="center">
              <template #default="{ row }">
                <span class="count-value ems-mono">{{ getPositionEmployeeCount(row) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="80" align="center">
              <template #default="{ row }"><span class="sort-value ems-mono">{{ row.sort }}</span></template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light" round>
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right" v-if="hasPermission('system:manage')">
              <template #default="{ row }">
                <el-button size="small" type="primary" link :icon="Edit" @click="handleEditDict(row)">编辑</el-button>
                <el-button size="small" type="danger" link :icon="Delete" @click="handleDeleteDict(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="职级管理" name="rank">
          <el-table :data="rankList" v-loading="dictLoading" class="dict-table">
            <el-table-column type="index" label="#" width="60" align="center" />
            <el-table-column prop="name" label="职级名称" min-width="180">
              <template #default="{ row }">
                <div class="rank-row">
                  <span class="rank-dot" :style="{ background: getRankColor(row.sort) }"></span>
                  <span class="dict-name">{{ row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="code" label="编码" min-width="140">
              <template #default="{ row }"><span class="dict-code ems-mono">{{ row.code }}</span></template>
            </el-table-column>
            <el-table-column label="人数" width="100" align="center">
              <template #default="{ row }">
                <span class="count-value ems-mono">{{ getRankEmployeeCount(row) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="80" align="center">
              <template #default="{ row }"><span class="sort-value ems-mono">{{ row.sort }}</span></template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light" round>
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center" fixed="right" v-if="hasPermission('system:manage')">
              <template #default="{ row }">
                <el-button size="small" type="primary" link :icon="Edit" @click="handleEditDict(row)">编辑</el-button>
                <el-button size="small" type="danger" link :icon="Delete" @click="handleDeleteDict(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="deptDialogVisible" :title="isEditDept ? '编辑部门' : '新增部门'" width="520px" class="org-dialog">
      <el-form :model="deptForm" :rules="deptRules" ref="deptFormRef" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="deptForm.name" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="deptForm.code" placeholder="请输入部门编码" clearable :disabled="isEditDept">
            <template #prepend>DEPT_</template>
          </el-input>
        </el-form-item>
        <el-form-item label="上级部门" prop="parentCode">
          <el-select v-model="deptForm.parentCode" placeholder="请选择上级部门" clearable filterable style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.code" :label="dept.name" :value="dept.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门负责人" prop="leaderId">
          <el-select v-model="deptForm.leaderId" placeholder="请选择部门负责人" clearable filterable style="width: 100%">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id">
              <div class="emp-option">
                <el-avatar :size="24" :src="emp.avatar">{{ emp.name?.charAt(0) }}</el-avatar>
                <span>{{ emp.name }}</span>
                <span class="emp-no">{{ emp.employeeNo }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="deptForm.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="部门职能" prop="description">
          <el-input v-model="deptForm.description" type="textarea" :rows="3" placeholder="请输入部门职能描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="deptForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deptDialogVisible = false">取消</el-button>
        <el-button type="primary" :icon="Check" @click="handleDeptSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dictDialogVisible" :title="isEditDict ? `编辑${dictTypeLabel}` : `新增${dictTypeLabel}`" width="480px">
      <el-form :model="dictForm" :rules="dictRules" ref="dictFormRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dictForm.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="dictForm.code" placeholder="请输入编码，留空则同名称" clearable />
        </el-form-item>
        <el-form-item v-if="activeTab === 'position'" label="所属部门" prop="parentCode">
          <el-select v-model="dictForm.parentCode" placeholder="请选择所属部门" clearable filterable style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.code" :label="dept.name" :value="dept.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dictForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dictForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictDialogVisible = false">取消</el-button>
        <el-button type="primary" :icon="Check" @click="handleDictSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Edit, Delete, Check, OfficeBuilding, UserFilled, User, Medal,
  Guide, Grid, List, FolderOpened
} from '@element-plus/icons-vue'
import { hasPermission } from '@/utils/permission'
import { departmentApi, type Department } from '@/api/department'
import { dictionaryApi, type Dictionary } from '@/api/dictionary'
import { employeeApi } from '@/api/employee'
import OrgTreeNode from '@/components/OrgTreeNode.vue'

const loading = ref(false)
const dictLoading = ref(false)
const activeTab = ref('department')
const viewMode = ref<'tree' | 'grid' | 'list'>('tree')

const orgTreeData = ref<Department[]>([])
const departments = ref<Department[]>([])
const employees = ref<any[]>([])
const positionList = ref<Dictionary[]>([])
const rankList = ref<Dictionary[]>([])
const positionDeptFilter = ref('')

const deptDialogVisible = ref(false)
const isEditDept = ref(false)
const deptFormRef = ref<any>()
const deptForm = reactive<Department>({
  code: '', name: '', parentCode: '', leaderId: undefined, sort: 0, status: 1, description: '',
})
const deptRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
}

const dictDialogVisible = ref(false)
const isEditDict = ref(false)
const dictFormRef = ref<any>()
const dictForm = reactive<Dictionary>({
  type: 'position', name: '', code: '', parentCode: '', sort: 0, status: 1,
})
const dictRules = computed(() => ({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  parentCode: [{ required: activeTab.value === 'position', message: '请选择所属部门', trigger: 'change' }],
}))

const dictTypeLabel = computed(() => {
  const map: Record<string, string> = { position: '岗位', rank: '职级' }
  return map[activeTab.value] || ''
})

const departmentNameMap = computed(() =>
  Object.fromEntries(departments.value.map(d => [d.code, d.name]))
)

const filteredPositions = computed(() => {
  if (!positionDeptFilter.value) return positionList.value
  return positionList.value.filter(p => p.parentCode === positionDeptFilter.value)
})

const positionCount = computed(() => positionList.value.length)
const rankCount = computed(() => rankList.value.length)
const activeEmployees = computed(() => employees.value.filter(emp => emp.status === 1))

const totalDepartments = computed(() => {
  let count = 0
  const walk = (nodes: Department[]) => { nodes.forEach(n => { count++; if (n.children?.length) walk(n.children) }) }
  walk(orgTreeData.value)
  return count
})

const totalEmployees = computed(() => {
  return activeEmployees.value.length
})

const flatDepartments = computed(() => {
  const result: any[] = []
  const flatten = (nodes: Department[], level = 0) => {
    nodes.forEach(n => {
      result.push({ ...n, level })
      if (n.children?.length) flatten(n.children, level + 1)
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
]

const getDeptColor = (level: number = 0) => deptColors[level % deptColors.length]

const rankColors = ['#6366f1', '#8b5cf6', '#0ea5e9', '#10b981', '#f59e0b', '#f43f5e', '#14b8a6', '#ec4899']
const getRankColor = (sort: number = 0) => rankColors[sort % rankColors.length]

const getPositionEmployeeCount = (position: Dictionary) => {
  return activeEmployees.value.filter(emp =>
    emp.position === position.code || emp.position === position.name
  ).length
}

const getRankEmployeeCount = (rank: Dictionary) => {
  return activeEmployees.value.filter(emp =>
    emp.rank === rank.code || emp.rank === rank.name
  ).length
}

const getDepartmentEmployeeCount = (department: Department) => {
  const frontendCount = activeEmployees.value.filter(emp =>
    emp.department === department.code || emp.department === department.name
  ).length
  return frontendCount || department.employeeCount || 0
}

const loadDeptData = async () => {
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
  } finally {
    loading.value = false
  }
}

const loadDictData = async () => {
  dictLoading.value = true
  try {
    if (activeTab.value === 'position') {
      positionList.value = await dictionaryApi.listByType('position')
    } else if (activeTab.value === 'rank') {
      rankList.value = await dictionaryApi.listByType('rank')
    }
  } finally {
    dictLoading.value = false
  }
}

const loadOrgDictionaries = async () => {
  dictLoading.value = true
  try {
    const [positions, ranks] = await Promise.all([
      dictionaryApi.listByType('position'),
      dictionaryApi.listByType('rank'),
    ])
    positionList.value = positions
    rankList.value = ranks
  } finally {
    dictLoading.value = false
  }
}

const handleTabChange = () => {
  positionDeptFilter.value = ''
  if (activeTab.value !== 'department') {
    loadDictData()
  }
}

const handleNodeSelect = (node: Department) => {
  ElMessage.info(`选中部门: ${node.name}`)
}

const handleAddDept = () => {
  isEditDept.value = false
  resetDeptForm()
  deptDialogVisible.value = true
}

const handleAddChild = (parent: Department) => {
  isEditDept.value = false
  resetDeptForm()
  deptForm.parentCode = parent.code
  deptDialogVisible.value = true
}

const handleEditDept = (node: Department) => {
  isEditDept.value = true
  Object.assign(deptForm, node)
  deptDialogVisible.value = true
}

const handleDeleteDept = (node: Department) => {
  ElMessageBox.confirm(`确定删除部门"${node.name}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await departmentApi.delete(node.id!)
      ElMessage.success('删除成功')
      loadDeptData()
    })
    .catch(() => {})
}

const resetDeptForm = () => {
  Object.assign(deptForm, { code: '', name: '', parentCode: '', leaderId: undefined, sort: 0, status: 1, description: '' })
}

const handleDeptSubmit = async () => {
  const valid = await deptFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEditDept.value) {
      await departmentApi.update(deptForm.id!, deptForm)
      ElMessage.success('更新成功')
    } else {
      await departmentApi.create(deptForm)
      ElMessage.success('新增成功')
    }
    deptDialogVisible.value = false
    loadDeptData()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

const handleAddDict = () => {
  isEditDict.value = false
  resetDictForm()
  dictForm.type = activeTab.value as any
  dictDialogVisible.value = true
}

const handleEditDict = (row: Dictionary) => {
  isEditDict.value = true
  Object.assign(dictForm, row)
  dictDialogVisible.value = true
}

const handleDeleteDict = (row: Dictionary) => {
  ElMessageBox.confirm(`确定删除该${dictTypeLabel.value}吗？`, '提示', { type: 'warning' })
    .then(async () => {
      if (!row.id) return
      await dictionaryApi.delete(row.id)
      ElMessage.success('删除成功')
      loadDictData()
    })
    .catch(() => {})
}

const resetDictForm = () => {
  Object.assign(dictForm, { type: activeTab.value, name: '', code: '', parentCode: '', sort: 0, status: 1 })
}

const handleDictSubmit = async () => {
  const valid = await dictFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (!dictForm.code) dictForm.code = dictForm.name
    const payload: Dictionary = {
      type: dictForm.type, name: dictForm.name, code: dictForm.code, sort: dictForm.sort, status: dictForm.status,
    }
    if (dictForm.type === 'position') payload.parentCode = dictForm.parentCode
    if (isEditDict.value && dictForm.id) {
      await dictionaryApi.update(dictForm.id, payload)
    } else {
      await dictionaryApi.save(payload)
    }
    ElMessage.success(isEditDict.value ? '更新成功' : '新增成功')
    dictDialogVisible.value = false
    loadDictData()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

onMounted(() => {
  loadDeptData()
  loadOrgDictionaries()
})
</script>

<style scoped>
.org-setting-page {
  height: calc(100vh - 56px);
  overflow-y: auto;
  box-sizing: border-box;
  padding: 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background:
    radial-gradient(at 0% 0%, rgba(99, 102, 241, 0.06) 0px, transparent 50%),
    radial-gradient(at 100% 0%, rgba(139, 92, 246, 0.05) 0px, transparent 50%),
    var(--bg-page);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.title-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
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
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.28s var(--ease-out);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 3px;
  transition: width 0.28s var(--ease-out);
}

.stat-card.dept::before { background: linear-gradient(180deg, #6366f1, #8b5cf6); }
.stat-card.pos::before { background: linear-gradient(180deg, #10b981, #059669); }
.stat-card.rank::before { background: linear-gradient(180deg, #0ea5e9, #0284c7); }
.stat-card.emp::before { background: linear-gradient(180deg, #f59e0b, #d97706); }

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-card:hover::before {
  width: 5px;
}

.stat-left {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.stat-label {
  font-size: 12.5px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.stat-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
}

.stat-card.dept .stat-icon-wrap { background: linear-gradient(135deg, #6366f1, #8b5cf6); }
.stat-card.pos .stat-icon-wrap { background: linear-gradient(135deg, #10b981, #059669); }
.stat-card.rank .stat-icon-wrap { background: linear-gradient(135deg, #0ea5e9, #0284c7); }
.stat-card.emp .stat-icon-wrap { background: linear-gradient(135deg, #f59e0b, #d97706); }

.content-card {
  flex: 1;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.org-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.org-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-subtle);
}

.org-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.org-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
  font-weight: 500;
}

.org-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
  padding: 16px 20px;
}

.org-tabs :deep(.el-tab-pane) {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.tab-toolbar {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tab-toolbar :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
}

.tree-view {
  flex: 1;
  overflow: auto;
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-subtle);
}

.tree-scroll {
  padding: 20px;
  min-height: 100%;
}

.grid-view {
  flex: 1;
  overflow: auto;
}

.grid-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.dept-card {
  display: flex;
  gap: 14px;
  padding: 16px;
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.28s var(--ease-out);
  animation: fadeUp 0.4s var(--ease-out) both;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.dept-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--brand-300);
  background: var(--bg-elevated);
}

.dept-card-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}

.dept-card-body {
  flex: 1;
  min-width: 0;
}

.dept-card-name {
  font-size: 14.5px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.dept-card-code {
  font-size: 11.5px;
  color: var(--text-tertiary);
  margin-bottom: 8px;
}

.dept-card-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

.dept-card-stats span {
  display: flex;
  align-items: center;
  gap: 3px;
}

.dept-card-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.dept-card:hover .dept-card-actions {
  opacity: 1;
}

.list-view {
  flex: 1;
  overflow: hidden;
}

.dict-table {
  border-radius: var(--radius-md);
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-subtle);
}

.dict-table :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.dept-list-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.level-badge {
  width: 26px;
  height: 22px;
  border-radius: 6px;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.name-text {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.dict-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.dict-code {
  font-size: 12px;
  color: var(--brand-700);
  background: var(--brand-50);
  padding: 2px 8px;
  border-radius: 4px;
  letter-spacing: 0.3px;
  border: 1px solid var(--brand-100);
}

.sort-value {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-soft);
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid var(--border-subtle);
}

.rank-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rank-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.dict-filter {
  margin-bottom: 12px;
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

.org-dialog :deep(.el-dialog) {
  border-radius: var(--radius-xl) !important;
}

@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .grid-wrapper {
    grid-template-columns: 1fr;
  }
}

html.dark .dict-code {
  color: var(--brand-300);
  background: rgba(99, 102, 241, 0.15);
  border-color: rgba(99, 102, 241, 0.25);
}
</style>
