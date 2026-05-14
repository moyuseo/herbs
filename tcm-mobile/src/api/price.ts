import { get } from './request'

export interface PriceItem {
  id: number
  name: string
  spec: string
  market: string
  price: number
  change: number
  changePercent: number
  unit: string
  date: string
}

export interface MarketOverview {
  upCount: number
  downCount: number
  flatCount: number
}

export interface PriceTrend {
  date: string
  price: number
}

export interface HerbDetail {
  id: number
  name: string
  alias: string
  origin: string
  category: string
  spec: string
  unit: string
  currentPrice: number
  change: number
  changePercent: number
  markets: MarketPrice[]
  trends: PriceTrend[]
}

export interface MarketPrice {
  market: string
  price: number
  change: number
  changePercent: number
  date: string
}

export function getMarketOverview() {
  return get<MarketOverview>('/price/overview')
}

export function getPriceList(params: { market?: string; keyword?: string; page?: number; pageSize?: number }) {
  return get<PriceItem[]>('/price/list', params)
}

export function getHerbDetail(id: number) {
  return get<HerbDetail>(`/price/detail/${id}`)
}

export function getPriceTrend(params: { herbId: number; days?: number }) {
  return get<PriceTrend[]>('/price/trend', params)
}

export function searchHerb(keyword: string) {
  return get<PriceItem[]>('/price/search', { keyword })
}
