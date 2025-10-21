<template>
  <nav class="tabs container">
    <div class="segmented">
      <button
        v-for="t in tabs"
        :key="t.value"
        class="seg-btn focus-ring"
        :class="{ active: modelValue === t.value }"
        @click="$emit('update:modelValue', t.value)"
      >
        <span class="icon" aria-hidden="true">
          <svg v-if="t.value === 'start'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a4 4 0 0 1-4 4H7l-4 3V5a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4v10Z"/>
          </svg>
          <svg v-else-if="t.value === 'compare'" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 16V8l-9-5-9 5v8l9 5 9-5Z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="7"/>
            <path d="M21 21l-4.3-4.3"/>
          </svg>
        </span>
        <span class="label">{{ t.label }}</span>
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
.tabs { padding: 8px 0 8px; }
.segmented {
  display: flex;
  gap: 6px;
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
  padding-left: 20px;
}
.seg-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid rgba(0,0,0,0.10);
  background: #fff;
  color: #0f172a;
  cursor: pointer;
  transition: filter .2s ease, background .2s ease, color .2s ease, border-color .2s ease;
}
.seg-btn:hover { filter: brightness(1.03); }
.icon { display: inline-flex; width: 16px; height: 16px; }
.label { line-height: 1; }
.seg-btn.active {
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-600) 100%);
  color: #fff;
  border-color: var(--primary-700);
}
@media (max-width: 520px) {
  .segmented { justify-content: flex-start; }
}
</style>