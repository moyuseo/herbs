import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRanking, getPriceIndex } from '@/api/market'

export const useMarketStore = defineStore('market', () => {
  const rankingData = ref({ up: [], down: [] })
  const indexData = ref([])
  const currentPeriod = ref('day')

  async function fetchRanking(period = 'day') {
    currentPeriod.value = period
    const upData = await getRanking(period, 20)
    rankingData.value = upData
  }

  async function fetchIndexData(indexType) {
    indexData.value = await getPriceIndex(indexType)
  }

  return { rankingData, indexData, currentPeriod, fetchRanking, fetchIndexData }
})
