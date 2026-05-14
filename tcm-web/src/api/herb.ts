import request from './request'

export function searchHerb(keyword: string): Promise<any> {
  return request.get('/herb/search', { params: { keyword } }) as Promise<any>
}

export function getHerbList(params?: Record<string, any>): Promise<any> {
  return request.get('/herb/list', { params }) as Promise<any>
}

export function getHerbDetail(herbId: string | number): Promise<any> {
  return request.get(`/herb/detail/${herbId}`) as Promise<any>
}
