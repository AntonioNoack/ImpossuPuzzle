package me.antonio.noack.gl

import android.opengl.GLES20.*
import me.antonio.noack.gl.GLConstants.OpenGLES

object Shader {

    private fun convertToMatchingSource(source: String, isFirstOne: Boolean): String {
        return if(OpenGLES)
            source
        else
            "#version 330 core\n" +
            (source
                .replace("attribute ", "in ")
                .replace("varying ", if(isFirstOne) "out " else "in ")
                /*.replace("void main(){", "vec4 matMul4x4(vec3 pos, vec4 mx, vec4 my, vec4 mz, vec4 mw){\x" +
                        "   return pos.x * mx + pos.y * my + pos.z * mz + mw;\x" +
                        "}\nvoid main(){")*/)
    }

    fun create(source: String, type: Int): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, convertToMatchingSource(source, type == GL_VERTEX_SHADER))
        glCompileShader(shader)
        val error = glGetShaderInfoLog(shader) ?: null
        if(error != null && error.isNotBlank()){
            println("Error in source code:\n$error\n${
                source.split('\n').mapIndexed { index, s -> "${index+1}: $s" }.joinToString("\n")
            }")
        }
        return shader
    }

    fun extractUniforms(program: Int, vertexSource: String, fragmentSource: String): HashMap<String, Int> {
        glUseProgram(program)
        val map = HashMap<String, Int>()
        var sampleCounter = 0
        val whitespaceRegex = "\\s".toRegex()
        for(source in arrayOf(vertexSource, fragmentSource)){
            source.split(';').forEach { lin ->
                val line = lin
                    .replace(whitespaceRegex, " ")
                    .replace(',', ' ')
                    .replace('\n', ' ')
                    .replace("  ", " ")
                    .replace("  ", " ")
                    .replace("  ", " ")
                    .trim()
                if(line.startsWith("uniform ")){
                    val args = line.split(' ')
                    val arg1 = args[1].trim().equals("sampler2D", true)
                    for(i in 2 until args.size){
                        val arg = args[i]
                        if(arg.isNotEmpty()){
                            val loc = glGetUniformLocation(program, arg)
                            if(loc < 0){ println("Uniform ${args[1]} $arg is not used by the program!")
                            } else { map[arg] = loc }
                            if(arg1){
                                glUniform1i(loc, sampleCounter)
                                sampleCounter++
                            }
                        }
                    }
                }
            }
        }
        return map
    }

    fun extractAttributes(program: Int, source: String): HashMap<String, Int> {

        val map = HashMap<String, Int>()
        val whitespaceRegex = "\\s".toRegex()
        source.split(';').forEach { lin ->
            val line = lin
                .replace(whitespaceRegex, " ")
                .replace(',', ' ')
                .replace('\n', ' ')
                .replace("  ", " ")
                .replace("  ", " ")
                .replace("  ", " ")
                .trim()
            if(line.startsWith("attribute ")){
                val args = line.split(' ')
                /*val scale = when(args[1]){
                    "float" -> 1
                    "vec2" -> 2
                    "vec3" -> 3
                    "vec4" -> 4
                    else -> { println("Attribute type ${args[1]} of variable ${args[2]} not recognized!"); 0 }
                }*/
                for(i in 2 until args.size){
                    val arg = args[i]
                    if(arg.isNotEmpty()){
                        val location = glGetAttribLocation(program, arg)
                        if(location < -1){ println("Attribute $arg not found!") }
                        map[arg] = location
                    }
                }
            }
        }

        return map

    }

}