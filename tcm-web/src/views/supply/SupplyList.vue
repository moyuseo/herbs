<template>
  <div class="supply-list">
    <div class="page-header">
      <div class="page-title">
        <h1>供应信息</h1>
        <p class="page-subtitle">优质中药材供应，源头直供品质保障</p>
      </div>
      <el-button class="publish-btn" @click="router.push('/supply/publish')">
        <el-icon><Plus /></el-icon>
        发布供应
      </el-button>
    </div>

    <div class="filter-section">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="品种">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索品种名称"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="产地">
          <el-select
            v-model="filters.origin"
            placeholder="选择产地"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="item in originOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-select
            v-model="filters.spec"
            placeholder="规格筛选"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="item in specOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-loading="loading" class="supply-cards">
      <el-empty v-if="!loading && supplyList.length === 0" description="暂无供应信息" />
      <div
        v-for="item in supplyList"
        :key="item.id"
        class="supply-card"
      >
        <div class="card-top">
          <div class="card-title-row">
            <span class="herb-name">{{ item.herbName }}</span>
            <span
              :class="['price-type-badge', getPriceTypeLabel(item.priceType) === '明码' ? 'badge-explicit' : 'badge-negotiate']"
            >
              {{ getPriceTypeLabel(item.priceType) }}
            </span>
          </div>
          <div class="card-price-row">
            <span v-if="getPriceTypeLabel(item.priceType) === '明码'" class="price-explicit">
              ¥{{ item.price }}<span class="price-unit">/{{ item.unit }}</span>
            </span>
            <span v-else class="price-negotiate-badge">电议</span>
          </div>
        </div>

        <div class="card-info-grid">
          <div class="info-item">
            <span class="info-label">规格</span>
            <span class="info-value">{{ item.spec || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">产地</span>
            <span class="info-value">{{ item.origin || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">数量</span>
            <span class="info-value">{{ item.quantity }}{{ item.unit }}</span>
          </div>
        </div>

        <div v-if="item.description" class="card-desc">
          {{ item.description }}
        </div>

        <div class="card-footer">
          <span class="publish-time">{{ item.createdAt || item.publishTime }}</span>
          <div class="contact-area">
            <span v-if="userStore.token" class="contact-info">
              <el-icon><Phone /></el-icon>
              {{ item.contactPhone }}
            </span>
            <el-button v-else type="primary" size="small" class="login-btn" @click="goLogin">
              登录后查看
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSupplyList } from '@/api/supply'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

interface SupplyItem {
  id: number | string
  herbName: string
  spec: string
  origin: string
  quantity: number
  unit: string
  priceType: number | string
  price: number | null
  contactName: string
  contactPhone: string
  description: string
  createdAt: string
  publishTime: string
}

const loading = ref(false)
const supplyList = ref<SupplyItem[]>([])

const filters = reactive({
  keyword: '',
  origin: '',
  spec: '',
})

const originOptions = ref<string[]>([
  '安徽', '四川', '云南', '甘肃', '广西', '贵州', '河南', '河北', '山东', '山西',
])

const specOptions = ref<string[]>([
  '统货', '选货', '一级', '二级', '三级', '特级',
])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getSupplyList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      origin: filters.origin || undefined,
      spec: filters.spec || undefined,
    })
    supplyList.value = res.list || []
    pagination.total = res.total || 0
  } catch {
    supplyList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchList()
}

function resetFilters() {
  filters.keyword = ''
  filters.origin = ''
  filters.spec = ''
  handleSearch()
}

function goLogin() {
  router.push('/login')
}

function getPriceTypeLabel(priceType: number | string): string {
  if (priceType === 1 || priceType === '1') return '明码'
  if (priceType === 2 || priceType === '2') return '电议'
  return String(priceType)
}

