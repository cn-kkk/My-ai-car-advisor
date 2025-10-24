import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: true, // 对外监听（0.0.0.0），便于局域网/穿透访问
    port: 5173,
    allowedHosts: ['url'], // 允许通过该公网域名访问
    proxy: {
      '/ai': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})