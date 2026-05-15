<template>
  <div class="herb-detail">
    <div v-loading="loading" class="herb-detail__content">
      <el-empty v-if="!loading && !herb" description="未找到药材信息" />

      <template v-if="herb">
        <nav class="breadcrumb">
          <span class="breadcrumb__item" @click="$router.push('/')">首页</span>
          <span class="breadcrumb__sep">/</span>
          <span class="breadcrumb__item" @click="$router.push('/herb')">药材名录</span>
          <span class="breadcrumb__sep">/</span>
          <span class="breadcrumb__item breadcrumb__item--current">{{ herb.name }}</span>
        </nav>

        <div class="detail-hero">
          <div class="detail-hero__left">
            <h1 class="detail-hero__name">{{ herb.name }}</h1>
            <span class="detail-hero__alias">{{ herb.alias }}</span>
          </div>
          <div class="detail-hero__right">
            <el-button
              :class="['watch-btn', { 'is-watched': isWatched }]"
              @click="toggleWatch"
            >
              <span class="watch-btn__icon">{{ isWatched ? '★' : '☆' }}</span>
              {{ isWatched ? '已关注' : '加入关注' }}
            </el-button>
          </div>
        </div>

        <div class="detail-body">
          <div class="detail-body__main">
            <section class="info-section">
              <div class="section-divider">
                <span class="section-divider__icon">◆</span>
                <h2 class="section-divider__title">基本信息</h2>
              </div>
              <el-descriptions :column="2" border class="herb-descriptions">
                <el-descriptions-item label="性味">{{ herb.natureFlavor || herb.property || '-' }}</el-descriptions-item>
                <el-descriptions-item label="归经">{{ herb.meridianTropism || herb.meridian || '-' }}</el-descriptions-item>
                <el-descriptions-item label="药用部位">{{ herb.medicinalPart || '-' }}</el-descriptions-item>
                <el-descriptions-item label="功效">{{ herb.efficacy || herb.effect || '-' }}</el-descriptions-item>
                <el-descriptions-item label="主治" :span="2">{{ herb.indication || '-' }}</el-descriptions-item>
                <el-descriptions-item label="分类">{{ herb.categoryName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="价格趋势">
                  <span v-if="herb.priceTrend" :class="['price-trend', herb.priceTrend === '上涨' ? 'price-trend--up' : herb.priceTrend === '下跌' ? 'price-trend--down' : '']">
                    {{ herb.priceTrend === '上涨' ? '↑' : herb.priceTrend === '下跌' ? '↓' : '' }}
                    {{ herb.priceTrend }}
                  </span>
                  <span v-else>-</span>
                </el-descriptions-item>
              </el-descriptions>
            </section>

            <section class="info-section">
              <div class="section-divider">
                <span class="section-divider__icon">◆</span>
                <h2 class="section-divider__title">产地分布</h2>
              </div>
              <div class="section-content">
                <p class="section-text">{{ herb.description || herb.originDesc || '暂无产地分布信息' }}</p>
              </div>
            </section>

            <section class="info-section">
              <div class="section-divider">
                <span class="section-divider__icon">◆</span>
                <h2 class="section-divider__title">规格等级</h2>
              </div>
              <div class="section-content">
                <p class="section-text">{{ herb.specDesc || '按市场统货、选货等规格分级' }}</p>
              </div>
            </section>

            <section class="info-section">
              <div class="section-divider">
                <span class="section-divider__icon">◆</span>
                <h2 class="section-divider__title">关联资讯</h2>
              </div>
              <div class="section-content">
                <el-empty description="暂无关联资讯" :image-size="60" />
              </div>
            </section>
          </div>

          <aside class="detail-body__side">
            <div class="price-card">
              <div class="price-card__header">
                <h3 class="price-card__title">当前价格</h3>
              </div>
              <div class="price-card__body">
                <template v-if="herb.currentPrice">
                  <div class="price-card__value">
                    <span class="price-card__symbol">¥</span>
                    <span class="price-card__amount">{{ herb.currentPrice }}</span>
                  </div>
                  <div class="price-card__meta">
                    <span v-if="herb.priceTrend" :class="['price-card__trend', herb.priceTrend === '上涨' ? 'price-card__trend--up' : herb.priceTrend === '下跌' ? 'price-card__trend--down' : '']">
                      {{ herb.priceTrend === '上涨' ? '↑' : herb.priceTrend === '下跌' ? '↓' : '' }}
                      {{ herb.priceTrend }}
                    </span>
                  </div>
                  <el-button class="price-card__link" type="primary" @click="goPriceDetail">
                    查看价格详情 →
                  </el-button>
                </template>
                <el-empty v-else description="暂无价格数据" :image-size="60" />
              </div>
            </div>

            <div class="related-card">
              <div class="price-card__header">
                <h3 class="price-card__title">相关药材</h3>
              </div>
              <div class="related-card__body">
                <div class="related-item">
                  <span class="related-item__name">黄芪</span>
                  <span class="related-item__tag">根茎类</span>
                </div>
                <div class="related-item">
                  <span class="related-item__name">当归</span>
                  <span class="related-item__tag">根茎类</span>
                </div>
                <div class="related-item">
                  <span class="related-item__name">白术</span>
                  <span class="related-item__tag">根茎类</span>
                </div>
                <div class="related-item">
                  <span class="related-item__name">党参</span>
                  <span class="related-item__tag">根茎类</span>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHerbDetail } from '@/api/herb'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const isWatched = ref(false)

interface HerbDetail {
  id: number | string
  herbId: number | string
  name: string
  alias: string
  natureFlavor: string
  property: string
  meridianTropism: string
  meridian: string
  medicinalPart: string
  efficacy: string
  effect: string
  indication: string
  description: string
  originDesc: string
  specDesc: string
  currentPrice: number | null
  priceTrend: string
  categoryName: string
}

const herb = ref<HerbDetail | null>(null)

async function fetchDetail() {
  const herbId = route.params.herbId as string
  if (!herbId) return
  loading.value = true
  try {
    const res = (await getHerbDetail(herbId)) as any
    herb.value = res?.data || res || null
  } catch {
    herb.value = null
  } finally {
    loading.value = false
  }
}

function toggleWatch() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  isWatched.value = !isWatched.value
  ElMessage.success(isWatched.value ? '已加入关注' : '已取消关注')
}

