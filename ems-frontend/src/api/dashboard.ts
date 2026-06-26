import request from './request'

export interface NameValue {
  name: string
  value: number | string
}

export interface MonthlyTrend {
  month: string
  onboardCount: number
  leftCount: number
}

export interface DepartmentSalary {
  department: string
  headcount: number
  totalSalary: number
  avgSalary: number
}

export interface RankDistribution {
  rank: string
  count: number
}

export interface DashboardKpi {
  totalCount: number
  activeCount: number
  leftCount: number
  newHireThisMonth: number
  leftThisMonth: number
  departmentCount: number
  avgSalary: number
  totalSalary: number
  avgTenureYears: number
}

export interface DashboardData {
  kpi: DashboardKpi
  departmentDistribution: NameValue[]
  ageDistribution: NameValue[]
  tenureDistribution: NameValue[]
  educationDistribution: NameValue[]
  salaryDistribution: NameValue[]
  genderRatio: NameValue[]
  monthlyTrend: MonthlyTrend[]
  departmentSalary: DepartmentSalary[]
  rankDistribution: RankDistribution[]
}

export const dashboardApi = {
  overview: () => request.get<DashboardData>('/dashboard/overview'),
}
