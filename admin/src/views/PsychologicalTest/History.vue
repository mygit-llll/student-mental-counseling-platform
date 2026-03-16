<!-- src/views/PsychologicalTest/History.vue -->
<template>
  <div class="history-container" style="padding: 20px;">
    <h2 style="margin-bottom: 20px;">我的测试记录</h2>

    <el-table :data="historyList" style="width: 100%;">
      <el-table-column prop="testTitle" label="测试名称" />
      <el-table-column prop="totalScore" label="得分" width="100">
        <template #default="{ row }">
          <span style="color: #e6a23c; font-weight: bold;">{{ row.totalScore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            type="text"
            size="small"
            @click="$router.push(`/psychological/result?testId=${row.testId}`)"
          >
            查看结果
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getMyTestHistory } from '@/api/psychological'

export default {
  name: 'PsychologicalTestHistory',
  data() {
    return {
      historyList: []
    }
  },
  async created() {
    await this.loadHistory()
  },
  methods: {
    async loadHistory() {
      const res = await getMyTestHistory()
      if (res.code === 200) {
        this.historyList = res.data || []
      }
    },
    formatTime(dateStr) {
      const d = new Date(dateStr)
      const pad = n => n.toString().padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    }
  }
}
</script>