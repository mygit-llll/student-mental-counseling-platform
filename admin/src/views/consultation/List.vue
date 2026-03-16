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
          @click="startSession(counselor.id)"
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

export default {
  name: 'ConsultationList',
  data() {
    return {
      counselors: [],
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  async created() {
    await this.loadCounselors()
  },
  methods: {
    async loadCounselors() {
      const res = await getCounselors()
      if (res.code === 200) {
        this.counselors = res.data || []
      } else {
        this.$message.error('加载咨询师失败')
      }
    },

    async startSession(counselorId) {
      // TODO: 实际应生成 AES 会话密钥，并用 counselor 的公钥加密
      // 此处先模拟一个加密后的密钥字符串
      const mockEncryptedKey = 'enc_K_' + Date.now() + '_simulated_key'

      try {
        const res = await createSession(counselorId, mockEncryptedKey)
        if (res.code === 200) {
          this.$message.success('会话创建成功')
          this.$router.push(`/consultation/chat/${res.data}`)
        } else {
          this.$message.error(res.message || '创建会话失败')
        }
      } catch (error) {
        console.error('创建会话异常:', error)
        this.$message.error('网络错误，请重试')
      }
    }
  }
}
</script>