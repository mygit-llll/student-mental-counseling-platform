import router from './router'
import store from './store'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/token'

const whiteList = ['/login', '/register']

router.beforeEach(async(to, from, next) => {
  NProgress.start()

  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      // 如果已登录，访问 login 则跳首页
      next({ path: '/dashboard/index' })
    } else {
      // 检查是否已获取用户角色
      if (store.getters.roleName) {
        // 已获取，直接放行
        next()
      } else {
        try {
          // 未获取，先拉取用户信息
          const response = await store.dispatch('Detail')
          // 动态生成路由
          await store.dispatch('GenerateRoutes', response.data)
          // ⚠️ 注意：addRoutes 已废弃，但如果你用 Vue Router 3 可继续用
          router.addRoutes(store.getters.addRouters)
          // ✅ 关键：用 next(to) 重新进入当前路由（触发新路由匹配）
          next({ ...to, replace: true }) // 或直接 next()
        } catch (error) {
          // 获取用户信息失败，清除 token 并跳转登录
          await store.dispatch('LogOut')
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // 无 token
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
