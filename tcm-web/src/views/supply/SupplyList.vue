<template>
  <div class="supply-list">
    <el-card shadow="never" class="filter-card">
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
    </el-card>

    <div v-loading="loading" class="supply-cards">
      <el-empty v-if="!loading && supplyList.length === 0" description="暂无供应信息" />
      <el-card
        v-for="item in supplyList"
        :key="item.id"
        shadow="hover"
        class="supply-card"
      >
        <div class="card-header">
          <span class="herb-name">{{ item.herbName }}</span>
          <el-tag size="small" :type="getPriceTypeTag(item.priceType)">
            {{ getPriceTypeLabel(item.priceType) }}
          </el-tag>
        </div>
        <div class="card-body">
          <div class="info-row">
            <span class="label">规格：</span>
            <span>{{ item.spec || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">产地：</span>
            <span>{{ item.origin || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">数量：</span>
            <span>{{ item.quantity }}{{ item.unit }}</span>
          </div>
          <div class="info-row">
            <span class="label">价格：</span>
            <span v-if="getPriceTypeLabel(item.priceType) === '明码'" class="price-value">
              ¥{{ item.price }}/{{ item.unit }}
            </span>
            <span v-else class="price-negotiable">电议</span>
          </div>
          <div v-if="item.description" class="info-row">
            <span class="label">说明：</span>
            <span>{{ item.description }}</span>
          </div>
        </div>
        <div class="card-footer">
          <span class="publish-time">{{ item.createdAt || item.publishTime }}</span>
          <span v-if="userStore.token" class="contact-info">
            <el-icon><Phone /></el-icon>
            {{ item.contactPhone }}
          </span>
          <el-button v-else type="primary" size="small" @click="goLogin">
            登录后查看
          </el-button>
        </div>
      </el-card>
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

<style scoped>
.supply-list {
  padding: 20px;
  max-width: 960px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.supply-cards {
  min-height: 300px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(440px, 1fr));
  gap: 12px;
}

.supply-card {
  cursor: default;
  transition: transform 0.2s;
}

.supply-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.herb-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-body {
  margin-bottom: 12px;
}

.info-row {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

.info-row .label {
  color: #909399;
}

.price-value {
  color: #e6a23c;
  font-weight: 600;
}

.price-negotiable {
  color: #e6a23c;
  font-style: italic;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #c0c4cc;
  border-top: 1px solid #f0f0f0;
  padding-top: 10px;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #67c23a;
  font-size: 13px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
