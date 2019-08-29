package me.antonio.noack.gl.buffer

class Buffer(val alignment: Alignment){

    var code = -1
    var elementsCode = -1
    var count = 0
    var strippedFlag = false

    fun clone(alignment: Alignment): Buffer {
        val buffer = Buffer(alignment)
        buffer.code = code
        buffer.elementsCode = elementsCode
        buffer.count = count
        buffer.strippedFlag = strippedFlag
        return buffer
    }
}

