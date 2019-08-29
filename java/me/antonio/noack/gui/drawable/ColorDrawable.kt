package me.antonio.noack.gui.drawable

import me.antonio.noack.gui.Gfx

class ColorDrawable(var color: Int, var opacity: Float = ((color shr 24) and 255)/255f):
    Drawable {
    override fun draw(x: Float, y: Float, width: Float, height: Float) {
        Gfx.drawRect(x, y, width, height, color, opacity)
    }
}