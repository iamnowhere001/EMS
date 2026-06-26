import request from './request'

export interface EmployeeLeave {
  id?: number
  employeeId?: number
  leaveType?: string
  applyDate?: string
  lastWorkDate?: string
  leaveDate?: string
  handoverTo?: number
  handoverNote?: string
  reason?: string
  exitInterviewNote?: string
  applicant?: string
  approver?: string
  approveDate?: string
  socialInsuranceCutoff?: string
  housingFundCutoff?: string
}

export interface WorkflowChange {
  id?: number
  employeeId: number
  changeType: 'TRANSFER' | 'ADJUST_SALARY' | 'CONFIRM' | 'LEAVE'
  effectiveDate: string
  beforeValue?: string
  afterValue?: string
  changeSummary?: string
  status?: number
  applicant?: string
  applyDate?: string
  approver?: string
  approveDate?: string
  reason?: string
  remark?: string
  fromDepartment?: string
  toDepartment?: string
  fromPosition?: string
  toPosition?: string
  fromRank?: string
  toRank?: string
  fromSalary?: number
  toSalary?: number
  adjustmentPercent?: number
  leave?: EmployeeLeave
}

export interface PageResp<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export const workflowApi = {
  submit: (data: WorkflowChange) => request.post<WorkflowChange>('/workflow/submit', data),
  revoke: (id: number) => request.post<boolean>(`/workflow/revoke/${id}`),
  page: (params: { page: number; size: number; changeType?: string; keyword?: string }) =>
    request.get<PageResp<WorkflowChange>>('/workflow/page', { params }),
  listByEmployee: (employeeId: number) =>
    request.get<WorkflowChange[]>(`/workflow/list-by-employee/${employeeId}`),
}
