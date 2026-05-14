<template>
  <div class="price-chart">
    <div class="price-chart__toolbar">
      <el-radio-group v-model="period" size="small" @change="fetchData">
        <el-radio-button value="day">日</el-radio-button>
        <el-radio-button value="week">周</el-radio-button>
        <el-radio-button value="month">月</el-radio-button>
        <el-radio-button value="year">年</el-radio-button>
      </el-radio-group>
      <el-radio-group v-model="chartType" size="small" @change="renderChart">
        <el-radio-button value="area">面积图</el-radio-button>
        <el-radio-button value="candlestick">K线图</el-radio-button>
      </el-radio-group>
    </div>
    <div ref="chartRef" class="price-chart__container" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getPriceHistory } from '@/api/price'

const props = defineProps<{
  herbId: number
  herbName: string
}>()

const period = ref<'day' | 'week' | 'month' | 'year'>('day')
const chartType = ref<'area' | 'candlestick'>('area')
const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const periodMap: Record<string, string> = {
  day: '30',
  week: '90',
  month: '180',
  year: '365',
}

interface HistoryItem {
  date: string
  price: number
  open?: number
  high?: number
  low?: number
  close?: number
}

let historyData = ref<HistoryItem[]>([])

function generateMockCandlestick(data: HistoryItem[]): HistoryItem[] {
  return data.map((item) => {
    const base = item.price
    const fluctuation = base * 0.03
    const open = base + (Math.random() - 0.5) * fluctuation
    const close = base + (Math.random() - 0.5) * fluctuation
    const high = Math.max(open, close) + Math.random() * fluctuation * 0.5
    const low = Math.min(open, close) - Math.random() * fluctuation * 0.5
    return { ...item, open, high, low, close }
  })
}

async function fetchData() {
  try {
    const res = await getPriceHistory(props.herbId, { period: periodMap[period.value] }) as any
    historyData.value = Array.isArray(res) ? res : res?.list || res?.data || []
  } catch {
    historyData.value = []
  }
  renderChart()
}

function renderChart() {
  if (!chartRef.value) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }

  const data = historyData.value
  if (data.length === 0) {
    chart.clear()
    return
  }

  const dates = data.map((d) => d.date)
  const prices = data.map((d) => d.price)

  if (chartType.value === 'area') {
    chart.setOption(
      {
        tooltip: {
          trigger: 'axis',
          formatter(params: any) {
            const p = Array.isArray(params) ? params[0] : params
            return `${p.axisValue}<br/>价格: ¥${Number(p.value).toFixed(2)}`
          },
        },
        grid: { top: 30, right: 20, bottom: 60, left: 60 },
        xAxis: {
          type: 'category',
          data: dates,
          boundaryGap: false,
          axisLabel: { fontSize: 11 },
        },
        yAxis: {
          type: 'value',
          scale: true,
          axisLabel: { fontSize: 11, formatter: '¥{value}' },
        },
        dataZoom: [
          { type: 'inside', start: 0, end: 100 },
          { type: 'slider', start: 0, end: 100, height: 20 },
        ],
        series: [
          {
            type: 'line',
            smooth: true,
            symbol: 'none',
            lineStyle: { width: 2, color: '#e74c3c' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(231,76,60,0.35)' },
                { offset: 1, color: 'rgba(231,76,60,0.05)' },
              ]),
            },
            data: prices,
          },
        ],
      },
      true,
    )
  } else {
    const candleData = generateMockCandlestick(data)
    chart.setOption(
      {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter(params: any) {
            const p = Array.isArray(params) ? params[0] : params
            const d = candleData[p.dataIndex]
            return `${d.date}<br/>开盘: ¥${d.open?.toFixed(2)}<br/>收盘: ¥${d.close?.toFixed(2)}<br/>最高: ¥${d.high?.toFixed(2)}<br/>最低: ¥${d.low?.toFixed(2)}`
          },
        },
        grid: { top: 30, right: 20, bottom: 60, left: 60 },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: { fontSize: 11 },
        },
        yAxis: {
          type: 'value',
          scale: true,
          axisLabel: { fontSize: 11, formatter: '¥{value}' },
        },
        dataZoom: [
          { type: 'inside', start: 0, end: 100 },
          { type: 'slider', start: 0, end: 100, height: 20 },
        ],
        series: [
          {
            type: 'candlestick',
            data: candleData.map((d) => [d.open, d.close, d.low, d.high]),
            itemStyle: {
              color: '#e74c3c',
              color0: '#27ae60',
              borderColor: '#e74c3c',
              borderColor0: '#27ae60',
            },
          },
        ],
      },
      true,
    )
  }
}

function handleResize() {
  chart?.resize()
}

watch(() => props.herbId, () => {
  fetchData()
})

onMounted(async () => {
  await fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})
</script>

<style scoped lang="scss">
.price-chart {
  &__toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  &__container {
    width: 100%;
    height: 400px;
  }
}
</style>
