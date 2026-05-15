<template>
  <div class="my-publishments">
    <div class="my-publishments__page-title">
      <h2>我的发布</h2>
    </div>

    <div v-if="!userStore.token" class="my-publishments__auth">
      <div class="my-publishments__empty-state">
        <div class="my-publishments__empty-icon">
          <el-icon :size="48"><Lock /></el-icon>
        </div>
        <div class="my-publishments__empty-text">请先登录后查看发布信息</div>
        <button class="my-publishments__empty-btn" @click="goLogin">去登录</button>
      </div>
    </div>

    <template v-else>
      <div class="my-publishments__tabs">
        <button
          class="my-publishments__tab"
          :class="{ 'my-publishments__tab--active': activeTab === 'supply' }"
          @click="activeTab = 'supply'"
        >
          我发布的供应
        </button>
        <button
          class="my-publishments__tab"
          :class="{ 'my-publishments__tab--active': activeTab === 'demand' }"
          @click="activeTab = 'demand'"
        >
          我发布的求购
        </button>
      </div>

      <div v-if="activeTab === 'supply'">
        <div v-loading="supplyLoading" class="my-publishments__grid">
          <div v-for="row in supplyList" :key="row.id" class="my-publishments__card">
            <div class="my-publishments__card-header">
              <span class="my-publishments__card-name">{{ row.herbName }}</span>
              <span class="my-publishments__card-badge" :class="`my-publishments__card-badge--${statusClass(row.status)}`">
                {{ statusLabel(row.status) }}
              </span>
            </div>
            <div class="my-publishments__card-body">
              <div class="my-publishments__card-row">
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">规格</span>
                  <span class="my-publishments__card-value">{{ row.spec || '-' }}</span>
                </div>
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">产地</span>
                  <span class="my-publishments__card-value">{{ row.origin || '-' }}</span>
                </div>
              </div>
              <div class="my-publishments__card-row">
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">数量</span>
                  <span class="my-publishments__card-value">{{ row.quantity }}{{ row.unit }}</span>
                </div>
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">价格</span>
                  <span v-if="row.priceType === '明码'" class="my-publishments__card-value my-publishments__card-value--price">¥{{ row.price }}/{{ row.unit }}</span>
                  <span v-else class="my-publishments__card-value my-publishments__card-value--negotiate">电议</span>
                </div>
              </div>
            </div>
            <div class="my-publishments__card-actions">
              <button class="my-publishments__card-action my-publishments__card-action--primary" @click="handleEditSupply(row)">编辑</button>
              <el-popconfirm
                title="确定删除此供应？"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleDeleteSupply(row)"
              >
                <template #reference>
                  <button class="my-publishments__card-action my-publishments__card-action--danger">删除</button>
                </template>
              </el-popconfirm>
              <button class="my-publishments__card-action my-publishments__card-action--accent" @click="handleRefreshSupply(row)">刷新</button>
            </div>
          </div>
        </div>
        <div v-if="!supplyLoading && supplyList.length === 0" class="my-publishments__empty-state">
          <div class="my-publishments__empty-icon">
            <el-icon :size="48"><Document /></el-icon>
          </div>
          <div class="my-publishments__empty-text">暂无发布的供应</div>
        </div>
      </div>

      <div v-if="activeTab === 'demand'">
        <div v-loading="demandLoading" class="my-publishments__grid">
          <div v-for="row in demandList" :key="row.id" class="my-publishments__card">
            <div class="my-publishments__card-header">
              <span class="my-publishments__card-name">{{ row.herbName }}</span>
              <span class="my-publishments__card-badge" :class="`my-publishments__card-badge--${statusClass(row.status)}`">
                {{ statusLabel(row.status) }}
              </span>
            </div>
            <div class="my-publishments__card-body">
              <div class="my-publishments__card-row">
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">规格</span>
                  <span class="my-publishments__card-value">{{ row.spec || '-' }}</span>
                </div>
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">数量</span>
                  <span class="my-publishments__card-value">{{ row.quantity }}{{ row.unit }}</span>
                </div>
              </div>
              <div class="my-publishments__card-row">
                <div class="my-publishments__card-field">
                  <span class="my-publishments__card-label">报价人数</span>
                  <span class="my-publishments__card-value my-publishments__card-value--accent">{{ row.quoteCount }}</span>
                </div>
              </div>
            </div>
            <div class="my-publishments__card-actions">
              <el-popconfirm
                title="确定删除此求购？"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleDeleteDemand(row)"
              >
                <template #reference>
                  <button class="my-publishments__card-action my-publishments__card-action--danger">删除</button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
        <div v-if="!demandLoading && demandList.length === 0" class="my-publishments__empty-state">
          <div class="my-publishments__empty-icon">
            <el-icon :size="48"><Document /></el-icon>
          </div>
          <div class="my-publishments__empty-text">暂无发布的求购</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('supply')

interface SupplyItem {
  id: number | string
  herbName: string
  spec: string
  origin: string
  quantity: number
  unit: string
  priceType: '明码' | '电议'
  price: number | null
  status: number
}

interface DemandItem {
  id: number | string
  herbName: string
  spec: string
  quantity: number
  unit: string
  quoteCount: number
  status: number
}

const supplyLoading = ref(false)
const demandLoading = ref(false)
const supplyList = ref<SupplyItem[]>([])
const demandList = ref<DemandItem[]>([])

function statusType(status: number): 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<number, 'success' | 'warning' | 'info' | 'danger'> = {
    1: 'success',
    2: 'warning',
    3: 'info',
    0: 'danger',
  }
  return map[status] || 'info'
}

function statusClass(status: number): string {
  const map: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'inactive',
    0: 'danger',
  }
  return map[status] || 'inactive'
}

