<template>
  <div class="layout">
    <!-- 顶部标题（统一固定宽度，含副标题） -->
    <TopBarTitle title="AI买车导购" subtitle="帮您更快、更好的了解车、购买车" />

    <!-- 顶部描述区：我可以帮助您 -->
    <IntroPanel />

    <!-- 中间对话区域：仅左右竖线 + 外扩极淡镭射阴影 -->
    <ChatCanvas />

    <!-- 底部：三选项 + 输入框（无外层方框） -->
    <BottomControls @send="handleSend" />
  </div>
</template>

<script setup>
import TopBarTitle from '../components/TopBarTitle.vue'
import IntroPanel from '../components/IntroPanel.vue'
import ChatCanvas from '../components/ChatCanvas.vue'
import BottomControls from '../components/BottomControls.vue'

// 后续将通过 provide/inject 或事件总线把消息渲染到 ChatCanvas
const handleSend = ({ text, mode }) => {
  console.log('send', { text, mode })
}
</script>

<style scoped>
.layout {
  display: grid;
  grid-template-rows: auto auto 1fr auto;
  min-height: 100vh; /* 桌面与旧移动端兼容 */
}
/* 现代移动端安全视口，避免地址栏导致的 100vh 误差 */
@supports (height: 100svh) {
  .layout { min-height: 100svh; }
}
</style>