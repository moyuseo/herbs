<template>
  <view class="page">
    <view class="login-header">
      <text class="login-title">中药材行情</text>
      <text class="login-subtitle">行业数据 一手掌握</text>
    </view>

    <view class="form-section">
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input
          class="form-input"
          v-model="phone"
          type="number"
          maxlength="11"
          placeholder="请输入手机号"
        />
      </view>
      <view class="form-item">
        <text class="form-label">验证码</text>
        <view class="code-row">
          <input
            class="form-input code-input"
            v-model="code"
            type="number"
            maxlength="6"
            placeholder="请输入验证码"
          />
          <view
            :class="['code-btn', { disabled: countdown > 0 }]"
            @tap="sendCode"
          >
            <text class="code-btn-text">{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}</text>
          </view>
        </view>
      </view>

      <view class="login-btn" @tap="handleLogin">
        <text class="login-btn-text">登 录</text>
      </view>

      <view class="agreement">
        <text class="agreement-text">登录即表示同意</text>
        <text class="agreement-link">《用户协议》</text>
        <text class="agreement-text">和</text>
        <text class="agreement-link">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const phone = ref('')
const code = ref('')
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

function validatePhone(): boolean {
  if (!phone.value) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return false
  }
  return true
}

function sendCode() {
  if (countdown.value > 0) return
  if (!validatePhone()) return

  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      if (timer) clearInterval(timer)
      timer = null
    }
  }, 1000)

  uni.showToast({ title: '验证码已发送', icon: 'none' })
}

function handleLogin() {
  if (!validatePhone()) return
  if (!code.value) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return
  }
  if (code.value.length < 4) {
    uni.showToast({ title: '验证码格式不正确', icon: 'none' })
    return
  }

  uni.showLoading({ title: '登录中...' })
  setTimeout(() => {
    uni.hideLoading()
    uni.setStorageSync('token', 'mock_token_123')
    uni.setStorageSync('userInfo', JSON.stringify({
      id: 1,
      nickname: '药材人',
      avatar: '',
      phone: phone.value,
      isVip: false
    }))
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }, 1000)
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #fff;
}

.login-header {
  padding: 100rpx 60rpx 60rpx;
  background: #e74c3c;
}

.login-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
  display: block;
}

.login-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 16rpx;
  display: block;
}

.form-section {
  padding: 60rpx 40rpx;
}

.form-item {
  margin-bottom: 36rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  display: block;
  margin-bottom: 16rpx;
}

.form-input {
  width: 100%;
  height: 88rpx;
  border: 1rpx solid #ddd;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.code-row {
  display: flex;
  align-items: center;
}

.code-input {
  flex: 1;
  margin-right: 20rpx;
}

.code-btn {
  width: 200rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e74c3c;
  border-radius: 12rpx;
}

.code-btn.disabled {
  background: #ccc;
}

.code-btn-text {
  font-size: 26rpx;
  color: #fff;
}

.login-btn {
  margin-top: 60rpx;
  height: 88rpx;
  background: #e74c3c;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.agreement {
  margin-top: 30rpx;
  text-align: center;
}

.agreement-text {
  font-size: 24rpx;
  color: #999;
}

.agreement-link {
  font-size: 24rpx;
  color: #e74c3c;
}
</style>
