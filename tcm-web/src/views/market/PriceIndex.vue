<template>
  <div class="price-index">
    <el-row :gutter="20" class="price-index__overview">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in overviewCards" :key="item.label">
        <el-card shadow="hover" class="price-index__overview-card">
          <div class="price-index__overview-label">{{ item.label }}</div>
          <div class="price-index__overview-value">{{ item.value }}</div>
          <div
            class="price-index__overview-change"
            :class="item.change > 0 ? 'price-up' : item.change < 0 ? 'price-down' : ''"
          >
            {{ item.change > 0 ? '+' : '' }}{{ item.change }}%
          </div>
          <div class="price-index__overview-date">{{ item.date }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="price-index__chart-card" shadow="hover">
      <template #header>
        <span class="price-index__card-title">价格指数走势</span>
      </template>
      <IndexChart :index-data="indexHistory" :index-types="indexTypes" />
    </el-card>

    <el-card class="price-index__table-card" shadow="hover">
      <template #header>
        <span class="price-index__card-title">分类指数</span>
      </template>
      <el-table :data="indexList" stripe style="width: 100%">
        <el-table-column prop="name" label="指数名称" min-width="140" />
        <el-table-column prop="value" label="最新值" min-width="100">
          <template #default="{ row }">
            <span class="price-text">{{ row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="change" label="涨跌幅" min-width="100">
          <template #default="{ row }">
            <span :class="row.change > 0 ? 'price-up' : row.change < 0 ? 'price-down' : ''">
              {{ row.change > 0 ? '+' : '' }}{{ row.change }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" min-width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPriceIndex, getPriceIndexHistory } from '@/api/market'
import IndexChart from '@/components/market/IndexChart.vue'

const indexList = ref([])
const indexHistory = ref([])
const indexTypes = ref([])
const overviewCards = ref([])

async function loadIndexData() {
  try {
    const data = await getPriceIndex()
    indexList.value = data?.list || []
    indexTypes.value = data?.types || []

    overviewCards.value = (data?.overview || []).map(item => ({
      label: item.name,
      value: item.value,
      change: item.change,
      date: item.date
    }))
  } catch (e) {
    overviewCards.value = []
    indexList.value = []
    indexTypes.value = []
  }
}

async function loadIndexHistory() {
  try {
    const data = await getPriceIndexHistory()
    indexHistory.value = data || []
  } catch (e) {
    indexHistory.value = []
  }
}

onMounted(() => {
  loadIndexData()
  loadIndexHistory()
})
</script>

<style lang="scss" scoped>
.price-index {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  &__overview {
    margin-bottom: 20px;
  }

  &__overview-card {
    text-align: center;
    margin-bottom: 12px;
  }

  &__overview-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }

  &__overview-value {
    font-size: 28px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 4px;
  }

  &__overview-change {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 4px;
  }

  &__overview-date {
    font-size: 12px;
    color: #C0C4CC;
  }

  &__chart-card {
    margin-bottom: 20px;
  }

  &__table-card {
    margin-bottom: 20px;
  }

  &__card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
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
  .price-index {
    padding: 12px;

    &__overview-value {
      font-size: 22px;
    }
  }
}
</style>
