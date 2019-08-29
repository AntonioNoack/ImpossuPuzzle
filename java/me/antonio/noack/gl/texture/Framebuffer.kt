package me.antonio.noack.gl.texture

import android.opengl.GLES20.*
import android.opengl.GLES30.GL_DEPTH_COMPONENT24
import me.antonio.noack.gl.GLConstants.info
import java.nio.ByteBuffer
import kotlin.math.abs

class Framebuffer(val width: Int, val height: Int, val colorTexture: Texture?, firstOne: Boolean = false){

    constructor(texture: Texture): this(texture.width, texture.height, texture, false)

    constructor(width: Int, height: Int, type: Int, linear: Boolean, firstOne: Boolean = false): this(width, height, {

        val texture = Texture(GL_CLAMP_TO_EDGE, linear, width, height)
        glTexImage2D(GL_TEXTURE_2D, 0, type, width, height, 0, type, GL_UNSIGNED_BYTE, null as ByteBuffer?)

        texture
    }(), firstOne)

    val code: Int
    val nanos = info.nanos

    init {

        if(firstOne){

            code = 0

        } else {

            val codes = intArrayOf(-1)
            glGenFramebuffers(1, codes, 0)
            code = codes[0]

            glBindFramebuffer(GL_FRAMEBUFFER, code)

        }

    }

    private val renderBuffer: Int

    init {

        if(firstOne){

            renderBuffer = -1

        } else {

            colorTexture?.use()

            val codes = intArrayOf(-1)
            glGenRenderbuffers(1, codes, 0)
            renderBuffer = codes[0]
            glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer)
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height)
            glBindRenderbuffer(GL_RENDERBUFFER, 0)

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture!!.code, 0)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer)

            if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
                println("Something went wrong with framebuffer #$code")
            }

        }

    }

    val creationTime = System.currentTimeMillis()

    fun use(){
        glBindFramebuffer(GL_FRAMEBUFFER, code)
        if(code > 0) glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer)
        glViewport(0, 0, width, height)
    }

    fun destroy(){
        if(code > 0){
            glDeleteFramebuffers(1, intArrayOf(code), 0)
            glDeleteRenderbuffers(1, intArrayOf(renderBuffer), 0)
            colorTexture?.destroy()
        }
    }

    companion object {

        fun get(wrap: Int, linear: Boolean, type: Int, width: Int, height: Int, nanos: Long, framebuffer: Framebuffer?): Framebuffer {
            return if(framebuffer == null || (abs(framebuffer.nanos - nanos) > 1000000000) && (width != framebuffer.width || height != framebuffer.height)){
                if(framebuffer == null){
                    Framebuffer(Texture(wrap, linear, width, height, type))
                } else {
                    framebuffer.destroy()
                    Framebuffer(Texture(wrap, linear, width, height, type))
                }
            } else framebuffer
        }

    }

}