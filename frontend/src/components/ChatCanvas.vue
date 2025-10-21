<template>
  <section class="chat-canvas container">
    <div class="canvas"></div>
  </section>
</template>

<script setup>
</script>

<style scoped>
/* 让竖线锚定在容器的内容内边界（与 IntroPanel/ChatInput 一致） */
.chat-canvas {
  position: relative;
  padding: 0; /* 移除 20px，沿用 .container 的内边距定义 */
  margin: 0 auto; /* 保持居中 */
}
.canvas {
  min-height: clamp(420px, 56vh, 760px);
  border-radius: var(--radius);
  background: transparent;
  padding: 0; margin: 0;
}
/* 支持安全视口单位时，使用 56svh 以避免移动端高度错误 */
@supports (height: 100svh) {
  .canvas { min-height: clamp(380px, 56svh, 760px); }
}
/* 左右竖线绘制在 section 上，精确对齐到容器的 20px 内边界 */
.chat-canvas::before,
.chat-canvas::after {
  content: '';
  position: absolute;
  top: 0; bottom: 0;
  width: 1px;
  background: var(--border);
  pointer-events: none;
}
.chat-canvas::before { left: 0; }
.chat-canvas::after { right: 0; }
/* 小屏进一步降低中间区域最小高度，保证底部可见并可滚动 */
@media (max-width: 520px) {
  .canvas { min-height: 360px; }
}
</style>