package me.antonio.noack.gui.interactions

interface Touchable<V> {
    fun onTouch(element: V, x: Float, y: Float, dx: Float, dy: Float, time: Long, deltaTime: Long)
}