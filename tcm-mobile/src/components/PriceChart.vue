<template>
  <view class="chart-wrap">
    <canvas
      canvas-id="priceChart"
      id="priceChart"
      class="chart-canvas"
      @touchstart="onTouchStart"
      @touchmove="onTouchMove"
      @touchend="onTouchEnd"
    />
    <view v-if="tooltip.show" class="tooltip" :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }">
      <text class="tooltip-text">{{ tooltip.date }}</text>
      <text class="tooltip-price">¥{{ tooltip.price.toFixed(2) }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, nextTick } from 'vue'
import { getCurrentInstance } from 'vue'

const props = defineProps<{
  trends: Array<{ date: string; price: number }>
}>()

const tooltip = ref({
  show: false,
  x: 0,
  y: 0,
  date: '',
  price: 0
})

let canvasWidth = 0
let canvasHeight = 0
const padding = { top: 30, right: 30, bottom: 40, left: 60 }
let pointList: Array<{ x: number; y: number; date: string; price: number }> = []

function drawChart() {
  if (!props.trends || props.trends.length === 0) return

  const query = uni.createSelectorQuery().in(getCurrentInstance()?.proxy)
  query.select('#priceChart').boundingClientRect((rect: any) => {
    if (!rect) return

    canvasWidth = rect.width
    canvasHeight = rect.height

    const ctx = uni.createCanvasContext('priceChart', getCurrentInstance()?.proxy)
    const chartW = canvasWidth - padding.left - padding.right
    const chartH = canvasHeight - padding.top - padding.bottom

    const prices = props.trends.map((t) => t.price)
    const minPrice = Math.min(...prices)
    const maxPrice = Math.max(...prices)
    const priceRange = maxPrice - minPrice || 1

    ctx.clearRect(0, 0, canvasWidth, canvasHeight)

    ctx.setStrokeStyle('#eee')
    ctx.setLineWidth(1)
    for (let i = 0; i <= 4; i++) {
      const y = padding.top + (chartH / 4) * i
      ctx.beginPath()
      ctx.moveTo(padding.left, y)
      ctx.lineTo(canvasWidth - padding.right, y)
      ctx.stroke()

      const priceVal = maxPrice - (priceRange / 4) * i
      ctx.setFontSize(10)
      ctx.setFillStyle('#999')
      ctx.setTextAlign('right')
      ctx.fillText(priceVal.toFixed(1), padding.left - 8, y + 3)
    }

    pointList = props.trends.map((t, i) => ({
      x: padding.left + (chartW / (props.trends.length - 1 || 1)) * i,
      y: padding.top + chartH - ((t.price - minPrice) / priceRange) * chartH,
      date: t.date,
      price: t.price
    }))

    if (pointList.length > 1) {
      const gradient = ctx.createLinearGradient(0, padding.top, 0, canvasHeight - padding.bottom)
      gradient.addColorStop(0, 'rgba(231, 76, 60, 0.3)')
      gradient.addColorStop(1, 'rgba(231, 76, 60, 0.02)')

      ctx.beginPath()
      ctx.moveTo(pointList[0].x, canvasHeight - padding.bottom)
      pointList.forEach((p) => ctx.lineTo(p.x, p.y))
      ctx.lineTo(pointList[pointList.length - 1].x, canvasHeight - padding.bottom)
      ctx.closePath()
      ctx.setFillStyle(gradient)
      ctx.fill()

      ctx.beginPath()
      ctx.setStrokeStyle('#e74c3c')
      ctx.setLineWidth(2)
      ctx.moveTo(pointList[0].x, pointList[0].y)
      for (let i = 1; i < pointList.length; i++) {
        ctx.lineTo(pointList[i].x, pointList[i].y)
      }
      ctx.stroke()
    }

    pointList.forEach((p) => {
      ctx.beginPath()
      ctx.arc(p.x, p.y, 3, 0, 2 * Math.PI)
      ctx.setFillStyle('#e74c3c')
      ctx.fill()
      ctx.beginPath()
      ctx.arc(p.x, p.y, 2, 0, 2 * Math.PI)
      ctx.setFillStyle('#fff')
      ctx.fill()
    })

    const labelInterval = Math.max(1, Math.floor(props.trends.length / 5))
    ctx.setFontSize(10)
    ctx.setFillStyle('#999')
    ctx.setTextAlign('center')
    pointList.forEach((p, i) => {
      if (i % labelInterval === 0 || i === pointList.length - 1) {
        const shortDate = p.date.length > 5 ? p.date.slice(-5) : p.date
        ctx.fillText(shortDate, p.x, canvasHeight - padding.bottom + 20)
      }
    })

    ctx.draw()
  }).exec()
}

function onTouchStart(e: any) {
  showTooltip(e)
}

function onTouchMove(e: any) {
  showTooltip(e)
}

function onTouchEnd() {
  tooltip.value.show = false
}

function showTooltip(e: any) {
  if (pointList.length === 0) return
  const touch = e.touches[0]
  let closest = pointList[0]
  let minDist = Infinity
  pointList.forEach((p) => {
    const dist = Math.abs(p.x - (touch.x || 0))
    if (dist < minDist) {
      minDist = dist
      closest = p
    }
  })
  tooltip.value = {
    show: true,
    x: closest.x,
    y: closest.y - 50,
    date: closest.date,
    price: closest.price
  }
}

watch(
  () => props.trends,
  () => {
    nextTick(() => {
      drawChart()
    })
  },
  { deep: true }
)

onMounted(() => {
  nextTick(() => {
    drawChart()
  })
})
</script>

<style scoped>
.chart-wrap {
  position: relative;
  width: 100%;
  height: 100%;
}

.chart-canvas {
  width: 100%;
  height: 100%;
}

.tooltip {
  position: absolute;
  background: rgba(0, 0, 0, 0.75);
  border-radius: 8rpx;
  padding: 10rpx 16rpx;
  transform: translateX(-50%);
  pointer-events: none;
}

.tooltip-text {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
}

.tooltip-price {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
  display: block;
  margin-top: 4rpx;
}
</style>
