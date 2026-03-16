import { login, logout, detail } from '@/api/account'
import { getToken, setToken, removeToken, parseUserIdFromToken } from '@/utils/token'
import { ensureKeyPairExists } from '@/utils/keypair-manager'
import { uploadPublicKey } from '@/api/account'

import Vue from 'vue'

const account = {
  state: {
    token: getToken(),
    accountId: -1,
    email: null,
    name: null,
    loginTime: -1,
    registerTime: -1,
    roleName: null,
    // 用于判断是否已上传公钥（避免重复上传）
    publicKey: null,
    permissionCodeList: [],
    sessionKeys: {}
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
      // 👇 保存 publicKey 到 state
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
      state.publicKey = null // 👈 重置
      state.permissionCodeList = []
      state.sessionKeys = {}
    },
    SET_SESSION_KEY(state, { sessionId, key }) {
      Vue.set(state.sessionKeys, sessionId, key)
    },
    CLEAR_SESSION_KEY(state, sessionId) {
      if (state.sessionKeys[sessionId]) {
        Vue.delete(state.sessionKeys, sessionId)
      }
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

    //  重点修改：Detail action
    Detail({ commit, state }) {
      return new Promise(async(resolve, reject) => {
        try {
          const response = await detail()
          commit('SET_ACCOUNT', response.data)

          // 判断是否为咨询师（根据你的后端返回字段调整！）
          // 常见情况：roleName 是 "咨询师"，或 roleId === 2
          const isCounselor = response.data.roleName === '咨询师' ||
             (response.data.roleId && response.data.roleId === 2) ||
             (response.data.roleId && response.data.roleId === '2')

          if (isCounselor) {
            console.log('检测到当前用户是咨询师，正在初始化加密密钥...')

            try {
              const keyPair = await ensureKeyPairExists()

              // 如果后端返回的 publicKey 为空，说明还没上传过
              if (!response.data.publicKey || response.data.publicKey.trim() === '') {
                console.log('本地存在密钥对，但服务器无公钥，正在上传...')
                await uploadPublicKey(keyPair.publicKey)
                console.log('公钥上传成功！')
              }
            } catch (err) {
              console.error('咨询师密钥初始化失败:', err)
              // 不阻塞主流程，但可提示（谨慎：避免暴露安全细节）
            }
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
    getSessionKey: (state) => (sessionId) => {
      return state.sessionKeys[sessionId] || null
    }
  }
}

export default account

