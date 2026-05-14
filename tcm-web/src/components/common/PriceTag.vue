<template>
  <span :class="['price-tag', tagClass]">
    <span class="price-tag__arrow">{{ arrow }}</span>
    <span class="price-tag__value">{{ formattedValue }}</span>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value: number
  type?: 'day' | 'month'
}>()

const tagClass = computed(() => {
  if (props.value > 0) return 'price-tag--up'
  if (props.value < 0) return 'price-tag--down'
  return 'price-tag--flat'
})

const arrow = computed(() => {
  if (props.value > 0) return '↑'
  if (props.value < 0) return '↓'
  return '—'
})

const formattedValue = computed(() => {
  if (props.value === 0) return '稳'
  const abs = Math.abs(props.value)
  return props.type === 'month' ? `${abs}%` : `${abs}`
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.price-tag {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  font-weight: 600;

  &--up {
    color: $primary-color;
  }

  &--down {
    color: $danger-color;
  }

  &--flat {
    color: $text-secondary;
  }

  &__arrow {
    margin-right: 2px;
  }
}
</style>
