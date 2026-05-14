<template>
  <div class="demand-list">
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

    <div v-loading="loading" class="demand-cards">
      <el-empty v-if="!loading && demandList.length === 0" description="暂无求购信息" />
      <el-card
        v-for="item in demandList"
        :key="item.id"
        shadow="hover"
        class="demand-card"
      >
        <div class="card-header">
          <span class="herb-name">{{ item.herbName }}</span>
          <el-tag
            v-if="item.remainingDays <= 3"
            size="small"
            type="danger"
          >
            即将截止
          </el-tag>
        </div>
        <div class="card-body">
          <div class="info-row">
            <span class="label">规格：</span>
            <span>{{ item.spec || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">需求数量：</span>
            <span>{{ item.quantity }}{{ item.unit }}</span>
          </div>
          <div class="info-row">
            <span class="label">交货地址：</span>
            <span>{{ item.deliveryAddress || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">已报价：</span>
            <span class="quote-count">{{ item.quoteCount }}人</span>
          </div>
          <div class="info-row">
            <span class="label">剩余天数：</span>
            <span :class="['remaining-days', { urgent: item.remainingDays <= 3 }]">
              {{ item.remainingDays > 0 ? `${item.remainingDays}天` : '已截止' }}
            </span>
          </div>
        </div>
        <div class="card-footer">
          <span class="publish-time">{{ item.publishTime }}</span>
          <el-button
            type="primary"
            size="small"
            :disabled="item.remainingDays <= 0"
            @click="handleQuote(item)"
          >
            我要报价
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

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.demand-list {
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

.demand-cards {
  min-height: 300px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(440px, 1fr));
  gap: 12px;
}

.demand-card {
  cursor: default;
  transition: transform 0.2s;
}

.demand-card:hover {
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

.quote-count {
  color: #409eff;
  font-weight: 500;
}

.remaining-days {
  color: #67c23a;
  font-weight: 500;
}

.remaining-days.urgent {
  color: #f56c6c;
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

.publish-time {
  color: #c0c4cc;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
