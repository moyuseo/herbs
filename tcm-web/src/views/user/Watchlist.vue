<template>
  <div class="watchlist">
    <div class="watchlist__header">
      <h2 class="watchlist__title">我的关注</h2>
      <button class="watchlist__add-btn" @click="goHerbList">
        <el-icon><Plus /></el-icon>
        <span>添加关注</span>
      </button>
    </div>

    <div v-if="!userStore.token" class="watchlist__auth">
      <div class="watchlist__empty-state">
        <div class="watchlist__empty-icon">
          <el-icon :size="48"><Lock /></el-icon>
        </div>
        <div class="watchlist__empty-text">请先登录后查看关注列表</div>
        <button class="watchlist__empty-btn" @click="goLogin">去登录</button>
      </div>
    </div>

    <template v-else>
      <div v-loading="loading" class="watchlist__grid">
        <div v-for="item in watchList" :key="item.herbId" class="watchlist__card">
          <div class="watchlist__card-header">
            <span class="watchlist__card-name" @click="goDetail(item.herbId)">{{ item.herbName }}</span>
            <el-popconfirm
              title="确定取消关注？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleUnwatch(item)"
            >
              <template #reference>
                <button class="watchlist__card-unwatch">取消关注</button>
              </template>
            </el-popconfirm>
          </div>
          <div class="watchlist__card-body">
            <div class="watchlist__card-price">
              <span class="watchlist__card-price-label">当前价格</span>
              <span v-if="item.currentPrice" class="watchlist__card-price-value">¥{{ item.currentPrice }}</span>
              <span v-else class="watchlist__card-price-value watchlist__card-price-value--na">-</span>
            </div>
            <div class="watchlist__card-changes">
              <div class="watchlist__card-change">
                <span class="watchlist__card-change-label">日涨跌</span>
                <PriceTag v-if="item.dayChange !== undefined" :value="item.dayChange" type="day" />
                <span v-else class="watchlist__card-change-na">-</span>
              </div>
              <div class="watchlist__card-change">
                <span class="watchlist__card-change-label">月涨跌</span>
                <PriceTag v-if="item.monthChange !== undefined" :value="item.monthChange" type="month" />
                <span v-else class="watchlist__card-change-na">-</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && watchList.length === 0" class="watchlist__empty-state">
        <div class="watchlist__empty-icon">
          <el-icon :size="48"><Star /></el-icon>
        </div>
        <div class="watchlist__empty-text">暂无关注品种</div>
        <div class="watchlist__empty-hint">浏览品种列表，关注感兴趣的中药材</div>
        <button class="watchlist__empty-btn" @click="goHerbList">去浏览</button>
      </div>
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
  id: number | string
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
    await request.delete(`/user/watchlist/${item.id}`)
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
  padding: 24px 16px;
  max-width: $container-max;
  margin: 0 auto;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
  }

  &__title {
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

  &__add-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 20px;
    border-radius: 22px;
    border: 1.5px solid $primary-color;
    background: $primary-lighter;
    color: $primary-color;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: $primary-color;
      color: #fff;
      box-shadow: 0 2px 8px rgba($primary-color, 0.3);
    }
  }

  &__auth {
    padding: 60px 0;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
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
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $primary-light;
    }
  }

  &__card-unwatch {
    padding: 4px 10px;
    border-radius: 12px;
    border: none;
    background: transparent;
    color: $text-muted;
    font-size: 12px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: $danger-light;
      color: $danger-color;
    }
  }

  &__card-body {
    padding: 16px 18px;
  }

  &__card-price {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 12px;
  }

  &__card-price-label {
    font-size: 13px;
    color: $text-muted;
  }

  &__card-price-value {
    font-family: $font-display;
    font-size: 22px;
    font-weight: 700;
    color: $text-color;

    &--na {
      font-size: 16px;
      color: $text-muted;
      font-weight: 400;
    }
  }

  &__card-changes {
    display: flex;
    gap: 20px;
  }

  &__card-change {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__card-change-label {
    font-size: 12px;
    color: $text-muted;
  }

  &__card-change-na {
    font-size: 14px;
    color: $text-muted;
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
    margin-bottom: 8px;
  }

  &__empty-hint {
    font-size: 14px;
    color: $text-muted;
    margin-bottom: 20px;
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

    &:hover {
      background: $primary-light;
      box-shadow: 0 2px 8px rgba($primary-color, 0.3);
    }
  }
}

@media (max-width: 768px) {
  .watchlist {
    &__grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
