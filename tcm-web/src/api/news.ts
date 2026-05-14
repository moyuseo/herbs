import request from './request'

export function getNewsList(params?: Record<string, any>): Promise<any> {
  return request.get('/news/list', { params }) as Promise<any>
}

export function getNewsDetail(id: string | number): Promise<any> {
  return request.get(`/news/detail/${id}`) as Promise<any>
}
