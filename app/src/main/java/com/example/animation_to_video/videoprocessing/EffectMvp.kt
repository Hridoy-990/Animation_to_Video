package com.example.animation_to_video.videoprocessing

import android.opengl.Matrix
import android.util.Size
import kotlin.math.max

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class EffectMvp {

    fun getMvp(effectNumber: Int, pTime: Long, totalTime: Long, size: Size): FloatArray {

        val mvp = FloatArray(16)
        Matrix.setIdentityM(mvp, 0)

        val viewMatrix = FloatArray(16)
        Matrix.setIdentityM(viewMatrix,0)
        val projMatrix = FloatArray(16)
        Matrix.setIdentityM(projMatrix,0)

        val p = pTime.toFloat()
        val t = totalTime.toFloat()
        val ratio = (size.width.toFloat())/(size.height.toFloat())
        when(effectNumber){
            0 -> Matrix.scaleM(mvp, 0,p/t, 1f, 1f)
            1 -> Matrix.scaleM(mvp, 0, 1f, p/t, 1f)
            2 -> Matrix.scaleM(mvp, 0, 1f, 1f, p/t)
            (2+1) -> {
                //Matrix.setLookAtM(mvp,0,0f,0f,0f,0f,0f,0f,0f,1f,0f)
                Matrix.setRotateM(mvp,0,(p/t)*360f,1.0f,0.5f,0f)
            }
            4 -> {
                Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 10f-((p/t)*7f), 0f, 0f, 0f, 0f, 1f, 0.0f)
                Matrix.frustumM(projMatrix,0,-1f,1f,-1f,1f,1f,10f)
                Matrix.multiplyMM(mvp,0,projMatrix,0,viewMatrix,0)
            }
            5 -> {
                var r = max(1.0f,p/(t-2000000f))
                Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 10f-(r*7f), 0f, 0f, 0f, 0f, 1f, 0.0f)
                Matrix.frustumM(projMatrix,0,-1f,1f,-1f,1f,1f,10f)
                Matrix.multiplyMM(mvp,0,projMatrix,0,viewMatrix,0)
            }
            6 -> {
                //Log.d(TAG, "getMvp: ${ratio}")
                Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, (8f - (p / t) * 8f) + 2f, 0f, 0f, 0f, 0f, 1f, 0.0f)
                Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
                Matrix.multiplyMM(mvp, 0, projMatrix, 0, viewMatrix, 0)
            }
            7 -> {
                Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 2f, 0f, 0f, 0f, 0f, 1f, 0.0f)
                Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
                Matrix.multiplyMM(mvp, 0, projMatrix, 0, viewMatrix, 0)
                val rotateMatrix = FloatArray(16)
                Matrix.setRotateM(rotateMatrix,0, minOf(6f*360f,6f*(p/t)*360f),0.0f,0.0f,-1.0f)
                Matrix.multiplyMM(mvp,0,mvp,0,rotateMatrix,0)
            }
        }
        Matrix.scaleM(mvp,0,1f,-1f,1f)
        return mvp
    }
}