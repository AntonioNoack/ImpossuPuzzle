package me.antonio.noack.gui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLES20.GL_UNPACK_ALIGNMENT
import android.opengl.GLES20.glPixelStorei
import android.opengl.GLES30.GL_UNPACK_ROW_LENGTH
import android.opengl.GLUtils
import android.os.Build
import me.antonio.noack.gl.texture.Texture
import me.antonio.noack.gui.Gfx.paint

class TextLine(var lastTime: Long, text: String, val size: Float){

    val texture: Texture

    init {

        println("create $text @$size")

        if(size < 1f){

            texture = Texture(GLES20.GL_CLAMP_TO_EDGE, false, 1, 1)

        } else {

            // skew? maybe add later :)
            paint.textSize = size
            paint.color = 0xffff0000.toInt()

            val width = (1f + paint.measureText(text)).toInt()
            val height = (size * factorY).toInt()
            // creating the bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
            val canvas = Canvas(bitmap)
            canvas.drawText(text, width * .5f, (height - (paint.ascent() + paint.descent())) * .5f, paint)
            // creating the texture
            if(Build.VERSION.SDK_INT > 17){
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0) }
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            texture = Texture(GLES20.GL_CLAMP_TO_EDGE, false, bitmap.width, bitmap.height)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()

        }

    }

    fun destroy(){
        texture.destroy()
    }

    companion object {

        // für das g und so...
        var factorY = 1.5f

    }

}