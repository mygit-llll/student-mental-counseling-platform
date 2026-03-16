import request from '@/utils/request'

// 获取测试详情（含题目）
export function getPublicTestDetail(testId) {
  return request({
    url: `/api/public/tests/${testId}`,
    method: 'get'
  })
}

// 提交测试
export function submitTest(data) {
  return request({
    url: '/api/public/submit',
    method: 'post',
    data
  })
}

// 获取所有公开测试（不含题目）
export function getPublicTests() {
  return request({
    url: '/api/public/tests',
    method: 'get'
  })
}

// 获取某次测试的结果（含分数、解读、时间）
export function getTestResult(testId) {
  return request({
    url: `/api/my/tests/${testId}/result`,
    method: 'get'
  })
}

// 获取用户所有历史记录
export function getMyTestHistory() {
  return request({
    url: '/api/my/tests/history',
    method: 'get'
  })
}
