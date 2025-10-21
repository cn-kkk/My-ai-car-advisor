<template>
  <nav class="tabs container">
    <div class="segmented card">
      <button
        v-for="t in tabs"
        :key="t.value"
        class="seg-btn focus-ring"
        :class="{ active: modelValue === t.value }"
        @click="$emit('update:modelValue', t.value)"
      >
        {{ t.label }}
      </button>
    </div>
  </nav>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: 'start' } })
const tabs = [
  { value: 'start', label: '开始选车' },
  { value: 'compare', label: '车辆对比' },
  { value: 'identify', label: '车型识别' }
]
</script>

<style scoped>
.tabs { padding: 12px 20px 18px; }
.segmented {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  padding: 8px;
  border-radius: var(--radius);
}
.seg-btn {
  padding: 12px 16px;
  border-radius: calc(var(--radius) - 4px);
  border: 1px solid var(--border);
  background: #fff;
  color: #0f172a;
  box-shadow: var(--shadow-soft);
  cursor: pointer;
  transition: transform .16s ease, box-shadow .2s ease, background .2s ease, color .2s ease, border-color .2s ease;
}
.seg-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-hover);
}
.seg-btn.active {
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-600) 100%);
  color: #fff;
  border-color: var(--primary-700);
  box-shadow: 0 6px 14px rgba(10, 132, 255, 0.35);
}
@media (max-width: 520px) {
  .segmented { grid-template-columns: 1fr; }
}
</style>