<template>
  <div class="price-alerts">
    <div class="price-alerts__header">
      <h2>价格预警</h2>
      <el-button type="primary" @click="showCreateDialog">创建预警</el-button>
    </div>

    <div v-if="!userStore.token" class="price-alerts__auth">
      <el-empty description="请先登录后查看预警列表">
        <el-button type="primary" @click="goLogin">去登录</el-button>
      </el-empty>
    </div>

    <template v-else>
      <el-alert
        v-if="!isMember && alertList.length >= 3"
        title="免费用户最多创建3条预警，升级会员可无限制创建"
        type="warning"
        show-icon
        :closable="false"
        class="price-alerts__limit-tip"
      />

      <el-table
        v-loading="loading"
        :data="alertList"
        stripe
        class="price-alerts__table"
      >
        <el-table-column label="品种名" min-width="120">
          <template #default="{ row }">
            <el-link type="primary" :underline="false" @click="goDetail(row.herbId)">
              {{ row.herbName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="条件" min-width="120">
          <template #default="{ row }">
            {{ row.conditionTypeName }}
          </template>
        </el-table-column>
        <el-table-column label="阈值" min-width="100" align="right">
          <template #default="{ row }">
            <span v-if="row.conditionType <= 2">¥{{ row.threshold }}</span>
            <span v-else>{{ row.threshold }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="当前价格" min-width="100" align="right">
          <template #default="{ row }">
            <span v-if="row.currentPrice != null">¥{{ row.currentPrice }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'" size="small">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上次触发" min-width="160" align="center">
          <template #default="{ row }">
            <span v-if="row.lastTriggered">{{ formatTime(row.lastTriggered) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button
              size="small"
              link
              :type="row.isActive ? 'warning' : 'success'"
              @click="handleToggle(row)"
            >
              {{ row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm
              title="确定删除此预警？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button size="small" link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && alertList.length === 0" description="暂无预警规则" />
    </template>

    <el-dialog
      v-model="createDialogVisible"
      title="创建价格预警"
      width="480px"
      :close-on-click-modal="false"
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

  &__limit-tip {
    margin-bottom: 16px;
  }

  &__table {
    margin-bottom: 16px;
  }
}
</style>
