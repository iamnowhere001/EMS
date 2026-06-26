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
  border-radius: 10px;
  background: linear-gradient(135deg, #409eff, #5470d6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

html.dark .title {
  color: #e0e0e0;
}

html.dark .subtitle {
  color: #8d9099;
}
</style>
