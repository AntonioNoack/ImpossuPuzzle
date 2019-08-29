package me.antonio.noack.gl.buffer

import android.opengl.GLES20.*

enum class Type(val type: Int, val bytes: Int, val normalized: Boolean = false) {

    S8(GL_BYTE, 1),
    U8(GL_UNSIGNED_BYTE, 1),
    S8N(GL_BYTE, 1, true),
    U8N(GL_UNSIGNED_BYTE, 1, true),

    S16(GL_SHORT, 2),
    U16(GL_UNSIGNED_SHORT, 2),
    S16N(GL_SHORT, 2, true),
    U16N(GL_UNSIGNED_SHORT, 2, true),

    S32(GL_INT, 4),
    U32(GL_UNSIGNED_INT, 4),
    S32N(GL_INT, 4, true),
    U32N(GL_UNSIGNED_INT, 4, true),

    FLOAT(GL_FLOAT, 4),

}