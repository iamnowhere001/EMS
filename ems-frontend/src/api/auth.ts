import request from './request'

export interface LoginForm {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  employeeId?: number
  role: string
  permissions: string[]
  token: string
  refreshToken?: string
}

export const authApi = {
  login(data: LoginForm) {
    return request.post<any, UserInfo>('/auth/login', data)
  },
  logout(refreshToken?: string) {
    return request.post<any, { message: string }>('/auth/logout', { refreshToken })
  },
  refresh(refreshToken: string) {
    return request.post<any, UserInfo>('/auth/refresh', { refreshToken })
  },
}
