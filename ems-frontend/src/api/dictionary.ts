import request from './request'

export interface Dictionary {
  id?: number
  type?: string
  code?: string
  name: string
  sort?: number
  status?: number
  parentCode?: string
}

export const dictionaryApi = {
  listByType(type: string, parentCode?: string) {
    return request.get<Dictionary[]>(`/dictionary/${type}`, { params: { parentCode } })
  },

  listAll() {
    return request.get<Dictionary[]>('/dictionary')
  },

  save(data: Dictionary) {
    return request.post<void>('/dictionary', data)
  },

  update(id: number, data: Dictionary) {
    return request.put<void>(`/dictionary/${id}`, data)
  },

  delete(id: number) {
    return request.delete<void>(`/dictionary/${id}`)
  },
}
