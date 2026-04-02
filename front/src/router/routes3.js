const Routes3 = [
  {
    path: '/project/create',
    name: 'ProjectCreate',
    component: () => import('@/views/project/ProjectCreatePage.vue')
  },
  {
    path: '/project/setting/:id',
    component: () => import('@/views/project/ProjectSettingPage.vue'),
    children: [
      {
        path: '',
        name: 'ProjectSetting',
        redirect: (to) => `/project/setting/${to.params.id}/info`
      },
      {
        path: 'info',
        name: 'ProjectSettingInfo',
        component: () => import('@/views/project/tabs/ProjectInfoTab.vue')
      },
      {
        path: 'members',
        name: 'ProjectSettingMembers',
        component: () => import('@/views/project/tabs/ProjectMemberTab.vue')
      },
      {
        path: 'versions',
        name: 'ProjectSettingVersions',
        component: () => import('@/views/project/tabs/ProjectVersionTab.vue')
      }
    ]
  }
];
export default Routes3;
