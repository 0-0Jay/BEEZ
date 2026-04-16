import { useAlertStore } from '@/stores/alert';
import { useAuthStore } from '@/stores/auth';
import { jwtDecode } from 'jwt-decode';

export const setupAuthGuard = async (to, from, next) => {
  const authStore = useAuthStore();
  const alertStore = useAlertStore();
  const token = authStore.accessToken;
  const isAuthenticated = !!token;

  if (to.meta.requiresAuth && !isAuthenticated) {
    alertStore.setAlert('로그인이 필요한 서비스입니다.');
    return next({ name: 'login' });
  }

  if (to.name === 'login' && isAuthenticated) {
    return next({ name: 'dashboard' });
  }

  if (token) {
    try {
      const currentTime = Date.now() / 1000; // 현재 시간
      const decoded = jwtDecode(token);

      // 토큰 만료 체크
      if (decoded.exp && decoded.exp < currentTime) {
        alertStore.setAlert('세션이 만료되었습니다. 다시 로그인해주세요.');
        authStore.logout();
        // authStore.$reset(); // 초기화
        return next({ name: 'login' });
      }

      // 토큰은 유효한데 스토어에 유저 정보가 없다면 복구
      // if (!authStore.user) {
      //   authStore.user = {
      //     id: decoded.sub,
      //     name: decoded.name,
      //     roles: decoded.roles || []
      //   };
      // }
    } catch (e) {
      console.error('토큰 검증/복구 에러:', e);
      authStore.logout();
      return next({ name: 'login' });
    }
  }

  // 프로젝트별 권한 확인
  if (to.meta.permission) {
    let projectId = to.params.id || to.params.projectId;
    // 관리자는 무조건 통과
    if (authStore.user.role === 'ROLE0001') return next();

    if (!projectId) {
      const projectData = sessionStorage.getItem('project');
      if (projectData) {
        projectId = JSON.parse(projectData).selectedProject?.id;
      }
    }

    console.log(`[Guard] 현재 페이지: ${to.name}, 찾은 ID: ${projectId}`);

    if (!projectId) {
      if (to.name === 'ProjectCreate' && (authStore.user.role === 'ROLE0001' || authStore.user.role === 'ROLE0002')) {
        return next();
      }

      if (to.meta.permission) {
        alertStore.setAlert('접근 권한이 없습니다');
        return next(false);
      }

      alertStore.setAlert('프로젝트를 선택해주세요.');
      return next({ name: 'projectList' });
    }

    if (authStore.currentProjectId !== projectId) {
      try {
        await Promise.all([authStore.findPermissionsByProject(projectId), authStore.findRolesByProject(projectId)]);
        authStore.currentProjectId = projectId;
      } catch (e) {
        alertStore.setAlert('권한 정보를 불러오지 못했습니다.');

        if (window.history.length > 1) {
          return next(false);
        } else {
          return next({ name: 'dashboard' });
        }
      }
    }

    if (!authStore.hasPermissions(to.meta.permission)) {
      alertStore.setAlert('접근 권한이 없습니다');
      if (window.history.length > 1) {
        return next(false);
      } else {
        return next({ name: 'dashboard' });
      }
    }

    console.log('내 역할:', authStore.user?.role);
    console.log('필요한 코드:', to.meta.permission);
    console.log('보유 권한 리스트:', JSON.stringify(authStore.projectPermissions));
    console.log('포함 여부:', authStore.projectPermissions.includes(to.meta.permission));
  }

  const ROLE_NAMES = {
    ROLE0001: '관리자',
    ROLE0002: 'PM / PL',
    ROLE0003: '일반사용자'
  };

  // 권한 체크
  if (to.meta.role) {
    // console.log('권한 체크 진입');
    let userRole = authStore.user?.role || [];
    // console.log('userRoles:', userRoles);
    // console.log('required:', to.meta.role);

    // 배열이 아닌 경우 강제 변환
    if (!Array.isArray(userRole)) {
      userRole = [userRole];
    }

    if (!userRole.includes(to.meta.role)) {
      // const roleName = ROLE_NAMES[to.meta.role] || to.meta.role;
      // const myRoleName = userRole.length ? userRole.map((r) => ROLE_NAMES[r] || r).join(', ') : '없음';

      // console.log('alert 직전');
      // alertStore.setAlert(`접근 권한이 없습니다!\n(보유 권한: ${myRoleName}, 필요: ${roleName})`);
      alertStore.setAlert('접근 권한이 없습니다!');

      return next({ name: 'dashboard' });
    }
  }

  next(); // 모든 조건 통과
};
