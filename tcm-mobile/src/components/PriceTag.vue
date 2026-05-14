<template>
  <view :class="['price-tag', tagClass]">
    <text class="tag-text">{{ displayText }}</text>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value: number
}>()

const tagClass = computed(() => {
  if (props.value > 0) return 'up'
  if (props.value < 0) return 'down'
  return 'flat'
})

const displayText = computed(() => {
  if (props.value > 0) return `+${props.value.toFixed(2)}%`
  if (props.value < 0) return `${props.value.toFixed(2)}%`
  return '持平'
})
</script>

<style scoped>
.price-tag {
  display: inline-flex;
  align-items: center;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-top: 6rpx;
}

.tag-text {
  font-size: 24rpx;
  font-weight: 500;
}

.price-tag.up {
  background: #fff0ed;
}

.price-tag.up .tag-text {
  color: #e74c3c;
}

.price-tag.down {
  background: #edf7ef;
}

.price-tag.down .tag-text {
  color: #27ae60;
}

.price-tag.flat {
  background: #f5f5f5;
}

.price-tag.flat .tag-text {
  color: #999;
}
</style>
