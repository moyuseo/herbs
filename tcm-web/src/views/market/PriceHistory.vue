<template>
  <div class="price-history page-wrapper">
    <div class="container">
      <div class="card-box">
        <h2 class="page-title">历史价格查询</h2>

        <div class="filter-section">
          <el-row :gutter="16" align="middle">
            <el-col :xs="24" :sm="8" :md="6">
              <el-autocomplete
                v-model="herbKeyword"
                :fetch-suggestions="querySearchHerbs"
                placeholder="搜索品种"
                clearable
                style="width: 100%"
                @select="handleHerbSelect"
              />
            </el-col>
            <el-col :xs="24" :sm="8" :md="6">
              <el-select
                v-model="selectedSpec"
                placeholder="选择规格"
                clearable
                :disabled="!selectedHerb"
                style="width: 100%"
              >
                <el-option
                  v-for="spec in specList"
                  :key="spec.value"
                  :label="spec.label"
                  :value="spec.value"
                />
              </el-select>
            </el-col>
            <el-col :xs="24" :sm="8" :md="8">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-col>
            <el-col :xs="24" :sm="24" :md="4">
              <el-button type="primary" :loading="loading" @click="handleQuery">查询</el-button>
            </el-col>
          </el-row>
        </div>

        <div v-if="hasQueried" class="chart-section">
          <h3 class="section-title">价格走势</h3>
          <TrendChart :history-data="trendData" @period-change="handlePeriodChange" />
        </div>

        <div v-if="hasQueried" class="table-section">
          <div class="table-header">
            <h3 class="section-title">价格数据</h3>
            <el-button type="primary" plain>
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>

          <el-table :data="pagedData" stripe border style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="price" label="价格(元)" width="120" />
            <el-table-column label="涨跌额" width="120">
              <template #default="{ row }">
                <span :class="row.change > 0 ? 'price-up' : row.change < 0 ? 'price-down' : ''">
                  {{ row.change > 0 ? '+' : '' }}{{ row.change }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="涨跌幅" width="120">
              <template #default="{ row }">
                <span :class="row.changeRate > 0 ? 'price-up' : row.changeRate < 0 ? 'price-down' : ''">
                  {{ row.changeRate > 0 ? '+' : '' }}{{ row.changeRate }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column label="走势" min-width="200">
              <template #default="{ row }">
                <span :class="row.change > 0 ? 'price-up' : row.change < 0 ? 'price-down' : ''">
                  <el-icon v-if="row.change > 0"><Top /></el-icon>
                  <el-icon v-else-if="row.change < 0"><Bottom /></el-icon>
                  <el-icon v-else><Minus /></el-icon>
                  {{ row.change > 0 ? '上涨' : row.change < 0 ? '下跌' : '持平' }}
                </span>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="tableData.length"
              layout="total, sizes, prev, pager, next, jumper"
              background
            />
          </div>
        </div>

        <el-empty v-if="!hasQueried" description="请选择品种并点击查询" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Download, Top, Bottom, Minus } from '@element-plus/icons-vue'
import { searchHerbs, getHerbSpecs, getPriceHistoryQuery } from '@/api/market'
import TrendChart from '@/components/market/TrendChart.vue'

const route = useRoute()

const herbKeyword = ref('')
const selectedHerb = ref(null)
const selectedSpec = ref('')
const specList = ref([])
const dateRange = ref(null)
const loading = ref(false)
const hasQueried = ref(false)

const tableData = ref([])
const trendData = ref([])
const currentPeriod = ref('1y')

const currentPage = ref(1)
const pageSize = ref(10)

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return tableData.value.slice(start, start + pageSize.value)
})

async function querySearchHerbs(keyword, cb) {
  if (!keyword) {
    cb([])
    return
  }
  try {
    const data = await searchHerbs(keyword)
    const suggestions = (data || []).map((item) => ({
      value: item.name,
      id: item.id
    }))
    cb(suggestions)
  } catch {
    cb([])
  }
}

function handleHerbSelect(item) {
  selectedHerb.value = item.id
  selectedSpec.value = ''
  loadSpecs(item.id)
}

async function loadSpecs(herbId) {
  try {
    const data = await getHerbSpecs(herbId)
    specList.value = (data || []).map((s) => ({ label: s.name, value: s.id }))
  } catch {
    specList.value = []
  }
}

async function handleQuery() {
  if (!selectedHerb.value) return
  loading.value = true
  try {
    const params = {
      herbId: selectedHerb.value,
      specId: selectedSpec.value || undefined,
      period: currentPeriod.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const data = await getPriceHistory(params)
    const list = data?.list || data || []
    tableData.value = list
    trendData.value = list.map((item) => ({ date: item.date, price: item.price }))
    hasQueried.value = true
    currentPage.value = 1
  } catch {
    tableData.value = []
    trendData.value = []
  } finally {
    loading.value = false
  }
}

async function handlePeriodChange(period) {
  currentPeriod.value = period
  if (selectedHerb.value) {
    await handleQuery()
  }
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.page-title {
  font-size: 20px;
  color: $text-color;
  margin-bottom: 20px;
}

.filter-section {
  margin-bottom: 24px;
  padding: 16px;
  background: $bg-color;
  border-radius: $border-radius;

  .el-col {
    margin-bottom: 8px;
  }
}

.section-title {
  font-size: 16px;
  color: $text-color;
  margin-bottom: 16px;
}

.chart-section {
  margin-bottom: 30px;
}

.table-section {
  .table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;

    .section-title {
      margin-bottom: 0;
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .filter-section {
    .el-col {
      margin-bottom: 12px;
    }
  }

  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
