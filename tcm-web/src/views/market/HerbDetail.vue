<template>
  <div class="herb-detail" v-loading="loading">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/market' }">行情中心</el-breadcrumb-item>
      <el-breadcrumb-item>{{ herbDetail?.name || '品种详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="20" class="herb-detail__body">
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card class="herb-detail__info-card" shadow="hover">
          <template #header>
            <div class="herb-detail__card-header">
              <span class="herb-detail__herb-name">{{ herbDetail?.name }}</span>
              <el-button
                :type="isFavorited ? 'warning' : 'default'"
                :icon="isFavorited ? 'StarFilled' : 'Star'"
                :disabled="!userStore.isLoggedIn"
                @click="toggleFavorite"
              >
                {{ isFavorited ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="别名">{{ herbDetail?.alias || '-' }}</el-descriptions-item>
            <el-descriptions-item label="产地">{{ herbDetail?.origin || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性味归经">{{ herbDetail?.meridian || '-' }}</el-descriptions-item>
            <el-descriptions-item label="功效">{{ herbDetail?.efficacy || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <div class="herb-detail__chart-section">
          <TrendChart :history-data="priceHistory" @period-change="onPeriodChange" />
        </div>

        <el-card class="herb-detail__spec-card" shadow="hover">
          <template #header>
            <span class="herb-detail__card-title">规格价格</span>
          </template>
          <el-table :data="herbDetail?.specs || []" stripe style="width: 100%">
            <el-table-column prop="name" label="规格名" min-width="120" />
            <el-table-column prop="price" label="价格" min-width="100">
              <template #default="{ row }">
                <span class="price-text">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="changeRate" label="涨跌幅" min-width="100">
              <template #default="{ row }">
                <span :class="row.changeRate > 0 ? 'price-up' : row.changeRate < 0 ? 'price-down' : ''">
                  {{ row.changeRate > 0 ? '+' : '' }}{{ row.changeRate }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column label="走势" min-width="80">
              <template #default="{ row }">
                <el-tag :type="row.changeRate > 0 ? 'danger' : row.changeRate < 0 ? 'success' : 'info'" size="small">
                  {{ row.changeRate > 0 ? '↑' : row.changeRate < 0 ? '↓' : '→' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="herb-detail__market-card" shadow="hover">
          <template #header>
            <span class="herb-detail__card-title">市场价格</span>
          </template>
          <div class="herb-detail__price-list">
            <div
              v-for="item in herbDetail?.marketPrices || []"
              :key="item.market"
              class="herb-detail__price-item"
            >
              <span class="herb-detail__price-label">{{ item.market }}</span>
              <span class="price-text">¥{{ item.price }}</span>
            </div>
            <el-empty v-if="!herbDetail?.marketPrices?.length" description="暂无数据" :image-size="60" />
          </div>
        </el-card>

        <el-card class="herb-detail__origin-card" shadow="hover">
          <template #header>
            <span class="herb-detail__card-title">产地价格</span>
          </template>
          <div class="herb-detail__price-list">
            <div
              v-for="item in herbDetail?.originPrices || []"
              :key="item.origin"
              class="herb-detail__price-item"
            >
              <span class="herb-detail__price-label">{{ item.origin }}</span>
              <span class="price-text">¥{{ item.price }}</span>
            </div>
            <el-empty v-if="!herbDetail?.originPrices?.length" description="暂无数据" :image-size="60" />
          </div>
        </el-card>

        <el-card class="herb-detail__news-card" shadow="hover">
          <template #header>
            <span class="herb-detail__card-title">相关资讯</span>
          </template>
          <el-empty description="暂无资讯" :image-size="60" />
        </el-card>

        <el-card class="herb-detail__supply-card" shadow="hover">
          <template #header>
            <span class="herb-detail__card-title">供需信息</span>
          </template>
          <el-empty description="暂无供需信息" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePrice } from '@/composables/usePrice'
import TrendChart from '@/components/market/TrendChart.vue'

const route = useRoute()
const userStore = useUserStore()
const { herbDetail, priceHistory, loading, fetchHerbDetail, fetchPriceHistory } = usePrice()

const isFavorited = ref(false)

function toggleFavorite() {
  isFavorited.value = !isFavorited.value
}

async function onPeriodChange(period) {
  const specId = herbDetail.value?.specs?.[0]?.id || route.params.id
  await fetchPriceHistory(specId, period)
}

onMounted(async () => {
  const id = route.params.id
  await fetchHerbDetail(id)
  const specId = herbDetail.value?.specs?.[0]?.id || id
  await fetchPriceHistory(specId)
})
</script>

<style lang="scss" scoped>
.herb-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .el-breadcrumb {
    margin-bottom: 20px;
  }

  &__body {
    margin-top: 8px;
  }

  &__card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__herb-name {
    font-size: 20px;
    font-weight: 700;
    color: #303133;
  }

  &__card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  &__info-card {
    margin-bottom: 20px;
  }

  &__chart-section {
    margin-bottom: 20px;
  }

  &__spec-card {
    margin-bottom: 20px;
  }

  &__market-card,
  &__origin-card,
  &__news-card,
  &__supply-card {
    margin-bottom: 20px;
  }

  &__price-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  &__price-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }
  }

  &__price-label {
    color: #606266;
    font-size: 14px;
  }
}

.price-text {
  font-weight: 600;
  color: #303133;
}

.price-up {
  color: #F56C6C;
  font-weight: 600;
}

.price-down {
  color: #2D8C4E;
  font-weight: 600;
}

@media (max-width: 768px) {
  .herb-detail {
    padding: 12px;

    &__herb-name {
      font-size: 18px;
    }
  }
}
</style>
