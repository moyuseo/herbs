<template>
  <div class="demand-list">
    <div class="page-header">
      <div class="page-title">
        <h1>求购信息</h1>
        <p class="page-subtitle">精准匹配需求，高效对接资源</p>
      </div>
      <el-button class="publish-btn" @click="router.push('/demand/publish')">
        <el-icon><Plus /></el-icon>
        发布求购
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

    <div v-loading="loading" class="demand-cards">
      <el-empty v-if="!loading && demandList.length === 0" description="暂无求购信息" />
      <div
        v-for="item in demandList"
        :key="item.id"
        class="demand-card"
      >
        <div class="card-top">
          <div class="card-title-row">
            <span class="herb-name">{{ item.herbName }}</span>
            <span
              :class="['countdown-badge', getCountdownClass(item.remainingDays)]"
            >
              <template v-if="item.remainingDays > 0">
                剩余 {{ item.remainingDays }} 天
              </template>
              <template v-else>已截止</template>
            </span>
          </div>
        </div>

        <div class="card-info-grid">
          <div class="info-item">
            <span class="info-label">规格</span>
            <span class="info-value">{{ item.spec || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">需求数量</span>
            <span class="info-value">{{ item.quantity }}{{ item.unit }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">交货地址</span>
            <span class="info-value address-value">{{ item.deliveryAddress || '-' }}</span>
          </div>
        </div>

        <div class="card-meta-row">
          <span class="quote-badge">
            <el-icon><ChatDotRound /></el-icon>
            {{ item.quoteCount }}人已报价
          </span>
        </div>

        <div v-if="item.description" class="card-desc">
          {{ item.description }}
        </div>

        <div class="card-footer">
          <span class="publish-time">{{ item.publishTime }}</span>
          <el-button
            class="quote-btn"
            :disabled="item.remainingDays <= 0"
            @click="handleQuote(item)"
          >
            我要报价
          </el-button>
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
import { ElMessage } from 'element-plus'
import { getDemandList } from '@/api/supply'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

interface DemandItem {
  id: number | string
  herbName: string
  spec: string
  quantity: number
  unit: string
  deliveryAddress: string
  quoteCount: number
  remainingDays: number
  publishTime: string
  createdAt: string
  expireAt: string
  description: string
}

const loading = ref(false)
const demandList = ref<DemandItem[]>([])

const filters = reactive({
  keyword: '',
  spec: '',
})

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
    const res = await getDemandList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      spec: filters.spec || undefined,
    })
    const list = res.list || []
    demandList.value = list.map((item: any) => ({
      ...item,
      remainingDays: item.remainingDays ?? calcRemainingDays(item.expireAt),
      publishTime: item.publishTime || item.createdAt,
    }))
    pagination.total = res.total || 0
  } catch {
    demandList.value = []
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
  filters.spec = ''
  handleSearch()
}

function handleQuote(item: DemandItem) {
  if (!userStore.token) {
    ElMessage.warning('请先登录后再报价')
    router.push('/login')
    return
  }
  ElMessage.success('报价功能开发中，敬请期待')
}

function calcRemainingDays(expireAt: string): number {
  if (!expireAt) return 0
  const expire = new Date(expireAt)
  const now = new Date()
  return Math.max(0, Math.ceil((expire.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)))
}

function getCountdownClass(days: number): string {
  if (days <= 0) return 'countdown-expired'
  if (days < 3) return 'countdown-urgent'
  if (days <= 7) return 'countdown-warning'
  return 'countdown-safe'
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

.demand-list {
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

.demand-cards {
  min-height: 300px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.demand-card {
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
}

.herb-name {
  font-family: $font-display;
  font-size: 18px;
  font-weight: 700;
  color: $text-color;
}

.countdown-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.3px;
  white-space: nowrap;
}

.countdown-safe {
  background: $primary-lighter;
  color: $primary-color;
}

.countdown-warning {
  background: $accent-lighter;
  color: $accent-color;
}

.countdown-urgent {
  background: #fde8e8;
  color: $danger-color;
}

.countdown-expired {
  background: #f0ede8;
  color: #999;
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

.address-value {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta-row {
  padding: 0 20px 12px;
}

.quote-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: $primary-lighter;
  color: $primary-color;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;

  .el-icon {
    font-size: 13px;
  }
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

.quote-btn {
  background: $primary-color;
  border-color: $primary-color;
  color: #fff;
  font-size: 14px;
  padding: 8px 22px;
  border-radius: $radius-sm;
  transition: all 0.25s ease;

  &:hover,
  &:focus {
    background: $primary-light;
    border-color: $primary-light;
  }

  &.is-disabled {
    background: #c0c4cc;
    border-color: #c0c4cc;
    cursor: not-allowed;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-bottom: 16px;
}
</style>
