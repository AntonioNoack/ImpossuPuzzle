package me.antonio.noack.gl.buffer

import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glVertexAttribPointer
import me.antonio.noack.gl.Program

class Alignment private constructor(){

    constructor(elements: List<Triple<String, Type, Int>>, program: Program):this(){

        var byteIndex = 0
        val attrs = program.attributes
        for((name, type, count) in elements){
            val index = attrs[name] ?: -1
            if(index > -1){
                ordered.add(
                    Data(
                        index,
                        count,
                        type.type,
                        type.normalized,
                        byteIndex
                    )
                )
            }
            byteIndex += count * type.bytes
        }

        ordered.sortWith(Comparator { a, b -> a.attributeIndex - b.attributeIndex })
        byteSize = byteIndex

        // println(ordered.joinToString("|"))

    }

    constructor(type: Type, count: Int):this(){
        ordered.add(Data(0, count, type.type, type.normalized, 0))
        byteSize = count * type.bytes
    }

    // attribute getNodeIndex, scale, byte getNodeIndex
    private val ordered = ArrayList<Data>()
    var byteSize = 0

    fun align(){
        ordered.forEach { data ->
            glVertexAttribPointer(data.attributeIndex, data.count, data.type, data.normalized, byteSize, data.byteIndex)
            glEnableVertexAttribArray(data.attributeIndex)
        }
    }

    class Data(val attributeIndex: Int, val count: Int, val type: Int, val normalized: Boolean, val byteIndex: Int){
        override fun toString(): String {
            return "$attributeIndex, $count, $type, $normalized, $byteIndex"
        }
    }

}