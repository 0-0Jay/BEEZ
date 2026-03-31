const Routes2 = [
  {
    path: '/tasks',
    name: 'task',
    component: () => import('@/views/task/TaskListPage.vue')
  },
  {
    path: '/task/option',
    name: 'taskOption',
    component: () => import('@/views/task/TaskOptionManagePage.vue')
  },
  {
    path: '/task/create',
    name: 'taskCreate',
    component: () => import('@/views/task/TaskEditPage.vue')
  },
  {
    path: '/task/edit',
    name: 'taskEdit',
    component: () => import('@/views/task/TaskEditPage.vue')
  },
  {
    path: '/task/copy',
    name: 'taskCopy',
    component: () => import('@/views/task/TaskEditPage.vue')
  },
  {
    path: '/task/:taskId',
    name: 'taskDetail',
    component: () => import('@/views/task/TaskDetailPage.vue')
  }
];
export default Routes2;
