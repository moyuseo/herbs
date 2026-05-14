<template>
  <div class="ranking">
    <el-tabs v-model="periodType" @tab-change="fetchData">
      <el-tab-pane label="日涨跌" name="day" />
      <el-tab-pane label="周涨跌" name="week" />
      <el-tab-pane label="月涨跌" name="month" />
    </el-tabs>

    <div class="ranking__stats">
      <el-statistic title="上涨品种" :value="upCount" />
      <el-statistic title="下跌品种" :value="downCount" />
      <el-statistic title="持平品种" :value="flatCount" />
    </div>

    <div class="ranking__charts">
      <div class="ranking__chart-col">
        <h3 class="ranking__chart-title">涨幅榜 Top20</h3>
        <div ref="upChartRef" class="ranking__chart-container" />
      </div>
      <div class="ranking__chart-col">
        <h3 class="ranking__chart-title">跌幅榜 Top20</h3>
        <div ref="downChartRef" class="ranking__chart-container" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getRanking } from '@/api/price'

const router = useRouter()

const periodType = ref<'day' | 'week' | 'month'>('day')
const upChartRef = ref<HTMLDivElement>()
const downChartRef = ref<HTMLDivElement>()
let upChart: echarts.ECharts | null = null
let downChart: echarts.ECharts | null = null

const upCount = ref(0)
const downCount = ref(0)
const flatCount = ref(0)

interface RankingItem {
  herbId: number
  herbName: string
  changeRate: number
  change: number
  currentPrice: number
  trend: string
}

let upList = ref<RankingItem[]>([])
let downList = ref<RankingItem[]>([])

function renderUpChart() {
  if (!upChartRef.value || upList.value.length === 0) return
  if (!upChart) {
    upChart = echarts.init(upChartRef.value)
  }
  const data = upList.value.slice(0, 20).reverse()
  upChart.setOption({
    animation: true,
    grid: { top: 10, right: 60, bottom: 10, left: 80 },
    xAxis: { type: 'value', show: false },
    yAxis: {
      type: 'category',
      data: data.map((d) => d.herbName),
      axisLabel: {
        fontSize: 12,
        formatter(value: string) {
          return value.length > 6 ? value.slice(0, 6) + '…' : value
        },
      },
      triggerEvent: true,
    },
    tooltip: {
      trigger: 'axis',
      formatter(params: any) {
        const p = Array.isArray(params) ? params[0] : params
        return `${p.name}: +${Number(p.value).toFixed(2)}%`
      },
    },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.change),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#e74c3c80' },
            { offset: 1, color: '#e74c3c' },
          ]),
          borderRadius: [0, 4, 4, 0],
        },
        barWidth: 14,
        label: {
          show: true,
          position: 'right',
          formatter: '+{c}%',
          fontSize: 11,
          color: '#e74c3c',
        },
      },
    ],
  }, true)

  upChart.off('click')
  upChart.on('click', (params: any) => {
    const item = data[params.dataIndex]
    if (item) {
      router.push({ name: 'PriceDetail', params: { herbId: item.herbId } })
    }
  })
}

function renderDownChart() {
  if (!downChartRef.value || downList.value.length === 0) return
  if (!downChart) {
    downChart = echarts.init(downChartRef.value)
  }
  const data = downList.value.slice(0, 20).reverse()
  downChart.setOption({
    animation: true,
    grid: { top: 10, right: 60, bottom: 10, left: 80 },
    xAxis: { type: 'value', show: false },
    yAxis: {
      type: 'category',
      data: data.map((d) => d.herbName),
      axisLabel: {
        fontSize: 12,
        formatter(value: string) {
          return value.length > 6 ? value.slice(0, 6) + '…' : value
        },
      },
      triggerEvent: true,
    },
    tooltip: {
      trigger: 'axis',
      formatter(params: any) {
        const p = Array.isArray(params) ? params[0] : params
        return `${p.name}: ${Number(p.value).toFixed(2)}%`
      },
    },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.change),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: '#27ae6080' },
            { offset: 1, color: '#27ae60' },
          ]),
          borderRadius: [0, 4, 4, 0],
        },
        barWidth: 14,
        label: {
          show: true,
          position: 'right',
          formatter: '{c}%',
          fontSize: 11,
          color: '#27ae60',
        },
      },
    ],
  }, true)

  downChart.off('click')
  downChart.on('click', (params: any) => {
    const item = data[params.dataIndex]
    if (item) {
      router.push({ name: 'PriceDetail', params: { herbId: item.herbId } })
    }
  })
}

async function fetchData() {
  try {
    const res = (await getRanking({ type: periodType.value })) as any
    const list = res?.list || res?.data || (Array.isArray(res) ? res : [])
    const mappedList = list.map((i: RankingItem) => ({
      ...i,
      change: i.changeRate ?? i.change ?? 0,
    }))
    upList.value = mappedList.filter((i: any) => i.change > 0).sort((a: any, b: any) => b.change - a.change)
    downList.value = mappedList.filter((i: any) => i.change < 0).sort((a: any, b: any) => a.change - b.change)
    upCount.value = upList.value.length
    downCount.value = downList.value.length
    flatCount.value = mappedList.filter((i: any) => i.change === 0).length
  } catch {
    upList.value = []
    downList.value = []
    upCount.value = 0
    downCount.value = 0
    flatCount.value = 0
  }
  await nextTick()
  renderUpChart()
  renderDownChart()
}

function handleResize() {
  upChart?.resize()
  downChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  upChart?.dispose()
  downChart?.dispose()
  upChart = null
  downChart = null
})
</script>

<style scoped lang="scss">
.ranking {
  padding: 16px;

  &__stats {
    display: flex;
    gap: 48px;
    background: #fff;
    padding: 20px 32px;
    border-radius: 4px;
    margin-bottom: 16px;
  }

  &__charts {
    display: flex;
    gap: 16px;
  }

  &__chart-col {
    flex: 1;
    background: #fff;
    border-radius: 4px;
    padding: 16px;
  }

  &__chart-title {
    margin: 0 0 8px;
    font-size: 15px;
    color: #303133;
  }

  &__chart-container {
    width: 100%;
    height: 500px;
  }
}
</style>
