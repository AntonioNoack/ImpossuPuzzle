package me.antonio.noack.gl.texture

import android.opengl.GLES20.*
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.floor

class NoiseTexture(linear: Boolean, size: Int,
                   stageMultiplier: Float = 0.3f,
                   effectMultiplier: Float = 1.5f): Texture(GL_REPEAT, linear, size, size){

    init {

        // todo generate perlin based noise...

        val random = Random()

        // completely random for now
        val bytes = ByteArray(size * size * 4)
        val length = size * size

        val layersR = ArrayList<NoiseLayer>()
        val layersG = ArrayList<NoiseLayer>()
        val layersB = ArrayList<NoiseLayer>()
        val layersA = ArrayList<NoiseLayer>()
        var layerSize = size.toFloat() * stageMultiplier
        var effect = 1f
        var sum = 0f
        while(layerSize > 2){

            layersR.add(NoiseLayer(layerSize.toInt(), size, effect, random))
            layersG.add(NoiseLayer(layerSize.toInt(), size, effect, random))
            layersB.add(NoiseLayer(layerSize.toInt(), size, effect, random))
            layersA.add(NoiseLayer(layerSize.toInt(), size, effect, random))
            sum += effect

            layerSize *= stageMultiplier
            effect *= effectMultiplier
        }

        val fSum = 127f / sum

        val pixels = ByteBuffer.allocateDirect(length * 4)
        for(i in 0 until size){
            for(j in 0 until size){
                var r = 0f
                var g = 0f
                var b = 0f
                var a = 0f
                for(l in layersR){ r += l.getRelative(i, j) }
                for(l in layersG){ g += l.getRelative(i, j) }
                for(l in layersB){ b += l.getRelative(i, j) }
                for(l in layersA){ a += l.getRelative(i, j) }
                pixels.put((127f + fSum * r).toByte())
                pixels.put((127f + fSum * g).toByte())
                pixels.put((127f + fSum * b).toByte())
                pixels.put((127f + fSum * a).toByte())
            }
        }
        pixels.position(0)

        val type = GL_RGBA
        glTexImage2D(GL_TEXTURE_2D, 0, type, width, height, 0, type, GL_UNSIGNED_BYTE, pixels)

    }

    class NoiseLayer(val size: Int, outsideSize: Int, mul: Float, random: Random){

        val fSize = size.toFloat() / outsideSize

        val values = FloatArray(size * size){
            (random.nextFloat() - 0.5f) * mul
        }

        fun getRelative(x: Int, y: Int) = get(x * fSize, y * fSize)

        fun get(x: Float, y: Float): Float {
            val rawI = floor(x)
            val rawJ = floor(y)
            val fractI = x - rawI
            val fractJ = y - rawJ
            val fi2 = 1f - fractI
            val fj2 = 1f - fractJ
            val i = rawI.toInt()
            val j = rawJ.toInt()
            val i2 = if(i+1 == size) 0 else i+1
            val j2 = if(j+1 == size) 0 else j+1
            return mix(
                mix(get(i, j), get(i2, j), fi2, fractI),
                mix(get(i, j2), get(i2, j2), fi2, fractI),
                fj2, fractJ
            )
        }

        fun get(x: Int, y: Int): Float = values[x + y*size]
        fun mix(a: Float, b: Float, fa: Float, fb: Float) = fa * a + fb * b


    }

}