<template>
  <div class="price-alerts">
    <div class="price-alerts__header">
      <h2 class="price-alerts__title">价格预警</h2>
      <button class="price-alerts__create-btn" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        <span>创建预警</span>
      </button>
    </div>

    <div v-if="!userStore.token" class="price-alerts__auth">
      <div class="price-alerts__empty-state">
        <div class="price-alerts__empty-icon">
          <el-icon :size="48"><Lock /></el-icon>
        </div>
        <div class="price-alerts__empty-text">请先登录后查看预警列表</div>
        <button class="price-alerts__empty-btn" @click="goLogin">去登录</button>
      </div>
    </div>

    <template v-else>
      <div v-if="!isMember && alertList.length >= 3" class="price-alerts__limit-tip">
        <el-icon><WarningFilled /></el-icon>
        <span>免费用户最多创建3条预警，升级会员可无限制创建</span>
      </div>

      <div v-loading="loading" class="price-alerts__grid">
        <div v-for="row in alertList" :key="row.id" class="price-alerts__card">
          <div class="price-alerts__card-header">
            <span class="price-alerts__card-name" @click="goDetail(row.herbId)">{{ row.herbName }}</span>
            <span class="price-alerts__card-status" :class="row.isActive ? 'price-alerts__card-status--active' : 'price-alerts__card-status--inactive'">
              {{ row.isActive ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="price-alerts__card-body">
            <div class="price-alerts__card-condition">
              <div class="price-alerts__card-condition-type">
                <el-icon><Bell /></el-icon>
                <span>{{ row.conditionTypeName }}</span>
              </div>
              <div class="price-alerts__card-condition-value">
                <span v-if="row.conditionType <= 2">¥{{ row.threshold }}</span>
                <span v-else>{{ row.threshold }}%</span>
              </div>
            </div>
            <div class="price-alerts__card-info">
              <div class="price-alerts__card-info-item">
                <span class="price-alerts__card-info-label">当前价格</span>
                <span v-if="row.currentPrice != null" class="price-alerts__card-info-value">¥{{ row.currentPrice }}</span>
                <span v-else class="price-alerts__card-info-value price-alerts__card-info-value--na">-</span>
              </div>
              <div class="price-alerts__card-info-item">
                <span class="price-alerts__card-info-label">上次触发</span>
                <span v-if="row.lastTriggered" class="price-alerts__card-info-value">{{ formatTime(row.lastTriggered) }}</span>
                <span v-else class="price-alerts__card-info-value price-alerts__card-info-value--na">-</span>
              </div>
            </div>
          </div>
          <div class="price-alerts__card-actions">
            <button
              class="price-alerts__card-action"
              :class="row.isActive ? 'price-alerts__card-action--warn' : 'price-alerts__card-action--success'"
              @click="handleToggle(row)"
            >
              {{ row.isActive ? '禁用' : '启用' }}
            </button>
            <el-popconfirm
              title="确定删除此预警？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <button class="price-alerts__card-action price-alerts__card-action--danger">删除</button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>

      <div v-if="!loading && alertList.length === 0" class="price-alerts__empty-state">
        <div class="price-alerts__empty-icon">
          <el-icon :size="48"><Bell /></el-icon>
        </div>
        <div class="price-alerts__empty-text">暂无预警规则</div>
        <div class="price-alerts__empty-hint">创建价格预警，及时掌握药材价格变动</div>
        <button class="price-alerts__empty-btn" @click="showCreateDialog">创建预警</button>
      </div>
    </template>

    <el-dialog
      v-model="createDialogVisible"
      title="创建价格预警"
      width="480px"
      :close-on-click-modal="false"
      class="price-alerts__dialog"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="80px"
      >
        <el-form-item label="品种" prop="herbId">
          <el-select
            v-model="createForm.herbId"
            filterable
            remote
            reserve-keyword
            placeholder="搜索品种"
            :remote-method="searchHerb"
            :loading="herbSearching"
            style="width: 100%"
          >
            <el-option
              v-for="item in herbOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="条件类型" prop="conditionType">
          <el-select v-model="createForm.conditionType" placeholder="选择条件" style="width: 100%">
            <el-option label="价格高于" :value="1" />
            <el-option label="价格低于" :value="2" />
            <el-option label="日涨幅超过" :value="3" />
            <el-option label="日跌幅超过" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="阈值" prop="threshold">
          <el-input-number
            v-model="createForm.threshold"
            :min="0.01"
            :precision="2"
            :step="1"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'
import { searchHerb } from '@/api/herb'

const router = useRouter()
const userStore = useUserStore()

interface AlertItem {
  id: number | string
  herbId: number | string
  herbName: string
  conditionType: number
  conditionTypeName: string
  threshold: number
  currentPrice: number | null
  isActive: boolean
  lastTriggered: string | null
  createdAt: string
}

interface HerbOption {
  id: number | string
  name: string
}

const loading = ref(false)
const alertList = ref<AlertItem[]>([])
const isMember = ref(false)

const createDialogVisible = ref(false)
const createLoading = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = ref({
  herbId: null as number | string | null,
  conditionType: null as number | null,
  threshold: null as number | null,
})
const createRules: FormRules = {
  herbId: [{ required: true, message: '请选择品种', trigger: 'change' }],
  conditionType: [{ required: true, message: '请选择条件类型', trigger: 'change' }],
  threshold: [{ required: true, message: '请输入阈值', trigger: 'blur' }],
}

const herbSearching = ref(false)
const herbOptions = ref<HerbOption[]>([])

async function fetchAlerts() {
  if (!userStore.token) return
  loading.value = true
  try {
    const res = (await request.get('/user/alerts')) as any
    alertList.value = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
  } catch {
    alertList.value = []
  } finally {
    loading.value = false
  }
}

async function fetchMembership() {
  if (!userStore.token) return
  try {
    const res = (await request.get('/membership/current')) as any
    isMember.value = res?.level != null && res.level > 0
  } catch {
    isMember.value = false
  }
}

function showCreateDialog() {
  createForm.value = { herbId: null, conditionType: null, threshold: null }
  herbOptions.value = []
  createDialogVisible.value = true
}

async function handleHerbSearch(query: string) {
  if (!query) {
    herbOptions.value = []
    return
  }
  herbSearching.value = true
  try {
    const res = (await searchHerb(query)) as any
    const list = res?.list || res?.data || res?.records || (Array.isArray(res) ? res : [])
    herbOptions.value = list.map((item: any) => ({ id: item.id, name: item.name }))
  } catch {
    herbOptions.value = []
  } finally {
    herbSearching.value = false
  }
}

async function handleCreate() {
  if (!createFormRef.value) return
  await createFormRef.value.validate()
  createLoading.value = true
  try {
    await request.post('/user/alerts', {
      herbId: createForm.value.herbId,
      conditionType: createForm.value.conditionType,
      threshold: createForm.value.threshold,
    })
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    fetchAlerts()
  } catch {
    // error handled by interceptor
  } finally {
    createLoading.value = false
  }
}

async function handleToggle(row: AlertItem) {
  try {
    await request.put(`/user/alerts/${row.id}/toggle`, { active: !row.isActive })
    ElMessage.success(row.isActive ? '已禁用' : '已启用')
    fetchAlerts()
  } catch {
    // error handled by interceptor
  }
}

async function handleDelete(row: AlertItem) {
  try {
    await request.delete(`/user/alerts/${row.id}`)
    ElMessage.success('删除成功')
    alertList.value = alertList.value.filter((a) => a.id !== row.id)
  } catch {
    // error handled by interceptor
  }
}

function formatTime(time: string | null): string {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

function goDetail(herbId: number | string) {
  router.push(`/herb/detail/${herbId}`)
}

function goLogin() {
  router.push('/login')
}

onMounted(() => {
  fetchAlerts()
  fetchMembership()
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.price-alerts {
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

  &__create-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 20px;
    border-radius: 22px;
    border: 1.5px solid $accent-color;
    background: $accent-lighter;
    color: $accent-color;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: $accent-color;
      color: #fff;
      box-shadow: 0 2px 8px rgba($accent-color, 0.3);
    }
  }

  &__auth {
    padding: 60px 0;
  }

  &__limit-tip {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: $accent-lighter;
    border-radius: $radius-sm;
    border-left: 3px solid $accent-color;
    font-size: 13px;
    color: $accent-color;
    margin-bottom: 20px;
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
    cursor: pointer;
    transition: color 0.2s;

    &:hover {
      color: $primary-light;
    }
  }

  &__card-status {
    padding: 3px 10px;
    border-radius: 10px;
    font-size: 12px;
    font-weight: 500;

    &--active {
      background: $primary-lighter;
      color: $primary-color;
    }

    &--inactive {
      background: $bg-warm;
      color: $text-muted;
    }
  }

  &__card-body {
    padding: 16px 18px;
  }

  &__card-condition {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    background: $accent-lighter;
    border-radius: $radius-sm;
    border-left: 3px solid $accent-color;
    margin-bottom: 12px;
  }

  &__card-condition-type {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: $text-color;
    font-weight: 500;
  }

  &__card-condition-value {
    font-family: $font-display;
    font-size: 18px;
    font-weight: 700;
    color: $accent-color;
  }

  &__card-info {
    display: flex;
    gap: 24px;
  }

  &__card-info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__card-info-label {
    font-size: 12px;
    color: $text-muted;
  }

  &__card-info-value {
    font-size: 14px;
    color: $text-color;
    font-weight: 500;

    &--na {
      color: $text-muted;
      font-weight: 400;
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

    &--warn {
      background: $accent-lighter;
      color: $accent-color;

      &:hover {
        background: $accent-color;
        color: #fff;
      }
    }

    &--success {
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
    background: $accent-lighter;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $accent-color;
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
    background: $accent-color;
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: darken(#c8953e, 5%);
      box-shadow: 0 2px 8px rgba($accent-color, 0.3);
    }
  }
}

@media (max-width: 768px) {
  .price-alerts {
    &__grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
