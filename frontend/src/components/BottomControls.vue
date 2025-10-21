<template>
  <section class="bottom-controls">
    <FeatureTabs v-model="active" />
    <ChatInput :placeholder="placeholder" @send="onSend" />
  </section>
</template>

<script setup>
import { ref } from 'vue'
import FeatureTabs from './FeatureTabs.vue'
import ChatInput from './ChatInput.vue'

const active = ref('start')
const placeholder = '请输入您的需求，例如：预算20万，适合家用的SUV'

const emit = defineEmits(['send'])
const onSend = (text) => emit('send', { text, mode: active.value })
</script>

<style scoped>
.bottom-controls { padding: 6px 0 0; }
</style>