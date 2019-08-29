package me.antonio.noack.gui

import android.opengl.GLES20.*
import me.antonio.noack.gl.GLConstants.showFPS
import me.antonio.noack.gl.RenderInfo
import java.util.*

class GUIManager {

    val mainWindowStack = Stack<Dialog>()

    // todo every element is scrollable, if too large
    // todo every element can be easily animated :)
    // todo animation is always just internal?... idk... going away?...
    // todo zooming in?... mmh :) we should add those animations to the programs :)

    var oldWidth = 0f
    var oldHeight = 0f

    var lastFps = 0f

    fun draw(info: RenderInfo, width: Float, height: Float){

        // todo sets of all menues:
        // calculates the available areas for menues
        // onMeasure if measurements changed
        // calles viewport() for all, so no x-adjustment is needed :)

        glDepthMask(false)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)
        glEnable(GL_BLEND)

        // glBlendEquation(GL_FUNC_ADD)
        // glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        // without premultiplied alpha:
        glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD)
        // glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        // with premultiplied alpha:
        // glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD)
        // glBlendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        val nanos = info.nanos
        val millis = nanos / 1000

        Gfx.sizeX = 1f/width
        Gfx.sizeY = 1f/height

        if(showFPS){

            val f = 1f / (lastFps + 30f)
            val fps = lastFps * (1f-f) + f*info.dt.toFloat()
            lastFps = fps

            Gfx.drawText(nanos, Alignment.MIN, false, 35f, 20f, width - 35f, 30f, "${(1f/fps).toInt()}",  -1, 1f)

        }

        // Gfx.drawText(millis, Alignment.CENTER, false, 0f, 0f, width, 20f, "${info.height.toInt()}", -1, 1f)

        // println(dt)
        if(mainWindowStack.isNotEmpty()){

            val dialog = mainWindowStack.peek()
            if(dialog.invalidated || oldWidth != width || oldHeight != height){
                dialog.invalidated = false
                dialog.measure(0f, 0f, width, height)
                oldWidth = width
                oldHeight = height
            }

            Gfx.sizeX = 1f/width
            Gfx.sizeY = 1f/height

            dialog.draw(millis)
        }

        Gfx.tick(nanos)

    }

}