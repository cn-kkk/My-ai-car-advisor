<template>
  <div class="layout">
    <TopBarTitle title="AI买车导购" subtitle="帮您更快、更好的了解车、购买车" />
    <IntroPanel />
    <ChatCanvas :mode="mode" :messages="messages" />
    <BottomControls v-model="mode" :sending="sending" @send="handleSend" @upload="handleUpload" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import TopBarTitle from '../components/TopBarTitle.vue'
import IntroPanel from '../components/IntroPanel.vue'
import ChatCanvas from '../components/ChatCanvas.vue'
import BottomControls from '../components/BottomControls.vue'

const mode = ref('start')
const messagesByMode = ref({ start: [], compare: [], identify: [] })
const messages = computed(() => messagesByMode.value[mode.value])
const sending = ref(false)
const conversationIds = ref({ start: null, compare: null, identify: null })
const userIdKey = 'userId'
const userId = ref(localStorage.getItem(userIdKey) || `user-${Math.random().toString(36).slice(2,10)}`)

onMounted(() => {
  if (!localStorage.getItem(userIdKey)) {
    localStorage.setItem(userIdKey, userId.value)
  }
})

const uuid = () => (crypto?.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random().toString(16).slice(2)}`)

const isDocHidden = () => {
  const vs = document.visibilityState
  return document.hidden || (vs && vs !== 'visible')
}

const handleSend = async ({ text, mode: active }) => {
  const list = messagesByMode.value[active]
  const userMsgId = Date.now()
  list.push({ id: userMsgId, role: 'user', text })
  if (sending.value) return
  sending.value = true

  const placeholderId = userMsgId + 1
  list.push({ id: placeholderId, role: 'assistant', text: '。' })
  const appendDot = () => {
    const idx = list.findIndex(m => m.id === placeholderId)
    if (idx !== -1) list[idx].text += '。'
  }
  const dotsTimer = setInterval(appendDot, 3000)

  const body = {
    userId: userId.value,
    requestId: uuid(),
    conversationId: conversationIds.value[active],
    message: text
  }

  const controller = new AbortController()
  const timeout = setTimeout(() => controller.abort(), 45000)

  const endpoint = active === 'compare' ? '/ai/compare' : '/ai/chat'

  try {
    const resp = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
      signal: controller.signal
    })
    clearTimeout(timeout)
    const data = await resp.json()
    if (data?.conversationId && !conversationIds.value[active]) {
      conversationIds.value[active] = data.conversationId
    }
    const idx = list.findIndex(m => m.id === placeholderId)
    if (idx !== -1) {
      const responseText = data?.message || '（无响应内容）'

      // 页面不可见或用户减少动效时直接一次性渲染；可见时启用逐字动画
      const prefersReduced = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches
      const shouldType = !prefersReduced && !isDocHidden()

      if (!shouldType) {
        list[idx].text = responseText
        await nextTick()
        document.querySelector('.msg-list')?.lastElementChild?.scrollIntoView({ behavior: 'auto' })
      } else {
        let displayedText = ''
        for (const char of responseText) {
          // 动画过程中若页面变为不可见，立即填充剩余文本
          if (isDocHidden()) {
            list[idx].text = responseText
            break
          }
          displayedText += char
          list[idx].text = displayedText
          await new Promise(resolve => setTimeout(resolve, 10))
        }
        await nextTick()
        document.querySelector('.msg-list')?.lastElementChild?.scrollIntoView({ behavior: 'auto' })
      }
    }
  } catch (e) {
    const idx = list.findIndex(m => m.id === placeholderId)
    if (e?.name === 'AbortError') {
      if (idx !== -1) list[idx].text = '请求超时，请稍后重试。'
    } else {
      if (idx !== -1) list[idx].text = '请求失败，请稍后再试。'
    }
  } finally {
    clearInterval(dotsTimer)
    sending.value = false
  }
}

const handleUpload = ({ files, mode: active }) => {
  const names = files.map(f => f.name).join(', ')
  messagesByMode.value[active].push({ id: Date.now(), role: 'user', text: `已选择图片：${names}` })
}
</script>

<style scoped>
.layout {
  display: grid;
  grid-template-rows: auto auto 1fr auto;
  min-height: 100vh;
}
@supports (height: 100svh) {
  .layout { min-height: 100svh; }
}
</style>