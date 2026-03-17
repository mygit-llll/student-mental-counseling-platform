<template>
  <div class="consultation-list" style="padding: 20px;">
    <h2 style="margin-bottom: 20px;">选择咨询师</h2>

    <el-card v-for="counselor in counselors" :key="counselor.id" style="margin-bottom: 16px;">
      <div style="display: flex; align-items: center;">
        <el-avatar :src="counselor.avatar || defaultAvatar" :size="60" style="margin-right: 16px;" />
        <div>
          <h3>{{ counselor.name }}</h3>
          <p v-if="counselor.title" style="color: #666; margin: 4px 0;">{{ counselor.title }}</p>
          <p v-if="counselor.introduction" style="color: #999; font-size: 14px; margin: 4px 0;">
            {{ counselor.introduction }}
          </p>
        </div>
      </div>

      <div style="text-align: right; margin-top: 12px;">
        <el-button
          type="primary"
          size="small"
          @click="startSession(counselor)"
          :loading="startingSessionId === counselor.id"
        >
          开始咨询
        </el-button>
      </div>
    </el-card>

    <div v-if="counselors.length === 0" style="text-align: center; color: #999; margin-top: 40px;">
      暂无可选咨询师
    </div>
  </div>
</template>

<script>
import { getCounselors, createSession } from '@/api/consultation'
import { ensureKeyPairExists, importPublicKey, encryptWithPublicKey } from '@/utils/e2ee'

export default {
  name: 'ConsultationList',
  data() {
    return {
      counselors: [],
      startingSessionId: null,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  async created() {
    await this.loadCounselors()
  },
  methods: {
    async loadCounselors() {
      try {
        const res = await getCounselors()
        if (res.code === 200) {
          this.counselors = res.data || []
        } else {
          this.$message.error('加载咨询师失败')
        }
      } catch (err) {
        console.error('加载咨询师异常:', err)
        this.$message.error('网络错误，请重试')
      }
    },

    async startSession(counselor) {
      this.startingSessionId = counselor.id
      try {
        // 1. 确保本地有密钥对（也会触发上传公钥）
        const myKeyPair = await ensureKeyPairExists()

        // 2. 检查对方公钥
        if (!counselor.publicKey || !myKeyPair.publicKey) {
          this.$message.error('公钥缺失，无法建立安全会话')
          return
        }

        // 3. 生成会话 AES 密钥 (32 bytes = 256 bits)
        const aesKey = window.crypto.getRandomValues(new Uint8Array(32))

        // 4. 加密给咨询师
        const counselorPubKey = await importPublicKey(counselor.publicKey)
        const encryptedForCounselor = await encryptWithPublicKey(aesKey, counselorPubKey)

        // 5. 加密给自己（学生）
        const myPubKey = await importPublicKey(myKeyPair.publicKey)
        const encryptedForStudent = await encryptWithPublicKey(aesKey, myPubKey)

        // 6. 发送给后端（注意：传 Base64 字符串）
        const payload = {
          counselorId: counselor.id,
          encryptedKeyForStudent: arrayBufferToBase64(encryptedForStudent),
          encryptedKeyForCounselor: arrayBufferToBase64(encryptedForCounselor)
        }

        const res = await createSession(payload)
        if (res.code !== 200) {
          throw new Error(res.message || '创建会话失败')
        }

        this.$message.success('安全会话已创建')
        const sessionId = res.data
        this.$router.push(`/consultation/chat/${sessionId}`)
      } catch (error) {
        console.error('创建会话异常:', error)
        this.$message.error('创建失败：' + (error.message || '请重试'))
      } finally {
        this.startingSessionId = null
      }
    }
  }
}

// 工具函数：ArrayBuffer → Base64（用于传输）
function arrayBufferToBase64(buffer) {
  let binary = ''
  const bytes = new Uint8Array(buffer)
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}
</script>