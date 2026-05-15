<template>
  <div class="home">
    <section class="home__hero">
      <div class="home__hero-bg" />
      <div class="home__hero-inner">
        <div class="home__hero-text">
          <h1 class="home__hero-title">中药材行情平台</h1>
          <p class="home__hero-subtitle">实时掌握市场动态，洞悉价格走势</p>
          <div class="home__hero-gold-line" />
        </div>
        <div class="home__hero-stats">
          <div class="home__stat-card home__stat-card--index">
            <span class="home__stat-label">综合指数</span>
            <span class="home__stat-value">{{ latestIndexValue }}</span>
            <span
              v-if="latestIndexChange !== null"
              class="home__stat-badge"
              :class="latestIndexChange >= 0 ? 'home__stat-badge--up' : 'home__stat-badge--down'"
            >
              {{ latestIndexChange >= 0 ? '+' : '' }}{{ (latestIndexChange * 100).toFixed(2) }}%
            </span>
          </div>
          <div class="home__stat-card home__stat-card--up">
            <span class="home__stat-label">今日上涨</span>
            <span class="home__stat-value home__stat-value--up">{{ summary?.upCount ?? '--' }}</span>
          </div>
          <div class="home__stat-card home__stat-card--down">
            <span class="home__stat-label">今日下跌</span>
            <span class="home__stat-value home__stat-value--down">{{ summary?.downCount ?? '--' }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="home__section home__section--chart">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="home__panel home__panel--chart">
            <div class="home__panel-header">
              <h2 class="home__section-title">综合价格指数走势</h2>
              <span class="home__panel-link" @click="router.push({ name: 'PriceIndex' })">详情 →</span>
            </div>
            <div v-if="indexData.length" class="home__index-summary">
              <span class="home__index-num">{{ latestIndexValue }}</span>
              <span
                v-if="latestIndexChange !== null"
                class="home__index-change"
                :class="latestIndexChange >= 0 ? 'home__index-change--up' : 'home__index-change--down'"
              >
                {{ latestIndexChange >= 0 ? '+' : '' }}{{ (latestIndexChange * 100).toFixed(2) }}%
              </span>
            </div>
            <div v-if="indexData.length" ref="indexChartRef" class="home__chart" />
            <el-empty v-else description="暂无指数数据" :image-size="80" />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="home__panel home__panel--summary">
            <div class="home__panel-header">
              <h2 class="home__section-title">今日涨跌概览</h2>
            </div>
            <template v-if="summary">
              <div class="home__summary-bars">
                <div class="home__summary-bar-item">
                  <div class="home__summary-bar-head">
                    <span class="home__summary-bar-label">上涨</span>
                    <span class="home__summary-bar-num home__summary-bar-num--up">{{ summary.upCount }}</span>
                  </div>
                  <div class="home__summary-bar-track">
                    <div
                      class="home__summary-bar-fill home__summary-bar-fill--up"
                      :style="{ width: ((summary.upCount / Math.max(summary.upCount + summary.downCount + (summary.stableCount || summary.flatCount || 0), 1)) * 100).toFixed(1) + '%' }"
                    />
                  </div>
                </div>
                <div class="home__summary-bar-item">
                  <div class="home__summary-bar-head">
                    <span class="home__summary-bar-label">下跌</span>
                    <span class="home__summary-bar-num home__summary-bar-num--down">{{ summary.downCount }}</span>
                  </div>
                  <div class="home__summary-bar-track">
                    <div
                      class="home__summary-bar-fill home__summary-bar-fill--down"
                      :style="{ width: ((summary.downCount / Math.max(summary.upCount + summary.downCount + (summary.stableCount || summary.flatCount || 0), 1)) * 100).toFixed(1) + '%' }"
                    />
                  </div>
                </div>
                <div class="home__summary-bar-item">
                  <div class="home__summary-bar-head">
                    <span class="home__summary-bar-label">持平</span>
                    <span class="home__summary-bar-num home__summary-bar-num--flat">{{ summary.stableCount || summary.flatCount || 0 }}</span>
                  </div>
                  <div class="home__summary-bar-track">
                    <div
                      class="home__summary-bar-fill home__summary-bar-fill--flat"
                      :style="{ width: (((summary.stableCount || summary.flatCount || 0) / Math.max(summary.upCount + summary.downCount + (summary.stableCount || summary.flatCount || 0), 1)) * 100).toFixed(1) + '%' }"
                    />
                  </div>
                </div>
              </div>
              <div class="home__top-list">
                <div class="home__top-section">
                  <div class="home__top-heading home__top-heading--up">涨幅前三</div>
                  <div v-for="item in summary.topGainers" :key="item.herbName || item.name" class="home__top-row">
                    <span class="home__top-name">{{ item.herbName || item.name }}</span>
                    <span class="home__top-value home__top-value--up">+{{ ((item.changeRate || item.change || 0)).toFixed(2) }}%</span>
                  </div>
                  <el-empty v-if="!summary.topGainers?.length" description="暂无数据" :image-size="40" />
                </div>
                <div class="home__top-section">
                  <div class="home__top-heading home__top-heading--down">跌幅前三</div>
                  <div v-for="item in summary.topLosers" :key="item.herbName || item.name" class="home__top-row">
                    <span class="home__top-name">{{ item.herbName || item.name }}</span>
                    <span class="home__top-value home__top-value--down">{{ ((item.changeRate || item.change || 0)).toFixed(2) }}%</span>
                  </div>
                  <el-empty v-if="!summary.topLosers?.length" description="暂无数据" :image-size="40" />
                </div>
              </div>
            </template>
            <el-empty v-else description="暂无涨跌数据" :image-size="80" />
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="home__section home__section--market">
      <div class="home__panel">
        <div class="home__panel-header">
          <h2 class="home__section-title">市场价格快览</h2>
        </div>
        <div class="home__market-tabs">
          <button
            v-for="m in markets"
            :key="m"
            class="home__market-tab"
            :class="{ 'home__market-tab--active': activeMarket === m }"
            @click="activeMarket = m; fetchMarketPrices()"
          >
            {{ m }}
          </button>
        </div>
        <el-table v-if="marketPrices.length" :data="marketPrices" class="home__market-table" :show-header="true">
          <el-table-column prop="herbName" label="品种" min-width="100">
            <template #default="{ row }">
              <span class="home__herb-link" @click="goPriceDetail(row.herbId)">{{ row.herbName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="spec" label="规格" min-width="80" />
          <el-table-column prop="price" label="今日价" min-width="90">
            <template #default="{ row }">
              <span class="home__price-value">¥{{ Number(row.price).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dayChange" label="日涨跌" min-width="100">
            <template #default="{ row }">
              <PriceTag :value="row.dayChange" />
            </template>
          </el-table-column>
          <el-table-column prop="monthChange" label="月涨跌" min-width="100">
            <template #default="{ row }">
              <PriceTag :value="row.monthChange" />
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无行情数据" :image-size="80" />
        <div v-if="marketPrices.length" class="home__more">
          <span class="home__more-link" @click="router.push({ name: 'MarketPrice' })">查看更多 →</span>
        </div>
      </div>
    </section>

    <section class="home__section">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="home__panel">
            <div class="home__panel-header">
              <h2 class="home__section-title">产地地图</h2>
            </div>
            <OriginMap />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="home__panel">
            <div class="home__panel-header">
              <h2 class="home__section-title">行情资讯</h2>
            </div>
            <template v-if="newsList.length">
              <div class="home__news">
                <div v-for="item in newsList" :key="item.id" class="home__news-item" @click="goNewsDetail(item.id)">
                  <span class="home__news-dot" :class="'home__news-dot--' + newsTagType(item.category)" />
                  <span class="home__news-title">{{ item.title }}</span>
                  <span class="home__news-time">{{ item.publishedAt || item.publishTime }}</span>
                </div>
              </div>
              <div class="home__more">
                <span class="home__more-link" @click="router.push({ name: 'NewsList' })">查看更多 →</span>
              </div>
            </template>
            <el-empty v-else description="暂无资讯" :image-size="80" />
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="home__section">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="home__panel">
            <div class="home__panel-header">
              <h2 class="home__section-title">供应信息</h2>
              <span class="home__panel-link" @click="router.push({ name: 'SupplyList' })">更多 →</span>
            </div>
            <div v-if="supplyList.length" class="home__card-list">
              <div v-for="(row, idx) in supplyList" :key="idx" class="home__card-item">
                <div class="home__card-item-head">
                  <span class="home__card-item-herb">{{ row.herbName }}</span>
                  <span class="home__card-item-price">¥{{ row.price }}</span>
                </div>
                <div class="home__card-item-meta">
                  <span v-if="row.spec">{{ row.spec }}</span>
                  <span v-if="row.origin">· {{ row.origin }}</span>
                  <span v-if="row.quantity">· 数量: {{ row.quantity }}{{ row.unit || '吨' }}</span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无供应信息" :image-size="80" />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="home__panel">
            <div class="home__panel-header">
              <h2 class="home__section-title">求购信息</h2>
              <span class="home__panel-link" @click="router.push({ name: 'DemandList' })">更多 →</span>
            </div>
            <div v-if="demandList.length" class="home__card-list">
              <div v-for="(row, idx) in demandList" :key="idx" class="home__card-item">
                <div class="home__card-item-head">
                  <span class="home__card-item-herb">{{ row.herbName }}</span>
                  <span class="home__card-item-badge">{{ row.quoteCount ?? row.bidderCount ?? 0 }}人报价</span>
                </div>
                <div class="home__card-item-meta">
                  <span v-if="row.spec">{{ row.spec }}</span>
                  <span>· 数量: {{ row.quantity }}{{ row.unit || '吨' }}</span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无求购信息" :image-size="80" />
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getPriceIndex, getLatestIndex, getMarketSummary, getMarketPrices } from '@/api/price'
import { getNewsList } from '@/api/news'
import { getSupplyList, getDemandList } from '@/api/supply'
import PriceTag from '@/components/price/PriceTag.vue'
import OriginMap from '@/components/map/OriginMap.vue'

const router = useRouter()

const markets = ['亳州', '安国', '成都', '玉林', '廉桥', '普宁']
const activeMarket = ref('亳州')

interface IndexItem {
  date: string
  value: number
}

interface TopItem {
  name: string
  herbName: string
  change: number
  changeRate: number
}

interface SummaryData {
  upCount: number
  downCount: number
  flatCount: number
  stableCount: number
  topGainers: TopItem[]
  topLosers: TopItem[]
}

interface MarketPriceItem {
  herbId: number
  herbName: string
  spec: string
  price: number
  dayChange: number
  monthChange: number
}

interface NewsItem {
  id: number
  title: string
  category: string
  publishedAt: string
  publishTime: string
}

interface SupplyItem {
  herbName: string
  spec: string
  origin: string
  quantity: number | string
  unit: string
  price: number | string
  priceType: number
  contactPhone: string
  createdAt: string
}

interface DemandItem {
  herbName: string
  spec: string
  quantity: number | string
  unit: string
  quoteCount: number
  bidderCount: number
  deliveryAddress: string
  createdAt: string
}

const indexData = ref<IndexItem[]>([])
const summary = ref<SummaryData | null>(null)
const marketPrices = ref<MarketPriceItem[]>([])
const newsList = ref<NewsItem[]>([])
const supplyList = ref<SupplyItem[]>([])
const demandList = ref<DemandItem[]>([])

const latestIndexValue = ref('--')
const latestIndexChange = ref<number | null>(null)

const indexChartRef = ref<HTMLDivElement>()
let indexChart: echarts.ECharts | null = null

function generateMockIndexData(): IndexItem[] {
  const data: IndexItem[] = []
  const now = new Date()
  let val = 2200
  for (let i = 29; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    val += (Math.random() - 0.48) * 30
    data.push({
      date: `${d.getMonth() + 1}/${d.getDate()}`,
      value: Math.round(val * 100) / 100,
    })
  }
  return data
}

function generateMockSummary(): SummaryData {
  const upCount = Math.floor(Math.random() * 80) + 40
  const downCount = Math.floor(Math.random() * 60) + 20
  const flatCount = Math.floor(Math.random() * 30) + 10
  const herbs = ['白芍', '三七', '黄芪', '当归', '枸杞', '人参', '柴胡', '地黄', '金银花', '菊花', '桔梗']
  const topGainers: TopItem[] = []
  const topLosers: TopItem[] = []
  const shuffled = [...herbs].sort(() => Math.random() - 0.5)
  for (let i = 0; i < 3; i++) {
    topGainers.push({ name: shuffled[i], change: +(Math.random() * 15 + 2).toFixed(2) })
    topLosers.push({ name: shuffled[i + 3], change: +(-(Math.random() * 12 + 2)).toFixed(2) })
  }
  return { upCount, downCount, flatCount, topGainers, topLosers }
}

function renderIndexChart() {
  if (!indexChartRef.value || !indexData.value.length) return
  if (!indexChart) {
    indexChart = echarts.init(indexChartRef.value)
  }
  const dates = indexData.value.map((d) => d.date)
  const values = indexData.value.map((d) => d.value)
  indexChart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        formatter(params: any) {
          const p = Array.isArray(params) ? params[0] : params
          return `${p.axisValue}<br/>指数: ${Number(p.value).toFixed(2)}`
        },
      },
      grid: { top: 30, right: 20, bottom: 30, left: 60 },
      xAxis: {
        type: 'category',
        data: dates,
        boundaryGap: false,
        axisLabel: { fontSize: 11 },
      },
      yAxis: {
        type: 'value',
        scale: true,
        axisLabel: { fontSize: 11 },
      },
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
          data: values,
        },
      ],
    },
    true,
  )
}

