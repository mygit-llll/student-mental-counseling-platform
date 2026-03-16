<template>
  <div class="app-container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">个人中心</span>
      </div>

      <el-form
        v-loading.body="loading"
        :model="form"
        :rules="rules"
        ref="profileForm"
        label-width="100px"
        size="medium"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="name">
          <el-input
            v-if="isEditing"
            v-model="form.name"
            placeholder="请输入用户名"
            clearable
          />
          <span v-else>{{ form.name }}</span>
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-if="isEditing"
            v-model="form.email"
            placeholder="请输入邮箱"
            clearable
          />
          <span v-else>{{ form.email }}</span>
        </el-form-item>

        <!-- 注册时间 -->
        <el-form-item label="注册时间">
          <span>{{ formatTime(form.registerTime) }}</span>
        </el-form-item>

        <!-- 最后登录时间 -->
        <el-form-item label="最后登录">
          <span>{{ form.loginTime ? formatTime(form.loginTime) : '从未登录' }}</span>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button
            v-if="!isEditing"
            type="primary"
            icon="el-icon-edit"
            @click="startEdit"
          >编辑资料</el-button>
          <el-button
            v-else
            type="success"
            icon="el-icon-check"
            :loading="btnLoading"
            @click="submitEdit"
          >保存</el-button>
          <el-button
            v-if="isEditing"
            @click="cancelEdit"
          >取消</el-button>
          <el-button
            type="danger"
            icon="el-icon-key"
            @click="showPasswordDialog"
            style="margin-left: 20px;"
          >修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码弹窗 -->
    <el-dialog
      title="修改密码"
      :visible.sync="passwordDialogVisible"
      width="420px"
      @close="resetPasswordForm"
    >
      <el-form
        ref="passwordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="80px"
        status-icon
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            type="password"
            v-model="passwordForm.oldPassword"
            show-password
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            type="password"
            v-model="passwordForm.newPassword"
            show-password
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            type="password"
            v-model="passwordForm.confirmPassword"
            show-password
            autocomplete="off"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="submitPasswordChange"
        >确认修改</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { unix2CurrentTime } from '@/utils'
import { isValidateEmail } from '@/utils/validate'
import { updateProfile, changePassword } from '@/api/account'

export default {
  name: 'Profile',
  data() {
    // 校验函数
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!isValidateEmail(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }

    const validateName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (value.length < 3) {
        callback(new Error('用户名至少3个字符'))
      } else {
        callback()
      }
    }

    // ✅ 修正：仅校验格式，不验证密码正确性
    const validateOldPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入旧密码'))
      } else if (value.length < 6) {
        callback(new Error('旧密码至少6位'))
      } else {
        // ✅ 不再调用 validatePassword！
        callback()
      }
    }

    const validateNewPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('新密码至少6位'))
      } else if (value === this.passwordForm.oldPassword) {
        callback(new Error('新旧密码不能相同'))
      } else {
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      btnLoading: false,
      isEditing: false,
      passwordDialogVisible: false,
      form: {
        id: '',
        name: '',
        email: '',
        registerTime: null,
        loginTime: null
      },
      rules: {
        name: [{ validator: validateName, trigger: 'blur' }],
        email: [{ validator: validateEmail, trigger: 'blur' }]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ validator: validateOldPassword, trigger: 'blur' }],
        newPassword: [{ validator: validateNewPassword, trigger: 'blur' }],
        confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapState({
      account: state => state.account
    })
  },
  created() {
    this.loadProfile()
  },
  methods: {
    formatTime(time) {
      return unix2CurrentTime(time)
    },
    loadProfile() {
      this.loading = true
      this.$store
        .dispatch('Detail')
        .then(() => {
          this.form.id = this.account.accountId
          this.form.name = this.account.name
          this.form.email = this.account.email
          this.form.registerTime = this.account.registerTime
          this.form.loginTime = this.account.loginTime
        })
        .finally(() => {
          this.loading = false
        })
    },
    startEdit() {
      this.isEditing = true
    },
    cancelEdit() {
      this.isEditing = false
      this.loadProfile() // 回滚
    },
    submitEdit() {
      this.$refs.profileForm.validate(valid => {
        if (valid) {
          this.btnLoading = true
          updateProfile({
            id: this.form.id,
            name: this.form.name,
            email: this.form.email
          }).then(res => {
            this.$store.commit('SET_TOKEN', res.data)
            this.$message.success('资料更新成功')
            this.isEditing = false
            return this.$store.dispatch('Detail')
          }).catch(err => {
            console.error('更新失败:', err)
            this.$message.error('资料更新失败')
          }).finally(() => {
            this.btnLoading = false
          })
        }
      })
    },
    showPasswordDialog() {
      this.passwordDialogVisible = true
    },
    resetPasswordForm() {
      if (this.$refs.passwordForm) {
        this.$refs.passwordForm.resetFields()
      }
    },
    submitPasswordChange() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.btnLoading = true
          changePassword({
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }).then(res => {
            this.$message.success('密码修改成功，请重新登录')
            this.passwordDialogVisible = false
            return this.$store.dispatch('FedLogout')
          }).then(() => {
            this.$router.push('/login')
          })
            .catch(err => {
              let errorMsg = '修改失败，请稍后重试'
              if (err.response) {
                const { data, status } = err.response
                if (status === 400 || status === 200) {
                  if (data && typeof data === 'object' && data.message) {
                    errorMsg = data.message
                  } else if (typeof data === 'string') {
                    errorMsg = data
                  }
                } else if (status === 401) {
                  errorMsg = '登录已过期，请重新登录'
                }
              }
              this.$message.error(errorMsg)
            })
            .finally(() => {
              this.btnLoading = false
            })
        }
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.box-card {
  max-width: 700px;
  margin: 0 auto;
}
</style>