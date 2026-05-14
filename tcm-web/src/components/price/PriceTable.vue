<template>
  <el-table :data="prices" v-loading="loading" stripe style="width: 100%">
    <el-table-column prop="herbName" label="品种" min-width="100">
      <template #default="{ row }">
        <el-link type="primary" @click="goDetail(row.herbId)">{{ row.herbName }}</el-link>
      </template>
    </el-table-column>
    <el-table-column prop="spec" label="规格" min-width="80" />
    <el-table-column prop="origin" label="产地" min-width="80" />
    <el-table-column prop="market" label="市场" min-width="80" />
    <el-table-column prop="price" label="今日价" min-width="90" sortable>
      <template #default="{ row }">
        <span style="font-weight: 600">¥{{ Number(row.price).toFixed(2) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="dayChange" label="日涨跌" min-width="100" sortable>
      <template #default="{ row }">
        <PriceTag :value="row.dayChange" />
      </template>
    </el-table-column>
    <el-table-column prop="monthChange" label="月涨跌" min-width="100" sortable>
      <template #default="{ row }">
        <PriceTag :value="row.monthChange" />
      </template>
    </el-table-column>
    <el-table-column label="走势" min-width="100" align="center">
      <template #default="{ row }">
        <SparkLine
          :data="row.trend || []"
          :color="row.dayChange >= 0 ? '#e74c3c' : '#27ae60'"
        />
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import PriceTag from './PriceTag.vue'
import SparkLine from './SparkLine.vue'

export interface PriceVO {
  herbId: number
  herbName: string
  spec: string
  origin: string
  market: string
  price: number
  dayChange: number
  monthChange: number
  trend?: number[]
}

defineProps<{
  prices: PriceVO[]
  loading: boolean
}>()

const router = useRouter()

function goDetail(herbId: number) {
  router.push({ name: 'PriceDetail', params: { herbId } })
}
</script>
