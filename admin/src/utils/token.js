import Cookies from 'js-cookie'
import { jwtDecode } from 'jwt-decode'

const TokenKey = 'Account-Token'
const expires = 1

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token, { expires })
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function parseUserIdFromToken(token) {
  if (!token) return null
  try {
    const cleanToken = token.trim().replace(/^Bearer\s+/i, '')
    if (!cleanToken) return null
    const decoded = jwtDecode(cleanToken)
    return typeof decoded.accountId === 'number' ? decoded.accountId : null
  } catch (e) {
    console.warn('Token 解析失败:', e.message || e)
    return null
  }
}
