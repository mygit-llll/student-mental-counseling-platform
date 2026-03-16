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
import { importPublicKey, encryptWithPublicKey } from '@/utils/rsa'

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

    async startSession(counselor) {
      try {
        // 1. 检查 counselor 是否有公钥
        if (!counselor.publicKey) {
          this.$message.error(`咨询师 ${counselor.name} 未配置公钥`)
          return
        }

        // 2. 生成 32 字节 AES 会话密钥
        const aesKey = window.crypto.getRandomValues(new Uint8Array(32))

        // 3. 导入 counselor 公钥
        const publicKey = await importPublicKey(counselor.publicKey)

        // 4. 用公钥加密 AES 密钥
        const encryptedAesKey = await encryptWithPublicKey(aesKey, publicKey)

        // 5. 调用后端创建会话
        const res = await createSession(counselor.id, encryptedAesKey)
        if (res.code !== 200) {
          throw new Error(res.message || '创建会话失败')
        }

        // 6.  将原始 AES 密钥存入 Vuex（临时）
        this.$store.commit('SET_SESSION_KEY', {
          sessionId: res.data,
          key: Array.from(aesKey).map(b => String.fromCharCode(b)).join('')
        })

        // 7. 跳转到聊天页
        this.$message.success('会话创建成功')
        this.$router.push(`/consultation/chat/${res.data}`)
      } catch (error) {
        console.error('创建会话异常:', error)
        this.$message.error('创建失败：' + (error.message || '请重试'))
      }
    }
  }
}
</script>