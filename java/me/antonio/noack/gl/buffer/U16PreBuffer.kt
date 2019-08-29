package me.antonio.noack.gl.buffer

import android.opengl.GLES20.*
import java.lang.RuntimeException
import java.nio.ByteBuffer

class U16PreBuffer(approximateSize: Int = 64){

    val data = ArrayList<Byte>(approximateSize)

    fun put(s: Int){
        data.add((s and 255).toByte())
        data.add((s shr 8).toByte())
    }

    fun put(vararg s: Int){
        for(i in s){
            put(i)
        }
    }

    fun toBuffer(alignment: Alignment, usage: Int = GL_STATIC_DRAW): Buffer {
        if(data.size % alignment.byteSize != 0) throw RuntimeException("not matching the alignment: ${data.size} % ${alignment.byteSize}")
        val buffer = Buffer(alignment)
        if(data.isEmpty()) return buffer
        val sBuffer = ByteBuffer.allocateDirect(data.size)
            .put(data.toByteArray())
            .rewind()
        val ints = intArrayOf(-1)
        glGenBuffers(1, ints, 0)
        val glBuffer = ints[0]
        glBindBuffer(GL_ARRAY_BUFFER, glBuffer)
        glBufferData(GL_ARRAY_BUFFER, data.size, sBuffer, usage)
        buffer.code = glBuffer
        buffer.count = data.size / alignment.byteSize
        return buffer
    }

}