<template>
  <div ref="chartRef" style="width: 80px; height: 30px" />
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = withDefaults(defineProps<{
  data: number[]
  color?: string
}>(), {
  color: '#e74c3c',
})

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

function render() {
  if (!chartRef.value || props.data.length === 0) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption({
    animation: false,
    grid: { top: 2, right: 0, bottom: 2, left: 0 },
    xAxis: { type: 'category', show: false, boundaryGap: false },
    yAxis: { type: 'value', show: false, min: 'dataMin', max: 'dataMax' },
    series: [
      {
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 1.5, color: props.color },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: props.color + '40' },
            { offset: 1, color: props.color + '05' },
          ]),
        },
        data: props.data,
      },
    ],
  })
}

watch(() => [props.data, props.color], render, { deep: true })

onMounted(() => {
  render()
})

onUnmounted(() => {
  chart?.dispose()
  chart = null
})
</script>
