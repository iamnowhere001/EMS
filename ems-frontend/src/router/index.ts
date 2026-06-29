import { createRouter, createWebHistory } from 'vue-router'
import EmployeeView from '../views/EmployeeView.vue'
import LoginView from '../views/LoginView.vue'
import OrganizationView from '../views/OrganizationView.vue'
import { STORAGE_KEYS } from '@/utils/constants'
import { currentUser, hasPermission, loadUserState } from '@/utils/permission'

const getAccessibleHome = () => {
  loadUserState()
  // 根据用户权限智能重定向到第一个可访问的页面
  if (currentUser.value?.role === 'EMPLOYEE' && hasPermission('menu:personal')) return '/personal'
  if (hasPermission('menu:employee')) return '/employee'
  if (hasPermission(['menu:dashboard', 'dashboard:view'])) return '/dashboard'
  if (hasPermission('menu:personal')) return '/personal'
  if (hasPermission(['menu:workflow', 'workflow:view', 'employee:edit', 'salary:manage'])) return '/workflow'
  if (hasPermission('menu:attendance')) return '/attendance'
  if (hasPermission('menu:leave')) return '/leave'
  if (hasPermission('menu:salary')) return '/salary'
  return '/personal'
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true },
    },
    {
      path: '/',
      redirect: getAccessibleHome,
    },
    {
      path: '/employee',
      name: 'employee',
      component: EmployeeView,
      meta: { permission: 'menu:employee' },
    },
    {
      path: '/organization',
      name: 'organization',
      component: OrganizationView,
      meta: { permission: 'menu:organization' },
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { permission: ['menu:dashboard', 'dashboard:view'] },
    },
    {
      path: '/attendance',
      name: 'attendance',
      component: () => import('../views/AttendanceView.vue'),
      meta: { permission: 'menu:attendance' },
    },
    {
      path: '/workflow',
      name: 'workflow',
      component: () => import('../views/WorkflowView.vue'),
      meta: { permission: ['menu:workflow', 'workflow:view', 'employee:edit', 'salary:manage'] },
    },
    {
      path: '/salary',
      name: 'salary',
      component: () => import('../views/SalaryView.vue'),
      meta: { permission: 'menu:salary' },
    },
    {
      path: '/leave',
      name: 'leave',
      component: () => import('../views/LeaveView.vue'),
      meta: { permission: 'menu:leave' },
    },
    {
      path: '/personal',
      name: 'personal',
      component: () => import('../views/PersonalCenterView.vue'),
      meta: { permission: 'menu:personal' },
    },
    {
      path: '/system',
      name: 'system',
      component: () => import('../views/SystemView.vue'),
      meta: { permission: 'menu:system' },
    },
    {
      path: '/role-manage',
      name: 'role-manage',
      component: () => import('../views/RoleManageView.vue'),
      meta: { permission: 'system:role' },
    },
  ],
})

router.beforeEach((to) => {
  loadUserState()
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
  if (to.meta.public) {
    return true
  }
  if (!token) {
    return '/login'
  }
  if (to.meta.permission && !hasPermission(to.meta.permission as string | string[])) {
    return getAccessibleHome()
  }
  return true
})

export default router
