import request from './request'
import type { PageResult } from './employee'

/** 角色 */
export interface Role {
  id?: number
  name?: string
  code?: string
  description?: string
  sortOrder?: number
  status?: number
  createTime?: string
  permissionCount?: number
}

/** 权限（树节点） */
export interface Permission {
  id?: number
  name?: string
  code?: string
  type?: number
  parentId?: number
  path?: string
  icon?: string
  sortOrder?: number
  checked?: boolean
  children?: Permission[]
}

export const roleApi = {
  list() {
    return request.get<any, Role[]>('/role/list')
  },
  getById(id: number) {
    return request.get<any, Role>(`/role/${id}`)
  },
  save(data: Partial<Role>) {
    return request.post<any, string>('/role/save', data)
  },
  update(data: Partial<Role>) {
    return request.put<any, string>('/role/update', data)
  },
  delete(id: number) {
    return request.delete<any, string>(`/role/delete/${id}`)
  },
  getPermissions(roleId: number) {
    return request.get<any, Permission[]>(`/role/permissions/${roleId}`)
  },
  assignPermissions(roleId: number, permissionIds: number[]) {
    return request.post<any, string>(`/role/assign-permissions?roleId=${roleId}`, permissionIds)
  },
}

export const permissionApi = {
  list() {
    return request.get<any, Permission[]>('/permission/list')
  },
  menus() {
    return request.get<any, Permission[]>('/permission/menus')
  },
  tree() {
    return request.get<any, Permission[]>('/permission/tree')
  },
  getById(id: number) {
    return request.get<any, Permission>(`/permission/${id}`)
  },
  save(data: Partial<Permission>) {
    return request.post<any, string>('/permission/save', data)
  },
  update(data: Partial<Permission>) {
    return request.put<any, string>('/permission/update', data)
  },
  delete(id: number) {
    return request.delete<any, string>(`/permission/delete/${id}`)
  },
}
