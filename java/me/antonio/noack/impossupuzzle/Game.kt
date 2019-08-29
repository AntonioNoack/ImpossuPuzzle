package me.antonio.noack.impossupuzzle

import android.opengl.GLES20.*
import me.antonio.noack.gl.texture.Texture
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.floor

class Game(val sx: Int, val sy: Int, val df: Int, val initPerDot: Float, val states: Int, val connector: Connector){

    val fields = Array(sx * sy){ Field(it,0, states) }
    var clickCounter = 0
    var randomInitKey = 0L
    var swaps = (sx * sy * initPerDot).toInt()

    fun get(x: Int, y: Int): Field {
        return fields[x + y * sx]
    }

    fun getIndex(x: Int, y: Int): Int = x + y * sx

    fun get(sx: Float, sy: Float, sw: Float, sh: Float): Field {
        return get(floor(this.sx * sx / sw).toInt(), floor(this.sy * sy / sh).toInt())
    }

    fun initTask(){
        val random = Random(randomInitKey)
        fields.forEach { it.reset() }
        for(i in 0 until (sx * sy * initPerDot).toInt()){
            fields[random.nextInt(fields.size)].click()
        }
        for(i in 0 until 5){
            if(!isSolved()){
                break
            } else {
                fields[random.nextInt(fields.size)].click()
            }
        }
        clickCounter = 0
    }

    fun resetCounter(){
        clickCounter = 0
    }

    fun resetGame(){
        fields.forEach { it.reset() }
        initTask()
    }

    fun isSolved(): Boolean {
        for(i in 0 until sx * sy){
            if(fields[i].state != 0) return false
        }
        return true
    }

    init {
        connector.connect(this)
    }

    fun biConnect(a: Int, b: Int){
        if(a == b) return
        val f0 = fields[a]
        val f1 = fields[b]
        f0.connect(f1)
        f1.connect(f0)
    }

    fun soloConnect(from: Int, to: Int){
        fields[from].connect(fields[to])
    }

    class Field(val index: Int, var state: Int = 0, private val states: Int){

        private var texture: Texture? = null

        fun getTexture(game: Game): Texture {
            if(texture == null){
                val size = game.sx * game.sy
                val type = GL_RGBA
                val bytes = ByteArray(size){
                    val second = game.fields[it]
                    if(toConnections.contains(second)) 125 else 255.toByte()
                }
                val bytesRGBA = ByteArray(bytes.size * 4){ bytes[it.shr(2)] }
                texture = Texture(GL_CLAMP_TO_EDGE, false, game.sx, game.sy, type, bytesRGBA)

            }
            return texture!!
        }

        fun clear(){
            this.texture?.destroy()
            this.texture = null
        }

        val toConnections = HashSet<Field>()

        fun connect(to: Field){
            if(to != this){
                toConnections.add(to)
            }
        }

        fun click(){
            flip()
            for(con in toConnections){
                con.flip()
            }
        }

        fun flip(){
            state = (state + 1) % states
        }

        fun reset(){
            state = 0
        }

    }

}