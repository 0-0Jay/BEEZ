const Routes1 = [
  {
    path: '/users/list',
    name: 'userList',
    component: () => import('@/views/users/UserListPage.vue'),
    meta: { requiresAuth: true, role: 'ROLE0001' }
  },
  {
    path: '/roles/list',
    name: 'roleList',
    component: () => import('@/views/roles/RoleListPage.vue'),
    meta: { requiresAuth: true, role: 'ROLE0001' }
  },
  {
    path: '/roles/add',
    name: 'roldAdd',
    component: () => import('@/views/roles/RoleAddPage.vue'),
    meta: { requiresAuth: true, role: 'ROLE0001' }
  },
  {
    path: '/roles/edit',
    name: 'roldEdit',
    component: () => import('@/views/roles/RoleManagePage.vue'),
    meta: { requiresAuth: true, role: 'ROLE0001' }
  },
  {
    path: '/workflow/list',
    name: 'workflow',
    component: () => import('@/views/roles/WorkflowManagePage.vue'),
    meta: { requiresAuth: true, role: 'ROLE0001' }
  }
];
export default Routes1;
