<template>
  <div class="dashboard-container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>心理健康知识</h2>
      <!-- 权限控制：仅特定角色显示 -->
      <el-button
        v-if="canPublish"
        type="primary"
        size="small"
        icon="el-icon-edit-outline"
        @click="$router.push('/knowledge/publish')"
      >
        我要发布
      </el-button>
      <el-button
        v-if="isAdmin"
        type="success"
        size="small"
        icon="el-icon-s-check"
        @click="$router.push('/knowledge/audit')"
        style="margin-left: 10px;"
      >
        文章审核
      </el-button>
    </div>

    <!-- 分类筛选栏 -->
    <div style="margin-bottom: 20px;">
      <el-tag
        :key="0"
        :type="selectedCategory === null ? 'primary' : ''"
        size="medium"
        style="margin-right: 10px; cursor: pointer;"
        @click="filterByCategory(null)"
      >
        全部
      </el-tag>
      <el-tag
        v-for="category in categories"
        :key="category.id"
        :type="selectedCategory === category.id ? 'primary' : ''"
        size="medium"
        style="margin-right: 10px; cursor: pointer;"
        @click="filterByCategory(category.id)"
      >
        {{ category.name }}
      </el-tag>
    </div>

    <!-- 搜索栏 -->
    <div style="margin: 15px 0;">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索心理健康知识..."
        style="width: 300px;"
        clearable
        @input="applyFilters"
      >
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </div>

    <!-- 文章列表 -->
    <div
      v-for="article in articles"
      :key="article.id"
      style="cursor: pointer; margin-bottom: 15px;"
      @click="goToDetail(article.id)"
    >
      <el-card>
        <div slot="header" class="clearfix">
          <span style="font-weight: bold; font-size: 18px;">{{ article.title }}</span>
          <el-tag size="mini" style="float: right;" v-if="article.categoryName">{{ article.categoryName }}</el-tag>
        </div>
        <p style="color: #666; line-height: 1.6;">
          {{ article.content | truncate(100) }}
        </p>
        <div style="text-align: right; margin-top: 10px; color: #999;">
          <i class="el-icon-time"></i> {{ article.createTime | formatDate }}
          <span v-if="article.authorName" style="margin-left: 10px;">
            <i class="el-icon-user"></i> {{ article.authorName }}
          </span>
        </div>
      </el-card>
    </div>

    <!-- 无数据提示 -->
    <div v-if="articles.length === 0" style="text-align: center; color: #999; margin-top: 40px;">
      暂无{{ selectedCategoryName }}心理健康知识
    </div>
  </div>
</template>

<script>
import { getPublicArticleList } from '@/api/knowledge'
import { getCategoryList } from '@/api/category'

export default {
  name: 'Dashboard',
  data() {
    return {
      allArticles: [],
      articles: [],
      categories: [],
      selectedCategory: null,
      searchKeyword: '',
      page: 1,
      size: 10,
      total: 0
    }
  },
  computed: {
    selectedCategoryName() {
      if (this.selectedCategory === null) return ''
      const cat = this.categories.find(c => c.id === this.selectedCategory)
      return cat ? cat.name : ''
    },
    canPublish() {
      const roleId = Number(localStorage.getItem('USER_ROLE_ID'))
      return [1, 2].includes(roleId)
    },
    isAdmin() {
      const roleId = Number(localStorage.getItem('USER_ROLE_ID'))
      return roleId === 1
    }
  },
  filters: {
    truncate(value, length) {
      if (!value) return ''
      return value.length > length ? value.substring(0, length) + '...' : value
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
  },
  async created() {
    await this.fetchCategories()
    await this.fetchAllArticles()
  },
  methods: {
    async fetchCategories() {
      try {
        const res = await getCategoryList()
        if (res.code === 200) {
          this.categories = res.data || []
        }
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    },
    async fetchAllArticles() {
      try {
        const res = await getPublicArticleList(1, 1000)
        if (res.code === 200) {
          this.allArticles = res.data.list || []
          this.applyFilters()
        }
      } catch (error) {
        console.error('获取文章失败:', error)
        this.$message.error('加载知识列表失败')
      }
    },
    filterByCategory(categoryId) {
      this.selectedCategory = categoryId
      this.applyFilters()
    },
    handleSearch() {
      this.page = 1 // 搜索时回到第一页
      this.applyFilters()
    },
    applyFilters() {
      let filtered = this.allArticles

      // 1. 分类筛选
      if (this.selectedCategory !== null) {
        filtered = filtered.filter(article => article.categoryId === this.selectedCategory)
      }

      // 2. 关键词搜索（标题或内容）
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.trim().toLowerCase()
        filtered = filtered.filter(article =>
          (article.title && article.title.toLowerCase().includes(keyword)) ||
          (article.content && article.content.toLowerCase().includes(keyword))
        )
      }

      // 3. 更新显示列表（不分页，直接全量显示）
      this.articles = filtered
      this.total = filtered.length
    },
    goToDetail(id) {
      this.$router.push(`/knowledge/detail/${id}`)
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}
</style>