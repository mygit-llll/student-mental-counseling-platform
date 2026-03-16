<template>
  <div class="chat-container" style="padding: 20px; display: flex; flex-direction: column; height: calc(100vh - 120px);">
    <h2>与 {{ otherName }} 的对话</h2>

    <!-- 消息区域 -->
    <div ref="messageBox" style="flex: 1; overflow-y: auto; border: 1px solid #eee; padding: 16px; margin: 16px 0; background: #fafafa;">
      <div v-for="msg in messages" :key="msg.id" style="margin-bottom: 12px;">
        <div style="font-weight: bold; color: #409EFF;" v-if="msg.isSelf">
          我：
        </div>
        <div style="font-weight: bold; color: #67C23A;" v-else>
          {{ otherName }}：
        </div>
        <div style="margin-left: 16px; word-break: break-all;">
          {{ msg.decryptedContent }}
        </div>
        <div style="font-size: 12px; color: #999; margin-top: 4px;">
          {{ formatTime(msg.sentAt) }}
        </div>
      </div>
    </div>

    <!-- 输入框 -->
    <div style="display: flex;">
      <el-input
        v-model="inputMessage"
        placeholder="请输入消息（将自动加密）"
        style="flex: 1; margin-right: 8px;"
        @keyup.enter.native="sendMessage"
      />
      <el-button type="primary" @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script>
import { getMessages, sendMessage, getSessionById } from '@/api/consultation'
import { encryptAESGCM, decryptAESGCM } from '@/utils/aes-gcm'

export default {
  name: 'ConsultationChat',
  data() {
    return {
      sessionId: null,
      otherName: '对方',
      messages: [],
      inputMessage: '',
      sessionKey: null,
      pollTimer: null
    }
  },
  async created() {
    this.sessionId = parseInt(this.$route.params.sessionId)
    if (!this.sessionId) {
      this.$message.error('无效会话')
      this.$router.back()
      return
    }

    // 获取会话详情，确定对方姓名
    try {
      const sessionRes = await getSessionById(this.sessionId)
      if (sessionRes.code === 200) {
        const session = sessionRes.data
        const currentUserId = parseInt(this.$store.getters.accountId)
        console.log('session.userId:', session.userId, typeof session.userId)
        console.log('store accountId:', this.$store.getters.accountId, typeof this.$store.getters.accountId)
        console.log('相等吗？', session.userId === currentUserId)

        if (session.userId === currentUserId) {
          // 当前用户是学生，对方是咨询师
          this.otherName = session.counselorName || '咨询师'
        } else {
          // 当前用户是咨询师，对方是学生
          this.otherName = session.userName || '学生'
        }
      }
    } catch (e) {
      console.warn('获取会话详情失败:', e)
      this.otherName = '对方'
    }

    // 模拟会话密钥（实际应从前端安全存储中获取）
    this.sessionKey = 'x'.repeat(32)

    if (!this.sessionKey || this.sessionKey.length !== 32) {
      this.$message.error('会话密钥无效')
      return
    }

    await this.loadMessages()
    this.startPolling()
  },
  beforeDestroy() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer)
    }
  },
  methods: {
    async loadMessages() {
      const res = await getMessages(this.sessionId)
      if (res.code === 200) {
        const currentUserId = Number(this.$store.getters.accountId)
        const decryptedMessages = []

        for (const msg of res.data || []) {
          let decryptedContent = '[解密中...]'
          try {
            if (this.sessionKey) {
              decryptedContent = await decryptAESGCM(msg.encryptedContent, this.sessionKey)
            } else {
              decryptedContent = '[无密钥]'
            }
          } catch (e) {
            console.warn('解密失败:', e.message)
            const preview = msg.encryptedContent && msg.encryptedContent.substring(0, 20) || ''
            decryptedContent = '[解密失败] ' + preview + '...'
          }

          decryptedMessages.push({
            ...msg,
            isSelf: Number(msg.senderId) === currentUserId,
            decryptedContent
            // 注意：模板中直接用 otherName，无需存 senderName
          })
        }

        this.messages = decryptedMessages
        this.scrollToBottom()

        console.log('【聊天消息】', this.messages)
        console.log('当前用户 ID:', this.$store.getters.userId)
      }
    },

    async sendMessage() {
      if (!this.inputMessage.trim()) return
      if (!this.sessionKey) {
        this.$message.error('未获取会话密钥')
        return
      }

      try {
        const encrypted = await encryptAESGCM(this.inputMessage, this.sessionKey)
        await sendMessage(this.sessionId, encrypted)
        this.inputMessage = ''
        await this.loadMessages()
      } catch (error) {
        console.error('加密/发送失败:', error)
        this.$message.error('发送失败：' + (error.message || '未知错误'))
      }
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const box = this.$refs.messageBox
        if (box) {
          box.scrollTop = box.scrollHeight
        }
      })
    },

    startPolling() {
      this.pollTimer = setInterval(() => {
        this.loadMessages()
      }, 3000)
    },

    formatTime(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return '无效时间'
      const pad = n => n.toString().padStart(2, '0')
      return `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    }
  }
}
</script>