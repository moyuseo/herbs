<template>
  <div class="membership">
    <div class="membership__page-title">
      <h1>会员中心</h1>
    </div>

    <div v-if="!userStore.token" class="membership__auth">
      <div class="membership__auth-card">
        <div class="membership__auth-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <circle cx="24" cy="24" r="22" stroke="#c8953e" stroke-width="2" fill="#fdf6e8"/>
            <path d="M24 14v10l6 4" stroke="#c8953e" stroke-width="2.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h3 class="membership__auth-title">解锁完整会员权益</h3>
        <p class="membership__auth-desc">登录后即可查看会员信息、选择套餐</p>
        <div class="membership__auth-plans">
          <div v-for="plan in plans" :key="plan.level" class="membership__auth-plan-preview">
            <span class="membership__auth-plan-name">{{ plan.name }}</span>
            <span class="membership__auth-plan-price">¥{{ plan.price }}</span>
          </div>
        </div>
        <el-button type="primary" size="large" @click="goLogin" class="membership__auth-btn">
          立即登录
        </el-button>
      </div>
    </div>

    <template v-else>
      <div v-if="currentMembership" class="membership__status">
        <div class="membership__status-card">
          <div class="membership__status-info">
            <span class="membership__status-label">当前会员</span>
            <span
              class="membership__status-badge"
              :class="currentMembership.level > 0 ? 'membership__status-badge--vip' : ''"
            >
              {{ currentMembership.name }}
            </span>
          </div>
          <div v-if="currentMembership.level > 0" class="membership__status-features">
            <span v-for="feature in currentMembership.features" :key="feature" class="membership__feature-tag">
              {{ feature }}
            </span>
          </div>
        </div>
      </div>

      <h3 class="membership__section-title">套餐选择</h3>
      <div class="membership__plans">
        <div
          v-for="plan in plans"
          :key="plan.level"
          class="membership__plan-card"
          :class="{
            'membership__plan-card--active': currentMembership?.level === plan.level,
            'membership__plan-card--premium': plan.level > 1,
          }"
        >
          <div v-if="plan.level > 1" class="membership__plan-badge">推荐</div>
          <div class="membership__plan-name">{{ plan.name }}</div>
          <div class="membership__plan-price">
            <span class="membership__plan-currency">¥</span>
            <span class="membership__plan-amount">{{ plan.price }}</span>
          </div>
          <div class="membership__plan-duration">{{ plan.durationDays }}天</div>
          <div class="membership__plan-divider"></div>
          <div class="membership__plan-features">
            <div v-for="feature in plan.features" :key="feature" class="membership__plan-feature">
              <el-icon><Check /></el-icon>
              <span>{{ feature }}</span>
            </div>
          </div>
          <el-button
            v-if="currentMembership?.level !== plan.level"
            :type="plan.level > 1 ? 'primary' : 'default'"
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
        </div>
      </div>

      <h3 class="membership__section-title">权益对比</h3>
      <div class="membership__comparison-wrap">
        <table class="membership__comparison">
          <thead>
            <tr>
              <th class="membership__comparison-feature">权益</th>
              <th>免费</th>
              <th>月度</th>
              <th>季度</th>
              <th class="membership__comparison-highlight">年度</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in comparisonData" :key="row.feature">
              <td class="membership__comparison-feature">{{ row.feature }}</td>
              <td>
                <el-icon v-if="row.free === true" class="membership__check"><Check /></el-icon>
                <span v-else-if="row.free === false" class="membership__cross">—</span>
                <span v-else>{{ row.free }}</span>
              </td>
              <td>
                <el-icon v-if="row.monthly === true" class="membership__check"><Check /></el-icon>
                <span v-else-if="row.monthly === false" class="membership__cross">—</span>
                <span v-else>{{ row.monthly }}</span>
              </td>
              <td>
                <el-icon v-if="row.quarterly === true" class="membership__check"><Check /></el-icon>
                <span v-else-if="row.quarterly === false" class="membership__cross">—</span>
                <span v-else>{{ row.quarterly }}</span>
              </td>
              <td class="membership__comparison-highlight">
                <el-icon v-if="row.yearly === true" class="membership__check"><Check /></el-icon>
                <span v-else-if="row.yearly === false" class="membership__cross">—</span>
                <span v-else>{{ row.yearly }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
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
  max-width: $container-max;
  margin: 0 auto;
  background: $bg-warm;
  min-height: calc(100vh - #{$header-height});

  &__page-title {
    margin-bottom: 24px;

    h1 {
      margin: 0;
      font-family: $font-display;
      font-size: 26px;
      font-weight: 700;
      color: $text-color;
      position: relative;
      padding-left: 16px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 4px;
        bottom: 4px;
        width: 4px;
        background: $primary-color;
        border-radius: 2px;
      }
    }
  }

  &__auth {
    display: flex;
    justify-content: center;
    padding: 40px 0;
  }

  &__auth-card {
    background: $card-bg;
    border-radius: $radius-lg;
    padding: 48px 40px;
    text-align: center;
    box-shadow: $shadow-md;
    max-width: 480px;
    width: 100%;
  }

  &__auth-icon {
    margin-bottom: 20px;
  }

  &__auth-title {
    margin: 0 0 8px;
    font-family: $font-display;
    font-size: 22px;
    font-weight: 700;
    color: $text-color;
  }

  &__auth-desc {
    margin: 0 0 24px;
    font-size: 14px;
    color: $text-muted;
  }

  &__auth-plans {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-bottom: 28px;
  }

  &__auth-plan-preview {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px 20px;
    background: $bg-warm;
    border-radius: $radius-sm;
    border: 1px solid $border-color;
  }

  &__auth-plan-name {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 4px;
  }

  &__auth-plan-price {
    font-size: 18px;
    font-weight: 700;
    color: $accent-color;
  }

  &__auth-btn {
    min-width: 200px;
  }

  &__status {
    margin-bottom: 28px;
  }

  &__status-card {
    background: $card-bg;
    border-radius: $radius-md;
    padding: 20px 24px;
    box-shadow: $shadow-sm;
    border-left: 4px solid $accent-color;
  }

  &__status-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
  }

  &__status-label {
    font-size: 14px;
    color: $text-secondary;
  }

  &__status-badge {
    display: inline-block;
    padding: 4px 14px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
    background: $bg-warm;
    color: $text-secondary;

    &--vip {
      background: $accent-lighter;
      color: $accent-color;
      border: 1px solid $accent-light;
    }
  }

  &__status-features {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  &__feature-tag {
    font-size: 12px;
    color: $primary-color;
    background: $primary-lighter;
    padding: 3px 10px;
    border-radius: 20px;
  }

  &__section-title {
    font-family: $font-display;
    font-size: 20px;
    font-weight: 600;
    color: $text-color;
    margin: 28px 0 18px;
    position: relative;
    padding-left: 14px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 3px;
      bottom: 3px;
      width: 4px;
      background: $primary-color;
      border-radius: 2px;
    }
  }

  &__plans {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  &__plan-card {
    background: $card-bg;
    border-radius: $radius-md;
    padding: 28px 24px;
    text-align: center;
    box-shadow: $shadow-sm;
    border: 2px solid transparent;
    transition: all 0.3s;
    position: relative;
    overflow: hidden;

    &:hover {
      box-shadow: $shadow-md;
      transform: translateY(-2px);
    }

    &--active {
      border-color: $primary-color;
    }

    &--premium {
      border-color: $accent-color;
      background: linear-gradient(180deg, $accent-lighter 0%, $card-bg 30%);
    }
  }

  &__plan-badge {
    position: absolute;
    top: 12px;
    right: -28px;
    background: $accent-color;
    color: #fff;
    font-size: 12px;
    font-weight: 600;
    padding: 2px 32px;
    transform: rotate(45deg);
  }

  &__plan-name {
    font-family: $font-display;
    font-size: 20px;
    font-weight: 600;
    color: $text-color;
    margin-bottom: 16px;
  }

  &__plan-price {
    margin-bottom: 4px;
  }

  &__plan-currency {
    font-size: 16px;
    color: $accent-color;
    vertical-align: top;
    line-height: 2;
  }

  &__plan-amount {
    font-size: 40px;
    font-weight: 700;
    color: $accent-color;
  }

  &__plan-duration {
    font-size: 13px;
    color: $text-muted;
    margin-bottom: 8px;
  }

  &__plan-divider {
    height: 1px;
    background: $border-light;
    margin: 16px 0;
  }

  &__plan-features {
    text-align: left;
    margin-bottom: 20px;
  }

  &__plan-feature {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: $text-secondary;
    padding: 5px 0;
  }

  &__plan-btn {
    width: 100%;
  }

  &__comparison-wrap {
    background: $card-bg;
    border-radius: $radius-md;
    overflow: hidden;
    box-shadow: $shadow-sm;
  }

  &__comparison {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;

    th {
      background: $primary-dark;
      color: #fff;
      font-weight: 500;
      padding: 12px 16px;
      text-align: center;
      white-space: nowrap;
    }

    td {
      padding: 12px 16px;
      border-bottom: 1px solid $border-light;
      text-align: center;
      color: $text-color;
    }

    &-feature {
      text-align: left !important;
      font-weight: 500;
      background: $bg-warm;
    }

    &-highlight {
      background: $accent-lighter;
    }

    tbody tr {
      transition: background 0.2s;

      &:hover {
        background: $primary-lighter;
      }
    }
  }

  &__check {
    color: $primary-color;
    font-size: 16px;
  }

  &__cross {
    color: $border-color;
  }
}

@media (max-width: 768px) {
  .membership__plans {
    grid-template-columns: 1fr;
  }

  .membership__auth-plans {
    flex-direction: column;
    align-items: center;
  }
}
</style>
