package me.antonio.noack.gui.interactions

interface Zoomable<V> {
    fun onZoom(element: V, centerX: Float, centerY: Float, radius: Float, relativeScale: Float, time: Long, deltaTime: Long)
}