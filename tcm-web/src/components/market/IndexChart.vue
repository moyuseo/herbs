<template>
  <div class="index-chart">
    <v-chart class="index-chart__chart" :option="chartOption" autoresize />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, CanvasRenderer])

const COLOR_SEQUENCE = ['#2D8C4E', '#F56C6C', '#E6A23C', '#409EFF', '#909399', '#67C23A', '#B37FEB', '#FF85C0']

const props = defineProps({
  indexData: {
    type: Array,
    default: () => []
  },
  indexTypes: {
    type: Array,
    default: () => []
  }
})

const chartOption = computed(() => {
  if (!props.indexData.length || !props.indexTypes.length) {
    return {}
  }

  const dates = [...new Set(props.indexData.map(item => item.date))].sort()

  const series = props.indexTypes.map((typeItem, idx) => {
    const data = dates.map(date => {
      const found = props.indexData.find(d => d.date === date && d.type === typeItem.key)
      return found ? found.value : null
    })

    return {
      name: typeItem.name,
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 2 },
      itemStyle: { color: COLOR_SEQUENCE[idx % COLOR_SEQUENCE.length] },
      data
    }
  })

  return {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: props.indexTypes.map(t => t.name),
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '14%',
      top: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100 }
    ],
    series
  }
})
</script>

<style lang="scss" scoped>
.index-chart {
  background: #fff;
  border-radius: 8px;
  padding: 20px;

  &__chart {
    width: 100%;
    height: 400px;
  }
}
</style>
