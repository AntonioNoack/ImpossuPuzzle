package me.antonio.noack.gui.drawable

import me.antonio.noack.gl.texture.Texture
import me.antonio.noack.gui.Gfx

class ImageDrawable(val texture: Texture): Drawable {
    override fun draw(x: Float, y: Float, width: Float, height: Float) {
        Gfx.drawTintedImage(x, y, width, height, texture)
    }
}