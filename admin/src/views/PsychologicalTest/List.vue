<template>
  <div class="test-list-container" style="padding: 20px;">
    <h2 style="margin-bottom: 20px;">心理测试列表</h2>
    
    <el-card v-for="test in tests" :key="test.id" style="margin-bottom: 16px;">
      <h3>{{ test.name }}</h3>
      <p v-if="test.description" style="color: #666; margin: 8px 0;">{{ test.description }}</p>
      
      <el-button 
        type="primary" 
        size="small"
        @click="$router.push(`/psychological/take/${test.id}`)"
      >
        开始测试
      </el-button>
    </el-card>

    <div v-if="tests.length === 0" style="text-align: center; color: #999; margin-top: 40px;">
      暂无心理测试
    </div>
  </div>
</template>

<script>
import { getPublicTests } from '@/api/psychological'

export default {
  name: 'PsychologicalTestList',
  data() {
    return {
      tests: []
    }
  },
  async created() {
    try {
      const res = await getPublicTests()
      if (res.code === 200) {
        this.tests = res.data || []
      } else {
        this.$message.error('加载测试列表失败')
      }
    } catch (error) {
      console.error('获取测试列表失败:', error)
      this.$message.error('网络错误，请重试')
    }
  }
}
</script>