import request from './request'
import type { PageResult } from './employee'

export interface OperationLog {
  id?: number
  module?: string
  action?: string
  content?: string
  operatorId?: number
  operatorName?: string
  ip?: string
  createTime?: string
}

export const operationLogApi = {
  page(params: { page: number; size: number; module?: string; action?: string; operator?: string; startTime?: string; endTime?: string }) {
    return request.get<any, PageResult<OperationLog>>('/operation-log/page', { params })
  },
}
