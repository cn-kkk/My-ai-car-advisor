<template>
  <div class="layout">
    <!-- 顶部标题（统一固定宽度，含副标题） -->
    <TopBarTitle title="AI买车导购" subtitle="帮您更快、更好的了解车、购买车" />

    <!-- 顶部描述区：我可以帮助您 -->
    <IntroPanel />

    <!-- 中间对话区域：仅左右竖线 + 外扩极淡镭射阴影 -->
    <ChatCanvas :mode="mode" :messages="messages" />

    <!-- 底部：三选项 + 输入框（无外层方框） -->
    <BottomControls v-model="mode" :sending="sending" @send="handleSend" @upload="handleUpload" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TopBarTitle from '../components/TopBarTitle.vue'
import IntroPanel from '../components/IntroPanel.vue'
import ChatCanvas from '../components/ChatCanvas.vue'
import BottomControls from '../components/BottomControls.vue'

const mode = ref('start')
const messages = ref([])
const sending = ref(false)
const conversationId = ref(null)
const userIdKey = 'userId'
const userId = ref(localStorage.getItem(userIdKey) || `user-${Math.random().toString(36).slice(2,10)}`)

onMounted(() => {
  if (!localStorage.getItem(userIdKey)) {
    localStorage.setItem(userIdKey, userId.value)
  }
})

const uuid = () => (crypto?.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random().toString(16).slice(2)}`)

const handleSend = async ({ text, mode }) => {
  // 先展示用户消息
  const userMsgId = Date.now()
  messages.value.push({ id: userMsgId, role: 'user', text })
  if (sending.value) return
  sending.value = true

  // 预置占位的助手消息，初始显示「。」
  const placeholderId = userMsgId + 1
  messages.value.push({ id: placeholderId, role: 'assistant', text: '。' })
  const appendDot = () => {
    const idx = messages.value.findIndex(m => m.id === placeholderId)
    if (idx !== -1) messages.value[idx].text += '。'
  }
  const dotsTimer = setInterval(appendDot, 3000)

  const body = {
    userId: userId.value,
    requestId: uuid(),
    conversationId: conversationId.value,
    message: text
  }

  const controller = new AbortController()
  const timeout = setTimeout(() => controller.abort(), 45000)

  try {
    const resp = await fetch('/ai/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
      signal: controller.signal
    })
    clearTimeout(timeout)
    const data = await resp.json()
    if (data?.conversationId && !conversationId.value) {
      conversationId.value = data.conversationId
    }
    // 逐字显示响应
    const idx = messages.value.findIndex(m => m.id === placeholderId)
    if (idx !== -1) {
      const responseText = data?.message || '（无响应内容）'
      let displayedText = ''
      for (const char of responseText) {
        displayedText += char
        messages.value[idx].text = displayedText
        await new Promise(resolve => setTimeout(resolve, 30)) // 30ms 逐字间隔
      }
    }
  } catch (e) {
    const idx = messages.value.findIndex(m => m.id === placeholderId)
    if (e?.name === 'AbortError') {
      if (idx !== -1) messages.value[idx].text = '请求超时，请稍后重试。'
    } else {
      if (idx !== -1) messages.value[idx].text = '请求失败，请稍后再试。'
    }
  } finally {
    clearInterval(dotsTimer)
    sending.value = false
  }
}

const handleUpload = ({ files, mode: active }) => {
  const names = files.map(f => f.name).join(', ')
  messages.value.push({ id: Date.now(), role: 'user', text: `已选择图片：${names}` })
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