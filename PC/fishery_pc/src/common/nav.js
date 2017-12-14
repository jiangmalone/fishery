import dynamic from 'dva/dynamic';

// wrapper of dynamic
const dynamicWrapper = (app, models, component) => dynamic({
  app,
  models: () => models.map(m => import(`../models/${m}.js`)),
  component,
});

// nav data
export const getNavData = app => [
  {
    component: dynamicWrapper(app, ['user', 'login'], () => import('../layouts/BasicLayout')),
    layout: 'BasicLayout',
    name: '首页', // for breadcrumb
    path: '/',
    children: [
      {
        name: '首页',
        icon: 'home',
        path: 'analysis',
        component: dynamicWrapper(app, ['chart'], () => import('../routes/Dashboard/Analysis')),
       
      },
      {
        name: '用户管理',
        path: 'userManage',
        icon: 'userManagement',
        children: [
          {
            name: '普通用户',
            path: 'basic-form',
            component: dynamicWrapper(app, ['form'], () => import('../routes/Forms/BasicForm')),
          },
          {
            name: '企业用户',
            path: 'company-user',
            component: dynamicWrapper(app, ['form'], () => import('../routes/CompanyUser/CompanyUserList')),
            children: [
              {
                path: 'confirm',
                component: dynamicWrapper(app, ['form'], () => import('../routes/Forms/StepForm/Step2')),
              }
            ],
          }
        ],
      },
      {
        name: '设备查询',
        path: 'basic',
        icon: 'form',
        component: dynamicWrapper(app, ['profile'], () => import('../routes/Profile/BasicProfile')),

      },
      {
        name: '账户管理',
        path: 'advanced-form',
        icon: 'form',
        component: dynamicWrapper(app, ['form'], () => import('../routes/Forms/AdvancedForm')),

      },
      {
        // name: '设备相关跳转',
        path: 'equipment',
        children: [
          {
            // name: '设备管理',
            path: 'equipment-management',
            component: dynamicWrapper(app, ['form'], () => import('../routes/Equipment/EquipmentManagement')),
          },
          {
            // name: '设备详情',
            path: 'equipment-detail',
            component: dynamicWrapper(app, ['form'], () => import('../routes/Equipment/EquipmentDetail')),
            
          }
        ],
      },
    ],
  },
  {
    component: dynamicWrapper(app, [], () => import('../layouts/UserLayout')),
    path: '/user',
    layout: 'UserLayout',
    children: [
      {
        // name: '帐户',
        // icon: 'user',
        path: 'user',
        children: [
          {
            // name: '登录',
            path: 'login',
            component: dynamicWrapper(app, ['login'], () => import('../routes/User/Login')),
          },
          {
            // name: '注册',
            path: 'register',
            component: dynamicWrapper(app, ['register'], () => import('../routes/User/Register')),
          },
          {
            // name: '注册结果',
            path: 'register-result',
            component: dynamicWrapper(app, [], () => import('../routes/User/RegisterResult')),
          },
        ],
      },
    ],
  }
];
