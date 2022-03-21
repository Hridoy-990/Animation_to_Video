package com.example.animation_to_video.animation.gl

import android.opengl.Matrix
import android.util.Size

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class BitmapEffectFactory {
    fun getMVP(effectNumber: Int, pTime: Long, totalTime: Long, tSize: Size, sSize: Size): FloatArray {

        val mvp = FloatArray(16)
        Matrix.setIdentityM(mvp, 0)

        val viewMatrix = FloatArray(16)
        Matrix.setIdentityM(viewMatrix,0)
        val projMatrix = FloatArray(16)
        Matrix.setIdentityM(projMatrix,0)

        val p = pTime.toFloat()
        val t = totalTime.toFloat()

        when(effectNumber){
            0 -> {
                val translateMatrix = FloatArray(16)
                Matrix.setIdentityM(translateMatrix,0)
                Matrix.translateM(translateMatrix,0,0f,-0.5f,0f)
                Matrix.scaleM(mvp, 0,p/t, 0.5f, 1f)
            }
            1 -> {
                val scaleX = tSize.width.toFloat()/sSize.width.toFloat()
                val scaleY = tSize.height.toFloat()/sSize.height.toFloat()
                Matrix.scaleM(mvp,0,scaleX,scaleY,1f)
            }
        }

        Matrix.scaleM(mvp,0,1f,-1f,1f)
        return mvp
    }
}