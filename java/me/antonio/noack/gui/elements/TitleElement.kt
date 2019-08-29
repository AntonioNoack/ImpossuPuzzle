package me.antonio.noack.gui.elements

import me.antonio.noack.gui.Alignment
import me.antonio.noack.gui.Gfx

/**
 * an element with a single line of text..., centered
 * */
class TitleElement(var text: String, size: Size, parent: Element?): Element(size, parent){

    var textAlign = Alignment.CENTER

    var color = -1

    var fontSize = 50f
    var fontSizeProvider: (() -> Float)? = null

    override fun onMeasure(x: Float, y: Float, width: Float, height: Float) {
        // todo flip to multiple lines, if too much...
        this.x = x
        this.y = y
        this.width = width
        this.fontSize = fontSizeProvider?.invoke() ?: fontSize
        this.height = fontSize+1f
    }

    override fun onDraw(time: Long) {
        super.onDraw(time)

        Gfx.drawRect(
            x+borderSize,
            y+borderSize,
            width-2*borderSize,
            height-2*borderSize, color)

        Gfx.drawText(time, textAlign,
            x+borderSize,
            y+borderSize,
            width-2*borderSize,
            height-2*borderSize, text, color)

    }

}