function goPriceDetail() {
  router.push(`/price/detail/${route.params.herbId}`)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.herb-detail {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 60px;

  &__content {
    max-width: $container-max;
    margin: 0 auto;
    padding: 0 24px;
    min-height: 400px;
  }
}

.breadcrumb {
  padding: 20px 0 16px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;

  &__item {
    color: $text-secondary;
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $primary-color;
    }

    &--current {
      color: $primary-color;
      cursor: default;
      font-weight: 500;

      &:hover {
        color: $primary-color;
      }
    }
  }

  &__sep {
    color: $border-color;
    font-size: 12px;
  }
}

.detail-hero {
  background: $card-bg;
  border-radius: $radius-md;
  padding: 32px 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  border: 1px solid $border-light;
  box-shadow: $shadow-sm;

  &__left {
    display: flex;
    align-items: baseline;
    gap: 16px;
    flex-wrap: wrap;
  }

  &__name {
    font-family: $font-display;
    font-size: 36px;
    font-weight: 700;
    color: $primary-color;
    margin: 0;
    position: relative;
    padding-bottom: 8px;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 48px;
      height: 3px;
      background: $accent-color;
      border-radius: 2px;
    }
  }

  &__alias {
    font-size: 15px;
    color: $text-muted;
  }
}

.watch-btn {
  border-radius: 20px !important;
  padding: 8px 24px !important;
  font-size: 14px !important;
  border-color: $primary-color !important;
  color: $primary-color !important;
  background: transparent !important;
  transition: all 0.25s ease !important;

  &__icon {
    margin-right: 4px;
  }

  &:hover {
    background: $primary-lighter !important;
  }

  &.is-watched {
    background: $primary-color !important;
    color: #fff !important;
    border-color: $primary-color !important;
  }
}

.detail-body {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  align-items: start;

  @media (max-width: 960px) {
    grid-template-columns: 1fr;
  }
}

.info-section {
  background: $card-bg;
  border-radius: $radius-md;
  padding: 24px 28px;
  margin-bottom: 20px;
  border: 1px solid $border-light;
}

.section-divider {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid $border-light;

  &__icon {
    color: $accent-color;
    font-size: 10px;
  }

  &__title {
    font-family: $font-display;
    font-size: 18px;
    font-weight: 600;
    color: $primary-color;
    margin: 0;
  }
}

.section-content {
  padding: 4px 0;
}

.section-text {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.9;
  margin: 0;
}

.herb-descriptions {
  :deep(.el-descriptions__label) {
    background: $primary-lighter !important;
    color: $primary-color !important;
    font-weight: 500 !important;
    font-size: 13px !important;
    min-width: 90px;
  }

  :deep(.el-descriptions__content) {
    font-size: 14px !important;
    color: $text-color !important;
  }

  :deep(.el-descriptions__cell) {
    border-color: $border-color !important;
  }

  :deep(.el-descriptions__header) {
    margin-bottom: 16px;
  }
}

.price-trend {
  font-weight: 600;
  font-size: 14px;

  &--up {
    color: $up-color;
  }

  &--down {
    color: $down-color;
  }
}

.price-card {
  background: $card-bg;
  border-radius: $radius-md;
  border: 1px solid $border-light;
  overflow: hidden;
  margin-bottom: 20px;

  &__header {
    padding: 16px 20px 12px;
    border-bottom: 1px solid $border-light;
  }

  &__title {
    font-family: $font-display;
    font-size: 16px;
    font-weight: 600;
    color: $primary-color;
    margin: 0;
  }

  &__body {
    padding: 20px;
  }

  &__value {
    display: flex;
    align-items: baseline;
    gap: 4px;
    margin-bottom: 8px;
  }

  &__symbol {
    font-size: 18px;
    font-weight: 600;
    color: $up-color;
  }

  &__amount {
    font-family: $font-display;
    font-size: 36px;
    font-weight: 700;
    color: $up-color;
    line-height: 1;
  }

  &__meta {
    margin-bottom: 16px;
  }

  &__trend {
    font-size: 13px;
    font-weight: 600;

    &--up {
      color: $up-color;
    }

    &--down {
      color: $down-color;
    }
  }

  &__link {
    width: 100%;
    background: $primary-color !important;
    border-color: $primary-color !important;
    border-radius: $radius-sm !important;

    &:hover {
      background: $primary-light !important;
      border-color: $primary-light !important;
    }
  }
}

.related-card {
  background: $card-bg;
  border-radius: $radius-md;
  border: 1px solid $border-light;
  overflow: hidden;

  &__body {
    padding: 8px 12px;
  }
}

.related-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 8px;
  border-bottom: 1px solid $border-light;
  cursor: pointer;
  transition: all 0.2s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: $primary-lighter;

    .related-item__name {
      color: $primary-color;
    }
  }

  &__name {
    font-family: $font-display;
    font-size: 14px;
    font-weight: 500;
    color: $text-color;
    transition: color 0.2s;
  }

  &__tag {
    font-size: 11px;
    color: $text-muted;
    background: $bg-warm;
    padding: 2px 8px;
    border-radius: 10px;
  }
}
</style>
