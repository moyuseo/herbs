<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-card__header">
        <div class="login-card__logo">
          <svg width="36" height="36" viewBox="0 0 36 36" fill="none">
            <circle cx="18" cy="18" r="16" fill="#c8953e" opacity="0.2"/>
            <path d="M18 8c-2 4-6 6-6 10a6 6 0 0012 0c0-4-4-6-6-10z" fill="#c8953e"/>
          </svg>
        </div>
        <h2 class="login-card__title">登录</h2>
        <p class="login-card__subtitle">欢迎回到中药材行情平台</p>
      </div>

      <div class="login-card__body">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleLogin"
          class="login-form"
        >
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
              maxlength="11"
              size="large"
            />
          </el-form-item>

          <el-form-item label="验证码" prop="code">
            <div class="code-row">
              <el-input
                v-model="form.code"
                placeholder="请输入验证码"
                maxlength="6"
                size="large"
              />
              <el-button
                :disabled="countdown > 0 || !isPhoneValid"
                @click="handleSendCode"
                size="large"
                class="code-btn"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="submitting"
              class="login-btn"
              size="large"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { sendSmsCode } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

const form = reactive({
  phone: '',
  code: '',
})

const isPhoneValid = computed(() => /^1\d{10}$/.test(form.phone))

const rules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' },
  ],
}

async function handleSendCode() {
  if (!isPhoneValid.value) return
  try {
    await sendSmsCode(form.phone)
    ElMessage.success('验证码已发送')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer!)
        timer = null
      }
    }, 1000)
  } catch {
    countdown.value = 0
  }
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await userStore.login(form.phone, form.code)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - #{$header-height} - 80px);
  padding: 20px;
  background: $bg-warm;
}

.login-card {
  width: 420px;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow-lg;
  background: $card-bg;

  &__header {
    background: linear-gradient(135deg, $primary-dark 0%, $primary-color 60%, $primary-light 100%);
    padding: 36px 32px 28px;
    text-align: center;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -1px;
      left: 0;
      right: 0;
      height: 20px;
      background: $card-bg;
      border-radius: $radius-lg $radius-lg 0 0;
    }
  }

  &__logo {
    margin-bottom: 12px;
    position: relative;
    z-index: 1;
  }

  &__title {
    margin: 0 0 4px;
    font-family: $font-display;
    font-size: 24px;
    font-weight: 700;
    color: #fff;
    position: relative;
    z-index: 1;
  }

  &__subtitle {
    margin: 0;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    position: relative;
    z-index: 1;
  }

  &__body {
    padding: 24px 32px 32px;
  }
}

.login-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: $text-color;
    font-size: 13px;
  }

  :deep(.el-input__wrapper) {
    border-radius: $radius-sm;
    box-shadow: 0 0 0 1px $border-color inset;
    transition: box-shadow 0.25s;

    &:hover {
      box-shadow: 0 0 0 1px $primary-light inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px $primary-color inset, 0 0 0 3px rgba($primary-color, 0.1);
    }
  }

  :deep(.el-input__prefix .el-icon) {
    color: $text-muted;
  }
}

.code-row {
  display: flex;
  gap: 10px;
  width: 100%;

  .el-input {
    flex: 1;
  }
}

.code-btn {
  flex-shrink: 0;
  min-width: 110px;
  border-color: $accent-color;
  color: $accent-color;
  border-radius: $radius-sm;

  &:hover:not(:disabled) {
    background: $accent-lighter;
    border-color: $accent-color;
    color: $accent-color;
  }

  &:disabled {
    border-color: $border-color;
    color: $text-muted;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  border-radius: $radius-sm;
  background: linear-gradient(135deg, $primary-color, $primary-light);
  border: none;

  &:hover {
    background: linear-gradient(135deg, $primary-light, $primary-color);
  }
}
</style>
