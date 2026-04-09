import { createPinia } from 'pinia';
import piniaPlugin from 'pinia-plugin-persistedstate';
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import VueGanttastic from '@infectoone/vue-ganttastic';
import { $t } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';

import '@/assets/styles.scss';
import '@/assets/tailwind.css';
import 'primeicons/primeicons.css';

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPlugin);

const amber = {
  50: '#fffbeb',
  100: '#fef3c7',
  200: '#fde68a',
  300: '#fcd34d',
  400: '#fbbf24',
  500: '#f59e0b',
  600: '#d97706',
  700: '#b45309',
  800: '#92400e',
  900: '#78350f',
  950: '#451a03'
};

app.use(VueGanttastic);
app.use(pinia);
app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.app-dark'
    }
  }
});

$t()
  .preset(Aura)
  .preset({
    semantic: {
      primary: amber
    }
  })
  .use({ useDefaultOptions: true });

app.use(ToastService);
app.use(ConfirmationService);

app.mount('#app');
