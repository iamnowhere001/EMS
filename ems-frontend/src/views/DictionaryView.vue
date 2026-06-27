<template>
  <div class="dictionary-page ems-page">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <PageHeader
            title="组织字典"
            subtitle="维护部门、岗位、职级等组织基础数据"
            :icon="OfficeBuilding"
          />
          <div class="header-actions">
            <el-button v-if="isAdmin()" type="primary" :icon="Plus" @click="handleAdd">新增字典</el-button>
          </div>
        </div>
      </template>

      <div class="filter-section">
        <el-radio-group v-model="currentType" @change="handleTypeChange" size="default">
          <el-radio-button label="department">
            <el-icon style="margin-right: 4px;"><OfficeBuilding /></el-icon>
            部门
          </el-radio-button>
          <el-radio-button label="position">
            <el-icon style="margin-right: 4px;"><User /></el-icon>
            岗位
          </el-radio-button>
          <el-radio-button label="rank">
            <el-icon style="margin-right: 4px;"><Medal /></el-icon>
            职级
          </el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="tableData" v-loading="loading" class="dictionary-table">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="name" label="名称" min-width="180">
          <template #default="{ row }">
            <span class="dict-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="编码" min-width="140">
          <template #default="{ row }">
            <span class="dict-code ems-mono">{{ row.code }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="currentType === 'position'" label="所属部门" min-width="160">
          <template #default="{ row }">
            <el-tag size="small" effect="light" type="primary">
              {{ departmentNameMap[row.parentCode] || row.parentCode || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="90" align="center">
          <template #default="{ row }">
            <span class="sort-value ems-mono">{{ row.sort }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light" round>
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right" v-if="isAdmin()">
          <template #default="{ row }">
            <el-button size="small" type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无数据" :image-size="120">
          <el-button v-if="isAdmin()" type="primary" :icon="Plus" @click="handleAdd">新增字典</el-button>
        </el-empty>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字典' : '新增字典'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type" :disabled="isEdit">
            <el-radio label="department">部门</el-radio>
            <el-radio label="position">岗位</el-radio>
            <el-radio label="rank">职级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编码，留空则同名称" clearable />
        </el-form-item>
        <el-form-item v-if="form.type === 'position'" label="所属部门" prop="parentCode">
          <el-select v-model="form.parentCode" placeholder="请选择所属部门" clearable filterable style="width: 100%">
            <el-option
              v-for="dept in departments"
              :key="dept.code"
              :label="dept.name"
              :value="dept.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
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
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Check, OfficeBuilding, User, Medal } from '@element-plus/icons-vue'
import { dictionaryApi, type Dictionary } from '@/api/dictionary'
import { isAdmin } from '@/utils/auth'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref<Dictionary[]>([])
const currentType = ref('department')
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<any>(null)
const departments = ref<Dictionary[]>([])

const departmentNameMap = computed(() => {
  return Object.fromEntries(departments.value.map((d) => [d.code, d.name]))
})

const form = reactive<Dictionary>({
  type: 'department',
  name: '',
  code: '',
  parentCode: '',
  sort: 0,
  status: 1,
})

const rules = computed(() => {
  return {
    name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
    parentCode: [
      { required: form.type === 'position', message: '请选择所属部门', trigger: 'change' },
    ],
  }
})

const fetchData = async () => {
  loading.value = true
  try {
    const [data, deptRes] = await Promise.all([
      dictionaryApi.listByType(currentType.value),
      currentType.value === 'position'
        ? dictionaryApi.listByType('department')
        : Promise.resolve([]),
    ])
    tableData.value = data
    if (currentType.value === 'position') {
      departments.value = deptRes
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handleTypeChange = () => {
  fetchData()
  resetForm()
}

const resetForm = () => {
  form.type = currentType.value as any
  form.name = ''
  form.code = ''
  form.parentCode = ''
  form.sort = 0
  form.status = 1
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: Dictionary) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: Dictionary) => {
  ElMessageBox.confirm('确定删除该字典项吗？', '提示', { type: 'warning' })
    .then(async () => {
      if (!row.id) return
      await dictionaryApi.delete(row.id)
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (!form.code) {
      form.code = form.name
    }
    const payload: Dictionary = {
      type: form.type,
      name: form.name,
      code: form.code,
      sort: form.sort,
      status: form.status,
    }
    if (form.type === 'position') {
      payload.parentCode = form.parentCode
    }
    if (isEdit.value && form.id) {
      await dictionaryApi.update(form.id, payload)
    } else {
      await dictionaryApi.save(payload)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

watch(currentType, () => {
  resetForm()
})

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dictionary-page {
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
  align-items: center;
  justify-content: space-between;
}

.filter-section {
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 12px;
  border: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
  gap: 12px;
}

.dictionary-table {
  border-radius: var(--radius-md);
  overflow: hidden;
  flex: 1;
  min-height: 0;
  border: 1px solid var(--border-subtle);
}

.dictionary-table :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

.dictionary-table :deep(.el-table__fixed-right) {
  background: var(--bg-soft);
  z-index: 10;
  left: auto;
  right: 0;
  border-left: 1px solid var(--border-subtle);
}

.dictionary-table :deep(.el-table__fixed-right-patch) {
  background: var(--bg-soft);
}

.dictionary-table :deep(.el-table__fixed-right::before) {
  content: '';
  position: absolute;
  left: -1px;
  top: 0;
  bottom: 0;
  width: 1px;
  background: var(--border-subtle);
  z-index: 1;
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
  background: var(--bg-elevated);
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid var(--border-subtle);
}

.empty-state {
  padding: 48px 0;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
