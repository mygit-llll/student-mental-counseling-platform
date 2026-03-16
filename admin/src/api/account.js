import request from '@/utils/request'

export function search(searchForm) {
  return request({
    url: '/account/search',
    method: 'post',
    data: searchForm
  })
}

export function list(params) {
  return request({
    url: '/account',
    method: 'get',
    params
  })
}

// 用户修改自己的资料（不含密码）
export function updateProfile(profileData) {
  return request({
    url: '/account/detail',
    method: 'put',
    data: profileData
  })
}

// 用户修改自己的密码
export function changePassword({ oldPassword, newPassword }) {
  return request({
    url: '/account/profile/password',
    method: 'put',
    data: { oldPassword, newPassword }
  })
}

// 管理员修改任意用户（需权限）
export function adminUpdateAccount(id, accountData) {
  return request({
    url: `/account/${id}`, // 使用反引号！
    method: 'put',
    data: accountData
  })
}

export function remove(accountId) {
  return request({
    url: '/account/' + accountId,
    method: 'delete'
  })
}

export function register(accountForm) {
  return request({
    url: '/account',
    method: 'post',
    data: accountForm
  })
}

export function login(data) {
  return request({
    url: '/public/login',
    method: 'post',
    data
  })
}

export function detail() {
  return request({
    url: '/account/detail',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/account/token',
    method: 'delete'
  })
}

/**
 * 上传用户公钥（用于咨询师）
 */
export function uploadPublicKey(publicKey) {
  return request({
    url: '/account/public-key',
    method: 'post',
    data: { publicKey }
  })
}

