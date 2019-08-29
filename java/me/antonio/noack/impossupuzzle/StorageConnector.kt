package me.antonio.noack.impossupuzzle

import java.io.DataInputStream
import java.io.DataOutputStream

class StorageConnector(val input: DataInputStream): Connector {

    init {
        for(char in MAGIC){
            assert(input.readByte().toChar() == char)
        }
    }

    val version = input.readInt()
    val states = input.readUnsignedShort()
    val sx = input.readInt()
    val sy = input.readInt()
    val df = input.readInt()
    val clickCount = input.readInt()
    val randomInitKey = input.readLong()
    val initPerDot = input.readFloat()

    override fun connect(game: Game){

        assert(game.sx == sx)
        assert(game.sy == sy)
        assert(game.states == states)

        for(y in 0 until sy){
            for(x in 0 until sx){
                val count = input.readUnsignedShort()
                val state = input.readUnsignedShort()
                game.get(x, y).state = state
                if(count > 0){
                    val from = game.getIndex(x, y)
                    for(k in 0 until count){
                        val to = input.readInt()
                        game.soloConnect(from, to)
                    }
                }
            }
        }

        game.clickCounter = clickCount
        game.randomInitKey = randomInitKey

    }

    companion object {
        val MAGIC = "IPXx"
        fun writeMagic(output: DataOutputStream){
            for(char in MAGIC){
                output.writeByte(char.toInt())
            }
        }
    }

}