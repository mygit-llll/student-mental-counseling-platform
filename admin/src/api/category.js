import request from '@/utils/request'

// 获取所有分类（公开）
export function getCategoryList() {
  return request({
    url: '/knowledge/public/categories', // ← 请根据你的后端路径调整
    method: 'get'
  })
}
