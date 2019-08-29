package me.antonio.noack.gl.buffer

import android.opengl.GLES20.*
import java.lang.RuntimeException
import java.nio.ByteBuffer

class U8PreBuffer(approximateSize: Int = 64){

    constructor(bytes: ByteArray):this(bytes.size){
        for(byte in bytes) data.add(byte)
    }

    val data = ArrayList<Byte>(approximateSize)

    fun put(s: Byte){
        data.add(s)
    }

    fun put(vararg s: Byte){
        data.addAll(s.toList())
    }

    fun toBuffer(alignment: Alignment, usage: Int = GL_STATIC_DRAW): Buffer {
        if(data.size % alignment.byteSize != 0) throw RuntimeException("not matching the alignment: ${data.size} % ${alignment.byteSize}")
        val buffer = Buffer(alignment)
        if(data.isEmpty()) return buffer
        val sBuffer = ByteBuffer.allocateDirect(data.size)
            .put(data.toByteArray())
            .position(0)
        val ints = intArrayOf(-1)
        glGenBuffers(1, ints, 0)
        val glBuffer = ints[0]
        println("buffer $glBuffer for size ${data.size}")
        glBindBuffer(GL_ARRAY_BUFFER, glBuffer)
        glBufferData(GL_ARRAY_BUFFER, data.size, sBuffer, usage)
        buffer.code = glBuffer
        buffer.count = data.size / alignment.byteSize
        return buffer
    }

}