package me.antonio.noack.maths

import me.antonio.noack.maths.Utils.mix
import me.antonio.noack.maths.Utils.closestCtoAB
import me.antonio.noack.maths.Utils.sq
import kotlin.math.exp
import kotlin.math.floor

object Utils {

    fun roughly(a: Double, b: Double, fac: Double): Boolean {
        return if(a > b) roughly(b, a, fac)
        else a * fac >= b
    }

    fun sigmoid(a: Double): Double {
        return 1.0 / (1.0 + exp(-a))
    }

    fun sigmoid(a: Float): Float {
        return 1f / (1f + exp(-a))
    }

    fun signed11(i: Int): Int {
        val v = i and 2047
        return if(v > 1023) v - 2048 else v
    }

    fun fract(f: Float): Float {
        return f - floor(f)
    }

    fun fract(d: Double): Double {
        return d - Math.floor(d)
    }

    fun clamp(x: Double, min: Double, max: Double): Double {
        return if(x < min) min else if(x < max) x else max
    }

    fun clamp(x: Float, min: Float, max: Float): Float {
        return if(x < min) min else if(x < max) x else max
    }

    fun clamp(x: Int, min: Int, max: Int): Int {
        return if(x < min) min else if(x < max) x else max
    }

    fun sq(x: Float): Float {
        return x*x
    }

    fun sq(x: Double): Double {
        return x*x
    }

    fun sq(x: Float, y: Float = 0f): Float {
        return x*x+y*y
    }

    fun sq(x: Int, y: Int): Int {
        return x*x+y*y
    }

    fun sq(x: Double, y: Double): Double {
        return x*x+y*y
    }

    fun sq(x: Float, y: Float, z: Float): Float {
        return x*x+y*y+z*z
    }

    fun sq(x: Double, y: Double, z: Double): Double {
        return x*x+y*y+z*z
    }

    fun mix(a: Float, b: Float, f: Float): Float {
        return (1f-f)*a+f*b
    }

    fun mix(a: Int, b: Int, f: Float, fm1: Float = 1f - f): Float {
        return fm1*a+f*b
    }

    fun mix(a: Double, b: Double, f: Double): Double {
        return (1.0-f)*a+f*b
    }

    fun closestCtoAB(ax: Double, ay: Double, bx: Double, by: Double, cx: Double, cy: Double): Double {
        return closestCto0B(bx - ax, by - ay, cx - ax, cy - ay)
    }

    fun closestCto0B(bx: Double, by: Double, cx: Double, cy: Double): Double {
        val bsq = bx*bx+by*by
        val bc = bx*cx+by*cy
        return bc / bsq
    }

}

fun main(){
    val ax = 5.0
    val ay = 7.0
    val bx = 15.0
    val by = 13.0
    val cx = 7.0
    val cy = 1.0
    var t = closestCtoAB(ax, ay, bx, by, cx, cy)
    println(t)
    val dx = mix(ax, bx, t)
    val dy = mix(ay, by, t)
    println(sq(dx-7.0, dy-1.0))
    t -= 0.01
    val dx2 = mix(ax, bx, t)
    val dy2 = mix(ay, by, t)
    println(sq(dx2-7.0, dy2-1.0))
    t += 0.02
    val dx3 = mix(ax, bx, t)
    val dy3 = mix(ay, by, t)
    println(sq(dx3-7.0, dy3-1.0))
}