import router from '@/router';
import axios from 'axios';
import { useAuthStore } from './auth';

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// token 관련 처리(요청)
// 서버로 가기 전 LocalStorage에서 토큰을 꺼내서 붙여줌
axiosInstance.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token = authStore.accessToken || localStorage.getItem('accessToken');

    if (token) {
      // Authorization 헤더에 Bearer 토큰 추가
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 공통 에러 처리(응답)
axiosInstance.interceptors.response.use(
  (response) => response, // 성공은 그대로 통과

  (error) => {
    const status = error.response?.status;
    const serverMessage = error.response?.data;
    const message = serverMessage || ERROR_MESSAGES[status] || `알 수 없는 오류가 발생했습니다. (${status})`;

    // 공통 에러 핸들링
    handleError(status, message);
    console.log(error);

    // 호출부에서 추가 처리가 필요하면 reject로 전달
    error.message = message;
    return Promise.reject(error);
  }
);

// 상태 코드별 메시지 정의
const ERROR_MESSAGES = {
  400: '잘못된 요청입니다.',
  401: '인증이 필요합니다.',
  403: '접근 권한이 없습니다.',
  404: '요청한 리소스를 찾을 수 없습니다.',
  405: '허용되지 않는 메서드입니다.',
  500: '서버 내부 오류가 발생했습니다.',
  501: '구현되지 않은 기능입니다.',
  503: '서비스를 일시적으로 사용할 수 없습니다.'
};

function handleError(status, message) {
  const isLoginPage = window.location.pathname === '/';

  switch (status) {
    case 401:
      if (!isLoginPage) {
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        const authStore = useAuthStore();
        authStore.logout();
        window.location.href = '/';
      }
      break;

    case 403:
      alert(ERROR_MESSAGES[403]);
      router.push('/main');
      break;

    case 404:
    case 405:
    case 500:
    case 501:
      // 예: 전역 토스트/알림 출력
      console.log(message); // 실제론 toast 라이브러리 권장
      break;

    default:
      console.error(`[HTTP Error] ${status}: ${message}`);
  }
}

export default axiosInstance;
