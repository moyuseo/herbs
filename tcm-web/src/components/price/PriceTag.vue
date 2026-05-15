<template>
  <span
    class="price-tag"
    :class="{
      'price-tag--up': value > 0,
      'price-tag--down': value < 0,
      'price-tag--flat': value === 0,
    }"
  >
    <el-icon v-if="value > 0" class="price-tag__icon"><Top /></el-icon>
    <el-icon v-else-if="value < 0" class="price-tag__icon"><Bottom /></el-icon>
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

<style scoped lang="scss">
@import '@/styles/variables.scss';

.price-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-weight: 600;
  font-size: 14px;
  font-variant-numeric: tabular-nums;
  padding: 2px 8px;
  border-radius: 4px;
  line-height: 1.6;

  &__icon {
    font-size: 12px;
  }

  &--up {
    color: $up-color;
    background: $danger-light;
  }

  &--down {
    color: $down-color;
    background: $primary-lighter;
  }

  &--flat {
    color: $text-muted;
    background: $bg-warm;
  }
}
</style>
