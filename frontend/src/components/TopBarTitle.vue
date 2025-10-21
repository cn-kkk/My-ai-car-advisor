<template>
  <header class="topbar container">
    <h1 class="title">{{ title }}</h1>
    <p class="subtitle">{{ subtitle }}</p>
  </header>
</template>

<script setup>
defineProps({
  title: { type: String, default: 'AI买车导购' },
  subtitle: { type: String, default: '帮你您更快、更好的了解车、购买车' }
})
</script>

<style scoped>
.topbar {
  padding: 26px 20px 8px;
}
.title {
  text-align: center;
  margin: 0;
  font-size: clamp(28px, 3.6vw, 40px);
  font-weight: 800;
  letter-spacing: 0.02em; /* 增加字距，避免拥挤 */
  line-height: 1.15;
  /* 统一中英文视觉风格 */
  font-family: 'Noto Sans SC', 'Noto Sans', -apple-system, BlinkMacSystemFont,
    'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Microsoft YaHei', sans-serif;
  /* 细微的渐变高光，增强质感但不喧宾夺主 */
  background: linear-gradient(180deg, #0b1020 0%, #1b2333 60%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle {
  text-align: center;
  margin: 8px 0 0;
  font-size: clamp(12px, 2.2vw, 14px);
  color: var(--muted-text);
  letter-spacing: 0.01em;
}
</style>