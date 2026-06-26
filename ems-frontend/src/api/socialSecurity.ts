import request from './request'

export interface SocialSecurityConfig {
  id?: number
  yearMonth: string
  pensionBase?: number
  pensionCompanyRate?: number
  pensionPersonalRate?: number
  medicalBase?: number
  medicalCompanyRate?: number
  medicalPersonalRate?: number
  unemploymentBase?: number
  unemploymentCompanyRate?: number
  unemploymentPersonalRate?: number
  injuryBase?: number
  injuryCompanyRate?: number
  maternityBase?: number
  maternityCompanyRate?: number
  housingFundBase?: number
  housingFundCompanyRate?: number
  housingFundPersonalRate?: number
}

export const socialSecurityApi = {
  getByYearMonth(yearMonth: string) {
    return request.get<any, SocialSecurityConfig>('/socialSecurity', { params: { yearMonth } })
  },
  saveOrUpdate(data: SocialSecurityConfig) {
    return request.post<any, string>('/socialSecurity', data)
  },
}
