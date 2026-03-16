<template>
  <div class="register-container">
    <el-form
      ref="registerForm"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
      autocomplete="off"
    >
      <h3 class="title">用户注册</h3>

      <!-- 用户名 -->
      <el-form-item prop="name">
        <el-input
          v-model="registerForm.name"
          placeholder="用户名（3-20位字母、数字或下划线）"
          prefix-icon="el-icon-user"
          name="name"
        />
      </el-form-item>

      <!-- 邮箱 -->
      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          placeholder="邮箱"
          prefix-icon="el-icon-message"
          name="email"
          type="email"
        />
      </el-form-item>

      <!-- 密码 -->
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          placeholder="密码（至少6位）"
          prefix-icon="el-icon-lock"
          name="password"
          show-password
        />
      </el-form-item>

      <!-- 注册按钮 -->
      <el-form-item>
        <el-button
          type="primary"
          style="width: 100%"
          :loading="loading"
          @click.native.prevent="handleRegister"
        >
          {{ loading ? '注册中...' : '注册' }}
        </el-button>
      </el-form-item>

      <!-- 已有账号？去登录 -->
      <div class="login-link">
        已有账号？
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </el-form>
  </div>
</template>

<script>
// 局部导入 axios
import axios from 'axios'

export default {
  name: 'Register',
  data() {
    // 自定义用户名校验规则
    const validateName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (!/^[a-zA-Z0-9_]{3,20}$/.test(value)) {
        callback(new Error('用户名只能包含字母、数字、下划线，长度3-20'))
      } else {
        callback()
      }
    }

    // 自定义密码校验
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码不能少于6位'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      registerForm: {
        name: '',
        email: '',
        password: ''
      },
      registerRules: {
        name: [{ validator: validateName, trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [{ validator: validatePassword, trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          // 使用 axios 发送请求
          axios
            .post('/public/register', this.registerForm)
            .then(res => {
              this.loading = false
              if (res.data.code === 200) {
                this.$message.success(res.data.message || '注册成功！')
                setTimeout(() => {
                  this.$router.push('/login')
                }, 1000)
              } else {
                this.$message.error(res.data.message || '注册失败')
              }
            })
            .catch(err => {
              this.loading = false
              console.error('注册请求失败:', err)
              this.$message.error('网络错误，请稍后重试')
            })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('../../assets/LoginImages/login.png') no-repeat center center;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-form {
  padding: 30px 40px;
  width: 400px;
  max-width: 90%;
  background: rgba(255, 255, 255, 0.85);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);

  .title {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
    font-size: 24px;
    font-weight: bold;
  }

  .el-input {
    height: 40px;
    ::v-deep .el-input__inner {
      height: 40px;
      line-height: 40px;
    }
  }

  .el-button {
    height: 44px;
    font-size: 16px;
  }
}

.login-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #666;
}

.link {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.link:hover {
  text-decoration: underline;
}
</style>