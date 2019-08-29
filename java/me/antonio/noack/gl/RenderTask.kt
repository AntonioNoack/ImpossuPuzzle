package me.antonio.noack.gl

import android.view.View

abstract class RenderTask(threadSafeInit: (that: Initializable) -> Unit):
    Initializable(threadSafeInit),
    View.OnTouchListener {

    abstract fun draw(wfh: Double, width: Int, height: Int)

}