function statusLabel(status: number): string {
  const map: Record<number, string> = {
    1: '上架中',
    2: '审核中',
    3: '已下架',
    0: '已驳回',
  }
  return map[status] || '未知'
}

async function fetchSupplyList() {
  if (!userStore.token) return
  supplyLoading.value = true
  try {
    const res = (await request.get('/supply/user')) as any
    supplyList.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
  } catch {
    supplyList.value = []
  } finally {
    supplyLoading.value = false
  }
}

async function fetchDemandList() {
  if (!userStore.token) return
  demandLoading.value = true
  try {
    const res = (await request.get('/demand/user')) as any
    demandList.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
  } catch {
    demandList.value = []
  } finally {
    demandLoading.value = false
  }
}

function handleEditSupply(row: SupplyItem) {
  router.push({ path: '/supply/publish', query: { id: String(row.id) } })
}

async function handleDeleteSupply(row: SupplyItem) {
  try {
    await request.delete(`/supply/delete/${row.id}`)
    ElMessage.success('删除成功')
    supplyList.value = supplyList.value.filter((s) => s.id !== row.id)
  } catch {
    // error handled by interceptor
  }
}

async function handleRefreshSupply(row: SupplyItem) {
  try {
    await request.put(`/supply/${row.id}/refresh`)
    ElMessage.success('刷新成功')
  } catch {
    // error handled by interceptor
  }
}

async function handleDeleteDemand(row: DemandItem) {
  try {
    await request.delete(`/demand/${row.id}`)
    ElMessage.success('删除成功')
    demandList.value = demandList.value.filter((d) => d.id !== row.id)
  } catch {
    // error handled by interceptor
  }
}

function goLogin() {
  router.push('/login')
}

onMounted(() => {
  fetchSupplyList()
  fetchDemandList()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.my-publishments {
  padding: 24px 16px;
  max-width: $container-max;
  margin: 0 auto;

  &__page-title {
    margin-bottom: 24px;

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

  &__auth {
    padding: 60px 0;
  }

  &__tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    background: $bg-warm;
    padding: 4px;
    border-radius: 26px;
    border: 1px solid $border-light;
    width: fit-content;
  }

  &__tab {
    padding: 8px 24px;
    border-radius: 22px;
    border: none;
    background: transparent;
    color: $text-secondary;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      color: $primary-color;
    }

    &--active {
      background: $primary-color;
      color: #fff;
      box-shadow: 0 1px 4px rgba($primary-color, 0.3);

      &:hover {
        color: #fff;
      }
    }
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
    min-height: 100px;
  }

  &__card {
    background: $card-bg;
    border-radius: $radius-md;
    border: 1px solid $border-light;
    box-shadow: $shadow-sm;
    overflow: hidden;
    transition: all 0.2s ease;

    &:hover {
      box-shadow: $shadow-md;
      border-color: $border-color;
    }
  }

  &__card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 18px;
    border-bottom: 1px solid $border-light;
    background: $bg-warm;
  }

  &__card-name {
    font-size: 15px;
    font-weight: 600;
    color: $primary-color;
  }

  &__card-badge {
    padding: 3px 10px;
    border-radius: 10px;
    font-size: 12px;
    font-weight: 500;

    &--success {
      background: $primary-lighter;
      color: $primary-color;
    }

    &--warning {
      background: $accent-lighter;
      color: $accent-color;
    }

    &--inactive {
      background: $bg-warm;
      color: $text-muted;
      border: 1px solid $border-color;
    }

    &--danger {
      background: $danger-light;
      color: $danger-color;
    }
  }

  &__card-body {
    padding: 16px 18px;
  }

  &__card-row {
    display: flex;
    gap: 20px;
    margin-bottom: 10px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  &__card-field {
    display: flex;
    flex-direction: column;
    gap: 4px;
    flex: 1;
  }

  &__card-label {
    font-size: 12px;
    color: $text-muted;
  }

  &__card-value {
    font-size: 14px;
    color: $text-color;
    font-weight: 500;

    &--price {
      color: $up-color;
      font-weight: 600;
    }

    &--negotiate {
      color: $accent-color;
      font-style: italic;
    }

    &--accent {
      color: $accent-color;
      font-weight: 600;
    }
  }

  &__card-actions {
    display: flex;
    gap: 8px;
    padding: 12px 18px;
    border-top: 1px solid $border-light;
  }

  &__card-action {
    padding: 5px 14px;
    border-radius: 14px;
    border: none;
    font-size: 12px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;

    &--primary {
      background: $primary-lighter;
      color: $primary-color;

      &:hover {
        background: $primary-color;
        color: #fff;
      }
    }

    &--danger {
      background: $danger-light;
      color: $danger-color;

      &:hover {
        background: $danger-color;
        color: #fff;
      }
    }

    &--accent {
      background: $accent-lighter;
      color: $accent-color;

      &:hover {
        background: $accent-color;
        color: #fff;
      }
    }
  }

  &__empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
  }

  &__empty-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: $primary-lighter;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $primary-color;
    margin-bottom: 16px;
  }

  &__empty-text {
    font-size: 16px;
    font-weight: 500;
    color: $text-color;
  }

  &__empty-btn {
    padding: 10px 28px;
    border-radius: 22px;
    border: none;
    background: $primary-color;
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    margin-top: 16px;

    &:hover {
      background: $primary-light;
      box-shadow: 0 2px 8px rgba($primary-color, 0.3);
    }
  }
}

@media (max-width: 768px) {
  .my-publishments {
    &__grid {
      grid-template-columns: 1fr;
    }

    &__tabs {
      width: 100%;
    }

    &__tab {
      flex: 1;
      text-align: center;
    }
  }
}
</style>
