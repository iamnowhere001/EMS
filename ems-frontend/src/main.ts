import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

// ECharts 按需注册（仅注册我们用到的图表类型，减小打包体积）
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart, RadarChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  DataZoomComponent,
  ToolboxComponent,
  MarkLineComponent,
} from 'echarts/components'
import VChart, { THEME_KEY } from 'vue-echarts'

// 权限指令
import permissionDirective from './directives/permission'
import { hasPermission, isAdmin } from './utils/permission'

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  RadarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  DataZoomComponent,
  ToolboxComponent,
  MarkLineComponent,
])

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.component('VChart', VChart)
app.provide(THEME_KEY, 'light')

// 注册权限指令
app.directive('permission', permissionDirective)

// 全局属性：模板中可用 $hasPermission('employee:create') 判断
app.config.globalProperties.$hasPermission = hasPermission
app.config.globalProperties.$isAdmin = isAdmin

app.mount('#app')
