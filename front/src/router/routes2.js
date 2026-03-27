const Routes2 = [
  {
    path: '/tasks',
    name: 'task',
    component: () => import('@/views/task/TaskList.vue')
  },
  {
    path: '/task/option',
    name: 'taskoption',
    component: () => import('@/views/task/TaskOptionManage.vue')
  }
];
export default Routes2;
