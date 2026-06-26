<template>
  <div class="dashboard-page ems-page">
    <!-- 顶部 KPI -->
    <div class="kpi-row">
      <div
        class="kpi-card"
        v-for="(item, index) in kpiList"
        :key="item.key"
        :class="['kpi-' + item.tone, 'kpi-anim']"
        :style="{ animationDelay: index * 80 + 'ms' }"
      >
        <div class="kpi-glow" :style="{ background: item.glow }"></div>
        <div class="kpi-icon">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <div class="kpi-info">
          <div class="kpi-label">{{ item.label }}</div>
          <div class="kpi-value ems-mono">{{ item.display }}</div>
          <div class="kpi-sub">{{ item.sub }}</div>
        </div>
      </div>
    </div>

    <!-- 第一行：核心分布与趋势 -->
    <el-row :gutter="14" class="chart-row">
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #6366f1"></span>
                部门人员分布
              </div>
              <el-tag size="small" type="primary" effect="light" round>在职员工</el-tag>
            </div>
          </template>
          <v-chart :option="deptOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #8b5cf6"></span>
                月度入职趋势
              </div>
              <span class="chart-sub">最近 12 个月</span>
            </div>
          </template>
          <v-chart :option="trendOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #10b981"></span>
                性别比例
              </div>
            </div>
          </template>
          <v-chart :option="genderOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：薪资与职级 -->
    <el-row :gutter="14" class="chart-row">
      <el-col :span="14">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #0ea5e9"></span>
                部门薪资统计
              </div>
              <span class="chart-sub">在职员工 · 按总薪资降序</span>
            </div>
          </template>
          <v-chart :option="deptSalaryOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #f59e0b"></span>
                薪资区间分布
              </div>
            </div>
          </template>
          <v-chart :option="salaryOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行：员工画像 -->
    <el-row :gutter="14" class="chart-row">
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #10b981"></span>
                年龄分布
              </div>
            </div>
          </template>
          <v-chart :option="ageOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #f59e0b"></span>
                工龄分布
              </div>
            </div>
          </template>
          <v-chart :option="tenureOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #8b5cf6"></span>
                学历分布
              </div>
            </div>
          </template>
          <v-chart :option="educationOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="chart-title">
                <span class="chart-title-dot" style="background: #f43f5e"></span>
                职级分布
              </div>
            </div>
          </template>
          <v-chart :option="rankOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, CircleCheckFilled, OfficeBuilding, Money } from '@element-plus/icons-vue'
import { dashboardApi, type DashboardData, type NameValue } from '@/api/dashboard'
import { formatSalary } from '@/utils/common'

const data = ref<DashboardData>({} as DashboardData)
const loading = ref(false)

