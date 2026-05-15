<template>
  <div class="origin-map-page">
    <div class="origin-map-page__page-title">
      <h2>产地价格热力图</h2>
    </div>
    <div class="origin-map-page__body">
      <div class="origin-map-page__sidebar">
        <div class="origin-map-page__filter-card">
          <div class="origin-map-page__filter-header">
            <span class="origin-map-page__filter-title">品种筛选</span>
          </div>
          <div class="origin-map-page__category-pills">
            <button
              v-for="cat in categories"
              :key="cat.value"
              class="origin-map-page__category-pill"
              :class="{ 'origin-map-page__category-pill--active': selectedCategory === cat.value }"
              @click="selectedCategory = selectedCategory === cat.value ? '' : cat.value; handleCategoryChange(selectedCategory)"
            >
              {{ cat.label }}
            </button>
          </div>
          <div v-if="selectedCategory" class="origin-map-page__filter-info">
            <span class="origin-map-page__filter-tag">当前：{{ selectedCategoryLabel }}</span>
          </div>
          <div class="origin-map-page__divider"></div>
          <div class="origin-map-page__legend">
            <div class="origin-map-page__legend-title">热力图图例</div>
            <div class="origin-map-page__legend-bar">
              <div class="origin-map-page__legend-bar-inner"></div>
            </div>
            <div class="origin-map-page__legend-labels">
              <span class="origin-map-page__legend-label--down">跌</span>
              <span class="origin-map-page__legend-label--stable">稳</span>
              <span class="origin-map-page__legend-label--up">涨</span>
            </div>
          </div>
        </div>
      </div>
      <div class="origin-map-page__map">
        <OriginMap :heat-data="heatData" :show-heatmap="showHeatmap" />
      </div>
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
  padding: 20px 16px;
  max-width: $container-max;
  margin: 0 auto;

  &__page-title {
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-family: $font-display;
      font-size: 22px;
      font-weight: 600;
      color: $text-color;
      padding-left: 16px;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: $primary-color;
        border-radius: 2px;
      }
    }
  }

  &__body {
    display: flex;
    height: calc(100vh - #{$header-height} - 120px);
    gap: 20px;
  }

  &__sidebar {
    width: 280px;
    flex-shrink: 0;
  }

  &__filter-card {
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
    border: 1px solid $border-light;
    padding: 20px;
    height: 100%;
    overflow-y: auto;
  }

  &__filter-header {
    margin-bottom: 16px;
  }

  &__filter-title {
    font-family: $font-display;
    font-size: 16px;
    font-weight: 600;
    color: $text-color;
    position: relative;
    padding-left: 12px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 16px;
      background: $accent-color;
      border-radius: 2px;
    }
  }

  &__category-pills {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  &__category-pill {
    padding: 6px 14px;
    border-radius: 18px;
    border: 1px solid $border-color;
    background: $bg-warm;
    color: $text-secondary;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      border-color: $primary-light;
      color: $primary-color;
      background: $primary-lighter;
    }

    &--active {
      background: $primary-color;
      border-color: $primary-color;
      color: #fff;
      box-shadow: 0 1px 4px rgba($primary-color, 0.3);

      &:hover {
        background: $primary-light;
        border-color: $primary-light;
        color: #fff;
      }
    }
  }

  &__filter-info {
    margin-top: 12px;
  }

  &__filter-tag {
    display: inline-flex;
    align-items: center;
    padding: 4px 12px;
    border-radius: 14px;
    background: $accent-lighter;
    color: $accent-color;
    font-size: 12px;
    font-weight: 500;
    border: 1px solid $accent-light;
  }

  &__divider {
    height: 1px;
    background: $border-light;
    margin: 20px 0;
  }

  &__map {
    flex: 1;
    min-width: 0;
    border-radius: $radius-md;
    overflow: hidden;
    border: 1px solid $border-light;
    box-shadow: $shadow-sm;

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
    margin-bottom: 10px;
  }

  &__legend-bar {
    height: 14px;
    border-radius: 7px;
    overflow: hidden;
    background: $bg-warm;
    border: 1px solid $border-light;
  }

  &__legend-bar-inner {
    height: 100%;
    border-radius: 7px;
    background: linear-gradient(to right, $down-color, $accent-color, $up-color);
  }

  &__legend-labels {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    margin-top: 6px;
  }

  &__legend-label--down {
    color: $down-color;
    font-weight: 500;
  }

  &__legend-label--stable {
    color: $accent-color;
    font-weight: 500;
  }

  &__legend-label--up {
    color: $up-color;
    font-weight: 500;
  }
}

@media (max-width: 768px) {
  .origin-map-page {
    &__body {
      flex-direction: column;
      height: auto;
    }

    &__sidebar {
      width: 100%;
    }

    &__map {
      height: 400px;
    }
  }
}
</style>
