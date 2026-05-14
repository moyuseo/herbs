<template>
  <div class="trend-chart">
    <div class="trend-chart__header">
      <span class="trend-chart__title">价格走势</span>
      <el-radio-group v-model="currentPeriod" size="small" @change="onPeriodChange">
        <el-radio-button value="1m">1月</el-radio-button>
        <el-radio-button value="3m">3月</el-radio-button>
        <el-radio-button value="6m">6月</el-radio-button>
        <el-radio-button value="1y">1年</el-radio-button>
        <el-radio-button value="3y">3年</el-radio-button>
        <el-radio-button value="all">全部</el-radio-button>
      </el-radio-group>
    </div>
    <v-chart class="trend-chart__chart" :option="chartOption" autoresize />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, CanvasRenderer])

const props = defineProps({
  historyData: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['period-change'])

const currentPeriod = ref('1y')

function onPeriodChange(val) {
  emit('period-change', val)
}

const chartOption = computed(() => {
  const dates = props.historyData.map(item => item.date)
  const prices = props.historyData.map(item => item.price)

  return {
    tooltip: {
      trigger: 'axis',
      formatter(params) {
        const d = params[0]
        return `${d.axisValue}<br/>价格：¥${d.value}`
      }
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
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100 }
    ],
    series: [
      {
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { color: '#2D8C4E', width: 2 },
        itemStyle: { color: '#2D8C4E' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(45,140,78,0.35)' },
              { offset: 1, color: 'rgba(45,140,78,0.05)' }
            ]
          }
        },
        data: prices
      }
    ]
  }
})
</script>

<style lang="scss" scoped>
.trend-chart {
  background: #fff;
  border-radius: 8px;
  padding: 20px;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  &__chart {
    width: 100%;
    height: 400px;
  }
}
</style>
