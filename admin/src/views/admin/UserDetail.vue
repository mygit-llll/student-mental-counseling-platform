<!-- src/views/admin/UserDetail.vue -->
<template>
  <div class="user-detail">
    <el-page-header @back="$router.go(-1)" content="用户详情" />
    <el-card style="margin-top: 20px;">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user.name }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ user.roleName || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatTime(user.registerTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="最后登录">
          {{ user.loginTime ? formatTime(user.loginTime) : '从未登录' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script>
import { getAccountById } from '@/api/account'

export default {
  name: 'UserDetail',
  data() {
    return {
      user: {}
    }
  },
  async created() {
    const idStr = this.$route.params.id
    if (!/^\d+$/.test(idStr)) {
      this.$message.error('无效的用户ID')
      this.$router.back()
      return
    }

    await this.fetchUser(Number(idStr))
  },
  methods: {
    async fetchUser(userId) {
      try {
        const res = await getAccountById(userId)
        if (res.code === 200) {
          this.user = res.data
        } else {
          throw new Error(res.message || '用户不存在')
        }
      } catch (error) {
        console.error('加载用户失败:', error)
        this.$message.error(error.message || '加载失败')
        this.$router.back()
      }
    },
    formatTime(timestamp) {
      if (!timestamp) return ''
      const d = new Date(timestamp) // 注意：现在假设是毫秒时间戳（如 1773768479000）
      return d.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.user-detail {
  padding: 20px;
}
</style>