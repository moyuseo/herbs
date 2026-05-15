<template>
  <div class="ranking">
    <div class="ranking__page-title">
      <h1>涨跌排行</h1>
    </div>

    <div class="ranking__controls">
      <div class="ranking__period-pills">
        <button
          v-for="p in periodOptions"
          :key="p.value"
          class="ranking__period-pill"
          :class="{ 'ranking__period-pill--active': periodType === p.value }"
          @click="periodType = p.value; fetchData()"
        >
          {{ p.label }}
        </button>
      </div>

      <div class="ranking__stats">
        <div class="ranking__stat ranking__stat--up">
          <span class="ranking__stat-value">{{ upCount }}</span>
          <span class="ranking__stat-label">上涨</span>
        </div>
        <div class="ranking__stat ranking__stat--down">
          <span class="ranking__stat-value">{{ downCount }}</span>
          <span class="ranking__stat-label">下跌</span>
        </div>
        <div class="ranking__stat">
          <span class="ranking__stat-value">{{ flatCount }}</span>
          <span class="ranking__stat-label">持平</span>
        </div>
      </div>
    </div>

    <div class="ranking__charts">
      <div class="ranking__chart-col ranking__chart-col--up">
        <h3 class="ranking__chart-title ranking__chart-title--up">
          <span class="ranking__chart-title-icon">▲</span>
          涨幅榜 Top20
        </h3>
        <div class="ranking__list">
          <div
            v-for="(item, idx) in upList"
            :key="item.herbId"
            class="ranking__item ranking__item--up"
            @click="router.push({ name: 'PriceDetail', params: { herbId: item.herbId } })"
          >
            <span
              class="ranking__rank"
              :class="{
                'ranking__rank--gold': idx === 0,
                'ranking__rank--silver': idx === 1,
                'ranking__rank--bronze': idx === 2,
              }"
            >
              {{ idx + 1 }}
            </span>
            <span class="ranking__item-name">{{ item.herbName }}</span>
            <span class="ranking__item-rate ranking__item-rate--up">+{{ item.change.toFixed(2) }}%</span>
          </div>
          <div v-if="upList.length === 0" class="ranking__empty">暂无上涨品种</div>
        </div>
        <div ref="upChartRef" class="ranking__chart-container" />
      </div>

      <div class="ranking__chart-col ranking__chart-col--down">
        <h3 class="ranking__chart-title ranking__chart-title--down">
          <span class="ranking__chart-title-icon">▼</span>
          跌幅榜 Top20
        </h3>
        <div class="ranking__list">
          <div
            v-for="(item, idx) in downList"
            :key="item.herbId"
            class="ranking__item ranking__item--down"
            @click="router.push({ name: 'PriceDetail', params: { herbId: item.herbId } })"
          >
            <span
              class="ranking__rank"
              :class="{
                'ranking__rank--gold': idx === 0,
                'ranking__rank--silver': idx === 1,
                'ranking__rank--bronze': idx === 2,
              }"
            >
              {{ idx + 1 }}
            </span>
            <span class="ranking__item-name">{{ item.herbName }}</span>
            <span class="ranking__item-rate ranking__item-rate--down">{{ item.change.toFixed(2) }}%</span>
          </div>
          <div v-if="downList.length === 0" class="ranking__empty">暂无下跌品种</div>
        </div>
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

