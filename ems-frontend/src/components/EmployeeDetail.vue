<template>
  <el-drawer
    v-model="visible"
    :title="detail?.employee ? `${detail.employee.name} 的员工档案` : '员工档案'"
    size="580px"
    class="employee-detail-drawer"
  >
    <div v-if="detail?.employee" class="detail-content">
      <!-- 顶部信息卡 -->
      <div class="detail-hero">
        <div class="hero-bg"></div>
        <div class="hero-main">
          <el-avatar
            :size="68"
            :src="detail.employee.avatar"
            class="hero-avatar"
            :style="{ backgroundColor: getAvatarColor(detail.employee.name, detail.employee.gender) }"
          >
            {{ detail.employee.name?.charAt(0) }}
          </el-avatar>
          <div class="hero-info">
            <div class="hero-name-row">
              <span class="hero-name">{{ detail.employee.name }}</span>
              <el-tag :type="detail.employee.status === 1 ? 'success' : 'info'" effect="dark" size="small" round>
                {{ detail.employee.status === 1 ? '在职' : '离职' }}
              </el-tag>
            </div>
            <div class="hero-dept">
              {{ formatEmpty(deptName) }}
              <span v-if="detail.employee.position" class="hero-sep">·</span>
              {{ formatEmpty(posName) }}
              <span v-if="detail.employee.rank" class="hero-sep">·</span>
              {{ formatEmpty(detail.employee.rank) }}
            </div>
            <div class="hero-no ems-mono">工号 {{ formatEmpty(detail.employee.employeeNo) }}</div>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-value ems-mono">{{ formatWorkYears(detail.employee.hireDate) }}</div>
            <div class="stat-label">工龄</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value ems-mono">{{ formatEmpty(detail.employee.age) || '-' }}</div>
            <div class="stat-label">年龄</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value salary ems-mono">¥{{ formatSalary(detail.employee.salary) }}</div>
            <div class="stat-label">月薪</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value ems-mono">{{ detail.contracts?.length || 0 }}</div>
            <div class="stat-label">合同</div>
          </div>
        </div>
      </div>

      <!-- Tab 切换档案类别 -->
      <el-tabs v-model="activeTab" class="detail-tabs">
        <!-- 档案总览 -->
        <el-tab-pane label="档案总览" name="overview">
          <div class="section-block">
            <div class="section-header">
              <span class="section-icon work"></span>
              <span class="section-title">工作信息</span>
            </div>
            <div class="info-grid">
              <div class="info-item"><span class="info-label">部门</span><span class="info-value">{{ formatEmpty(deptName) }}</span></div>
              <div class="info-item"><span class="info-label">职位</span><span class="info-value">{{ formatEmpty(posName) }}</span></div>
              <div class="info-item"><span class="info-label">职级</span><span class="info-value">{{ formatEmpty(detail.employee.rank) }}</span></div>
              <div class="info-item"><span class="info-label">入职日期</span><span class="info-value">{{ formatEmpty(detail.employee.hireDate) }}</span></div>
            </div>
          </div>

          <div class="section-block">
            <div class="section-header">
              <span class="section-icon base"></span>
              <span class="section-title">基础资料</span>
            </div>
            <div class="info-grid">
              <div class="info-item"><span class="info-label">性别</span><span class="info-value">{{ detail.employee.gender === 1 ? '男' : '女' }}</span></div>
              <div class="info-item"><span class="info-label">年龄</span><span class="info-value">{{ formatEmpty(detail.employee.age) }}</span></div>
              <div class="info-item"><span class="info-label">民族</span><span class="info-value">{{ formatEmpty(detail.employee.nation) }}</span></div>
              <div class="info-item"><span class="info-label">籍贯</span><span class="info-value">{{ formatEmpty(detail.employee.nativePlace) }}</span></div>
              <div class="info-item"><span class="info-label">婚姻</span><span class="info-value">{{ maritalText }}</span></div>
              <div class="info-item"><span class="info-label">政治面貌</span><span class="info-value">{{ formatEmpty(detail.employee.politicalStatus) }}</span></div>
            </div>
          </div>

          <div class="section-block">
            <div class="section-header">
              <span class="section-icon contact"></span>
              <span class="section-title">联系方式</span>
            </div>
            <div class="info-list">
              <div class="info-row"><span class="info-label">手机号</span><span class="info-value">{{ formatEmpty(detail.employee.phone) }}</span></div>
              <div class="info-row"><span class="info-label">邮箱</span><span class="info-value">{{ formatEmpty(detail.employee.email) }}</span></div>
              <div class="info-row"><span class="info-label">身份证号</span><span class="info-value">{{ formatEmpty(detail.employee.idCard) }}</span></div>
              <div class="info-row"><span class="info-label">银行卡号</span><span class="info-value">{{ formatEmpty(detail.employee.bankCard) }}</span></div>
              <div class="info-row"><span class="info-label">紧急联系人</span><span class="info-value">{{ formatEmpty(detail.employee.emergencyContact) }} {{ detail.employee.emergencyPhone ? `(${formatEmpty(detail.employee.emergencyPhone)})` : '' }}</span></div>
            </div>
          </div>

          <div class="section-block">
            <div class="section-header">
              <span class="section-icon address"></span>
              <span class="section-title">住址信息</span>
            </div>
            <div class="info-list">
              <div class="info-row"><span class="info-label">现住址</span><span class="info-value">{{ formatEmpty(detail.employee.currentAddress) }}</span></div>
              <div class="info-row"><span class="info-label">户籍地址</span><span class="info-value">{{ formatEmpty(detail.employee.hukouAddress) }}</span></div>
            </div>
          </div>

          <div v-if="detail.families?.length" class="section-block">
            <div class="section-header">
              <span class="section-icon family"></span>
              <span class="section-title">家庭成员</span>
              <el-tag size="small" effect="plain">{{ detail.families.length }} 位</el-tag>
            </div>
            <div class="family-list">
              <div v-for="(f, i) in detail.families" :key="f.id || i" class="family-item">
                <div class="family-main">
                  <span class="family-name">{{ f.name }}</span>
                  <el-tag size="small" effect="plain">{{ f.relation }}</el-tag>
                </div>
                <div class="family-sub">
                  {{ f.gender === 1 ? '男' : '女' }}
                  <span v-if="f.birthDate"> · {{ f.birthDate }}</span>
                  <span v-if="f.phone"> · {{ f.phone }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 合同薪资 -->
        <el-tab-pane :label="`合同薪资`" name="contract">
          <div v-if="!detail.contracts?.length" class="empty-block">暂无合同记录</div>
          <div v-else>
            <div v-for="c in detail.contracts" :key="c.id" class="contract-card" :class="contractStatusClass(c)">
              <div class="contract-head">
                <div class="contract-title-row">
                  <el-tag :type="contractTagType(c)" effect="dark" size="small">{{ c.contractType }}</el-tag>
                  <span class="contract-no ems-mono">{{ c.contractNo || '未填写合同号' }}</span>
                </div>
                <div class="contract-status-tag">
                  <el-tag v-if="getContractExpireDays(c) !== null && getContractExpireDays(c)! <= 30 && getContractExpireDays(c)! > 0" type="warning" size="small" effect="plain">
                    {{ getContractExpireDays(c) }} 天后到期
                  </el-tag>
                  <el-tag v-else-if="getContractExpireDays(c) !== null && getContractExpireDays(c)! <= 0" type="danger" size="small" effect="plain">
                    已到期
                  </el-tag>
                  <el-tag v-else-if="getContractExpireDays(c) === null" type="info" size="small" effect="plain">无固定期</el-tag>
                  <el-tag v-else type="success" size="small" effect="plain">执行中</el-tag>
                </div>
              </div>
              <div class="contract-grid">
                <div class="cg-item"><span class="cg-label">开始日期</span><span class="cg-value">{{ c.startDate || '-' }}</span></div>
                <div class="cg-item"><span class="cg-label">结束日期</span><span class="cg-value">{{ c.endDate || '无固定期' }}</span></div>
                <div class="cg-item"><span class="cg-label">签订日期</span><span class="cg-value">{{ c.signedDate || '-' }}</span></div>
                <div class="cg-item"><span class="cg-label">工作地点</span><span class="cg-value">{{ c.workLocation || '-' }}</span></div>
                <div class="cg-item"><span class="cg-label">试用期</span><span class="cg-value">{{ c.probationMonths || 0 }} 个月</span></div>
                <div class="cg-item"><span class="cg-label">合同薪资</span><span class="cg-value salary">¥{{ formatSalary(c.salary) }}</span></div>
              </div>
              <div v-if="c.remark" class="contract-remark">备注：{{ c.remark }}</div>
            </div>
          </div>

          <div v-if="detail.probations?.length" class="section-block" style="margin-top: 14px;">
            <div class="section-header">
              <span class="section-icon probation"></span>
              <span class="section-title">试用期记录</span>
            </div>
            <el-timeline>
              <el-timeline-item
                v-for="(p, i) in detail.probations"
                :key="p.id || i"
                :timestamp="`${p.startDate || '?'} ~ ${p.endDate || '?'}`"
                placement="top"
                :hollow="false"
                :type="p.result === 0 ? 'danger' : p.result === 3 ? 'warning' : 'success'"
              >
                <div class="timeline-card">
                  <div class="tc-row">
                    <el-tag size="small" :type="p.result === 0 ? 'danger' : p.result === 3 ? 'warning' : 'success'" effect="plain">
                      {{ probationResultText(p.result) }}
                    </el-tag>
                  </div>
                  <div class="tc-sub">
                    考核日期：{{ p.resultDate || '-' }}
                    <span v-if="p.evaluator"> · 考核人：{{ p.evaluator }}</span>
                    <span v-if="p.result === 3 && p.extensionEndDate"> · 延长至 {{ p.extensionEndDate }}</span>
                  </div>
                  <div v-if="p.evaluation" class="tc-reason">评语：{{ p.evaluation }}</div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-tab-pane>

        <!-- 履历资料 -->
        <el-tab-pane label="履历资料" name="resume">
          <div class="section-block">
            <div class="section-header">
              <span class="section-icon edu"></span>
              <span class="section-title">教育经历</span>
              <el-tag v-if="detail.educations?.length" size="small" effect="plain">{{ detail.educations.length }} 段</el-tag>
            </div>
            <div v-if="!detail.educations?.length" class="empty-inline">暂无教育经历</div>
            <el-timeline v-else>
              <el-timeline-item
                v-for="(e, i) in detail.educations"
                :key="e.id || i"
                :timestamp="`${e.startDate || '?'} ~ ${e.endDate || '至今'}`"
                placement="top"
                :hollow="false"
                type="primary"
              >
                <div class="timeline-card">
                  <div class="tc-row">
                    <span class="tc-title">{{ e.school }}</span>
                    <el-tag size="small" effect="plain">{{ e.education }}</el-tag>
                    <el-tag v-if="e.degree" size="small" type="success" effect="plain">{{ e.degree }}</el-tag>
                  </div>
                  <div class="tc-sub">{{ e.major || '未填写专业' }}<span v-if="e.isFullTime === 1"> · 全日制</span></div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>

          <div class="section-block">
            <div class="section-header">
              <span class="section-icon work"></span>
              <span class="section-title">工作经历</span>
              <el-tag v-if="detail.workExperiences?.length" size="small" effect="plain">{{ detail.workExperiences.length }} 段</el-tag>
            </div>
            <div v-if="!detail.workExperiences?.length" class="empty-inline">暂无工作经历</div>
            <el-timeline v-else>
              <el-timeline-item
                v-for="(w, i) in detail.workExperiences"
                :key="w.id || i"
                :timestamp="`${w.startDate || '?'} ~ ${w.endDate || '至今'}`"
                placement="top"
                type="warning"
              >
                <div class="timeline-card">
                  <div class="tc-row">
                    <span class="tc-title">{{ w.company }}</span>
                  </div>
                  <div class="tc-sub">
                    {{ w.position || '未填写职位' }}<span v-if="w.department"> · {{ w.department }}</span>
                  </div>
                  <div v-if="w.leaveReason" class="tc-reason">离职原因：{{ w.leaveReason }}</div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>

          <div class="section-block">
            <div class="section-header">
              <span class="section-icon cert"></span>
              <span class="section-title">技能证书</span>
              <el-tag v-if="detail.certificates?.length" size="small" effect="plain">{{ detail.certificates.length }} 本</el-tag>
            </div>
            <div v-if="!detail.certificates?.length" class="empty-inline">暂无技能证书</div>
            <div v-else class="cert-list">
              <div v-for="(c, i) in detail.certificates" :key="c.id || i" class="cert-item">
                <div class="cert-name">{{ c.name }}</div>
                <div class="cert-meta">
                  <el-tag size="small" effect="plain">{{ c.level || '-' }}</el-tag>
                  <span class="cert-issuer">{{ c.issuer || '-' }}</span>
                </div>
                <div class="cert-date">
                  {{ c.issueDate || '-' }} ~ {{ c.expireDate || '长期' }}
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 变更记录 -->
        <el-tab-pane :label="`变更记录`" name="changes">
          <div v-if="!changes.length" class="empty-block">暂无变更记录</div>
          <el-timeline v-else>
            <el-timeline-item
              v-for="c in changes"
              :key="c.id"
              :timestamp="c.effectiveDate"
              placement="top"
              :hollow="false"
              :type="changeTypeColor(c.changeType)"
            >
              <div class="timeline-card">
                <div class="tc-row">
                  <el-tag :type="changeTypeColor(c.changeType)" effect="dark" size="small">
                    {{ changeTypeLabel(c.changeType) }}
                  </el-tag>
                  <span class="tc-title">{{ c.changeSummary }}</span>
                </div>
                <div class="tc-sub">
                  <span v-if="c.applicant">申请人：{{ c.applicant }}</span>
                  <span v-if="c.approver"> · 审批人：{{ c.approver }}</span>
                  <span v-if="c.approveDate"> · 审批日期：{{ c.approveDate }}</span>
                </div>
                <div v-if="c.reason" class="tc-reason">原因：{{ c.reason }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { type EmployeeDetailVO, employeeApi } from '@/api/employee'
import { type Dictionary } from '@/api/dictionary'
import { workflowApi, type WorkflowChange } from '@/api/workflow'
import { getAvatarColor, formatSalary, formatEmpty, formatWorkYears } from '@/utils/common'

const props = defineProps<{
  visible: boolean
  employeeId?: number | null
  departmentOptions?: Dictionary[]
  positionOptions?: Dictionary[]
}>()

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void
}>()

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const activeTab = ref('overview')
const detail = ref<EmployeeDetailVO | null>(null)
const changes = ref<WorkflowChange[]>([])

const load = async () => {
  if (!props.employeeId) {
    detail.value = null
    return
  }
  const res = await employeeApi.getDetail(props.employeeId)
  detail.value = res
  loadChanges()
}

const loadChanges = async () => {
  if (!props.employeeId) {
    changes.value = []
    return
  }
  try {
    const res = await workflowApi.listByEmployee(props.employeeId)
    changes.value = (res || []) as WorkflowChange[]
  } catch (e) {
    changes.value = []
  }
}

watch(() => props.employeeId, load, { immediate: true })
watch(visible, (v) => { if (v) load() })

const changeTypeColor = (type?: string): 'primary' | 'success' | 'warning' | 'danger' | 'info' => {
  switch (type) {
    case 'TRANSFER': return 'primary'
    case 'ADJUST_SALARY': return 'success'
    case 'CONFIRM': return 'warning'
    case 'LEAVE': return 'danger'
    default: return 'info'
  }
}

const changeTypeLabel = (type?: string) => {
  switch (type) {
    case 'TRANSFER': return '调岗'
    case 'ADJUST_SALARY': return '调薪'
    case 'CONFIRM': return '转正'
    case 'LEAVE': return '离职'
    default: return '其他'
  }
}

const departmentNameMap = computed(() => {
  const map: Record<string, string> = {}
  ;(props.departmentOptions || []).forEach((d) => { if (d.code) map[d.code] = d.name })
  return map
})

const positionNameMap = computed(() => {
  const map: Record<string, string> = {}
  ;(props.positionOptions || []).forEach((d) => { if (d.code) map[d.code] = d.name })
  return map
})

const deptName = computed(() => {
  const code = detail.value?.employee?.department
  return code ? (departmentNameMap.value[code] || code) : ''
})

const posName = computed(() => {
  const code = detail.value?.employee?.position
  return code ? (positionNameMap.value[code] || code) : ''
})

const maritalText = computed(() => {
  const m = detail.value?.employee?.maritalStatus
  switch (m) {
    case 1: return '已婚'
    case 2: return '离异'
    case 3: return '丧偶'
    default: return m === 0 ? '未婚' : '-'
  }
})

const getContractExpireDays = (c: any): number | null => {
  if (!c.endDate) return null
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const end = new Date(c.endDate)
  const diff = Math.ceil((end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))
  return diff
}

const contractStatusClass = (c: any) => {
  const days = getContractExpireDays(c)
  if (days === null) return ''
  if (days <= 0) return 'contract-expired'
  if (days <= 30) return 'contract-warning'
  return 'contract-normal'
}

const contractTagType = (c: any): 'success' | 'warning' | 'info' | 'danger' => {
  const days = getContractExpireDays(c)
  if (days === null) return 'info'
  if (days <= 0) return 'danger'
  if (days <= 30) return 'warning'
  return 'success'
}

const probationResultText = (result?: number) => {
  switch (result) {
    case 0: return '未通过'
    case 1: return '提前转正'
    case 2: return '正常转正'
    case 3: return '延长试用期'
    default: return '未考核'
  }
}
</script>

<style scoped>
.detail-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 4px 2px 12px;
}

