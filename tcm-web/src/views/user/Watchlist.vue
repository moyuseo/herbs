<template>
  <div class="watchlist">
    <div class="watchlist__header">
      <h2>我的关注</h2>
      <el-button type="primary" @click="goHerbList">添加关注</el-button>
    </div>

    <div v-if="!userStore.token" class="watchlist__auth">
      <el-empty description="请先登录后查看关注列表">
        <el-button type="primary" @click="goLogin">去登录</el-button>
      </el-empty>
    </div>

    <template v-else>
      <el-table
        v-loading="loading"
        :data="watchList"
        stripe
        class="watchlist__table"
      >
        <el-table-column label="品种名" min-width="120">
          <template #default="{ row }">
            <el-link type="primary" :underline="false" @click="goDetail(row.herbId)">
              {{ row.herbName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="当前价格" min-width="100" align="right">
          <template #default="{ row }">
            <span v-if="row.currentPrice">¥{{ row.currentPrice }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="日涨跌" min-width="100" align="center">
          <template #default="{ row }">
            <PriceTag v-if="row.dayChange !== undefined" :value="row.dayChange" type="day" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="月涨跌" min-width="100" align="center">
          <template #default="{ row }">
            <PriceTag v-if="row.monthChange !== undefined" :value="row.monthChange" type="month" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-popconfirm
              title="确定取消关注？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleUnwatch(row)"
            >
              <template #reference>
                <el-button type="danger" size="small" link>取消关注</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && watchList.length === 0" description="暂无关注品种" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import PriceTag from '@/components/common/PriceTag.vue'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

interface WatchItem {
  herbId: number | string
  herbName: string
  currentPrice: number | null
  dayChange: number
  monthChange: number
}

const loading = ref(false)
const watchList = ref<WatchItem[]>([])

async function fetchWatchList() {
  if (!userStore.token) return
  loading.value = true
  try {
    const res = (await request.get('/user/watchlist')) as any
    watchList.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
  } catch {
    watchList.value = []
  } finally {
    loading.value = false
  }
}

async function handleUnwatch(item: WatchItem) {
  try {
    await request.delete(`/user/watchlist/${item.herbId}`)
    ElMessage.success('已取消关注')
    watchList.value = watchList.value.filter((w) => w.herbId !== item.herbId)
  } catch {
    // error handled by interceptor
  }
}

function goDetail(herbId: number | string) {
  router.push(`/herb/detail/${herbId}`)
}

function goHerbList() {
  router.push('/herb/list')
}

function goLogin() {
  router.push('/login')
}

onMounted(() => {
  fetchWatchList()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.watchlist {
  padding: 20px;
  max-width: 960px;
  margin: 0 auto;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      color: $text-color;
    }
  }

  &__auth {
    padding: 60px 0;
  }

  &__table {
    margin-bottom: 16px;
  }
}
</style>
