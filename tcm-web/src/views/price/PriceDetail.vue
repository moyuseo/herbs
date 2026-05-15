<template>
  <div class="price-detail" v-loading="loading">
    <nav class="price-detail__breadcrumb">
      <router-link to="/">首页</router-link>
      <span class="price-detail__breadcrumb-sep">/</span>
      <router-link to="/price">行情中心</router-link>
      <span class="price-detail__breadcrumb-sep">/</span>
      <span class="price-detail__breadcrumb-current">{{ detail.herbName || '品种详情' }}</span>
    </nav>

    <div class="price-detail__page-title">
      <h1>{{ detail.herbName || '品种详情' }}</h1>
      <span class="price-detail__spec">{{ detail.spec }}</span>
      <span class="price-detail__origin">产地: {{ detail.origin }}</span>
    </div>

    <div class="price-detail__body">
      <div class="price-detail__main">
        <div class="price-detail__chart">
          <PriceChart :herb-id="herbId" :herb-name="detail.herbName || ''" />
        </div>

        <div class="price-detail__market-table">
          <h3 class="price-detail__section-title">各市场价格</h3>
          <div class="price-detail__table-wrap">
            <table class="price-detail__table">
              <thead>
                <tr>
                  <th>市场</th>
                  <th>规格</th>
                  <th>价格</th>
                  <th>涨跌</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in marketPrices" :key="item.market">
                  <td>{{ item.market }}</td>
                  <td>{{ item.spec }}</td>
                  <td class="price-detail__table-price">¥{{ Number(item.price).toFixed(2) }}</td>
                  <td><PriceTag :value="item.dayChange" /></td>
                </tr>
                <tr v-if="marketPrices.length === 0">
                  <td colspan="4" class="price-detail__table-empty">暂无市场报价</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="price-detail__news">
          <h3 class="price-detail__section-title">关联资讯</h3>
          <el-empty description="暂无关联资讯" />
        </div>
      </div>

      <aside class="price-detail__sidebar">
        <div class="price-detail__sidebar-action">
          <el-button :type="watched ? 'default' : 'primary'" @click="toggleWatch" class="price-detail__watch-btn">
            <el-icon><Star /></el-icon>
            {{ watched ? '已关注' : '加入关注' }}
          </el-button>
        </div>

        <div class="price-detail__stats">
          <div
            v-for="stat in statCards"
            :key="stat.label"
            class="price-detail__stat-card"
            :class="stat.cardClass"
          >
            <div class="price-detail__stat-label">{{ stat.label }}</div>
            <div class="price-detail__stat-value">
              <PriceTag v-if="stat.isChange" :value="stat.value" />
              <span v-else>{{ stat.display }}</span>
            </div>
          </div>
        </div>
      </aside>
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
  trend: string
}

interface DetailData {
  herbName: string
  spec: string
  origin: string
  marketPrices: MarketPriceItem[]
  dayChange: number
  dayChangeRate: number
  monthChange: number
  monthChangeRate: number
  weekChange: number
  yearChange: number
  highPrice: number
  lowPrice: number
  yearHigh: number
  yearLow: number
}

const detail = ref<Partial<DetailData>>({})
const marketPrices = computed(() => detail.value.marketPrices || [])
const stats = computed(() => ({
  dayChange: detail.value.dayChangeRate ?? detail.value.dayChange ?? 0,
  weekChange: detail.value.weekChange ?? 0,
  monthChange: detail.value.monthChangeRate ?? detail.value.monthChange ?? 0,
  yearChange: detail.value.yearChange ?? 0,
  highPrice: detail.value.yearHigh ?? detail.value.highPrice ?? 0,
  lowPrice: detail.value.yearLow ?? detail.value.lowPrice ?? 0,
}))

