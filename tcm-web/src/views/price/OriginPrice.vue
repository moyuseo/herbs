<template>
  <div class="origin-price">
    <div class="origin-price__page-title">
      <h1>产地行情</h1>
    </div>

    <div class="origin-price__province-pills">
      <button
        class="origin-price__pill"
        :class="{ 'origin-price__pill--active': activeProvince === '' }"
        @click="activeProvince = ''; handleSearch()"
      >
        全部
      </button>
      <button
        v-for="prov in provinces"
        :key="prov"
        class="origin-price__pill"
        :class="{ 'origin-price__pill--active': activeProvince === prov }"
        @click="activeProvince = prov; handleSearch()"
      >
        {{ prov }}
      </button>
    </div>

    <div class="origin-price__body">
      <div class="origin-price__sidebar">
        <div class="origin-price__sidebar-title">产区</div>
        <button
          class="origin-price__area-btn"
          :class="{ 'origin-price__area-btn--active': activeArea === '' }"
          @click="handleAreaSelect('')"
        >
          全部产区
        </button>
        <button
          v-for="area in areas"
          :key="area"
          class="origin-price__area-btn"
          :class="{ 'origin-price__area-btn--active': activeArea === area }"
          @click="handleAreaSelect(area)"
        >
          {{ area }}
        </button>
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
@import '@/styles/variables.scss';

.origin-price {
  padding: 20px;
  max-width: $container-max;
  margin: 0 auto;
  background: $bg-warm;
  min-height: calc(100vh - #{$header-height});

  &__page-title {
    margin-bottom: 20px;

    h1 {
      margin: 0;
      font-family: $font-display;
      font-size: 26px;
      font-weight: 700;
      color: $text-color;
      position: relative;
      padding-left: 16px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 4px;
        bottom: 4px;
        width: 4px;
        background: $primary-color;
        border-radius: 2px;
      }
    }
  }

  &__province-pills {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 20px;
    background: $card-bg;
    padding: 12px 16px;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
  }

  &__pill {
    border: 1px solid $border-color;
    background: $card-bg;
    padding: 6px 18px;
    font-size: 13px;
    color: $text-secondary;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.25s;
    white-space: nowrap;

    &--active {
      background: $primary-color;
      border-color: $primary-color;
      color: #fff;
      box-shadow: 0 2px 6px rgba($primary-color, 0.3);
    }

    &:hover:not(&--active) {
      border-color: $primary-color;
      color: $primary-color;
      background: $primary-lighter;
    }
  }

  &__body {
    display: flex;
    gap: 20px;
  }

  &__sidebar {
    width: 160px;
    flex-shrink: 0;
    background: $card-bg;
    border-radius: $radius-md;
    padding: 16px 12px;
    box-shadow: $shadow-sm;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__sidebar-title {
    font-family: $font-display;
    font-size: 15px;
    font-weight: 600;
    color: $text-color;
    padding: 6px 10px;
    margin-bottom: 4px;
    border-bottom: 2px solid $accent-color;
    padding-bottom: 10px;
  }

  &__area-btn {
    border: none;
    background: transparent;
    text-align: left;
    padding: 8px 10px;
    font-size: 13px;
    color: $text-secondary;
    border-radius: $radius-sm;
    cursor: pointer;
    transition: all 0.2s;

    &--active {
      background: $primary-color;
      color: #fff;
      font-weight: 500;
    }

    &:hover:not(&--active) {
      background: $primary-lighter;
      color: $primary-color;
    }
  }

  &__main {
    flex: 1;
    min-width: 0;
    background: $card-bg;
    border-radius: $radius-md;
    padding: 20px;
    box-shadow: $shadow-sm;
  }

  &__pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}

@media (max-width: 768px) {
  .origin-price__body {
    flex-direction: column;
  }

  .origin-price__sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 6px;
  }

  .origin-price__sidebar-title {
    width: 100%;
  }
}
</style>