const formatNum = (n?: number) => {
  if (n == null) return '0'
  return n.toLocaleString('zh-CN', { maximumFractionDigits: 2 })
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await dashboardApi.overview()
    data.value = res
  } catch (e: any) {
    ElMessage.error(e.message || '加载看板失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)

// ============ KPI 列表(数据驱动) ============
const kpiList = computed(() => [
  {
    key: 'total',
    label: '员工总数',
    display: data.value.kpi?.totalCount?.toLocaleString() ?? '0',
    sub: `在职 ${data.value.kpi?.activeCount ?? 0} · 离职 ${data.value.kpi?.leftCount ?? 0}`,
    icon: UserFilled,
    tone: 'primary',
    glow: 'radial-gradient(circle at 100% 0%, rgba(99, 102, 241, 0.18), transparent 60%)',
  },
  {
    key: 'new',
    label: '本月入职',
    display: data.value.kpi?.newHireThisMonth?.toLocaleString() ?? '0',
    sub: '近 12 月趋势见下方',
    icon: CircleCheckFilled,
    tone: 'success',
    glow: 'radial-gradient(circle at 100% 0%, rgba(16, 185, 129, 0.18), transparent 60%)',
  },
  {
    key: 'dept',
    label: '部门数量',
    display: data.value.kpi?.departmentCount?.toLocaleString() ?? '0',
    sub: `平均工龄 ${data.value.kpi?.avgTenureYears ?? 0} 年`,
    icon: OfficeBuilding,
    tone: 'warning',
    glow: 'radial-gradient(circle at 100% 0%, rgba(245, 158, 11, 0.18), transparent 60%)',
  },
  {
    key: 'salary',
    label: '月度薪资',
    display: '¥ ' + formatNum(data.value.kpi?.totalSalary),
    sub: `平均 ¥ ${formatNum(data.value.kpi?.avgSalary)} / 人`,
    icon: Money,
    tone: 'danger',
    glow: 'radial-gradient(circle at 100% 0%, rgba(244, 63, 94, 0.18), transparent 60%)',
  },
])

// ============ 通用配色 ============
const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#f43f5e', '#8b5cf6', '#0ea5e9', '#ec4899', '#06b6d4', '#84cc16', '#a855f7']
const genderColors = ['#6366f1', '#f43f5e']

// ============ ECharts 主题色（与设计令牌联动） ============
const textColor = '#57534e'
const subTextColor = '#a8a29e'
const splitLineColor = 'rgba(0, 0, 0, 0.05)'

// ============ 部门分布：横向条形图 ============
const deptOption = computed(() => {
  const items = data.value.departmentDistribution || []
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(28, 25, 23, 0.95)',
      borderWidth: 0,
      textStyle: { color: '#fff', fontSize: 12 },
      padding: [8, 12],
    },
    grid: { left: 80, right: 40, top: 10, bottom: 10, containLabel: true },
    xAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: splitLineColor } }, axisLabel: { color: subTextColor } },
    yAxis: { type: 'category', data: items.map(i => i.name), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: textColor, fontWeight: 500 } },
    series: [{
      type: 'bar',
      data: items.map((i, idx) => ({
        value: i.value,
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: COLORS[idx % COLORS.length] + 'AA' },
              { offset: 1, color: COLORS[idx % COLORS.length] }
            ]
          },
          borderRadius: [0, 8, 8, 0]
        }
      })),
      label: { show: true, position: 'right', color: subTextColor, fontSize: 11, fontWeight: 600 },
      barWidth: 14,
      showBackground: true,
      backgroundStyle: { color: 'rgba(0, 0, 0, 0.025)', borderRadius: [0, 8, 8, 0] }
    }],
  }
})

// ============ 性别比例：饼图 ============
const genderOption = computed(() => {
  const items = data.value.genderRatio || []
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, icon: 'circle', textStyle: { color: textColor, fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['48%', '72%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 3 },
      label: { show: true, formatter: '{b}\n{c} 人', color: textColor, fontSize: 12 },
      labelLine: { length: 6, length2: 6 },
      data: items.map((i, idx) => ({ name: i.name, value: i.value, itemStyle: { color: genderColors[idx % genderColors.length] } })),
    }],
  }
})

// ============ 月度入职趋势：折线图 ============
const trendOption = computed(() => {
  const items = data.value.monthlyTrend || []
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 24, bottom: 22, containLabel: true },
    xAxis: { type: 'category', data: items.map(i => i.month.substring(5)), axisLine: { lineStyle: { color: splitLineColor } }, axisLabel: { color: subTextColor } },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: splitLineColor } }, axisLabel: { color: subTextColor } },
    series: [
      {
        name: '入职人数',
        type: 'line',
        data: items.map(i => i.onboardCount),
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        lineStyle: { width: 3, color: '#6366f1' },
        itemStyle: { color: '#6366f1', borderColor: '#fff', borderWidth: 2 },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
          { offset: 0, color: 'rgba(99, 102, 241, 0.35)' },
          { offset: 1, color: 'rgba(99, 102, 241, 0.02)' },
        ] } },
        markPoint: { symbol: 'pin', symbolSize: 36, data: [{ type: 'max', name: '最大' }], itemStyle: { color: '#6366f1' } },
      },
    ],
  }
})

