<template>
  <div class="layout">
    <TopBarTitle title="AI买车导购" />

    <IntroPanel />

    <FeatureTabs v-model="active" />

    <div class="content container">
      <div class="panel card">
        <StartSection v-if="active === 'start'" />
        <CompareSection v-else-if="active === 'compare'" />
        <IdentifySection v-else />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import TopBarTitle from '../components/TopBarTitle.vue'
import IntroPanel from '../components/IntroPanel.vue'
import FeatureTabs from '../components/FeatureTabs.vue'
import StartSection from '../sections/StartSection.vue'
import CompareSection from '../sections/CompareSection.vue'
import IdentifySection from '../sections/IdentifySection.vue'

const active = ref('start')
</script>

<style scoped>
.layout {
  display: grid;
  grid-template-rows: auto auto auto 1fr;
  min-height: 100vh;
}
.content { padding: 8px 20px 40px; }
.panel { padding: 20px; border-radius: var(--radius); }
</style>