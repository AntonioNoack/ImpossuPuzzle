package me.antonio.noack.gui.elements

import me.antonio.noack.gui.Alignment
import me.antonio.noack.gui.Gfx

/**
 * an element with a single line of text..., centered
 * */
class TextElement(var text: String, size: Size, parent: Element?): Element(size, parent){

    var textAlign = Alignment.MIN

    var color = 0
    var opacity = 1f

    var fontSize = 75f
    var relativeSpace = 0f

    private val lines = ArrayList<String>()
    private fun calculateLines(maxWidth: Float){
        lines.clear()
        if(text.isNotBlank()){
            val paint = Gfx.paint
            paint.textSize = fontSize
            val spaceWidth = paint.measureText(" ")
            text.split('\n').forEach { line ->
                val parts = line.split(' ')
                val widths = parts.map { part -> paint.measureText(part) }
                var x = 0f
                var lineToAdd = parts[0]
                parts.forEachIndexed { index, part ->
                    if(index > 0){
                        val w = x + 3 * spaceWidth + widths[index]
                        if(w >= maxWidth){
                            lines.add(lineToAdd)
                            lineToAdd = part
                            x = -spaceWidth
                        } else {
                            lineToAdd += " $part"
                            x = w
                        }
                    }
                }
                if(lineToAdd.isNotEmpty()){
                    lines.add(lineToAdd)
                }
            }
        }
    }

    override fun onMeasure(x: Float, y: Float, width: Float, height: Float) {
        // todo flip to multiple lines, if too much...
        this.x = x
        this.y = y
        this.width = width
        calculateLines(width - 2 * borderSize)
        this.height = (fontSize + 1f) * lines.size * (1f + relativeSpace) + 2 * borderSize
    }

    override fun onDraw(time: Long) {
        super.onDraw(time)
        val width = width - 2 * borderSize
        val x0 = this.x + borderSize
        val height = (fontSize + 1f)
        val offset = height * (1f + relativeSpace)
        var y = this.y + borderSize + height * relativeSpace * .5f
        lines.forEach { line ->
            Gfx.drawText(time, textAlign, false, x0, y, width, height, line, color, opacity)
            y += offset
        }
    }

}