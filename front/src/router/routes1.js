const Routes1 = [
  {
    path: '/users/list',
    name: 'userList',
    component: () => import('@/views/users/UserListPage.vue'),
    meta: { requiresAuth: true, role: 'ROLE0001' }
  }
];
export default Routes1;