// ============ 职级分布：玫瑰图 ============
const rankOption = computed(() => {
  const items = data.value.rankDistribution || []
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} 人 ({d}%)' },
    legend: { type: 'scroll', orient: 'horizontal', bottom: 0, icon: 'circle', textStyle: { color: textColor, fontSize: 11 } },
    series: [{
      type: 'pie',
      radius: [20, 80],
      center: ['50%', '45%'],
      roseType: 'area',
      itemStyle: { borderRadius: 4 },
      label: {
        show: true,
        formatter: '{b}\n{c}人',
        fontSize: 11,
        color: textColor,
      },
      labelLine: { show: true, length: 8, length2: 8 },
      labelLayout: { hideOverlap: false },
      data: items.map((i, idx) => ({ name: i.rank, value: i.count, itemStyle: { color: COLORS[idx % COLORS.length] } })),
    }],
  }
})

// ============ 年龄分布：柱状图 ============
const ageOption = computed(() => buildBarOption(data.value.ageDistribution, '#10b981'))

// ============ 工龄分布：柱状图 ============
const tenureOption = computed(() => buildBarOption(data.value.tenureDistribution, '#f59e0b'))

// ============ 学历分布：柱状图 ============
const educationOption = computed(() => buildBarOption(data.value.educationDistribution, '#8b5cf6'))

function buildBarOption(items: NameValue[], color: string) {
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 10, right: 10, top: 20, bottom: 5, containLabel: true },
    xAxis: {
      type: 'category',
      data: items?.map(i => i.name) || [],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { interval: 0, fontSize: 10, color: subTextColor }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: splitLineColor } }, axisLabel: { color: subTextColor } },
    series: [{
      type: 'bar',
      data: items?.map(i => i.value) || [],
      barWidth: 18,
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: color },
            { offset: 1, color: color + '33' }
          ]
        },
        borderRadius: [8, 8, 0, 0]
      },
      label: { show: true, position: 'top', color: subTextColor, fontSize: 10, fontWeight: 600 },
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: color + '55' } }
    }],
  }
}

// ============ 薪资区间：柱状图 ============
const salaryOption = computed(() => {
  const items = data.value.salaryDistribution || []
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: (p: any) => `${p[0].name}<br/>${p[0].marker} ${p[0].value} 人` },
    grid: { left: 10, right: 10, top: 20, bottom: 5, containLabel: true },
    xAxis: {
      type: 'category',
      data: items.map(i => i.name),
      axisLabel: { interval: 0, fontSize: 10, rotate: items.length > 5 ? 20 : 0, color: subTextColor },
      axisLine: { show: false },
      axisTick: { show: false }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: splitLineColor } }, axisLabel: { color: subTextColor } },
    series: [{
      type: 'bar',
      data: items.map(i => i.value),
      barWidth: 22,
      itemStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
          { offset: 0, color: '#f43f5e' },
          { offset: 1, color: '#fbbf24' }
        ] },
        borderRadius: [10, 10, 0, 0],
      },
      label: { show: true, position: 'top', color: subTextColor, fontSize: 10, fontWeight: 600 },
      emphasis: { itemStyle: { shadowBlur: 15, shadowColor: 'rgba(244, 63, 94, 0.35)' } }
    }],
  }
})

