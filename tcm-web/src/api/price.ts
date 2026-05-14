import request from './request'

export function getMarketPrices(params?: Record<string, any>): Promise<any> {
  return request.get('/price/market', { params }) as Promise<any>
}

export function getOriginPrices(params?: Record<string, any>): Promise<any> {
  return request.get('/price/origin', { params }) as Promise<any>
}

export function getPriceDetail(herbId: string | number): Promise<any> {
  return request.get(`/price/detail/${herbId}`) as Promise<any>
}

export function getPriceHistory(herbId: string | number, params?: Record<string, any>): Promise<any> {
  return request.get(`/price/history/${herbId}`, { params }) as Promise<any>
}

export function getRanking(params?: Record<string, any>): Promise<any> {
  return request.get('/price/ranking', { params }) as Promise<any>
}

export function getPriceIndex(params?: Record<string, any>): Promise<any> {
  return request.get('/data/index', { params }) as Promise<any>
}

export function getLatestIndex(params?: Record<string, any>): Promise<any> {
  return request.get('/data/index/latest', { params }) as Promise<any>
}

export function getMarketSummary(): Promise<any> {
  return request.get('/price/summary') as Promise<any>
}
