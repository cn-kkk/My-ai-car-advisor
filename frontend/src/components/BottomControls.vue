<template>
  <section class="bottom-controls">
    <FeatureTabs v-model="active" />
    <ChatInput :placeholder="placeholder" :mode="active" :sending="props.sending" @send="onSend" @upload="onUpload" />
  </section>
</template>

<script setup>
import { ref, watch } from 'vue'
import FeatureTabs from './FeatureTabs.vue'
import ChatInput from './ChatInput.vue'

const props = defineProps({ modelValue: { type: String, default: 'start' }, sending: { type: Boolean, default: false } })
const emit = defineEmits(['update:modelValue','send','upload'])

const active = ref(props.modelValue)
watch(() => props.modelValue, v => { active.value = v })
watch(active, v => emit('update:modelValue', v))

const placeholder = '有问题尽管问我...'
const onSend = (text) => emit('send', { text, mode: active.value })
const onUpload = (files) => emit('upload', { files, mode: active.value })
</script>

<style scoped>
.bottom-controls { padding: 6px 0 0; }
</style>