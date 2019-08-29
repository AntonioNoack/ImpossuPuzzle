package me.antonio.noack.impossupuzzle

import androidx.core.math.MathUtils
import me.antonio.noack.maths.Utils
import kotlin.math.sqrt

object ColorHelper {

    fun brightness(r: Float, g: Float, b: Float): Float {
        return sqrt(0.299f*r*r + 0.587f*g*g + 0.114f*b*b)
    }

    fun normalize(color: Int, length: Float = 1f): Int {
        if(color.and(0xffffff) == 0) return 0xffffff
        val r = color.shr(16).and(255).toFloat()
        val g = color.shr(8).and(255).toFloat()
        val b = color.and(255).toFloat()
        val factor = length / brightness(r, g, b)
        return rgbFrom01(r * factor, g * factor, b * factor)
    }

    fun rgbFrom01(r: Float, g: Float, b: Float): Int = rgb((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())

    fun rgb(r: Int, g: Int, b: Int): Int {
        return (MathUtils.clamp(r, 0, 255).shl(16)) or (MathUtils.clamp(g, 0, 255).shl(8) or (MathUtils.clamp(
            b,
            0,
            255
        )))
    }

    fun mixRGB(a: Int, b: Int, f: Float, fm1: Float = 1f - f): Int {
        return rgb(
            Utils.mix(a.shr(16).and(255), b.shr(16).and(255), f, fm1).toInt(),
            Utils.mix(a.shr(8).and(255), b.shr(8).and(255), f, fm1).toInt(),
            Utils.mix(a.and(255), b.and(255), f, fm1).toInt()
        )
    }

}