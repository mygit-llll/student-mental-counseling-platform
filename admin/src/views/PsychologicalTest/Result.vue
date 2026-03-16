<!-- Result.vue -->
<template>
  <div class="result-container" v-if="result">
    <el-page-header @back="$router.push('/psychological/test')" content="测试结果" />

    <el-card style="margin-top: 20px;">
      <h2 style="text-align: center; margin-bottom: 24px;">{{ testTitle }}</h2>

      <!-- 分数 -->
      <div class="result-item">
        <label>总分：</label>
        <span class="score">{{ result.totalScore }} 分</span>
      </div>

      <!-- 测试时间 -->
      <div class="result-item">
        <label>完成时间：</label>
        <span>{{ formatTime(this.result.createdAt) }}</span>
      </div>

      <!-- 结果解读 -->
      <div class="result-item" style="margin-top: 24px;">
        <label>结果解读：</label>
        <div class="interpretation" v-html="result.resultText"></div>
      </div>

      <!-- 历史记录入口 -->
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
      testId: null,
      result: null,
      testTitle: ''
    }
  },
  async created() {
    this.testId = Number(this.$route.query.testId)
    if (!this.testId) {
      this.$message.error('缺少测试ID')
      this.$router.back()
      return
    }
    await this.fetchResult()
  },
  methods: {
    async fetchResult() {
      try {
        const res = await getTestResult(this.testId)
        if (res.code === 200) {
          this.result = res.data
          // 如果后端没返回标题，可以额外请求一次 test 信息
          this.testTitle = res.data.testTitle || '心理测试结果'
        } else {
          this.$message.error(res.message || '加载结果失败')
          this.$router.back()
        }
      } catch (error) {
        console.error('获取结果失败:', error)
        this.$message.error('加载失败，请重试')
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