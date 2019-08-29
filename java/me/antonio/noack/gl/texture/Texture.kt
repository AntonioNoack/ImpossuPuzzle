package me.antonio.noack.gl.texture

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLES30.GL_UNPACK_ROW_LENGTH
import android.opengl.GLUtils
import android.os.Build
import me.antonio.noack.gl.GLTaskQueue.taskQueue
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.nio.ByteBuffer

open class Texture(wrap: Int, linear: Boolean, var width: Int, var height: Int){

    val code: Int

    init {

        val codeBuffer = intArrayOf(-1)
        glGenTextures(1, codeBuffer, 0)
        code = codeBuffer[0]

        glBindTexture(GL_TEXTURE_2D, code)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, if(linear) GL_LINEAR else GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap)

    }

    constructor(wrap: Int, linear: Boolean, width: Int, height: Int, type: Int = GL_RGB): this(wrap, linear, width, height){
        glTexImage2D(GL_TEXTURE_2D, 0, type, width, height, 0, type, GL_UNSIGNED_BYTE, null as ByteBuffer?)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    constructor(wrap: Int, linear: Boolean, width: Int, height: Int, type: Int = GL_RGB, data: ByteArray): this(wrap, linear, width, height){
        val pixels = ByteBuffer.allocateDirect(data.size).put(data).position(0) as ByteBuffer
        glTexImage2D(GL_TEXTURE_2D, 0, type, width, height, 0, type, GL_UNSIGNED_BYTE, pixels)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun use(){
        glBindTexture(GL_TEXTURE_2D, code)
    }

    fun use(index: Int){
        glActiveTexture(index + GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, code)
    }

    fun destroy(){
        glDeleteTextures(1, intArrayOf(code), 0)
    }

    companion object {

        fun fromResource(resources: Resources, id: Int, linear: Boolean): Texture {

            val bitmap = BitmapFactory.decodeStream(resources.openRawResource(id))
            val texture = Texture(GL_CLAMP_TO_EDGE, linear, bitmap.width, bitmap.height)
            if(Build.VERSION.SDK_INT > 17){
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0) }
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
            return texture

        }

        fun fromResource(id: Int, linear: Boolean): Texture {
            TODO("return fromResource(AllManager.res, id, linear)")
        }

        fun fromFileAsync(file: File, linear: Boolean,
                          onSuccess: (result: Texture) -> Unit,
                          onError: ((error: Exception) -> Unit) = { e -> e.printStackTrace() }){

            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                taskQueue.add(100 to {
                    val texture = Texture(GL_REPEAT, linear, bitmap.width, bitmap.height)
                    if(Build.VERSION.SDK_INT > 17){
                        glPixelStorei(GL_UNPACK_ROW_LENGTH, 0) }
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
                    glGenerateMipmap(GL_TEXTURE_2D)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
                    bitmap.recycle()
                    onSuccess(texture)
                })
            } catch (e: Exception){
                onError(e)
            }
        }

        fun fromFileSync(file: File, linear: Boolean): Texture {

            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val texture = Texture(GL_REPEAT, linear, bitmap.width, bitmap.height)
            if(Build.VERSION.SDK_INT > 17){
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0) }
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            glGenerateMipmap(GL_TEXTURE_2D)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            bitmap.recycle()
            return texture

        }

        fun fromStreamAsync(input: InputStream,linear: Boolean,
                          onSuccess: (result: Texture) -> Unit,
                          onError: ((error: Exception) -> Unit) = { e -> e.printStackTrace() }){

            try {
                val bitmap = BitmapFactory.decodeStream(input)
                taskQueue.add(100 to {
                    val texture = Texture(GL_REPEAT, linear, bitmap.width, bitmap.height)
                    if(Build.VERSION.SDK_INT > 17){
                        glPixelStorei(GL_UNPACK_ROW_LENGTH, 0) }
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
                    glGenerateMipmap(GL_TEXTURE_2D)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
                    bitmap.recycle()
                    onSuccess(texture)
                })
            } catch (e: Exception){
                onError(e)
            }
        }

        fun fromStreamSync(input: InputStream,linear: Boolean): Texture {

            val bitmap = BitmapFactory.decodeStream(input)
            val texture = Texture(GL_REPEAT, linear, bitmap.width, bitmap.height)
            if(Build.VERSION.SDK_INT > 17){
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0) }
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            glGenerateMipmap(GL_TEXTURE_2D)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            bitmap.recycle()
            return texture

        }

    }

}