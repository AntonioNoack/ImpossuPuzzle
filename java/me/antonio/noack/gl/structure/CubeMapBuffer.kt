package me.antonio.noack.gl.structure

import me.antonio.noack.gl.*
import me.antonio.noack.gl.buffer.Alignment
import me.antonio.noack.gl.buffer.Buffer
import me.antonio.noack.gl.buffer.Type
import me.antonio.noack.gl.buffer.U8PreBuffer

// done by Anno 2205 in the same way...
class CubeMapBuffer(program: Program){

    val buffer: Buffer

    init {

        val pre = U8PreBuffer()
        pre.data.addAll(listOf(

            0, 0, 0, 0, 0,
            0, 0, 1, 0, 1,
            0, 1, 1, 1, 1,
            0, 0, 0, 0, 0,
            0, 1, 1, 1, 1,
            0, 1, 0, 1, 0,

            1, 0, 0, 0, 0,
            1, 1, 1, 1, 1,
            1, 0, 1, 0, 1,
            1, 0, 0, 0, 0,
            1, 1, 0, 1, 0,
            1, 1, 1, 1, 1,

            0, 0, 0, 0, 0,
            0, 1, 0, 0, 1,
            1, 1, 0, 1, 1,
            0, 0, 0, 0, 0,
            1, 1, 0, 1, 1,
            1, 0, 0, 1, 0,

            0, 0, 1, 0, 0,
            1, 1, 1, 1, 1,
            0, 1, 1, 0, 1,
            0, 0, 1, 0, 0,
            1, 0, 1, 1, 0,
            1, 1, 1, 1, 1,

            0, 0, 0, 0, 0,
            1, 0, 0, 0, 1,
            1, 0, 1, 1, 1,
            0, 0, 0, 0, 0,
            1, 0, 1, 1, 1,
            0, 0, 1, 1, 0,

            0, 1, 0, 0, 0,
            1, 1, 1, 1, 1,
            1, 1, 0, 0, 1,
            0, 1, 0, 0, 0,
            0, 1, 1, 1, 0,
            1, 1, 1, 1, 1

        ))

        buffer = pre.toBuffer(
            Alignment(
                listOf(
                    Triple("loc", Type.U8, 3),
                    Triple("uv", Type.U8, 2)
                ),
                program
            )
        )

        println("buffer count: ${buffer.count}")

    }

}