const statCards = computed(() => [
  { label: '日涨跌幅', value: stats.value.dayChange, isChange: true, cardClass: stats.value.dayChange > 0 ? 'price-detail__stat-card--up' : stats.value.dayChange < 0 ? 'price-detail__stat-card--down' : '' },
  { label: '周涨跌幅', value: stats.value.weekChange, isChange: true, cardClass: stats.value.weekChange > 0 ? 'price-detail__stat-card--up' : stats.value.weekChange < 0 ? 'price-detail__stat-card--down' : '' },
  { label: '月涨跌幅', value: stats.value.monthChange, isChange: true, cardClass: stats.value.monthChange > 0 ? 'price-detail__stat-card--up' : stats.value.monthChange < 0 ? 'price-detail__stat-card--down' : '' },
  { label: '年涨跌幅', value: stats.value.yearChange, isChange: true, cardClass: stats.value.yearChange > 0 ? 'price-detail__stat-card--up' : stats.value.yearChange < 0 ? 'price-detail__stat-card--down' : '' },
  { label: '最高价', value: stats.value.highPrice, isChange: false, display: '¥' + stats.value.highPrice.toFixed(2), cardClass: '' },
  { label: '最低价', value: stats.value.lowPrice, isChange: false, display: '¥' + stats.value.lowPrice.toFixed(2), cardClass: '' },
])

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
@import '@/styles/variables.scss';

.price-detail {
  padding: 20px;
  max-width: $container-max;
  margin: 0 auto;
  background: $bg-warm;
  min-height: calc(100vh - #{$header-height});

  &__breadcrumb {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: $text-muted;
    margin-bottom: 16px;

    a {
      color: $text-secondary;
      text-decoration: none;
      transition: color 0.2s;

      &:hover {
        color: $primary-color;
      }
    }

    &-sep {
      color: $border-color;
    }

    &-current {
      color: $text-color;
      font-weight: 500;
    }
  }

  &__page-title {
    display: flex;
    align-items: baseline;
    gap: 12px;
    margin-bottom: 24px;

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

  &__spec {
    color: $text-muted;
    font-size: 14px;
  }

  &__origin {
    color: $text-muted;
    font-size: 14px;
  }

  &__body {
    display: flex;
    gap: 20px;
    align-items: flex-start;
  }

  &__main {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  &__chart {
    background: $card-bg;
    padding: 24px;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
    transition: box-shadow 0.3s;

    &:hover {
      box-shadow: $shadow-md;
    }
  }

  &__section-title {
    margin: 0 0 16px;
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
      top: 3px;
      bottom: 3px;
      width: 4px;
      background: $primary-color;
      border-radius: 2px;
    }
  }

  &__market-table {
    background: $card-bg;
    border-radius: $radius-md;
    padding: 24px;
    box-shadow: $shadow-sm;
  }

  &__table-wrap {
    overflow-x: auto;
  }

  &__table {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;

    th {
      background: $bg-warm;
      color: $text-secondary;
      font-weight: 500;
      padding: 10px 16px;
      text-align: left;
      border-bottom: 2px solid $border-color;
      white-space: nowrap;
    }

    td {
      padding: 12px 16px;
      border-bottom: 1px solid $border-light;
      color: $text-color;
    }

    tbody tr {
      transition: background 0.2s;

      &:hover {
        background: $primary-lighter;
      }
    }

    &-price {
      font-weight: 600;
      color: $accent-color;
    }

    &-empty {
      text-align: center;
      color: $text-muted;
      padding: 32px 16px !important;
    }
  }

  &__news {
    background: $card-bg;
    border-radius: $radius-md;
    padding: 24px;
    box-shadow: $shadow-sm;
  }

  &__sidebar {
    width: 280px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    gap: 16px;
    position: sticky;
    top: calc(#{$header-height} + 20px);
  }

  &__sidebar-action {
    background: $card-bg;
    border-radius: $radius-md;
    padding: 16px;
    box-shadow: $shadow-sm;
  }

  &__watch-btn {
    width: 100%;
  }

  &__stats {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  &__stat-card {
    background: $card-bg;
    border-radius: $radius-md;
    padding: 14px 16px;
    box-shadow: $shadow-sm;
    border-left: 4px solid $border-color;
    transition: box-shadow 0.3s, transform 0.2s;

    &:hover {
      box-shadow: $shadow-md;
      transform: translateY(-1px);
    }

    &--up {
      border-left-color: $up-color;
    }

    &--down {
      border-left-color: $down-color;
    }
  }

  &__stat-label {
    font-size: 12px;
    color: $text-muted;
    margin-bottom: 4px;
  }

  &__stat-value {
    font-size: 18px;
    font-weight: 600;
  }
}

@media (max-width: 900px) {
  .price-detail__body {
    flex-direction: column;
  }

  .price-detail__sidebar {
    width: 100%;
    position: static;
  }

  .price-detail__stats {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .price-detail__stat-card {
    flex: 1;
    min-width: 140px;
  }
}
</style>
