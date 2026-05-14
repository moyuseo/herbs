import request from './request'

export function getMarketPrices(params) {
  return request.get('/market/prices', { params })
}

export function getOriginPrices(params) {
  return request.get('/market/origin-prices', { params })
}

export function getHerbDetail(id) {
  return request.get(`/market/herbs/${id}`)
}

export function getPriceHistory(id, period = '1y') {
  return request.get(`/market/herbs/${id}/history`, { params: { period } })
}

export function getPriceHistoryQuery(params) {
  return request.get('/market/price-history/query', { params })
}

export function getHerbSpecs(herbId) {
  return request.get(`/market/herbs/${herbId}/specs`)
}

export function getRanking(period = 'day', limit = 20) {
  return request.get('/market/ranking', { params: { period, limit } })
}

export function getPriceIndex(indexType) {
  return request.get('/market/index', { params: { indexType } })
}

export function getPriceIndexHistory(params) {
  return request.get('/market/index/history', { params })
}

export function searchHerbs(keyword, page = 1, size = 20) {
  return request.get('/market/herbs/search', { params: { keyword, page, size } })
}
