import request from '@/utils/request'

// 获取所有咨询师列表（公开）
export function getCounselors() {
  return request({
    url: '/api/consult/counselors',
    method: 'get'
  })
}

// 创建新会话
export function createSession(counselorId, sessionKeyEncrypted) {
  return request({
    url: '/api/consult/sessions',
    method: 'post',
    params: { counselorId },
    headers: {
      'Content-Type': 'text/plain'
    },
    data: sessionKeyEncrypted
  })
}

// 获取当前用户的会话历史
export function getMySessions() {
  return request({
    url: '/api/consult/sessions/me',
    method: 'get'
  })
}

// 发送消息
export function sendMessage(sessionId, encryptedMessage) {
  return request({
    url: '/api/consult/messages',
    method: 'post',
    params: { sessionId },
    headers: {
      'Content-Type': 'text/plain'
    },
    data: encryptedMessage
  })
}

// 拉取消息
export function getMessages(sessionId) {
  return request({
    url: `/api/consult/sessions/${sessionId}/messages`,
    method: 'get'
  })
}

// 获取会话详情（用于聊天页显示对方信息）
export function getSessionById(sessionId) {
  return request({
    url: `/api/consult/sessions/${sessionId}`,
    method: 'get'
  })
}

