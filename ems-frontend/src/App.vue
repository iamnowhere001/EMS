<template>
  <div class="app-wrapper" :class="{ dark: isDark }">
    <el-container class="app-container">
      <!-- 侧边栏 -->
      <el-aside
        v-if="!isLoginPage"
        class="app-sidebar"
        :class="{ collapsed: isCollapsed }"
        :width="isCollapsed ? '64px' : '220px'"
      >
        <div class="sidebar-header">
          <div class="logo-box">
            <el-icon class="logo-icon"><Management /></el-icon>
          </div>
          <div v-show="!isCollapsed" class="logo-text">
            <div class="logo-title">员工管理系统</div>
            <div class="logo-subtitle">EMS</div>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/employee">
            <el-icon><UserFilled /></el-icon>
            <template #title>员工管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <template #title>数据看板</template>
          </el-menu-item>
          <el-menu-item index="/workflow">
            <el-icon><Document /></el-icon>
            <template #title>入转调离</template>
          </el-menu-item>
          <el-menu-item index="/attendance">
            <el-icon><Clock /></el-icon>
            <template #title>考勤管理</template>
          </el-menu-item>
          <el-menu-item index="/salary">
            <el-icon><Money /></el-icon>
            <template #title>薪资管理</template>
          </el-menu-item>
          <el-menu-item index="/social-security">
            <el-icon><Wallet /></el-icon>
            <template #title>社保公积金</template>
          </el-menu-item>
          <el-menu-item v-if="isAdmin()" index="/dictionary">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>组织架构</template>
          </el-menu-item>
          <el-menu-item v-if="isAdmin()" index="/user">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
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
            <span class="page-title">{{ pageTitle }}</span>
          </div>
          <div class="header-actions">
            <el-tooltip :content="isDark ? '切换浅色模式' : '切换暗黑模式'" placement="bottom">
              <div class="theme-toggle" @click="toggleTheme">
                <el-icon v-if="isDark" class="theme-icon"><Sunny /></el-icon>
                <el-icon v-else class="theme-icon"><Moon /></el-icon>
              </div>
            </el-tooltip>
            <el-badge is-dot class="notice-badge">
              <el-icon class="notice-icon"><Bell /></el-icon>
            </el-badge>
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
import { Management, UserFilled, Bell, ArrowDown, SwitchButton, Sunny, Moon, OfficeBuilding, Key, DataLine, User, Document, Clock, Money, Wallet, Expand, Fold } from '@element-plus/icons-vue'
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
    '/dashboard': '数据看板',
    '/workflow': '入转调离',
    '/attendance': '考勤管理',
    '/salary': '薪资管理',
    '/social-security': '社保公积金',
    '/dictionary': '组织架构',
    '/user': '用户管理',
    '/login': '登录',
  }
  return map[route.path] || '员工管理系统'
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
  background-color: #f5f7fa;
}

.app-wrapper.dark {
  background-color: #141414;
}

.app-container {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.app-sidebar {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.08);
}

.app-sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6, #06b6d4);
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  min-height: 72px;
  box-sizing: border-box;
  transition: padding 0.3s ease;
}

.app-sidebar.collapsed .sidebar-header {
  padding: 22px 14px;
  justify-content: center;
}

.logo-box {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-box:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.5);
}

.logo-icon {
  font-size: 22px;
  color: #fff;
  transition: transform 0.3s ease;
}

.logo-box:hover .logo-icon {
  transform: rotate(5deg);
}

.logo-text {
  overflow: hidden;
  opacity: 1;
  transition: opacity 0.25s ease;
}

.logo-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
  white-space: nowrap;
  background: linear-gradient(90deg, #fff 0%, #94a3b8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtitle {
  font-size: 11px;
  color: rgba(148, 163, 184, 0.8);
  margin-top: 3px;
  font-weight: 500;
  letter-spacing: 1px;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 12px 10px;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 2px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.25);
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(226, 232, 240, 0.7);
  height: 46px;
  line-height: 46px;
  margin: 4px 0;
  border-radius: 10px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  padding: 0 14px !important;
}

.sidebar-menu :deep(.el-menu-item::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 3px;
  height: 20px;
  background: linear-gradient(180deg, #3b82f6, #8b5cf6);
  border-radius: 0 3px 3px 0;
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  transform: translateX(2px);
}

.sidebar-menu :deep(.el-menu-item:hover::before) {
  transform: translateY(-50%) scaleY(0.6);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.15) 0%, rgba(139, 92, 246, 0.1) 100%);
  color: #fff;
  font-weight: 600;
}

.sidebar-menu :deep(.el-menu-item.is-active::before) {
  transform: translateY(-50%) scaleY(1);
  height: 24px;
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #60a5fa;
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 19px;
  margin-right: 12px;
  transition: all 0.25s ease;
  color: rgba(148, 163, 184, 0.8);
}

.sidebar-menu :deep(.el-menu-item:hover .el-icon) {
  color: #fff;
  transform: scale(1.1);
}

.sidebar-menu.el-menu--collapse :deep(.el-menu-item .el-icon) {
  margin-right: 0;
}

.sidebar-menu.el-menu--collapse :deep(.el-menu-item) {
  justify-content: center;
  padding: 0 !important;
}

.sidebar-menu.el-menu--collapse :deep(.el-menu-item .el-menu-item__title) {
  display: none;
}

.sidebar-footer {
  padding: 12px 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  transition: padding 0.3s ease;
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  border-radius: 10px;
  color: rgba(148, 163, 184, 0.8);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 13px;
  font-weight: 500;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  transform: translateX(2px);
}

.collapse-btn .el-icon {
  font-size: 16px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.collapse-btn:hover .el-icon {
  transform: translateX(-2px);
}

.app-sidebar.collapsed .collapse-btn:hover .el-icon {
  transform: translateX(2px);
}

.app-sidebar.collapsed .collapse-btn {
  padding: 10px;
  justify-content: center;
}

/* 主容器 */
.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
}

/* 顶部栏 */
.app-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
  height: 56px;
}

.app-wrapper.dark .app-header {
  background: #1d1e1f;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.app-wrapper.dark .page-title {
  color: #e5eaf3;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.theme-toggle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s;
}

.app-wrapper.dark .theme-toggle {
  background: #333;
}

.theme-toggle:hover {
  background: #e4e7ed;
  transform: scale(1.05);
}

.theme-icon {
  font-size: 18px;
}

.notice-badge {
  cursor: pointer;
}

.notice-icon {
  font-size: 20px;
  opacity: 0.7;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 24px;
  background: #f0f2f5;
  cursor: pointer;
  white-space: nowrap;
}

.app-wrapper.dark .user-card {
  background: #333;
}

.user-avatar {
  background-color: #409eff;
  color: #fff;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.app-wrapper.dark .user-name {
  color: #e5eaf3;
}

.user-role {
  font-size: 11px;
  color: #909399;
}

.app-wrapper.dark .user-role {
  color: #a3a6ad;
}

.arrow-icon {
  font-size: 12px;
  opacity: 0.5;
  margin-left: 4px;
}

.app-main {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.app-wrapper.dark .app-main {
  background: #141414;
}

/* 路由切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease-out;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>
