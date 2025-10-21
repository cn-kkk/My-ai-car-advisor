<template>
  <section class="intro container">
    <div class="intro-card">
      <h2 class="intro-title">我可以帮助您：</h2>
      <ul class="intro-list">
        <li>快速了解车型亮点与适配场景</li>
        <li>智能比较配置与性价比建议</li>
        <li>支持图片识别，更直观匹配车型</li>
      </ul>
      <p class="intro-welcome">快向我提问吧！</p>
    </div>
  </section>
</template>

<script setup>
</script>

<style scoped>
.intro { padding: 10px 0 16px; }
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
  grid-template-columns: 1fr; /* 单列，每行一个文本框 */
  gap: 10px;
  justify-items: center; /* 默认全部居中 */
}
.intro-list li {
  color: #4b5563;
  font-size: clamp(13px, 2.3vw, 14px);
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(10, 132, 255, 0.18); /* 淡蓝边框 */
  box-shadow: 0 0 0 2px rgba(10, 132, 255, 0.10); /* 很淡的蓝色发光 */
  border-radius: 12px;
  padding: 8px 12px;
  width: fit-content; /* 宽度随内容 */
  max-width: 100%;
  cursor: default; /* 悬停不显示文本光标 */
  user-select: none; /* 禁止选中文本，避免出现文本插入光标 */
  text-align: center; /* 文本居中 */
}
/* 改用 transform 进行左右偏移，避免 margin 导致溢出 */
.intro-list li:nth-child(1) { transform: translateX(-20%); }
.intro-list li:nth-child(2) { transform: translateX(0); }
.intro-list li:nth-child(3) { }
.intro-list li:nth-child(3) { justify-self: center; transform: translateX(20%); text-align: center; }
.intro-welcome {
  margin: 10px 0 0;
  text-align: center;
  color: #6b7280;
  font-size: 13px;
}
@media (max-width: 640px) {
  .intro-list { grid-template-columns: 1fr; }
}
/* 小屏去除左右偏移，避免宽度超出视口 */
@media (max-width: 520px) {
  .intro-list li { transform: none !important; }
}
</style>