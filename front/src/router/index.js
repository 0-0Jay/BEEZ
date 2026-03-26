import AppLayout from '@/layout/AppLayout.vue';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: AppLayout,
      children: [
        {
          path: '/',
          name: 'dashboard',
          component: () => import('@/views/Dashboard.vue')
        },
         {
          path: '/project/list',
          name: 'projectList',
          component: () => import('@/views/project/ProjectListPage.vue')
        },
        {
          path: '/test',
          name: 'test',
          component: () => import('@/views/TestPage.vue')
        }
      ]
    }
    // 여기에 페이지 추가 금지
    // ...????Routes 형식으로 라우트 파일 새로 생성해서 연결 -> 충돌방지
    // 무슨 말인지 모르겠으면 MES 프로젝트의 index.js 파일 참조
  ]
});

export default router;
