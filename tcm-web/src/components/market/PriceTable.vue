<template>
  <el-table :data="tableData" stripe v-loading="loading" class="price-table">
    <el-table-column prop="name" label="品种" min-width="100">
      <template #default="{ row }">
        <router-link :to="`/market/herb/${row.id}`" class="herb-link">{{ row.name }}</router-link>
      </template>
    </el-table-column>
    <el-table-column prop="spec" label="规格" min-width="100" />
    <el-table-column prop="area" :label="areaLabel" min-width="100" />
    <el-table-column prop="price" label="今日价" min-width="90" align="right">
      <template #default="{ row }">
        <span class="price-value">{{ row.price }}</span>
      </template>
    </el-table-column>
    <el-table-column label="月涨跌" min-width="100" align="center">
      <template #default="{ row }">
        <TrendTag :trend="row.trend" :change-percent="row.changePercent" />
      </template>
    </el-table-column>
    <el-table-column label="走势" width="80" align="center">
      <template #default="{ row }">
        <el-button type="primary" link size="small" @click="handleViewTrend(row)">查看</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { useRouter } from 'vue-router'
import TrendTag from './TrendTag.vue'

const props = defineProps({
  tableData: { type: Array, default: () => [] },
  areaLabel: { type: String, default: '市场' },
  loading: { type: Boolean, default: false }
})

const router = useRouter()

function handleViewTrend(row) {
  router.push(`/market/price-history/${row.id}`)
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.herb-link {
  color: $primary-color;
  font-weight: 500;
  &:hover {
    color: $primary-light;
    text-decoration: underline;
  }
}

.price-value {
  font-weight: 600;
  color: $text-color;
}
</style>
