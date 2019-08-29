package me.antonio.noack.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.InputDevice
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import me.antonio.noack.gl.GLConstants.info
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLView(ctx: Context, attributeSet: AttributeSet? = null): GLSurfaceView(ctx, attributeSet), GLSurfaceView.Renderer {

    var renderTask: RenderTask? = null

    init {

        setEGLContextClientVersion(2)
        setRenderer(this)

        val gestureDetector = GestureDetector(ctx, object: GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                return (renderTask as? GestureDetector.OnDoubleTapListener)?.onDoubleTap(e) ?: false
            }

            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                return (renderTask as? GestureDetector.OnDoubleTapListener)?.onDoubleTapEvent(e) ?: false
            }

            override fun onDown(e: MotionEvent?): Boolean {
                return (renderTask as? GestureDetector.OnGestureListener)?.onDown(e) ?: false
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                return (renderTask as? GestureDetector.OnGestureListener)?.onFling(e1, e2, velocityX, velocityY) ?: false
            }

            override fun onLongPress(e: MotionEvent?) {
                (renderTask as? GestureDetector.OnGestureListener)?.onLongPress(e)
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                return (renderTask as? GestureDetector.OnGestureListener)?.onScroll(e1, e2, distanceX, distanceY) ?: false
            }

            override fun onShowPress(e: MotionEvent?) {
                (renderTask as? GestureDetector.OnGestureListener)?.onShowPress(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                return (renderTask as? GestureDetector.OnDoubleTapListener)?.onSingleTapConfirmed(e) ?: false
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return (renderTask as? GestureDetector.OnGestureListener)?.onSingleTapUp(e) ?: false
            }

        })

        val scaleGestureDetector = ScaleGestureDetector(ctx, object: ScaleGestureDetector.OnScaleGestureListener {

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                return (renderTask as? ScaleGestureDetector.OnScaleGestureListener)?.onScale(detector) ?: false
            }

            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                return (renderTask as? ScaleGestureDetector.OnScaleGestureListener)?.onScaleBegin(detector) ?: false
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                (renderTask as? ScaleGestureDetector.OnScaleGestureListener)?.onScaleEnd(detector)
            }

        })

        setOnTouchListener { v, e ->
            if(e != null){
                val primary = renderTask?.onTouch(v, e) ?: true
                if(!primary){
                    if(renderTask is ScaleGestureDetector.OnScaleGestureListener) scaleGestureDetector.onTouchEvent(e)
                    if(renderTask is GestureDetector.OnGestureListener) gestureDetector.onTouchEvent(e)
                };true
            } else false
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        renderTask?.init()

    }
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        info.wfh = width.toDouble() / height
        info.frameWidth = width
        info.frameHeight = height
        println("set size to $width x $height")
    }

    override fun onDrawFrame(gl: GL10?) {

        GLTaskQueue.work()

        val task = renderTask
        if(task != null){

            val width = info.frameWidth
            val height = info.frameHeight

            task.init()
            task.draw(info.wfh, width, height)

        }

    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if(event != null && event.source and InputDevice.SOURCE_CLASS_POINTER != 0){
            val renderTask = renderTask
            if(renderTask is GestureDetector.OnGestureListener){
                when(event.actionMasked){
                    MotionEvent.ACTION_SCROLL -> {
                        renderTask.onScroll(event, event, 0f, event.getAxisValue(MotionEvent.AXIS_VSCROLL))
                    }
                }
            }
        }
        return super.onGenericMotionEvent(event)
    }

}