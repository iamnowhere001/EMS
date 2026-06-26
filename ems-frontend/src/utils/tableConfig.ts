import { reactive } from 'vue'

export const COLUMN_VISIBLE_KEY = 'ems_employee_columns_visible'
export const COLUMN_WIDTH_KEY = 'ems_employee_columns_width'

export const columnLabelMap: Record<string, string> = {
  drag: '拖拽',
  selection: '选择',
  index: '序号',
  info: '员工信息',
  employeeNo: '工号',
  department: '部门',
  position: '岗位',
  age: '年龄',
  gender: '性别',
  rank: '职级',
  contact: '联系方式',
  salary: '薪资',
  hireDate: '入职日期',
  workYears: '工龄',
  status: '状态',
  operation: '操作'
}

export const defaultColumnVisible: Record<string, boolean> = {
  drag: true,
  selection: true,
  index: true,
  info: true,
  employeeNo: true,
  department: true,
  position: true,
  age: true,
  gender: true,
  rank: true,
  contact: true,
  salary: true,
  hireDate: true,
  workYears: true,
  status: true,
  operation: true,
}

export const defaultColumnWidth: Record<string, number | string> = {
  drag: 40,
  selection: 50,
  index: 60,
  info: '',
  employeeNo: 100,
  department: 110,
  position: 110,
  age: 80,
  gender: 80,
  rank: 90,
  contact: '',
  salary: 130,
  hireDate: 120,
  workYears: 100,
  status: 100,
  operation: 180,
}

export const columnVisible = reactive<Record<string, boolean>>({ ...defaultColumnVisible })
export const columnWidth = reactive<Record<string, number | string>>({ ...defaultColumnWidth })

export const loadColumnSettings = () => {
  const savedVisible = localStorage.getItem(COLUMN_VISIBLE_KEY)
  if (savedVisible) {
    try {
      Object.assign(columnVisible, JSON.parse(savedVisible))
    } catch (e) {
      console.error(e)
    }
  }
  const savedWidth = localStorage.getItem(COLUMN_WIDTH_KEY)
  if (savedWidth) {
    try {
      Object.assign(columnWidth, JSON.parse(savedWidth))
    } catch (e) {
      console.error(e)
    }
  }
}

export const saveColumnVisible = () => {
  localStorage.setItem(COLUMN_VISIBLE_KEY, JSON.stringify(columnVisible))
}

export const saveColumnWidth = () => {
  localStorage.setItem(COLUMN_WIDTH_KEY, JSON.stringify(columnWidth))
}

const highlightedRowIds = new Set<number>()

export const highlightRow = (id?: number) => {
  if (!id) return
  highlightedRowIds.add(id)
  setTimeout(() => {
    highlightedRowIds.delete(id)
  }, 1500)
}

export const isHighlighted = (id?: number) => {
  return id !== undefined && highlightedRowIds.has(id)
}