function getPriceTypeTag(priceType: number | string): string {
  if (priceType === 2 || priceType === '2') return 'warning'
  return 'success'
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="scss" scoped>
$primary-color: #1a5632;
$primary-light: #2d7a4a;
$primary-lighter: #e8f5ee;
$accent-color: #c8953e;
$accent-lighter: #fdf6e8;
$text-color: #1a1a1a;
$text-secondary: #5a5a5a;
$bg-color: #f7f6f3;
$bg-warm: #faf9f6;
$card-bg: #ffffff;
$border-color: #e8e5df;
$border-light: #f0ede8;
$shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
$shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08), 0 2px 4px rgba(0, 0, 0, 0.04);
$radius-sm: 6px;
$radius-md: 10px;
$font-display: 'Noto Serif SC', serif;
$container-max: 1240px;
$danger-color: #c0392b;
$warning-color: #c8953e;

.supply-list {
  max-width: $container-max;
  margin: 0 auto;
  padding: 32px 24px;
  background: $bg-color;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 28px;
}

.page-title {
  h1 {
    font-family: $font-display;
    font-size: 28px;
    font-weight: 700;
    color: $text-color;
    margin: 0;
    padding-left: 16px;
    border-left: 4px solid $primary-color;
    line-height: 1.3;
  }

  .page-subtitle {
    margin: 6px 0 0 16px;
    font-size: 14px;
    color: $text-secondary;
  }
}

.publish-btn {
  background: $primary-color;
  border-color: $primary-color;
  color: #fff;
  font-size: 15px;
  padding: 10px 24px;
  border-radius: $radius-sm;
  transition: all 0.25s ease;

  &:hover,
  &:focus {
    background: $primary-light;
    border-color: $primary-light;
  }

  .el-icon {
    margin-right: 4px;
  }
}

.filter-section {
  background: $card-bg;
  border-radius: $radius-md;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: $shadow-sm;
  border: 1px solid $border-light;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.supply-cards {
  min-height: 300px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.supply-card {
  background: $card-bg;
  border-radius: $radius-md;
  border: 1px solid $border-color;
  padding: 0;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: $shadow-sm;
  cursor: default;
  display: flex;
  flex-direction: column;

  &:hover {
    border-color: $accent-color;
    box-shadow: $shadow-md, 0 0 0 1px rgba($accent-color, 0.15);
    transform: translateY(-3px);
  }
}

.card-top {
  padding: 20px 20px 16px;
  border-bottom: 1px solid $border-light;
  background: $bg-warm;
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.herb-name {
  font-family: $font-display;
  font-size: 18px;
  font-weight: 700;
  color: $text-color;
}

.price-type-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.badge-explicit {
  background: $primary-lighter;
  color: $primary-color;
}

.badge-negotiate {
  background: $accent-lighter;
  color: $accent-color;
}

.card-price-row {
  display: flex;
  align-items: baseline;
}

.price-explicit {
  font-family: $font-display;
  font-size: 26px;
  font-weight: 700;
  color: $accent-color;
  line-height: 1.2;

  .price-unit {
    font-size: 13px;
    font-weight: 400;
    color: $text-secondary;
  }
}

.price-negotiate-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 14px;
  background: $accent-lighter;
  color: $accent-color;
  border-radius: $radius-sm;
  font-size: 14px;
  font-weight: 600;
}

.card-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 16px 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: $text-secondary;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: $text-color;
}

.card-desc {
  padding: 0 20px 16px;
  font-size: 13px;
  color: $text-secondary;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid $border-light;
  margin-top: auto;
}

.publish-time {
  font-size: 12px;
  color: #b0ada6;
}

.contact-area {
  display: flex;
  align-items: center;
}

.contact-info {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: $primary-color;
  font-size: 13px;
  font-weight: 500;
}

.login-btn {
  --el-button-bg-color: #{$primary-color};
  --el-button-border-color: #{$primary-color};
  --el-button-hover-bg-color: #{$primary-light};
  --el-button-hover-border-color: #{$primary-light};
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-bottom: 16px;
}
</style>
