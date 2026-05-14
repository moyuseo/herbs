<template>
  <div ref="mapRef" class="origin-map" />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet.heat'

interface OriginItem {
  name: string
  province: string
  herbs: string[]
  lat: number
  lng: number
  avgPrice?: string
}

interface HeatPoint {
  lat: number
  lng: number
  intensity: number
}

const props = withDefaults(defineProps<{
  heatData?: HeatPoint[]
  showHeatmap?: boolean
}>(), {
  heatData: () => [],
  showHeatmap: true,
})

const defaultOriginData: OriginItem[] = [
  { name: '亳州', province: '安徽', herbs: ['白芍', '菊花'], lat: 33.87, lng: 115.78, avgPrice: '28.50' },
  { name: '安国', province: '河北', herbs: ['柴胡', '桔梗'], lat: 38.42, lng: 115.33, avgPrice: '35.20' },
  { name: '文山', province: '云南', herbs: ['三七'], lat: 23.37, lng: 104.24, avgPrice: '128.00' },
  { name: '岷县', province: '甘肃', herbs: ['当归'], lat: 34.44, lng: 104.04, avgPrice: '52.80' },
  { name: '陇西', province: '甘肃', herbs: ['黄芪'], lat: 34.99, lng: 104.63, avgPrice: '22.60' },
  { name: '平邑', province: '山东', herbs: ['金银花'], lat: 35.50, lng: 117.63, avgPrice: '145.00' },
  { name: '中宁', province: '宁夏', herbs: ['枸杞'], lat: 37.48, lng: 105.68, avgPrice: '48.50' },
  { name: '武陟', province: '河南', herbs: ['地黄'], lat: 35.10, lng: 113.40, avgPrice: '18.30' },
  { name: '抚松', province: '吉林', herbs: ['人参'], lat: 42.22, lng: 127.15, avgPrice: '265.00' },
]

const defaultHeatData: HeatPoint[] = [
  { lat: 33.87, lng: 115.77, intensity: 0.8 },
  { lat: 38.42, lng: 115.33, intensity: 0.3 },
  { lat: 23.37, lng: 104.33, intensity: 0.6 },
  { lat: 34.44, lng: 104.02, intensity: 0.2 },
  { lat: 35.08, lng: 105.20, intensity: 0.1 },
  { lat: 35.50, lng: 117.63, intensity: 0.9 },
  { lat: 37.48, lng: 105.67, intensity: 0.4 },
  { lat: 35.10, lng: 113.40, intensity: 0.5 },
  { lat: 42.33, lng: 127.15, intensity: 0.3 },
]

const mapRef = ref<HTMLDivElement>()
let map: L.Map | null = null
let markerLayerGroup: L.LayerGroup | null = null
let heatLayer: L.Layer | null = null

function getHeatPoints(): Array<[number, number, number]> {
  const source = props.heatData.length > 0 ? props.heatData : defaultHeatData
  return source.map((p) => [p.lat, p.lng, p.intensity])
}

function createMarkerLayer(): L.LayerGroup {
  const group = L.layerGroup()
  defaultOriginData.forEach((item) => {
    const icon = L.divIcon({
      className: 'origin-marker',
      html: `<div class="origin-marker__pin"><span class="origin-marker__dot"></span></div>`,
      iconSize: [24, 24],
      iconAnchor: [12, 24],
      popupAnchor: [0, -24],
    })

    const popupContent = `
      <div class="origin-popup">
        <div class="origin-popup__title">${item.name}（${item.province}）</div>
        <div class="origin-popup__row"><span class="origin-popup__label">主要品种：</span>${item.herbs.join('、')}</div>
        <div class="origin-popup__row"><span class="origin-popup__label">当前均价：</span>¥${item.avgPrice}/kg</div>
      </div>
    `

    L.marker([item.lat, item.lng], { icon })
      .addTo(group)
      .bindPopup(popupContent, {
        maxWidth: 260,
        className: 'origin-popup-container',
      })
  })
  return group
}

function createHeatLayer(): L.Layer {
  const points = getHeatPoints()
  return (L as any).heatLayer(points, {
    radius: 40,
    blur: 30,
    maxZoom: 8,
    gradient: {
      0.0: '#27ae60',
      0.5: '#f39c12',
      1.0: '#e74c3c',
    },
  })
}

function initMap() {
  if (!mapRef.value) return

  map = L.map(mapRef.value, {
    center: [35.86, 104.19],
    zoom: 4,
    zoomControl: true,
    attributionControl: true,
  })

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
    maxZoom: 18,
  }).addTo(map)

  markerLayerGroup = createMarkerLayer()
  heatLayer = createHeatLayer()

  const baseLayers: Record<string, L.Layer> = {}
  const overlayLayers: Record<string, L.Layer> = {
    '标记点': markerLayerGroup,
    '热力图': heatLayer,
  }

  if (props.showHeatmap) {
    heatLayer.addTo(map)
  }
  markerLayerGroup.addTo(map)

  L.control.layers(baseLayers, overlayLayers, { position: 'topright' }).addTo(map)
}

function updateHeatLayer() {
  if (!map || !heatLayer) return

  if (heatLayer && map.hasLayer(heatLayer)) {
    map.removeLayer(heatLayer)
  }

  heatLayer = createHeatLayer()

  if (props.showHeatmap) {
    heatLayer.addTo(map)
  }
}

watch(() => props.heatData, () => {
  updateHeatLayer()
}, { deep: true })

watch(() => props.showHeatmap, (val) => {
  if (!map || !heatLayer) return
  if (val && !map.hasLayer(heatLayer)) {
    heatLayer.addTo(map)
  } else if (!val && map.hasLayer(heatLayer)) {
    map.removeLayer(heatLayer)
  }
})

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
    markerLayerGroup = null
    heatLayer = null
  }
})
</script>

<style scoped lang="scss">
@import '@/styles/variables.scss';

.origin-map {
  width: 100%;
  height: 420px;
  border-radius: 4px;
  overflow: hidden;
}

:deep(.origin-marker) {
  background: none;
  border: none;
}

:deep(.origin-marker__pin) {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

:deep(.origin-marker__dot) {
  width: 14px;
  height: 14px;
  background: $primary-color;
  border: 2px solid #fff;
  border-radius: 50%;
  box-shadow: 0 2px 6px rgba(231, 76, 60, 0.5);
  display: block;
}

:deep(.origin-popup-container .leaflet-popup-content-wrapper) {
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

:deep(.origin-popup) {
  padding: 4px 0;
  min-width: 160px;
}

:deep(.origin-popup__title) {
  font-size: 15px;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid $border-color;
}

:deep(.origin-popup__row) {
  font-size: 13px;
  color: $text-secondary;
  line-height: 1.8;
}

:deep(.origin-popup__label) {
  color: $text-color;
  font-weight: 500;
}
</style>
