<template>
  <div class="membership">
    <div class="membership__header">
      <h2>会员中心</h2>
    </div>

    <div v-if="!userStore.token" class="membership__auth">
      <el-empty description="请先登录后查看会员信息">
        <el-button type="primary" @click="goLogin">去登录</el-button>
      </el-empty>
    </div>

    <template v-else>
      <div v-if="currentMembership" class="membership__status">
        <el-card shadow="never">
          <div class="membership__status-content">
            <div class="membership__status-info">
              <span class="membership__status-label">当前会员</span>
              <el-tag
                :type="currentMembership.level > 0 ? 'warning' : 'info'"
                size="large"
              >
                {{ currentMembership.name }}
              </el-tag>
            </div>
            <div v-if="currentMembership.level > 0" class="membership__status-features">
              <span v-for="feature in currentMembership.features" :key="feature" class="membership__feature-tag">
                {{ feature }}
              </span>
            </div>
          </div>
        </el-card>
      </div>

      <h3 class="membership__section-title">套餐选择</h3>
      <div class="membership__plans">
        <el-card
          v-for="plan in plans"
          :key="plan.level"
          shadow="hover"
          class="membership__plan-card"
          :class="{ 'membership__plan-card--active': currentMembership?.level === plan.level }"
        >
          <div class="membership__plan-name">{{ plan.name }}</div>
          <div class="membership__plan-price">
            <span class="membership__plan-currency">¥</span>
            <span class="membership__plan-amount">{{ plan.price }}</span>
          </div>
          <div class="membership__plan-duration">{{ plan.durationDays }}天</div>
          <el-divider />
          <div class="membership__plan-features">
            <div v-for="feature in plan.features" :key="feature" class="membership__plan-feature">
              <el-icon><Check /></el-icon>
              <span>{{ feature }}</span>
            </div>
          </div>
          <el-button
            v-if="currentMembership?.level !== plan.level"
            type="primary"
            class="membership__plan-btn"
            @click="handleUpgrade(plan.level)"
          >
            立即开通
          </el-button>
          <el-button
            v-else
            type="info"
            class="membership__plan-btn"
            disabled
          >
            当前套餐
          </el-button>
        </el-card>
      </div>

      <h3 class="membership__section-title">权益对比</h3>
      <el-table :data="comparisonData" stripe class="membership__comparison">
        <el-table-column prop="feature" label="权益" min-width="160" />
        <el-table-column label="免费" align="center" min-width="100">
          <template #default="{ row }">
            <el-icon v-if="row.free === true" class="membership__check"><Check /></el-icon>
            <span v-else-if="row.free === false" class="membership__cross">-</span>
            <span v-else>{{ row.free }}</span>
          </template>
        </el-table-column>
        <el-table-column label="月度" align="center" min-width="100">
          <template #default="{ row }">
            <el-icon v-if="row.monthly === true" class="membership__check"><Check /></el-icon>
            <span v-else-if="row.monthly === false" class="membership__cross">-</span>
            <span v-else>{{ row.monthly }}</span>
          </template>
        </el-table-column>
        <el-table-column label="季度" align="center" min-width="100">
          <template #default="{ row }">
            <el-icon v-if="row.quarterly === true" class="membership__check"><Check /></el-icon>
            <span v-else-if="row.quarterly === false" class="membership__cross">-</span>
            <span v-else>{{ row.quarterly }}</span>
          </template>
        </el-table-column>
        <el-table-column label="年度" align="center" min-width="100">
          <template #default="{ row }">
            <el-icon v-if="row.yearly === true" class="membership__check"><Check /></el-icon>
            <span v-else-if="row.yearly === false" class="membership__cross">-</span>
            <span v-else>{{ row.yearly }}</span>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

interface Plan {
  level: number
  name: string
  price: number
  durationDays: number
  features: string[]
}

const plans = ref<Plan[]>([])
const currentMembership = ref<Plan | null>(null)

const comparisonData = ref([
  { feature: '价格预警', free: '3条', monthly: true, quarterly: true, yearly: true },
  { feature: '基础行情查看', free: true, monthly: true, quarterly: true, yearly: true },
  { feature: '供求信息发布', free: true, monthly: true, quarterly: true, yearly: true },
  { feature: '历史价格数据导出', free: false, monthly: true, quarterly: true, yearly: true },
  { feature: '市场行情深度分析', free: false, monthly: true, quarterly: true, yearly: true },
  { feature: '专属客服支持', free: false, monthly: true, quarterly: true, yearly: true },
  { feature: '产地直供对接', free: false, monthly: false, quarterly: true, yearly: true },
  { feature: '价格走势预测', free: false, monthly: false, quarterly: true, yearly: true },
  { feature: '行业报告订阅', free: false, monthly: false, quarterly: false, yearly: true },
  { feature: 'API接口调用', free: false, monthly: false, quarterly: false, yearly: true },
])

async function fetchPlans() {
  try {
    const res = (await request.get('/membership/plans')) as any
    plans.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
  } catch {
    plans.value = []
  }
}

async function fetchCurrentMembership() {
  if (!userStore.token) return
  try {
    const res = (await request.get('/membership/current')) as any
    currentMembership.value = res
  } catch {
    currentMembership.value = null
  }
}

async function handleUpgrade(level: number) {
  const planName = plans.value.find((p) => p.level === level)?.name || ''
  try {
    await ElMessageBox.confirm(
      `确定要开通${planName}吗？(MVP阶段直接生效)`,
      '确认开通',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await request.post('/membership/upgrade', { level })
    ElMessage.success('开通成功')
    fetchCurrentMembership()
  } catch {
    // cancelled or error
  }
}

function goLogin() {
  router.push('/login')
}

onMounted(() => {
  fetchPlans()
  fetchCurrentMembership()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.membership {
  padding: 20px;
  max-width: 1100px;
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

  &__status {
    margin-bottom: 24px;
  }

  &__status-content {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  &__status-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__status-label {
    font-size: 14px;
    color: $text-secondary;
  }

  &__status-features {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  &__feature-tag {
    font-size: 12px;
    color: $text-secondary;
    background: #f5f5f5;
    padding: 2px 8px;
    border-radius: 4px;
  }

  &__section-title {
    font-size: 16px;
    color: $text-color;
    margin: 24px 0 16px;
  }

  &__plans {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  &__plan-card {
    text-align: center;

    &--active {
      border-color: $primary-color;
    }
  }

  &__plan-name {
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
    margin-bottom: 12px;
  }

  &__plan-price {
    margin-bottom: 4px;
  }

  &__plan-currency {
    font-size: 16px;
    color: $primary-color;
    vertical-align: top;
    line-height: 2;
  }

  &__plan-amount {
    font-size: 36px;
    font-weight: 700;
    color: $primary-color;
  }

  &__plan-duration {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 4px;
  }

  &__plan-features {
    text-align: left;
    margin-bottom: 16px;
  }

  &__plan-feature {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: $text-secondary;
    padding: 4px 0;
  }

  &__plan-btn {
    width: 100%;
  }

  &__comparison {
    margin-bottom: 24px;
  }

  &__check {
    color: $danger-color;
    font-size: 16px;
  }

  &__cross {
    color: #ccc;
  }
}

@media (max-width: 768px) {
  .membership__plans {
    grid-template-columns: 1fr;
  }
}
</style>
