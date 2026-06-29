/**
 * 兼容层：统一使用正确的权限判断逻辑
 * 所有 getCurrentUser / isAdmin / hasPermission 均使用正确的角色编码
 */
import { STORAGE_KEYS } from './constants'

export const getCurrentUser = () => {
  const userStr = localStorage.getItem(STORAGE_KEYS.USER)
  if (!userStr) return null
  try {
    return JSON.parse(userStr)
  } catch {
    return null
  }
}

export const getCurrentRole = () => {
  const user = getCurrentUser()
  return user?.role || ''
}

/**
 * 判断是否为管理员（兼容新旧角色编码）
 */
export const isAdmin = () => {
  const role = getCurrentRole()?.toUpperCase()
  return role === 'SUPER_ADMIN' || role === 'HR_ADMIN' || role === 'ADMIN'
}

export const hasPermission = (permission: string) => {
  const role = getCurrentRole()?.toUpperCase()
  if (role === 'SUPER_ADMIN' || role === 'ADMIN') return true
  // 对于非超管角色，从 localStorage 读取完整权限列表判断
  const userStr = localStorage.getItem(STORAGE_KEYS.USER)
  if (!userStr) return false
  try {
    const user = JSON.parse(userStr)
    const perms: string[] = user?.permissions || []
    if (perms.includes('*')) return true
    return perms.includes(permission)
  } catch {
    return false
  }
}
