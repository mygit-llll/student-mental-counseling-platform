<template>
  <div class="login-container">
    <el-form
      class="card-box login-form"
      autocomplete="on"
      :model="loginForm"
      :rules="loginRules"
      ref="loginForm"
      status-icon
      label-position="left"
      label-width="0px"
    >
      <h3 class="title">大学生心理健康平台登录</h3>
      
      <!-- 角色选择 -->
      <el-form-item prop="roleId">
      <el-select v-model="loginForm.roleId" placeholder="请选择角色" style="width:100%">
        <el-option
          v-for="role in roleOptions"
          :key="role.id"
          :label="role.name"
          :value="role.id"
        />
      </el-select>
    </el-form-item>
      
      <el-form-item prop="nameOrEmail">
        <span class="svg-container svg-container_login">
          <icon-svg icon-class="name" />
        </span>
        

        <el-input
          type="text"
          autocomplete="on"
          v-model="loginForm.nameOrEmail"
          placeholder="请输入账户名或邮箱"
          @keyup.enter="handleLogin"
        />
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <icon-svg icon-class="password" />
        </span>
        <el-input
          :type="passwordType"
          autocomplete="on"
          v-model="loginForm.password"
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
        />
        <span class="show-pwd" @click.prevent="showPwd">
          <icon-svg icon-class="eye" />
        </span>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          style="width:100%;"
          :loading="btnLoading"
          @click.prevent="handleLogin"
        >登录</el-button>
        <div style="text-align: center; margin-top: 15px;">
          <span class="prompt-text">还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from 'axios'
import { isValidateEmail } from '@/utils/validate'

export default {
  name: 'login',
  data() {
    const validateNameOrEmail = (rule, value, callback) => {
      if (value.length < 3) {
        callback(new Error('账户名长度必须在3或以上!!!'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码长度必须在6或以上!!!'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      loginForm: {
        nameOrEmail: 'admin',
        password: 'admin123',
        roleId: null
      },
      loginRules: {
        nameOrEmail: [
          { required: true, trigger: 'blur', validator: validateNameOrEmail }
        ],
        password: [
          { required: true, trigger: 'blur', validator: validatePassword }
        ],
        roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
      },
      passwordType: 'password',
      btnLoading: false,
      roleOptions: []
    }
  },
  async created() {
    try {
      const res = await axios.get('/public/roles')
      this.roleOptions = res.data.data || []
      // 如果第一个角色是默认选中（可选）
      if (this.roleOptions.length > 0) {
        this.loginForm.roleId = this.roleOptions[0].id
      }
    } catch (error) {
      console.error('加载角色失败', error)
      this.$message.error('无法加载角色列表')
    }
  },
  methods: {
    showPwd() {
      this.passwordType =
        this.passwordType === 'password' ? '' : (this.passwordType = 'password')
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          const account = {}
          if (isValidateEmail(this.loginForm.nameOrEmail)) {
            account.email = this.loginForm.nameOrEmail
          } else {
            account.name = this.loginForm.nameOrEmail
          }
          account.password = this.loginForm.password
          // 添加角色信息（如果需要传递给后端）
          account.roleId = this.loginForm.roleId
          this.loading = true
          this.$store.dispatch('Login', account).then(() => {
            localStorage.setItem('USER_ROLE_ID', this.loginForm.roleId)
            this.loading = false
            this.$router.push({ path: '/dashboard' })
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
@import '../../../src/styles/mixin.scss';

$bg: url('../../assets/LoginImages/login.png') no-repeat center center fixed;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  @include relative;
  height: 100vh;
  overflow-y: hidden;

  background: $bg;
  background-size: cover;

  input:-webkit-autofill {
    -webkit-box-shadow: 0 0 0 1000px #293444 inset !important;
    -webkit-text-fill-color: #fff !important;
  }
  input {
    background: transparent;
    border: 0;
    -webkit-appearance: none;
    -moz-appearance: none;    
    appearance: none;    
    border-radius: 0;
    padding: 12px 5px 12px 15px;
    color: $light_gray;
    height: 47px;
  }
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;
    input {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
    border-radius: 5px;
    padding: 12px 15px;
    color: #fff;
    font-size: 14px;
    height: 47px;
    transition: all 0.3s ease;
  }
  }
  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;
  }
  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
    &_login {
      font-size: 20px;
    }
  }
  .title {
    font-size: 30px;
    color: rgba(62, 39, 125, 0.636);
    margin: 0 auto 40px auto;
    text-align: center;
    font-weight: bold;
  }
  .login-form {
    position: absolute;
    left: 0;
    right: 0;
    width: 400px;
    padding: 35px 35px 15px 35px;
    margin: 120px auto;
  }
  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
  }
  .register-link {
    color: #409eff;
    text-decoration: none;
    font-size: 14px;
  }
  .register-link:hover {
    text-decoration: underline;
  }
  // 角色选择框样式
.el-select {
  width: 100%;
  .el-input__inner {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    color: #ffffff;
    font-size: 14px;
    padding: 12px 15px;
    height: 47px;
    border-radius: 5px;
    transition: all 0.3s ease;
  }
  .el-input__suffix {
    color: #ccc;
    }
  }
  .prompt-text {
  color: #ffffff; 
  font-size: 14px;
}
}
</style>
