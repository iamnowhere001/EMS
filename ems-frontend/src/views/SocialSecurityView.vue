<template>
  <div class="social-security-page ems-page">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <PageHeader
            title="社保公积金配置"
            subtitle="五险一金缴费基数与比例管理"
            :icon="Medal"
          />
          <div class="header-actions">
            <el-button type="primary" :icon="Edit" @click="handleEdit">编辑配置</el-button>
          </div>
        </div>
      </template>

      <div class="search-section">
        <el-form :inline="true" class="search-form">
          <el-form-item label="选择月份">
            <el-date-picker v-model="currentYearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" @change="loadData" />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="config" class="config-content">
        <div class="config-section">
          <div class="section-title">
            <el-icon class="section-icon"><Coin /></el-icon>
            <span>基本养老保险</span>
          </div>
          <div class="section-grid">
            <div class="config-item">
              <span class="item-label">缴费基数</span>
              <span class="item-value ems-mono">¥{{ (config.pensionBase || 0).toFixed(2) }}</span>
            </div>
            <div class="config-item">
              <span class="item-label">公司比例</span>
              <span class="item-value ems-mono">{{ config.pensionCompanyRate || 0 }}%</span>
            </div>
            <div class="config-item">
              <span class="item-label">个人比例</span>
              <span class="item-value ems-mono">{{ config.pensionPersonalRate || 0 }}%</span>
            </div>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon class="section-icon"><FirstAidKit /></el-icon>
            <span>基本医疗保险</span>
          </div>
          <div class="section-grid">
            <div class="config-item">
              <span class="item-label">缴费基数</span>
              <span class="item-value ems-mono">¥{{ (config.medicalBase || 0).toFixed(2) }}</span>
            </div>
            <div class="config-item">
              <span class="item-label">公司比例</span>
              <span class="item-value ems-mono">{{ config.medicalCompanyRate || 0 }}%</span>
            </div>
            <div class="config-item">
              <span class="item-label">个人比例</span>
              <span class="item-value ems-mono">{{ config.medicalPersonalRate || 0 }}%</span>
            </div>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon class="section-icon"><Briefcase /></el-icon>
            <span>失业保险</span>
          </div>
          <div class="section-grid">
            <div class="config-item">
              <span class="item-label">缴费基数</span>
              <span class="item-value ems-mono">¥{{ (config.unemploymentBase || 0).toFixed(2) }}</span>
            </div>
            <div class="config-item">
              <span class="item-label">公司比例</span>
              <span class="item-value ems-mono">{{ config.unemploymentCompanyRate || 0 }}%</span>
            </div>
            <div class="config-item">
              <span class="item-label">个人比例</span>
              <span class="item-value ems-mono">{{ config.unemploymentPersonalRate || 0 }}%</span>
            </div>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon class="section-icon"><Warning /></el-icon>
            <span>工伤保险</span>
          </div>
          <div class="section-grid">
            <div class="config-item">
              <span class="item-label">缴费基数</span>
              <span class="item-value ems-mono">¥{{ (config.injuryBase || 0).toFixed(2) }}</span>
            </div>
            <div class="config-item">
              <span class="item-label">公司比例</span>
              <span class="item-value ems-mono">{{ config.injuryCompanyRate || 0 }}%</span>
            </div>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon class="section-icon"><Cherry /></el-icon>
            <span>生育保险</span>
          </div>
          <div class="section-grid">
            <div class="config-item">
              <span class="item-label">缴费基数</span>
              <span class="item-value ems-mono">¥{{ (config.maternityBase || 0).toFixed(2) }}</span>
            </div>
            <div class="config-item">
              <span class="item-label">公司比例</span>
              <span class="item-value ems-mono">{{ config.maternityCompanyRate || 0 }}%</span>
            </div>
          </div>
        </div>

        <div class="config-section">
          <div class="section-title">
            <el-icon class="section-icon"><OfficeBuilding /></el-icon>
            <span>住房公积金</span>
          </div>
          <div class="section-grid">
            <div class="config-item">
              <span class="item-label">缴费基数</span>
              <span class="item-value ems-mono">¥{{ (config.housingFundBase || 0).toFixed(2) }}</span>
            </div>
            <div class="config-item">
              <span class="item-label">公司比例</span>
              <span class="item-value ems-mono">{{ config.housingFundCompanyRate || 0 }}%</span>
            </div>
            <div class="config-item">
              <span class="item-label">个人比例</span>
              <span class="item-value ems-mono">{{ config.housingFundPersonalRate || 0 }}%</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-empty description="暂无配置数据" :image-size="120">
          <el-button type="primary" :icon="Edit" @click="handleEdit">新增配置</el-button>
        </el-empty>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑社保公积金配置" width="680px">
      <el-form :model="form" label-width="130px">
        <el-divider content-position="left">养老保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="form.pensionBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.pensionCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.pensionPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">医疗保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="form.medicalBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.medicalCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.medicalPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">失业保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="form.unemploymentBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.unemploymentCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.unemploymentPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">工伤保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="form.injuryBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.injuryCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">生育保险</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="form.maternityBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.maternityCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>

        <el-divider content-position="left">住房公积金</el-divider>
        <el-form-item label="缴费基数">
          <el-input-number v-model="form.housingFundBase" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.housingFundCompanyRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.housingFundPersonalRate" :min="0" :max="100" :precision="2" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Medal, Edit, Coin, FirstAidKit, Briefcase, Warning, Cherry, OfficeBuilding,
} from '@element-plus/icons-vue'
import { socialSecurityApi, type SocialSecurityConfig } from '@/api/socialSecurity'
import PageHeader from '@/components/PageHeader.vue'

