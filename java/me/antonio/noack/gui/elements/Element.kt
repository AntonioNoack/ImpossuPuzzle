package me.antonio.noack.gui.elements

import me.antonio.noack.gui.drawable.ColorDrawable
import me.antonio.noack.gui.drawable.Drawable

open class Element(var size: Size, var parent: Element?){

    var background: Drawable? = null

    var x = 0f
    var y = 0f
    var width = 100f
    var height = 100f

    var weight = -1f

    var invalidated = false

    var borderSizeProvider: (() -> Float)? = null
    var borderStyleProvider: (() -> Drawable)? = null
    var borderSize = 0f
    var borderStyle: Drawable? = null

    fun setBorder(size: Float, color: Int, opacity: Float = ((color shr 24) and 255)/255f){
        borderSizeProvider = { size }
        val drawable = ColorDrawable(color, opacity)
        borderStyleProvider = { drawable }
    }

    fun invalidate(){
        if(!invalidated){
            parent?.invalidate()
            invalidated = true
        }
    }

    fun measure(x: Float, y: Float, width: Float, height: Float){
        if(size != Size.HIDDEN){
            borderSize = borderSizeProvider?.invoke() ?: borderSize
            borderStyle = borderStyleProvider?.invoke() ?: borderStyle
            onMeasure(x, y, width, height)
        } else {
            this.x = x
            this.y = y
            this.width = 0f
            this.height = 0f
        }
    }

    fun isVisible(): Boolean {
        return when(size){
            Size.HIDDEN, Size.INVISIBLE -> false
            else -> true
        }
    }

    open fun onMeasure(x: Float, y: Float, width: Float, height: Float){
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun draw(time: Long){
        if(isVisible()){
            onDraw(time)
        }
    }

    open fun onDraw(time: Long){
        val borderStyle = borderStyle
        if(borderStyle != null && borderSize != 0f){
            borderStyle.draw(x, y, width, height)
        }
    }

    enum class Size {
        INVISIBLE,
        HIDDEN,
        SMALL,
        MEDIUM,
        LARGE,
        XLARGE,
        XXL
    }
}