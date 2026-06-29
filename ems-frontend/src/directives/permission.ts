/**
 * v-permission 自定义指令
 * 用法：
 *   <el-button v-permission="'employee:create'">新增</el-button>
 *   <el-button v-permission="['employee:create','employee:edit']">新增或编辑</el-button>
 *
 * 逻辑：无权限时移除该 DOM 元素
 */
import type { Directive, DirectiveBinding } from 'vue'
import { getPermissions } from '@/utils/permission'

function checkPermission(value: string | string[]): boolean {
  if (!value) return true
  const perms = getPermissions()
  if (!perms.length) return false
  if (perms.includes('*')) return true
  if (Array.isArray(value)) {
    return value.some(p => perms.includes(p))
  }
  return perms.includes(value)
}

const permissionDirective: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    if (!checkPermission(binding.value)) {
      el.parentNode?.removeChild(el)
    }
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    if (!checkPermission(binding.value)) {
      el.parentNode?.removeChild(el)
    } else if (!el.parentNode) {
      // 如果元素已被移除，需要重新插入（这种情况较少，暂不处理）
    }
  },
}

export default permissionDirective
