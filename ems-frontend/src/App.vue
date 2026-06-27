<template>
  <div class="app-wrapper" :class="{ dark: isDark }">
    <el-container class="app-container">
      <!-- 侧边栏 -->
      <el-aside
        v-if="!isLoginPage"
        class="app-sidebar"
        :class="{ collapsed: isCollapsed }"
        :width="isCollapsed ? '72px' : '232px'"
      >
        <div class="sidebar-header">
          <div class="logo-box">
            <el-icon class="logo-icon"><Management /></el-icon>
          </div>
          <div v-show="!isCollapsed" class="logo-text">
            <div class="logo-title">员工管理系统</div>
            <div class="logo-subtitle">EMS · WORKSPACE</div>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :collapse-transition="false"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <div class="menu-group" v-show="!isCollapsed">
            <span class="menu-group-title">工作台</span>
          </div>
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <template #title>数据看板</template>
          </el-menu-item>
          <el-menu-item index="/employee">
            <el-icon><UserFilled /></el-icon>
            <template #title>员工管理</template>
          </el-menu-item>
          <el-menu-item index="/organization">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>组织设置</template>
          </el-menu-item>

          <div class="menu-group" v-show="!isCollapsed">
            <span class="menu-group-title">人事考勤</span>
          </div>
          <el-menu-item index="/attendance">
            <el-icon><Clock /></el-icon>
            <template #title>考勤管理</template>
          </el-menu-item>

          <div class="menu-group" v-show="!isCollapsed">
            <span class="menu-group-title">薪酬福利</span>
          </div>
          <el-menu-item index="/salary">
            <el-icon><Money /></el-icon>
            <template #title>薪资社保</template>
          </el-menu-item>

          <div class="menu-group" v-show="!isCollapsed">
            <span class="menu-group-title">系统管理</span>
          </div>
          <el-menu-item v-if="isAdmin()" index="/system">
            <el-icon><Setting /></el-icon>
            <template #title>系统设置</template>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-footer">
          <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <el-icon>
              <Expand v-if="isCollapsed" />
              <Fold v-else />
            </el-icon>
            <span v-show="!isCollapsed">{{ isCollapsed ? '展开' : '收起' }}</span>
          </div>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="main-container">
        <!-- 顶部栏 -->
        <el-header v-if="!isLoginPage" class="app-header">
          <div class="header-left">
            <div class="breadcrumb-wrap">
              <span class="breadcrumb-icon">
                <el-icon><Menu /></el-icon>
              </span>
              <span class="page-title">{{ pageTitle }}</span>
              <span class="breadcrumb-sep">/</span>
              <span class="page-breadcrumb">{{ pageSubtitle }}</span>
            </div>
          </div>
          <div class="header-actions">
            <el-tooltip :content="isDark ? '切换浅色模式' : '切换暗黑模式'" placement="bottom">
              <div class="header-icon-btn" @click="toggleTheme">
                <el-icon v-if="isDark" class="icon"><Sunny /></el-icon>
                <el-icon v-else class="icon"><Moon /></el-icon>
              </div>
            </el-tooltip>
            <el-badge is-dot class="notice-badge" :hidden="true">
              <div class="header-icon-btn">
                <el-icon class="icon"><Bell /></el-icon>
              </div>
            </el-badge>
            <div class="header-divider"></div>
            <el-dropdown @command="handleCommand">
              <div class="user-card">
                <el-avatar :size="34" :icon="UserFilled" class="user-avatar" />
                <div class="user-info">
                  <div class="user-name">{{ userInfo?.nickname || userInfo?.username || '管理员' }}</div>
                  <div class="user-role">{{ userInfo?.role === 'ADMIN' ? '系统管理员' : '普通用户' }}</div>
                </div>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="changePassword">
                    <el-icon><Key /></el-icon> 修改密码
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="app-main">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>

    <ChangePasswordDialog v-model:visible="passwordDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Management, UserFilled, Bell, ArrowDown, SwitchButton, Sunny, Moon, Key, DataLine, Clock, Money, Expand, Fold, Setting, Menu, OfficeBuilding } from '@element-plus/icons-vue'
import { useTheme } from '@/composables/useTheme'
import { isAdmin } from '@/utils/auth'
import { STORAGE_KEYS } from '@/utils/constants'
import { authApi } from '@/api/auth'
import ChangePasswordDialog from '@/components/ChangePasswordDialog.vue'

