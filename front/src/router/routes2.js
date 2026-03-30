const Routes2 = [
  {
    path: '/tasks',
    name: 'task',
    component: () => import('@/views/task/TaskListPage.vue')
  },
  {
    path: '/task/option',
    name: 'taskoption',
    component: () => import('@/views/task/TaskOptionManagePage.vue')
  },
  {
    path: '/task/create',
    name: 'taskcreate',
    component: () => import('@/views/task/TaskEditPage.vue')
  },
  {
    path: '/task/edit',
    name: 'taskedit',
    component: () => import('@/views/task/TaskEditPage.vue')
  },
  {
    path: '/task/copy',
    name: 'taskcopy',
    component: () => import('@/views/task/TaskEditPage.vue')
  }
];
export default Routes2;
