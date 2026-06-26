import { ref, onMounted, watch, type WatchStopHandle } from 'vue'

const isDark = ref(false)
const STORAGE_KEY = 'ems_theme'

export function useTheme() {
  let watcher: WatchStopHandle | null = null

  const toggleTheme = () => {
    isDark.value = !isDark.value
    applyTheme()
  }

  const setTheme = (dark: boolean) => {
    isDark.value = dark
    applyTheme()
  }

  const applyTheme = () => {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
    localStorage.setItem(STORAGE_KEY, isDark.value ? 'dark' : 'light')
  }

  const initTheme = () => {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved === 'dark') {
      isDark.value = true
    } else if (saved === 'light') {
      isDark.value = false
    } else {
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    applyTheme()
    
    // 监听系统主题变化
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', (e) => {
        if (!localStorage.getItem(STORAGE_KEY)) {
          setTheme(e.matches)
        }
      })
    }
  }

  const clearThemePreference = () => {
    localStorage.removeItem(STORAGE_KEY)
    initTheme()
  }

  onMounted(initTheme)

  return {
    isDark,
    toggleTheme,
    setTheme,
    initTheme,
    clearThemePreference
  }
}
