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
import { getMessages, sendMessage, getSessionById, getEncryptedSessionKey } from '@/api/consultation'
import { decryptAESGCM } from '@/utils/aes-gcm'
// 私钥解密工具
import { importPrivateKey, decryptWithPrivateKey } from '@/utils/rsa-decrypt'

export default {
  name: 'ConsultationChat',
  data() {
    return {
      sessionId: null,
      otherName: '对方',
      messages: [],
      inputMessage: '',
      sessionKey: null, // 明文 AES 密钥（256-bit）
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

    // 获取会话详情（用于显示对方姓名）
    let session = null
    try {
      const sessionRes = await getSessionById(this.sessionId)
      if (sessionRes.code !== 200) {
        throw new Error('获取会话失败')
      }
      session = sessionRes.data

      const currentUserId = parseInt(this.$store.getters.accountId)
      if (session.userId === currentUserId) {
        this.otherName = session.counselorName || '咨询师'
      } else {
        this.otherName = session.userName || '学生'
      }
    } catch (e) {
      console.warn('获取会话详情失败:', e)
      this.$message.error('无法加载会话信息')
      this.$router.push('/consultation/list')
      return
    }

    // === 统一：所有用户都通过私钥解密会话密钥 ===
    let key = null
    try {
      // 1. 从后端获取加密的会话密钥（Base64 字符串）
      const encryptedKeyRes = await getEncryptedSessionKey(this.sessionId)
      if (encryptedKeyRes.code !== 200 || !encryptedKeyRes.data) {
        throw new Error('未获取到加密的会话密钥')
      }
      const encryptedAesKeyB64 = encryptedKeyRes.data

      // 2. 从 localStorage 获取当前用户的 RSA 私钥
      const storedKeysStr = localStorage.getItem('user_rsa_keypair')
      if (!storedKeysStr) {
        throw new Error('本地私钥缺失，请重新登录')
      }
      const storedKeys = JSON.parse(storedKeysStr)
      if (!storedKeys.privateKey) {
        throw new Error('私钥数据损坏')
      }

      // 3. 导入私钥并解密 AES 会话密钥
      const privateKey = await importPrivateKey(storedKeys.privateKey)
      const aesKeyBytes = await decryptWithPrivateKey(encryptedAesKeyB64, privateKey)
      // 验证长度（必须是 32 字节 = 256 位
      if (aesKeyBytes.length !== 32) {
        throw new Error(`解密后的密钥长度无效：${aesKeyBytes.length} 字节（应为 32）`)
      }
      key = String.fromCharCode(...aesKeyBytes)
      console.log('密钥类型:', typeof key)
      console.log('密钥长度:', key.length)
      console.log('前5字节 char codes:', key.split('').slice(0, 5).map(c => c.charCodeAt(0)))
    } catch (err) {
      console.error('解密会话密钥失败:', err)
      this.$message.error('初始化加密会话失败：' + (err.message || '请重试'))
      this.$router.push('/consultation/list')
      return
    }

    this.sessionKey = key
    await this.loadMessages()
    this.startPolling()
  },
  beforeDestroy() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer)
    }
    // 注意：不再需要 CLEAR_SESSION_KEY，因为没存 Vuex
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
            const preview = (msg.encryptedContent && msg.encryptedContent.substring(0, 20)) || ''
            decryptedContent = `[解密失败] ${preview}...`
          }

          decryptedMessages.push({
            ...msg,
            isSelf: Number(msg.senderId) === currentUserId,
            decryptedContent
          })
        }

        this.messages = decryptedMessages
        this.scrollToBottom()
      }
    },

    async sendMessage() {
      if (!this.inputMessage.trim()) return
      if (!this.sessionKey) {
        this.$message.error('未获取会话密钥')
        return
      }

      try {
        // 注意：这里需要引入 encryptAESGCM
        const { encryptAESGCM } = await import('@/utils/aes-gcm')
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