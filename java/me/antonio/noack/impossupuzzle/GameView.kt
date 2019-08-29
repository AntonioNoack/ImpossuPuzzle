package me.antonio.noack.impossupuzzle

import android.graphics.Color
import android.opengl.GLES20.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.math.MathUtils.clamp
import me.antonio.noack.gl.GLConstants.info
import me.antonio.noack.gl.RenderTask
import me.antonio.noack.gl.structure.FlatBuffer
import me.antonio.noack.gui.Gfx
import me.antonio.noack.impossupuzzle.ColorHelper.mixRGB
import me.antonio.noack.impossupuzzle.ColorHelper.normalize
import me.antonio.noack.maths.Utils.mix
import kotlin.concurrent.thread
import kotlin.math.min
import kotlin.math.sqrt

class GameView: RenderTask({

    FlatBuffer.init()
    Gfx.init()

}), GestureDetector.OnGestureListener {

    val exampleTimeout = 0.25f
    val exampleStates = 3

    var clickedColor = -1
    var clicked: Game.Field? = null
    var clickedTime = 0f
    val clickFadeSpeed = 5f

    var timeUntilClick = 0f

    var game: Game? = null
    val stateColors = ArrayList<Int>()

    var exampleGame: Game? = null

    var waiting = false

    fun getExample(): Game {
        var exGame = exampleGame
        return if(exGame == null){
            val count = 6
            // val mid = sqrt(info.frameWidth * 1f * info.frameHeight)
            /*exGame = Game((info.frameWidth * count / mid).toInt(), (info.frameHeight * count / mid).toInt(), 3, object: Connector {
                ...
            })*/
            exGame = Game(count, count, 0, 0f, exampleStates, object: Connector {
                override fun connect(game: Game) {
                    for(field in game.fields){
                        // asym
                        field.connect(game.fields.random())
                        // sym
                        val second = game.fields.random()
                        field.connect(second)
                        second.connect(field)
                    }
                }
            })
            exampleGame = exGame
            exGame
        } else exGame
    }

    override fun draw(wfh: Double, width: Int, height: Int) {

        val time = System.nanoTime()
        info.dt = (time - lastTime) * 1e-9
        lastTime = time

        val game = game ?: getExample()

        drawGame(game)

        val example = exampleGame
        if(this.game == null && example != null){

            timeUntilClick -= info.dt.toFloat()
            if(timeUntilClick < 0f){
                val field = example.get((Math.random() * example.sx).toInt(), (Math.random() * example.sy).toInt())
                field.click()
                clicked = field
                clickedTime = 1f
                timeUntilClick = exampleTimeout
            }
        }

    }

    var lastTime = System.nanoTime()

    fun drawGame(game: Game){

        val measuredWidth = info.frameWidth
        val measuredHeight = info.frameHeight

        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        val rw = width / game.sx
        val rh = height / game.sy

        if(stateColors.size != game.states){
            generateStateColors(game.states)
        }

        val margin = min(rw, rh) * 0.1f

        Gfx.sizeX = 1f / width
        Gfx.sizeY = 1f / height

        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        glDepthMask(false)
        glEnable(GL_BLEND)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)

        glBlendEquation(GL_FUNC_ADD)
        // glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        for(j in 0 until game.sy){
            val top = j * rh
            val bottom = top + rh
            for(i in 0 until game.sx){
                val left = i * rw
                val right = left + rw
                val field = game.get(i, j)
                val color = if(field == clicked){
                    mixRGB(stateColors[field.state], clickedColor, clickedTime)
                } else {
                    stateColors[field.state]
                }
                // done generate field texture...
                // done then draw it... with tinted color... with border, inversed :D
                Gfx.drawTintedImage(
                    left + margin, top + margin,
                    right - left - 2 * margin, bottom - top - 2 * margin,
                    field.getTexture(game), color)
            }
        }

        clickedTime -= clickFadeSpeed * info.dt.toFloat()
        if(clickedTime <= 0f) clicked = null

    }

    val doneColor = 0xff000000.toInt()

    fun generateStateColors(size: Int){
        stateColors.clear()
        stateColors.add(doneColor)
        if(size == 2) stateColors.add(normalize(-1, 0.7f))
        else {
            val hsv = floatArrayOf(0f, 0.5f, 0.5f)
            for(i in 1 until size){
                hsv[0] = (i) * 360f / (size - 1)
                stateColors.add(0xff000000.toInt() or normalize(Color.HSVToColor(hsv)))
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean = false
    override fun onDown(e: MotionEvent?): Boolean = true
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent?) {}
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false
    override fun onShowPress(e: MotionEvent?) {}
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return if(e == null) false
        else {
            val game = game ?: getExample()
            val field = game.get(e.x, e.y, info.frameWidth.toFloat(), info.frameHeight.toFloat())
            field.click()
            game.clickCounter++
            clicked = field
            clickedTime = 1f
            if(game.isSolved()){
                // show that we solved it...
                AllManager.showWon()
            }
            true
        }
    }


    // todo an example game in the background? :D

}