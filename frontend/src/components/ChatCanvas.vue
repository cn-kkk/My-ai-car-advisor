<template>
  <section class="chat-canvas container">
    <div class="canvas">
      <!-- 空状态：根据模式显示无边框提示 -->
      <div v-if="!messages || messages.length === 0" class="empty-hint">
        <template v-if="mode === 'start'">
          <p class="hint-title">你好，请在下方输入购车需求，我会帮您选购爱车。</p>
          <p class="hint-eg">例如：预算20w，想要买一辆suv通勤用，不接受新能源。</p>
        </template>
        <template v-else-if="mode === 'compare'">
          <p class="hint-title">请告诉我你想对比的车型，不要超过3款哟。</p>
          <p class="hint-eg">例如：帮我比较下24年model 3和24年小米su7标准版的不同之处。</p>
        </template>
        <template v-else-if="mode === 'identify'">
          <p class="hint-title">请先点击下面的+号上传车辆图片</p>
        </template>
      </div>

      <!-- 对话列表：用户右侧、AI左侧 -->
      <div v-else class="msg-list">
        <div v-for="m in messages" :key="m.id" class="msg" :class="m.role">
          <div class="bubble" :class="m.role">{{ m.text }}</div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  mode: { type: String, default: 'start' },
  messages: { type: Array, default: () => [] }
})
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
  padding: 0 25px; margin: 0;
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

/* 空状态提示：无边框、轻色文案 */
.empty-hint { color: #475569; }
.hint-title { margin: 0; font-size: 14px; line-height: 1.6; }
.hint-eg { margin: 6px 0 0; font-size: 13px; color: #64748b; }

/* 对话列表与气泡样式 */
.msg-list { display: flex; flex-direction: column; gap: 10px; }
.msg { display: flex; }
.msg.user { justify-content: flex-end; }
.msg.assistant { justify-content: flex-start; }
.bubble {
  max-width: min(75%, 680px);
  padding: 10px 12px;
  border-radius: 14px;
  border: 1px solid rgba(0,0,0,0.10);
  background: #fff;
  color: #0f172a;
  box-shadow: var(--shadow-soft);
  white-space: pre-wrap; /* 保留段落换行 */
}
.bubble.user {
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-600) 100%);
  color: #fff;
  border-color: var(--primary-700);
  box-shadow: none;
}
</style>