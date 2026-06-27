import { createRouter, createWebHistory } from 'vue-router'
import EmployeeView from '../views/EmployeeView.vue'
import LoginView from '../views/LoginView.vue'
import OrganizationView from '../views/OrganizationView.vue'
import { STORAGE_KEYS } from '@/utils/constants'

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
      redirect: '/employee',
    },
    {
      path: '/employee',
      name: 'employee',
      component: EmployeeView,
    },
    {
      path: '/organization',
      name: 'organization',
      component: OrganizationView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
    },
    {
      path: '/attendance',
      name: 'attendance',
      component: () => import('../views/AttendanceView.vue'),
    },
    {
      path: '/salary',
      name: 'salary',
      component: () => import('../views/SalaryView.vue'),
    },
    {
      path: '/system',
      name: 'system',
      component: () => import('../views/SystemView.vue'),
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
  if (to.meta.public) {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
