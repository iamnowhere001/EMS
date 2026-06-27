<template>
  <div class="system-page ems-page">
    <div class="page-header">
      <div class="header-left">
        <div class="title-icon">
          <el-icon><Setting /></el-icon>
        </div>
        <div class="title-text">
          <h1>系统设置</h1>
          <p class="subtitle">用户管理 · 操作日志</p>
        </div>
      </div>
      <div class="header-right">
        <template v-if="activeTab === 'users'">
          <el-button type="primary" :icon="Plus" @click="handleAddUser">新增用户</el-button>
        </template>
        <template v-else>
          <el-button :icon="RefreshRight" @click="loadLogData">刷新日志</el-button>
        </template>
      </div>
    </div>

    <div class="content-card">
      <el-tabs v-model="activeTab" class="system-tabs">
        <el-tab-pane label="用户管理" name="users">
          <div class="search-section">
            <el-form :inline="true" :model="userQuery" class="search-form">
              <el-form-item label="用户名">
                <el-input v-model="userQuery.username" placeholder="请输入用户名" clearable :prefix-icon="Search" @keyup.enter="loadUserData" />
              </el-form-item>
              <el-form-item label="角色">
                <el-select v-model="userQuery.role" placeholder="全部" clearable style="width: 140px" @change="loadUserData">
                  <el-option label="管理员" value="ADMIN" />
                  <el-option label="普通用户" value="USER" />
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="userQuery.status" placeholder="全部" clearable style="width: 120px" @change="loadUserData">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
              <div class="search-actions">
                <el-button type="primary" :icon="Search" @click="loadUserData">查询</el-button>
                <el-button :icon="RefreshRight" @click="resetUserSearch">重置</el-button>
              </div>
            </el-form>
          </div>

          <el-table :data="userTableData" v-loading="userLoading" class="user-table">
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="username" label="用户名" min-width="120">
              <template #default="{ row }">
                <span class="username-text">{{ row.username }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" min-width="120" />
            <el-table-column prop="role" label="角色" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" effect="light" size="small" round>
                  {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-switch
                  :model-value="row.status === 1"
                  :disabled="row.username === 'admin'"
                  size="small"
                  @change="(val: boolean) => handleStatusChange(row, val)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" align="center">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" :icon="Edit" @click="handleEditUser(row)">编辑</el-button>
                <el-button link type="warning" :icon="Key" :disabled="row.username === 'admin'" @click="handleResetPwd(row)">重置密码</el-button>
                <el-button link type="danger" :icon="Delete" :disabled="row.username === 'admin'" @click="handleDeleteUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <div class="pagination-left">
              <span class="pagination-total">
                共 <em class="ems-mono">{{ userTotal }}</em> 条记录
              </span>
            </div>
            <el-pagination
              v-model:current-page="userQuery.page"
              v-model:page-size="userQuery.size"
              :total="userTotal"
              :page-sizes="[10, 20, 50]"
              background
              layout="sizes, prev, pager, next, jumper"
              @size-change="loadUserData"
              @current-change="loadUserData"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="操作日志" name="logs">
          <div class="search-section">
            <el-form :inline="true" :model="logQuery" class="search-form">
              <el-form-item label="模块">
                <el-select v-model="logQuery.module" placeholder="全部模块" clearable style="width: 140px" @change="loadLogData">
                  <el-option label="员工管理" value="员工管理" />
                  <el-option label="薪资管理" value="薪资管理" />
                  <el-option label="用户管理" value="用户管理" />
                  <el-option label="系统设置" value="系统设置" />
                </el-select>
              </el-form-item>
              <el-form-item label="操作">
                <el-select v-model="logQuery.action" placeholder="全部操作" clearable style="width: 140px" @change="loadLogData">
                  <el-option label="新增" value="新增" />
                  <el-option label="修改" value="修改" />
                  <el-option label="删除" value="删除" />
                  <el-option label="登录" value="登录" />
                </el-select>
              </el-form-item>
              <el-form-item label="操作人">
                <el-input v-model="logQuery.operator" placeholder="操作人" clearable style="width: 120px" @keyup.enter="loadLogData" />
              </el-form-item>
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="logDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  style="width: 260px"
                  @change="loadLogData"
                />
              </el-form-item>
              <div class="search-actions">
                <el-button type="primary" :icon="Search" @click="loadLogData">查询</el-button>
                <el-button :icon="RefreshRight" @click="resetLogSearch">重置</el-button>
              </div>
            </el-form>
          </div>

          <el-table :data="logTableData" v-loading="logLoading" class="log-table">
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="module" label="模块" width="120" align="center">
              <template #default="{ row }">
                <el-tag type="primary" effect="plain" size="small">{{ row.module || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="action" label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getActionTagType(row.action)" effect="light" size="small" round>
                  {{ row.action || '-' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="操作内容" min-width="300" show-overflow-tooltip />
            <el-table-column prop="operatorName" label="操作人" width="120" align="center" />
            <el-table-column prop="ip" label="IP地址" width="140" align="center">
              <template #default="{ row }">
                <span class="ip-text ems-mono">{{ row.ip || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="操作时间" width="180" align="center">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>

          <div v-if="logTableData.length === 0 && !logLoading" class="empty-state">
            <el-empty description="暂无操作日志" :image-size="120" />
          </div>

          <div class="pagination-wrapper">
            <div class="pagination-left">
              <span class="pagination-total">
                共 <em class="ems-mono">{{ logTotal }}</em> 条记录
              </span>
            </div>
            <el-pagination
              v-model:current-page="logQuery.page"
              v-model:page-size="logQuery.size"
              :total="logTotal"
              :page-sizes="[10, 20, 50, 100]"
              background
              layout="sizes, prev, pager, next, jumper"
              @size-change="loadLogData"
              @current-change="loadLogData"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog
      v-model="userDialogVisible"
      :title="isEditUser ? '编辑用户' : '新增用户'"
      width="460px"
      align-center
      @close="handleUserDialogClose"
    >
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEditUser" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEditUser" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" show-password placeholder="留空则使用默认密码 123456" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="userForm.role">
            <el-radio value="USER">普通用户</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="userSubmitting" @click="handleUserSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Setting, Plus, Search, RefreshRight, Edit, Delete, Key,
} from '@element-plus/icons-vue'
import { userApi, type UserItem } from '@/api/user'
import { operationLogApi, type OperationLog } from '@/api/operationLog'
import { formatDateTime } from '@/utils'

const activeTab = ref('users')

const userLoading = ref(false)
const userTableData = ref<UserItem[]>([])
const userTotal = ref(0)
const userQuery = reactive({
  username: '',
  role: '',
  status: undefined as number | undefined,
  page: 1,
  size: 10,
})

const userDialogVisible = ref(false)
const isEditUser = ref(false)
const userSubmitting = ref(false)
const userFormRef = ref<FormInstance>()
const userForm = reactive<UserItem & { password?: string }>({
  username: '',
  nickname: '',
  role: 'USER',
  status: 1,
  password: '',
})

const userRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const logLoading = ref(false)
const logTableData = ref<OperationLog[]>([])
const logTotal = ref(0)
const logDateRange = ref<string[]>([])
const logQuery = reactive({
  module: '',
  action: '',
  operator: '',
  page: 1,
  size: 10,
})

const loadUserData = async () => {
  userLoading.value = true
  try {
    const res = await userApi.page({
      page: userQuery.page,
      size: userQuery.size,
      username: userQuery.username || undefined,
      role: userQuery.role || undefined,
      status: userQuery.status,
    })
    userTableData.value = res.records
    userTotal.value = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '查询失败')
  } finally {
    userLoading.value = false
  }
}

const resetUserSearch = () => {
  userQuery.username = ''
  userQuery.role = ''
  userQuery.status = undefined
  userQuery.page = 1
  loadUserData()
}

const handleAddUser = () => {
  isEditUser.value = false
  userForm.username = ''
  userForm.nickname = ''
  userForm.role = 'USER'
  userForm.status = 1
  userForm.password = ''
  userDialogVisible.value = true
}

const handleEditUser = (row: UserItem) => {
  isEditUser.value = true
  Object.assign(userForm, { ...row, password: '' })
  userDialogVisible.value = true
}

const handleStatusChange = async (row: UserItem, val: boolean) => {
  try {
    await userApi.update(row.id!, { ...row, status: val ? 1 : 0 })
    row.status = val ? 1 : 0
    ElMessage.success('状态已更新')
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  }
}

const handleResetPwd = (row: UserItem) => {
  ElMessageBox.confirm(`确定将用户 "${row.username}" 的密码重置为 123456 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await userApi.resetPassword(row.id!)
        ElMessage.success('密码已重置为 123456')
      } catch (error: any) {
        ElMessage.error(error.message || '重置失败')
      }
    })
    .catch(() => {})
}

const handleDeleteUser = (row: UserItem) => {
  ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await userApi.delete(row.id!)
        ElMessage.success('删除成功')
        loadUserData()
      } catch (error: any) {
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => {})
}

const handleUserDialogClose = () => {
  userFormRef.value?.clearValidate()
}

const handleUserSubmit = async () => {
  await userFormRef.value?.validate()
  userSubmitting.value = true
  try {
    if (isEditUser.value) {
      await userApi.update(userForm.id!, {
        id: userForm.id,
        username: userForm.username,
        nickname: userForm.nickname,
        role: userForm.role,
        status: userForm.status,
      })
      ElMessage.success('更新成功')
    } else {
      await userApi.create({
        username: userForm.username,
        password: userForm.password,
        nickname: userForm.nickname,
        role: userForm.role,
        status: userForm.status,
      })
      ElMessage.success('新增成功')
    }
    userDialogVisible.value = false
    loadUserData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    userSubmitting.value = false
  }
}

const loadLogData = async () => {
  logLoading.value = true
  try {
    const res = await operationLogApi.page({
      page: logQuery.page,
      size: logQuery.size,
      module: logQuery.module || undefined,
      action: logQuery.action || undefined,
      operator: logQuery.operator || undefined,
      startTime: logDateRange.value?.[0],
      endTime: logDateRange.value?.[1],
    })
    logTableData.value = res.records
    logTotal.value = res.total
  } catch (e: any) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    logLoading.value = false
  }
}

const resetLogSearch = () => {
  logQuery.module = ''
  logQuery.action = ''
  logQuery.operator = ''
  logDateRange.value = []
  logQuery.page = 1
  loadLogData()
}

const getActionTagType = (action?: string): 'success' | 'warning' | 'danger' | 'info' | 'primary' => {
  switch (action) {
    case '新增': return 'success'
    case '修改': return 'warning'
    case '删除': return 'danger'
    case '登录': return 'primary'
    default: return 'info'
  }
}

onMounted(() => {
  loadUserData()
  loadLogData()
})
</script>

<style scoped>
.system-page {
  height: calc(100vh - 56px);
  overflow-y: auto;
  box-sizing: border-box;
  padding: 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background:
    radial-gradient(at 0% 0%, rgba(139, 92, 246, 0.05) 0px, transparent 50%),
    radial-gradient(at 100% 0%, rgba(236, 72, 153, 0.05) 0px, transparent 50%),
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
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  box-shadow: 0 8px 20px -6px rgba(139, 92, 246, 0.5);
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

.header-right {
  display: flex;
  gap: 8px;
}

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

.system-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.system-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-subtle);
}

.system-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.system-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
  font-weight: 500;
}

.system-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
  padding: 16px 20px;
}

.system-tabs :deep(.el-tab-pane) {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.search-section {
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 12px;
  border: 1px solid var(--border-subtle);
}

.search-form {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
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

.user-table,
.log-table {
  border-radius: var(--radius-md);
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-subtle);
}

.user-table :deep(.el-table__body-wrapper),
.log-table :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.user-table :deep(.el-table__fixed-right),
.log-table :deep(.el-table__fixed-right) {
  background: var(--bg-soft);
  z-index: 10;
  left: auto;
  right: 0;
  border-left: 1px solid var(--border-subtle);
}

.username-text {
  font-weight: 600;
  color: var(--text-primary);
}

.ip-text {
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-state {
  padding: 32px 0;
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

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