function handleResize() {
  indexChart?.resize()
}

async function fetchIndexData() {
  try {
    const res = (await getPriceIndex({ indexType: 'composite', period: 'month' })) as any
    const list = Array.isArray(res) ? res : []
    if (list.length > 0 && list[0].dates?.length) {
      const item = list[0]
      indexData.value = item.dates.map((d: string, i: number) => ({
        date: d.includes('-') ? `${d.split('-')[1]}/${d.split('-')[2]}` : d,
        value: Number(item.values[i]),
      }))
      if (item.indexValue != null) {
        latestIndexValue.value = Number(item.indexValue).toFixed(2)
      }
      if (item.changeRate != null) {
        latestIndexChange.value = Number(item.changeRate)
      }
    } else {
      indexData.value = generateMockIndexData()
    }
  } catch {
    indexData.value = generateMockIndexData()
  }
  try {
    const latest = (await getLatestIndex({ indexType: 'composite' })) as any
    if (latest) {
      if (latest.indexValue != null) {
        latestIndexValue.value = Number(latest.indexValue).toFixed(2)
      }
      if (latest.changeRate != null) {
        latestIndexChange.value = Number(latest.changeRate)
      }
    }
  } catch {
    // keep existing values
  }
  await nextTick()
  renderIndexChart()
}

