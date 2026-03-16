import Vue from 'vue'
import Router from 'vue-router'
import Layout from '../views/layout/Layout'

const _import = require('./_import_' + process.env.NODE_ENV)

Vue.use(Router)

export const constantRouterMap = [
  { path: '/login', component: _import('login/index'), hidden: true },
  { path: '/404', component: _import('errorPage/404'), hidden: true },
  { path: '/401', component: _import('errorPage/401'), hidden: true },
  { path: '/register', component: _import('register/index'), hidden: true, meta: { noAuth: true }},

  // 发布文章（隐藏，通常由按钮跳转）
  {
    path: '/knowledge/publish',
    name: 'KnowledgePublish',
    component: _import('knowledge/Publish'),
    hidden: true,
    meta: { title: '发布文章' }
  },

  // 文章详情（动态路由，必须隐藏）
  {
    path: '/knowledge/detail/:id',
    component: _import('knowledge/ArticleDetail'),
    name: 'ArticleDetail',
    meta: { title: '文章详情', noCache: true },
    hidden: true
  },

  // 文章审核（可选是否显示，此处按需保留）
  {
    path: '/knowledge/audit',
    component: _import('knowledge/Audit'),
    hidden: true,
    meta: { title: '文章审核' }
  },

  // === 顶级菜单开始 ===

  // 心理健康知识（Dashboard）
  {
    path: '/dashboard',
    component: Layout,
    redirect: '/dashboard/index',
    icon: 'dashboard',
    noDropDown: true,
    children: [{
      path: 'index',
      name: '心理健康知识',
      component: _import('dashboard/index'),
      meta: { title: '心理健康知识', noCache: true }
    }]
  },

  // 账户管理
  {
    path: '/account',
    component: Layout,
    redirect: '/account/list',
    icon: 'name',
    noDropDown: true,
    children: [{
      path: 'list',
      name: '账户管理',
      component: _import('account/list'),
      meta: { title: '账户管理', permission: ['account:list'] }
    }]
  },

  // 角色管理
  {
    path: '/role',
    component: Layout,
    redirect: '/role/list',
    icon: 'role',
    noDropDown: true,
    children: [{
      path: 'list',
      name: '角色管理',
      component: _import('role/list'),
      meta: { title: '角色管理', permission: ['role:list'] }
    }]
  },

  // 心理测试（带子菜单）
  {
    path: '/psychological',
    name: '心理测试',
    component: Layout,
    redirect: '/psychological/test',
    icon: 'name',
    meta: { title: '心理测试' },
    children: [
      {
        path: 'test',
        name: '心理测试列表',
        component: _import('PsychologicalTest/List'),
        meta: { title: '心理测试' }
      },
      {
        path: 'history',
        name: '测试记录',
        component: _import('PsychologicalTest/History'),
        meta: { title: '测试记录' }
      },
      {
        path: 'result',
        name: 'PsychologicalTestResult',
        component: _import('PsychologicalTest/Result'),
        hidden: true
      }
    ]
  },

  // 在线咨询（带子菜单）
  {
    path: '/consultation',
    name: '咨询',
    component: Layout,
    redirect: '/consultation/list',
    icon: 'name',
    meta: { title: '在线咨询' },
    children: [
      {
        path: 'list',
        name: '在线咨询',
        component: _import('consultation/List'),
        meta: { title: '在线咨询' }
      },
      {
        path: 'history',
        name: '咨询记录',
        component: _import('consultation/History'),
        meta: { title: '咨询记录' }
      },
      {
        path: 'chat/:sessionId',
        name: 'ConsultationChat',
        component: _import('consultation/Chat'),
        hidden: true,
        meta: { title: '聊天窗口', noCache: true }
      }
    ]
  },

  // 个人中心（顶级菜单）
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    icon: 'name',
    noDropDown: true,
    children: [
      {
        path: 'index',
        component: _import('profile/index'),
        name: '个人中心',
        meta: { title: '个人中心', noCache: true }
      }
    ]
  },

  // 答题页（隐藏）
  {
    path: '/psychological/take/:id',
    component: _import('PsychologicalTest/TakeTest'),
    hidden: true,
    meta: { requiresAuth: true }
  }
]

export const asyncRouterMap = []

export default new Router({
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})
