import type { DirectiveBinding } from 'vue'

interface ResizeState {
  startX: number
  startWidth: number
  col: HTMLElement
  handle: HTMLElement
  table: HTMLElement
}

let activeState: ResizeState | null = null

function initResize(table: HTMLElement) {
  const headerCells = table.querySelectorAll('.el-table__header-wrapper th.el-table__cell')
  const cols = table.querySelectorAll('colgroup col')

  headerCells.forEach((th, index) => {
    if (th.querySelector('.resize-handle')) return

    const handle = document.createElement('div')
    handle.className = 'resize-handle'
    handle.style.cssText = `
      position: absolute;
      right: 0;
      top: 0;
      bottom: 0;
      width: 6px;
      cursor: col-resize;
      z-index: 10;
      background: transparent;
      transition: background 0.2s;
    `
    ;(th as HTMLElement).style.position = 'relative'
    th.appendChild(handle)

    handle.addEventListener('mousedown', (e: MouseEvent) => {
      e.preventDefault()
      e.stopPropagation()
      const col = cols[index] as HTMLElement
      const rect = (th as HTMLElement).getBoundingClientRect()
      activeState = {
        startX: e.clientX,
        startWidth: rect.width,
        col,
        handle: handle as HTMLElement,
        table,
      }
      document.body.style.cursor = 'col-resize'
      document.body.style.userSelect = 'none'
    })
  })
}

function onMouseMove(e: MouseEvent) {
  if (!activeState) return
  const diff = e.clientX - activeState.startX
  const newWidth = Math.max(60, activeState.startWidth + diff)
  activeState.col.style.width = `${newWidth}px`
  activeState.col.setAttribute('width', `${newWidth}`)
}

function onMouseUp() {
  if (!activeState) return
  activeState = null
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
}

document.addEventListener('mousemove', onMouseMove)
document.addEventListener('mouseup', onMouseUp)

export const vResizeColumn = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const table = el.querySelector('.el-table') as HTMLElement
    if (!table) return

    if (binding.value === false) return

    const observer = new MutationObserver(() => {
      initResize(table)
    })
    observer.observe(table.querySelector('.el-table__header-wrapper') as Node, {
      childList: true,
      subtree: true,
    })

    setTimeout(() => initResize(table), 0)
  },
}
