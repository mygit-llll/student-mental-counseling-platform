<template>
  <div class="audit-container">
    <el-card shadow="never" style="margin-bottom: 20px;">
      <div slot="header">
        <span style="font-weight: bold;">文章审核</span>
        <el-button size="mini" @click="goBack">
            <i class="el-icon-arrow-left"></i> 返回
        </el-button>
      </div>

      <!-- 搜索与筛选 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="关键词" clearable @keyup.enter.native="fetchData" />
        </el-form-item>

        <el-form-item label="作者ID">
          <el-input v-model.number="searchForm.authorId" placeholder="作者ID" clearable @keyup.enter.native="fetchData" />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px;">
            <el-option :value="1" label="待审核" />
            <el-option :value="2" label="已发布" />
            <el-option :value="3" label="已驳回" />
            <el-option :value="4" label="已删除" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 文章列表 -->
    <el-table
      :data="tableData"
      v-loading="loading"
      element-loading-text="加载中..."
      border
      style="width: 100%;"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200">
        <template slot-scope="scope">
          <el-link type="primary" @click="goToDetail(scope.row.id)">{{ scope.row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="authorName" label="作者" width="120" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="success">已发布</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="danger">已驳回</el-tag>
          <el-tag v-else-if="scope.row.status === 4" type="info">已删除</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template slot-scope="scope">
          {{ scope.row.createTime | formatDate }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status === 1"
            size="mini"
            type="success"
            @click="handleAudit(scope.row.id, 2)"
          >
            通过
          </el-button>
          <el-button
            v-if="scope.row.status === 1"
            size="mini"
            type="danger"
            @click="handleAudit(scope.row.id, 3)"
          >
            驳回
          </el-button>
          <span v-else>—</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; text-align: right;">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="searchForm.page"
        :page-sizes="[10, 20, 50]"
        :page-size="searchForm.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>
  </div>
</template>

<script>
import { getAdminArticleList, auditArticle } from '@/api/knowledge'

export default {
  name: 'KnowledgeAudit',
  filters: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  },
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      searchForm: {
        page: 1,
        size: 10,
        title: '',
        authorId: null,
        status: 1 // 默认只看“待审核”
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.searchForm.page,
          size: this.searchForm.size,
          title: this.searchForm.title || undefined,
          authorId: this.searchForm.authorId || undefined,
          status: this.searchForm.status !== null ? this.searchForm.status : undefined
        }

        const res = await getAdminArticleList(params)
        if (res.code === 200) {
          this.tableData = res.data.list || []
          this.total = res.data.total || 0
        } else {
          this.$message.error(res.message || '获取数据失败')
        }
      } catch (error) {
        console.error('审核列表加载失败:', error)
        this.$message.error('加载失败，请重试')
      } finally {
        this.loading = false
      }
    },

    handleAudit(id, result) {
      this.$confirm(`确定要${result === 2 ? '通过' : '驳回'}该文章吗？`, '审核确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: result === 2 ? 'success' : 'warning'
      }).then(async() => {
        try {
          await auditArticle(id, result)
          this.$message.success('操作成功')
          this.fetchData() // 刷新列表
        } catch (error) {
          this.$message.error('操作失败')
        }
      }).catch(() => {})
    },

    goToDetail(id) {
      this.$router.push({
        path: `/knowledge/detail/${id}`,
        query: { from: 'admin' }
      })
    },

    resetSearch() {
      this.searchForm = {
        page: 1,
        size: 10,
        title: '',
        authorId: null,
        status: 1
      }
      this.fetchData()
    },

    handleSizeChange(val) {
      this.searchForm.size = val
      this.fetchData()
    },

    handleCurrentChange(val) {
      this.searchForm.page = val
      this.fetchData()
    },
    goBack() {
      this.$router.push('/dashboard')
    }
  }
}
</script>

<style scoped>
.audit-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}
</style>
