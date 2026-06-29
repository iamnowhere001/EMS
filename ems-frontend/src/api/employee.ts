import request from './request'

export interface Employee {
  id?: number
  employeeNo?: string
  name: string
  age?: number
  gender?: number
  phone?: string
  email?: string
  idCard?: string
  bankCard?: string
  department?: string
  position?: string
  rank?: string
  salary?: number
  hireDate?: string
  avatar?: string
  status?: number
  createTime?: string
  updateTime?: string
  // Phase 1 档案补全字段
  emergencyContact?: string
  emergencyPhone?: string
  currentAddress?: string
  hukouAddress?: string
  politicalStatus?: string
  maritalStatus?: number
  nation?: string
  nativePlace?: string
}

export interface EmployeeEducation {
  id?: number
  employeeId?: number
  school: string
  major?: string
  education?: string
  degree?: string
  startDate?: string
  endDate?: string
  isFullTime?: number
  sortOrder?: number
}

export interface EmployeeWorkExperience {
  id?: number
  employeeId?: number
  company: string
  position?: string
  department?: string
  startDate?: string
  endDate?: string
  leaveReason?: string
  sortOrder?: number
}

export interface EmployeeFamily {
  id?: number
  employeeId?: number
  name: string
  relation: string
  gender?: number
  birthDate?: string
  company?: string
  phone?: string
  sortOrder?: number
}

export interface EmployeeCertificate {
  id?: number
  employeeId?: number
  name: string
  level?: string
  issuer?: string
  issueDate?: string
  expireDate?: string
  certNo?: string
  sortOrder?: number
}

export interface EmployeeContract {
  id?: number
  employeeId?: number
  contractNo?: string
  contractType: string
  startDate: string
  endDate?: string
  probationMonths?: number
  probationStartDate?: string
  probationEndDate?: string
  signedDate?: string
  salary?: number
  workLocation?: string
  status?: number
  remark?: string
  attachmentUrl?: string
  sortOrder?: number
}

export interface EmployeeProbation {
  id?: number
  employeeId?: number
  contractId?: number
  startDate: string
  endDate: string
  result?: number
  resultDate?: string
  extensionEndDate?: string
  evaluator?: string
  evaluation?: string
  remark?: string
}

export interface EmployeeDetailVO {
  employee: Employee
  educations: EmployeeEducation[]
  workExperiences: EmployeeWorkExperience[]
  families: EmployeeFamily[]
  certificates: EmployeeCertificate[]
  contracts: EmployeeContract[]
  probations: EmployeeProbation[]
}

export interface EmployeeFormPayload {
  employee: Employee
  educations?: EmployeeEducation[]
  workExperiences?: EmployeeWorkExperience[]
  families?: EmployeeFamily[]
  certificates?: EmployeeCertificate[]
  contracts?: EmployeeContract[]
  probations?: EmployeeProbation[]
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface EmployeeStatistics {
  totalCount: number
  activeCount: number
  departmentCount: number
  totalSalary: number
}

export const employeeApi = {
  page(params: {
    name?: string
    employeeNo?: string
    department?: string
    position?: string
    status?: number
    page: number
    size: number
    sortField?: string
    sortOrder?: string
  }) {
    return request.get<any, PageResult<Employee>>('/employee/page', { params })
  },
  list() {
    return request.get<any, Employee[]>('/employee/list')
  },
  getById(id: number) {
    return request.get<any, Employee>(`/employee/${id}`)
  },
  me() {
    return request.get<any, Employee>('/employee/me')
  },
  updateMe(data: EmployeeFormPayload) {
    return request.put<any, void>('/employee/me', data)
  },
  getDetail(id: number) {
    return request.get<any, EmployeeDetailVO>(`/employee/${id}/detail`)
  },
  save(data: EmployeeFormPayload) {
    return request.post<any, void>('/employee', data)
  },
  update(id: number, data: EmployeeFormPayload) {
    return request.put<any, void>(`/employee/${id}`, data)
  },
  delete(id: number) {
    return request.delete<any, void>(`/employee/${id}`)
  },
  deleteBatch(ids: number[]) {
    return request.delete<any, void>('/employee/batch', { params: { ids } })
  },
  batchTransfer(ids: number[], department: string, position: string) {
    return request.post<any, number>('/employee/batch/transfer', { ids, department, position })
  },
  batchAdjustSalary(ids: number[], mode: string, amount: number) {
    return request.post<any, number>('/employee/batch/adjust-salary', { ids, mode, amount })
  },
  sort(ids: number[]) {
    return request.post<any, void>('/employee/sort', ids)
  },
  statistics() {
    return request.get<any, EmployeeStatistics>('/employee/statistics')
  },
  export() {
    return request.get('/employee/export', {
      responseType: 'blob',
    })
  },
  import(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<any, void>('/employee/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
