package me.antonio.noack.gl.buffer

import java.nio.ByteBuffer
import java.nio.FloatBuffer

object Utils {

    fun floatBuffer(capacity: Int): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(capacity * 4)
		.order(java.nio.ByteOrder.nativeOrder())
        return buffer.asFloatBuffer()
    }

}