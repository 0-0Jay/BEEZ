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
    path: '/wiki/history/:id',
    name: 'WikiHistory',
    component: () => import('@/views/wiki/WikiHistory.vue')
  },
  {
    path: '/document/list/:projectId',
    name: 'DocumentList',
    component: () => import('@/views/document/DocumentList.vue')
  },
  {
    path: '/document/write/:projectId',
    name: 'DocumentWrite',
    component: () => import('@/views/document/DocumentWrite.vue')
  },
  {
    path: '/document/detail/:projectId/:docId',
    name: 'DocumentDetail',
    component: () => import('@/views/document/DocumentDetail.vue')
  },
  {
    path: '/document/edit/:projectId/:docId',
    name: 'DocumentEdit',
    component: () => import('@/views/document/DocumentWrite.vue')
  }
]; //router end
export default Routes4;
