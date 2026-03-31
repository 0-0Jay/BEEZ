const Routes3 = [
  {
    path: '/project/create',
    name: 'ProjectCreate',
    component: () => import('@/views/project/ProjectCreatePage.vue')
  },
  {
    path: '/project/setting/:id',
    name: 'ProjectSetting',
    component: () => import('@/views/project/ProjectSettingPage.vue')
  }
];
export default Routes3;
