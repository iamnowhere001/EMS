import { ref, watch, type Ref } from 'vue'

export function useCountUp(targetValue: Ref<number> | number, duration = 800) {
  const displayValue = ref(0)
  let rafId: number | null = null

  const getTarget = () => {
    return typeof targetValue === 'number' ? targetValue : targetValue.value
  }

  const animate = (target: number) => {
    if (rafId) cancelAnimationFrame(rafId)
    const start = displayValue.value
    const startTime = performance.now()

    const step = (currentTime: number) => {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      const easeOutQuart = 1 - Math.pow(1 - progress, 4)
      displayValue.value = start + (target - start) * easeOutQuart

      if (progress < 1) {
        rafId = requestAnimationFrame(step)
      } else {
        displayValue.value = target
      }
    }

    rafId = requestAnimationFrame(step)
  }

  watch(
    () => getTarget(),
    (val) => animate(val),
    { immediate: true }
  )

  return displayValue
}
