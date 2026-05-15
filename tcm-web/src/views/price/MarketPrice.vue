<template>
  <div class="market-price">
    <div class="market-price__title-bar">
      <h1 class="market-price__title">市场行情</h1>
      <p class="market-price__subtitle">实时中药材市场价格信息</p>
    </div>

    <div class="market-price__tabs">
      <button
        class="market-price__tab-pill"
        :class="{ 'market-price__tab-pill--active': activeMarket === '' }"
        @click="activeMarket = ''; handleSearch()"
      >全部</button>
      <button
        v-for="mkt in markets"
        :key="mkt"
        class="market-price__tab-pill"
        :class="{ 'market-price__tab-pill--active': activeMarket === mkt }"
        @click="activeMarket = mkt; handleSearch()"
      >{{ mkt }}</button>
    </div>

    <div class="market-price__body">
      <div class="market-price__sidebar">
        <div class="market-price__sidebar-header">药材分类</div>
        <ul class="market-price__category-list">
          <li
            class="market-price__category-item"
            :class="{ 'market-price__category-item--active': activeCategory === '' }"
            @click="handleCategorySelect('')"
          >全部分类</li>
          <li
            v-for="cat in categories"
            :key="cat"
            class="market-price__category-item"
            :class="{ 'market-price__category-item--active': activeCategory === cat }"
            @click="handleCategorySelect(cat)"
          >{{ cat }}</li>
        </ul>
      </div>

      <div class="market-price__main">
        <div class="market-price__card">
          <PriceTable :prices="priceList" :loading="loading" />
        </div>

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
const markets = ['亳州', '安国', '成都', '玉林', '廉桥', '普宁']

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
$primary-color: #1a5632;
$primary-light: #2d7a4a;
$primary-lighter: #e8f5ee;
$accent-color: #c8953e;
$accent-lighter: #fdf6e8;
$text-color: #1a1a1a;
$text-secondary: #5a5a5a;
$text-muted: #999;
$bg-color: #f7f6f3;
$bg-warm: #faf9f6;
$card-bg: #ffffff;
$border-color: #e8e5df;
$border-light: #f0ede8;
$shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
$radius-sm: 6px;
$radius-md: 10px;
$header-height: 64px;
$font-display: 'Noto Serif SC', serif;
$container-max: 1240px;

.market-price {
  max-width: $container-max;
  margin: 0 auto;
  padding: 24px 20px;
  background: $bg-color;
  min-height: calc(100vh - #{$header-height});

  &__title-bar {
    border-left: 4px solid $primary-color;
    padding: 12px 20px;
    margin-bottom: 24px;
    background: $card-bg;
    border-radius: $radius-sm;
    box-shadow: $shadow-sm;
  }

  &__title {
    font-family: $font-display;
    font-size: 24px;
    font-weight: 700;
    color: $primary-color;
    margin: 0 0 4px;
    letter-spacing: 2px;
  }

  &__subtitle {
    font-size: 13px;
    color: $text-muted;
    margin: 0;
  }

  &__tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 24px;
    padding: 16px 20px;
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
  }

  &__tab-pill {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 8px 22px;
    border: 1px solid $border-color;
    border-radius: 999px;
    background: $bg-warm;
    color: $text-secondary;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.25s ease;
    outline: none;

    &:hover {
      border-color: $primary-light;
      color: $primary-light;
      background: $primary-lighter;
    }

    &--active {
      background: $primary-color;
      border-color: $primary-color;
      color: #fff;
      box-shadow: 0 2px 8px rgba($primary-color, 0.3);

      &:hover {
        background: $primary-light;
        border-color: $primary-light;
        color: #fff;
      }
    }
  }

  &__body {
    display: flex;
    gap: 20px;
    align-items: flex-start;
  }

  &__sidebar {
    width: 180px;
    flex-shrink: 0;
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
    overflow: hidden;
  }

  &__sidebar-header {
    padding: 14px 18px;
    font-family: $font-display;
    font-size: 15px;
    font-weight: 600;
    color: $primary-color;
    border-bottom: 1px solid $border-light;
    background: $bg-warm;
    letter-spacing: 1px;
  }

  &__category-list {
    list-style: none;
    margin: 0;
    padding: 6px 0;
  }

  &__category-item {
    padding: 10px 18px;
    font-size: 14px;
    color: $text-secondary;
    cursor: pointer;
    transition: all 0.2s ease;
    border-left: 3px solid transparent;
    position: relative;

    &:hover {
      color: $primary-color;
      background: $primary-lighter;
    }

    &--active {
      color: $primary-color;
      font-weight: 600;
      background: $primary-lighter;
      border-left-color: $primary-color;

      &::after {
        content: '';
        position: absolute;
        right: 14px;
        top: 50%;
        transform: translateY(-50%);
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: $accent-color;
      }
    }
  }

  &__main {
    flex: 1;
    min-width: 0;
  }

  &__card {
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-md;
    overflow: hidden;
  }

  &__pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    padding: 16px 20px;
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;

    :deep(.el-pagination) {
      .btn-prev,
      .btn-next {
        border-radius: $radius-sm;
        background: $bg-warm;
        border: 1px solid $border-color;

        &:hover {
          color: $primary-color;
          border-color: $primary-light;
        }

        &.disabled {
          opacity: 0.5;
        }
      }

      .el-pager li {
        border-radius: $radius-sm;
        background: $bg-warm;
        border: 1px solid $border-color;
        margin: 0 2px;
        min-width: 32px;

        &:hover {
          color: $primary-color;
          border-color: $primary-light;
        }

        &.is-active {
          background: $primary-color;
          border-color: $primary-color;
          color: #fff;
        }
      }

      .el-pagination__total,
      .el-pagination__sizes {
        color: $text-secondary;
      }

      .el-pagination__jump {
        color: $text-secondary;

        .el-input__wrapper {
          border-radius: $radius-sm;
        }
      }

      .el-select .el-input__wrapper {
        border-radius: $radius-sm;
      }
    }
  }
}
</style>
