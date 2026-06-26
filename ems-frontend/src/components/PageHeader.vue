<template>
  <div class="title-section">
    <div class="title-icon">
      <component :is="icon" v-if="icon" />
    </div>
    <div>
      <div class="title">{{ title }}</div>
      <div class="subtitle" v-if="subtitle">{{ subtitle }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { type Component } from 'vue'

interface Props {
  title: string
  subtitle?: string
  icon?: Component
}

defineProps<Props>()
</script>

<style scoped>
.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  width: 40px;
  height: 40px;
  border-radius: 11px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
  box-shadow: 0 6px 14px -4px rgba(99, 102, 241, 0.5);
  transition: transform 0.3s var(--ease-spring);
}
.title-icon:hover { transform: scale(1.06) rotate(-4deg); }

.title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.subtitle {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 3px;
  font-weight: 500;
  letter-spacing: 0.2px;
}
</style>
