package me.antonio.noack.gui.elements

import androidx.core.math.MathUtils.clamp
import me.antonio.noack.gui.Gfx
import me.antonio.noack.gui.interactions.ScrollableY

open class VerticalLayout(size: Element.Size, parent: Element, val shownElements: Int): Element(size, parent), ScrollableY<Element> {

    var children = ArrayList<Element>()

    var paddingBetweenElements = 5f

    var scrollbarWidth = 5f

    var scrollbarBackground = 0xbbbbbb
    var scrollbarColor = 0x555555

    var scrollMaximum = 0f
    var scrollPosition = 0f

    override fun onScroll(element: Element, x: Float, y: Float, deltaY: Float, time: Long, deltaTime: Long) {
        scrollPosition = clamp(scrollPosition + deltaY, 0f, scrollMaximum)
        invalidate()
    }

    override fun onMeasure(x: Float, y: Float, width: Float, height: Float) {
        this.x = x
        this.y = y
        this.width = width
        val widthWB = width - scrollbarWidth
        if(shownElements > 0){
            // todo show the expectedSize of Element.Size * shownElements
        } else {

        }
    }

    open fun drawScrollbar(){
        val center = height * scrollPosition / scrollMaximum
        val size = 0.5f * height * height / (height + scrollMaximum)
        val x = this.x + this.width - scrollbarWidth
        val lower = center - size
        val upper = center + size
        if(lower > 0f){
            Gfx.drawRect(x, scrollbarWidth, y, lower, scrollbarBackground) }
        Gfx.drawRect(x, scrollbarWidth, y + lower, upper - lower, scrollbarColor)
        if(upper < height){
            Gfx.drawRect(x, scrollbarWidth, y + upper, height - upper, scrollbarBackground) }
    }

    override fun onDraw(time: Long) {
        children.forEach { child ->
            child.draw(time)
        }
        drawScrollbar()
    }

}