<template>
  <div class="price-detail" v-loading="loading">
    <div class="price-detail__header">
      <div class="price-detail__info">
        <h2>{{ detail.herbName || '品种详情' }}</h2>
        <span class="price-detail__spec">{{ detail.spec }}</span>
        <span class="price-detail__origin">产地: {{ detail.origin }}</span>
      </div>
      <el-button :type="watched ? 'default' : 'primary'" @click="toggleWatch">
        <el-icon><Star /></el-icon>
        {{ watched ? '已关注' : '加入关注' }}
      </el-button>
    </div>

    <div class="price-detail__cards">
      <el-card v-for="item in marketPrices" :key="item.market" shadow="hover" class="price-detail__card">
        <template #header>
          <span>{{ item.market }}</span>
        </template>
        <div class="price-detail__card-body">
          <div class="price-detail__price">¥{{ Number(item.price).toFixed(2) }}</div>
          <PriceTag :value="item.dayChange" />
          <div class="price-detail__card-spec">{{ item.spec }}</div>
        </div>
      </el-card>
    </div>

    <div class="price-detail__chart">
      <PriceChart :herb-id="herbId" :herb-name="detail.herbName || ''" />
    </div>

    <div class="price-detail__stats">
      <el-card shadow="hover" class="price-detail__stat-card">
        <el-statistic title="日涨跌幅">
          <template #default>
            <PriceTag :value="stats.dayChange" />
          </template>
        </el-statistic>
      </el-card>
      <el-card shadow="hover" class="price-detail__stat-card">
        <el-statistic title="周涨跌幅">
          <template #default>
            <PriceTag :value="stats.weekChange" />
          </template>
        </el-statistic>
      </el-card>
      <el-card shadow="hover" class="price-detail__stat-card">
        <el-statistic title="月涨跌幅">
          <template #default>
            <PriceTag :value="stats.monthChange" />
          </template>
        </el-statistic>
      </el-card>
      <el-card shadow="hover" class="price-detail__stat-card">
        <el-statistic title="年涨跌幅">
          <template #default>
            <PriceTag :value="stats.yearChange" />
          </template>
        </el-statistic>
      </el-card>
      <el-card shadow="hover" class="price-detail__stat-card">
        <el-statistic title="最高价" :value="'¥' + stats.highPrice.toFixed(2)" />
      </el-card>
      <el-card shadow="hover" class="price-detail__stat-card">
        <el-statistic title="最低价" :value="'¥' + stats.lowPrice.toFixed(2)" />
      </el-card>
    </div>

    <div class="price-detail__news">
      <h3>关联资讯</h3>
      <el-empty description="暂无关联资讯" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPriceDetail } from '@/api/price'
import PriceChart from '@/components/price/PriceChart.vue'
import PriceTag from '@/components/price/PriceTag.vue'

const route = useRoute()
const herbId = computed(() => Number(route.params.herbId))

const loading = ref(false)
const watched = ref(false)

interface MarketPriceItem {
  market: string
  spec: string
  price: number
  dayChange: number
}

interface DetailData {
  herbName: string
  spec: string
  origin: string
  marketPrices: MarketPriceItem[]
  dayChange: number
  weekChange: number
  monthChange: number
  yearChange: number
  highPrice: number
  lowPrice: number
}

const detail = ref<Partial<DetailData>>({})
const marketPrices = computed(() => detail.value.marketPrices || [])
const stats = computed(() => ({
  dayChange: detail.value.dayChange || 0,
  weekChange: detail.value.weekChange || 0,
  monthChange: detail.value.monthChange || 0,
  yearChange: detail.value.yearChange || 0,
  highPrice: detail.value.highPrice || 0,
  lowPrice: detail.value.lowPrice || 0,
}))

function toggleWatch() {
  watched.value = !watched.value
}

async function fetchDetail() {
  loading.value = true
  try {
    const res = (await getPriceDetail(herbId.value)) as any
    detail.value = res || {}
  } catch {
    detail.value = {}
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="scss">
.price-detail {
  padding: 16px;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    padding: 20px 24px;
    border-radius: 4px;
    margin-bottom: 16px;
  }

  &__info {
    display: flex;
    align-items: baseline;
    gap: 12px;

    h2 {
      margin: 0;
      font-size: 22px;
    }
  }

  &__spec {
    color: #909399;
    font-size: 14px;
  }

  &__origin {
    color: #909399;
    font-size: 14px;
  }

  &__cards {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-bottom: 16px;
  }

  &__card {
    min-width: 180px;
    flex: 1;
  }

  &__card-body {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__price {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
  }

  &__card-spec {
    font-size: 12px;
    color: #909399;
  }

  &__chart {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 16px;
  }

  &__stats {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-bottom: 16px;
  }

  &__stat-card {
    min-width: 150px;
    flex: 1;
  }

  &__news {
    background: #fff;
    padding: 20px;
    border-radius: 4px;

    h3 {
      margin: 0 0 16px;
      font-size: 16px;
    }
  }
}
</style>
