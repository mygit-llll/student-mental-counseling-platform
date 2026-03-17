<template>
  <div class="high-risk-monitor">
    <el-card>
      <div slot="header">
        <span>高危心理测评监控（分数 > 30）</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="fetchData"
        >
          刷新
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        style="width: 100%"
        empty-text="暂无高危测评记录"
      >
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />

        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.username" size="small">{{ scope.row.username }}</el-tag>
            <span v-else class="text-gray">已注销</span>
          </template>
        </el-table-column>

        <el-table-column prop="totalScore" label="总分" width="80">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.totalScore >= 40">极高危</el-tag>
            <el-tag type="warning" v-else-if="scope.row.totalScore >= 30">高危</el-tag>
            {{ scope.row.totalScore }}
          </template>
        </el-table-column>

        <el-table-column prop="resultText" label="评估结果" min-width="200" />

        <el-table-column prop="createdAt" label="测评时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button
              size="mini"
              type="primary"
              @click="viewUser(scope.row.userId)"
            >
              查看用户
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getHighRiskTests } from '@/api/admin/monitor'

export default {
  name: 'HighRiskMonitor',
  data() {
    return {
      loading: false,
      tableData: []
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getHighRiskTests({ threshold: 30 })
        // 如果后端返回 { code: 200, data: [...] }
        this.tableData = res.data || []
      } catch (error) {
        this.$message.error('获取高危记录失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },

    viewUser(userId) {
      if (!userId) {
        this.$message.warning('该用户已注销，无法查看')
        return
      }
      this.$router.push(`/admin/user/${userId}`)
    }
  }
}
</script>

<style scoped>
.high-risk-monitor {
  padding: 20px;
}
</style>