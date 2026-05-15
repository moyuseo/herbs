<template>
  <div class="price-index">
    <div class="price-index__hero">
      <div class="price-index__hero-bg"></div>
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
          <div class="price-index__type-pills">
            <button
              v-for="t in indexTypes"
              :key="t.value"
              class="price-index__type-pill"
              :class="{ 'price-index__type-pill--active': activeType === t.value }"
              @click="activeType = t.value; handleTypeChange()"
            >
              {{ t.label }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="price-index__chart-card">
      <div class="price-index__chart-header">
        <span class="price-index__chart-title">指数走势</span>
        <div class="price-index__period-pills">
          <button
            v-for="p in periods"
            :key="p.value"
            class="price-index__period-pill"
            :class="{ 'price-index__period-pill--active': activePeriod === p.value }"
            @click="activePeriod = p.value; fetchData()"
          >
            {{ p.label }}
          </button>
        </div>
      </div>
      <div v-if="historyData.dates?.length" ref="chartRef" class="price-index__chart" />
      <el-empty v-else description="暂无指数数据" :image-size="100" />
    </div>

    <div class="price-index__info-card">
      <div class="price-index__info-header">
        <span class="price-index__info-title">指数构成说明</span>
      </div>
      <div class="price-index__info-content">
        <div class="price-index__info-list">
          <div class="price-index__info-item" v-for="item in indexDescriptions" :key="item.label">
            <div class="price-index__info-label">{{ item.label }}</div>
            <div class="price-index__info-desc">{{ item.desc }}</div>
          </div>
        </div>
        <div class="price-index__info-note">
          <el-icon><InfoFilled /></el-icon>
          <span>指数基期定为 2024年1月1日，基期指数为 1000 点。数据每日更新，仅供参考，不构成投资建议。</span>
        </div>
      </div>
    </div>
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

const periods = [
  { value: 'month', label: '近1月' },
  { value: '3month', label: '近3月' },
  { value: '6month', label: '近6月' },
  { value: 'year', label: '近1年' },
]

const indexDescriptions = [
  { label: '综合指数', desc: '反映中药材市场整体价格变动趋势的综合指数，以主要大宗中药材品种为样本，按加权平均法计算。' },
  { label: '根茎类指数', desc: '以三七、白芍、当归、黄芪、甘草、地黄、人参、川芎、柴胡、桔梗等根茎类药材为样本编制。' },
  { label: '果实类指数', desc: '以枸杞子、陈皮、连翘、五味子等果实种子类药材为样本编制。' },
  { label: '花类指数', desc: '以金银花、菊花、红花等花类药材为样本编制。' },
  { label: '全草类指数', desc: '以薄荷、藿香等全草类药材为样本编制。' },
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
  if (chart) {
    chart.dispose()
    chart = null
  }
  chart = echarts.init(chartRef.value)

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

watch(
  () => [historyData.value.dates, chartRef.value] as const,
  () => {
    if (historyData.value.dates?.length && chartRef.value) {
      nextTick(() => renderChart())
    }
  },
  { flush: 'post' },
)

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.price-index {
  max-width: $container-max;
  margin: 0 auto;
  padding: 20px 16px;

  &__hero {
    position: relative;
    border-radius: $radius-lg;
    overflow: hidden;
    margin-bottom: 20px;
    padding: 36px 40px;
    color: #fff;
  }

  &__hero-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, $primary-dark 0%, $primary-color 50%, $primary-light 100%);
    z-index: 0;

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
    }
  }

  &__hero-inner {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 32px;
  }

  &__hero-left {
    flex-shrink: 0;
  }

  &__hero-label {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.75);
    margin-bottom: 8px;
    letter-spacing: 1px;
  }

  &__hero-value {
    font-family: $font-display;
    font-size: 52px;
    font-weight: 700;
    color: #fff;
    line-height: 1.1;
    letter-spacing: -1px;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }

  &__hero-change {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 16px;
    font-weight: 600;
    margin-top: 10px;
    padding: 4px 14px;
    border-radius: 20px;

    &--up {
      color: #ffd6d6;
      background: rgba(255, 255, 255, 0.15);
    }

    &--down {
      color: #c8f7d5;
      background: rgba(255, 255, 255, 0.15);
    }
  }

  &__hero-right {
    flex: 1;
    min-width: 0;
    display: flex;
    justify-content: flex-end;
  }

  &__type-pills {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: flex-end;
  }

  &__type-pill {
    padding: 8px 20px;
    border-radius: 24px;
    border: 1.5px solid rgba(255, 255, 255, 0.3);
    background: rgba(255, 255, 255, 0.08);
    color: rgba(255, 255, 255, 0.85);
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.25s ease;
    backdrop-filter: blur(4px);

    &:hover {
      background: rgba(255, 255, 255, 0.18);
      border-color: rgba(255, 255, 255, 0.5);
    }

    &--active {
      background: $accent-color;
      border-color: $accent-color;
      color: #fff;
      box-shadow: 0 2px 8px rgba($accent-color, 0.4);

      &:hover {
        background: darken(#c8953e, 5%);
        border-color: darken(#c8953e, 5%);
      }
    }
  }

  &__chart-card {
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-md;
    padding: 24px;
    margin-bottom: 20px;
    border: 1px solid $border-light;
  }

  &__chart-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }

  &__chart-title {
    font-family: $font-display;
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
    position: relative;
    padding-left: 14px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 20px;
      background: $primary-color;
      border-radius: 2px;
    }
  }

  &__period-pills {
    display: flex;
    gap: 6px;
    background: $bg-warm;
    padding: 3px;
    border-radius: 22px;
    border: 1px solid $border-light;
  }

  &__period-pill {
    padding: 6px 16px;
    border-radius: 18px;
    border: none;
    background: transparent;
    color: $text-secondary;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      color: $primary-color;
    }

    &--active {
      background: $primary-color;
      color: #fff;
      box-shadow: 0 1px 4px rgba($primary-color, 0.3);

      &:hover {
        color: #fff;
      }
    }
  }

  &__chart {
    width: 100%;
    height: 400px;
  }

  &__info-card {
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
    border: 1px solid $border-light;
    overflow: hidden;
  }

  &__info-header {
    padding: 16px 24px;
    border-bottom: 1px solid $border-light;
    background: $bg-warm;
  }

  &__info-title {
    font-family: $font-display;
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
    position: relative;
    padding-left: 14px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 20px;
      background: $accent-color;
      border-radius: 2px;
    }
  }

  &__info-content {
    padding: 20px 24px;
  }

  &__info-list {
    display: flex;
    flex-direction: column;
    gap: 0;
  }

  &__info-item {
    display: flex;
    gap: 16px;
    padding: 14px 0;
    border-bottom: 1px solid $border-light;

    &:last-child {
      border-bottom: none;
    }
  }

  &__info-label {
    flex-shrink: 0;
    width: 100px;
    font-weight: 600;
    font-size: 14px;
    color: $primary-color;
    padding: 4px 0;
    border-left: 3px solid $accent-color;
    padding-left: 12px;
  }

  &__info-desc {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.7;
  }

  &__info-note {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-top: 16px;
    padding: 14px 16px;
    background: $accent-lighter;
    border-radius: $radius-sm;
    border-left: 3px solid $accent-color;
    font-size: 13px;
    color: $accent-color;
    line-height: 1.6;

    .el-icon {
      flex-shrink: 0;
      margin-top: 3px;
    }
  }
}

@media (max-width: 768px) {
  .price-index {
    &__hero {
      padding: 24px 20px;
    }

    &__hero-inner {
      flex-direction: column;
      align-items: flex-start;
    }

    &__hero-value {
      font-size: 36px;
    }

    &__hero-right {
      width: 100%;
    }

    &__type-pills {
      justify-content: flex-start;
    }

    &__type-pill {
      padding: 6px 14px;
      font-size: 13px;
    }

    &__chart {
      height: 280px;
    }

    &__chart-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    &__info-item {
      flex-direction: column;
      gap: 6px;
    }

    &__info-label {
      border-left: none;
      padding-left: 0;
    }
  }
}
</style>
