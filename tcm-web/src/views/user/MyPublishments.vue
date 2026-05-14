<template>
  <div class="my-publishments">
    <div v-if="!userStore.token" class="my-publishments__auth">
      <el-empty description="请先登录后查看发布信息">
        <el-button type="primary" @click="goLogin">去登录</el-button>
      </el-empty>
    </div>

    <template v-else>
      <el-tabs v-model="activeTab" class="my-publishments__tabs">
        <el-tab-pane label="我发布的供应" name="supply">
          <el-table v-loading="supplyLoading" :data="supplyList" stripe>
            <el-table-column prop="herbName" label="品种" min-width="100" />
            <el-table-column prop="spec" label="规格" min-width="80" />
            <el-table-column prop="origin" label="产地" min-width="80" />
            <el-table-column prop="quantity" label="数量" min-width="80">
              <template #default="{ row }">
                {{ row.quantity }}{{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column label="价格" min-width="100">
              <template #default="{ row }">
                <span v-if="row.priceType === '明码'">¥{{ row.price }}/{{ row.unit }}</span>
                <span v-else>电议</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" min-width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="180" align="center">
              <template #default="{ row }">
                <el-button size="small" link type="primary" @click="handleEditSupply(row)">编辑</el-button>
                <el-popconfirm
                  title="确定删除此供应？"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  @confirm="handleDeleteSupply(row)"
                >
                  <template #reference>
                    <el-button size="small" link type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
                <el-button size="small" link type="success" @click="handleRefreshSupply(row)">刷新</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!supplyLoading && supplyList.length === 0" description="暂无发布的供应" />
        </el-tab-pane>

        <el-tab-pane label="我发布的求购" name="demand">
          <el-table v-loading="demandLoading" :data="demandList" stripe>
            <el-table-column prop="herbName" label="品种" min-width="100" />
            <el-table-column prop="spec" label="规格" min-width="80" />
            <el-table-column prop="quantity" label="数量" min-width="80">
              <template #default="{ row }">
                {{ row.quantity }}{{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column prop="quoteCount" label="报价人数" min-width="90" align="center" />
            <el-table-column label="状态" min-width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="100" align="center">
              <template #default="{ row }">
                <el-popconfirm
                  title="确定删除此求购？"
                  confirm-button-text="确定"
                  cancel-button-text="取消"
                  @confirm="handleDeleteDemand(row)"
                >
                  <template #reference>
                    <el-button size="small" link type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!demandLoading && demandList.length === 0" description="暂无发布的求购" />
        </el-tab-pane>
      </el-tabs>
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
    const res = (await request.get('/user/supply')) as any
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
    const res = (await request.get('/user/demand')) as any
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
    await request.delete(`/supply/${row.id}`)
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
  padding: 20px;
  max-width: 960px;
  margin: 0 auto;

  &__auth {
    padding: 60px 0;
  }

  &__tabs {
    background: #fff;
    padding: 16px;
    border-radius: 4px;
  }
}
</style>
