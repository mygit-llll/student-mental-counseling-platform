import request from '@/utils/request'

// 获取公开文章列表（支持分类 + 搜索）
export function getPublicArticleList(page = 1, size = 10, categoryId = null, keyword = null) {
  return request({
    url: '/knowledge/public/list',
    method: 'get',
    params: {
      page,
      size,
      categoryId,
      keyword
    }
  })
}

// 创建文章
export function createArticle(data) {
  return request({
    url: '/knowledge',
    method: 'post',
    data
  })
}

// 获取公开文章详情
export function getPublicArticleById(id) {
  return request({
    url: `/knowledge/public/${id}`,
    method: 'get'
  })
}

// 获取管理员文章列表
export function getAdminArticleList(params) {
  return request({
    url: '/knowledge/admin/list',
    method: 'get',
    params
  })
}

// 审核文章
export function auditArticle(id, result) {
  return request({
    url: `/knowledge/${id}/audit`,
    method: 'put',
    params: { result }
  })
}

export function getAdminArticleDetail(id) {
  return request({
    url: `/knowledge/admin/detail/${id}`,
    method: 'get'
  })
}

// 删除文章
export function deleteArticle(id) {
  return request({
    url: `/knowledge/${id}`,
    method: 'delete'
  })
}
