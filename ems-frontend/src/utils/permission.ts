/**
 * 前端权限工具
 * 使用响应式 ref 跟踪用户权限，确保菜单等 UI 在登录/登出后自动更新
 */
import { ref } from 'vue'

const STORAGE_KEY = 'ems_user'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  employeeId?: number
  role: string
  permissions: string[]
  token: string
  refreshToken: string
}

/** 响应式权限状态 */
export const currentUser = ref<UserInfo | null>(null)
export const currentPermissions = ref<string[]>([])

/** 从 localStorage 加载用户信息到响应式状态 */
export function loadUserState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    currentUser.value = raw ? JSON.parse(raw) : null
  } catch {
    currentUser.value = null
  }
  currentPermissions.value = currentUser.value?.permissions || []
}

/** 清除响应式用户状态 */
export function clearUserState() {
  currentUser.value = null
  currentPermissions.value = []
}

/** 获取本地存储的用户信息（兼容旧调用） */
export function getLocalUser(): UserInfo | null {
  return currentUser.value
}

/** 获取当前用户的权限列表 */
export function getPermissions(): string[] {
  return currentPermissions.value
}

/**
 * 判断当前用户是否拥有某权限
 * @param perm 单个权限编码，或权限编码数组（满足任一即通过）
 */
export function hasPermission(perm: string | string[]): boolean {
  const perms = currentPermissions.value
  if (!perms.length) return false
  // SUPER_ADMIN 拥有全部权限（通配符）
  if (perms.includes('*')) return true
  
  // 如果是超级管理员角色，也拥有全部权限
  const user = currentUser.value
  if (user?.role === 'SUPER_ADMIN') return true
  
  if (Array.isArray(perm)) {
    return perm.some(p => perms.includes(p))
  }
  return perms.includes(perm)
}

/**
 * 判断当前用户是否为管理员（SUPER_ADMIN 或 HR_ADMIN）
 */
export function isAdmin(): boolean {
  const user = currentUser.value
  if (!user) return false
  const r = user.role?.toUpperCase()
  return r === 'SUPER_ADMIN' || r === 'HR_ADMIN' || r === 'ADMIN'
}

// 初始化时加载一次
loadUserState()
