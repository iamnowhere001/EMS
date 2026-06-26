import request from './request'
import type { PageResult } from './employee'

export interface Attendance {
  id?: number
  employeeId: number
  attendanceDate: string
  checkInTime?: string
  checkOutTime?: string
  status?: number
  workHours?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

export const attendanceApi = {
  page(params: { employeeId?: number; yearMonth?: string; page: number; size: number }) {
    return request.get<any, PageResult<Attendance>>('/attendance/page', { params })
  },
  list(params: { employeeId: number; yearMonth?: string }) {
    return request.get<any, Attendance[]>('/attendance/list', { params })
  },
  checkIn(employeeId: number) {
    return request.post<any, string>('/attendance/checkIn', null, { params: { employeeId } })
  },
  checkOut(employeeId: number) {
    return request.post<any, string>('/attendance/checkOut', null, { params: { employeeId } })
  },
  batchImport(data: Attendance[]) {
    return request.post<any, string>('/attendance/batchImport', data)
  },
  update(data: Attendance) {
    return request.put<any, string>('/attendance', data)
  },
  delete(id: number) {
    return request.delete<any, string>(`/attendance/${id}`)
  },
}