const route = useRoute()
const router = useRouter()
const { isDark, toggleTheme } = useTheme()

const isLoginPage = computed(() => route.path === '/login')
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const map: Record<string, string> = {
    '/employee': '员工管理',
    '/organization': '组织设置',
    '/dashboard': '数据看板',
    '/attendance': '考勤管理',
    '/salary': '薪资社保',
    '/system': '系统设置',
    '/login': '登录',
  }
  return map[route.path] || '员工管理系统'
})

const pageSubtitle = computed(() => {
  const map: Record<string, string> = {
    '/employee': '人员档案',
    '/organization': '部门 · 岗位 · 职级',
    '/dashboard': '核心指标',
    '/attendance': '签到打卡',
    '/salary': '薪资结算 · 社保配置',
    '/system': '用户管理 · 操作日志',
  }
  return map[route.path] || '首页'
})

interface UserInfo {
  id: number
  username: string
  nickname: string
  role: string
  token: string
}

const userInfo = ref<UserInfo | null>(null)

const loadUserInfo = () => {
  const raw = localStorage.getItem(STORAGE_KEYS.USER)
  userInfo.value = raw ? JSON.parse(raw) : null
}

loadUserInfo()

watch(
  () => route.path,
  () => loadUserInfo()
)

const handleMenuSelect = (index: string) => {
  router.push(index)
}

const handleCommand = (command: string) => {
  if (command === 'changePassword') {
    passwordDialogVisible.value = true
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
      .then(async () => {
        const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN) || undefined
        try {
          await authApi.logout(refreshToken)
        } catch (e) {
          console.warn('退出登录接口异常', e)
        }
        localStorage.removeItem(STORAGE_KEYS.TOKEN)
        localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN)
        localStorage.removeItem(STORAGE_KEYS.USER)
        ElMessage.success('已退出登录')
        router.push('/login')
      })
      .catch(() => {})
  }
}

const passwordDialogVisible = ref(false)
</script>

<style scoped>
.app-wrapper {
  min-height: 100vh;
  background-color: var(--bg-page);
  transition: background-color var(--dur-slow) var(--ease-out);
}

.app-container {
  height: 100vh;
  overflow: hidden;
}

/* ============================ 侧边栏 ============================ */
.app-sidebar {
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width 0.32s var(--ease-out), background 0.3s ease;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
  box-shadow: var(--sidebar-shadow);
  z-index: 2;
  border-right: 1px solid var(--sidebar-border);
}

.app-sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--brand-500), var(--violet-500), var(--sky-500));
  opacity: 0.9;
  z-index: 1;
}

.app-wrapper.dark .app-sidebar {
  background:
    radial-gradient(at 100% 0%, rgba(99, 102, 241, 0.15) 0px, transparent 45%),
    radial-gradient(at 0% 100%, rgba(139, 92, 246, 0.1) 0px, transparent 50%),
    linear-gradient(180deg, #0f172a 0%, #0b1120 100%);
  border-right: 1px solid rgba(255,255,255,0.06);
  box-shadow: 4px 0 24px -4px rgba(15, 23, 42, 0.22);
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 16px;
  border-bottom: 1px solid var(--sidebar-border);
  min-height: 64px;
  box-sizing: border-box;
  transition: padding 0.32s var(--ease-out), border-color 0.3s ease;
  position: relative;
  z-index: 1;
}

.app-wrapper.dark .sidebar-header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.app-sidebar.collapsed .sidebar-header {
  padding: 18px 14px;
  justify-content: center;
}

.logo-box {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--brand-500) 0%, var(--violet-500) 100%);
  box-shadow:
    0 6px 14px -4px rgba(99, 102, 241, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s var(--ease-spring);
  position: relative;
}

.logo-box::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--brand-500), var(--violet-500));
  filter: blur(10px);
  opacity: 0.3;
  z-index: -1;
}

.logo-box:hover {
  transform: scale(1.06) rotate(-3deg);
  box-shadow:
    0 8px 20px -4px rgba(99, 102, 241, 0.6),
    inset 0 1px 0 rgba(255, 255, 255, 0.22);
}

.logo-icon {
  font-size: 20px;
  color: #fff;
  transition: transform 0.3s var(--ease-spring);
}

.logo-box:hover .logo-icon {
  transform: rotate(8deg);
}