const config = ref<SocialSecurityConfig | null>(null)
const dialogVisible = ref(false)
const currentYearMonth = ref(new Date().toISOString().slice(0, 7))

const form = reactive<SocialSecurityConfig>({
  yearMonth: '',
  pensionBase: 0,
  pensionCompanyRate: 16,
  pensionPersonalRate: 8,
  medicalBase: 0,
  medicalCompanyRate: 9.5,
  medicalPersonalRate: 2,
  unemploymentBase: 0,
  unemploymentCompanyRate: 0.5,
  unemploymentPersonalRate: 0.5,
  injuryBase: 0,
  injuryCompanyRate: 0.4,
  maternityBase: 0,
  maternityCompanyRate: 0.8,
  housingFundBase: 0,
  housingFundCompanyRate: 12,
  housingFundPersonalRate: 12,
})

const loadData = async () => {
  if (!currentYearMonth.value) return
  try {
    const res = await socialSecurityApi.getByYearMonth(currentYearMonth.value)
    config.value = res
  } catch (e) {
    config.value = null
  }
}

const handleEdit = () => {
  if (config.value) {
    Object.assign(form, config.value)
  } else {
    form.yearMonth = currentYearMonth.value
    form.pensionBase = 0
    form.pensionCompanyRate = 16
    form.pensionPersonalRate = 8
    form.medicalBase = 0
    form.medicalCompanyRate = 9.5
    form.medicalPersonalRate = 2
    form.unemploymentBase = 0
    form.unemploymentCompanyRate = 0.5
    form.unemploymentPersonalRate = 0.5
    form.injuryBase = 0
    form.injuryCompanyRate = 0.4
    form.maternityBase = 0
    form.maternityCompanyRate = 0.8
    form.housingFundBase = 0
    form.housingFundCompanyRate = 12
    form.housingFundPersonalRate = 12
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  form.yearMonth = currentYearMonth.value
  await socialSecurityApi.saveOrUpdate(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.social-security-page {
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

.card-header .header-actions {
  display: flex;
  gap: 8px;
}

.search-section {
  background: var(--bg-soft);
  border-radius: var(--radius-md);
  padding: 12px 16px;
  margin-bottom: 16px;
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

.config-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.config-section {
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  padding: 14px 18px;
  transition: all 0.2s ease;
}

.config-section:hover {
  border-color: var(--brand-200);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.08);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--border-default);
}

.section-icon {
  font-size: 18px;
  color: var(--brand-500);
}

.section-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-label {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.item-value {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.empty-state {
  padding: 48px 0;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
