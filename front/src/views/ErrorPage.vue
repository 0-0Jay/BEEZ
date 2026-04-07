<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const dotGridStyle = computed(() => ({
  backgroundImage: 'radial-gradient(circle, #c9d1e8 1px, transparent 1px)',
  backgroundSize: '28px 28px',
  opacity: '0.4'
}));

const goHome = () => {
  router.push('/');
};

const goBack = () => {
  router.back();
};
</script>
<template>
  <div class="min-h-screen bg-[#FFFFFF] flex items-center justify-center relative overflow-hidden">
    <!-- 배경 도트 패턴 -->
    <div class="absolute inset-0 pointer-events-none" :style="dotGridStyle" />

    <!-- 메인 카드 -->
    <Card class="error-card relative z-10 w-full max-w-[500px] mx-4 overflow-hidden animate-rise">
      <!-- 상단 컬러 스트립 -->
      <template #header>
        <div class="h-1 w-full" style="background: linear-gradient(90deg, #5b6e96 0%, #2d8fad 100%)" />
      </template>

      <template #content>
        <div class="flex flex-col items-center text-center px-8 py-10 gap-6">
          <!-- 이미지 슬롯 -->
          <img src="/demo/images/error/error.png" alt="에러 이미지" class="w-full h-full object-cover rounded-2xl" />

          <!-- 숨겨진 파일 인풋 -->
          <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleFileChange" />
          <!-- 제목 -->
          <div class="flex flex-col gap-2">
            <h1 class="text-xl font-semibold leading-snug" style="color: #3a4560">존재하지 않는 페이지입니다.</h1>
            <p class="text-sm leading-relaxed" style="color: #8a97b5">
              요청하신 페이지를 찾을 수 없습니다.<br />
              주소가 잘못 입력되었거나 프로젝트 및 일감이 삭제되었을 수 있습니다.
            </p>
          </div>

          <!-- 구분선 -->
          <Divider class="!my-0 w-full" style="border-color: #dde3f0" />

          <!-- 버튼 그룹 -->
          <div class="flex gap-3 flex-wrap justify-center">
            <Button label="홈으로 이동" icon="pi pi-home" class="!text-sm !font-medium !px-5 !py-2.5 !rounded-lg transition-all duration-200" style="background: #2d8fad; border-color: #2d8fad; color: #ffffff" @click="goHome" />
            <Button label="이전 페이지" icon="pi pi-arrow-left" class="!text-sm !font-medium !px-5 !py-2.5 !rounded-lg transition-all duration-200" style="background: transparent; border: 1.5px solid #2d8fad; color: #2d8fad" @click="goBack" />
          </div>
        </div>
      </template>
    </Card>
  </div>
</template>

<style scoped>
/* 카드 등장 애니메이션 */
@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.animate-rise {
  animation: rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* 카드 그림자 & 테두리 */
.error-card {
  border-radius: 20px !important;
  border: 1px solid #dde3f0 !important;
  box-shadow:
    0 2px 8px rgba(91, 110, 150, 0.07),
    0 16px 48px rgba(91, 110, 150, 0.12) !important;
}

/* PrimeVue Card 내부 패딩 초기화 */
:deep(.p-card-body),
:deep(.p-card-content) {
  padding: 0 !important;
}
:deep(.p-card-header) {
  padding: 0 !important;
}

/* 이미지 슬롯 hover 테두리 색상 */
.image-slot:hover {
  border-color: #2d8fad !important;
}

/* 버튼 hover */
:deep(.p-button:first-of-type:hover) {
  background: #23799a !important;
  border-color: #23799a !important;
  box-shadow: 0 6px 18px rgba(45, 143, 173, 0.28) !important;
  transform: translateY(-1px);
}
:deep(.p-button:last-of-type:hover) {
  background: rgba(45, 143, 173, 0.08) !important;
}

/* Divider 색상 */
:deep(.p-divider-horizontal::before) {
  border-top-color: #dde3f0 !important;
}
</style>
