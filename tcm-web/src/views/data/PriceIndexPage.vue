<template>
  <div class="price-index">
    <el-card shadow="hover" class="price-index__hero">
      <div class="price-index__hero-inner">
        <div class="price-index__hero-left">
          <div class="price-index__hero-label">{{ currentTypeName }}</div>
          <div class="price-index__hero-value">
            {{ latestData ? Number(latestData.indexValue).toFixed(2) : '--' }}
          </div>
          <div v-if="latestData" class="price-index__hero-change" :class="changeClass">
            <el-icon><component :is="changeIcon" /></el-icon>
            <span>{{ changeText }}</span>
          </div>
        </div>
        <div class="price-index__hero-right">
          <el-tabs v-model="activeType" @tab-change="handleTypeChange">
            <el-tab-pane
              v-for="t in indexTypes"
              :key="t.value"
              :label="t.label"
              :name="t.value"
            />
          </el-tabs>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="price-index__chart-card">
      <template #header>
        <div class="price-index__chart-header">
          <span class="price-index__chart-title">指数走势</span>
          <el-radio-group v-model="activePeriod" size="small" @change="fetchData">
            <el-radio-button value="month">近1月</el-radio-button>
            <el-radio-button value="3month">近3月</el-radio-button>
            <el-radio-button value="6month">近6月</el-radio-button>
            <el-radio-button value="year">近1年</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div v-if="historyData.dates?.length" ref="chartRef" class="price-index__chart" />
      <el-empty v-else description="暂无指数数据" :image-size="100" />
    </el-card>

    <el-card shadow="hover" class="price-index__info-card">
      <template #header>
        <span class="price-index__info-title">指数构成说明</span>
      </template>
      <div class="price-index__info-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="综合指数">
            反映中药材市场整体价格变动趋势的综合指数，以主要大宗中药材品种为样本，按加权平均法计算。
          </el-descriptions-item>
          <el-descriptions-item label="根茎类指数">
            以三七、白芍、当归、黄芪、甘草、地黄、人参、川芎、柴胡、桔梗等根茎类药材为样本编制。
          </el-descriptions-item>
          <el-descriptions-item label="果实类指数">
            以枸杞子、陈皮、连翘、五味子等果实种子类药材为样本编制。
          </el-descriptions-item>
          <el-descriptions-item label="花类指数">
            以金银花、菊花、红花等花类药材为样本编制。
          </el-descriptions-item>
          <el-descriptions-item label="全草类指数">
            以薄荷、藿香等全草类药材为样本编制。
          </el-descriptions-item>
        </el-descriptions>
        <div class="price-index__info-note">
          <el-icon><InfoFilled /></el-icon>
          <span>指数基期定为 2024年1月1日，基期指数为 1000 点。数据每日更新，仅供参考，不构成投资建议。</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { getPriceIndex, getLatestIndex } from '@/api/price'

const indexTypes = [
  { value: 'composite', label: '综合指数' },
  { value: 'root', label: '根茎类' },
  { value: 'fruit', label: '果实类' },
  { value: 'flower', label: '花类' },
  { value: 'herb', label: '全草类' },
]

const activeType = ref('composite')
const activePeriod = ref('month')

interface LatestIndexData {
  indexType: string
  indexTypeName: string
  indexValue: number
  changeRate: number
}

interface HistoryData {
  indexType: string
  indexTypeName: string
  indexValue: number
  changeRate: number
  dates: string[]
  values: number[]
}

const latestData = ref<LatestIndexData | null>(null)
const historyData = ref<HistoryData>({
  indexType: '',
  indexTypeName: '',
  indexValue: 0,
  changeRate: 0,
  dates: [],
  values: [],
})

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const currentTypeName = computed(() => {
  const found = indexTypes.find((t) => t.value === activeType.value)
  return found ? found.label : '综合指数'
})

const changeClass = computed(() => {
  if (!latestData.value) return ''
  const rate = Number(latestData.value.changeRate)
  if (rate > 0) return 'price-index__hero-change--up'
  if (rate < 0) return 'price-index__hero-change--down'
  return ''
})

const changeIcon = computed(() => {
  if (!latestData.value) return 'Minus'
  const rate = Number(latestData.value.changeRate)
  if (rate > 0) return 'Top'
  if (rate < 0) return 'Bottom'
  return 'Minus'
})

const changeText = computed(() => {
  if (!latestData.value) return '--'
  const rate = Number(latestData.value.changeRate)
  const pct = (rate * 100).toFixed(2)
  if (rate > 0) return `+${pct}%`
  if (rate < 0) return `${pct}%`
  return `${pct}%`
})

