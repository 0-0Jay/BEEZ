import { fileURLToPath, URL } from 'node:url';

import { PrimeVueResolver } from '@primevue/auto-import-resolver';
import tailwindcss from '@tailwindcss/vite';
import vue from '@vitejs/plugin-vue';
import Components from 'unplugin-vue-components/vite';
import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
  optimizeDeps: {
    noDiscovery: true,
    include: ['@stomp/stompjs'],
    exclude: ['@/lib/bryntum/gantt.module.js']
  },
  plugins: [
    vue(),
    tailwindcss(),
    Components({
      resolvers: [PrimeVueResolver()]
    })
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8888',
        changeOrigin: true
      },
      '/ws': {
        target: 'ws://localhost:8888',
        ws: true
      }
    }
  },
  resolve: {
    dedupe: ['vue'],
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler'
      }
    }
  }
});
