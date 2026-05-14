<template>
  <div class="origin-price">
    <div class="filter-bar card-box">
      <el-row :gutter="16" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-select v-model="filters.origin" placeholder="选择产地" clearable @change="handleSearch">
            <el-option v-for="o in originOptions" :key="o" :label="o" :value="o" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="8" :md="6">
          <el-input v-model="filters.keyword" placeholder="搜索品种名称" clearable :prefix-icon="Search" @keyup.enter="handleSearch" @clear="handleSearch" />
        </el-col>
        <el-col :xs="24" :sm="8" :md="6">
          <el-select v-model="filters.trend" placeholder="走势筛选" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="升" value="up" />
            <el-option label="降" value="down" />
            <el-option label="稳" value="stable" />
            <el-option label="少" value="rare" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="8" :md="6">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="card-box table-section">
      <PriceTable :table-data="tableData" area-label="产地" :loading="loading" />
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getOriginPrices } from '@/api/market'
import PriceTable from '@/components/market/PriceTable.vue'

const originOptions = ['甘肃', '四川', '云南', '贵州', '广西', '安徽', '河南', '河北', '山东', '山西']

const loading = ref(false)
const tableData = ref([])

const filters = reactive({
  origin: '',
  keyword: '',
  trend: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filters.origin) params.origin = filters.origin
    if (filters.keyword) params.keyword = filters.keyword
    if (filters.trend) params.trend = filters.trend
    const data = await getOriginPrices(params)
    tableData.value = data?.list || data || []
    pagination.total = data?.total || 0
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  filters.origin = ''
  filters.keyword = ''
  filters.trend = ''
  pagination.page = 1
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.origin-price {
  margin-top: 20px;
}

.filter-bar {
  margin-bottom: 16px;
  :deep(.el-select),
  :deep(.el-input) {
    width: 100%;
  }
}

.table-section {
  margin-bottom: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
