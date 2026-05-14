<template>
  <span :style="{ color: value > 0 ? '#e74c3c' : value < 0 ? '#27ae60' : '#909399', fontWeight: 500 }">
    <el-icon v-if="value > 0" style="vertical-align: middle"><Top /></el-icon>
    <el-icon v-else-if="value < 0" style="vertical-align: middle"><Bottom /></el-icon>
    {{ formatted }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value: number
}>()

const formatted = computed(() => {
  if (props.value > 0) return `+${props.value.toFixed(2)}%`
  if (props.value < 0) return `${props.value.toFixed(2)}%`
  return '0.00%'
})
</script>
