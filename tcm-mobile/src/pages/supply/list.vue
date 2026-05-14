<template>
  <view class="page">
    <view class="type-tabs">
      <view
        :class="['type-tab', { active: currentType === 'supply' }]"
        @tap="currentType = 'supply'"
      >
        供应
      </view>
      <view
        :class="['type-tab', { active: currentType === 'demand' }]"
        @tap="currentType = 'demand'"
      >
        求购
      </view>
    </view>

    <scroll-view
      scroll-y
      class="list-scroll"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="supply-list">
        <view
          v-for="item in supplyList"
          :key="item.id"
          class="supply-card"
        >
          <view class="card-header">
            <text :class="['card-type', item.type === '供应' ? 'supply' : 'demand']">{{ item.type }}</text>
            <text class="card-date">{{ item.date }}</text>
          </view>
          <view class="card-body">
            <text class="card-name">{{ item.name }}</text>
            <text class="card-spec">{{ item.spec }}</text>
          </view>
          <view class="card-footer">
            <text class="card-quantity">数量：{{ item.quantity }}</text>
            <text class="card-price">{{ item.price }}</text>
          </view>
          <view class="card-contact">
            <text class="contact-name">{{ item.contact }}</text>
            <text class="contact-area">{{ item.area }}</text>
          </view>
        </view>
      </view>

      <view class="load-tip">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const currentType = ref('supply')
const supplyList = ref<any[]>([])
const page = ref(1)
const loading = ref(false)
const refreshing = ref(false)
const noMore = ref(false)

function loadList(reset = false) {
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  if (loading.value || noMore.value) return

  loading.value = true

  const isSupply = currentType.value === 'supply'
  const mockData = isSupply
    ? [
        { id: 1, type: '供应', name: '黄芪', spec: '统片 | 甘肃产', quantity: '500kg', price: '面议', contact: '张先生', area: '甘肃岷县', date: '2026-05-14' },
        { id: 2, type: '供应', name: '当归', spec: '草把 | 岷归', quantity: '300kg', price: '55元/kg', contact: '李女士', area: '甘肃岷县', date: '2026-05-13' },
        { id: 3, type: '供应', name: '白术', spec: '统个 | 浙江产', quantity: '200kg', price: '115元/kg', contact: '王先生', area: '浙江磐安', date: '2026-05-12' },
        { id: 4, type: '供应', name: '甘草', spec: '毛草 | 内蒙古', quantity: '1000kg', price: '16元/kg', contact: '赵先生', area: '内蒙古赤峰', date: '2026-05-11' },
        { id: 5, type: '供应', name: '川芎', spec: '统个 | 四川产', quantity: '400kg', price: '28元/kg', contact: '刘女士', area: '四川都江堰', date: '2026-05-10' }
      ]
    : [
        { id: 6, type: '求购', name: '当归', spec: '草把 | 岷归', quantity: '200kg', price: '58元/kg', contact: '陈先生', area: '安徽亳州', date: '2026-05-14' },
        { id: 7, type: '求购', name: '黄芪', spec: '统片 | 甘肃产', quantity: '100kg', price: '23元/kg', contact: '孙女士', area: '河北安国', date: '2026-05-13' },
        { id: 8, type: '求购', name: '茯苓', spec: '白块 | 云南产', quantity: '150kg', price: '35元/kg', contact: '周先生', area: '广东清平', date: '2026-05-12' },
        { id: 9, type: '求购', name: '柴胡', spec: '北柴胡', quantity: '50kg', price: '85元/kg', contact: '吴女士', area: '湖南廉桥', date: '2026-05-11' }
      ]

  setTimeout(() => {
    if (reset) {
      supplyList.value = mockData
    } else {
      supplyList.value = [...supplyList.value, ...mockData.map((item, i) => ({
        ...item,
        id: supplyList.value.length + i + 1
      }))]
    }
    noMore.value = true
    loading.value = false
    refreshing.value = false
  }, 500)
}

watch(currentType, () => {
  loadList(true)
})

function onRefresh() {
  refreshing.value = true
  loadList(true)
}

function onLoadMore() {
  page.value++
  loadList()
}

loadList(true)
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.type-tabs {
  display: flex;
  background: #fff;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #eee;
}

.type-tab {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #666;
  border-radius: 8rpx;
  background: #f5f5f5;
  margin: 0 10rpx;
}

.type-tab.active {
  background: #e74c3c;
  color: #fff;
}

.list-scroll {
  flex: 1;
}

.supply-list {
  padding: 20rpx 30rpx;
}

.supply-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.card-type {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 6rpx;
}

.card-type.supply {
  background: #fff0ed;
  color: #e74c3c;
}

.card-type.demand {
  background: #edf7ef;
  color: #27ae60;
}

.card-date {
  font-size: 24rpx;
  color: #999;
}

.card-body {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.card-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-right: 16rpx;
}

.card-spec {
  font-size: 24rpx;
  color: #999;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.card-quantity {
  font-size: 26rpx;
  color: #666;
}

.card-price {
  font-size: 28rpx;
  font-weight: bold;
  color: #e74c3c;
}

.card-contact {
  display: flex;
  align-items: center;
}

.contact-name {
  font-size: 24rpx;
  color: #666;
  margin-right: 20rpx;
}

.contact-area {
  font-size: 24rpx;
  color: #999;
}

.load-tip {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>