function renderChart() {
  if (!chartRef.value || !historyData.value.dates?.length) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }

  const dates = historyData.value.dates.map((d: string) => {
    const parts = d.split('-')
    if (parts.length === 3) return `${parts[1]}/${parts[2]}`
    return d
  })
  const values = historyData.value.values

  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        formatter(params: any) {
          const p = Array.isArray(params) ? params[0] : params
          return `${p.axisValue}<br/>指数: ${Number(p.value).toFixed(2)}`
        },
      },
      grid: { top: 40, right: 30, bottom: 40, left: 70 },
      xAxis: {
        type: 'category',
        data: dates,
        boundaryGap: false,
        axisLabel: { fontSize: 11, color: '#999' },
        axisLine: { lineStyle: { color: '#e0e0e0' } },
      },
      yAxis: {
        type: 'value',
        scale: true,
        axisLabel: { fontSize: 11, color: '#999' },
        splitLine: { lineStyle: { color: '#f0f0f0' } },
      },
      series: [
        {
          type: 'line',
          smooth: true,
          symbol: 'none',
          lineStyle: { width: 2.5, color: '#e74c3c' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(231,76,60,0.30)' },
              { offset: 1, color: 'rgba(231,76,60,0.02)' },
            ]),
          },
          data: values,
        },
      ],
    },
    true,
  )
}

function handleResize() {
  chart?.resize()
}

async function fetchLatest() {
  try {
    const res = (await getLatestIndex({ indexType: activeType.value })) as any
    latestData.value = res || null
  } catch {
    latestData.value = null
  }
}

async function fetchHistory() {
  try {
    const res = (await getPriceIndex({
      indexType: activeType.value,
      period: activePeriod.value,
    })) as any
    const list = Array.isArray(res) ? res : []
    if (list.length > 0) {
      historyData.value = list[0]
    } else {
      historyData.value = { indexType: '', indexTypeName: '', indexValue: 0, changeRate: 0, dates: [], values: [] }
    }
  } catch {
    historyData.value = { indexType: '', indexTypeName: '', indexValue: 0, changeRate: 0, dates: [], values: [] }
  }
  await nextTick()
  renderChart()
}

async function fetchData() {
  await Promise.all([fetchLatest(), fetchHistory()])
}

function handleTypeChange() {
  fetchData()
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  await fetchData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.price-index {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;

  &__hero {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 24px 32px;
    }
  }

  &__hero-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24px;
  }

  &__hero-left {
    flex-shrink: 0;
  }

  &__hero-label {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 8px;
  }

  &__hero-value {
    font-size: 42px;
    font-weight: 700;
    color: $text-color;
    line-height: 1.2;
    letter-spacing: -1px;
  }

  &__hero-change {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 16px;
    font-weight: 600;
    margin-top: 8px;
    padding: 2px 10px;
    border-radius: 4px;

    &--up {
      color: #e74c3c;
      background: rgba(231, 76, 60, 0.08);
    }

    &--down {
      color: #27ae60;
      background: rgba(39, 174, 96, 0.08);
    }
  }

  &__hero-right {
    flex: 1;
    min-width: 0;

    :deep(.el-tabs__header) {
      margin-bottom: 0;
    }

    :deep(.el-tabs__nav-wrap::after) {
      display: none;
    }

    :deep(.el-tabs__item) {
      font-size: 14px;
      font-weight: 500;
    }
  }

  &__chart-card {
    margin-bottom: 16px;

    :deep(.el-card__header) {
      padding: 12px 20px;
    }
  }

  &__chart-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__chart-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-color;
  }

  &__chart {
    width: 100%;
    height: 400px;
  }

  &__info-card {
    :deep(.el-card__header) {
      padding: 12px 20px;
    }
  }

  &__info-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-color;
  }

  &__info-content {
    :deep(.el-descriptions__label) {
      width: 120px;
      font-weight: 600;
      color: $text-color;
    }
  }

  &__info-note {
    display: flex;
    align-items: flex-start;
    gap: 6px;
    margin-top: 16px;
    padding: 12px;
    background: #fdf6ec;
    border-radius: 6px;
    font-size: 13px;
    color: #e6a23c;
    line-height: 1.6;

    .el-icon {
      flex-shrink: 0;
      margin-top: 3px;
    }
  }
}

@media (max-width: 768px) {
  .price-index {
    &__hero-inner {
      flex-direction: column;
      align-items: flex-start;
    }

    &__hero-value {
      font-size: 32px;
    }

    &__chart {
      height: 280px;
    }
  }
}
</style>
