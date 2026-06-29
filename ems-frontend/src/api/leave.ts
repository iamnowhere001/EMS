import request from './request'
import type { PageResult } from './employee'

export interface LeaveRequest {
  id?: number
  employeeId: number
  leaveType: string
  startDate: string
  endDate: string
  days: number
  reason?: string
  status?: number
  approverId?: number
  approveRemark?: string
  approveTime?: string
  createTime?: string
  updateTime?: string
}

export const leaveApi = {
  submit(data: LeaveRequest) {
    return request.post<any, string>('/leave/submit', data)
  },
  approve(id: number, approverId: number, remark?: string) {
    return request.post<any, string>('/leave/approve', null, { params: { id, approverId, remark } })
  },
  reject(id: number, approverId: number, remark?: string) {
    return request.post<any, string>('/leave/reject', null, { params: { id, approverId, remark } })
  },
  cancel(id: number, employeeId: number) {
    return request.post<any, string>(`/leave/cancel/${id}`, null, { params: { employeeId } })
  },
  page(params: { employeeId?: number; status?: number; month?: string; page: number; size: number }) {
    return request.get<any, PageResult<LeaveRequest>>('/leave/page', { params })
  },
  my(params: { employeeId: number; status?: number; page: number; size: number }) {
    return request.get<any, PageResult<LeaveRequest>>('/leave/my', { params })
  },
  statistics(month?: string) {
    return request.get<any, any[]>('/leave/statistics', { params: { month } })
  },
}
