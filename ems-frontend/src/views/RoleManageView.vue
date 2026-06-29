<template>
  <div class="role-page ems-page">
    <!-- 顶部 -->
    <div class="top-section">
      <div class="section-header">
        <div class="section-title">
          <span class="title-dot"></span>
          角色管理
        </div>
        <div class="section-actions">
          <el-button type="primary" :icon="Plus" @click="openSaveDialog()">新建角色</el-button>
        </div>
      </div>
    </div>

    <div class="role-overview">
      <div class="overview-card primary">
        <div class="overview-label">角色总数</div>
        <div class="overview-value ems-mono">{{ tableData.length }}</div>
        <div class="overview-desc">覆盖系统预设与自定义角色</div>
      </div>
      <div class="overview-card">
        <div class="overview-label">启用角色</div>
        <div class="overview-value ems-mono">{{ enabledRoleCount }}</div>
        <div class="overview-desc">可被账号正常分配使用</div>
      </div>
      <div class="overview-card">
        <div class="overview-label">已分配权限</div>
        <div class="overview-value ems-mono">{{ totalPermissionCount }}</div>
        <div class="overview-desc">各角色权限数量汇总</div>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-section">
      <el-table :data="tableData" v-loading="loading" class="role-table" stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="name" label="角色名称" min-width="120" />
        <el-table-column prop="code" label="角色编码" min-width="140">
          <template #default="{ row }">
            <el-tag effect="light" size="small" round>{{ row.code }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="权限数" width="90" align="center">
          <template #default="{ row }">
            <el-text class="ems-mono" type="primary">{{ row.permissionCount || 0 }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" round>
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="openPermDialog(row)">分配权限</el-button>
            <el-button size="small" text type="primary" @click="openSaveDialog(row)">编辑</el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建/编辑角色弹窗 -->
    <el-dialog
      v-model="saveDialogVisible"
      :title="editingRole?.id ? '编辑角色' : '新建角色'"
      width="480px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="saveForm" label-width="90px" label-position="left">
        <el-form-item label="角色名称" required>
          <el-input v-model="saveForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" required>
          <el-input v-model="saveForm.code" placeholder="请输入角色编码（大写英文字母）" :disabled="!!editingRole?.id" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="saveForm.description" type="textarea" :rows="2" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="saveForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog
      v-model="permDialogVisible"
      title="分配权限"
      width="520px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="perm-dialog-body" v-loading="permLoading">
        <div class="perm-role-name">角色：{{ currentRole?.name }}</div>
        <el-tree
          ref="permTreeRef"
          :data="permissionTree"
          show-checkbox
          node-key="id"
          :props="{ children: 'children', label: 'name' }"
          default-expand-all
          class="perm-tree"
        />
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permSaving" @click="handleSavePerm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { roleApi, permissionApi, type Role, type Permission } from '@/api/role'

const loading = ref(false)
const saving = ref(false)
const permLoading = ref(false)
const permSaving = ref(false)

const tableData = ref<Role[]>([])
const saveDialogVisible = ref(false)
const permDialogVisible = ref(false)
const editingRole = ref<Role | null>(null)
const currentRole = ref<Role | null>(null)

const saveForm = ref<Partial<Role>>({
  name: '',
  code: '',
  description: '',
  status: 1,
})

const permissionTree = ref<Permission[]>([])
const permTreeRef = ref<InstanceType<typeof ElTree> | null>(null)
const enabledRoleCount = computed(() => tableData.value.filter(role => role.status === 1).length)
const totalPermissionCount = computed(() => {
  return tableData.value.reduce((total, role) => total + (role.permissionCount || 0), 0)
})

async function loadData() {
  loading.value = true
  try {
    tableData.value = await roleApi.list()
  } catch (e: any) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function openSaveDialog(row?: Role) {
  editingRole.value = row || null
  saveForm.value = row
    ? { ...row }
    : { name: '', code: '', description: '', status: 1 }
  saveDialogVisible.value = true
}

async function handleSave() {
  if (!saveForm.value.name || !saveForm.value.code) {
    ElMessage.warning('请填写角色名称和编码')
    return
  }
  saving.value = true
  try {
    if (editingRole.value?.id) {
      await roleApi.update(saveForm.value)
    } else {
      await roleApi.save(saveForm.value)
    }
    ElMessage.success('保存成功')
    saveDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: Role) {
  await ElMessageBox.confirm(`确定删除角色「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await roleApi.delete(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  }
}

async function openPermDialog(row: Role) {
  currentRole.value = row
  permDialogVisible.value = true
  permLoading.value = true
  try {
    const [tree, assigned] = await Promise.all([
      permissionApi.tree(),
      roleApi.getPermissions(row.id!),
    ])
    permissionTree.value = tree
    await nextTick()
    // 勾选已分配的权限
    const assignedIds = (assigned || []).map(p => p.id!).filter(Boolean)
    permTreeRef.value?.setCheckedKeys(assignedIds, false)
  } catch (e: any) {
    ElMessage.error(e.message || '加载权限失败')
  } finally {
    permLoading.value = false
  }
}

async function handleSavePerm() {
  if (!currentRole.value) return
  permSaving.value = true
  try {
    const checked = permTreeRef.value?.getCheckedKeys() || []
    const halfChecked = permTreeRef.value?.getHalfCheckedKeys() || []
    const allChecked = [...checked, ...halfChecked] as number[]
    await roleApi.assignPermissions(currentRole.value.id!, allChecked)
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    permSaving.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.role-page {
  width: 100%;
  padding: 18px 20px 24px;
}
.top-section { margin-bottom: 14px; }
.section-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px; background: var(--bg-elevated); border-radius: 14px;
  border: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-sm);
}
.section-title { font-size: 16px; font-weight: 700; color: var(--text-primary); display: flex; align-items: center; gap: 8px; }
.title-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--brand-500); }
.role-overview {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}
.overview-card {
  position: relative;
  overflow: hidden;
  min-height: 112px;
  padding: 18px 20px;
  border-radius: 16px;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-sm);
}
.overview-card.primary {
  color: #fff;
  background: linear-gradient(135deg, var(--brand-500), var(--violet-500));
  border-color: transparent;
}
.overview-card.primary::after {
  content: '';
  position: absolute;
  right: -38px;
  bottom: -48px;
  width: 150px;
  height: 150px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
}
.overview-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}
.overview-card.primary .overview-label,
.overview-card.primary .overview-desc {
  color: rgba(255, 255, 255, 0.78);
}
.overview-value {
  margin-top: 10px;
  font-size: 30px;
  line-height: 1;
  font-weight: 800;
  color: var(--text-primary);
}
.overview-card.primary .overview-value {
  color: #fff;
}
.overview-desc {
  margin-top: 10px;
  font-size: 12px;
  color: var(--text-tertiary);
}
.table-section {
  background: var(--bg-elevated); border-radius: 14px; padding: 16px 18px;
  border: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-sm);
}
.role-table { width: 100%; }
.perm-dialog-body { max-height: 420px; overflow-y: auto; }
.perm-role-name { font-size: 14px; font-weight: 600; color: var(--text-secondary); margin-bottom: 12px; }
.perm-tree { font-size: 14px; }

@media (max-width: 900px) {
  .role-overview {
    grid-template-columns: 1fr;
  }
}
</style>
