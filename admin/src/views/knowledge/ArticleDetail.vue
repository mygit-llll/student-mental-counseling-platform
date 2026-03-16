<template>
  <div class="article-detail-container" v-if="article">
    <el-page-header @back="$router.go(-1)" content="文章详情" />

    <el-card style="margin-top: 20px;">
      <h1 style="font-size: 24px; margin-bottom: 16px;">{{ article.title }}</h1>

      <div class="meta" style="color: #999; margin-bottom: 20px; font-size: 14px;">
        <i class="el-icon-time"></i> {{ article.createTime | formatDate }}
        <span v-if="article.authorName" style="margin-left: 10px;">
          <i class="el-icon-user"></i> {{ article.authorName }}
        </span>
        <el-tag size="mini" style="float: right;" v-if="article.categoryName">
          {{ article.categoryName }}
        </el-tag>
      </div>

      <!-- 渲染富文本 + 自动追加 images 中的图片 -->
      <div
        class="article-content"
        v-html="finalContent"
        style="line-height: 1.8; color: #333;"
      ></div>

      <!-- 管理员可见：显示状态 -->
      <div v-if="$route.query.from === 'admin'" style="margin-top: 16px; color: #666;">
        <strong>状态：</strong>
        <el-tag v-if="article.status === 1" type="warning">待审核</el-tag>
        <el-tag v-else-if="article.status === 2" type="success">已发布</el-tag>
        <el-tag v-else-if="article.status === 3" type="danger">已驳回</el-tag>
        <el-tag v-else-if="article.status === 4" type="info">已删除</el-tag>
      </div>

      <!-- 删除按钮：仅作者或管理员可见 -->
      <div v-if="canDelete" style="margin-top: 20px; text-align: right;">
        <el-button type="danger" size="small" @click="handleDelete" :loading="deleting">
          删除文章
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getPublicArticleById, getAdminArticleDetail, deleteArticle } from '@/api/knowledge'

export default {
  name: 'ArticleDetail',
  data() {
    return {
      article: null,
      deleting: false
    }
  },
  computed: {
    canDelete() {
      if (!this.article || !this.currentUser) return false

      const isAuthor = this.article.authorId === this.currentUser.accountId
      const isAdmin = this.currentUser.roles.includes(1)

      return isAuthor || isAdmin
    },
    currentUser() {
      const userRoleId = Number(localStorage.getItem('USER_ROLE_ID'))
      const userId = Number(localStorage.getItem('USER_ID'))

      return {
        accountId: userId,
        roles: [userRoleId]
      }
    },
    finalContent() {
      if (!this.article) return ''

      let html = this.article.content || ''

      if (this.article.images) {
        try {
          const imageList = JSON.parse(this.article.images)
          if (Array.isArray(imageList)) {
            imageList.forEach(path => {
              const filename = path.split('/').pop()
              const baseUrl = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'
              const fullUrl = `${baseUrl}/uploads/${filename}`
              html += `<img src="${fullUrl}" style="max-width:100%; height:auto; margin:10px 0;" />`
            })
          }
        } catch (e) {
          console.warn('解析 images 失败:', e)
        }
      }

      return html
    }
  },
  async created() {
    const id = this.$route.params.id
    const fromAdmin = this.$route.query.from === 'admin'

    if (id) {
      await this.fetchArticle(id, fromAdmin)
    }
  },
  methods: {
    async fetchArticle(id, isAdmin = false) {
      try {
        let res
        if (isAdmin) {
          res = await getAdminArticleDetail(id)
        } else {
          res = await getPublicArticleById(id)
        }

        if (res.code === 200) {
          this.article = res.data
        } else {
          this.$message.error(res.message || '文章不存在')
          this.$router.back()
        }
      } catch (error) {
        console.error('获取文章详情失败:', error)
        this.$message.error('加载失败')
        this.$router.back()
      }
    },
    async handleDelete() {
      try {
        await this.$confirm('确定要删除这篇文章吗？删除后不可恢复', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.deleting = true
        await deleteArticle(this.article.id)
        this.$message.success('删除成功')
        this.$router.go(-1)
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败：' + (error.message || '未知错误'))
        }
      } finally {
        this.deleting = false
      }
    }
  },
  filters: {
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.article-detail-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.article-content img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 16px 0;
}
</style>