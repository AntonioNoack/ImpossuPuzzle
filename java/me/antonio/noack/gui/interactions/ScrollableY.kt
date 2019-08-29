package me.antonio.noack.gui.interactions

interface ScrollableY<V> {
    fun onScroll(element: V, x: Float, y: Float, deltaY: Float, time: Long, deltaTime: Long)
}