package me.antonio.noack.maths

import kotlin.math.cos
import kotlin.math.sin

class Quaternion(axisX: Double, axisY: Double, axisZ: Double, angle: Double){

    var r: Double
    var i: Double
    var j: Double
    var k: Double

    init {

        val ah = angle * 0.5
        val c = cos(ah)
        val s = sin(ah)

        r = c
        i = axisX * s
        j = axisY * s
        k = axisZ * s

    }

}