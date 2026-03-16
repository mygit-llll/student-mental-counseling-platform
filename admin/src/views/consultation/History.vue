<template>
  <div class="consultation-history" style="padding: 20px;">
    <h2 style="margin-bottom: 20px;">我的咨询会话</h2>

    <el-table :data="sessions" style="width: 100%;">
      <!-- 会话对象列 -->
      <el-table-column label="会话对象" width="180">
        <template #default="{ row }">
          <div style="display: flex; align-items: center;">
            <el-avatar
              :src="defaultAvatar"
              size="small"
              style="margin-right: 8px;"
            />
            {{ row.otherName || '未知用户' }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="statusText" label="状态" width="100">
        <template #default="{ row }">
          <el-tag
            :type="row.status === 1 ? 'success' : 'info'"
            size="small"
          >
            {{ row.statusText || (row.status === 1 ? '进行中' : '已结束') }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="最后消息" min-width="200">
        <template #default="{ row }">
          <span v-if="row.lastMessage && typeof row.lastMessage === 'string' && row.lastMessage.trim()">
            {{ truncateText(row.lastMessage, 30) }}
          </span>
          <span v-else style="color: #999;">
            暂无消息
          </span>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createdAt) || '未知时间' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="$router.push(`/consultation/chat/${row.id}`)"
            :disabled="!row.id"
          >
            进入聊天
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="sessions.length === 0" style="text-align: center; color: #999; margin-top: 40px;">
      暂无会话记录
    </div>
  </div>
</template>

<script>
import { getMySessions } from '@/api/consultation'

export default {
  name: 'ConsultationHistory',
  data() {
    return {
      sessions: [],
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
  },
  async created() {
    await this.loadSessions()
  },
  methods: {
    async loadSessions() {
      try {
        const res = await getMySessions()
        if (res.code === 200) {
          this.sessions = (res.data || []).map(s => ({
            ...s,
            statusText: s.status === 1 ? '进行中' : '已结束',
            lastMessage: s.lastMessage || ''
          }))
        } else {
          this.$message.warning(res.message || '获取会话列表失败')
        }
      } catch (error) {
        console.error('加载会话失败:', error)
        this.$message.error('加载会话失败，请稍后重试')
      }
    },

    truncateText(text, maxLength) {
      if (!text) return ''
      const clean = text.trim()
      if (clean.length <= maxLength) return clean
      return clean.substring(0, maxLength) + '...'
    },

    formatTime(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return ''
      const pad = n => n.toString().padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    }
  }
}
</script>

<style scoped>
.consultation-history ::v-deep .el-table th,
.consultation-history ::v-deep .el-table td {
  padding: 12px 0;
}
</style>