// ============ 部门薪资统计：双 Y 轴 ============
const deptSalaryOption = computed(() => {
  const items = data.value.departmentSalary || []
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['人数', '平均薪资'], top: 0, itemGap: 20, textStyle: { color: subTextColor, fontSize: 11 } },
    grid: { left: 10, right: 10, top: 36, bottom: 5, containLabel: true },
    xAxis: {
      type: 'category',
      data: items.map(i => i.department),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { interval: 0, rotate: items.length > 6 ? 25 : 0, color: subTextColor, fontSize: 10 },
    },
    yAxis: [
      { type: 'value', name: '人数', position: 'left', splitLine: { lineStyle: { type: 'dashed', color: splitLineColor } }, axisLabel: { color: subTextColor }, nameTextStyle: { color: subTextColor } },
      { type: 'value', name: '平均薪资', position: 'right', splitLine: { show: false }, axisLabel: { formatter: (v: number) => '¥' + (v / 1000).toFixed(0) + 'k', color: subTextColor }, nameTextStyle: { color: subTextColor } },
    ],
    series: [
      {
        name: '人数', type: 'bar', data: items.map(i => i.headcount),
        barWidth: 16,
        itemStyle: {
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
            { offset: 0, color: '#6366f1' },
            { offset: 1, color: '#6366f133' }
          ] },
          borderRadius: [6, 6, 0, 0]
        },
      },
      {
        name: '平均薪资', type: 'line', yAxisIndex: 1, data: items.map(i => i.avgSalary),
        smooth: true, symbol: 'circle', symbolSize: 8,
        lineStyle: { width: 3, color: '#f43f5e', shadowBlur: 10, shadowColor: 'rgba(244, 63, 94, 0.3)', shadowOffsetY: 5 },
        itemStyle: { color: '#f43f5e', borderColor: '#fff', borderWidth: 2 },
      },
    ],
  }
})
</script>

<style scoped>
.dashboard-page {
  min-height: calc(100vh - 56px);
  overflow-y: auto;
}

/* 顶部 KPI */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.kpi-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  background: var(--bg-elevated);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s var(--ease-out);
  overflow: hidden;
  cursor: default;
}

.kpi-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
  border-color: transparent;
}

.kpi-glow {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0.6;
  transition: opacity 0.3s;
}

.kpi-card:hover .kpi-glow {
  opacity: 1;
}

.kpi-card.kpi-primary { --accent: #6366f1; --accent-soft: rgba(99, 102, 241, 0.1); }
.kpi-card.kpi-success { --accent: #10b981; --accent-soft: rgba(16, 185, 129, 0.1); }
.kpi-card.kpi-warning { --accent: #f59e0b; --accent-soft: rgba(245, 158, 11, 0.1); }
.kpi-card.kpi-danger  { --accent: #f43f5e; --accent-soft: rgba(244, 63, 94, 0.1); }

.kpi-icon {
  position: relative;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 21px;
  color: #fff;
  background: var(--accent);
  box-shadow: 0 6px 14px -4px var(--accent);
  flex-shrink: 0;
  transition: transform 0.3s var(--ease-spring);
}

.kpi-card:hover .kpi-icon {
  transform: scale(1.06) rotate(-4deg);
}

.kpi-info { flex: 1; min-width: 0; position: relative; }

.kpi-label {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-bottom: 4px;
  font-weight: 500;
  letter-spacing: 0.2px;
}

.kpi-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.1;
  letter-spacing: -0.02em;
  font-feature-settings: 'tnum';
  background: linear-gradient(135deg, var(--text-primary) 0%, var(--text-regular) 100%);
  -webkit-background-clip: text;
  background-clip: text;
}

.kpi-sub {
  font-size: 11.5px;
  color: var(--text-tertiary);
  margin-top: 6px;
  font-weight: 500;
}

.kpi-anim {
  animation: kpiIn 0.6s var(--ease-out) both;
}

@keyframes kpiIn {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* 图表卡片 */
.chart-row { margin: 0 !important; }
.chart-card {
  border-radius: var(--radius-lg) !important;
  border: 1px solid var(--border-subtle) !important;
}
.chart-card :deep(.el-card__header) { padding: 12px 16px; }
.chart-card :deep(.el-card__body) { padding: 12px 16px; }

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.chart-title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--text-primary);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  letter-spacing: -0.01em;
}

.chart-title-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  position: relative;
}
.chart-title-dot::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: inherit;
  opacity: 0.18;
  filter: blur(2px);
  z-index: -1;
}

.chart-sub {
  font-size: 11.5px;
  color: var(--text-tertiary);
  font-weight: 500;
}
</style>
