package me.antonio.noack.gl.structure

import me.antonio.noack.gl.Initializable
import me.antonio.noack.gl.buffer.Alignment
import me.antonio.noack.gl.buffer.Buffer
import me.antonio.noack.gl.buffer.Type
import me.antonio.noack.gl.buffer.U8PreBuffer

object FlatBuffer: Initializable({

    FlatBuffer.small = U8PreBuffer(byteArrayOf(
        0, 0,
        0, 1,
        1, 1,
        0, 0,
        1, 1,
        1, 0
    )).toBuffer(Alignment(Type.S8, 2))

    FlatBuffer.large = U8PreBuffer(byteArrayOf(
        -1, -1,
        -1, 1,
        1, 1,
        -1, -1,
        1, 1,
        1, -1
    )).toBuffer(Alignment(Type.S8, 2))

}){

    lateinit var small: Buffer
    lateinit var large: Buffer

}