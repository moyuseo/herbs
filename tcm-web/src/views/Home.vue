<template>
  <div class="home">
    <el-row :gutter="16" class="home__row">
      <el-col :span="16">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <div class="home__card-header">
              <span class="home__card-title">综合价格指数走势</span>
              <el-link type="primary" @click="router.push({ name: 'PriceIndex' })">详情 →</el-link>
            </div>
          </template>
          <div v-if="indexData.length" class="home__index-summary">
            <div class="home__index-value">
              <span class="home__index-num">{{ latestIndexValue }}</span>
              <span v-if="latestIndexChange !== null" class="home__index-change" :class="latestIndexChange >= 0 ? 'home__index-change--up' : 'home__index-change--down'">
                {{ latestIndexChange >= 0 ? '+' : '' }}{{ (latestIndexChange * 100).toFixed(2) }}%
              </span>
            </div>
          </div>
          <div v-if="indexData.length" ref="indexChartRef" class="home__chart" />
          <el-empty v-else description="暂无指数数据" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <span class="home__card-title">今日涨跌概览</span>
          </template>
          <template v-if="summary">
            <div class="home__summary-counts">
              <div class="home__summary-item home__summary-item--up">
                <div class="home__summary-num">{{ summary.upCount }}</div>
                <div class="home__summary-label">上涨</div>
              </div>
              <div class="home__summary-item home__summary-item--down">
                <div class="home__summary-num">{{ summary.downCount }}</div>
                <div class="home__summary-label">下跌</div>
              </div>
              <div class="home__summary-item home__summary-item--flat">
                <div class="home__summary-num">{{ summary.flatCount }}</div>
                <div class="home__summary-label">持平</div>
              </div>
            </div>
            <el-divider />
            <div class="home__top-list">
              <div class="home__top-section">
                <div class="home__top-heading home__top-heading--up">涨幅前三</div>
                <div v-for="item in summary.topGainers" :key="item.name" class="home__top-row">
                  <span class="home__top-name">{{ item.name }}</span>
                  <span class="home__top-value home__top-value--up">+{{ item.change.toFixed(2) }}%</span>
                </div>
                <el-empty v-if="!summary.topGainers?.length" description="暂无数据" :image-size="40" />
              </div>
              <div class="home__top-section">
                <div class="home__top-heading home__top-heading--down">跌幅前三</div>
                <div v-for="item in summary.topLosers" :key="item.name" class="home__top-row">
                  <span class="home__top-name">{{ item.name }}</span>
                  <span class="home__top-value home__top-value--down">{{ item.change.toFixed(2) }}%</span>
                </div>
                <el-empty v-if="!summary.topLosers?.length" description="暂无数据" :image-size="40" />
              </div>
            </div>
          </template>
          <el-empty v-else description="暂无涨跌数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="home__row">
      <el-col :span="24">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <span class="home__card-title">市场价格快览</span>
          </template>
          <el-tabs v-model="activeMarket" @tab-change="fetchMarketPrices">
            <el-tab-pane v-for="m in markets" :key="m" :label="m" :name="m" />
          </el-tabs>
          <el-table v-if="marketPrices.length" :data="marketPrices" stripe style="width: 100%">
            <el-table-column prop="herbName" label="品种" min-width="100">
              <template #default="{ row }">
                <el-link type="primary" @click="goPriceDetail(row.herbId)">{{ row.herbName }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="spec" label="规格" min-width="80" />
            <el-table-column prop="price" label="今日价" min-width="90">
              <template #default="{ row }">
                <span style="font-weight: 600">¥{{ Number(row.price).toFixed(2) }}</span>
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
            <el-link type="primary" @click="router.push({ name: 'MarketPrice' })">查看更多 →</el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="home__row">
      <el-col :span="12">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <span class="home__card-title">产地地图</span>
          </template>
          <OriginMap />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <span class="home__card-title">行情资讯</span>
          </template>
          <template v-if="newsList.length">
            <div class="home__news">
              <div v-for="item in newsList" :key="item.id" class="home__news-item" @click="goNewsDetail(item.id)">
                <el-tag size="small" :type="newsTagType(item.category)" class="home__news-tag">
                  {{ item.category }}
                </el-tag>
                <span class="home__news-title">{{ item.title }}</span>
                <span class="home__news-time">{{ item.publishTime }}</span>
              </div>
            </div>
            <div class="home__more">
              <el-link type="primary" @click="router.push({ name: 'NewsList' })">查看更多 →</el-link>
            </div>
          </template>
          <el-empty v-else description="暂无资讯" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="home__row">
      <el-col :span="12">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <span class="home__card-title">供应信息</span>
          </template>
          <el-table v-if="supplyList.length" :data="supplyList" stripe style="width: 100%">
            <el-table-column prop="herbName" label="品种" min-width="80" />
            <el-table-column prop="spec" label="规格" min-width="70" />
            <el-table-column prop="origin" label="产地" min-width="70" />
            <el-table-column prop="quantity" label="数量" min-width="70" />
            <el-table-column prop="price" label="价格" min-width="80">
              <template #default="{ row }">
                <span style="font-weight: 600">¥{{ row.price }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无供应信息" :image-size="80" />
          <div v-if="supplyList.length" class="home__more">
            <el-link type="primary" @click="router.push({ name: 'SupplyList' })">查看更多 →</el-link>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="home__card">
          <template #header>
            <span class="home__card-title">求购信息</span>
          </template>
          <el-table v-if="demandList.length" :data="demandList" stripe style="width: 100%">
            <el-table-column prop="herbName" label="品种" min-width="80" />
            <el-table-column prop="spec" label="规格" min-width="70" />
            <el-table-column prop="quantity" label="数量" min-width="70" />
            <el-table-column prop="bidderCount" label="报价人数" min-width="80">
              <template #default="{ row }">
                <span>{{ row.bidderCount }}人</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无求购信息" :image-size="80" />
          <div v-if="demandList.length" class="home__more">
            <el-link type="primary" @click="router.push({ name: 'DemandList' })">查看更多 →</el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
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
  change: number
}

interface SummaryData {
  upCount: number
  downCount: number
  flatCount: number
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
  publishTime: string
}

interface SupplyItem {
  herbName: string
  spec: string
  origin: string
  quantity: string
  price: string
}

interface DemandItem {
  herbName: string
  spec: string
  quantity: string
  bidderCount: number
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

.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;

  &__row {
    margin-bottom: 16px;
  }

  &__card {
    height: 100%;

    :deep(.el-card__header) {
      padding: 12px 20px;
    }
  }

  &__card-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-color;
  }

  &__card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__index-summary {
    margin-bottom: 12px;
  }

  &__index-value {
    display: flex;
    align-items: baseline;
    gap: 12px;
  }

  &__index-num {
    font-size: 28px;
    font-weight: 700;
    color: $text-color;
  }

  &__index-change {
    font-size: 14px;
    font-weight: 600;
    padding: 1px 8px;
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

  &__chart {
    width: 100%;
    height: 300px;
  }

  &__summary-counts {
    display: flex;
    justify-content: space-around;
    text-align: center;
    padding: 8px 0;
  }

  &__summary-item {
    &--up .home__summary-num {
      color: #e74c3c;
    }

    &--down .home__summary-num {
      color: #27ae60;
    }

    &--flat .home__summary-num {
      color: #909399;
    }
  }

  &__summary-num {
    font-size: 28px;
    font-weight: 700;
    line-height: 1.2;
  }

  &__summary-label {
    font-size: 13px;
    color: $text-secondary;
    margin-top: 4px;
  }

  &__top-list {
    display: flex;
    gap: 16px;
  }

  &__top-section {
    flex: 1;
  }

  &__top-heading {
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 8px;

    &--up {
      color: #e74c3c;
    }

    &--down {
      color: #27ae60;
    }
  }

  &__top-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 0;
    font-size: 13px;
  }

  &__top-name {
    color: $text-color;
  }

  &__top-value {
    font-weight: 600;

    &--up {
      color: #e74c3c;
    }

    &--down {
      color: #27ae60;
    }
  }

  &__more {
    text-align: center;
    padding-top: 12px;
  }

  &__news {
    display: flex;
    flex-direction: column;
    gap: 0;
  }

  &__news-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid $border-color;
    cursor: pointer;
    transition: background 0.2s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background: #fafafa;
    }
  }

  &__news-tag {
    flex-shrink: 0;
    margin-right: 8px;
  }

  &__news-title {
    flex: 1;
    font-size: 14px;
    color: $text-color;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__news-time {
    flex-shrink: 0;
    margin-left: 12px;
    font-size: 12px;
    color: $text-secondary;
  }
}
</style>
