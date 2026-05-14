import request from './request'

export function sendSmsCode(phone: string): Promise<any> {
  return request.post('/auth/sms', { phone }) as Promise<any>
}

export function login(phone: string, code: string): Promise<any> {
  return request.post('/auth/login', { phone, code }) as Promise<any>
}

export function getUserProfile(): Promise<any> {
  return request.get('/auth/profile') as Promise<any>
}
