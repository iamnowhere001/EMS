<template>
  <div class="tree-node" :class="[`level-${level}`, { expanded: isExpanded, 'is-leaf': !hasChildren }]">
    <div class="node-line" v-if="level > 0">
      <div class="line-horizontal" v-if="!isLast"></div>
      <div class="line-vertical" v-if="hasChildren && isExpanded"></div>
    </div>
    
    <div class="node-content" @click="handleClick">
      <div class="node-left">
        <div 
          class="expand-icon" 
          v-if="hasChildren"
          @click.stop="isExpanded = !isExpanded"
        >
          <el-icon :class="{ rotated: isExpanded }">
            <ArrowRight />
          </el-icon>
        </div>
        <div class="expand-placeholder" v-else></div>
        
        <div class="node-icon" :style="{ background: getIconBg() }">
          <el-icon><component :is="hasChildren ? FolderOpened : UserFilled" /></el-icon>
        </div>
      </div>
      
      <div class="node-info">
        <div class="node-name">
          <span>{{ node.name }}</span>
          <span class="node-code">{{ node.code }}</span>
        </div>
        <div class="node-meta">
          <span v-if="node.leaderName" class="meta-item">
            <el-icon :size="12"><User /></el-icon>
            {{ node.leaderName }}
          </span>
          <span class="meta-item">
            <el-icon :size="12"><User /></el-icon>
            {{ node.employeeCount || 0 }} 人
          </span>
          <span v-if="hasChildren" class="meta-item">
            <el-icon :size="12"><Folder /></el-icon>
            {{ node.children?.length }} 子部门
          </span>
        </div>
      </div>
      
      <div class="node-actions" v-if="isAdmin()">
        <el-tooltip content="编辑" placement="top">
          <el-button size="small" circle :icon="Edit" @click.stop="$emit('edit', node)" />
        </el-tooltip>
        <el-tooltip content="添加子部门" placement="top">
          <el-button size="small" circle :icon="Plus" @click.stop="$emit('add', node)" />
        </el-tooltip>
        <el-tooltip content="删除" placement="top">
          <el-button size="small" circle :icon="Delete" type="danger" @click.stop="$emit('delete', node)" />
        </el-tooltip>
      </div>
    </div>
    
    <div class="node-children" v-show="hasChildren && isExpanded">
      <OrgTreeNode
        v-for="(child, idx) in node.children || []"
        :key="child.id"
        :node="child"
        :level="level + 1"
        :index="idx"
        :total="(node.children || []).length"
        :default-expanded="false"
        @select="$emit('select', $event)"
        @add="$emit('add', $event)"
        @edit="$emit('edit', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ArrowRight, FolderOpened, Folder, UserFilled, User, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { isAdmin } from '@/utils/auth'
import type { Department } from '@/api/department'

const props = defineProps<{
  node: Department
  level: number
  index: number
  total: number
  defaultExpanded?: boolean
}>()

defineEmits(['select', 'add', 'edit', 'delete'])

const isExpanded = ref(props.defaultExpanded ?? false)

const hasChildren = computed(() => (props.node.children?.length || 0) > 0)
const isLast = computed(() => props.index === props.total - 1)

const iconColors = [
  'linear-gradient(135deg, #6366f1, #8b5cf6)',
  'linear-gradient(135deg, #10b981, #059669)',
  'linear-gradient(135deg, #0ea5e9, #0284c7)',
  'linear-gradient(135deg, #f59e0b, #d97706)',
  'linear-gradient(135deg, #f43f5e, #e11d48)',
]

const getIconBg = () => {
  return iconColors[props.level % iconColors.length]
}

const handleClick = () => {
  if (hasChildren.value) {
    isExpanded.value = !isExpanded.value
  }
}
</script>

<style scoped>
.tree-node {
  position: relative;
}

.tree-node.level-0 {
  padding-left: 0;
}

.tree-node.level-1 {
  padding-left: 36px;
}

.tree-node.level-2 {
  padding-left: 72px;
}

.tree-node.level-3 {
  padding-left: 108px;
}

.tree-node.level-4 {
  padding-left: 144px;
}

.tree-node.level-5 {
  padding-left: 180px;
}

.node-line {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 36px;
  pointer-events: none;
}

.line-horizontal {
  position: absolute;
  left: 0;
  top: 14px;
  width: 18px;
  height: 1px;
  background: var(--border-default);
}

.line-vertical {
  position: absolute;
  left: 0;
  top: 32px;
  bottom: 0;
  width: 1px;
  background: var(--border-default);
}

.node-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  margin-bottom: 4px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s var(--ease-out);
}

.node-content:hover {
  background: var(--bg-soft);
}

.node-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.expand-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: all 0.2s;
}

.expand-icon:hover {
  background: var(--bg-soft);
  color: var(--text-secondary);
}

.expand-icon .rotated {
  transform: rotate(90deg);
}

.expand-placeholder {
  width: 24px;
}

.node-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  flex-shrink: 0;
}

.node-info {
  flex: 1;
  min-width: 0;
}

.node-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 3px;
}

.node-name span:first-child {
  font-size: 14.5px;
  font-weight: 600;
  color: var(--text-primary);
}

.node-code {
  font-size: 11px;
  color: var(--text-tertiary);
  font-family: var(--font-mono);
  background: var(--bg-soft);
  padding: 2px 6px;
  border-radius: var(--radius-xs);
}

.node-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--text-tertiary);
}

.node-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.node-content:hover .node-actions {
  opacity: 1;
}

.node-actions :deep(.el-button) {
  width: 28px;
  height: 28px;
  padding: 0;
}

.node-children {
  margin-top: 4px;
}

html.dark .line-horizontal,
html.dark .line-vertical {
  background: var(--border-default);
}

html.dark .node-content:hover {
  background: var(--bg-soft);
}

html.dark .node-code {
  background: var(--bg-soft);
}
</style>