<template>
  <view class="page">
    <view class="header-section">
      <text class="herb-name">{{ detail.name }}</text>
      <text class="herb-alias">别名：{{ detail.alias }}</text>
      <view class="price-row">
        <text class="current-price">¥{{ detail.currentPrice.toFixed(2) }}</text>
        <PriceTag :value="detail.changePercent" />
      </view>
      <view class="info-row">
        <text class="info-item">产地：{{ detail.origin }}</text>
        <text class="info-item">规格：{{ detail.spec }}</text>
        <text class="info-item">单位：{{ detail.unit }}</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">价格走势</text>
      <view class="chart-container">
        <PriceChart :trends="detail.trends" />
      </view>
    </view>

    <view class="section">
      <text class="section-title">各市场价格</text>
      <view class="market-list">
        <view
          v-for="(item, idx) in detail.markets"
          :key="idx"
          class="market-card"
        >
          <view class="market-card-header">
            <text class="market-name">{{ item.market }}</text>
            <PriceTag :value="item.changePercent" />
          </view>
          <view class="market-card-body">
            <text class="market-price">¥{{ item.price.toFixed(2) }}</text>
            <text class="market-date">{{ item.date }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PriceTag from '@/components/PriceTag.vue'
import PriceChart from '@/components/PriceChart.vue'

const detail = ref({
  id: 0,
  name: '',
  alias: '',
  origin: '',
  category: '',
  spec: '',
  unit: '',
  currentPrice: 0,
  change: 0,
  changePercent: 0,
  markets: [] as any[],
  trends: [] as any[]
})

function loadDetail(id: number) {
  detail.value = {
    id,
    name: '黄芪',
    alias: '绵芪、黄耆',
    origin: '甘肃、内蒙古',
    category: '根茎类',
    spec: '统片',
    unit: '元/公斤',
    currentPrice: 22.5,
    change: 0.7,
    changePercent: 3.2,
    markets: [
      { market: '亳州', price: 22.5, changePercent: 3.2, date: '2026-05-14' },
      { market: '安国', price: 23.0, changePercent: 2.8, date: '2026-05-14' },
      { market: '成都', price: 21.8, changePercent: 4.1, date: '2026-05-14' },
      { market: '玉林', price: 22.0, changePercent: 1.5, date: '2026-05-14' },
      { market: '廉桥', price: 22.3, changePercent: 2.0, date: '2026-05-14' }
    ],
    trends: [
      { date: '2026-04', price: 19.5 },
      { date: '2026-05-01', price: 20.0 },
      { date: '2026-05-05', price: 20.8 },
      { date: '2026-05-08', price: 21.2 },
      { date: '2026-05-10', price: 21.8 },
      { date: '2026-05-12', price: 22.0 },
      { date: '2026-05-14', price: 22.5 }
    ]
  }
}

onLoad((options) => {
  const id = Number(options?.id || 1)
  loadDetail(id)
})
</script>

<style scoped>
.page {
  padding-bottom: 40rpx;
}

.header-section {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.herb-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  display: block;
}

.herb-alias {
  font-size: 26rpx;
  color: #999;
  margin-top: 10rpx;
  display: block;
}

.price-row {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.current-price {
  font-size: 48rpx;
  font-weight: bold;
  color: #e74c3c;
  margin-right: 16rpx;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20rpx;
}

.info-item {
  font-size: 26rpx;
  color: #666;
  margin-right: 30rpx;
  margin-top: 8rpx;
}

.section {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.chart-container {
  width: 100%;
  height: 400rpx;
}

.market-list {
  display: flex;
  flex-direction: column;
}

.market-card {
  padding: 24rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
}

.market-card:last-child {
  margin-bottom: 0;
}

.market-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.market-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.market-card-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
}

.market-price {
  font-size: 34rpx;
  font-weight: bold;
  color: #e74c3c;
}

.market-date {
  font-size: 24rpx;
  color: #999;
}
</style>
