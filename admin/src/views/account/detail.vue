<template>
  <div class="app-container">
    <el-form
      v-loading.body="loading"
      :model="tmpAccount"
      :rules="updateDetailRules"
      ref="tmpAccount"
      label-width="115px"
    >
      <el-row :gutter="18">
        <el-col :span="9">
          <el-form-item label="账户名" prop="name">
            <el-input v-if="toUpdate" v-model="tmpAccount.name" />
            <span v-else>{{ tmpAccount.name }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="9">
          <el-form-item label="邮箱" prop="email">
            <el-input v-if="toUpdate" v-model="tmpAccount.email" />
            <span v-else>{{ tmpAccount.email }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :span="9">
          <el-form-item label="注册时间">
            <span>{{ formatTime(account.registerTime) }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="9">
          <el-form-item label="最后登录时间">
            <span>{{ account.loginTime ? formatTime(account.loginTime) : '从未登录' }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-row :gutter="18">
          <el-col :span="6">
            <el-button type="success" :loading="btnLoading" @click="regainAccountDetail">重新获取信息</el-button>
          </el-col>

          <el-col :span="6" v-if="!toUpdate">
            <el-button type="primary" :loading="btnLoading" @click="toUpdate = true">修改信息</el-button>
          </el-col>
          <el-col :span="6" v-else>
            <el-button type="primary" :loading="btnLoading" @click="updateDetail">确认修改</el-button>
            <el-button type="warning" @click="cancelEdit">取消修改</el-button>
          </el-col>
          
          <el-col :span="6">
            <el-button type="danger" @click="showUpdatePasswordDialog">修改密码</el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <el-dialog title="修改密码" :visible.sync="dialogFormVisible" @close="resetPasswordForm">
      <el-form
        status-icon
        label-position="left"
        label-width="115px"
        style="width: 400px; margin-left: 50px;"
        :model="tmpPassword"
        :rules="updatePasswordRules"
        ref="tmpPassword"
      >
        <el-form-item label="旧密码" prop="oldPassword" required>
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            placeholder="请输入旧密码"
            v-model="tmpPassword.oldPassword"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword" required>
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            placeholder="请输入新密码"
            v-model="tmpPassword.newPassword"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" required>
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            placeholder="请再次输入新密码"
            v-model="tmpPassword.confirmPassword"
            show-password
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="danger" @click="$refs['tmpPassword'].resetFields()">重置</el-button>
        <el-button type="primary" :loading="btnLoading" @click="updatePassword">更新</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { unix2CurrentTime } from '@/utils'
// ✅ 修正导入：使用正确的 API
import { updateProfile, changePassword } from '@/api/account'

export default {
  name: 'AccountDetail',
  data() {
    const validateName = (rule, value, callback) => {
      if (!value || value.length < 3) {
        callback(new Error('账户名长度必须 ≥ 3'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!emailRegex.test(value)) {
        callback(new Error('邮箱格式错误'))
      } else {
        callback()
      }
    }
    const validateNewPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度必须 ≥ 6'))
      } else if (value === this.tmpPassword.oldPassword) {
        callback(new Error('新旧密码不能相同'))
      } else {
        callback()
      }
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.tmpPassword.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      btnLoading: false,
      dialogFormVisible: false,
      toUpdate: false,
      tmpAccount: {
        name: '',
        email: ''
      },
      tmpPassword: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      updateDetailRules: {
        name: [{ required: true, trigger: 'blur', validator: validateName }],
        email: [{ required: true, trigger: 'blur', validator: validateEmail }]
      },
      updatePasswordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
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
    this.setDetail()
  },
  methods: {
    formatTime(time) {
      return unix2CurrentTime(time)
    },
    setDetail() {
      this.tmpAccount.name = this.account.name
      this.tmpAccount.email = this.account.email
    },
    cancelEdit() {
      this.toUpdate = false
      this.setDetail() // 回滚
    },
    regainAccountDetail() {
      this.loading = true
      this.$store.dispatch('Detail').finally(() => {
        this.loading = false
      })
    },
    // ✅ 使用 updateProfile（无需 id）
    updateDetail() {
      this.$refs.tmpAccount.validate(valid => {
        if (valid) {
          this.btnLoading = true
          updateProfile({
            name: this.tmpAccount.name,
            email: this.tmpAccount.email
          })
            .then(res => {
              this.$store.commit('SET_TOKEN', res.data)
              this.$message.success('资料更新成功')
              this.toUpdate = false
              return this.$store.dispatch('Detail')
            })
            .catch(err => {
              console.error('更新失败:', err)
              this.$message.error('更新失败')
            })
            .finally(() => {
              this.btnLoading = false
            })
        }
      })
    },
    showUpdatePasswordDialog() {
      this.dialogFormVisible = true
      this.tmpPassword = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    },
    resetPasswordForm() {
      if (this.$refs.tmpPassword) {
        this.$refs.tmpPassword.resetFields()
      }
    },
    // ✅ 使用 changePassword
    updatePassword() {
      this.$refs.tmpPassword.validate(valid => {
        if (valid) {
          this.btnLoading = true
          changePassword({
            oldPassword: this.tmpPassword.oldPassword,
            newPassword: this.tmpPassword.newPassword
          })
            .then(res => {
              this.$store.commit('SET_TOKEN', res.data)
              this.$message.success('密码修改成功')
              this.dialogFormVisible = false
              return this.$store.dispatch('Detail')
            })
            .catch(err => {
              console.error('密码修改失败:', err)
              this.$message.error('旧密码错误或修改失败')
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