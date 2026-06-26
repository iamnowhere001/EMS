<template>
  <el-drawer
    v-model="visible"
    :title="detail?.employee ? `${detail.employee.name} 的员工档案` : '员工档案'"
    size="560px"
    class="employee-detail-drawer"
  >
    <div v-if="detail?.employee" class="detail-content">
      <!-- 顶部信息卡 -->
      <div class="detail-header">
        <el-avatar
          :size="72"
          :src="detail.employee.avatar"
          :style="{ backgroundColor: getAvatarColor(detail.employee.name, detail.employee.gender) }"
        >
          {{ detail.employee.name?.charAt(0) }}
        </el-avatar>
        <div class="detail-header-info">
          <div class="detail-name">
            {{ detail.employee.name }}
            <el-tag :type="detail.employee.status === 1 ? 'success' : 'info'" effect="dark" size="small" round>
              {{ detail.employee.status === 1 ? '在职' : '离职' }}
            </el-tag>
            <el-tag v-if="detail.employee.rank" effect="plain" size="small" round style="margin-left:6px">
              {{ detail.employee.rank }}
            </el-tag>
          </div>
          <div class="detail-meta">工号：{{ formatEmpty(detail.employee.employeeNo) }}</div>
          <div class="detail-meta">
            {{ formatEmpty(deptName) }}<span v-if="detail.employee.position"> · {{ formatEmpty(posName) }}</span>
          </div>
        </div>
      </div>

      <!-- Tab 切换档案类别 -->
      <el-tabs v-model="activeTab" class="detail-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="base">
          <div class="detail-section">
            <div class="detail-section-title"><span class="section-dot purple" />工作信息</div>
            <div class="detail-grid">
              <div class="detail-item"><span class="detail-label">部门</span><span class="detail-value">{{ formatEmpty(deptName) }}</span></div>
              <div class="detail-item"><span class="detail-label">职位</span><span class="detail-value">{{ formatEmpty(posName) }}</span></div>
              <div class="detail-item"><span class="detail-label">职级</span><span class="detail-value">{{ formatEmpty(detail.employee.rank) }}</span></div>
              <div class="detail-item"><span class="detail-label">薪资</span><span class="detail-value salary">¥ {{ formatSalary(detail.employee.salary) }}</span></div>
              <div class="detail-item"><span class="detail-label">入职日期</span><span class="detail-value">{{ formatEmpty(detail.employee.hireDate) }}</span></div>
            </div>
          </div>

          <div class="detail-section">
            <div class="detail-section-title"><span class="section-dot blue" />基础资料</div>
            <div class="detail-grid">
              <div class="detail-item"><span class="detail-label">年龄</span><span class="detail-value">{{ formatEmpty(detail.employee.age) }}</span></div>
              <div class="detail-item"><span class="detail-label">性别</span><span class="detail-value">{{ detail.employee.gender === 1 ? '男' : '女' }}</span></div>
              <div class="detail-item"><span class="detail-label">工龄</span><span class="detail-value">{{ formatWorkYears(detail.employee.hireDate) }}</span></div>
              <div class="detail-item"><span class="detail-label">民族</span><span class="detail-value">{{ formatEmpty(detail.employee.nation) }}</span></div>
              <div class="detail-item"><span class="detail-label">籍贯</span><span class="detail-value">{{ formatEmpty(detail.employee.nativePlace) }}</span></div>
              <div class="detail-item"><span class="detail-label">婚姻</span><span class="detail-value">{{ maritalText }}</span></div>
              <div class="detail-item"><span class="detail-label">政治面貌</span><span class="detail-value">{{ formatEmpty(detail.employee.politicalStatus) }}</span></div>
            </div>
          </div>

          <div class="detail-section">
            <div class="detail-section-title"><span class="section-dot green" />联系方式</div>
            <div class="detail-list">
              <div class="detail-item full"><span class="detail-label">手机号</span><span class="detail-value">{{ formatEmpty(detail.employee.phone) }}</span></div>
              <div class="detail-item full"><span class="detail-label">邮箱</span><span class="detail-value">{{ formatEmpty(detail.employee.email) }}</span></div>
              <div class="detail-item full"><span class="detail-label">身份证号</span><span class="detail-value">{{ formatEmpty(detail.employee.idCard) }}</span></div>
              <div class="detail-item full"><span class="detail-label">银行卡号</span><span class="detail-value">{{ formatEmpty(detail.employee.bankCard) }}</span></div>
              <div class="detail-item full"><span class="detail-label">紧急联系人</span><span class="detail-value">{{ formatEmpty(detail.employee.emergencyContact) }} {{ detail.employee.emergencyPhone ? `(${formatEmpty(detail.employee.emergencyPhone)})` : '' }}</span></div>
            </div>
          </div>

          <div class="detail-section">
            <div class="detail-section-title"><span class="section-dot orange" />住址</div>
            <div class="detail-list">
              <div class="detail-item full"><span class="detail-label">现住址</span><span class="detail-value">{{ formatEmpty(detail.employee.currentAddress) }}</span></div>
              <div class="detail-item full"><span class="detail-label">户籍地址</span><span class="detail-value">{{ formatEmpty(detail.employee.hukouAddress) }}</span></div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 教育经历 -->
        <el-tab-pane :label="`教育 (${detail.educations?.length || 0})`" name="edu">
          <div v-if="!detail.educations?.length" class="empty-block">暂无教育经历</div>
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
        </el-tab-pane>

        <!-- 工作经历 -->
        <el-tab-pane :label="`工作经历 (${detail.workExperiences?.length || 0})`" name="work">
          <div v-if="!detail.workExperiences?.length" class="empty-block">暂无工作经历</div>
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
        </el-tab-pane>

        <!-- 家庭成员 -->
        <el-tab-pane :label="`家庭 (${detail.families?.length || 0})`" name="family">
          <div v-if="!detail.families?.length" class="empty-block">暂未登记家庭成员</div>
          <el-table v-else :data="detail.families" size="small" :show-header="true" stripe>
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="relation" label="关系" width="80" />
            <el-table-column label="性别" width="60">
              <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
            </el-table-column>
            <el-table-column prop="birthDate" label="出生日期" width="120" />
            <el-table-column prop="company" label="工作单位" />
            <el-table-column prop="phone" label="联系电话" width="130" />
          </el-table>
        </el-tab-pane>

        <!-- 技能证书 -->
        <el-tab-pane :label="`证书 (${detail.certificates?.length || 0})`" name="cert">
          <div v-if="!detail.certificates?.length" class="empty-block">暂无技能证书</div>
          <el-table v-else :data="detail.certificates" size="small" stripe>
            <el-table-column prop="name" label="证书名称" width="160" />
            <el-table-column prop="level" label="级别" width="80" />
            <el-table-column prop="issuer" label="发证机构" width="140" />
            <el-table-column label="发证/到期" width="200">
              <template #default="{ row }">
                {{ row.issueDate || '-' }} ~ {{ row.expireDate || '长期' }}
              </template>
            </el-table-column>
            <el-table-column prop="certNo" label="证书编号" />
          </el-table>
        </el-tab-pane>

        <!-- 合同信息 -->
        <el-tab-pane :label="`合同 (${detail.contracts?.length || 0})`" name="contract">
          <div v-if="!detail.contracts?.length" class="empty-block">暂无合同记录</div>
          <div v-else>
            <div v-for="c in detail.contracts" :key="c.id" class="contract-card" :class="contractStatusClass(c)">
              <div class="contract-head">
                <div class="contract-title">
                  <el-tag :type="contractTagType(c)" effect="dark" size="small">{{ c.contractType }}</el-tag>
                  <span class="contract-no">{{ c.contractNo || '未填写合同号' }}</span>
                </div>
                <div class="contract-status">
                  <el-tag v-if="getContractExpireDays(c) !== null && getContractExpireDays(c)! <= 30 && getContractExpireDays(c)! > 0" type="warning" size="small" effect="plain">
                    {{ getContractExpireDays(c) }} 天后到期
                  </el-tag>
                  <el-tag v-else-if="getContractExpireDays(c) !== null && getContractExpireDays(c)! <= 0" type="danger" size="small" effect="plain">
                    已到期
                  </el-tag>
                  <el-tag v-else-if="getContractExpireDays(c) === null" type="info" size="small" effect="plain">长期</el-tag>
                </div>
              </div>
              <div class="contract-body">
                <div class="contract-row">
                  <div class="contract-item"><span class="ci-label">开始日期</span><span class="ci-value">{{ c.startDate || '-' }}</span></div>
                  <div class="contract-item"><span class="ci-label">结束日期</span><span class="ci-value">{{ c.endDate || '无固定期' }}</span></div>
                  <div class="contract-item"><span class="ci-label">签订日期</span><span class="ci-value">{{ c.signedDate || '-' }}</span></div>
                </div>
                <div class="contract-row">
                  <div class="contract-item"><span class="ci-label">试用期</span><span class="ci-value">{{ c.probationMonths || 0 }} 个月</span></div>
                  <div class="contract-item"><span class="ci-label">试用期</span><span class="ci-value">{{ c.probationStartDate || '-' }} ~ {{ c.probationEndDate || '-' }}</span></div>
                  <div class="contract-item"><span class="ci-label">工作地点</span><span class="ci-value">{{ c.workLocation || '-' }}</span></div>
                </div>
                <div class="contract-row" v-if="c.salary">
                  <div class="contract-item"><span class="ci-label">合同薪资</span><span class="ci-value salary">¥ {{ formatSalary(c.salary) }}</span></div>
                  <div class="contract-item"><span class="ci-label">状态</span><span class="ci-value">{{ contractStatusText(c.status) }}</span></div>
                </div>
                <div v-if="c.remark" class="contract-remark">备注：{{ c.remark }}</div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 试用期记录 -->
        <el-tab-pane :label="`试用期 (${detail.probations?.length || 0})`" name="probation">
          <div v-if="!detail.probations?.length" class="empty-block">暂无试用期记录</div>
          <el-timeline v-else>
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
                  <span class="tc-title">试用期</span>
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
        </el-tab-pane>

        <!-- 变更历史 -->
        <el-tab-pane :label="`变更历史 (${changes.length})`" name="changes">
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

