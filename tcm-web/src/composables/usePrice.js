import { ref } from 'vue'
import { getHerbDetail, getPriceHistory } from '@/api/market'

export function usePrice() {
  const herbDetail = ref(null)
  const priceHistory = ref([])
  const loading = ref(false)

  async function fetchHerbDetail(id) {
    loading.value = true
    try {
      herbDetail.value = await getHerbDetail(id)
    } finally {
      loading.value = false
    }
  }

  async function fetchPriceHistory(herbSpecId, period = '1y') {
    try {
      priceHistory.value = await getPriceHistory(herbSpecId, period)
    } catch (e) {
      priceHistory.value = []
    }
  }

  return { herbDetail, priceHistory, loading, fetchHerbDetail, fetchPriceHistory }
}
