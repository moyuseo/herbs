import request from './request'

export function getSupplyList(params?: Record<string, any>): Promise<any> {
  return request.get('/supply/list', { params }) as Promise<any>
}

export function getDemandList(params?: Record<string, any>): Promise<any> {
  return request.get('/demand/list', { params }) as Promise<any>
}

export function publishSupply(data: Record<string, any>): Promise<any> {
  return request.post('/supply/publish', data) as Promise<any>
}

export function publishDemand(data: Record<string, any>): Promise<any> {
  return request.post('/demand/publish', data) as Promise<any>
}