const activeTab = ref('base')
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

// 变更类型 -> Element Tag/类型映射
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

// 合同相关辅助方法
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

const contractStatusText = (status?: number) => {
  switch (status) {
    case 0: return '已到期'
    case 1: return '执行中'
    case 2: return '已终止'
    case 3: return '已续签'
    default: return '执行中'
  }
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
  gap: 8px;
  padding: 0 6px;
}
.detail-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 14px;
  background: linear-gradient(135deg, #e3f2fd, #f3e5f5);
  border-radius: 10px;
  position: relative;
  overflow: hidden;
}
.detail-header::after {
  content: '';
  position: absolute;
  right: -30px;
  top: -30px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.6), transparent 60%);
}
.detail-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}
.detail-meta {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}
.detail-tabs {
  margin-top: 4px;
}
.detail-tabs :deep(.el-tabs__nav-wrap)::after {
  background: transparent;
}
.detail-section {
  background: #fff;
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ebeef5;
  margin-bottom: 6px;
}
.detail-section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}
.section-dot {
  display: inline-block;
  width: 4px;
  height: 14px;
  border-radius: 2px;
  background: #409eff;
}
.section-dot.blue { background: #409eff; }
.section-dot.green { background: #67c23a; }
.section-dot.orange { background: #e6a23c; }
.section-dot.purple { background: #9c64f0; }
.section-dot.gray { background: #909399; }
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px 14px;
}
.detail-list { display: flex; flex-direction: column; gap: 6px; }
.detail-item {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.detail-item.full { width: 100%; }
.detail-label {
  font-size: 11px;
  color: #909399;
}
.detail-value {
  font-size: 13px;
  color: #303133;
}
.detail-value.salary { color: #f56c6c; font-weight: 600; }
.empty-block {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 13px;
}
.timeline-card {
  background: #fafafa;
  border-radius: 8px;
  padding: 10px 12px;
  border: 1px solid #f0f0f0;
}
.tc-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.tc-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.tc-sub {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}
.tc-reason {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  font-style: italic;
}

/* 合同卡片样式 */
.contract-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  background: #fafbfc;
  transition: all 0.2s;
}
.contract-card.contract-expired {
  border-color: #f56c6c;
  background: #fef0f0;
}
.contract-card.contract-warning {
  border-color: #e6a23c;
  background: #fdf6ec;
}
.contract-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ebeef5;
}
.contract-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
}
.contract-no {
  color: #606266;
  font-size: 12px;
  font-weight: normal;
}
.contract-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.contract-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 6px 14px;
}
.contract-item {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.ci-label {
  font-size: 11px;
  color: #909399;
}
.ci-value {
  font-size: 13px;
  color: #303133;
}
.ci-value.salary {
  color: #f56c6c;
  font-weight: 600;
}
.contract-remark {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  padding: 6px 10px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
