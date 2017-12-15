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
        icon: 'team',
        children: [
          {
            name: '普通用户',
            path: 'common-user',
            // 
            children: [
              {
                // name: '普通用户',
                path: '',
                component: dynamicWrapper(app, ['commonuser'], () => import('../routes/CommonUser/Index')),
              },
              {
                // name: '普通用户',
                path: ':id',
                component: dynamicWrapper(app, ['commonuser'], () => import('../routes/CommonUser/UserInfo')),
              },
            ]
          },
          {
            name: '企业用户',
            path: 'company-user',
            component: dynamicWrapper(app, ['form'], () => import('../routes/CompanyUser/CompanyUserList')),
<<<<<<< HEAD
            // children: [
            //   {
            //     name: '企业用户',
            //     path: 'company-user-detail',
            //     component: dynamicWrapper(app, ['form'], () => import('../routes/CompanyUser/CompanyUserDetail')),
            //   },
            // ],
          },
          {
            // name: '企业用户',
            path: 'company-user-detail',
            component: dynamicWrapper(app, ['form'], () => import('../routes/CompanyUser/CompanyUserDetail')),
          },
=======
            children: [
              {
                path: 'confirm',
                component: dynamicWrapper(app, ['form'], () => import('../routes/Forms/StepForm/Step2')),
              }
            ],
          },
          {
            name: '',
            path: 'pond',
            component: dynamicWrapper(app, ['form'], () => import('../routes/CompanyUser/CompanyUserList')),
            children: [
              {
                path: 'confirm',
                component: dynamicWrapper(app, ['form'], () => import('../routes/Forms/StepForm/Step2')),
              }
            ],
          }
>>>>>>> b733e56efa6345ec0a5ab3ef40cc3728dc4cf652
        ],
      },
      {
        name: '设备查询',
        path: 'basic',
        icon: 'bars',
        component: dynamicWrapper(app, ['profile'], () => import('../routes/Profile/BasicProfile')),

      },
      {
        name: '账户管理',
        path: 'advanced-form',
        icon: 'user',
        component: dynamicWrapper(app, ['form'], () => import('../routes/Forms/AdvancedForm')),

      },
<<<<<<< HEAD
      {
        // name: '设备相关跳转',
        path: 'equipment',
        children: [
          {
            // name: '设备管理',
            path: 'equipment-management',
            component: dynamicWrapper(app, [], () => import('../routes/Equipment/EquipmentManagement')),
          },
          {
            // name: '设备详情',
            path: 'equipment-detail',
            component: dynamicWrapper(app, [], () => import('../routes/Equipment/EquipmentDetail')),

          }
        ],
      },
=======
      // {
      //   // name: '设备相关跳转',
      //   path: 'equipment',
      //   children: [
      //     {
      //       // name: '设备管理',
      //       path: 'equipment-management',
      //       component: dynamicWrapper(app, ['form'], () => import('../routes/Equipment/EquipmentManagement')),
      //     },
      //     {
      //       // name: '设备详情',
      //       path: 'equipment-detail',
      //       component: dynamicWrapper(app, ['form'], () => import('../routes/Equipment/EquipmentDetail')),

      //     }
      //   ],
      // },
>>>>>>> b733e56efa6345ec0a5ab3ef40cc3728dc4cf652
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