const periodOptions = [
  { label: '日', value: 'day' as const },
  { label: '周', value: 'week' as const },
  { label: '月', value: 'month' as const },
]

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
            { offset: 0, color: '#c0392b80' },
            { offset: 1, color: '#c0392b' },
          ]),
          borderRadius: [0, 4, 4, 0],
        },
        barWidth: 14,
        label: {
          show: true,
          position: 'right',
          formatter: '+{c}%',
          fontSize: 11,
          color: '#c0392b',
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
            { offset: 0, color: '#1a563280' },
            { offset: 1, color: '#1a5632' },
          ]),
          borderRadius: [0, 4, 4, 0],
        },
        barWidth: 14,
        label: {
          show: true,
          position: 'right',
          formatter: '{c}%',
          fontSize: 11,
          color: '#1a5632',
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
@import '@/styles/variables.scss';

.ranking {
  padding: 20px;
  max-width: $container-max;
  margin: 0 auto;
  background: $bg-warm;
  min-height: calc(100vh - #{$header-height});

  &__page-title {
    margin-bottom: 20px;

    h1 {
      margin: 0;
      font-family: $font-display;
      font-size: 26px;
      font-weight: 700;
      color: $text-color;
      position: relative;
      padding-left: 16px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 4px;
        bottom: 4px;
        width: 4px;
        background: $primary-color;
        border-radius: 2px;
      }
    }
  }

  &__controls {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 16px;
  }

  &__period-pills {
    display: flex;
    gap: 6px;
    background: $card-bg;
    border-radius: $radius-md;
    padding: 4px;
    box-shadow: $shadow-sm;
  }

  &__period-pill {
    border: none;
    background: transparent;
    padding: 8px 24px;
    font-size: 14px;
    font-weight: 500;
    color: $text-secondary;
    border-radius: $radius-sm;
    cursor: pointer;
    transition: all 0.25s;

    &--active {
      background: $primary-color;
      color: #fff;
      box-shadow: $shadow-sm;
    }

    &:hover:not(&--active) {
      background: $primary-lighter;
      color: $primary-color;
    }
  }

  &__stats {
    display: flex;
    gap: 24px;
  }

  &__stat {
    display: flex;
    align-items: center;
    gap: 6px;
    background: $card-bg;
    padding: 8px 16px;
    border-radius: $radius-sm;
    box-shadow: $shadow-sm;

    &--up .ranking__stat-value {
      color: $up-color;
    }

    &--down .ranking__stat-value {
      color: $down-color;
    }
  }

  &__stat-value {
    font-size: 18px;
    font-weight: 700;
    color: $text-color;
  }

  &__stat-label {
    font-size: 13px;
    color: $text-muted;
  }

  &__charts {
    display: flex;
    gap: 20px;
  }

  &__chart-col {
    flex: 1;
    background: $card-bg;
    border-radius: $radius-md;
    padding: 20px;
    box-shadow: $shadow-sm;
    border-top: 3px solid $border-color;

    &--up {
      border-top-color: $up-color;
    }

    &--down {
      border-top-color: $down-color;
    }
  }

  &__chart-title {
    margin: 0 0 16px;
    font-family: $font-display;
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
    display: flex;
    align-items: center;
    gap: 6px;

    &--up .ranking__chart-title-icon {
      color: $up-color;
    }

    &--down .ranking__chart-title-icon {
      color: $down-color;
    }
  }

  &__chart-title-icon {
    font-size: 14px;
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-bottom: 16px;
    max-height: 340px;
    overflow-y: auto;
  }

  &__item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 14px;
    border-radius: $radius-sm;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      box-shadow: $shadow-md;
      transform: translateX(2px);
    }

    &--up:hover {
      background: $danger-light;
    }

    &--down:hover {
      background: $primary-lighter;
    }
  }

  &__rank {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 700;
    border-radius: 50%;
    background: $bg-warm;
    color: $text-secondary;
    flex-shrink: 0;

    &--gold {
      background: linear-gradient(135deg, #c8953e, #e8b84e);
      color: #fff;
      box-shadow: 0 2px 6px rgba(200, 149, 62, 0.4);
    }

    &--silver {
      background: linear-gradient(135deg, #95a5a6, #bdc3c7);
      color: #fff;
      box-shadow: 0 2px 6px rgba(149, 165, 166, 0.4);
    }

    &--bronze {
      background: linear-gradient(135deg, #a0522d, #cd853f);
      color: #fff;
      box-shadow: 0 2px 6px rgba(160, 82, 45, 0.4);
    }
  }

  &__item-name {
    flex: 1;
    font-size: 14px;
    color: $text-color;
    font-weight: 500;
  }

  &__item-rate {
    font-size: 14px;
    font-weight: 600;
    font-variant-numeric: tabular-nums;

    &--up {
      color: $up-color;
    }

    &--down {
      color: $down-color;
    }
  }

  &__empty {
    text-align: center;
    color: $text-muted;
    padding: 24px;
    font-size: 14px;
  }

  &__chart-container {
    width: 100%;
    height: 500px;
  }
}

@media (max-width: 900px) {
  .ranking__charts {
    flex-direction: column;
  }

  .ranking__controls {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
