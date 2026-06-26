import request from './request'

export interface UserItem {
  id?: number
  username: string
  password?: string
  nickname?: string
  role?: string
  status?: number
  createTime?: string
  updateTime?: string
}

export const userApi = {
  page(params: { page: number; size: number; username?: string; role?: string; status?: number }) {
    return request.get<any, any>('/user/page', { params })
  },
  getById(id: number) {
    return request.get<any, UserItem>(`/user/${id}`)
  },
  create(data: UserItem) {
    return request.post<any, any>('/user', data)
  },
  update(id: number, data: UserItem) {
    return request.put<any, any>(`/user/${id}`, data)
  },
  delete(id: number) {
    return request.delete<any, any>(`/user/${id}`)
  },
  resetPassword(id: number) {
    return request.post<any, any>(`/user/reset-password/${id}`)
  },
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return request.post<any, any>('/user/change-password', data)
  },
  profile() {
    return request.get<any, UserItem>('/user/profile')
  },
}
