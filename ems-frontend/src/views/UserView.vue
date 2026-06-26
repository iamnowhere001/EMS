<template>
  <div class="user-page ems-page">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <PageHeader
            title="用户管理"
            subtitle="管理系统用户与角色"
            :icon="UserFilled"
          />
          <div class="header-actions">
            <el-button type="primary" size="large" :icon="Plus" @click="handleAdd">新增用户</el-button>
          </div>
        </div>
      </template>

      <div class="search-section">
        <el-form :inline="true" :model="query" class="search-form">
          <el-form-item label="用户名">
            <el-input v-model="query.username" placeholder="请输入用户名" clearable :prefix-icon="Search" @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="query.role" placeholder="全部" clearable style="width: 140px" @change="handleSearch">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="普通用户" value="USER" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px" @change="handleSearch">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <div class="search-actions">
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
          </div>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" class="user-table">
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
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" :icon="Key" :disabled="row.username === 'admin'" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button link type="danger" :icon="Delete" :disabled="row.username === 'admin'" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <div class="pagination-left">
          <span class="pagination-total">
            共 <em class="ems-mono">{{ total }}</em> 条记录
          </span>
        </div>
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          background
          layout="sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="460px"
      align-center
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="留空则使用默认密码 123456" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="USER">普通用户</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Plus, Search, RefreshRight, Edit, Delete, Key, UserFilled,
} from '@element-plus/icons-vue'
import { userApi, type UserItem } from '@/api/user'
import PageHeader from '@/components/PageHeader.vue'
import { formatDateTime } from '@/utils'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<UserItem[]>([])
const total = ref(0)

const query = reactive({
  username: '',
  role: '',
  status: undefined as number | undefined,
  page: 1,
  size: 10,
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<UserItem & { password?: string }>({
  username: '',
  nickname: '',
  role: 'USER',
  status: 1,
  password: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await userApi.page({
      page: query.page,
      size: query.size,
      username: query.username || undefined,
      role: query.role || undefined,
      status: query.status,
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '查询失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.page = 1
  fetchData()
}

const handleReset = () => {
  query.username = ''
  query.role = ''
  query.status = undefined
  query.page = 1
  fetchData()
}

const handleSizeChange = (val: number) => {
  query.size = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  query.page = val
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  form.username = ''
  form.nickname = ''
  form.role = 'USER'
  form.status = 1
  form.password = ''
  dialogVisible.value = true
}

const handleEdit = (row: UserItem) => {
  isEdit.value = true
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
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

const handleDelete = (row: UserItem) => {
  ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await userApi.delete(row.id!)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error: any) {
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => {})
}

const handleDialogClose = () => {
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await userApi.update(form.id!, {
        id: form.id,
        username: form.username,
        nickname: form.nickname,
        role: form.role,
        status: form.status,
      })
      ElMessage.success('更新成功')
    } else {
      await userApi.create({
        username: form.username,
        password: form.password,
        nickname: form.nickname,
        role: form.role,
        status: form.status,
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-page {
  height: calc(100vh - 56px);
  overflow-y: auto;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.main-card {
  border-radius: var(--radius-lg) !important;
  border: 1px solid var(--border-subtle) !important;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.main-card :deep(.el-card__header) {
  padding: 14px 20px;
}

.main-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 16px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.user-table {
  border-radius: var(--radius-md);
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-subtle);
}

.user-table :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.user-table :deep(.el-table__fixed-right) {
  background: var(--bg-soft);
  z-index: 10;
  left: auto;
  right: 0;
  border-left: 1px solid var(--border-subtle);
}

.user-table :deep(.el-table__fixed-right-patch) {
  background: var(--bg-soft);
}

.user-table :deep(.el-table__fixed-right::before) {
  content: '';
  position: absolute;
  left: -1px;
  top: 0;
  bottom: 0;
  width: 1px;
  background: var(--border-subtle);
  z-index: 1;
}

.username-text {
  font-weight: 600;
  color: var(--text-primary);
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
