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
            children: [
              {
                path: '',
                component: dynamicWrapper(app, ['commonuser'], () => import('../routes/CommonUser/Index')),
              },
              {
                path: ':id',
                component: dynamicWrapper(app, ['userDetail'], () => import('../routes/CommonUser/UserInfo')),
              },
            ]
          },
          {
            name: '企业用户',
            path: 'company-user',
            // component: dynamicWrapper(app, ['form'], () => import('../routes/CompanyUser/CompanyUserList')),
            children: [
              {
                path: '',
                component: dynamicWrapper(app, ['companyuser'], () => import('../routes/CompanyUser/CompanyUserList')),
              },
              {
                // name: '企业用户',
                path: ':id',
                component: dynamicWrapper(app, [], () => import('../routes/CompanyUser/CompanyUserDetail')),
              },
              {
                // name: '企业用户',
                path: '/equipment',
                component: dynamicWrapper(app, [], () => import('../routes/Equipment/Query')),
              },
            ],
          },

          {
            // name: '企业用户',
            path: 'company-user-detail',
            component: dynamicWrapper(app, [], () => import('../routes/CompanyUser/CompanyUserDetail')),
          },
          {
            name: '塘口管理',
            path: 'pondManage',

            children: [
              {
                path: '',
                component: dynamicWrapper(app, ['pond'], () => import('../routes/pondManage/Index')),
              }, {
                path: ':id',
                component: dynamicWrapper(app, ['pond'], () => import('../routes/pondManage/Detail')),
              }
            ],
          }
        ],
      },
      {
        name: '设备查询',
        path: 'equipmentsQuery',
        icon: 'bars',
        component: dynamicWrapper(app, ['allEquipment'], () => import('../routes/Equipment/AllEquipment')),

      },
      {
        name: '账户管理',
        path: 'account-management',
        icon: 'user',
        component: dynamicWrapper(app, ['login'], () => import('../routes/AccountManagement/AccountManagement')),

      },
      {
        // name: '设备相关跳转',
        path: 'equipment',
        children: [
          {
            // name: '设备管理',
            path: ':relation',
            component: dynamicWrapper(app, ['equipment'], () => import('../routes/Equipment/EquipmentManagement')),
          },

          {
            // name: '设备详情',
            path: 'detail',
            children: [
              {
                path: ':id',
                component: dynamicWrapper(app, ['equipment'], () => import('../routes/Equipment/EquipmentDetail')),
              }
            ]
          },

          {
            name: '水质曲线',
            path: 'water-quality',
            children: [
              {
                path: ":id",
                component: dynamicWrapper(app, [], () => import('../routes/Equipment/WaterQualityCurve')),
              }
            ]
          },
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