async function fetchSummary() {
  try {
    const res = (await getMarketSummary()) as any
    if (res && (res.upCount !== undefined || res.topGainers)) {
      summary.value = res
    } else {
      summary.value = generateMockSummary()
    }
  } catch {
    summary.value = generateMockSummary()
  }
}

async function fetchMarketPrices() {
  try {
    const res = (await getMarketPrices({ market: activeMarket.value, pageSize: 10 })) as any
    const list = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    marketPrices.value = list.slice(0, 10)
  } catch {
    marketPrices.value = []
  }
}

async function fetchNews() {
  try {
    const res = (await getNewsList({ pageSize: 5 })) as any
    const list = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    newsList.value = list.slice(0, 5)
  } catch {
    newsList.value = []
  }
}

async function fetchSupply() {
  try {
    const res = (await getSupplyList({ pageSize: 5 })) as any
    const list = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    supplyList.value = list.slice(0, 5)
  } catch {
    supplyList.value = []
  }
}

async function fetchDemand() {
  try {
    const res = (await getDemandList({ pageSize: 5 })) as any
    const list = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    demandList.value = list.slice(0, 5)
  } catch {
    demandList.value = []
  }
}

function newsTagType(category: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    行情: 'danger',
    政策: 'warning',
    分析: '',
    产地: 'success',
    科普: 'info',
  }
  return map[category] || 'info'
}

