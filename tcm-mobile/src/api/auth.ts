import { get, post } from './request'

export interface LoginParams {
  phone: string
  code: string
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export interface UserInfo {
  id: number
  nickname: string
  avatar: string
  phone: string
  isVip: boolean
}

export function sendSmsCode(phone: string) {
  return post<null>('/auth/sms-code', { phone })
}

export function login(params: LoginParams) {
  return post<LoginResult>('/auth/login', params)
}

export function getUserInfo() {
  return get<UserInfo>('/auth/user-info')
}

export function logout() {
  return post<null>('/auth/logout')
}
