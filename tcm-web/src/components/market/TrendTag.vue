<template>
  <span :class="['trend-tag', `trend-${trend}`]">
    <el-icon v-if="trend === 'up'"><Top /></el-icon>
    <el-icon v-else-if="trend === 'down'"><Bottom /></el-icon>
    <el-icon v-else-if="trend === 'stable'"><Minus /></el-icon>
    <span v-if="trend === 'rare'" class="rare-dot">●</span>
    <span v-if="showPercent && changePercent !== undefined && changePercent !== null" class="percent">
      {{ formatPercent(changePercent) }}
    </span>
    <span v-if="!showPercent || (changePercent === undefined || changePercent === null)" class="label">
      {{ trendLabel }}
    </span>
  </span>
</template>

<script setup>
import { computed } from 'vue'
import { Top, Bottom, Minus } from '@element-plus/icons-vue'

const props = defineProps({
  trend: { type: String, default: 'stable', validator: v => ['up', 'down', 'stable', 'rare'].includes(v) },
  changePercent: { type: Number, default: null },
  showPercent: { type: Boolean, default: true }
})

const trendLabel = computed(() => {
  const map = { up: '涨', down: '跌', stable: '稳', rare: '少' }
  return map[props.trend] || '稳'
})

function formatPercent(val) {
  if (val > 0) return `+${val.toFixed(2)}%`
  if (val < 0) return `${val.toFixed(2)}%`
  return '0.00%'
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.trend-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 13px;
  font-weight: 500;
}

.trend-up {
  color: $up-color;
}

.trend-down {
  color: $down-color;
}

.trend-stable {
  color: $text-secondary;
}

.trend-rare {
  color: #E6A23C;
}

.rare-dot {
  font-size: 10px;
}

.percent, .label {
  font-size: 13px;
}
</style>
