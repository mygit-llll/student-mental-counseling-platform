// src/store/modules/account.js
import { login, logout, detail, uploadPublicKey } from '@/api/account'
import { getToken, setToken, removeToken, parseUserIdFromToken } from '@/utils/token'
import { uploadPublicKeyIfNeeded } from '@/utils/e2ee'

const account = {
  state: {
    token: getToken(),
    accountId: -1,
    email: null,
    name: null,
    loginTime: -1,
    registerTime: -1,
    roleName: null,
    publicKey: null,
    permissionCodeList: []
    // ✅ 已删除 sessionKeys
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
      setToken(token)
      const accountId = parseUserIdFromToken(token)
      if (accountId !== null) {
        state.accountId = accountId
      }
    },
    SET_ACCOUNT: (state, account) => {
      state.accountId = account.id
      state.email = account.email
      state.name = account.name
      state.loginTime = account.loginTime
      state.registerTime = account.registerTime
      state.roleName = account.roleName
      state.publicKey = account.publicKey || null
      state.permissionCodeList = account.permissionCodeList
    },
    RESET_ACCOUNT: (state) => {
      state.token = null
      state.accountId = -1
      state.email = null
      state.name = null
      state.loginTime = -1
      state.registerTime = -1
      state.roleName = null
      state.publicKey = null
      state.permissionCodeList = []
    }
  },

  actions: {
    Login({ commit }, loginForm) {
      return new Promise((resolve, reject) => {
        login(loginForm).then(response => {
          if (response.code === 200) {
            setToken(response.data)
            commit('SET_TOKEN', response.data)
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    Detail({ commit, state }) {
      return new Promise(async(resolve, reject) => {
        try {
          const response = await detail()
          commit('SET_ACCOUNT', response.data)

          try {
            await uploadPublicKeyIfNeeded(uploadPublicKey)
          } catch (err) {
            console.error('公钥上传失败:', err)
          }

          resolve(response)
        } catch (error) {
          reject(error)
        }
      })
    },

    Logout({ commit }) {
      return new Promise((resolve, reject) => {
        logout().then(() => {
          commit('RESET_ACCOUNT')
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    FedLogout({ commit }) {
      return new Promise(resolve => {
        commit('RESET_ACCOUNT')
        removeToken()
        resolve()
      })
    }
  },

  getters: {
    // ✅ 已删除 getSessionKey
  }
}

export default account
