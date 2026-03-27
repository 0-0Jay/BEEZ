import router from '@/router'; // 필요 시 라우터 연동
import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// 공통 에러 처리
axiosInstance.interceptors.response.use(
  (response) => response, // 성공은 그대로 통과

  (error) => {
    const status = error.response?.status;
    const message = ERROR_MESSAGES[status] ?? `알 수 없는 오류가 발생했습니다. (${status})`;

    // 공통 에러 핸들링
    handleError(status, message);
    console.log(error);

    // 호출부에서 추가 처리가 필요하면 reject로 전달
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
  switch (status) {
    case 401:
      // 예: 로그인 페이지로 이동
      router.push('/login');
      break;

    case 403:
      // router.push('/forbidden');
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
