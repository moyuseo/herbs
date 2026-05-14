<template>
  <div class="market-price">
    <div class="market-price__tabs">
      <el-tabs v-model="activeMarket" @tab-change="handleSearch">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="亳州" name="亳州" />
        <el-tab-pane label="安国" name="安国" />
        <el-tab-pane label="成都" name="成都" />
        <el-tab-pane label="玉林" name="玉林" />
        <el-tab-pane label="廉桥" name="廉桥" />
        <el-tab-pane label="普宁" name="普宁" />
      </el-tabs>
    </div>

    <div class="market-price__body">
      <div class="market-price__sidebar">
        <el-menu :default-active="activeCategory" @select="handleCategorySelect">
          <el-menu-item index="">全部分类</el-menu-item>
          <el-menu-item v-for="cat in categories" :key="cat" :index="cat">{{ cat }}</el-menu-item>
        </el-menu>
      </div>

      <div class="market-price__main">
        <PriceTable :prices="priceList" :loading="loading" />

        <div class="market-price__pagination">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMarketPrices } from '@/api/price'
import PriceTable from '@/components/price/PriceTable.vue'
import type { PriceVO } from '@/components/price/PriceTable.vue'

const categories = ['根茎类', '果实类', '全草类', '花类', '叶类', '树皮类', '藤木类', '树脂类', '菌藻类', '动物类', '矿物类', '其他类']

const activeMarket = ref('')
const activeCategory = ref('')
const priceList = ref<PriceVO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

async function handleSearch() {
  loading.value = true
  try {
    const res = (await getMarketPrices({
      market: activeMarket.value || undefined,
      category: activeCategory.value || undefined,
      page: page.value,
      pageSize: pageSize.value,
    })) as any
    priceList.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    total.value = res?.total || priceList.value.length
  } catch {
    priceList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleCategorySelect(index: string) {
  activeCategory.value = index
  page.value = 1
  handleSearch()
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.market-price {
  padding: 16px;

  &__tabs {
    background: #fff;
    padding: 0 16px;
    border-radius: 4px;
    margin-bottom: 16px;
  }

  &__body {
    display: flex;
    gap: 16px;
  }

  &__sidebar {
    width: 160px;
    flex-shrink: 0;
    background: #fff;
    border-radius: 4px;

    .el-menu {
      border-right: none;
    }
  }

  &__main {
    flex: 1;
    min-width: 0;
    background: #fff;
    border-radius: 4px;
    padding: 16px;
  }

  &__pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
