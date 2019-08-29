package me.antonio.noack.gui.interactions

interface ScrollableX<V> {
    fun onScroll(element: V, x: Float, y: Float, deltaX: Float, time: Long, deltaTime: Long)
}