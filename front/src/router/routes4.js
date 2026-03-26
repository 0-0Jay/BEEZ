//곽현우 router
const Routes4 = [
  {
    path: '/wiki/write',
    name: 'WikiWrite',
    component: () => import('@/views/wiki/WikiWrite.vue')
  },
  {
    path: '/wiki/detail/:id',
    name: 'WikiDetail',
    component: () => import('@/views/wiki/WikiDetail.vue')
  },
  {
    path: '/wiki/history/:id',
    name: 'WikiHistory',
    component: () => import('@/views/wiki/WikiHistory.vue')
  }
];
export default Routes4;
