<template>
  <div class="social-security-page">
    <div class="page-header">
      <h2>社保公积金配置</h2>
    </div>

    <div class="search-area">
      <el-form :inline="true">
        <el-form-item label="月份">
          <el-date-picker v-model="currentYearMonth" type="month" placeholder="选择月份" format="YYYY-MM" value-format="YYYY-MM" @change="loadData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleEdit">编辑配置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-descriptions :model="config" border column="2" v-if="config">
      <el-descriptions-item label="养老保险基数">{{ config.pensionBase }}</el-descriptions-item>
      <el-descriptions-item label="养老保险比例">公司 {{ config.pensionCompanyRate }}% / 个人 {{ config.pensionPersonalRate }}%</el-descriptions-item>
      
      <el-descriptions-item label="医疗保险基数">{{ config.medicalBase }}</el-descriptions-item>
      <el-descriptions-item label="医疗保险比例">公司 {{ config.medicalCompanyRate }}% / 个人 {{ config.medicalPersonalRate }}%</el-descriptions-item>
      
      <el-descriptions-item label="失业保险基数">{{ config.unemploymentBase }}</el-descriptions-item>
      <el-descriptions-item label="失业保险比例">公司 {{ config.unemploymentCompanyRate }}% / 个人 {{ config.unemploymentPersonalRate }}%</el-descriptions-item>
      
      <el-descriptions-item label="工伤保险基数">{{ config.injuryBase }}</el-descriptions-item>
      <el-descriptions-item label="工伤保险比例">公司 {{ config.injuryCompanyRate }}%</el-descriptions-item>
      
      <el-descriptions-item label="生育保险基数">{{ config.maternityBase }}</el-descriptions-item>
      <el-descriptions-item label="生育保险比例">公司 {{ config.maternityCompanyRate }}%</el-descriptions-item>
      
      <el-descriptions-item label="公积金基数">{{ config.housingFundBase }}</el-descriptions-item>
      <el-descriptions-item label="公积金比例">公司 {{ config.housingFundCompanyRate }}% / 个人 {{ config.housingFundPersonalRate }}%</el-descriptions-item>
    </el-descriptions>

    <el-empty v-else description="暂无配置数据" />

    <el-dialog v-model="dialogVisible" title="编辑社保公积金配置" width="700px">
      <el-form :model="form" label-width="140px">
        <el-divider content-position="left">养老保险</el-divider>
        <el-form-item label="基数">
          <el-input-number v-model="form.pensionBase" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.pensionCompanyRate" :min="0" :max="100" :precision="2" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.pensionPersonalRate" :min="0" :max="100" :precision="2" />
        </el-form-item>

        <el-divider content-position="left">医疗保险</el-divider>
        <el-form-item label="基数">
          <el-input-number v-model="form.medicalBase" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.medicalCompanyRate" :min="0" :max="100" :precision="2" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.medicalPersonalRate" :min="0" :max="100" :precision="2" />
        </el-form-item>

        <el-divider content-position="left">失业保险</el-divider>
        <el-form-item label="基数">
          <el-input-number v-model="form.unemploymentBase" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.unemploymentCompanyRate" :min="0" :max="100" :precision="2" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.unemploymentPersonalRate" :min="0" :max="100" :precision="2" />
        </el-form-item>

        <el-divider content-position="left">工伤保险</el-divider>
        <el-form-item label="基数">
          <el-input-number v-model="form.injuryBase" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.injuryCompanyRate" :min="0" :max="100" :precision="2" />
        </el-form-item>

        <el-divider content-position="left">生育保险</el-divider>
        <el-form-item label="基数">
          <el-input-number v-model="form.maternityBase" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.maternityCompanyRate" :min="0" :max="100" :precision="2" />
        </el-form-item>

        <el-divider content-position="left">住房公积金</el-divider>
        <el-form-item label="基数">
          <el-input-number v-model="form.housingFundBase" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="公司比例(%)">
          <el-input-number v-model="form.housingFundCompanyRate" :min="0" :max="100" :precision="2" />
        </el-form-item>
        <el-form-item label="个人比例(%)">
          <el-input-number v-model="form.housingFundPersonalRate" :min="0" :max="100" :precision="2" />
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
import { socialSecurityApi, type SocialSecurityConfig } from '@/api/socialSecurity'

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
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}
</style>
