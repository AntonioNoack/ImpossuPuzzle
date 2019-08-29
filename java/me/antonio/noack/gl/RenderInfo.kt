package me.antonio.noack.gl

import me.antonio.noack.maths.Matrix
import me.antonio.noack.gl.texture.Framebuffer
import me.antonio.noack.gl.texture.Texture
import kotlin.math.sqrt

class RenderInfo {

    // man macht die Augen auf
    var brightness0 = 0.5
    var nanos = System.nanoTime()

    var buffer0: Framebuffer? = null
    var buffer1: Framebuffer? = null
    var buffer2: Framebuffer? = null

    fun getEffectFrame(): Pair<Framebuffer, Texture> {
        val temp = buffer2
        buffer2 = buffer1
        buffer1 = buffer0
        buffer0 = temp
        return buffer0!! to buffer1!!.colorTexture!!
    }

    fun getEffectFrame2(): Triple<Framebuffer, Texture, Texture> {
        val temp = buffer2
        buffer2 = buffer1
        buffer1 = buffer0
        buffer0 = temp
        return Triple(buffer0!!, buffer1!!.colorTexture!!, buffer2!!.colorTexture!!)
    }

    var screenWidth = 300
    var screenHeight = 300

    var frameWidth = 100
    var frameHeight = 100

    var dt = 0.0
    var time = 0.0

    var baseZ = Double.NaN

    var posX = 0.0
    var posY = 0.0
    var posZ = 0.0

    var velX = 0.0
    var velY = 0.0
    var velZ = 0.0

    var camX = 0.0
    var camY = 0.0
    var camZ = 0.0

    var near = 0.0
    var far = 0.0

    var height = 7.0

    var roll = 0.0
    var pitch = 0.0
    var yaw = 0.0

    var lat = 0.0
    var lon = 0.0

    var viewDirX = 0.0
    var viewDirY = 0.0
    var viewDirZ = 1.0

    var lightMatrix: Matrix? = null
    var inverseLightMatrix: Matrix? = null

    var sunMatrix: Matrix? = null
    var inverseSunMatrix: Matrix? = null

    var cameraMatrix: Matrix? = null
    var inverseCameraMatrix: Matrix? = null

    var wfh = 1.0
    var fov = 1.0

    var minDotZ = 0.0

    var eyeX = 0f
    var eyeY = 0f
    var eyeZ = 0f

    var sunX = 0f
    var sunY = 0.7f
    var sunZ = -0.7f

    fun calculateCameraMatrix(inverseToo: Boolean = true){

        val rotMatrix = Matrix.I(size = 4, uuid = 125)
            .rotateZ(roll)
            .rotateX(pitch)
            .rotateZ(yaw)

        eyeX = rotMatrix.getX().toFloat()
        eyeY = rotMatrix.getY().toFloat()
        eyeZ = rotMatrix.getZ().toFloat()

        val factor = 1f / sqrt(eyeX * eyeX + eyeY * eyeY + eyeZ * eyeZ)

        eyeX *= factor
        eyeY *= factor
        eyeZ *= factor

        cameraMatrix = Matrix
            .perspectiveMatrix(wfh, fov, near, far, uuid = 81)
            .multiply(rotMatrix)

        if(inverseToo){
            calculateInverseMatrix()
        }

    }

    fun calculateInverseMatrix(){
        val inverseCameraMatrix = cameraMatrix!!.clone().inverse()
        this.inverseCameraMatrix = inverseCameraMatrix
        viewDirX = inverseCameraMatrix.getX(0.0, 0.0, 0.0, 1.0)
        viewDirY = inverseCameraMatrix.getY(0.0, 0.0, 0.0, 1.0)
        viewDirZ = inverseCameraMatrix.getZ(0.0, 0.0, 0.0, 1.0)
    }

}