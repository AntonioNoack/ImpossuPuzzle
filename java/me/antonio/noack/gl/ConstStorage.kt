package me.antonio.noack.gl

import me.antonio.noack.gl.buffer.Utils

object ConstStorage {

    val M4x4Buffers = Array(20){ Utils.floatBuffer(16) }
    val M3x3Buffers = Array(20){ Utils.floatBuffer(9) }

    val LOD_COUNT = 5

}