.logo-text {
  overflow: hidden;
  opacity: 1;
  transition: opacity 0.2s var(--ease-out);
}

.logo-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--sidebar-text-primary);
  letter-spacing: -0.01em;
  white-space: nowrap;
  line-height: 1.2;
}

.app-wrapper.dark .logo-title {
  color: #fff;
}

.logo-subtitle {
  font-size: 9.5px;
  color: var(--sidebar-text-tertiary);
  margin-top: 3px;
  font-weight: 600;
  letter-spacing: 1.2px;
  white-space: nowrap;
  font-family: var(--font-mono);
}

.app-wrapper.dark .logo-subtitle {
  color: rgba(148, 163, 184, 0.6);
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 6px 8px 10px;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.sidebar-menu::-webkit-scrollbar {
  width: 3px;
}
.sidebar-menu::-webkit-scrollbar-track { background: transparent; }
.sidebar-menu::-webkit-scrollbar-thumb {
  background: var(--sidebar-scrollbar);
  border-radius: var(--radius-full);
}
.sidebar-menu::-webkit-scrollbar-thumb:hover { background: var(--sidebar-scrollbar-hover); }

.app-wrapper.dark .sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
}
.app-wrapper.dark .sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.18);
}

.menu-group {
  padding: 14px 10px 5px;
}

.menu-group-title {
  font-size: 10px;
  font-weight: 700;
  color: var(--sidebar-text-tertiary);
  letter-spacing: 1.2px;
  text-transform: uppercase;
  font-family: var(--font-mono);
  position: relative;
  padding-left: 8px;
}

.menu-group-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--brand-500);
  opacity: 0.7;
}

.app-wrapper.dark .menu-group-title {
  color: rgba(148, 163, 184, 0.45);
}

.sidebar-menu :deep(.el-menu-item) {
  color: var(--sidebar-text-secondary);
  height: 42px;
  line-height: 42px;
  margin: 2px 0;
  border-radius: 8px;
  transition: all 0.2s var(--ease-out);
  position: relative;
  overflow: hidden;
  padding: 0 12px !important;
  font-weight: 500;
  font-size: 13px;
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item) {
  color: rgba(203, 213, 225, 0.72);
}

.sidebar-menu :deep(.el-menu-item::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 3px;
  height: 16px;
  background: linear-gradient(180deg, var(--brand-400), var(--violet-500));
  border-radius: 0 3px 3px 0;
  transition: transform 0.22s var(--ease-out);
  opacity: 0;
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item::before) {
  opacity: 1;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: var(--sidebar-hover-bg);
  color: var(--sidebar-text-primary);
  transform: translateX(2px);
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item:hover::before) {
  transform: translateY(-50%) scaleY(0.6);
  opacity: 1;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: var(--sidebar-active-bg);
  color: var(--sidebar-active-text);
  font-weight: 600;
  box-shadow: var(--sidebar-active-shadow);
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(99, 102, 241, 0.22) 0%, rgba(139, 92, 246, 0.12) 100%);
  color: #fff;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.sidebar-menu :deep(.el-menu-item.is-active::before) {
  transform: translateY(-50%) scaleY(1);
  height: 20px;
  opacity: 1;
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: var(--sidebar-active-icon);
  filter: var(--sidebar-icon-glow);
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: var(--brand-300);
  filter: drop-shadow(0 0 6px rgba(99, 102, 241, 0.5));
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 17px;
  margin-right: 11px;
  transition: all 0.22s var(--ease-out);
  color: var(--sidebar-icon-color);
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item .el-icon) {
  color: rgba(148, 163, 184, 0.8);
}

.sidebar-menu :deep(.el-menu-item:hover .el-icon) {
  color: var(--brand-500);
  transform: scale(1.08);
}

.app-wrapper.dark .sidebar-menu :deep(.el-menu-item:hover .el-icon) {
  color: #fff;
  transform: scale(1.08);
}

.sidebar-menu.el-menu--collapse :deep(.el-menu-item .el-icon) { margin-right: 0; }
.sidebar-menu.el-menu--collapse :deep(.el-menu-item) { justify-content: center; padding: 0 !important; }
.sidebar-menu.el-menu--collapse :deep(.el-menu-item .el-menu-item__title) { display: none; }

.sidebar-footer {
  padding: 8px;
  border-top: 1px solid var(--sidebar-border);
  transition: padding 0.32s var(--ease-out), border-color 0.3s ease;
}

