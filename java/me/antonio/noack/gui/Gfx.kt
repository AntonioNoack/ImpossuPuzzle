package me.antonio.noack.gui

import android.graphics.Paint
import android.opengl.GLES20.glUniform3f
import android.opengl.GLES20.glUniform4f
import me.antonio.noack.gl.Initializable
import me.antonio.noack.gl.Program
import me.antonio.noack.gl.structure.FlatBuffer
import me.antonio.noack.gl.texture.Texture
import me.antonio.noack.gui.TextLine.Companion.factorY
import kotlin.math.log

object Gfx: Initializable({

    FlatBuffer.init()

    Gfx.apply {

        val transformationSource = "" +
                "attribute vec2 a;\n" +
                "uniform vec4 posSize;\n" +
                "void main(){" +
                "   gl_Position = vec4(posSize.xy + posSize.zw * a, .5, 1.);\n" +
                "   uv = a;\n" +
                "}"

        val varyingSource = "" +
                "varying vec2 uv;\n"

        colorProgram = Program(varyingSource, transformationSource,
            "" +
                    "uniform vec4 color;\n" +
                    "void main(){" +
                    // todo Kacheln, die transparent werden, ungleichmäßig, schwankend, als Heraus- und Aufflackern? :D
                    // todo braucht hier aber eine extra Textur / Funktion, mit der wir dann multiplizieren :)
                    // todo vielleicht ist es auch ratsam, es auf eine Zwischentextur zu malen... Posteffects haben wir ja vermutlich sowieso :)
                    "   gl_FragColor = color + .1 * uv.rgrg;\n" +
                    "}", false)

        imageProgram = Program(varyingSource, transformationSource,
            "" +
                    "uniform sampler2D image;\n" +
                    "void main(){" +
                    "   gl_FragColor = texture2D(image, uv);\n" +
                    "}", false)
        imageProgram.setTextureSlot("image",0)

        tintedImageProgram = Program(varyingSource, transformationSource,
            "" +
                    "uniform sampler2D image;\n" +
                    "uniform vec3 color;\n" +
                    "void main(){" +
                    "	vec4 raw = texture2D(image, uv);\n"+
                    "   gl_FragColor = raw.a > 0.5 ? vec4(color, 1.) : vec4(0.);\n" +
                    "}", false)
        tintedImageProgram.setTextureSlot("image",0)

        texts = HashMap()

    }


}){

    private lateinit var colorProgram: Program
    private lateinit var imageProgram: Program
    private lateinit var tintedImageProgram: Program

    var sizeX = 1f
    var sizeY = 1f

    private fun setSize(uniform: Int, x: Float, y: Float, width: Float, height: Float){
        glUniform4f(uniform, x*sizeX*2-1, 1-y*sizeY*2, width*sizeX*2, height*sizeY*-2)
    }

    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int, opacity: Float){
        colorProgram.use()
        setSize(colorProgram["posSize"], x, y, width, height)
        glUniform4f(colorProgram["color"], ((color shr 16) and 255) /255f, ((color shr 8) and 255)/255f, (color and 255)/255f, opacity)
        colorProgram.draw(FlatBuffer.small)
    }

    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int){
        drawRect(x, y, width, height, color, ((color shr 24) and 255)/255f)
    }

    fun drawTintedImage(x: Float, y: Float, width: Float, height: Float, image: Texture, tint: Int = -1, opacity: Float = ((tint shr 24) and 255)/255f){
        tintedImageProgram.use()
        image.use(0)
        setSize(tintedImageProgram["posSize"], x, y, width, height)
        glUniform3f(tintedImageProgram["color"], tint.shr(16).and(255)/255f, tint.shr(8).and(255)/255f, tint.and(255)/255f)
        tintedImageProgram.draw(FlatBuffer.small)
    }

    fun drawImage(x: Float, y: Float, width: Float, height: Float, image: Texture, tint: Int = -1, opacity: Float = ((tint shr 24) and 255)/255f){
        imageProgram.use()
        image.use(0)
        setSize(imageProgram["posSize"], x, y, width, height)
        imageProgram.draw(FlatBuffer.small)
    }

    private var texts = HashMap<Int, HashMap<String, TextLine>>()

    fun tick(time: Long, timeout: Long = 20000000000){
        val deadTime = time - timeout
        for((_, map) in texts){
            val iterator = map.iterator()
            for((_, element) in iterator){
                if(element.lastTime < deadTime){
                    element.destroy()
                    iterator.remove()
                }
            }
        }
    }

    fun drawText(time: Long, alignment: Alignment, x: Float, y: Float, width: Float, height: Float, text: String, color: Int, opacity: Float = ((color shr 24) and 255)/255f, pixelation: Float = 1f){
        val texture = requestText(time, text, height * pixelation)
        val realX: Float
        val realY: Float
        val realWidth: Float
        val realHeight: Float
        val newY = y - height * (factorY - 1f) * 0.5f
        val newHeight = height * factorY
        if(texture.width * newHeight > texture.height * width){
            // matches x -> center y
            realWidth = width
            realX = x
            realHeight = width * texture.height / texture.width
            realY = when(alignment){
                Alignment.MIN -> newY
                Alignment.CENTER -> newY + (newHeight - realHeight)/2
                Alignment.MAX -> newY + newHeight - realHeight
            }
        } else {
            // matches y -> center x
            realHeight = newHeight
            realY = newY
            realWidth = newHeight * texture.width / texture.height
            realX = when(alignment){
                Alignment.MIN -> x
                Alignment.CENTER -> x + (width - realWidth)/2
                Alignment.MAX -> x + width - realWidth
            }
        }
        drawTintedImage(realX, realY, realWidth, realHeight, texture, color, opacity)
    }

    fun drawText(time: Long, alignment: Alignment, alignY: Boolean, x: Float, y: Float, width: Float, height: Float, text: String, color: Int, opacity: Float = ((color shr 24) and 255)/255f, pixelation: Float = 1f){
        val texture = requestText(time, text, height * pixelation)
        val realX: Float
        val realY: Float
        val realWidth: Float
        val realHeight: Float
        val newY = y - height * (factorY - 1) * 0.5f
        val newHeight = height * factorY
        if(alignY){
            // matches x -> center y
            realWidth = width
            realX = x
            realHeight = width * texture.height / texture.width
            realY = when(alignment){
                Alignment.MIN -> newY
                 Alignment.CENTER -> newY + (newHeight - realHeight)/2
                Alignment.MAX -> newY + newHeight - realHeight
            }
        } else {
            // matches y -> center x
            realHeight = newHeight
            realY = newY
            realWidth = newHeight * texture.width / texture.height
            realX = when(alignment){
                Alignment.MIN -> x
                Alignment.CENTER -> x + (width - realWidth)/2
                Alignment.MAX -> x + width - realWidth
            }
        }
        drawTintedImage(realX, realY, realWidth, realHeight, texture, color, opacity)
    }

    private fun requestText(time: Long, text: String, size: Float): Texture {
        val sizeIndex = log(size, 1.1f).toInt()
        val map: HashMap<String, TextLine>? = texts[sizeIndex]
        return if(map == null){
            val element = TextLine(time, text, size)
            texts[sizeIndex] = hashMapOf(
                text to element
            )
            element.texture
        } else {
            var element = map[text]
            if(element == null){
                element = TextLine(time, text, size)
                map[text] = element
                element.texture
            } else {
                element.lastTime = time
                element.texture
            }
        }
    }

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = -1
        paint.textAlign = Paint.Align.CENTER
    }


}