<template>
  <div class="ranking-list" v-loading="loading">
    <div v-if="!data.length && !loading" class="empty-tip">暂无数据</div>
    <div
      v-for="(item, index) in data"
      :key="item.id || index"
      class="ranking-item"
    >
      <span class="rank-num" :class="getRankClass(index)">
        <span v-if="index < 3" class="medal">{{ ['🥇', '🥈', '🥉'][index] }}</span>
        <span v-else>{{ index + 1 }}</span>
      </span>
      <router-link :to="`/market/herb/${item.id}`" class="rank-name">{{ item.name }}</router-link>
      <span class="rank-price">{{ item.price }}</span>
      <span :class="['rank-change', type === 'up' ? 'change-up' : 'change-down']">
        {{ type === 'up' ? '+' : '' }}{{ item.changePercent?.toFixed(2) }}%
      </span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  data: { type: Array, default: () => [] },
  type: { type: String, default: 'up', validator: v => ['up', 'down'].includes(v) },
  loading: { type: Boolean, default: false }
})

function getRankClass(index) {
  if (index < 3) return `rank-top rank-top-${index + 1}`
  return ''
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.ranking-list {
  min-height: 200px;
}

.empty-tip {
  text-align: center;
  color: $text-secondary;
  padding: 40px 0;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  &:last-child {
    border-bottom: none;
  }
}

.rank-num {
  width: 32px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: $text-secondary;
  flex-shrink: 0;
}

.medal {
  font-size: 18px;
}

.rank-name {
  flex: 1;
  color: $text-color;
  font-size: 14px;
  text-decoration: none;
  &:hover {
    color: $primary-color;
  }
}

.rank-price {
  margin-right: 16px;
  font-weight: 600;
  font-size: 14px;
  color: $text-color;
}

.rank-change {
  font-size: 13px;
  font-weight: 500;
  min-width: 70px;
  text-align: right;
}

.change-up {
  color: $up-color;
}

.change-down {
  color: $down-color;
}
</style>
