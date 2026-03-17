import request from '@/utils/request'

// 获取高危测评记录
export function getHighRiskTests(params) {
  return request({
    url: '/api/admin/monitor/high-score-tests',
    method: 'get',
    params
  })
}
