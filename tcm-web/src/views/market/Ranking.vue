<template>
  <div class="ranking page-wrapper">
    <div class="container">
      <div class="card-box">
        <div class="ranking-header">
          <h2 class="ranking-title">涨跌排行</h2>
          <el-tabs v-model="activePeriod" @tab-change="handlePeriodChange">
            <el-tab-pane label="日排行" name="day" />
            <el-tab-pane label="周排行" name="week" />
            <el-tab-pane label="月排行" name="month" />
          </el-tabs>
        </div>

        <el-row :gutter="20" class="ranking-content">
          <el-col :span="12">
            <div class="ranking-panel">
              <div class="panel-header">
                <span class="panel-title up-title">涨幅榜</span>
              </div>
              <div class="ranking-list">
                <div
                  v-for="(item, index) in marketStore.rankingData.up"
                  :key="item.id || index"
                  class="ranking-item"
                >
                  <span class="rank-num">
                    <el-tag v-if="index < 3" type="danger" size="small" round>{{ index + 1 }}</el-tag>
                    <span v-else class="rank-text">{{ index + 1 }}</span>
                  </span>
                  <span class="herb-name" @click="goHerbDetail(item.id)">{{ item.name }}</span>
                  <span class="herb-price">{{ item.price }}</span>
                  <span class="herb-change price-up">+{{ item.changeRate }}%</span>
                </div>
                <el-empty v-if="!marketStore.rankingData.up.length" description="暂无数据" />
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="ranking-panel">
              <div class="panel-header">
                <span class="panel-title down-title">跌幅榜</span>
              </div>
              <div class="ranking-list">
                <div
                  v-for="(item, index) in marketStore.rankingData.down"
                  :key="item.id || index"
                  class="ranking-item"
                >
                  <span class="rank-num">
                    <el-tag v-if="index < 3" type="success" size="small" round>{{ index + 1 }}</el-tag>
                    <span v-else class="rank-text">{{ index + 1 }}</span>
                  </span>
                  <span class="herb-name" @click="goHerbDetail(item.id)">{{ item.name }}</span>
                  <span class="herb-price">{{ item.price }}</span>
                  <span class="herb-change price-down">{{ item.changeRate }}%</span>
                </div>
                <el-empty v-if="!marketStore.rankingData.down.length" description="暂无数据" />
              </div>
            </div>
          </el-col>
        </el-row>

        <div class="distribution-section">
          <h3 class="section-title">涨跌分布</h3>
          <div ref="pieChartRef" class="pie-chart" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useMarketStore } from '@/stores/market'
import * as echarts from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([PieChart, TitleComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const router = useRouter()
const marketStore = useMarketStore()

const activePeriod = ref('day')
const pieChartRef = ref(null)
let pieChart = null

const distribution = ref([
  { name: '涨', value: 45 },
  { name: '跌', value: 30 },
  { name: '稳', value: 15 },
  { name: '少', value: 10 }
])

function handlePeriodChange(period) {
  marketStore.fetchRanking(period)
}

function goHerbDetail(id) {
  if (id) router.push({ name: 'HerbDetail', params: { id } })
}

function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  updatePieChart()
}

function updatePieChart() {
  if (!pieChart) return
  pieChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 个 ({d}%)'
    },
    legend: {
      bottom: '5%',
      left: 'center'
    },
    series: [
      {
        name: '涨跌分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' }
        },
        data: distribution.value.map((item) => {
          const colorMap = { '涨': '#F56C6C', '跌': '#2D8C4E', '稳': '#E6A23C', '少': '#909399' }
          return { ...item, itemStyle: { color: colorMap[item.name] } }
        })
      }
    ]
  }, true)
}

function handleResize() {
  pieChart?.resize()
}

onMounted(async () => {
  await marketStore.fetchRanking('day')
  nextTick(() => {
    initPieChart()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  pieChart = null
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.ranking-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  .ranking-title {
    font-size: 20px;
    color: $text-color;
    white-space: nowrap;
  }

  :deep(.el-tabs) {
    flex: 1;
    margin-left: 30px;

    .el-tabs__header {
      margin-bottom: 0;
    }
  }
}

.ranking-content {
  margin-bottom: 30px;
}

.ranking-panel {
  border: 1px solid $border-color;
  border-radius: $border-radius;
  overflow: hidden;

  .panel-header {
    padding: 12px 16px;
    background: $bg-color;

    .panel-title {
      font-size: 16px;
      font-weight: 600;
    }

    .up-title {
      color: $up-color;
    }

    .down-title {
      color: $down-color;
    }
  }
}

.ranking-list {
  padding: 0 16px;
  max-height: 500px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  .rank-num {
    width: 36px;
    text-align: center;
    flex-shrink: 0;

    .rank-text {
      display: inline-block;
      width: 24px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      font-size: 13px;
      color: $text-secondary;
    }
  }

  .herb-name {
    flex: 1;
    color: $primary-color;
    cursor: pointer;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &:hover {
      color: $primary-light;
      text-decoration: underline;
    }
  }

  .herb-price {
    width: 80px;
    text-align: right;
    color: $text-color;
    font-weight: 500;
    margin-right: 16px;
  }

  .herb-change {
    width: 80px;
    text-align: right;
    font-weight: 600;
  }
}

.distribution-section {
  margin-top: 20px;

  .section-title {
    font-size: 16px;
    color: $text-color;
    margin-bottom: 16px;
  }
}

.pie-chart {
  width: 100%;
  height: 350px;
}

@media (max-width: 768px) {
  .ranking-header {
    flex-direction: column;
    align-items: flex-start;

    .ranking-title {
      margin-bottom: 10px;
    }

    :deep(.el-tabs) {
      margin-left: 0;
      width: 100%;
    }
  }

  .ranking-item {
    .herb-price {
      width: 60px;
      margin-right: 8px;
    }

    .herb-change {
      width: 60px;
    }
  }
}
</style>
