package me.antonio.noack.impossupuzzle

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout

class RandomBGColor(ctx: Context, attributeSet: AttributeSet?): LinearLayout(ctx, attributeSet){

    init {
        val hue = 360f * Math.random().toFloat()
        val hsv = floatArrayOf(hue, 0.5f, 0.5f)
        val rgb = ColorHelper.normalize(Color.HSVToColor(hsv)) or 0xff000000.toInt()
        setBackgroundColor(rgb)
    }

}