/* ========== 顶部 Hero 区 ========== */
.detail-hero {
  position: relative;
  border-radius: 14px;
  padding: 18px 18px 16px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 85% 20%, rgba(255,255,255,0.15) 0%, transparent 40%),
    radial-gradient(circle at 10% 90%, rgba(255,255,255,0.1) 0%, transparent 35%);
  pointer-events: none;
}

.hero-main {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.hero-avatar {
  border: 3px solid rgba(255,255,255,0.35);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  font-weight: 600;
  font-size: 24px;
}

.hero-info {
  flex: 1;
  min-width: 0;
}

.hero-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.hero-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.hero-dept {
  font-size: 13px;
  opacity: 0.9;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.hero-sep {
  opacity: 0.5;
}

.hero-no {
  font-size: 12px;
  opacity: 0.75;
  letter-spacing: 0.3px;
}

.hero-stats {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: rgba(255,255,255,0.12);
  backdrop-filter: blur(8px);
  border-radius: 10px;
  padding: 10px 4px;
  border: 1px solid rgba(255,255,255,0.18);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  font-size: 16px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-value.salary {
  font-size: 14px;
}

.stat-label {
  font-size: 11px;
  opacity: 0.75;
  margin-top: 3px;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 28px;
  background: rgba(255,255,255,0.2);
}

/* ========== Tabs ========== */
.detail-tabs {
  margin-top: 2px;
}

.detail-tabs :deep(.el-tabs__nav-wrap::after) {
  background: transparent;
}

.detail-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}

.detail-tabs :deep(.el-tabs__item) {
  height: 40px;
  line-height: 40px;
  font-size: 13.5px;
  font-weight: 500;
}

.detail-tabs :deep(.el-tabs__content) {
  padding: 0 2px;
}

/* ========== Section 通用 ========== */
.section-block {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 14px 16px;
  margin-bottom: 12px;
  transition: all 0.2s ease;
}

.section-block:hover {
  border-color: var(--border-default);
  box-shadow: 0 2px 8px -2px rgba(0,0,0,0.04);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.section-icon {
  width: 4px;
  height: 16px;
  border-radius: 2px;
  background: var(--brand-500);
}

.section-icon.work { background: linear-gradient(180deg, #8b5cf6, #6366f1); }
.section-icon.base { background: linear-gradient(180deg, #3b82f6, #2563eb); }
.section-icon.contact { background: linear-gradient(180deg, #10b981, #059669); }
.section-icon.address { background: linear-gradient(180deg, #f59e0b, #d97706); }
.section-icon.family { background: linear-gradient(180deg, #ec4899, #db2777); }
.section-icon.edu { background: linear-gradient(180deg, #6366f1, #4f46e5); }
.section-icon.work { background: linear-gradient(180deg, #f59e0b, #d97706); }
.section-icon.cert { background: linear-gradient(180deg, #14b8a6, #0d9488); }
.section-icon.probation { background: linear-gradient(180deg, #f97316, #ea580c); }

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

/* ========== 信息网格/列表 ========== */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.info-label {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
  flex-shrink: 0;
}

.info-value {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
  text-align: right;
  word-break: break-all;
}

.info-grid .info-value {
  text-align: left;
}

/* ========== 家庭成员 ========== */
.family-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.family-item {
  padding: 10px 12px;
  background: var(--bg-soft);
  border-radius: 8px;
  border: 1px solid var(--border-subtle);
}

.family-main {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.family-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.family-sub {
  font-size: 12px;
  color: var(--text-secondary);
}

/* ========== 合同卡片 ========== */
.contract-card {
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 14px 16px;
  margin-bottom: 12px;
  background: var(--bg-elevated);
  transition: all 0.2s;
}

.contract-card:hover {
  box-shadow: 0 4px 12px -4px rgba(0,0,0,0.08);
}

.contract-card.contract-expired {
  border-color: var(--rose-300);
  background: linear-gradient(135deg, #fff1f2, #fef2f2);
}

.contract-card.contract-warning {
  border-color: var(--amber-300);
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
}

.contract-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--border-default);
}

.contract-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.contract-no {
  font-size: 12px;
  color: var(--text-secondary);
}

.contract-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px 14px;
}

.cg-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cg-label {
  font-size: 11px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.cg-value {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}

.cg-value.salary {
  color: var(--rose-500);
  font-weight: 600;
}

.contract-remark {
  margin-top: 10px;
  padding: 8px 10px;
  background: var(--bg-soft);
  border-radius: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* ========== 时间线卡片 ========== */
.timeline-card {
  background: var(--bg-soft);
  border-radius: 8px;
  padding: 10px 12px;
  border: 1px solid var(--border-subtle);
}

.tc-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.tc-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.tc-sub {
  font-size: 12.5px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.tc-reason {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 6px;
  font-style: italic;
}

/* ========== 证书列表 ========== */
.cert-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.cert-item {
  padding: 12px;
  background: var(--bg-soft);
  border-radius: 8px;
  border: 1px solid var(--border-subtle);
  transition: all 0.2s;
}

.cert-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px -4px rgba(0,0,0,0.1);
}

.cert-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
  margin-bottom: 6px;
}

.cert-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.cert-issuer {
  font-size: 12px;
  color: var(--text-secondary);
}

.cert-date {
  font-size: 11.5px;
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

/* ========== 空状态 ========== */
.empty-block {
  text-align: center;
  color: var(--text-tertiary);
  padding: 48px 0;
  font-size: 13px;
}

.empty-inline {
  text-align: center;
  color: var(--text-tertiary);
  padding: 20px 0;
  font-size: 12.5px;
  background: var(--bg-soft);
  border-radius: 8px;
}

/* ========== 响应式 ========== */
@media (max-width: 480px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .contract-grid {
    grid-template-columns: 1fr 1fr;
  }
  .cert-list {
    grid-template-columns: 1fr;
  }
}
</style>
