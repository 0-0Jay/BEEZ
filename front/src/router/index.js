import AppLayout from '@/layout/AppLayout.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { setupAuthGuard } from './authGuard';
import Routes1 from './routes1';
import Routes2 from './routes2';
import Routes3 from './routes3';
import Routes4 from './routes4';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'login', component: () => import('@/views/auth/Login.vue') },

    {
      path: '/auth/pwRequest',
      name: 'pwRequest',
      component: () => import('@/views/auth/PasswordRequest.vue')
    },
    {
      path: '/auth/pwReset',
      name: 'pwReset',
      component: () => import('@/views/auth/PasswordReset.vue')
    },

    {
      path: '/main',
      component: AppLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '/main',
          name: 'dashboard',
          component: () => import('@/views/dashboard/MainDashBoardPage.vue')
        },
        {
          path: '/project/list',
          name: 'projectList',
          component: () => import('@/views/project/ProjectListPage.vue')
        },
        // 권한 테스트용
        // {
        //   path: '/test/auth',
        //   name: 'authTest',
        //   component: () => import('@/views/AuthTest.vue'),
        //   meta: { requiresAuth: true, role: 'ROLE0001' } // 필요한 권한을 메타 정보에 기록
        // },
        {
          path: '/error',
          name: 'error',
          component: () => import('@/views/ErrorPage.vue') // 에러페이지
        },
        ...Routes1,
        ...Routes2,
        ...Routes3,
        ...Routes4
      ]
    }
    // 여기에 페이지 추가 금지
    // ...????Routes 형식으로 라우트 파일 새로 생성해서 연결 -> 충돌방지
    // 무슨 말인지 모르겠으면 MES 프로젝트의 index.js 파일 참조
  ]
});

router.beforeEach(setupAuthGuard);
export default router;
