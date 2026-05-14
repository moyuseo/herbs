<template>
  <view class="page">
    <view class="user-header" v-if="isLogin">
      <view class="avatar-wrap">
        <image
          v-if="userInfo.avatar"
          class="avatar"
          :src="userInfo.avatar"
          mode="aspectFill"
        />
        <view v-else class="avatar-placeholder">
          <text class="avatar-text">{{ userInfo.nickname.charAt(0) }}</text>
        </view>
      </view>
      <view class="user-info">
        <text class="nickname">{{ userInfo.nickname }}</text>
        <text class="phone">{{ userInfo.phone }}</text>
      </view>
      <view v-if="userInfo.isVip" class="vip-badge">
        <text class="vip-text">VIP</text>
      </view>
    </view>

    <view class="user-header" v-else @tap="goLogin">
      <view class="avatar-wrap">
        <view class="avatar-placeholder">
          <text class="avatar-text">?</text>
        </view>
      </view>
      <view class="user-info">
        <text class="nickname">点击登录</text>
        <text class="phone">登录后享受更多服务</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item" @tap="handleMenu('follow')">
        <text class="menu-text">我的关注</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="handleMenu('alert')">
        <text class="menu-text">价格预警</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="handleMenu('vip')">
        <text class="menu-text">会员中心</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="handleMenu('publish')">
        <text class="menu-text">我的发布</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-section" v-if="isLogin">
      <view class="menu-item logout" @tap="handleLogout">
        <text class="menu-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const isLogin = ref(false)
const userInfo = ref({
  id: 0,
  nickname: '',
  avatar: '',
  phone: '',
  isVip: false
})

function checkLogin() {
  const token = uni.getStorageSync('token')
  const info = uni.getStorageSync('userInfo')
  if (token && info) {
    isLogin.value = true
    try {
      userInfo.value = JSON.parse(info)
    } catch {
      userInfo.value = { id: 0, nickname: '用户', avatar: '', phone: '', isVip: false }
    }
  } else {
    isLogin.value = false
  }
}

function goLogin() {
  uni.navigateTo({ url: '/pages/user/login' })
}

function handleMenu(type: string) {
  if (!isLogin.value) {
    goLogin()
    return
  }
  const messages: Record<string, string> = {
    follow: '我的关注功能开发中',
    alert: '价格预警功能开发中',
    vip: '会员中心功能开发中',
    publish: '我的发布功能开发中'
  }
  uni.showToast({ title: messages[type] || '功能开发中', icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        isLogin.value = false
        userInfo.value = { id: 0, nickname: '', avatar: '', phone: '', isVip: false }
        uni.showToast({ title: '已退出登录', icon: 'none' })
      }
    }
  })
}

onShow(() => {
  checkLogin()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f5f5;
}

.user-header {
  display: flex;
  align-items: center;
  padding: 40rpx 30rpx;
  background: #e74c3c;
}

.avatar-wrap {
  margin-right: 24rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
}

.avatar-placeholder {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 34rpx;
  color: #fff;
  font-weight: bold;
}

.phone {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
}

.vip-badge {
  background: #f39c12;
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
}

.vip-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
}

.menu-section {
  margin-top: 20rpx;
  background: #fff;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 32rpx;
  color: #ccc;
}

.menu-item.logout .menu-text {
  color: #e74c3c;
  text-align: center;
  width: 100%;
}
</style>
