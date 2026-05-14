<template>
  <div class="origin-price">
    <div class="origin-price__tabs">
      <el-tabs v-model="activeProvince" @tab-change="handleSearch">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane v-for="prov in provinces" :key="prov" :label="prov" :name="prov" />
      </el-tabs>
    </div>

    <div class="origin-price__body">
      <div class="origin-price__sidebar">
        <el-menu :default-active="activeArea" @select="handleAreaSelect">
          <el-menu-item index="">全部产区</el-menu-item>
          <el-menu-item v-for="area in areas" :key="area" :index="area">{{ area }}</el-menu-item>
        </el-menu>
      </div>

      <div class="origin-price__main">
        <PriceTable :prices="priceList" :loading="loading" />

        <div class="origin-price__pagination">
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
import { getOriginPrices } from '@/api/price'
import PriceTable from '@/components/price/PriceTable.vue'
import type { PriceVO } from '@/components/price/PriceTable.vue'

const provinces = ['四川', '云南', '贵州', '甘肃', '安徽', '河南', '河北', '广西', '广东', '湖北', '湖南', '吉林']

const areas = ['川贝母产区', '三七产区', '当归产区', '黄芪产区', '党参产区', '丹参产区', '白芍产区', '茯苓产区', '枸杞产区', '人参产区']

const activeProvince = ref('')
const activeArea = ref('')
const priceList = ref<PriceVO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

async function handleSearch() {
  loading.value = true
  try {
    const res = (await getOriginPrices({
      province: activeProvince.value || undefined,
      area: activeArea.value || undefined,
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

function handleAreaSelect(index: string) {
  activeArea.value = index
  page.value = 1
  handleSearch()
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.origin-price {
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
