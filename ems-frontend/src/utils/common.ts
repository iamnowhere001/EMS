export function getAvatarColor(name?: string, gender?: number) {
  if (gender === 0) return '#ff7e9a'
  if (gender === 1) return '#4a90e2'
  if (!name) return '#409eff'
  const neutralColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#8e44ad', '#16a085']
  return neutralColors[name.charCodeAt(0) % neutralColors.length]
}

export function formatSalary(salary: number | string | null | undefined) {
  if (salary === undefined || salary === null || salary === '') return '0'
  const num = typeof salary === 'string' ? Number(salary) : salary
  if (isNaN(num)) return '0'
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 })
}

export function formatEmpty(value: any, placeholder = '-') {
  return value === undefined || value === null || value === '' ? placeholder : value
}

export function formatWorkYears(hireDate?: string) {
  if (!hireDate) return '-'
  const start = new Date(hireDate)
  const now = new Date()
  if (isNaN(start.getTime()) || start > now) return '-'
  let years = now.getFullYear() - start.getFullYear()
  let months = now.getMonth() - start.getMonth()
  if (now.getDate() < start.getDate()) {
    months -= 1
  }
  if (months < 0) {
    years -= 1
    months += 12
  }
  if (years > 0 && months > 0) return `${years}年${months}个月`
  if (years > 0) return `${years}年`
  if (months > 0) return `${months}个月`
  return '不足1个月'
}

export function formatDateTime(value?: string) {
  if (!value) return '-'
  const d = new Date(value)
  if (isNaN(d.getTime())) return value
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

export function formatDate(value?: string) {
  if (!value) return '-'
  const d = new Date(value)
  if (isNaN(d.getTime())) return value
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

export function desensitizePhone(phone?: string) {
  if (!phone || phone.length < 7) return formatEmpty(phone)
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

export function desensitizeEmail(email?: string) {
  if (!email || !email.includes('@')) return formatEmpty(email)
  const parts = email.split('@')
  const name = parts[0] || ''
  const domain = parts[1] || ''
  const prefix = name.slice(0, 2)
  const masked = '*'.repeat(Math.max(2, name.length - 2))
  return `${prefix}${masked}@${domain}`
}

export function formatNumber(num: number | string | null | undefined, options?: Intl.NumberFormatOptions) {
  if (num === undefined || num === null || num === '') return '0'
  const n = typeof num === 'string' ? Number(num) : num
  if (isNaN(n)) return '0'
  return n.toLocaleString('zh-CN', options)
}

export function formatPercent(value: number | string | null | undefined, decimals = 2) {
  if (value === undefined || value === null || value === '') return '0%'
  const num = typeof value === 'string' ? Number(value) : value
  if (isNaN(num)) return '0%'
  return `${(num * 100).toFixed(decimals)}%`
}

export function sleep(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export function debounce<T extends (...args: any[]) => any>(fn: T, delay = 300) {
  let timer: ReturnType<typeof setTimeout> | null = null
  return function (this: any, ...args: Parameters<T>) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

export function throttle<T extends (...args: any[]) => any>(fn: T, delay = 300) {
  let lastTime = 0
  return function (this: any, ...args: Parameters<T>) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      lastTime = now
      fn.apply(this, args)
    }
  }
}

export function copyToClipboard(text: string) {
  return navigator.clipboard.writeText(text)
}

export function downloadFile(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
