<!-- src/views/PsychologicalTest/Result.vue -->
<template>
  <div class="result-container" v-if="result">
    <el-page-header @back="$router.push('/psychological/test')" content="测试结果" />

    <el-card style="margin-top: 20px;">
      <h2 style="text-align: center; margin-bottom: 24px;">
        {{ result.testTitle || '心理测试结果' }}
      </h2>

      <div class="result-item">
        <label>总分：</label>
        <span class="score">{{ result.totalScore }} 分</span>
      </div>

      <div class="result-item">
        <label>完成时间：</label>
        <span>{{ formatTime(result.createdAt) }}</span>
      </div>

      <div class="result-item" style="margin-top: 24px;">
        <label>结果解读：</label>
        <div class="interpretation" v-html="result.resultText"></div>
      </div>

      <div style="text-align: center; margin-top: 30px;">
        <el-button @click="$router.push('/psychological/history')">
          查看我的历史记录
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getTestResult } from '@/api/psychological'

export default {
  name: 'PsychologicalTestResult',
  data() {
    return {
      result: null
    }
  },
  async created() {
    const { testId } = this.$route.query

    if (!testId) {
      this.$message.error('缺少测试ID')
      this.$router.back()
      return
    }

    await this.fetchResultByTestId(Number(testId))
  },
  methods: {
    async fetchResultByTestId(testId) {
      try {
        const res = await getTestResult(testId)
        if (res.code === 200) {
          this.result = res.data
        } else {
          throw new Error(res.message || '加载失败')
        }
      } catch (error) {
        console.error('加载结果失败:', error)
        this.$message.error(error.message || '加载失败')
        this.$router.back()
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

<style scoped>
/* 样式保持不变 */
.result-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.result-item {
  display: flex;
  margin-bottom: 16px;
}

.result-item label {
  width: 80px;
  font-weight: bold;
  color: #333;
}

.score {
  color: #e6a23c;
  font-size: 18px;
  font-weight: bold;
}

.interpretation {
  flex: 1;
  line-height: 1.6;
  color: #666;
}
</style>