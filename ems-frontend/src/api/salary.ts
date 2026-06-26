import request from './request'
import type { PageResult } from './employee'

export interface SalaryStructure {
  id?: number
  employeeId?: number
  baseSalary?: number
  performanceSalary?: number
  allowance?: number
  subsidy?: number
}

export interface SalaryRecord {
  id?: number
  employeeId: number
  yearMonth: string
  baseSalary: number
  performanceSalary: number
  allowance: number
  subsidy: number
  overtimePay: number
  deduction: number
  socialSecurityPersonal: number
  housingFundPersonal: number
  tax: number
  actualSalary: number
  status?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

export const salaryApi = {
  getStructure(employeeId: number) {
    return request.get<any, SalaryStructure>(`/salary/structure/${employeeId}`)
  },
  saveStructure(employeeId: number, data: SalaryStructure) {
    return request.post<any, string>('/salary/structure', data, { params: { employeeId } })
  },
  recordPage(params: { employeeId?: number; yearMonth?: string; status?: number; page: number; size: number }) {
    return request.get<any, PageResult<SalaryRecord>>('/salary/record/page', { params })
  },
  recordList(employeeId: number) {
    return request.get<any, SalaryRecord[]>('/salary/record/list', { params: { employeeId } })
  },
  generate(yearMonth: string) {
    return request.post<any, string>('/salary/generate', null, { params: { yearMonth } })
  },
  confirm(id: number) {
    return request.post<any, string>('/salary/confirm', null, { params: { id } })
  },
  batchConfirm(ids: number[]) {
    return request.post<any, string>('/salary/batchConfirm', ids)
  },
  batchPay(ids: number[]) {
    return request.post<any, string>('/salary/batchPay', ids)
  },
  updateRecord(data: SalaryRecord) {
    return request.put<any, string>('/salary/record', data)
  },
}
