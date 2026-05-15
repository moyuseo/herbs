<template>
  <div class="publish-page">
    <div class="publish-page__page-title">
      <h2>发布信息</h2>
    </div>

    <div class="publish-page__tabs">
      <button
        class="publish-page__tab"
        :class="{ 'publish-page__tab--active': activeTab === 'supply' }"
        @click="activeTab = 'supply'"
      >
        发布供应
      </button>
      <button
        class="publish-page__tab"
        :class="{ 'publish-page__tab--active': activeTab === 'demand' }"
        @click="activeTab = 'demand'"
      >
        发布求购
      </button>
    </div>

    <div class="publish-page__form-wrapper">
      <el-form
        v-if="activeTab === 'supply'"
        ref="supplyFormRef"
        :model="supplyForm"
        :rules="supplyRules"
        label-width="100px"
        class="publish-form"
      >
        <el-form-item label="品种" prop="herbName">
          <el-select
            v-model="supplyForm.herbName"
            filterable
            allow-create
            remote
            :remote-method="searchHerbs"
            :loading="herbSearching"
            placeholder="搜索或输入品种名称"
          >
            <el-option
              v-for="item in herbOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="supplyForm.spec" placeholder="如：统货、选货、一级" />
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-input v-model="supplyForm.origin" placeholder="请输入产地" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <div class="quantity-row">
            <el-input-number
              v-model="supplyForm.quantity"
              :min="1"
              :precision="2"
              controls-position="right"
              class="quantity-input"
            />
            <el-select v-model="supplyForm.unit" class="unit-select">
              <el-option label="公斤" value="公斤" />
              <el-option label="吨" value="吨" />
              <el-option label="克" value="克" />
              <el-option label="斤" value="斤" />
            </el-select>
          </div>
        </el-form-item>
        <el-form-item label="价格类型" prop="priceType">
          <el-radio-group v-model="supplyForm.priceType">
            <el-radio value="明码">明码</el-radio>
            <el-radio value="电议">电议</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="supplyForm.priceType === '明码'" label="价格" prop="price">
          <el-input-number
            v-model="supplyForm.price"
            :min="0"
            :precision="2"
            controls-position="right"
            placeholder="请输入单价"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="supplyForm.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="supplyForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="#"
            :auto-upload="false"
            list-type="picture-card"
            :limit="5"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="supplyForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入供应描述信息"
          />
        </el-form-item>
        <el-form-item>
          <button type="button" class="publish-page__submit-btn" :disabled="submitting" @click="handleSupplySubmit">
            {{ submitting ? '提交中...' : '提交发布' }}
          </button>
          <button type="button" class="publish-page__reset-btn" @click="resetSupplyForm">重置</button>
        </el-form-item>
      </el-form>

      <el-form
        v-if="activeTab === 'demand'"
        ref="demandFormRef"
        :model="demandForm"
        :rules="demandRules"
        label-width="100px"
        class="publish-form"
      >
        <el-form-item label="品种" prop="herbName">
          <el-select
            v-model="demandForm.herbName"
            filterable
            allow-create
            remote
            :remote-method="searchHerbs"
            :loading="herbSearching"
            placeholder="搜索或输入品种名称"
          >
            <el-option
              v-for="item in herbOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="demandForm.spec" placeholder="如：统货、选货、一级" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <div class="quantity-row">
            <el-input-number
              v-model="demandForm.quantity"
              :min="1"
              :precision="2"
              controls-position="right"
              class="quantity-input"
            />
            <el-select v-model="demandForm.unit" class="unit-select">
              <el-option label="公斤" value="公斤" />
              <el-option label="吨" value="吨" />
              <el-option label="克" value="克" />
              <el-option label="斤" value="斤" />
            </el-select>
          </div>
        </el-form-item>
        <el-form-item label="交货地址" prop="deliveryAddress">
          <el-input v-model="demandForm.deliveryAddress" placeholder="请输入交货地址" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="demandForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入求购描述信息"
          />
        </el-form-item>
        <el-form-item>
          <button type="button" class="publish-page__submit-btn" :disabled="submitting" @click="handleDemandSubmit">
            {{ submitting ? '提交中...' : '提交发布' }}
          </button>
          <button type="button" class="publish-page__reset-btn" @click="resetDemandForm">重置</button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { publishSupply, publishDemand } from '@/api/supply'

const router = useRouter()

const activeTab = ref('supply')
const submitting = ref(false)
const herbSearching = ref(false)
const herbOptions = ref<string[]>([])

const supplyFormRef = ref<FormInstance>()
const demandFormRef = ref<FormInstance>()

