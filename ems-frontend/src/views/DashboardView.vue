<template>
  <div class="dashboard-page">
    <!-- 顶部 KPI -->
    <div class="kpi-row">
      <div class="kpi-card kpi-primary">
        <div class="kpi-icon">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="kpi-info">
          <div class="kpi-label">员工总数</div>
          <div class="kpi-value">{{ data.kpi?.totalCount ?? 0 }}</div>
          <div class="kpi-sub">在职 {{ data.kpi?.activeCount ?? 0 }} · 离职 {{ data.kpi?.leftCount ?? 0 }}</div>
        </div>
      </div>
      <div class="kpi-card kpi-success">
        <div class="kpi-icon"><el-icon><CircleCheckFilled /></el-icon></div>
        <div class="kpi-info">
          <div class="kpi-label">本月入职</div>
          <div class="kpi-value">{{ data.kpi?.newHireThisMonth ?? 0 }}</div>
          <div class="kpi-sub">近 12 月趋势见下方</div>
        </div>
      </div>
      <div class="kpi-card kpi-warning">
        <div class="kpi-icon"><el-icon><OfficeBuilding /></el-icon></div>
        <div class="kpi-info">
          <div class="kpi-label">部门数量</div>
          <div class="kpi-value">{{ data.kpi?.departmentCount ?? 0 }}</div>
          <div class="kpi-sub">平均工龄 {{ data.kpi?.avgTenureYears ?? 0 }} 年</div>
        </div>
      </div>
      <div class="kpi-card kpi-danger">
        <div class="kpi-icon"><el-icon><Money /></el-icon></div>
        <div class="kpi-info">
          <div class="kpi-label">月度薪资</div>
          <div class="kpi-value">¥ {{ formatNum(data.kpi?.totalSalary) }}</div>
          <div class="kpi-sub">平均 ¥ {{ formatNum(data.kpi?.avgSalary) }} / 人</div>
        </div>
      </div>
    </div>

    <!-- 第一行：核心分布与趋势 -->
    <el-row :gutter="10" class="chart-row">
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">部门人员分布</span>
              <el-tag size="small" type="info" effect="plain">在职员工</el-tag>
            </div>
          </template>
          <v-chart :option="deptOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">月度入职趋势</span>
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
              <span class="chart-title">性别比例</span>
            </div>
          </template>
          <v-chart :option="genderOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：薪资与职级 -->
    <el-row :gutter="10" class="chart-row">
      <el-col :span="14">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">部门薪资统计</span>
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
              <span class="chart-title">薪资区间分布</span>
            </div>
          </template>
          <v-chart :option="salaryOption" autoresize style="height: 240px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行：员工画像 -->
    <el-row :gutter="10" class="chart-row">
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">年龄分布</span>
            </div>
          </template>
          <v-chart :option="ageOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">工龄分布</span>
            </div>
          </template>
          <v-chart :option="tenureOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">学历分布</span>
            </div>
          </template>
          <v-chart :option="educationOption" autoresize style="height: 220px" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="chart-title">职级分布</span>
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

// ============ 通用配色 ============
const COLORS = ['#5B8FF9', '#5AD8A6', '#FFD666', '#F4664A', '#9270CA', '#6DC8EC', '#FF99C3', '#269A99', '#FF9D4D', '#A1A0F8']
const genderColors = ['#5B8FF9', '#F4664A']

// ============ 部门分布：横向条形图 ============
const deptOption = computed(() => {
  const items = data.value.departmentDistribution || []
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 80, right: 40, top: 10, bottom: 10, containLabel: true },
    xAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }, axisLabel: { color: '#94a3b8' } },
    yAxis: { type: 'category', data: items.map(i => i.name), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#475569', fontWeight: 500 } },
    series: [{
      type: 'bar',
      data: items.map((i, idx) => ({
        value: i.value,
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: COLORS[idx % COLORS.length] + '88' },
              { offset: 1, color: COLORS[idx % COLORS.length] }
            ]
          },
          borderRadius: [0, 10, 10, 0]
        }
      })),
      label: { show: true, position: 'right', color: '#64748b', fontSize: 10, fontWeight: 600 },
      barWidth: 12,
      showBackground: true,
      backgroundStyle: { color: 'rgba(180, 180, 180, 0.1)', borderRadius: [0, 10, 10, 0] }
    }],
  }
})

// ============ 性别比例：饼图 ============
const genderOption = computed(() => {
  const items = data.value.genderRatio || []
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, icon: 'circle' },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{c} 人' },
      data: items.map((i, idx) => ({ name: i.name, value: i.value, itemStyle: { color: genderColors[idx % genderColors.length] } })),
    }],
  }
})

// ============ 月度入职趋势：折线 + 柱状图 ============
const trendOption = computed(() => {
  const items = data.value.monthlyTrend || []
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['入职人数'], top: 0 },
    grid: { left: 40, right: 20, top: 36, bottom: 22, containLabel: true },
    xAxis: { type: 'category', data: items.map(i => i.month.substring(5)), axisLine: { lineStyle: { color: '#ddd' } } },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
    series: [
      {
        name: '入职人数',
        type: 'line',
        data: items.map(i => i.onboardCount),
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 3, color: '#5B8FF9' },
        itemStyle: { color: '#5B8FF9' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
          { offset: 0, color: 'rgba(91,143,249,0.4)' },
          { offset: 1, color: 'rgba(91,143,249,0.02)' },
        ] } },
        markPoint: { symbol: 'pin', symbolSize: 36, data: [{ type: 'max', name: '最大' }] },
      },
    ],
  }
})

