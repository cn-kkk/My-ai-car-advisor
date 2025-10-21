<template>
  <section class="chat-input container">
    <div class="input-row">
      <input
        v-model="text"
        class="input"
        type="text"
        :placeholder="placeholder"
        :disabled="props.sending"
        @keydown.enter="emitSend"
      />
      <!-- 隐藏的文件选择器（桌面与移动端通用） -->
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        style="display:none"
        @change="handleFileChange"
      />
      <button class="send" @click="handlePrimary" :aria-label="mode === 'identify' ? '上传图片' : '发送'" :disabled="mode !== 'identify' ? props.sending : false">
        <svg v-if="mode === 'identify'" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 5v14"/>
          <path d="M5 12h14"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M22 2L11 13"/>
          <path d="M22 2L15 22L11 13L2 9L22 2Z"/>
        </svg>
      </button>
    </div>
    <p v-if="hint" class="hint">{{ hint }}</p>
  </section>
</template>

<script setup>
import { ref } from 'vue'
const props = defineProps({ placeholder: { type: String, default: '请输入您的需求…' }, hint: { type: String, default: '' }, mode: { type: String, default: 'start' }, sending: { type: Boolean, default: false } })
const emit = defineEmits(['send','upload'])
const text = ref('')
const fileInput = ref(null)

const emitSend = () => {
  if (props.sending) return
  const val = text.value.trim()
  if (!val) return
  emit('send', val)
  text.value = ''
}

const openFilePicker = () => {
  if (fileInput.value) fileInput.value.click()
}

const handleFileChange = (e) => {
  const files = Array.from(e.target.files || [])
  if (files.length === 0) return
  emit('upload', files)
  // 清空以便重复选择同一文件
  e.target.value = ''
}

const handlePrimary = () => {
  if (props.mode === 'identify') {
    openFilePicker()
  } else {
    if (props.sending) return
    emitSend()
  }
}
</script>

<style scoped>
.chat-input { padding: 6px 0 14px; }
.input-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px; /* 缩小与按钮的间距 */
}
.input {
  width: 100%;
  padding: 14px 14px; /* 略增高 */
  border-radius: 12px;
  border: 1px solid rgba(0,0,0,0.12);
  background: #fff;
  font-size: 14px;
}
.input:focus { outline: none; border-color: var(--primary-600); box-shadow: 0 0 0 3px rgba(10,132,255,0.18); }
.send {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px; height: 44px; /* 与输入框更匹配 */
  border-radius: 12px;
  border: 1px solid rgba(0,0,0,0.12);
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-600) 100%);
  color: #fff;
  cursor: pointer;
  transition: transform .16s ease, filter .2s ease;
}
.send:hover { transform: translateY(-1px); filter: brightness(1.03); }
.send[disabled] {
  background: #9ca3af; /* 灰色禁用态 */
  border-color: rgba(0,0,0,0.12);
  cursor: not-allowed;
  filter: none;
}
.send[disabled]:hover { transform: none; filter: none; }
.hint { margin: 8px 2px 0; color: #6b7280; font-size: 12px; }
</style>