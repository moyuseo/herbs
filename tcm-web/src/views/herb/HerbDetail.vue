<template>
  <div class="herb-detail">
    <div v-loading="loading" class="herb-detail__content">
      <el-empty v-if="!loading && !herb" description="未找到药材信息" />

      <template v-if="herb">
        <div class="herb-detail__header">
          <div class="herb-detail__title">
            <h1>{{ herb.name }}</h1>
            <span class="herb-detail__alias">{{ herb.alias }}</span>
          </div>
          <el-button
            :type="isWatched ? 'default' : 'primary'"
            @click="toggleWatch"
          >
            {{ isWatched ? '已关注' : '加入关注' }}
          </el-button>
        </div>

        <el-card shadow="never" class="herb-detail__card">
          <template #header>
            <span class="card-title">基本信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="性味">{{ herb.natureFlavor || herb.property || '-' }}</el-descriptions-item>
            <el-descriptions-item label="归经">{{ herb.meridianTropism || herb.meridian || '-' }}</el-descriptions-item>
            <el-descriptions-item label="药用部位">{{ herb.medicinalPart || '-' }}</el-descriptions-item>
            <el-descriptions-item label="功效">{{ herb.efficacy || herb.effect || '-' }}</el-descriptions-item>
            <el-descriptions-item label="主治" :span="2">{{ herb.indication || '-' }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ herb.categoryName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="价格趋势">{{ herb.priceTrend || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="herb-detail__card">
          <template #header>
            <span class="card-title">产地分布</span>
          </template>
          <p class="herb-detail__text">{{ herb.description || herb.originDesc || '暂无产地分布信息' }}</p>
        </el-card>

        <el-card shadow="never" class="herb-detail__card">
          <template #header>
            <span class="card-title">规格等级</span>
          </template>
          <p class="herb-detail__text">{{ herb.specDesc || '按市场统货、选货等规格分级' }}</p>
        </el-card>

        <el-card shadow="never" class="herb-detail__card">
          <template #header>
            <span class="card-title">当前价格</span>
          </template>
          <div v-if="herb.currentPrice" class="herb-detail__price">
            <span class="price-value">¥{{ herb.currentPrice }}</span>
            <el-link type="primary" :underline="false" @click="goPriceDetail">
              查看价格详情 →
            </el-link>
          </div>
          <el-empty v-else description="暂无价格数据" :image-size="60" />
        </el-card>

        <el-card shadow="never" class="herb-detail__card">
          <template #header>
            <span class="card-title">关联资讯</span>
          </template>
          <el-empty description="暂无关联资讯" :image-size="60" />
        </el-card>
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
  padding: 20px;
  max-width: 960px;
  margin: 0 auto;

  &__content {
    min-height: 400px;
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: #fff;
    border-radius: 4px;
  }

  &__title {
    h1 {
      font-size: 24px;
      font-weight: 700;
      color: $text-color;
      margin: 0;
    }
  }

  &__alias {
    font-size: 14px;
    color: #999;
    margin-left: 12px;
  }

  &__card {
    margin-bottom: 16px;
  }

  &__text {
    font-size: 14px;
    color: $text-secondary;
    line-height: 1.8;
    margin: 0;
  }

  &__price {
    display: flex;
    align-items: center;
    gap: 16px;

    .price-value {
      font-size: 24px;
      font-weight: 700;
      color: $primary-color;
    }
  }
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: $text-color;
}
</style>
