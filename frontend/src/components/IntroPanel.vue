<template>
  <section class="intro container">
    <div class="intro-card">
      <h2 class="intro-title">欢迎使用 AI买车导购</h2>
      <ul class="intro-list">
        <li>快速了解车型亮点与适配场景</li>
        <li>智能比较配置与性价比建议</li>
        <li>支持图片识别，更直观匹配车型</li>
      </ul>
      <p class="intro-welcome">祝您选车顺利，体验更轻松的购车流程！</p>
    </div>
  </section>
</template>

<script setup>
</script>

<style scoped>
.intro { padding: 10px 20px 16px; }
.intro-card {
  border-radius: var(--radius);
  padding: 18px 18px 16px;
  background: linear-gradient(180deg, #f3f4f6 0%, #f8fafc 100%);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-soft);
}
.intro-title {
  margin: 0 0 8px;
  font-size: clamp(16px, 2.6vw, 18px);
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 0.01em;
  text-align: center;
}
.intro-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.intro-list li {
  color: #4b5563;
  font-size: clamp(13px, 2.3vw, 14px);
  background: rgba(255, 255, 255, 0.65);
  border: 1px dashed rgba(0,0,0,0.06);
  border-radius: 12px;
  padding: 8px 10px;
}
.intro-welcome {
  margin: 10px 0 0;
  text-align: center;
  color: #6b7280;
  font-size: 13px;
}
@media (max-width: 640px) {
  .intro-list { grid-template-columns: 1fr; }
}
</style>