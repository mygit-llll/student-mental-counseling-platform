<template>
  <div class="take-test-container" v-if="test">
    <el-page-header @back="$router.go(-1)" :content="`答题：${test.title}`" />

    <el-card style="margin-top: 20px;">
      <div class="test-description" v-if="test.description" style="margin-bottom: 24px; color: #666;">
        {{ test.description }}
      </div>

      <el-form ref="testForm" :model="answers" label-position="top">
        <div v-for="question in test.questions" :key="question.id" class="question-item" style="margin-bottom: 32px;">
          <h3 style="font-size: 18px; margin-bottom: 12px;">
            {{ question.sortOrder }}. {{ question.content }}
          </h3>
          <div v-for="(option, index) in parseOptions(question.options)" :key="index" class="option-item">
            <el-radio
              v-model="answers[question.id]"
              :label="getLabelByIndex(index)"
              border
              style="margin-right: 8px;"
            />
            <span class="option-text">{{ getLabelByIndex(index) }}. {{ option }}</span>
          </div>
        </div>
      </el-form>

      <div style="text-align: center; margin-top: 30px;">
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
          提交答卷
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getPublicTestDetail, submitTest } from '@/api/psychological'

export default {
  name: 'TakeTest',
  data() {
    return {
      testId: null,
      test: null,
      answers: {}, // key: questionId (number), value: "A"/"B"/"C"/"D"
      submitting: false
    }
  },
  async created() {
    this.testId = Number(this.$route.params.id)
    if (this.testId) {
      await this.fetchTest()
    } else {
      this.$message.error('缺少测试ID')
      this.$router.back()
    }
  },
  methods: {
    async fetchTest() {
      try {
        const res = await getPublicTestDetail(this.testId)
        if (res.code === 200) {
          this.test = res.data
          // 初始化 answers 对象
          this.answers = {}
          this.test.questions.forEach(q => {
            this.$set(this.answers, q.id, '') // 使用 $set 确保响应式
          })
        } else {
          this.$message.error(res.message || '加载失败')
          this.$router.back()
        }
      } catch (error) {
        console.error('获取测试失败:', error)
        this.$message.error('加载失败，请重试')
        this.$router.back()
      }
    },

    parseOptions(options) {
      try {
        return JSON.parse(options) || []
      } catch (e) {
        console.warn('解析 options 失败:', options)
        return Array.isArray(options) ? options : []
      }
    },

    getLabelByIndex(index) {
      // 0 -> A, 1 -> B, 2 -> C, 3 -> D ...
      return String.fromCharCode(65 + index)
    },

    validateAnswers() {
      for (const question of this.test.questions) {
        if (!this.answers[question.id]) {
          this.$message.warning(`请完成第 ${question.sortOrder} 题`)
          return false
        }
      }
      return true
    },

    async handleSubmit() {
      if (!this.validateAnswers()) return

      this.submitting = true
      try {
        // 构造提交数据：questionId 必须是字符串（后端 Map<String, String>）
        const submitData = {
          testId: this.testId,
          answers: {}
        }

        for (const [qid, answer] of Object.entries(this.answers)) {
          submitData.answers[qid] = answer // qid 是字符串，answer 是 "A"/"B"...
        }

        const res = await submitTest(submitData)
        if (res.code === 200) {
          this.$message.success('提交成功！')
          this.$router.push('/psychological/result?testId=' + this.testId)
        } else {
          this.$message.error(res.message || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        this.$message.error('提交失败，请重试')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.take-test-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.question-item h3 {
  font-weight: 600;
}

.el-radio {
  padding: 12px 20px !important;
  border-radius: 6px !important;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 8px;
}

.option-text {
  font-size: 14px;
  color: #333;
}
</style>