import { useAlertStore } from '@/stores/alert';
import { useAuthStore } from '@/stores/auth';
import { jwtDecode } from 'jwt-decode';

export const setupAuthGuard = (to, from, next) => {
  const authStore = useAuthStore();
  const alertStore = useAlertStore();
  const token = authStore.accessToken;
  const currentTime = Date.now() / 1000; // 현재 시간

  if (token) {
    try {
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

  // 인증 여부 확인
  const isAuthenticated = !!token;

  if (to.name === 'login' && isAuthenticated) {
    alertStore.setAlert('이미 로그인되어 있습니다.');
    return next({ name: 'dashboard' });
  }

  // 로그인 필수 페이지 체크
  if (to.meta.requiresAuth && !isAuthenticated) {
    alertStore.setAlert('로그인이 필요한 서비스입니다.');
    return next({ name: 'login' });
  }

  // 권한 체크
  if (to.meta.role) {
    // console.log('권한 체크 진입');
    const userRole = authStore.user?.role || [];
    // console.log('userRoles:', userRoles);
    // console.log('required:', to.meta.role);
    if (!userRole.includes(to.meta.role)) {
      console.log('alert 직전');
      alertStore.setAlert(`접근 권한이 없습니다!\n(보유 권한: ${userRole.length ? userRole : '없음'}, 필요: ${to.meta.role})`);
      return next({ name: 'dashboard' });
    }
  }

  next(); // 모든 조건 통과
};
