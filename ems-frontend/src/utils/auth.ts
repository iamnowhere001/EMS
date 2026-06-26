import { STORAGE_KEYS } from "./constants"

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
  return user?.role || ""
}

export const isAdmin = () => {
  const role = getCurrentRole()
  return role?.toUpperCase() === "ADMIN"
}

export const hasPermission = (permission: string) => {
  const role = getCurrentRole()
  if (role?.toUpperCase() === "ADMIN") return true
  return false
}
