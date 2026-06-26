<template>
  <div class="dictionary-page ems-page">
    <el-card class="dictionary-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="title-section">
            <div class="title-icon"><el-icon><OfficeBuilding /></el-icon></div>
            <div>
              <div class="title">组织架构</div>
              <div class="subtitle">维护部门、岗位、职级等组织基础数据</div>
            </div>
          </div>
          <div class="header-actions">
            <el-button v-if="isAdmin()" type="primary" :icon="Plus" @click="handleAdd">新增组织</el-button>
          </div>
        </div>
      </template>

      <div class="search-bar">
        <el-radio-group v-model="currentType" @change="handleTypeChange">
          <el-radio-button label="department">部门</el-radio-button>
          <el-radio-button label="position">岗位</el-radio-button>
          <el-radio-button label="rank">职级</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="tableData" v-loading="loading" class="dictionary-table">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="code" label="编码" min-width="120" />
        <el-table-column v-if="currentType === 'position'" label="所属部门" min-width="140">
          <template #default="{ row }">
            <el-tag size="small" effect="light" type="info">
              <el-icon style="margin-right: 4px;"><OfficeBuilding /></el-icon>
              {{ departmentNameMap[row.parentCode] || row.parentCode || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="dark" round>
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="编辑" placement="top" v-if="isAdmin()">
              <el-button type="primary" size="small" circle :icon="Edit" @click="handleEdit(row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top" v-if="isAdmin()">
              <el-button type="danger" size="small" circle :icon="Delete" @click="handleDelete(row)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无组织数据" :image-size="120">
          <el-button v-if="isAdmin()" type="primary" :icon="Plus" @click="handleAdd">新增组织</el-button>
        </el-empty>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑组织' : '新增组织'" width="480px" align-center>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
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
          <el-input v-model="form.code" placeholder="请输入编码，默认同名称" clearable />
        </el-form-item>
        <el-form-item v-if="form.type === 'position'" label="所属部门" prop="parentCode">
          <el-select v-model="form.parentCode" placeholder="请选择所属部门" clearable filterable class="full-width">
            <el-option
              v-for="dept in departments"
              :key="dept.code"
              :label="dept.name"
              :value="dept.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" class="full-width" />
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
import { Plus, Edit, Delete, Check, OfficeBuilding } from '@element-plus/icons-vue'
import { dictionaryApi, type Dictionary } from '@/api/dictionary'
import { isAdmin } from '@/utils/auth'

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
  overflow: hidden;
  box-sizing: border-box;
}

.dictionary-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: var(--radius-lg) !important;
  border: 1px solid var(--border-subtle) !important;
}

.dictionary-card :deep(.el-card__header) {
  padding: 14px 20px;
}

.dictionary-card :deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 16px 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  width: 40px;
  height: 40px;
  border-radius: 11px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
  box-shadow: 0 6px 14px -4px rgba(99, 102, 241, 0.5);
  transition: transform 0.3s var(--ease-spring);
}
.title-icon:hover { transform: scale(1.06) rotate(-4deg); }

.title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.subtitle {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
  font-weight: 500;
}

.search-bar {
  margin-bottom: 12px;
  flex-shrink: 0;
}

.dictionary-table {
  flex: 1;
  overflow: hidden;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.full-width {
  width: 100%;
}
</style>
