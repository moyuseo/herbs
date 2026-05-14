<template>
  <div class="market-index container">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="item in statCards" :key="item.key">
        <div class="stat-card card-box">
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="middle-row">
      <el-col :xs="24" :md="14">
        <div class="card-box ranking-card">
          <el-tabs v-model="rankingTab">
            <el-tab-pane label="涨幅榜" name="up">
              <RankingList :data="upRanking" type="up" :loading="rankingLoading" />
            </el-tab-pane>
            <el-tab-pane label="跌幅榜" name="down">
              <RankingList :data="downRanking" type="down" :loading="rankingLoading" />
            </el-tab-pane>
          </el-tabs>
          <div class="card-footer">
            <router-link to="/market/ranking">查看完整排行 →</router-link>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :md="10">
        <div class="card-box hot-card">
          <h3 class="card-title">热门品种快讯</h3>
          <div class="hot-list">
            <div v-for="item in hotHerbs" :key="item.id" class="hot-item">
              <router-link :to="`/market/herb/${item.id}`" class="hot-name">{{ item.name }}</router-link>
              <span class="hot-price">{{ item.price }}</span>
              <TrendTag :trend="item.trend" :change-percent="item.changePercent" />
            </div>
            <div v-if="!hotHerbs.length" class="empty-tip">暂无数据</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="card-box price-section">
      <el-tabs v-model="priceTab">
        <el-tab-pane label="市场价格" name="market">
          <PriceTable :table-data="marketPrices" area-label="市场" :loading="marketLoading" />
        </el-tab-pane>
        <el-tab-pane label="产地价格" name="origin">
          <PriceTable :table-data="originPrices" area-label="产地" :loading="originLoading" />
        </el-tab-pane>
      </el-tabs>
      <div class="card-footer">
        <router-link v-if="priceTab === 'market'" to="/market">查看更多市场价格 →</router-link>
        <router-link v-else to="/market/origin">查看更多产地价格 →</router-link>
      </div>
    </div>

    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRanking, getMarketPrices, getOriginPrices } from '@/api/market'
import RankingList from '@/components/market/RankingList.vue'
import TrendTag from '@/components/market/TrendTag.vue'
import PriceTable from '@/components/market/PriceTable.vue'

const rankingTab = ref('up')
const priceTab = ref('market')
const rankingLoading = ref(false)
const marketLoading = ref(false)
const originLoading = ref(false)

const upRanking = ref([])
const downRanking = ref([])
const marketPrices = ref([])
const originPrices = ref([])
const hotHerbs = ref([])

const statCards = ref([
  { key: 'total', label: '今日品种数', value: '--', color: '#303133' },
  { key: 'up', label: '涨品种数', value: '--', color: '#F56C6C' },
  { key: 'down', label: '跌品种数', value: '--', color: '#2D8C4E' },
  { key: 'index', label: '价格指数', value: '--', color: '#E6A23C' }
])

async function fetchRanking() {
  rankingLoading.value = true
  try {
    const data = await getRanking('day', 10)
    upRanking.value = data?.up || []
    downRanking.value = data?.down || []
    updateStats(data)
    buildHotHerbs(data)
  } catch {
    upRanking.value = []
    downRanking.value = []
  } finally {
    rankingLoading.value = false
  }
}

function updateStats(data) {
  if (data?.summary) {
    const s = data.summary
    statCards.value = [
      { key: 'total', label: '今日品种数', value: s.total ?? '--', color: '#303133' },
      { key: 'up', label: '涨品种数', value: s.upCount ?? '--', color: '#F56C6C' },
      { key: 'down', label: '跌品种数', value: s.downCount ?? '--', color: '#2D8C4E' },
      { key: 'index', label: '价格指数', value: s.priceIndex ?? '--', color: '#E6A23C' }
    ]
  }
}

function buildHotHerbs(data) {
  const upList = (data?.up || []).slice(0, 5)
  const downList = (data?.down || []).slice(0, 3)
  hotHerbs.value = [...upList, ...downList].map(item => ({
    id: item.id,
    name: item.name,
    price: item.price,
    trend: item.changePercent > 0 ? 'up' : 'down',
    changePercent: item.changePercent
  }))
}

async function fetchMarketPrices() {
  marketLoading.value = true
  try {
    const data = await getMarketPrices({ page: 1, size: 10 })
    marketPrices.value = data?.list || data || []
  } catch {
    marketPrices.value = []
  } finally {
    marketLoading.value = false
  }
}

async function fetchOriginPrices() {
  originLoading.value = true
  try {
    const data = await getOriginPrices({ page: 1, size: 10 })
    originPrices.value = data?.list || data || []
  } catch {
    originPrices.value = []
  } finally {
    originLoading.value = false
  }
}

onMounted(() => {
  fetchRanking()
  fetchMarketPrices()
  fetchOriginPrices()
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.market-index {
  padding: 20px 0;
}

.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
  padding: 24px 16px;
}

.stat-label {
  font-size: 13px;
  color: $text-secondary;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.middle-row {
  margin-bottom: 16px;
}

.ranking-card,
.hot-card {
  min-height: 400px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid $border-color;
}

.hot-list {
  min-height: 300px;
}

.hot-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  &:last-child {
    border-bottom: none;
  }
}

.hot-name {
  flex: 1;
  color: $text-color;
  font-size: 14px;
  text-decoration: none;
  &:hover {
    color: $primary-color;
  }
}

.hot-price {
  margin-right: 16px;
  font-weight: 600;
  font-size: 14px;
}

.empty-tip {
  text-align: center;
  color: $text-secondary;
  padding: 40px 0;
}

.price-section {
  margin-bottom: 16px;
}

.card-footer {
  text-align: center;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  a {
    font-size: 13px;
    color: $primary-color;
    text-decoration: none;
    &:hover {
      text-decoration: underline;
    }
  }
}

@media (max-width: 768px) {
  .stat-card {
    padding: 16px 12px;
  }
  .stat-value {
    font-size: 22px;
  }
}
</style>
