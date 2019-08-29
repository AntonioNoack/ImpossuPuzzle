package me.antonio.noack.gui.interactions

interface Clickable<V> {
    fun onClick(element: V, x: Float, y: Float, time: Long, deltaTime: Long): Boolean
}