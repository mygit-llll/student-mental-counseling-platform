<template>
  <div class="publish-container">
    <el-card>
      <div slot="header">
        <span style="font-size: 18px; font-weight: bold;">📝 发布心理健康文章</span>
      </div>

      <el-form
        ref="articleForm"
        :model="article"
        :rules="rules"
        label-width="80px"
        label-position="left"
      >
        <!-- 标题 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="article.title" placeholder="请输入文章标题" maxlength="100" show-word-limit />
        </el-form-item>

        <!-- 分类 -->
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="article.categoryId" placeholder="请选择文章分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>

        <!-- 内容 -->
        <el-form-item label="内容" prop="content">
          <quill-editor
            v-model="article.content"
            :options="editorOption"
            style="height: 300px;"
          />
        </el-form-item>

        <!-- 图片上传 -->
        <el-form-item label="封面/配图">
          <el-upload
            action="/api/upload"
            :auto-upload="true"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :on-preview="handlePictureCardPreview"
            :on-exceed="handleExceed"
            :before-upload="beforeUpload"
            :file-list="fileList"
            list-type="picture-card"
            :limit="5"
            multiple
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="Preview" />
          </el-dialog>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交审核</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { quillEditor } from 'vue-quill-editor'
import 'quill/dist/quill.snow.css'
import { getCategoryList } from '@/api/category'
import { createArticle } from '@/api/knowledge'

export default {
  name: 'KnowledgePublish',
  components: {
    quillEditor
  },
  data() {
    return {
      submitLoading: false,
      article: {
        title: '',
        content: '',
        categoryId: null,
        images: '' // 后端要求是 JSON 字符串
      },
      categories: [],
      fileList: [], // 存储已上传的文件（含 url）
      dialogImageUrl: '',
      dialogVisible: false,
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { max: 100, message: '标题不能超过100字', trigger: 'blur' }
        ],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
      },
      editorOption: {
        placeholder: '请输入文章内容...',
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            ['blockquote', 'code-block'],
            [{ list: 'ordered' }, { list: 'bullet' }],
            [{ script: 'sub' }, { script: 'super' }],
            [{ indent: '-1' }, { indent: '+1' }],
            [{ direction: 'rtl' }],
            [{ size: ['small', false, 'large', 'huge'] }],
            [{ header: [1, 2, 3, 4, 5, 6, false] }],
            [{ color: [] }, { background: [] }],
            [{ font: [] }],
            [{ align: [] }],
            ['clean'],
            ['link', 'image', 'video']
          ]
        }
      },
      uploadHeaders: {
        Authorization: 'Bearer ' + localStorage.getItem('token')
      }
    }
  },
  async created() {
    await this.fetchCategories()
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
        this.$message.error('加载分类失败')
      }
    },

    // 图片上传成功回调
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        // 关键：将服务器返回的 URL 赋给 file.url，用于预览和提交
        file.url = response.data
        this.fileList = fileList // 同步状态（可选但推荐）
      } else {
        this.$message.error(response.msg || '上传失败')
        // 移除上传失败的文件
        this.fileList = fileList.filter(f => f.uid !== file.uid)
      }
    },

    // 删除图片
    handleRemove(file, fileList) {
      this.fileList = fileList
    },

    // 预览图片
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },

    // 超出数量限制
    handleExceed(files, fileList) {
      this.$message.warning('最多只能上传5张图片')
    },

    // 上传前校验
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
      }
      return isImage && isLt5M
    },

    // 提交文章
    async handleSubmit() {
      this.$refs.articleForm.validate(async valid => {
        if (!valid) return

        // 提取所有已上传图片的 URL
        const imageUrls = this.fileList
          .map(file => file.url)
          .filter(url => url) // 过滤空值

        if (imageUrls.length > 5) {
          this.$message.error('图片最多5张')
          return
        }

        this.submitLoading = true
        try {
          const articleData = {
            ...this.article,
            images: imageUrls.length ? JSON.stringify(imageUrls) : ''
          }

          await createArticle(articleData)
          this.$message.success('提交成功，等待审核！')
          this.$router.push('/dashboard')
        } catch (error) {
          console.error('发布失败:', error)
          this.$message.error('发布失败，请重试')
        } finally {
          this.submitLoading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.publish-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}
::v-deep .ql-editor {
  min-height: 200px;
}
</style>