import request from './request'

export interface Department {
  id?: number
  code: string
  name: string
  parentCode?: string
  leaderId?: number
  leaderName?: string
  sort?: number
  status?: number
  description?: string
  employeeCount?: number
  children?: Department[]
}

export const departmentApi = {
  tree: () => request.get<any, Department[]>('/department/tree'),
  list: () => request.get<any, Department[]>('/department'),
  getById: (id: number) => request.get<any, Department>(`/department/${id}`),
  getByCode: (code: string) => request.get<any, Department>(`/department/code/${code}`),
  create: (data: Department) => request.post<any, string>('/department', data),
  update: (id: number, data: Department) => request.put<any, string>(`/department/${id}`, data),
  delete: (id: number) => request.delete<any, string>(`/department/${id}`),
}