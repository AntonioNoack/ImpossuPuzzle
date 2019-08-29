package me.antonio.noack.gui

import me.antonio.noack.gui.elements.Element

class Dialog(size: Size, parent: Element?, var alignmentX: Alignment, var alignmentY: Alignment): Element(size, parent){

    var child: Element? = null

    override fun onMeasure(x: Float, y: Float, width: Float, height: Float) {
        val child = child
        if(child != null){
            child.measure(x + borderSize, y + borderSize, width - 2*borderSize, height - 2*borderSize)
            this.x = child.x - borderSize
            this.y = child.y - borderSize
            this.width = child.width + 2*borderSize
            this.height = child.height + 2*borderSize
        }
    }

    override fun onDraw(time: Long) {
        super.onDraw(time)
        child?.draw(time)
    }

}