.app-wrapper.dark .sidebar-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 9px;
  border-radius: 8px;
  color: var(--sidebar-text-secondary);
  cursor: pointer;
  transition: all 0.22s var(--ease-out);
  font-size: 12px;
  font-weight: 500;
}

.app-wrapper.dark .collapse-btn {
  color: rgba(148, 163, 184, 0.75);
}

.collapse-btn:hover {
  background: var(--sidebar-hover-bg);
  color: var(--sidebar-text-primary);
  transform: translateX(2px);
}

.app-wrapper.dark .collapse-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.collapse-btn .el-icon {
  font-size: 14px;
  transition: transform 0.3s var(--ease-spring);
}

.collapse-btn:hover .el-icon { transform: translateX(-2px); }
.app-sidebar.collapsed .collapse-btn:hover .el-icon { transform: translateX(2px); }
.app-sidebar.collapsed .collapse-btn { padding: 9px; justify-content: center; }

/* ============================ 主容器 ============================ */
.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
  min-width: 0;
}

/* ============================ 顶部栏 ============================ */
.app-header {
  background: var(--bg-overlay);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  box-shadow: 0 1px 0 var(--border-subtle);
  flex-shrink: 0;
  height: 56px;
  position: relative;
  z-index: 1;
}

.app-wrapper.dark .app-header {
  background: rgba(10, 10, 15, 0.7);
  box-shadow: 0 1px 0 var(--border-default);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.breadcrumb-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.breadcrumb-icon {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all var(--dur-base) var(--ease-out);
}

.breadcrumb-icon:hover {
  background: var(--bg-soft);
  color: var(--brand-600);
}

.breadcrumb-icon .el-icon {
  font-size: 18px;
}

.page-title {
  font-size: 15.5px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.breadcrumb-sep {
  color: var(--text-tertiary);
  font-size: 13px;
  font-weight: 500;
}

.page-breadcrumb {
  font-size: 13px;
  color: var(--text-tertiary);
  font-weight: 500;
  letter-spacing: 0.1px;
}

.app-wrapper.dark .page-title { color: var(--text-primary); }

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon-btn {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--dur-base) var(--ease-out);
  position: relative;
}

.app-wrapper.dark .header-icon-btn {
  background: var(--bg-soft);
  border-color: var(--border-default);
}

.header-icon-btn:hover {
  background: var(--brand-50);
  border-color: var(--brand-200);
  color: var(--brand-600);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.app-wrapper.dark .header-icon-btn:hover {
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.3);
  color: var(--brand-300);
}

.header-icon-btn .icon {
  font-size: 17px;
  color: var(--text-secondary);
  transition: all var(--dur-base) var(--ease-out);
}

.header-icon-btn:hover .icon {
  color: var(--brand-600);
  transform: scale(1.08);
}

.app-wrapper.dark .header-icon-btn:hover .icon {
  color: var(--brand-300);
}

.header-divider {
  width: 1px;
  height: 24px;
  background: var(--border-default);
  margin: 0 4px;
}

.notice-badge {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 10px 4px 4px;
  border-radius: var(--radius-full);
  background: var(--bg-soft);
  border: 1px solid var(--border-subtle);
  cursor: pointer;
  white-space: nowrap;
  transition: all var(--dur-base) var(--ease-out);
}

.app-wrapper.dark .user-card {
  background: var(--bg-soft);
  border-color: var(--border-default);
}

.user-card:hover {
  background: var(--brand-50);
  border-color: var(--brand-200);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.app-wrapper.dark .user-card:hover {
  background: rgba(99, 102, 241, 0.12);
  border-color: rgba(99, 102, 241, 0.3);
}

.user-avatar {
  background: linear-gradient(135deg, var(--brand-500), var(--violet-500)) !important;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 2px 6px -2px rgba(99, 102, 241, 0.5);
}

.user-info {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.user-role {
  font-size: 10.5px;
  color: var(--text-tertiary);
  margin-top: 2px;
  letter-spacing: 0.2px;
}

.arrow-icon {
  font-size: 11px;
  opacity: 0.5;
  margin-left: 2px;
  color: var(--text-secondary);
  transition: transform var(--dur-base);
}
.user-card:hover .arrow-icon { transform: translateY(1px); }

.app-main {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: transparent;
}

/* ============================ 路由切换动画 ============================ */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.28s var(--ease-out), transform 0.28s var(--ease-out);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
