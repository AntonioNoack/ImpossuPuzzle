package me.antonio.noack.gl

import android.opengl.GLES20.*
import me.antonio.noack.gl.GLConstants.OpenGLES
import me.antonio.noack.gl.buffer.Buffer

open class Program(varyingSource: String, vertexSource: String, fragmentSource: String, val needsZSorted: Boolean = false){

    private val vertexShader = Shader.create(varyingSource + vertexSource, GL_VERTEX_SHADER)
    private val fragmentShader = Shader.create("precision mediump float;\n$varyingSource\n$fragmentSource", GL_FRAGMENT_SHADER)

    private val program = glCreateProgram()

    init {
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
    }

    private val uniforms = Shader.extractUniforms(program, vertexSource, fragmentSource)
    val attributes = Shader.extractAttributes(program, vertexSource)

    fun drawInit(buffer: Buffer){
        if(buffer.count > 0 && buffer.code > -1){
            glUseProgram(program)
            glBindBuffer(GL_ARRAY_BUFFER, buffer.code)
            buffer.alignment.align()
        }
    }

    fun draw(buffer: Buffer){
        if(buffer.count > 0 && buffer.code > -1){
            glUseProgram(program)
            glBindBuffer(GL_ARRAY_BUFFER, buffer.code)
            buffer.alignment.align()
            glDrawArrays(GL_TRIANGLES, 0, buffer.count)
        }
    }

    fun use(){
        glUseProgram(program)
    }

    fun glColor3(uniform: Int, color: Int){
        glUniform3f(uniform,
            ((color shr 16) and 255)/255f,
            ((color shr 8) and 255)/255f,
            (color and 255)/255f)
    }

    fun setTextureSlot(name: String, slot: Int){
        glUniform1i(glGetUniformLocation(program, name), slot)
    }

    operator fun get(key: String): Int {
        return uniforms[key] ?: -1
    }

    open fun initGlobally(){

    }

    companion object {

        fun uniformMatrix4x4(name: String): String {
            return if(true || OpenGLES){
                "uniform mat4 $name;\n"
            } else {
                "uniform vec4 ${name}X, ${name}Y, ${name}Z, ${name}W;\n"
            }
        }

        fun matMul(name: String, partner: String): String {
            return if(true || OpenGLES){
                "($name * vec4($partner, 1.));\n"
            } else {
                "matMul4x4($partner, ${name}X, ${name}Y, ${name}Z, ${name}W);\n"
            }
        }

    }

}