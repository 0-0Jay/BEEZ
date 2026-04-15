//곽현우 router
const Routes4 = [
  {
    path: '/wiki/write/:projectId',
    name: 'WikiWrite',
    component: () => import('@/views/wiki/WikiWrite.vue')
  },
  {
    path: '/wiki/detail/:projectId/:wikiId',
    name: 'WikiDetail',
    component: () => import('@/views/wiki/WikiDetail.vue')
  },
  {
    path: '/wiki/history/:projectId/:wikiId',
    name: 'WikiHistory',
    component: () => import('@/views/wiki/WikiHistory.vue')
  },

  {
    path: '/wiki/edit/:projectId/:wikiId',
    name: 'WikiEdit',
    component: () => import('@/views/wiki/WikiWrite.vue'),
    meta: { permission: 'PER260407010' }
  },

  {
    path: '/document/list/:projectId',
    name: 'DocumentList',
    component: () => import('@/views/document/DocumentList.vue')
  },
  {
    path: '/document/write/:projectId',
    name: 'DocumentWrite',
    component: () => import('@/views/document/DocumentWrite.vue'),
    meta: { permission: 'PER260413006' }
  },
  {
    path: '/document/detail/:projectId/:docId',
    name: 'DocumentDetail',
    component: () => import('@/views/document/DocumentDetail.vue')
  },
  {
    path: '/document/edit/:projectId/:docId',
    name: 'DocumentEdit',
    component: () => import('@/views/document/DocumentEdit.vue')
  }
]; //router end
export default Routes4;
