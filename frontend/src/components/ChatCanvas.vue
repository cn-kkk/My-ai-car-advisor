<template>
  <section class="chat-canvas container">
    <div class="canvas">
      <div v-if="!messages || messages.length === 0" class="empty-hint">
        <template v-if="mode === 'start'">
          <p class="hint-title">你好，请在下方输入购车需求，我会帮您选购爱车。</p>
          <p class="hint-eg">例如：预算20w，想要买一辆suv通勤用，不接受新能源。</p>
        </template>
        <template v-else-if="mode === 'compare'">
          <p class="hint-title">请告诉我你想对比的两款车型。</p>
          <p class="hint-eg">例如：帮我比较下model3和小米su7标准版的不同之处。</p>
        </template>
        <template v-else-if="mode === 'identify'">
          <p class="hint-title">请先点击下面的+号上传车辆图片</p>
        </template>
      </div>
      <div v-else class="msg-list">
        <div v-for="m in messages" :key="m.id" class="msg" :class="m.role">
          <template v-if="m.text">
            <div class="bubble" :class="m.role">{{ m.text }}</div>
          </template>
          <template v-else-if="m.images && m.images.length">
            <div class="bubble img-bubble" :class="m.role">
              <div class="img-grid">
                <img v-for="(src,i) in m.images" :key="i" :src="src" class="img-preview" :alt="`已选择图片 ${i+1}`" />
              </div>
            </div>
          </template>
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
.chat-canvas {
  position: relative;
  padding: 0;
  margin: 0 auto;
}
.canvas {
  min-height: clamp(420px, 56vh, 760px);
  border-radius: var(--radius);
  background: transparent;
  padding: 0 25px;
  margin: 0;
}
@supports (height: 100svh) {
  .canvas { min-height: clamp(380px, 56svh, 760px); }
}
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
@media (max-width: 520px) {
  .canvas { min-height: 360px; }
}
.empty-hint { color: #475569; }
.hint-title { margin: 0; font-size: 14px; line-height: 1.6; }
.hint-eg { margin: 6px 0 0; font-size: 13px; color: #64748b; }
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
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  word-break: break-word;
}
.bubble.user {
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-600) 100%);
  color: #fff;
  border-color: var(--primary-700);
  box-shadow: none;
}
/* 图片预览样式 */
.img-bubble { padding: 8px; }
.img-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 8px; }
.img-preview { width: 100%; max-height: 220px; object-fit: contain; border-radius: 10px; border: 1px solid rgba(0,0,0,0.10); background: #fff; }
/* 用户图片消息不使用渐变背景，避免影响预览 */
.img-bubble.user { background: #fff; color: inherit; border-color: var(--primary-700); }
</style>