function goPriceDetail(herbId: number) {
  router.push({ name: 'PriceDetail', params: { herbId } })
}

function goNewsDetail(id: number) {
  router.push({ name: 'NewsDetail', params: { id } })
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  await Promise.all([
    fetchIndexData(),
    fetchSummary(),
    fetchMarketPrices(),
    fetchNews(),
    fetchSupply(),
    fetchDemand(),
  ])
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  indexChart?.dispose()
  indexChart = null
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes shimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

.home {
  min-height: 100vh;
  background: $bg-warm;

  &__hero {
    position: relative;
    overflow: hidden;
    padding: 56px 0 48px;
    background: linear-gradient(135deg, $primary-dark 0%, $primary-color 40%, $primary-light 100%);

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background:
        radial-gradient(ellipse 600px 400px at 20% 80%, rgba($accent-color, 0.08) 0%, transparent 70%),
        radial-gradient(ellipse 500px 350px at 80% 20%, rgba($accent-color, 0.06) 0%, transparent 70%);
      pointer-events: none;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, transparent, $accent-color, transparent);
      opacity: 0.6;
    }
  }

  &__hero-inner {
    position: relative;
    z-index: 1;
    max-width: $container-max;
    margin: 0 auto;
    padding: 0 24px;
  }

  &__hero-text {
    text-align: center;
    margin-bottom: 36px;
    animation: fadeInUp 0.7s ease-out both;
  }

  &__hero-title {
    font-family: $font-display;
    font-size: 42px;
    font-weight: 700;
    color: #fff;
    letter-spacing: 6px;
    margin: 0 0 12px;
    text-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  }

  &__hero-subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.75);
    letter-spacing: 3px;
    margin: 0;
  }

  &__hero-gold-line {
    width: 60px;
    height: 2px;
    margin: 20px auto 0;
    background: linear-gradient(90deg, transparent, $accent-color, transparent);
    border-radius: 1px;
  }

  &__hero-stats {
    display: flex;
    justify-content: center;
    gap: 20px;
    animation: fadeInUp 0.7s ease-out 0.2s both;
  }

  &__stat-card {
    flex: 0 0 auto;
    min-width: 160px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: $radius-md;
    padding: 20px 28px;
    text-align: center;
    transition: transform 0.3s, background 0.3s;

    &:hover {
      transform: translateY(-3px);
      background: rgba(255, 255, 255, 0.16);
    }

    &--index {
      border-bottom: 2px solid $accent-color;
    }

    &--up {
      border-bottom: 2px solid $up-color;
    }

    &--down {
      border-bottom: 2px solid rgba(255, 255, 255, 0.5);
    }
  }

  &__stat-label {
    display: block;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.65);
    margin-bottom: 8px;
    letter-spacing: 1px;
  }

  &__stat-value {
    display: block;
    font-family: $font-display;
    font-size: 30px;
    font-weight: 700;
    color: #fff;

    &--up {
      color: #ff8a80;
    }

    &--down {
      color: #a5d6a7;
    }
  }

  &__stat-badge {
    display: inline-block;
    margin-top: 6px;
    font-size: 13px;
    font-weight: 600;
    padding: 2px 10px;
    border-radius: 20px;

    &--up {
      color: #ff8a80;
      background: rgba(255, 138, 128, 0.15);
    }

    &--down {
      color: #a5d6a7;
      background: rgba(165, 214, 167, 0.15);
    }
  }

  &__section {
    max-width: $container-max;
    margin: 0 auto;
    padding: 0 24px;
    margin-top: 28px;

    &--chart {
      margin-top: -24px;
      position: relative;
      z-index: 2;
    }
  }

  &__panel {
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
    padding: 24px;
    height: 100%;
    transition: box-shadow 0.3s ease;
    animation: fadeInUp 0.6s ease-out both;

    &:hover {
      box-shadow: $shadow-md;
    }

    &--chart {
      animation-delay: 0.1s;
    }

    &--summary {
      animation-delay: 0.2s;
    }
  }

  &__panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }

  &__section-title {
    font-family: $font-display;
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
    margin: 0;
    position: relative;
    padding-left: 14px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 3px;
      bottom: 3px;
      width: 3px;
      border-radius: 2px;
      background: linear-gradient(180deg, $accent-color, $primary-color);
    }
  }

  &__panel-link {
    font-size: 13px;
    color: $accent-color;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $primary-color;
    }
  }

  &__index-summary {
    display: flex;
    align-items: baseline;
    gap: 12px;
    margin-bottom: 16px;
  }

  &__index-num {
    font-family: $font-display;
    font-size: 32px;
    font-weight: 700;
    color: $text-color;
  }

  &__index-change {
    font-size: 14px;
    font-weight: 600;
    padding: 2px 10px;
    border-radius: 20px;

    &--up {
      color: $up-color;
      background: rgba($up-color, 0.08);
    }

    &--down {
      color: $down-color;
      background: rgba($down-color, 0.08);
    }
  }

  &__chart {
    width: 100%;
    height: 300px;
  }

  &__summary-bars {
    margin-bottom: 24px;
  }

  &__summary-bar-item {
    margin-bottom: 14px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  &__summary-bar-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  &__summary-bar-label {
    font-size: 13px;
    color: $text-secondary;
  }

  &__summary-bar-num {
    font-family: $font-display;
    font-size: 20px;
    font-weight: 700;

    &--up {
      color: $up-color;
    }

    &--down {
      color: $down-color;
    }

    &--flat {
      color: $text-muted;
    }
  }

  &__summary-bar-track {
    height: 6px;
    background: $border-light;
    border-radius: 3px;
    overflow: hidden;
  }

  &__summary-bar-fill {
    height: 100%;
    border-radius: 3px;
    transition: width 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);

    &--up {
      background: linear-gradient(90deg, rgba($up-color, 0.6), $up-color);
    }

    &--down {
      background: linear-gradient(90deg, rgba($down-color, 0.6), $down-color);
    }

    &--flat {
      background: linear-gradient(90deg, rgba($text-muted, 0.4), $text-muted);
    }
  }

  &__top-list {
    display: flex;
    gap: 20px;
    padding-top: 20px;
    border-top: 1px solid $border-light;
  }

  &__top-section {
    flex: 1;
  }

  &__top-heading {
    font-family: $font-display;
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 10px;
    padding-bottom: 6px;
    border-bottom: 1px dashed $border-color;

    &--up {
      color: $up-color;
    }

    &--down {
      color: $down-color;
    }
  }

  &__top-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 0;
    font-size: 13px;
    transition: background 0.2s;
    border-radius: $radius-sm;
    padding-left: 4px;
    padding-right: 4px;

    &:hover {
      background: $bg-warm;
    }
  }

  &__top-name {
    color: $text-color;
  }

  &__top-value {
    font-weight: 600;
    font-family: $font-display;

    &--up {
      color: $up-color;
    }

    &--down {
      color: $down-color;
    }
  }

  &__market-tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 20px;
    flex-wrap: wrap;
  }

  &__market-tab {
    padding: 6px 20px;
    border: 1px solid $border-color;
    border-radius: 20px;
    background: transparent;
    color: $text-secondary;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.25s ease;
    font-family: inherit;

    &:hover {
      border-color: $primary-color;
      color: $primary-color;
    }

    &--active {
      background: $primary-color;
      border-color: $primary-color;
      color: #fff;
      box-shadow: 0 2px 8px rgba($primary-color, 0.3);
    }
  }

  &__market-table {
    width: 100%;

    :deep(.el-table__header th) {
      background: $bg-warm !important;
      color: $text-secondary;
      font-weight: 600;
      font-size: 13px;
    }

    :deep(.el-table__row) {
      transition: background 0.2s;

      &:hover > td {
        background: rgba($accent-color, 0.04) !important;
      }
    }

    :deep(.el-table__body td) {
      border-bottom-color: $border-light;
    }
  }

  &__herb-link {
    color: $primary-color;
    font-weight: 500;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $accent-color;
    }
  }

  &__price-value {
    font-family: $font-display;
    font-weight: 700;
    color: $text-color;
  }

  &__more {
    text-align: center;
    padding-top: 16px;
  }

  &__more-link {
    font-size: 13px;
    color: $accent-color;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $primary-color;
    }
  }

  &__news {
    display: flex;
    flex-direction: column;
  }

  &__news-item {
    display: flex;
    align-items: center;
    padding: 12px 8px;
    border-bottom: 1px solid $border-light;
    cursor: pointer;
    transition: all 0.2s ease;
    border-radius: $radius-sm;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background: rgba($accent-color, 0.04);
      padding-left: 12px;
    }
  }

  &__news-dot {
    flex-shrink: 0;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 12px;

    &--danger {
      background: $up-color;
      box-shadow: 0 0 6px rgba($up-color, 0.3);
    }

    &--warning {
      background: $accent-color;
      box-shadow: 0 0 6px rgba($accent-color, 0.3);
    }

    &--success {
      background: $down-color;
      box-shadow: 0 0 6px rgba($down-color, 0.3);
    }

    &--info {
      background: $info-color;
      box-shadow: 0 0 6px rgba($info-color, 0.3);
    }

    &-- {
      background: $text-muted;
    }
  }

  &__news-title {
    flex: 1;
    font-size: 14px;
    color: $text-color;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    transition: color 0.2s;

    .home__news-item:hover & {
      color: $primary-color;
    }
  }

  &__news-time {
    flex-shrink: 0;
    margin-left: 16px;
    font-size: 12px;
    color: $text-muted;
  }

  &__card-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  &__card-item {
    padding: 14px 16px;
    background: $bg-warm;
    border-radius: $radius-sm;
    border: 1px solid $border-light;
    transition: all 0.25s ease;

    &:hover {
      border-color: rgba($accent-color, 0.3);
      box-shadow: $shadow-sm;
      transform: translateX(4px);
    }
  }

  &__card-item-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  &__card-item-herb {
    font-family: $font-display;
    font-size: 15px;
    font-weight: 600;
    color: $text-color;
  }

  &__card-item-price {
    font-family: $font-display;
    font-size: 15px;
    font-weight: 700;
    color: $accent-color;
  }

  &__card-item-badge {
    font-size: 12px;
    padding: 2px 10px;
    border-radius: 12px;
    background: rgba($primary-color, 0.08);
    color: $primary-color;
    font-weight: 500;
  }

  &__card-item-meta {
    font-size: 12px;
    color: $text-muted;

    span {
      margin-right: 2px;
    }
  }
}
</style>
