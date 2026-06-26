// 通用 API 响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 通用分页结果类型
export interface PageResult<T> {
  records: T[]
  total: number
  [key: string]: any
}

// 通用分页查询参数
export interface PageQuery {
  page?: number
  size?: number
  [key: string]: any
}

// 用户信息类型
export interface UserInfo {
  id: number
  username: string
  nickname: string
  role: string
  token?: string
  refreshToken?: string
  [key: string]: any
}

// 字典项类型
export interface Dictionary {
  id?: number
  type: string
  name: string
  code: string
  parentCode?: string
  sort?: number
  status?: number
  [key: string]: any
}

// 员工基础类型
export interface Employee {
  id?: number
  name?: string
  employeeNo?: string
  department?: string
  position?: string
  rank?: string
  [key: string]: any
}