// ============ 职级分布：饼图（玫瑰图） ============
const rankOption = computed(() => {
  const items = data.value.rankDistribution || []
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} 人 ({d}%)' },
    legend: { type: 'scroll', orient: 'horizontal', bottom: 0, icon: 'circle' },
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
        color: '#303133',
      },
      labelLine: { show: true, length: 8, length2: 8 },
      labelLayout: { hideOverlap: false },
      data: items.map((i, idx) => ({ name: i.rank, value: i.count, itemStyle: { color: COLORS[idx % COLORS.length] } })),
    }],
  }
})

// ============ 年龄分布：柱状图 ============
const ageOption = computed(() => buildBarOption(data.value.ageDistribution, '#5AD8A6'))

// ============ 工龄分布：柱状图 ============
const tenureOption = computed(() => buildBarOption(data.value.tenureDistribution, '#FFD666'))

// ============ 学历分布：柱状图 ============
const educationOption = computed(() => buildBarOption(data.value.educationDistribution, '#9270CA'))

function buildBarOption(items: NameValue[], color: string) {
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 10, right: 10, top: 20, bottom: 5, containLabel: true },
    xAxis: {
      type: 'category',
      data: items?.map(i => i.name) || [],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { interval: 0, fontSize: 10, color: '#64748b' }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }, axisLabel: { color: '#94a3b8' } },
    series: [{
      type: 'bar',
      data: items?.map(i => i.value) || [],
      barWidth: 16,
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: color },
            { offset: 1, color: color + '44' }
          ]
        },
        borderRadius: [8, 8, 0, 0]
      },
      label: { show: true, position: 'top', color: '#64748b', fontSize: 10, fontWeight: 600 },
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.1)' } }
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
      axisLabel: { interval: 0, fontSize: 10, rotate: items.length > 5 ? 20 : 0, color: '#64748b' },
      axisLine: { show: false },
      axisTick: { show: false }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }, axisLabel: { color: '#94a3b8' } },
    series: [{
      type: 'bar',
      data: items.map(i => i.value),
      barWidth: 20,
      itemStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
          { offset: 0, color: '#F4664A' },
          { offset: 1, color: '#FFD666' }
        ] },
        borderRadius: [10, 10, 0, 0],
      },
      label: { show: true, position: 'top', color: '#64748b', fontSize: 10, fontWeight: 600 },
      emphasis: { itemStyle: { shadowBlur: 15, shadowColor: 'rgba(244, 102, 74, 0.3)' } }
    }],
  }
})

// ============ 部门薪资统计：双 Y 轴（人头+平均薪资） ============
const deptSalaryOption = computed(() => {
  const items = data.value.departmentSalary || []
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['人数', '平均薪资'], top: 0, itemGap: 20, textStyle: { color: '#64748b', fontSize: 11 } },
    grid: { left: 10, right: 10, top: 40, bottom: 5, containLabel: true },
    xAxis: {
      type: 'category',
      data: items.map(i => i.department),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { interval: 0, rotate: items.length > 6 ? 25 : 0, color: '#64748b', fontSize: 10 },
    },
    yAxis: [
      { type: 'value', name: '人数', position: 'left', splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } }, axisLabel: { color: '#94a3b8' }, nameTextStyle: { color: '#94a3b8' } },
      { type: 'value', name: '平均薪资', position: 'right', splitLine: { show: false }, axisLabel: { formatter: (v: number) => '¥' + (v / 1000).toFixed(0) + 'k', color: '#94a3b8' }, nameTextStyle: { color: '#94a3b8' } },
    ],
    series: [
      {
        name: '人数', type: 'bar', data: items.map(i => i.headcount),
        barWidth: 14,
        itemStyle: {
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
            { offset: 0, color: '#5B8FF9' },
            { offset: 1, color: '#5B8FF944' }
          ] },
          borderRadius: [6, 6, 0, 0]
        },
      },
      {
        name: '平均薪资', type: 'line', yAxisIndex: 1, data: items.map(i => i.avgSalary),
        smooth: true, symbol: 'circle', symbolSize: 8,
        lineStyle: { width: 3, color: '#F4664A', shadowBlur: 10, shadowColor: 'rgba(244, 102, 74, 0.3)', shadowOffsetY: 5 },
        itemStyle: { color: '#F4664A', borderColor: '#fff', borderWidth: 2 },
      },
    ],
  }
})
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  height: calc(100vh - 60px);
  overflow-y: auto;
  box-sizing: border-box;
}

html.dark .dashboard-page {
  background: linear-gradient(135deg, #141414 0%, #1a1a1a 100%);
}

/* 顶部 KPI */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.kpi-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}
.kpi-card::before {
  content: '';
  position: absolute;
  inset: 0 0 0 auto;
  width: 3px;
  background: var(--accent);
}
.kpi-card.kpi-primary { --accent: #5B8FF9; }
.kpi-card.kpi-success { --accent: #5AD8A6; }
.kpi-card.kpi-warning { --accent: #FFD666; }
.kpi-card.kpi-danger  { --accent: #F4664A; }
.kpi-card:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.06); }
.kpi-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  font-size: 20px;
  color: #fff;
  background: var(--accent);
}
.kpi-info { flex: 1; min-width: 0; }
.kpi-label { font-size: 11px; color: #909399; margin-bottom: 1px; }
.kpi-value { font-size: 20px; font-weight: 700; color: #303133; line-height: 1.1; }
.kpi-sub { font-size: 10px; color: #909399; margin-top: 2px; }

/* 图表卡片 */
.chart-row { margin: 0 !important; gap: 0; }
.chart-card { border-radius: 8px; }
.chart-card :deep(.el-card__header) { padding: 8px 12px; }
.chart-card :deep(.el-card__body) { padding: 10px; }
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.chart-title { font-size: 13px; font-weight: 600; color: #303133; }
.chart-sub { font-size: 10px; color: #909399; }
</style>