const supplyForm = reactive({
  herbName: '',
  spec: '',
  origin: '',
  quantity: 1,
  unit: '公斤',
  priceType: '明码',
  price: 0,
  contactName: '',
  contactPhone: '',
  description: '',
})

const demandForm = reactive({
  herbName: '',
  spec: '',
  quantity: 1,
  unit: '公斤',
  deliveryAddress: '',
  description: '',
})

const supplyRules: FormRules = {
  herbName: [{ required: true, message: '请选择或输入品种', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
}

const demandRules: FormRules = {
  herbName: [{ required: true, message: '请选择或输入品种', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
}

function searchHerbs(query: string) {
  if (!query) {
    herbOptions.value = []
    return
  }
  herbSearching.value = true
  setTimeout(() => {
    herbOptions.value = [query, `${query}-统货`, `${query}-选货`, `${query}-一级`]
    herbSearching.value = false
  }, 300)
}

async function handleSupplySubmit() {
  const valid = await supplyFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await publishSupply({
      herbName: supplyForm.herbName,
      spec: supplyForm.spec,
      origin: supplyForm.origin,
      quantity: supplyForm.quantity,
      unit: supplyForm.unit,
      priceType: supplyForm.priceType === '明码' ? 1 : 2,
      price: supplyForm.priceType === '明码' ? supplyForm.price : null,
      contactName: supplyForm.contactName,
      contactPhone: supplyForm.contactPhone,
      description: supplyForm.description,
    })
    ElMessage.success('供应信息发布成功')
    router.push('/supply/list')
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

async function handleDemandSubmit() {
  const valid = await demandFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await publishDemand({
      herbName: demandForm.herbName,
      spec: demandForm.spec,
      quantity: demandForm.quantity,
      unit: demandForm.unit,
      deliveryAddress: demandForm.deliveryAddress,
      description: demandForm.description,
    })
    ElMessage.success('求购信息发布成功')
    router.push('/demand/list')
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

function resetSupplyForm() {
  supplyFormRef.value?.resetFields()
}

function resetDemandForm() {
  demandFormRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.publish-page {
  padding: 24px 16px;
  max-width: 820px;
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

  &__form-wrapper {
    background: $card-bg;
    border-radius: $radius-md;
    box-shadow: $shadow-sm;
    border: 1px solid $border-light;
    padding: 32px 36px;

    :deep(.el-form-item__label) {
      color: $text-color;
      font-weight: 500;
    }

    :deep(.el-input__wrapper),
    :deep(.el-textarea__inner),
    :deep(.el-input-number) {
      background: $bg-warm;
      border-color: $border-color;
    }

    :deep(.el-input__wrapper:hover),
    :deep(.el-textarea__inner:hover) {
      box-shadow: 0 0 0 1px $primary-light inset;
    }

    :deep(.el-input__wrapper.is-focus),
    :deep(.el-textarea__inner:focus) {
      box-shadow: 0 0 0 1px $primary-color inset;
    }

    :deep(.el-select) {
      width: 100%;
    }

    :deep(.el-radio__input.is-checked .el-radio__inner) {
      background: $primary-color;
      border-color: $primary-color;
    }

    :deep(.el-radio__input.is-checked + .el-radio__label) {
      color: $primary-color;
    }

    :deep(.el-upload--picture-card) {
      background: $bg-warm;
      border-color: $border-color;

      &:hover {
        border-color: $primary-light;
      }
    }
  }

  &__submit-btn {
    padding: 10px 36px;
    border-radius: 22px;
    border: none;
    background: linear-gradient(135deg, $accent-color, darken(#c8953e, 8%));
    color: #fff;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.25s ease;
    box-shadow: 0 2px 8px rgba($accent-color, 0.3);

    &:hover:not(:disabled) {
      box-shadow: 0 4px 16px rgba($accent-color, 0.4);
      transform: translateY(-1px);
    }

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }

  &__reset-btn {
    padding: 10px 28px;
    border-radius: 22px;
    border: 1px solid $border-color;
    background: $card-bg;
    color: $text-secondary;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
    margin-left: 12px;

    &:hover {
      border-color: $primary-light;
      color: $primary-color;
    }
  }
}

.publish-form {
  max-width: 600px;
}

.quantity-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.quantity-input {
  flex: 1;
}

.unit-select {
  width: 100px;
}

@media (max-width: 768px) {
  .publish-page {
    &__tabs {
      width: 100%;
    }

    &__tab {
      flex: 1;
      text-align: center;
    }

    &__form-wrapper {
      padding: 20px 16px;
    }
  }
}
</style>
