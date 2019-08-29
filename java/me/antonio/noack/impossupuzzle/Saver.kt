package me.antonio.noack.impossupuzzle

import java.io.DataOutputStream

object Saver {

    fun save(game: Game, name: String, dos: DataOutputStream){
        dos.writeUTF(name)
        StorageConnector.writeMagic(dos)
        dos.writeInt(1)
        dos.writeShort(game.states)
        dos.writeInt(game.sx)
        dos.writeInt(game.sy)
        dos.writeInt(game.df)
        dos.writeInt(game.clickCounter)
        dos.writeLong(game.randomInitKey)
        dos.writeFloat(game.initPerDot)
        synchronized(game){
            for(y in 0 until game.sy){
                for(x in 0 until game.sx){
                    val field = game.get(x, y)
                    dos.writeShort(field.toConnections.size)
                    dos.writeShort(field.state)
                    for(con in field.toConnections){
                        val index = con.index
                        dos.writeInt(index)
                    }
                }
            }
        }
        dos.write(0)
        dos.close()
    }

    /**
     * for(char in MAGIC){
    assert(input.readByte().toChar() == char)
    }
     *  val version = input.readInt()
    val states = input.readUnsignedShort()
    val sx = input.readInt()
    val sy = input.readInt()
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
     * */

}