<template>
  <div class="origin-map-page">
    <div class="origin-map-page__sidebar">
      <el-card shadow="hover" class="origin-map-page__filter-card">
        <template #header>
          <span class="origin-map-page__filter-title">品种筛选</span>
        </template>
        <el-select
          v-model="selectedCategory"
          placeholder="选择品种分类"
          clearable
          style="width: 100%"
          @change="handleCategoryChange"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.value"
            :label="cat.label"
            :value="cat.value"
          />
        </el-select>
        <div v-if="selectedCategory" class="origin-map-page__filter-info">
          <el-tag type="info" size="small">当前：{{ selectedCategoryLabel }}</el-tag>
        </div>
        <el-divider />
        <div class="origin-map-page__legend">
          <div class="origin-map-page__legend-title">热力图图例</div>
          <div class="origin-map-page__legend-bar" />
          <div class="origin-map-page__legend-labels">
            <span>跌</span>
            <span>稳</span>
            <span>涨</span>
          </div>
        </div>
      </el-card>
    </div>
    <div class="origin-map-page__map">
      <OriginMap :heat-data="heatData" :show-heatmap="showHeatmap" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { getOriginPrices } from '@/api/price'
import OriginMap from '@/components/map/OriginMap.vue'

interface HeatPoint {
  lat: number
  lng: number
  intensity: number
}

interface CategoryOption {
  label: string
  value: string
}

const categories: CategoryOption[] = [
  { label: '根及根茎类', value: 'root' },
  { label: '果实种子类', value: 'fruit' },
  { label: '全草类', value: 'herb' },
  { label: '花类', value: 'flower' },
  { label: '叶类', value: 'leaf' },
  { label: '树皮类', value: 'bark' },
  { label: '藤木类', value: 'vine' },
  { label: '树脂类', value: 'resin' },
  { label: '菌藻类', value: 'fungus' },
  { label: '动物类', value: 'animal' },
  { label: '矿物类', value: 'mineral' },
  { label: '其他类', value: 'other' },
]

const categoryHeatMap: Record<string, HeatPoint[]> = {
  root: [
    { lat: 33.87, lng: 115.77, intensity: 0.8 },
    { lat: 38.42, lng: 115.33, intensity: 0.3 },
    { lat: 34.44, lng: 104.02, intensity: 0.7 },
    { lat: 35.08, lng: 105.20, intensity: 0.2 },
    { lat: 35.10, lng: 113.40, intensity: 0.5 },
  ],
  fruit: [
    { lat: 37.48, lng: 105.67, intensity: 0.6 },
    { lat: 35.50, lng: 117.63, intensity: 0.4 },
  ],
  flower: [
    { lat: 35.50, lng: 117.63, intensity: 0.9 },
    { lat: 33.87, lng: 115.77, intensity: 0.5 },
  ],
  herb: [
    { lat: 23.37, lng: 104.33, intensity: 0.6 },
    { lat: 38.42, lng: 115.33, intensity: 0.4 },
  ],
  leaf: [],
  bark: [],
  vine: [],
  resin: [],
  fungus: [],
  animal: [],
  mineral: [],
  other: [],
}

const defaultHeatData: HeatPoint[] = [
  { lat: 33.87, lng: 115.77, intensity: 0.8 },
  { lat: 38.42, lng: 115.33, intensity: 0.3 },
  { lat: 23.37, lng: 104.33, intensity: 0.6 },
  { lat: 34.44, lng: 104.02, intensity: 0.2 },
  { lat: 35.08, lng: 105.20, intensity: 0.1 },
  { lat: 35.50, lng: 117.63, intensity: 0.9 },
  { lat: 37.48, lng: 105.67, intensity: 0.4 },
  { lat: 35.10, lng: 113.40, intensity: 0.5 },
  { lat: 42.33, lng: 127.15, intensity: 0.3 },
]

const selectedCategory = ref('')
const heatData = ref<HeatPoint[]>(defaultHeatData)
const showHeatmap = ref(true)

const selectedCategoryLabel = computed(() => {
  const found = categories.find((c) => c.value === selectedCategory.value)
  return found ? found.label : ''
})

async function fetchHeatDataFromApi(category: string) {
  try {
    const res = (await getOriginPrices({ category, type: 'heat' })) as any
    const list = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    if (list.length && list[0].lat !== undefined) {
      return list.map((item: any) => ({
        lat: item.lat,
        lng: item.lng,
        intensity: item.intensity ?? item.change ?? 0.5,
      }))
    }
    return null
  } catch {
    return null
  }
}

async function handleCategoryChange(category: string) {
  if (!category) {
    heatData.value = defaultHeatData
    return
  }

  const apiData = await fetchHeatDataFromApi(category)
  if (apiData) {
    heatData.value = apiData
  } else {
    heatData.value = categoryHeatMap[category] || defaultHeatData
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.origin-map-page {
  display: flex;
  height: calc(100vh - #{$header-height} - 32px);
  padding: 16px;
  gap: 16px;

  &__sidebar {
    width: 260px;
    flex-shrink: 0;
  }

  &__filter-card {
    height: 100%;

    :deep(.el-card__header) {
      padding: 12px 20px;
    }
  }

  &__filter-title {
    font-size: 16px;
    font-weight: 600;
    color: $text-color;
  }

  &__filter-info {
    margin-top: 12px;
  }

  &__map {
    flex: 1;
    min-width: 0;

    .origin-map {
      height: 100%;
    }
  }

  &__legend {
    padding: 0 4px;
  }

  &__legend-title {
    font-size: 14px;
    font-weight: 500;
    color: $text-color;
    margin-bottom: 8px;
  }

  &__legend-bar {
    height: 12px;
    border-radius: 6px;
    background: linear-gradient(to right, #27ae60, #f39c12, #e74c3c);
  }

  &__legend-labels {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: $text-secondary;
    margin-top: 4px;
  }
}
</style>
